package juan.quantum.poem

import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android test ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()