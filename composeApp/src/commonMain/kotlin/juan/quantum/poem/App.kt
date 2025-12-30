// shared/src/commonMain/kotlin/SignPostScreen.kt
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.material3.Text
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
    // keep same “post” proportion
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
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
                BoardWithText(
                    board = painterResource(Res.drawable.board_1),
                    text = texts.top,
                    modifier = Modifier
                )

                BoardWithText(
                    board = painterResource(Res.drawable.board_2),
                    text = texts.second,
                    modifier = Modifier,
                    textOffsetY = (10).toDp()
                )

                BoardWithText(
                    board = painterResource(Res.drawable.board_3),
                    text = texts.third,
                    modifier = Modifier,
                    textOffsetY = (-80).toDp()
                )

                BoardWithText(
                    board = painterResource(Res.drawable.board_4),
                    text = texts.bottom,
                    modifier = Modifier,
                    textOffsetY = (-60).toDp()
                )
            }
        }
    }
}


@Composable
private fun BoardWithText(
    board: androidx.compose.ui.graphics.painter.Painter,
    text: String,
    modifier: Modifier = Modifier,
    textOffsetY: Dp = (0).dp
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
            painter = board,
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
