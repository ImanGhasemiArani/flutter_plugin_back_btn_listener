package ghasemiarani.iman.back_btn_listener

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent

class AccessService : AccessibilityService() {
    private var isLongPress = false
    private val handler = Handler(Looper.getMainLooper())
    private val longPressRunnable = Runnable {
        isLongPress = true
//        Log.d("AccessService", "Back button long pressed")
        BackBtnListenerPlugin.onEvent("BACK_BTN_LONG_PRESSED")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        Log.d("AccessService", "Service started")
        BackBtnListenerPlugin.onEvent("SERVICE_STARTED")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        val action = event.action
        when (event.keyCode) {
            KeyEvent.KEYCODE_BACK -> if (action == KeyEvent.ACTION_DOWN) {
                handler.postDelayed(longPressRunnable, 1500)
            } else if (action == KeyEvent.ACTION_UP) {
                handler.removeCallbacks(longPressRunnable)
                if (!isLongPress) {
//                    Log.d("AccessService", "Back button pressed")
                    BackBtnListenerPlugin.onEvent("BACK_BTN_PRESSED")
                }
                isLongPress = false
            }
        }
        return super.onKeyEvent(event)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {}
}
