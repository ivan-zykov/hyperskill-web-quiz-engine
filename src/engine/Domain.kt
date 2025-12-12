package engine

data class NewQuiz(
    val title: String,
    val text: String,
    val options: List<String>,
    val answer: List<Int>?,
)

@JvmInline
value class QuizId(val value: Int)

data class Quiz(
    val title: String,
    val text: String,
    val options: List<String>,
    val answer: List<Int>?,
    val id: QuizId,
)

data class AnswerResult(
    val success: Boolean,
    val feedback: String
)

@JvmInline
value class Answer(val value: List<Int>)