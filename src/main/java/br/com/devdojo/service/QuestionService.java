package br.com.devdojo.service;

import br.com.devdojo.exeption.ResourceNotFoundexeception;
import br.com.devdojo.model.Question;
import br.com.devdojo.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * @project exam-generation
 * Created by Leandro Saniago on 17/02/2021 - 19:08.
 * classe responsável por acessar o repository e buscar a questão se não for encontrado lança exeption
 */
@Service
public class QuestionService implements Serializable {

    private final QuestionRepository questionRepository;

    @Autowired
    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public void throwResourceNotFoundIfQuestionDoesNotExist(Question question){

        if (question == null || question.getId() == null || questionRepository.findOne(question.getId()) == null){

            throw new ResourceNotFoundexeception("Questão não encontrado");
        }

    }

    public void throwResourceNotFoundIfQuestionDoesNotExist(long questionId){

        if (questionId == 0 || questionRepository.findOne(questionId) == null){

            throw new ResourceNotFoundexeception("Questão não encontrado");
        }

    }


}
