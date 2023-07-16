import SwiftUI
import shared

@main
struct iOSApp: App {

    init() {
        DependenciesKt.initialize()
    }

	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}
