package mobappdev.example.apiapplication.data

data class Joke(
    val error: Boolean,
    val category: String,
    val type: String,
    val joke: String? = null,
    val setup: String? = null,
    val delivery: String? = null,
    val flags: JokeFlags,
    val id: Int,
    val safe: Boolean,
    val lang: String
)

data class JokeFlags(
    val nsfw: Boolean,
    val religious: Boolean,
    val political: Boolean,
    val racist: Boolean,
    val sexist: Boolean,
    val explicit: Boolean
)
