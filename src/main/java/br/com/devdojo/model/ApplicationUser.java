package br.com.devdojo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;


@Entity
public class ApplicationUser extends AbstractEntity {

    @NotEmpty(message ="Campo USERNAME não pode ser NULO")
    @Column(unique = true)
    private String userName;
    @NotEmpty(message ="Campo PASSWORD não pode ser NULO")
    private String passWord;
    @OneToOne
    @JsonIgnore
    private Professor professor;

    public ApplicationUser() {
    }

    public ApplicationUser(ApplicationUser applicationUser) {
        this.userName = applicationUser.userName;
        this.passWord = applicationUser.passWord;
        this.professor = applicationUser.professor;

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }
}
