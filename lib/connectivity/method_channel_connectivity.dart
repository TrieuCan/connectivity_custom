import 'dart:async';

import 'package:flutter/services.dart';
import 'package:meta/meta.dart';

import '../src/utils.dart';
import 'connectivity_interface.dart';

/// An implementation of [ConnectivityPlatform] that uses method channels.
class MethodChannelConnectivity extends ConnectivityPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  MethodChannel methodChannel =
      const MethodChannel('com.trieucq.connectivity_custom/connectivity');

  /// The event channel used to receive ConnectivityResult changes from the native platform.
  @visibleForTesting
  EventChannel eventChannel =
      const EventChannel('com.trieucq.connectivity_custom/connectivity_status');

  Stream<List<ConnectivityResult>>? _onConnectivityChanged;

  /// Fires whenever the connectivity state changes.
  @override
  Stream<List<ConnectivityResult>> get onConnectivityChanged {
    return _onConnectivityChanged ??= eventChannel
        .receiveBroadcastStream()
        .map((dynamic result) => List<String>.from(result))
        .map(parseConnectivityResults);
  }

  @override
  Future<List<ConnectivityResult>> checkConnectivity() {
    return methodChannel
        .invokeListMethod<String>('check')
        .then((value) => parseConnectivityResults(value ?? []));
  }
}
