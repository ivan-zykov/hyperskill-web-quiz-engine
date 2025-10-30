package engine

@Suppress("unused")
class QuizDto (
    val title: String,
    val text: String,
    val options: List<String>,
)

@Suppress("unused")
class ResultDto(
    val success: Boolean,
    val feedback: String
)