package br.com.devdojo.endpoint.v1;

import br.com.devdojo.model.Question;
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
@RequestMapping("v1/professor/course/question")
@Api(description = "operacao relacionada as Questões do Curso")
public class QuestionEndpoint {

    private final QuestionRepository questionRepository;
    private final CourseRepository courseRepository;
    private final EndpointUtil endpointUtil;
    private final CascadeDeleteService cascadeDeleteService;
    private final GenericService service;

    @Autowired
    public QuestionEndpoint(QuestionRepository questionRepository, CourseRepository courseRepository, EndpointUtil endpointUtil, CascadeDeleteService cascadeDeleteService, GenericService service) {
        this.questionRepository = questionRepository;
        this.courseRepository = courseRepository;
        this.endpointUtil = endpointUtil;
        this.cascadeDeleteService = cascadeDeleteService;
        this.service = service;
    }

    /*@ApiOperation(value = "Retorna um curso baseado no id")
    @GetMapping(path = "{id}")
    public ResponseEntity<?> getCurseById(@PathVariable long id, Authentication authentication){
        //pegar o usuario autenticado para filtrar os cursos somente daquele professor
        Professor professor = ((ApplicationUser)authentication.getPrincipal()).getProfessor();
        return new ResponseEntity<>(questionRepository.findByIdAndProfessor(id, professor), HttpStatus.OK);
    }*/

    //outra forma de busca utilizando EL Spring
    @ApiOperation(value = "Retorna uma questão baseado no id")
    @GetMapping(path = "{id}")
    public ResponseEntity<?> getQuestionById(@PathVariable long id) {
        return endpointUtil.returnObjectOrNotFound(questionRepository.findOne(id));
    }

    //@RequestParam não é obrigatório colocar e o dafulat é vazio
    @ApiOperation(value = "Retorna uma lista de questões baseado no professor")
    @GetMapping(path = "list/{courseId}")
    public ResponseEntity<?> listQuestions(@PathVariable Long courseId,
                                           @ApiParam("Question title") @RequestParam(value = "title", defaultValue = "") String title) {
        return new ResponseEntity<>(questionRepository.lisQuestionByCourseAndTitle(courseId, title), HttpStatus.OK);
    }

    @ApiOperation(value = "Deleta um curso baseado no id e retona 200 OK")
    @DeleteMapping(path = "{id}")
    @Transactional
    public ResponseEntity<?> delete(@PathVariable long id) {
        service.throwResourceNotFoundIfNotExist(id,questionRepository,"Questao nao encontrada");
        //questionRepository.delete(id);
        cascadeDeleteService.deleteQuestionAndAllRelatedEntities(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //@Valid valida caso seja passado o nome em branco
    @ApiOperation(value = "faz o update na questão e retorna 200 ok ")
    @PutMapping
    public ResponseEntity<?> update(@Valid @RequestBody Question question) {
        service.throwResourceNotFoundIfNotExist(question,questionRepository,"Questao nao encontrada");
        questionRepository.save(question);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //neste caso, o professor será obtido através do metodo extractProfessorForToken
    @ApiOperation(value = "cria questão retorna 200 ok e o titulo da questão criado")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Question question) {
        //antes de salvar a questão, verifica se o curso existe
        service.throwResourceNotFoundIfNotExist(question.getCourse(),courseRepository,"Curso nao encontrada");
        question.setProfessor(endpointUtil.extractProfessorForToken());
        return new ResponseEntity<>(questionRepository.save(question), HttpStatus.OK);
    }

}
