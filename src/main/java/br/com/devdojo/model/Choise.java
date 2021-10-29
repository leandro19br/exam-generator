package br.com.devdojo.model;


import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class Choise extends AbstractEntity {

    @NotEmpty(message = "Campo titulo não pode ser VAZIO")
    @ApiModelProperty(notes = "O titulo da Alternativa")
    private String titulo;
    @NotNull(message = "Campo correctAnswer não pode ser VAZIO")
    @ApiModelProperty(notes = "Alternativa correta para a questão, voce só pode ter uma resposta correta por questão")
    @Column(columnDefinition = "boolean default false", nullable = false)
    private boolean correctAnswer;
    @ManyToOne(optional = false)
    private Professor professor;
    @ManyToOne(optional = false)
    private Question question;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public boolean isCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(boolean correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }


    public static final class ChoiseBuilder {
        private Choise choise;

        private ChoiseBuilder() {
            choise = new Choise();
        }

        public static ChoiseBuilder builder() {
            return new ChoiseBuilder();
        }

        public ChoiseBuilder id(Long id) {
            choise.setId(id);
            return this;
        }

        public ChoiseBuilder titulo(String titulo) {
            choise.setTitulo(titulo);
            return this;
        }

        public ChoiseBuilder enabled(boolean enabled) {
            choise.setEnabled(enabled);
            return this;
        }

        public ChoiseBuilder correctAnswer(boolean correctAnswer) {
            choise.setCorrectAnswer(correctAnswer);
            return this;
        }

        public ChoiseBuilder professor(Professor professor) {
            choise.setProfessor(professor);
            return this;
        }

        public ChoiseBuilder question(Question question) {
            choise.setQuestion(question);
            return this;
        }

        public Choise build() {
            return choise;
        }
    }
}
