package juan.quantum.poem

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.MutableSharedFlow

@Composable
actual fun VisualContactEffect(
    updateEvent: MutableSharedFlow<Unit>,
    modifier: Modifier,
    isDebugMode: Boolean
) {
    // Do nothing on iOS
}