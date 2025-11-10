package engine

@Suppress("unused")
class QuizOutDto(
    val id: UInt,
    val title: String,
    val text: String,
    val options: List<String>,
)

@Suppress("unused")
class ResultDto(
    val success: Boolean,
    val feedback: String
)

data class QuizInDto(
    val title: String,
    val text: String,
    val options: List<String>,
    val answer: Int,
)