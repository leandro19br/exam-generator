package br.com.devdojo.repository;

import br.com.devdojo.model.Course;
import br.com.devdojo.model.Professor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @project exam-generation
 * Created by Leandro Saniago on 04/02/2021 - 19:22.
 * quando extends PagingAndSortingRepository já herda o CRUD básico
 */
public interface CourseRepository extends CustomPagingAndSortingRepository<Course, Long> {
    //CRUD para buscar os cursos somente do professor
    Course findByIdAndProfessor(long id, Professor professor);

    @Query(value = "select c from Course c where c.name like %?1% and c.professor = ?#{principal.professor} and c.enabled = true")
    List<Course> lisCoursesByName(String name);

    /*@Query(value = "delete from Course c where c.id =?1 and c.professor = ?#{principal.professor}")
    @Modifying//anotação para definir que esta sendo modificado o estado do bd
    void delete(Long id);

    @Query(value = "delete from Course c where c = ?1 and c.professor = ?#{principal.professor}")
    @Modifying
    void delete(Course course);*/

}
