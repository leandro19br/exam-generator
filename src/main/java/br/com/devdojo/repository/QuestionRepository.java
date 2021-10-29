package br.com.devdojo.repository;

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
public interface QuestionRepository extends CustomPagingAndSortingRepository<Question, Long> {

    @Query(value = "select q from Question q where q.course.id =?1 and q.titulo like %?2% and q.professor = ?#{principal.professor} and q.enabled = true")
    List<Question> lisQuestionByCourseAndTitle(Long courseId, String titulo);

    //metodo que vai deletar as questoes relacionadas ao id curso
    @Query("update Question q set q.enabled = false where q.course.id =?1 and q.professor = ?#{principal.professor} and q.enabled = true")
    @Modifying
    void deleteAllQuestionRelatedToCourse(long courseId);

    /*@Query(value = "delete from Course c where c.id =?1 and c.professor = ?#{principal.professor}")
    @Modifying//anotação para definir que esta sendo modificado o estado do bd
    void delete(Long id);

    @Query(value = "delete from Course c where c = ?1 and c.professor = ?#{principal.professor}")
    @Modifying
    void delete(Course course);*/

}
