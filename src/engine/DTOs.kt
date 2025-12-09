package engine

class QuizOutDto(
    val id: QuizId,
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
    val answer: List<Int>?,
)