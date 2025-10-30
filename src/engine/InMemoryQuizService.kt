package engine

import org.springframework.stereotype.Service

@Suppress("unused")
@Service
class InMemoryQuizService : QuizService {
    override fun getQuiz(): Quiz = Quiz(
        title = "The Java Logo",
        text = "What is depicted on the Java logo?",
        options = listOf("Robot", "Tea leaf", "Cup of coffee", "Bug")
    )

    override fun checkAnswer(answerIdx: Int): AnswerResult {
        TODO("Not yet implemented")
    }

}
