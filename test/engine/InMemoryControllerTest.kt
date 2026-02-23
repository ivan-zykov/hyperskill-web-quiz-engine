package engine

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc

@ActiveProfiles("in-memory")
class InMemoryControllerTest @Autowired constructor(
    mockMvc: MockMvc,
    quizzesRepository: QuizzesRepository,
    mapper: ObjectMapper
) : ControllerTest(mockMvc, quizzesRepository, mapper)