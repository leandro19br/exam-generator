package br.com.devdojo.service;

import br.com.devdojo.repository.AssignmentRepository;
import br.com.devdojo.repository.ChoiseRepository;
import br.com.devdojo.repository.CourseRepository;
import br.com.devdojo.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @project exam-generation
 * Created by Leandro Saniago on 18/03/2021 - 19:42.
 */
@Service
public class CascadeDeleteService {

    private final CourseRepository courseRepository;
    private final QuestionRepository questionRepository;
    private final ChoiseRepository choiseRepository;
    private final AssignmentRepository assignmentRepository;

    @Autowired
    public CascadeDeleteService(CourseRepository courseRepository, QuestionRepository questionRepository, ChoiseRepository choiseRepository, AssignmentRepository assignmentRepository) {
        this.courseRepository = courseRepository;
        this.questionRepository = questionRepository;
        this.choiseRepository = choiseRepository;
        this.assignmentRepository = assignmentRepository;
    }

    public void deleteCourseAndAllRelatedEntities(long courseId){
        courseRepository.delete(courseId);
        questionRepository.deleteAllQuestionRelatedToCourse(courseId);
        choiseRepository.deleteAllChoisesRelatedToCourse(courseId);
        assignmentRepository.deleteAllAssignmentsRelatedToCourse(courseId);

    }

    public void deleteQuestionAndAllRelatedEntities(long questionId){
        questionRepository.delete(questionId);
        choiseRepository.deleteAllChoisesRelatedToCourse(questionId);

    }




}
