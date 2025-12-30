// shared/src/commonMain/kotlin/SignPostScreen.kt
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import quantumpoemapp.composeapp.generated.resources.Res
import quantumpoemapp.composeapp.generated.resources.background
import quantumpoemapp.composeapp.generated.resources.board_1
import quantumpoemapp.composeapp.generated.resources.board_2
import quantumpoemapp.composeapp.generated.resources.board_3
import quantumpoemapp.composeapp.generated.resources.board_4

data class SignText(
    val top: String,
    val second: String,
    val third: String,
    val bottom: String
)

data class BoardWithTextModel(
    val board: DrawableResource,
    val text: String,
    val textOffsetY: Dp = 0.dp
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
            textOffsetY = (10).toDp()
        ),
        BoardWithTextModel(
            board = Res.drawable.board_3,
            text = texts.third,
            textOffsetY = (-80).toDp()
        ),
        BoardWithTextModel(
            board = Res.drawable.board_4,
            text = texts.bottom,
            textOffsetY = (-60).toDp()
        )
    )

    var boards by remember(texts) { mutableStateOf(initialBoards) }
    
    val scope = rememberCoroutineScope()
    val updateEvent = remember { MutableSharedFlow<Unit>() }

    LaunchedEffect(Unit) {
        updateEvent.collect {
            boards = boards.shuffled()
        }
    }

    // keep same “post” proportion
    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
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

            Column {
                boards.forEach { item ->
                    BoardWithText(
                        board = item.board,
                        text = item.text,
                        textOffsetY = item.textOffsetY
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = {
                scope.launch {
                    updateEvent.emit(Unit)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text("TEst")
        }
    }
}


@Composable
private fun BoardWithText(
    board: DrawableResource,
    text: String,
    modifier: Modifier = Modifier,
    textOffsetY: Dp = 0.dp
) {
    val textStyle = TextStyle(
        color = Color(0xFFF2F2F2),
        fontSize = 22.sp,
        lineHeight = 24.sp,
        textAlign = TextAlign.Center
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(board),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillBounds
        )

        Text(
            text = text,
            style = textStyle,
            modifier = Modifier.offset(x = 0.dp, y = textOffsetY)
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
