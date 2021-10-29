package br.com.devdojo.endpoint.v1;

import br.com.devdojo.model.Assignment;
import br.com.devdojo.model.Course;
import br.com.devdojo.repository.AssignmentRepository;
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
 * Created by Leandro Saniago on 06/04/2021 - 09:46.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AssignmentEndpointTest {

    @MockBean
    private AssignmentRepository assignmentRepository;
    @Autowired
    private TestRestTemplate restTemplate;
    //para seguranca
    private HttpEntity<Void> professorHeader;
    private HttpEntity<Void> wrongHeader;
    private Assignment assignment = mockAssignment();

    //metodo para mokar um curso
    public static Assignment mockAssignment(){

        return Assignment.AssignmentBuilder.newBuilder()
                .id(1L)
                .titulo("Certificação Java")
                .course(CourseEndpointTest.mockCourse())
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

    //retorno do assignmentRepository mokado
    @Before
    public void setup(){

        BDDMockito.when(assignmentRepository.findOne(assignment.getId())).thenReturn(assignment);
        BDDMockito.when(assignmentRepository.listAssignmentByCourseAndTitle(assignment.getCourse().getId(),"")).thenReturn(Collections.singletonList(assignment));
        BDDMockito.when(assignmentRepository.listAssignmentByCourseAndTitle(assignment.getCourse().getId(),"Certificação Java")).thenReturn(Collections.singletonList(assignment));

    }

    @Test
    public void deveRetornarStatus403QuandoBuscarProvaPorIdETokenErrado(){
        ResponseEntity<String> exchange = restTemplate.exchange("/v1/professor/course/assignment/1", GET, wrongHeader, String.class);
        assertThat(exchange.getStatusCodeValue()).isEqualTo(403);
    }

    @Test
    public void deveRetornarStatus403QuandoBuscarListaProvasTokenErrado(){
        ResponseEntity<String> exchange = restTemplate.exchange("/v1/professor/course/assignment/list/1/?title=", GET, wrongHeader, String.class);
        assertThat(exchange.getStatusCodeValue()).isEqualTo(403);
    }

    @Test
    public void deveRetornarListaVaziaQandoBuscartituloQueNaoExiste(){
        ResponseEntity<List<Assignment>> exchange = restTemplate.exchange("/v1/professor/course/assignment/list/1/?title=xaxa", GET, professorHeader,
                new ParameterizedTypeReference<List<Assignment>>() {
                });
        assertThat(exchange.getBody()).isEmpty();
    }

    @Test
    public void deveRetornarStatus200QuandoBuscarListaProvasEhtituloExiste(){
        ResponseEntity<String> exchange = restTemplate.exchange("/v1/professor/course/assignment/list/1/?title=Certificação Java", GET, professorHeader, String.class);
        assertThat(exchange.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void deveRetornarStatus400QuandoBuscarProvaPorIdENaoPassarId(){
        ResponseEntity<String> exchange = restTemplate.exchange("/v1/professor/course/assignment/", GET, professorHeader, String.class);
        assertThat(exchange.getStatusCodeValue()).isEqualTo(400);
    }

    @Test
    public void deveRetornarStatus404QuandoBuscarProvaPorIdENaoExiste(){
        ResponseEntity<String> exchange = restTemplate.exchange("/v1/professor/course/assignment/-1", GET, professorHeader, String.class);
        assertThat(exchange.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void deveRetornarStatus200QuandoBuscarProvaPorIdExistente(){
        ResponseEntity<String> exchange = restTemplate.exchange("/v1/professor/course/assignment/1", GET, professorHeader, String.class);
        assertThat(exchange.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void deveRetornarStatus200QuandDeletarProvaPorIdExistente(){
        long id = 1L;
        BDDMockito.doNothing().when(assignmentRepository).delete(id);
        ResponseEntity<String> exchange = restTemplate.exchange("/v1/professor/course/assignment/{id}", DELETE, professorHeader, String.class,id);
        assertThat(exchange.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void deveRetornarStatus404QuandDeletarProvaPorIdNaoExistente(){
        long id = -1L;
        BDDMockito.doNothing().when(assignmentRepository).delete(id);
        ResponseEntity<String> exchange = restTemplate.exchange("/v1/professor/course/assignment/{id}", DELETE, professorHeader, String.class,id);
        assertThat(exchange.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void deveRetornarStatus400QuandCriarProvaComTituloNull(){
        Assignment assignment = assignmentRepository.findOne(1L);
        assignment.setTitulo(null);
        assertThat(criarProva(assignment).getStatusCodeValue()).isEqualTo(400);
    }

    @Test
    public void deveRetornarStatus404QuandCriarProvaComCursoNaoExiste(){
        Assignment assignment = assignmentRepository.findOne(1L);
        assignment.setCourse(new Course());
        assertThat(criarProva(assignment).getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void deveRetornarStatus404QuandCriarProvaComIdNull(){
        Assignment assignment = assignmentRepository.findOne(1L);
        assignment.setId(null);
        assertThat(criarProva(assignment).getStatusCodeValue()).isEqualTo(404);
    }

    public ResponseEntity<String> criarProva(Assignment assignment){
        BDDMockito.when(assignmentRepository.save(assignment)).thenReturn(assignment);
        return restTemplate.exchange("/v1/professor/course/assignment/", POST, new HttpEntity<>(assignment,professorHeader.getHeaders()),String.class);
    }


}