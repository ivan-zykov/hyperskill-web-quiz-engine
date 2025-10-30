package engine

class Quiz(
    val title: String,
    val text: String,
    val options: List<String>,
)

data class AnswerResult(
    val success: Boolean,
    val feedback: String
)