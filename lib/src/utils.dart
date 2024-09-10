import 'enums.dart';

/// Parses the given list of states to a list of [ConnectivityResult].
List<ConnectivityResult> parseConnectivityResults(List<String> states) {
  if (states.length > 1 && states.contains('wifi')) {
    return [ConnectivityResult.wifi];
  }
  return states.map((state) {
    switch (state.trim()) {
      case 'bluetooth':
        return ConnectivityResult.bluetooth;
      case 'wifi':
        return ConnectivityResult.wifi;
      case 'ethernet':
        return ConnectivityResult.ethernet;
      case 'mobile':
        return ConnectivityResult.mobile;
      case 'vpn':
        return ConnectivityResult.vpn;
      case 'other':
        return ConnectivityResult.other;
      default:
        return ConnectivityResult.none;
    }
  }).toList();
}
