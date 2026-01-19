package juan.quantum.poem

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font // Important: Use this import
import quantumpoemapp.composeapp.generated.resources.Res
import quantumpoemapp.composeapp.generated.resources.ComingSoon_Regular // This is generated after you add the font and rebuild

object AppFonts {
    @Composable
    fun ComingSoonFamily() = FontFamily(
        Font(
            Res.font.ComingSoon_Regular,
            weight = FontWeight.Normal,
            style = FontStyle.Normal
        )
    )
}