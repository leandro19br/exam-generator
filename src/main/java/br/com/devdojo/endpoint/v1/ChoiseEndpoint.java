package br.com.devdojo.endpoint.v1;

import br.com.devdojo.model.Choise;
import br.com.devdojo.model.Question;
import br.com.devdojo.repository.ChoiseRepository;
import br.com.devdojo.repository.CourseRepository;
import br.com.devdojo.repository.QuestionRepository;
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
@RequestMapping("v1/professor/course/question/choise")
@Api(description = "operacao relacionada as alternativa dessas questões")
public class ChoiseEndpoint {

    private final QuestionRepository questionRepository;
    private final ChoiseRepository choiseRepository;
    private final EndpointUtil endpointUtil;
    private final GenericService service;

    @Autowired
    public ChoiseEndpoint(QuestionRepository questionRepository, CourseRepository courseRepository, ChoiseRepository choiseRepository, EndpointUtil endpointUtil, GenericService service) {
        this.questionRepository = questionRepository;
        this.choiseRepository = choiseRepository;
        this.endpointUtil = endpointUtil;
        this.service = service;
    }

    @ApiOperation(value = "Retorna uma alternativa baseado no id")
    @GetMapping(path = "{id}")
    public ResponseEntity<?> getChoiseById(@PathVariable long id) {
        return endpointUtil.returnObjectOrNotFound(choiseRepository.findOne(id));
    }

    //@RequestParam não é obrigatório colocar e o dafulat é vazio
    @ApiOperation(value = "Retorna uma lista de alternativas relacionadas  a questão")
    @GetMapping(path = "list/{questionId}")
    public ResponseEntity<?> listQuestions(@PathVariable Long questionId) {
        return new ResponseEntity<>(choiseRepository.lisCoiseByQuestionId(questionId), HttpStatus.OK);
    }

    //neste caso, o professor será obtido através do metodo extractProfessorForToken
    @ApiOperation(value = "cria as alternativas para a questão e retorna 200 ok", notes = "Se uma escolha for verdadeira as outras deverão ser falsa")
    @PostMapping
    @Transactional
    public ResponseEntity<?> create(@Valid @RequestBody Choise choise) {
        //antes de salvar verifica se o qustão existe
        service.throwResourceNotFoundIfNotExist(choise.getQuestion(), questionRepository, "Questão nao foi encontrada");
        choise.setProfessor(endpointUtil.extractProfessorForToken());
        Choise saveChoise = choiseRepository.save(choise);
        alteraAlternativasNaoCorretaParaFalse(choise);
        return new ResponseEntity<>(saveChoise, HttpStatus.OK);
    }

    @ApiOperation(value = "atualiza as alternativas para a questão e retorna 200 ok")
    @PutMapping
    @Transactional
    public ResponseEntity<?> update(@Valid @RequestBody Choise choise) {
        ValidaAlternativaDaQuestao(choise);
        alteraAlternativasNaoCorretaParaFalse(choise);
        choiseRepository.save(choise);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @ApiOperation(value = "Deleta uma Alternativa baseado no id e retona 200 OK")
    @DeleteMapping(path = "{id}")
    @Transactional
    public ResponseEntity<?> delete(@PathVariable long id) {
        service.throwResourceNotFoundIfNotExist(id, choiseRepository, "alternativa não encontrado");
        choiseRepository.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    private void ValidaAlternativaDaQuestao(@RequestBody @Valid Choise choise) {
        service.throwResourceNotFoundIfNotExist(choise.getQuestion(), questionRepository, "Questão relacionada a esta alternativa nao foi encontrada");
    }

    //metodo que vai fazer update nas alternativas para falso deixando somente a correta como true
    private void alteraAlternativasNaoCorretaParaFalse(Choise choise) {
        if (choise.isCorrectAnswer()) {
            choiseRepository.updateTodasAlternativasDiferenteDaCorretaParaFalso(choise, choise.getQuestion());
        }
    }

}
