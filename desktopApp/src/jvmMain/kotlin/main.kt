import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.akjaw.ai.assistant.shared.App

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}