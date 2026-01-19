// shared/src/commonMain/kotlin/SignPostScreen.kt
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import juan.quantum.poem.AppFonts
import quantumpoemapp.composeapp.generated.resources.Res
import quantumpoemapp.composeapp.generated.resources.background
import quantumpoemapp.composeapp.generated.resources.board_1
import quantumpoemapp.composeapp.generated.resources.board_2
import quantumpoemapp.composeapp.generated.resources.board_3
import quantumpoemapp.composeapp.generated.resources.board_4
import juan.quantum.poem.VisualContactEffect
import juan.quantum.poem.BackgroundMusicEffect

data class SignText(
    val top: String,
    val second: String,
    val third: String,
    val bottom: String
)

data class BoardWithTextModel(
    val board: DrawableResource,
    val text: String,
    val textOffsetBias: Float = 0f
)

@Composable
fun SignPostScreen(
    texts: SignText = SignText(
        top = "With stars exploding",
        second = "The universe dies",
        third = "Our hands keep holding",
        bottom = "As I see your eyes",
    ),
    modifier: Modifier = Modifier
) {
    val initialBoards = listOf(
        BoardWithTextModel(
            board = Res.drawable.board_1,
            text = texts.top
        ),
        BoardWithTextModel(
            board = Res.drawable.board_2,
            text = texts.second,
            textOffsetBias = 0.05f
        ),
        BoardWithTextModel(
            board = Res.drawable.board_3,
            text = texts.third,
            textOffsetBias = -0.15f
        ),
        BoardWithTextModel(
            board = Res.drawable.board_4,
            text = texts.bottom,
            textOffsetBias = -0.1f
        )
    )

    var boards by remember(texts) { mutableStateOf(initialBoards) }
    var isDebugMode by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    val updateEvent = remember { MutableSharedFlow<Unit>() }

    LaunchedEffect(Unit) {
        updateEvent.collect {
            boards = boards.shuffled()
        }
    }

    BackgroundMusicEffect("background.mp3")

    Column(modifier = modifier.fillMaxSize()) {
        val signPostModifier = if (isDebugMode) {
            Modifier.weight(0.5f)
        } else {
            Modifier
        }

        BoxWithConstraints(
            modifier = signPostModifier,
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
            ) {
                // Background
                Image(
                    painter = painterResource(Res.drawable.background),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier.align(Alignment.TopCenter).fillMaxHeight(0.75f),
                ) {
                    boards.forEach { item ->
                        BoardWithText(
                            board = item.board,
                            text = item.text,
                            textOffsetBias = item.textOffsetBias,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

//            Row(
//                modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text("Debug Mode")
//                Switch(
//                    checked = isDebugMode,
//                    onCheckedChange = { isDebugMode = it },
//                    modifier = Modifier.padding(start = 8.dp)
//                )
//            }
        }
        
        val visualContactModifier = if (isDebugMode) {
            Modifier.weight(0.5f)
        } else {
            Modifier
        }

        VisualContactEffect(
            updateEvent = updateEvent,
            modifier = visualContactModifier,
            isDebugMode = isDebugMode
        )
    }
}


@Composable
private fun BoardWithText(
    board: DrawableResource,
    text: String,
    modifier: Modifier = Modifier,
    textOffsetBias: Float = 0f
) {
    val textStyle = TextStyle(
        color = Color(0xFFF2F2F2),
        fontFamily = AppFonts.ComingSoonFamily(),
        fontWeight = FontWeight.ExtraBold,
        fontSize = 22.sp,
        lineHeight = 24.sp,
        textAlign = TextAlign.Center
    )

    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val boardHeight = maxHeight

        Image(
            painter = painterResource(board),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillBounds
        )

        Text(
            text = text,
            style = textStyle,
            modifier = Modifier.offset(x = 0.dp, y = boardHeight * textOffsetBias)
        )
    }
}

// helper: Int px -> dp using current density
@Composable
private fun Int.toDp(): Dp = with(androidx.compose.ui.platform.LocalDensity.current) { this@toDp.toDp() }

@Preview
@Composable
fun SignPostScreenPreview() {
    SignPostScreen()
}
