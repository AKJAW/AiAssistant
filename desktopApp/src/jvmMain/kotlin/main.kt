import androidx.compose.ui.platform.isDebugInspectorInfoEnabled
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.akjaw.ai.assistant.shared.App
import com.akjaw.ai.assistant.shared.MainView
import com.akjaw.ai.assistant.shared.composition.Dependencies
import com.akjaw.ai.assistant.shared.chat.data.database.ProductionDriverFactory
import com.akjaw.ai.assistant.shared.chat.data.database.createDatabase
import com.akjaw.ai.assistant.shared.utils.BuildInfo

fun main() = application {
    BuildInfo.isDebug = true // For now change manually when creating a Jar
    createDatabase(ProductionDriverFactory())
    Window(onCloseRequest = ::exitApplication) {
        MainView()
    }
}