package engine

import org.springframework.stereotype.Service

@Service
class QuizService {
    fun getQuiz(): Quiz = Quiz(
        title = "The Java Logo",
        text = "What is depicted on the Java logo?",
        options = listOf("Robot", "Tea leaf", "Cup of coffee", "Bug")
    )

}
