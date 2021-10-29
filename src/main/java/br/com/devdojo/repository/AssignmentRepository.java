package br.com.devdojo.repository;

import br.com.devdojo.model.Assignment;
import br.com.devdojo.model.Question;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @project exam-generation
 * Created by Leandro Saniago on 31/03/2021 - 19:22.
 * quando extends PagingAndSortingRepository já herda o CRUD básico
 */
public interface AssignmentRepository extends CustomPagingAndSortingRepository<Assignment, Long> {

    @Query(value = "select a from Assignment a where a.course.id =?1 and a.titulo like %?2% and a.professor = ?#{principal.professor} and a.enabled = true")
    List<Assignment> listAssignmentByCourseAndTitle(Long courseId, String titulo);

    //metodo que vai deletar as questoes relacionadas ao id curso
    @Query("update Assignment a set a.enabled = false where a.course.id =?1 and a.professor = ?#{principal.professor} and a.enabled = true")
    @Modifying
    void deleteAllAssignmentsRelatedToCourse(long courseId);

}
