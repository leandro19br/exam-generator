package br.com.devdojo.endpoint.v1;

import br.com.devdojo.model.Professor;
import br.com.devdojo.repository.ProfessorRepository;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/professor")
public class ProfessorEndpoint {
    private final ProfessorRepository professorRepository;

    @Autowired
    public ProfessorEndpoint(ProfessorRepository professorRepository) {
        this.professorRepository = professorRepository;
    }

    @GetMapping
    public ResponseEntity<?> hi(){
        return new ResponseEntity<>("Hi",HttpStatus.OK);
    }


    @GetMapping(path = "{id}")
    @ApiOperation(value = "Find Professor by id" , notes = "We have make this method better", response = Professor.class)
    public ResponseEntity<?> getProfessorById(@PathVariable long id){
        Professor professor= professorRepository.findOne(id);
        return new ResponseEntity<>(professor, HttpStatus.OK);
    }

}
