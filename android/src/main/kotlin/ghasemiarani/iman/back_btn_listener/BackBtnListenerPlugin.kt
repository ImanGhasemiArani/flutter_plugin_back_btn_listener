package ghasemiarani.iman.back_btn_listener

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import java.lang.ref.WeakReference


/** BackBtnListenerPlugin */
class BackBtnListenerPlugin : FlutterPlugin, MethodChannel.MethodCallHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    companion object {

        @SuppressLint("StaticFieldLeak")
        private lateinit var instance: BackBtnListenerPlugin

        private val methodChannels = mutableMapOf<BinaryMessenger, MethodChannel>()
        private val eventChannels = mutableMapOf<BinaryMessenger, EventChannel>()
        private val eventHandlers = mutableListOf<WeakReference<EventCallbackHandler>>()

        private fun sendEvent(event: Map<String, Any>) {
            eventHandlers.forEach {
                it.get()?.send(event)
            }
        }

        fun sharePluginWithRegister(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) =
            initSharedInstance(
                flutterPluginBinding.applicationContext, flutterPluginBinding.binaryMessenger
            )

        private fun initSharedInstance(context: Context, binaryMessenger: BinaryMessenger) {
            if (!::instance.isInitialized) {
                instance = BackBtnListenerPlugin()
                instance.context = context
            }

            val channel = MethodChannel(binaryMessenger, "back_btn_listener")
            methodChannels[binaryMessenger] = channel
            channel.setMethodCallHandler(instance)

            val events = EventChannel(binaryMessenger, "back_btn_listener_event")
            eventChannels[binaryMessenger] = events
            val handler = EventCallbackHandler()
            eventHandlers.add(WeakReference(handler))
            events.setStreamHandler(handler)
        }

        fun onEvent(event: String) {
            sendEvent(
                mapOf(
                    "event" to event
                )
            )
        }

        fun onError(error: String) {
            sendEvent(
                mapOf(
                    "error" to error
                )
            )
        }

    }

    private lateinit var context: Context

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) =
        sharePluginWithRegister(flutterPluginBinding)

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) = when (call.method) {
        "startListening" -> {
            context.startService(Intent(context, AccessService::class.java))
            result.success(null)
        }

        "requestAccessibilityPermission" -> {
            result.success(requestAccessibilityPermission())
        }

        else -> result.notImplemented()
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        methodChannels.remove(binding.binaryMessenger)?.setMethodCallHandler(null)
        eventChannels.remove(binding.binaryMessenger)?.setStreamHandler(null)
    }

    private fun requestAccessibilityPermission(): Boolean {
        if (!isAccessibilityPermissionGranted()) {
            context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            return isAccessibilityPermissionGranted()
        }
        return true
    }

    private fun isAccessibilityPermissionGranted(): Boolean {
        try {
            val isGranted = Settings.Secure.getInt(
                context.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED
            ) == 1

//            Log.d("BackBtnListenerPlugin", "isAccessibilityPermissionGranted: $isGranted")
            return isGranted
        } catch (e: Settings.SettingNotFoundException) {
//            Log.e(
//                "BackBtnListenerPlugin",
//                "isAccessibilityPermissionGranted Error finding setting, default accessibility to not found: ${e.message}"
//            )
            return false
        }
    }

    class EventCallbackHandler : EventChannel.StreamHandler {

        private var eventSink: EventChannel.EventSink? = null

        override fun onListen(arguments: Any?, sink: EventChannel.EventSink) {
            eventSink = sink
        }

        override fun onCancel(arguments: Any?) {
            eventSink = null
        }

        fun send(event: Map<String, Any>) {
            Handler(Looper.getMainLooper()).post { eventSink?.success(event) }
        }
    }
}
