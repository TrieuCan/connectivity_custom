import UIKit
import Flutter

@UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate {
  override func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
  ) -> Bool {
      GeneratedPluginRegistrant.register(with: self)
      if let flutterViewController = window?.rootViewController as? FlutterViewController {
          guard let registrar = flutterViewController.pluginRegistry().registrar(forPlugin: "com.trieucq.connectivity_custom") else { return false }

          SwiftConnectivityPlusPlugin.register(with: registrar)
      }
    return super.application(application, didFinishLaunchingWithOptions: launchOptions)
  }
}
