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

            val style = TextStyle(
                color = Color(0xFFF2F2F2),
                fontSize = 22.sp,
                lineHeight = 24.sp,
                textAlign = TextAlign.Center
            )

            // Boards + text (positions tuned to match your example)
            BoardWithText(
                board = painterResource(Res.drawable.board_1),
                text = texts.top,
                textStyle = style,
                modifier = Modifier
//                    .absoluteOffset(x = (0.18f * constraints.maxWidth).toInt(), y = (0.02f * constraints.maxHeight).toInt())
//                    .width((0.64f * constraints.maxWidth).toDp())
            )

            BoardWithText(
                board = painterResource(Res.drawable.board_2),
                text = texts.second,
                textStyle = style,
                modifier = Modifier
//                    .absoluteOffset(x = (0.21f * constraints.maxWidth).toInt(), y = (0.18f * constraints.maxHeight).toInt())
//                    .width((0.66f * constraints.maxWidth).toDp())
            )

            BoardWithText(
                board = painterResource(Res.drawable.board_3),
                text = texts.third,
                textStyle = style,
                modifier = Modifier
//                    .absoluteOffset(x = (0.18f * constraints.maxWidth).toInt(), y = (0.38f * constraints.maxHeight).toInt())
//                    .width((0.65f * constraints.maxWidth).toDp())
            )

            BoardWithText(
                board = painterResource(Res.drawable.board_4),
                text = texts.bottom,
                textStyle = style,
                modifier = Modifier
//                    .absoluteOffset(x = (0.22f * constraints.maxWidth).toInt(), y = (0.54f * constraints.maxHeight).toInt())
//                    .width((0.65f * constraints.maxWidth).toDp())
            )
        }
    }
}


@Composable
private fun BoardWithText(
    board: androidx.compose.ui.graphics.painter.Painter,
    text: String,
    textStyle: TextStyle,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = board,
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Fit
        )

        Text(
            text = text,
            style = textStyle,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 12.dp)
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
