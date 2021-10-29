package br.com.devdojo.model;


import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Question extends AbstractEntity {

    @NotEmpty(message = "Campo titulo não pode ser VAZIO")
    @ApiModelProperty(notes = "O titulo da questao")
    private String titulo;
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


    public static final class Builder {
        private Question question;

        private Builder() {
            question = new Question();
        }

        public static Builder newQuestion() {
            return new Builder();
        }

        public Builder id(Long id) {
            question.setId(id);
            return this;
        }

        public Builder titulo(String titulo) {
            question.setTitulo(titulo);
            return this;
        }

        public Builder professor(Professor professor) {
            question.setProfessor(professor);
            return this;
        }

        public Builder enabled(boolean enabled) {
            question.setEnabled(enabled);
            return this;
        }

        public Builder course(Course course) {
            question.setCourse(course);
            return this;
        }

        public Question build() {
            return question;
        }
    }
}
