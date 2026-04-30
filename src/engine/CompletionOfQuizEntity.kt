package engine

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "completionsOfQuizzes")
class CompletionOfQuizEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null

    @ManyToOne
    @JoinColumn(name = "quizId")
    var quiz: QuizEntity? = null

    @Column(name = "completedAt")
    var completedAt: LocalDateTime? = null
}
