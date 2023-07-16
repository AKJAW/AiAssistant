import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.akjaw.ai.assistant.shared.MainView
import com.akjaw.ai.assistant.shared.composition.initializeDependencies

fun main() = application {
    initializeDependencies(isDebug = true) // For now change manually when creating a Jar
    Window(onCloseRequest = ::exitApplication) {
        MainView()
    }
}