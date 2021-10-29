package br.com.devdojo.model;


import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
public class Assignment extends AbstractEntity {

    @NotEmpty(message = "Campo titulo não pode ser VAZIO")
    @ApiModelProperty(notes = "O titulo da Alternativa")
    private String titulo;
    private LocalDateTime creatAt = LocalDateTime.now();
    @ManyToOne(optional = false)
    private Professor professor;
    @ManyToOne(optional = false)
    private Course course;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LocalDateTime getCreatAt() {
        return creatAt;
    }

    public void setCreatAt(LocalDateTime creatAt) {
        this.creatAt = creatAt;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public static final class AssignmentBuilder {
        private Assignment assignment;

        private AssignmentBuilder() {
            assignment = new Assignment();
        }

        public static AssignmentBuilder newBuilder() {
            return new AssignmentBuilder();
        }

        public AssignmentBuilder id(Long id) {
            assignment.setId(id);
            return this;
        }

        public AssignmentBuilder enabled(boolean enabled) {
            assignment.setEnabled(enabled);
            return this;
        }

        public AssignmentBuilder titulo(String titulo) {
            assignment.setTitulo(titulo);
            return this;
        }

        public AssignmentBuilder creatAt(LocalDateTime creatAt) {
            assignment.setCreatAt(creatAt);
            return this;
        }

        public AssignmentBuilder professor(Professor professor) {
            assignment.setProfessor(professor);
            return this;
        }

        public AssignmentBuilder course(Course course) {
            assignment.setCourse(course);
            return this;
        }

        public Assignment build() {
            return assignment;
        }
    }

    /*alt+shift+b*/

}
