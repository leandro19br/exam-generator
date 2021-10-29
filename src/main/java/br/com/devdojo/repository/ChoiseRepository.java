package br.com.devdojo.repository;

import br.com.devdojo.model.Choise;
import br.com.devdojo.model.Course;
import br.com.devdojo.model.Professor;
import br.com.devdojo.model.Question;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @project exam-generation
 * Created by Leandro Saniago on 04/02/2021 - 19:22.
 * quando extends PagingAndSortingRepository já herda o CRUD básico
 */
public interface ChoiseRepository extends CustomPagingAndSortingRepository<Choise, Long> {

    @Query(value = "select c from Choise c where c.question.id =?1 and c.professor = ?#{principal.professor} and c.enabled = true")
    List<Choise> lisCoiseByQuestionId(long questionId);

    //query para atualizar tosas as alternativas diferente de true relacionadas a questão
    @Query(value = "update Choise c set c.correctAnswer = false where c <> ?1 and c.question = ?2 and c.professor = ?#{principal.professor} and c.enabled = true")
    @Modifying
    void updateTodasAlternativasDiferenteDaCorretaParaFalso(Choise choise, Question question);

    //metodo que vai deletar as alternativas relacionadas ao id da questão
    @Query("update Choise c set c.enabled = false where c.question.id =?1 and c.professor = ?#{principal.professor} and c.enabled = true")
    @Modifying
    void deleteAllChoisesRelatedToQuestion(long questionId);

    //metodo que vai deletar as alternativas relacionadas ao id do curso
    @Query("update Choise c set c.enabled = false where c.question.id in (select q.id from Question q where q.course.id = ?1) and c.professor = ?#{principal.professor} and c.enabled = true")
    @Modifying
    void deleteAllChoisesRelatedToCourse(long courseId);

}
