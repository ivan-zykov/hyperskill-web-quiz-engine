package engine

class Quiz(
    val title: String,
    val text: String,
    val options: List<String>,
)

class AnswerResult(
    val success: Boolean,
    val feedback: String
)