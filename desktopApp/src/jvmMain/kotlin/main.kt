import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.akjaw.ai.assistant.shared.App
import com.akjaw.ai.assistant.shared.MainView
import com.akjaw.ai.assistant.shared.composition.Dependencies
import com.akjaw.ai.assistant.shared.chat.data.database.ProductionDriverFactory
import com.akjaw.ai.assistant.shared.chat.data.database.createDatabase

fun main() = application {
    createDatabase(ProductionDriverFactory())
    Window(onCloseRequest = ::exitApplication) {
        MainView()
    }
}