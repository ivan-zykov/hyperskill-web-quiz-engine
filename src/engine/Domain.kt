package engine

@JvmInline
value class QuizId(val value: Int)

data class Quiz(
    val title: String,
    val text: String,
    val options: List<String>,
    val answer: Int,
    val id: QuizId? = null,
)

data class AnswerResult(
    val success: Boolean,
    val feedback: String
)