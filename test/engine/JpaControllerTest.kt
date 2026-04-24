package engine

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc

@ActiveProfiles("test")
class JpaControllerTest @Autowired constructor(
    mockMvc: MockMvc,
    quizzesRepository: JpaQuizzesRepositoryAdapter,
    mapper: ObjectMapper,
    userRepo: AppUserRepository,
    passEncoder: PasswordEncoder
) : ControllerTest(mockMvc, quizzesRepository, mapper, userRepo, passEncoder)
