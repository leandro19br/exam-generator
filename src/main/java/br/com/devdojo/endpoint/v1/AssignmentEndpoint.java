package br.com.devdojo.endpoint.v1;

import br.com.devdojo.model.Assignment;
import br.com.devdojo.repository.AssignmentRepository;
import br.com.devdojo.repository.CourseRepository;
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
@RequestMapping("v1/professor/course/assignment")
@Api(description = "operacao relacionada as provas dos Curso")
public class AssignmentEndpoint {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final EndpointUtil endpointUtil;
    private final CascadeDeleteService cascadeDeleteService;
    private final GenericService service;

    @Autowired
    public AssignmentEndpoint(AssignmentRepository assignmentRepository, CourseRepository courseRepository, EndpointUtil endpointUtil, CascadeDeleteService cascadeDeleteService, GenericService service) {
        this.assignmentRepository = assignmentRepository;
        this.courseRepository = courseRepository;
        this.endpointUtil = endpointUtil;
        this.cascadeDeleteService = cascadeDeleteService;
        this.service = service;
    }

    @ApiOperation(value = "Retorna uma prova baseado no id")
    @GetMapping(path = "{id}")
    public ResponseEntity<?> getAssignmentById(@PathVariable long id) {
        return endpointUtil.returnObjectOrNotFound(assignmentRepository.findOne(id));
    }

    //@RequestParam não é obrigatório colocar e o dafulat é vazio
    @ApiOperation(value = "Retorna uma lista de provas relacioandas ao curso")
    @GetMapping(path = "list/{courseId}")
    public ResponseEntity<?> listAssignments(@PathVariable Long courseId,
                                           @ApiParam("Assignment title") @RequestParam(value = "title", defaultValue = "") String title) {
        return new ResponseEntity<>(assignmentRepository.listAssignmentByCourseAndTitle(courseId, title), HttpStatus.OK);
    }

    @ApiOperation(value = "Deleta uma prova baseado no id e retona 200 OK")
    @DeleteMapping(path = "{id}")
    @Transactional
    public ResponseEntity<?> delete(@PathVariable long id) {
        service.throwResourceNotFoundIfNotExist(id,assignmentRepository,"Prova nao encontrada");
        assignmentRepository.delete(id);
        //cascadeDeleteService.cascadeDeleteQuestionAndChoise(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //@Valid valida caso seja passado o nome em branco
    @ApiOperation(value = "faz o update na prova e retorna 200 ok ")
    @PutMapping
    public ResponseEntity<?> update(@Valid @RequestBody Assignment assignment) {
        service.throwResourceNotFoundIfNotExist(assignment,assignmentRepository,"prova nao encontrada");
        assignmentRepository.save(assignment);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //neste caso, o professor será obtido através do metodo extractProfessorForToken
    @ApiOperation(value = "cria a prova retorna 200 ok e o titulo da prova criada")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Assignment assignment) {

        service.throwResourceNotFoundIfNotExist(assignment.getCourse(),courseRepository,"Curso nao encontrada");
        assignment.setProfessor(endpointUtil.extractProfessorForToken());
        return new ResponseEntity<>(assignmentRepository.save(assignment), HttpStatus.OK);
    }

}
