package engine

@Suppress("unused")
class QuizOutDto(
    val id: Int,
    val title: String,
    val text: String,
    val options: List<String>,
)

@Suppress("unused")
class ResultDto(
    val success: Boolean,
    val feedback: String
)