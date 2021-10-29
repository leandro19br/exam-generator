package br.com.devdojo.model;


import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Professor extends AbstractEntity {

    @NotEmpty(message = "Campo nome não pode ser VAZIO")
    private String name;
    @Email(message = "E-MAIL não é VÁLIDO")
    @NotEmpty(message = "Campo EMAIL não pode ser VAZIO")
    @Column(unique = true)
    private String email;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public static final class Builder {
        private Professor professor;

        private Builder() {
            professor = new Professor();
        }

        public static Builder newProfessor() {
            return new Builder();
        }

        public Builder name(String name) {
            professor.setName(name);
            return this;
        }

        public Builder id(Long id) {
            professor.setId(id);
            return this;
        }

        public Builder email(String email) {
            professor.setEmail(email);
            return this;
        }

        public Professor build() {
            return professor;
        }
    }
}
