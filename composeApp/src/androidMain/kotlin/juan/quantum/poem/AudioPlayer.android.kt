package juan.quantum.poem

import android.media.MediaPlayer
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun BackgroundMusicEffect(resourcePath: String) {
    val context = LocalContext.current
    
    DisposableEffect(resourcePath) {
        var mediaPlayer: MediaPlayer? = null
        
        try {
            // Compose Resources are placed in 'files/' directory inside assets on Android
            val assetPath = "composeResources/quantumpoemapp.composeapp.generated.resources/files/$resourcePath"
            
            val assetManager = context.assets
            val fileExists = try {
                assetManager.open(assetPath).close()
                true
            } catch (e: Exception) {
                false
            }

            if (fileExists) {
                val fd = assetManager.openFd(assetPath)
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)
                    fd.close()
                    isLooping = true
                    prepare()
                    start()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        onDispose {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        }
    }
}
