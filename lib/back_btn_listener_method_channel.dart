import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'back_btn_listener_platform_interface.dart';

/// An implementation of [BackBtnListenerPlatform] that uses method channels.
class MethodChannelBackBtnListener extends BackBtnListenerPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('back_btn_listener');
  final eventChannel = const EventChannel('back_btn_listener_event');

  @override
  Stream<dynamic> get events => eventChannel.receiveBroadcastStream();

  @override
  Future<void> startListening() => methodChannel.invokeMethod('startListening');

  @override
  Future<bool?> requestAccessibilityPermission() =>
      methodChannel.invokeMethod<bool>('requestAccessibilityPermission');
}
