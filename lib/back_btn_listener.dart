import 'back_btn_listener_platform_interface.dart';

class BackBtnListener {
  BackBtnListener._();

  static final events =
      BackBtnListenerPlatform.instance.events.map(_parseEvent);

  static Future<void> startListening() =>
      BackBtnListenerPlatform.instance.startListening();

  static Future<bool> requestAccessibilityPermission() async =>
      await BackBtnListenerPlatform.instance.requestAccessibilityPermission() ??
      false;
}

sealed class Event {
  final String message;

  Event(this.message);

  @override
  String toString() => '$runtimeType: $message';
}

class BackBtnPressedEvent extends Event {
  BackBtnPressedEvent() : super('Back btn pressed');
}

class BackBtnLongPressedEvent extends Event {
  BackBtnLongPressedEvent() : super('Back btn long pressed');
}

class ErrorEvent extends Event {
  ErrorEvent(super.message);
}

class CustomEvent extends Event {
  final dynamic data;

  CustomEvent(super.message, this.data);

  @override
  String toString() => '$runtimeType: $message, data: $data';
}

Event _parseEvent(dynamic event) {
  if (event is Map) {
    final map = event.map((key, value) {
      if (key is String) {
        return MapEntry(key, value);
      }
      return MapEntry(key.toString(), value);
    });

    if (map['event'] == 'BACK_BTN_PRESSED') {
      return BackBtnPressedEvent();
    } else if (map['event'] == 'BACK_BTN_LONG_PRESSED') {
      return BackBtnLongPressedEvent();
    } else if (map.containsKey('error')) {
      return ErrorEvent(map['error']);
    }
  }
  return CustomEvent('Unknown event', event);
}
