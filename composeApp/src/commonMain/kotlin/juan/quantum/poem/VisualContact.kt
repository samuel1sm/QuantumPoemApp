package juan.quantum.poem

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.MutableSharedFlow

@Composable
expect fun VisualContactEffect(
    updateEvent: MutableSharedFlow<Unit>,
    modifier: Modifier = Modifier,
    isDebugMode: Boolean = false
)