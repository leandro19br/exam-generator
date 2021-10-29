package br.com.devdojo.endpoint.v1;

import br.com.devdojo.model.Course;
import br.com.devdojo.repository.CourseRepository;
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
 * Created by Leandro Saniago on 23/02/2021 - 20:07.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class CourseEndpointTest {

    @MockBean
    private CourseRepository courseRepository;
    @Autowired
    private TestRestTemplate restTemplate;
    //para seguranca
    private HttpEntity<Void> professorHeader;
    private HttpEntity<Void> wrongHeader;
    private Course course = mockCourse();

    //metodo para mokar um curso
    public static Course mockCourse(){

        return Course.Builder.newCourse()
                .id(1L)
                .name("JAVA")
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

    //retorno do courseRepository mokado
    @Before
    public void setup(){

        BDDMockito.when(courseRepository.findOne(course.getId())).thenReturn(course);
        BDDMockito.when(courseRepository.lisCoursesByName("")).thenReturn(Collections.singletonList(course));
        BDDMockito.when(courseRepository.lisCoursesByName("JAVA")).thenReturn(Collections.singletonList(course));

    }

    @Test
    public void deveRetornarStatus403QuandoBuscarCoursePorIdETokenErrado(){
        ResponseEntity<String> exchange = restTemplate.exchange("/v1/professor/course/1", GET, wrongHeader, String.class);
        assertThat(exchange.getStatusCodeValue()).isEqualTo(403);
    }

    @Test
    public void deveRetornarStatus403QuandoBuscarListaCoursesTokenErrado(){
        ResponseEntity<String> exchange = restTemplate.exchange("/v1/professor/course/list?name=", GET, wrongHeader, String.class);
        assertThat(exchange.getStatusCodeValue()).isEqualTo(403);
    }

    @Test
    public void deveRetornarListaVaziaQandoBuscarNomeQueNaoExiste(){
        ResponseEntity<List<Course>> exchange = restTemplate.exchange("/v1/professor/course/list?name=xaxa", GET, professorHeader,
                new ParameterizedTypeReference<List<Course>>() {
                });
        assertThat(exchange.getBody()).isEmpty();
    }

    @Test
    public void deveRetornarStatus200QuandoBuscarListaCoursesEhNomeExiste(){
        ResponseEntity<String> exchange = restTemplate.exchange("/v1/professor/course/list?name=JAVA", GET, professorHeader, String.class);
        assertThat(exchange.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void deveRetornarStatus400QuandoBuscarCoursePorIdENaoPassarId(){
        ResponseEntity<String> exchange = restTemplate.exchange("/v1/professor/course/", GET, professorHeader, String.class);
        assertThat(exchange.getStatusCodeValue()).isEqualTo(400);
    }

    @Test
    public void deveRetornarStatus404QuandoBuscarCoursePorIdENaoExiste(){
        ResponseEntity<String> exchange = restTemplate.exchange("/v1/professor/course/-1", GET, professorHeader, String.class);
        assertThat(exchange.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void deveRetornarStatus200QuandoBuscarCoursePorIdExistente(){
        ResponseEntity<String> exchange = restTemplate.exchange("/v1/professor/course/1", GET, professorHeader, String.class);
        assertThat(exchange.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void deveRetornarStatus200QuandDeletarCoursePorIdExistente(){
        long id = 1L;
        BDDMockito.doNothing().when(courseRepository).delete(id);
        ResponseEntity<String> exchange = restTemplate.exchange("/v1/professor/course/{id}", DELETE, professorHeader, String.class,id);
        assertThat(exchange.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void deveRetornarStatus404QuandDeletarCoursePorIdNaoExistente(){
        long id = -1L;
        BDDMockito.doNothing().when(courseRepository).delete(id);
        ResponseEntity<String> exchange = restTemplate.exchange("/v1/professor/course/{id}", DELETE, professorHeader, String.class,id);
        assertThat(exchange.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void deveRetornarStatus400QuandCriarCourseComNomeNull(){
        Course course = courseRepository.findOne(1L);
        course.setName(null);
        assertThat(criarCourse(course).getStatusCodeValue()).isEqualTo(400);
    }

    @Test
    public void deveRetornarStatus200QuandCriarCourseComIdNull(){
        Course course = courseRepository.findOne(1L);
        course.setId(null);
        assertThat(criarCourse(course).getStatusCodeValue()).isEqualTo(200);
    }

    public ResponseEntity<String> criarCourse(Course course){
        BDDMockito.when(courseRepository.save(course)).thenReturn(course);
        return restTemplate.exchange("/v1/professor/course/", POST, new HttpEntity<>(course,professorHeader.getHeaders()),String.class);
    }


}