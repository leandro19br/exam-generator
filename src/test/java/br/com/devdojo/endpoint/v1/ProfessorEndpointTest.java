package br.com.devdojo.endpoint.v1;

import br.com.devdojo.model.Professor;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @project exam-generation
 * Created by Leandro Saniago on 23/02/2021 - 20:00.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ProfessorEndpointTest {

public static Professor mockProfessor(){
    return Professor.Builder.newProfessor().id(1L)
            .name("Leandro")
            .email("leandro@gmail.com")
            .build();
}

}