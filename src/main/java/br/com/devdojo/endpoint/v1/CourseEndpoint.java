package br.com.devdojo.endpoint.v1;

import br.com.devdojo.model.Course;
import br.com.devdojo.repository.CourseRepository;
import br.com.devdojo.repository.QuestionRepository;
import br.com.devdojo.service.CascadeDeleteService;
import br.com.devdojo.service.GenericService;
import br.com.devdojo.utils.EndpointUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("v1/professor/course")
@Api(description = "operacao relacionada ao professor")
public class CourseEndpoint {

    private final CourseRepository courseRepository;
    private final QuestionRepository questionRepository;
    private final EndpointUtil endpointUtil;
    private final CascadeDeleteService cascadeDeleteService;
    private final GenericService service;

    @Autowired
    public CourseEndpoint(CourseRepository courseRepository, QuestionRepository questionRepository, EndpointUtil endpointUtil, CascadeDeleteService cascadeDeleteService, GenericService service) {
        this.courseRepository = courseRepository;
        this.questionRepository = questionRepository;
        this.endpointUtil = endpointUtil;
        this.cascadeDeleteService = cascadeDeleteService;
        this.service = service;
    }

    /*@ApiOperation(value = "Retorna um curso baseado no id")
    @GetMapping(path = "{id}")
    public ResponseEntity<?> getCurseById(@PathVariable long id, Authentication authentication){
        //pegar o usuario autenticado para filtrar os cursos somente daquele professor
        Professor professor = ((ApplicationUser)authentication.getPrincipal()).getProfessor();
        return new ResponseEntity<>(courseRepository.findByIdAndProfessor(id, professor), HttpStatus.OK);
    }*/

    //outra forma de busca utilizando EL Spring
    @ApiOperation(value = "Retorna um curso baseado no id")
    @GetMapping(path = "{id}")
    public ResponseEntity<?> getCurseById(@PathVariable long id) {
        return endpointUtil.returnObjectOrNotFound(courseRepository.findOne(id));
    }

    //@RequestParam não é obrigatório colocar e o dafulat é vazio
    @ApiOperation(value = "Retorna uma lista de curso baseado no professor")
    @GetMapping(path = "list")
    public ResponseEntity<?> listCourses(@ApiParam("Course Name") @RequestParam(value = "name", defaultValue = "") String name) {
        return new ResponseEntity<>(courseRepository.lisCoursesByName(name),HttpStatus.OK);
    }

    @ApiOperation(value = "Deleta um curso baseado no id e retona 200 OK")
    @DeleteMapping(path = "{id}")
    @Transactional//cascade faz tudo ou não faz nada(rollback em nos dois metodos
    public ResponseEntity<?> delete(@PathVariable long id) {
        service.throwResourceNotFoundIfNotExist(id,courseRepository,"Curso não encontrado");
       /* courseRepository.delete(id);
        //deleta as questoes relacionadas ao curso
        questionRepository.deleteAllQuestionRelatedToCourse(id);*/
       cascadeDeleteService.deleteCourseAndAllRelatedEntities(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //@Valid valida caso seja passado o nome em branco
    @ApiOperation(value = "faz o update no curso e retorna 200 ok ")
    @PutMapping
    public ResponseEntity<?> update(@Valid @RequestBody Course course) {
        service.throwResourceNotFoundIfNotExist(course, courseRepository, "Curso não encontrado");
        courseRepository.save(course);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //neste caso, o professor será obtido através do metodo extractProfessorForToken
    @ApiOperation(value = "cria o curso retorna 200 ok e o curso criado")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Course course) {
        course.setProfessor(endpointUtil.extractProfessorForToken());
        return new ResponseEntity<>(courseRepository.save(course),HttpStatus.OK);
    }

}
