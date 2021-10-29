package br.com.devdojo.service;

import br.com.devdojo.exeption.ResourceNotFoundexeception;
import br.com.devdojo.model.Course;
import br.com.devdojo.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * @project exam-generation
 * Created by Leandro Saniago on 17/02/2021 - 19:08.
 * classe responsável por acessar o repository e buscar o curso se não for encontrado lança exeption
 */
@Service
public class CourseService implements Serializable {

    private final CourseRepository courseRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public void throwResourceNotFoundIfCourseDoesNotExist(Course course){

        if (course == null || course.getId() == null || courseRepository.findOne(course.getId()) == null){

            throw new ResourceNotFoundexeception("Curso não encontrado");
        }

    }

    public void throwResourceNotFoundIfCourseDoesNotExist(long courseId){

        if (courseId == 0 || courseRepository.findOne(courseId) == null){

            throw new ResourceNotFoundexeception("Curso não encontrado");
        }

    }


}
