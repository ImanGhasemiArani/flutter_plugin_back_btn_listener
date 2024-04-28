import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'back_btn_listener_method_channel.dart';

abstract class BackBtnListenerPlatform extends PlatformInterface {
  /// Constructs a BackBtnListenerPlatform.
  BackBtnListenerPlatform() : super(token: _token);

  static final Object _token = Object();

  static BackBtnListenerPlatform _instance = MethodChannelBackBtnListener();

  /// The default instance of [BackBtnListenerPlatform] to use.
  ///
  /// Defaults to [MethodChannelBackBtnListener].
  static BackBtnListenerPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [BackBtnListenerPlatform] when
  /// they register themselves.
  static set instance(BackBtnListenerPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  late final Stream<dynamic> events;

  Future<void> startListening() =>
      throw UnimplementedError('startListening() has not been implemented.');

  Future<bool?> requestAccessibilityPermission() => throw UnimplementedError(
      'requestAccessibilityPermission() has not been implemented.');
}
