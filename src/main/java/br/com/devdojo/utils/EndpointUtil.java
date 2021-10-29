package br.com.devdojo.utils;

import br.com.devdojo.exeption.ResourceNotFoundexeception;
import br.com.devdojo.model.ApplicationUser;
import br.com.devdojo.model.Professor;
import javafx.application.Application;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * @project exam-generation
 * Created by Leandro Saniago on 12/02/2021 - 18:02.
 * classe que tem um metodo para auxiliar na return dos Endpoints
 */

@Service
public class EndpointUtil implements Serializable {


    public ResponseEntity<?> returnObjectOrNotFound(Object object) {
        if (object == null) throw new ResourceNotFoundexeception("Não encontrado ");
        return new ResponseEntity<>(object, HttpStatus.OK);
    }

    public ResponseEntity<?> returnObjectOrNotFound(List<?> list) {

        if (list == null || list.isEmpty()) throw new ResourceNotFoundexeception("Não encontrado");
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    //metodo para extrair o profesor do Authentication
    public Professor extractProfessorForToken() {
        //dentro do SecurityContextHolder tem o método getAuthentication com o professor autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((ApplicationUser) authentication.getPrincipal()).getProfessor();
    }

}
