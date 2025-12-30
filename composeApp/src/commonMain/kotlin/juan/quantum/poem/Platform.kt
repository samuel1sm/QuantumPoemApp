package juan.quantum.poem

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform