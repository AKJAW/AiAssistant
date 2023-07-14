import SwiftUI
import shared

@main
struct iOSApp: App {

    init() {
        InitializeDependenciesKt.execute()
    }

	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}
