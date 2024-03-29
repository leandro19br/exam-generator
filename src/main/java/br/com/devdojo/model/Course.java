package br.com.devdojo.model;


import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Course extends AbstractEntity {

    @NotEmpty(message = "Campo nome do curso não pode ser VAZIO")
    @ApiModelProperty(notes = "O nome do curso")
    private String name;
    @ManyToOne(optional = false)//varios curso para 1 professor optional = false pois é mandatorio ter o professor
    private Professor professor;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public static final class Builder {
        private Course course;

        private Builder() {
            course = new Course();
        }

        public static Builder newCourse() {
            return new Builder();
        }

        public Builder name(String name) {
            course.setName(name);
            return this;
        }

        public Builder id(Long id) {
            course.setId(id);
            return this;
        }

        public Builder professor(Professor professor) {
            course.setProfessor(professor);
            return this;
        }

        public Course build() {
            return course;
        }
    }
}
