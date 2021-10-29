package br.com.devdojo.endpoint.v1;

import br.com.devdojo.model.Choise;
import br.com.devdojo.model.Question;
import br.com.devdojo.repository.ChoiseRepository;
import br.com.devdojo.repository.QuestionRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
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

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.*;

/**
 * @project exam-generation
 * Created by Leandro Saniago on 12/03/2021 - 10:45.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ChoiseEndpointTest {

    @MockBean
    private QuestionRepository questionRepository;
    @MockBean
    private ChoiseRepository choiseRepository;
    @Autowired
    private TestRestTemplate restTemplate;
    //para seguranca
    private HttpEntity<Void> professorHeader;
    private HttpEntity<Void> wrongHeader;
    private Choise choiseCorrectAnswerFalse = mockChoiseCorrectAnswerFalse();
    private Choise choiseCorrectAnswerTrue = mockChoiseCorrectAnswerTrue();

    //metodo para mokar uma questão
    public static Choise mockChoiseCorrectAnswerFalse(){
        return Choise.ChoiseBuilder.builder()
                .id(1L)
                .titulo("Is a room?")
                .question(QuestionEndpointTest.mockQuestion())
                .correctAnswer(false)
                .professor(ProfessorEndpointTest.mockProfessor())
                .build();

    }

    public static Choise mockChoiseCorrectAnswerTrue(){
        return Choise.ChoiseBuilder.builder()
                .id(2L)
                .titulo("Is a Template?")
                .question(QuestionEndpointTest.mockQuestion())
                .correctAnswer(true)
                .professor(ProfessorEndpointTest.mockProfessor())
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

    //retorno do choiseRepository mokado
    @Before
    public void setup(){

        BDDMockito.when(choiseRepository.findOne(choiseCorrectAnswerFalse.getId())).thenReturn(choiseCorrectAnswerFalse);
        BDDMockito.when(choiseRepository.findOne(choiseCorrectAnswerTrue.getId())).thenReturn(choiseCorrectAnswerTrue);
        BDDMockito.when(choiseRepository.lisCoiseByQuestionId(choiseCorrectAnswerTrue.getQuestion().getId()))
                .thenReturn(asList(choiseCorrectAnswerFalse,choiseCorrectAnswerTrue));
        BDDMockito.when(questionRepository.findOne(choiseCorrectAnswerTrue.getQuestion().getId()))
                .thenReturn(choiseCorrectAnswerTrue.getQuestion());


    }

    @Test
    public void deveRetornarStatus403QuandoBuscaeAlternativaPorIdETokenErrado(){
        ResponseEntity<String> exchange = restTemplate.exchange("/v1/professor/course/question/choise/1", GET, wrongHeader, String.class);
        assertThat(exchange.getStatusCodeValue()).isEqualTo(403);
    }

    @Test
    public void deveRetornarStatus403QuandoBuscarListaAlternativaPorIdTokenErrado(){
        ResponseEntity<String> exchange = restTemplate.exchange("/v1/professor/course/question/choise/list/1/", GET, wrongHeader, String.class);
        assertThat(exchange.getStatusCodeValue()).isEqualTo(403);
    }

    @Test
    public void deveRetornarListaVaziaQandoBuscarAlternativaIdQueNaoExiste(){
        ResponseEntity<List<Choise>> exchange = restTemplate.exchange("/v1/professor/course/question/choise/list/3/", GET, professorHeader,
                new ParameterizedTypeReference<List<Choise>>() {
                });
        assertThat(exchange.getBody()).isEmpty();
    }

    @Test
    public void deveRetornarStatus200QuandoBuscarListaEhIdDaQuestoeExiste(){
        ResponseEntity<String> exchange = restTemplate.exchange("/v1/professor/course/question/choise/list/1/", GET, professorHeader, String.class);
        assertThat(exchange.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void deveRetornarStatus400QuandoBuscarQuestaoPorIdENaoPassarId(){
        ResponseEntity<String> exchange = restTemplate.exchange("/v1/professor/course/question/choise/", GET, professorHeader, String.class);
        assertThat(exchange.getStatusCodeValue()).isEqualTo(400);
    }

    @Test
    public void deveRetornarStatus404QuandoBuscarQuestaoPorIdENaoExisteAlternativa(){
        ResponseEntity<String> exchange = restTemplate.exchange("/v1/professor/course/question/choise/-1", GET, professorHeader, String.class);
        assertThat(exchange.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void deveRetornarStatus200QuandoBuscarQuestaoPorIdExistenteEAlternativaExiste(){
        ResponseEntity<String> exchange = restTemplate.exchange("/v1/professor/course/question/choise/1", GET, professorHeader, String.class);
        assertThat(exchange.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void deveRetornarStatus200QuandDeletarAlternativaPorIdExistente(){
        long id = 1L;
        BDDMockito.doNothing().when(choiseRepository).delete(id);
        ResponseEntity<String> exchange = restTemplate.exchange("/v1/professor/course/question/choise/{id}", DELETE, professorHeader, String.class,id);
        assertThat(exchange.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void deveRetornarStatus404QuandDeletarAltrnativaPorIdNaoExistente(){
        long id = -1L;
        BDDMockito.doNothing().when(choiseRepository).delete(id);
        ResponseEntity<String> exchange = restTemplate.exchange("/v1/professor/course/question/choise/{id}", DELETE, professorHeader, String.class,id);
        assertThat(exchange.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void deveRetornarStatus400QuandCriarAlternativaComTituloNull(){
        Choise choise = choiseRepository.findOne(1L);
        choise.setTitulo(null);
        assertThat(criarAlternativa(choise).getStatusCodeValue()).isEqualTo(400);
    }

    @Test
    public void deveRetornarStatus404QuandCriarAlternativaComQuestaoNaoExiste(){
        Choise choise = choiseRepository.findOne(1L);
        choise.setQuestion(new Question());
        assertThat(criarAlternativa(choise).getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void deveRetornarStatus200QuandCriarQuestaoComIdNull(){
        Choise choise = choiseRepository.findOne(1L);
        choise.setId(null);
        assertThat(criarAlternativa(choise).getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void deveCriarAlternativaComCorrectAnswerTrueEAtualizarAlternativasCriadas(){
        Choise choise = choiseRepository.findOne(2L);
        choise.setId(null);
        criarAlternativa(choise);
        //verifica se o método salvar foi chamado 1 vez
        BDDMockito.verify(choiseRepository, Mockito.times(1)).save(choise);
        BDDMockito.verify(choiseRepository, Mockito.times(1))
                .updateTodasAlternativasDiferenteDaCorretaParaFalso(choise, choise.getQuestion());

    }

    public ResponseEntity<String> criarAlternativa(Choise choise){
        BDDMockito.when(choiseRepository.save(choise)).thenReturn(choise);
        return restTemplate.exchange("/v1/professor/course/question/choise/", POST, new HttpEntity<>(choise,professorHeader.getHeaders()),String.class);
    }

}