package engine

data class Quiz(
    val title: String,
    val text: String,
    val options: List<String>,
    val answer: Int,
)

data class AnswerResult(
    val success: Boolean,
    val feedback: String
)