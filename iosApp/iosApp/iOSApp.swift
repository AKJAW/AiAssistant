import SwiftUI
import shared

@main
struct iOSApp: App {

    init() {
        DependenciesKt.initialize(isDebug: true)
    }

	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}
