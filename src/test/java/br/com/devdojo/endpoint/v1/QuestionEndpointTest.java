package br.com.devdojo.endpoint.v1;

import br.com.devdojo.model.Course;
import br.com.devdojo.model.Question;
import br.com.devdojo.repository.QuestionRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.*;

/**
 * @project exam-generation
 * Created by Leandro Saniago on 04/03/2021 - 09:06.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class QuestionEndpointTest {

    @MockBean
    private QuestionRepository questionRepository;
    @Autowired
    private TestRestTemplate restTemplate;
    //para seguranca
    private HttpEntity<Void> professorHeader;
    private HttpEntity<Void> wrongHeader;
    private Question question = mockQuestion();

    //metodo para mokar um curso
    public static Question mockQuestion(){

        return Question.Builder.newQuestion()
                .id(1L)
                .titulo("O que é Classe")
                .course(CourseEndpointTest.mockCourse())
                .build();

    }

    //Headers para gerar o token
    @Before
    public void configProfessorHeader(){

        String body = "{\"userName\" : \"leandro\",\"passWord\" : \"santiago\"}";
        HttpHeaders headers = restTemplate.postForEntity("/login", body, String.class).getHeaders();
        this.professorHeader = new HttpEntity<>(headers);
    }

    //add authorization errado
    @Before
    public void configWrongHeader(){

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "11111");
        this.wrongHeader = new HttpEntity<>(headers);
    }

    //retorno do questionRepository mokado
    @Before
    public void setup(){

        BDDMockito.when(questionRepository.findOne(question.getId())).thenReturn(question);
        BDDMockito.when(questionRepository.lisQuestionByCourseAndTitle(question.getCourse().getId(),"")).thenReturn(Collections.singletonList(question));
        BDDMockito.when(questionRepository.lisQuestionByCourseAndTitle(question.getCourse().getId(),"O que é Classe")).thenReturn(Collections.singletonList(question));

    }

    @Test
    public void deveRetornarStatus403QuandoBuscarQuestoaPorIdETokenErrado(){
        ResponseEntity<String> exchange = restTemplate.exchange("/v1/professor/course/question/1", GET, wrongHeader, String.class);
        assertThat(exchange.getStatusCodeValue()).isEqualTo(403);
    }

    @Test
    public void deveRetornarStatus403QuandoBuscarListaQuestoesTokenErrado(){
        ResponseEntity<String> exchange = restTemplate.exchange("/v1/professor/course/question/list/1/?title=", GET, wrongHeader, String.class);
        assertThat(exchange.getStatusCodeValue()).isEqualTo(403);
    }

    @Test
    public void deveRetornarListaVaziaQandoBuscartituloQueNaoExiste(){
        ResponseEntity<List<Question>> exchange = restTemplate.exchange("/v1/professor/course/question/list/1/?title=xaxa", GET, professorHeader,
                new ParameterizedTypeReference<List<Question>>() {
                });
        assertThat(exchange.getBody()).isEmpty();
    }

    @Test
    public void deveRetornarStatus200QuandoBuscarListaQuestoesEhtituloExiste(){
        ResponseEntity<String> exchange = restTemplate.exchange("/v1/professor/course/question/list/1/?title=O que é Classe", GET, professorHeader, String.class);
        assertThat(exchange.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void deveRetornarStatus400QuandoBuscarQuestaoPorIdENaoPassarId(){
        ResponseEntity<String> exchange = restTemplate.exchange("/v1/professor/course/question/", GET, professorHeader, String.class);
        assertThat(exchange.getStatusCodeValue()).isEqualTo(400);
    }

    @Test
    public void deveRetornarStatus404QuandoBuscarQuestaoPorIdENaoExiste(){
        ResponseEntity<String> exchange = restTemplate.exchange("/v1/professor/course/question/-1", GET, professorHeader, String.class);
        assertThat(exchange.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void deveRetornarStatus200QuandoBuscarQuestaoPorIdExistente(){
        ResponseEntity<String> exchange = restTemplate.exchange("/v1/professor/course/question/1", GET, professorHeader, String.class);
        assertThat(exchange.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void deveRetornarStatus200QuandDeletarQuestaoPorIdExistente(){
        long id = 1L;
        BDDMockito.doNothing().when(questionRepository).delete(id);
        ResponseEntity<String> exchange = restTemplate.exchange("/v1/professor/course/question/{id}", DELETE, professorHeader, String.class,id);
        assertThat(exchange.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void deveRetornarStatus404QuandDeletarQuestaoPorIdNaoExistente(){
        long id = -1L;
        BDDMockito.doNothing().when(questionRepository).delete(id);
        ResponseEntity<String> exchange = restTemplate.exchange("/v1/professor/course/question/{id}", DELETE, professorHeader, String.class,id);
        assertThat(exchange.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void deveRetornarStatus400QuandCriarQuestaoComTituloNull(){
        Question question = questionRepository.findOne(1L);
        question.setTitulo(null);
        assertThat(criarQuestao(question).getStatusCodeValue()).isEqualTo(400);
    }

    @Test
    public void deveRetornarStatus404QuandCriarQuestaoComCursoNaoExiste(){
        Question question = questionRepository.findOne(1L);
        question.setCourse(new Course());
        assertThat(criarQuestao(question).getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void deveRetornarStatus404QuandCriarQuestaoComIdNull(){
        Question question = questionRepository.findOne(1L);
        question.setId(null);
        assertThat(criarQuestao(question).getStatusCodeValue()).isEqualTo(404);
    }

    public ResponseEntity<String> criarQuestao(Question question){
        BDDMockito.when(questionRepository.save(question)).thenReturn(question);
        return restTemplate.exchange("/v1/professor/course/question/", POST, new HttpEntity<>(question,professorHeader.getHeaders()),String.class);
    }


}