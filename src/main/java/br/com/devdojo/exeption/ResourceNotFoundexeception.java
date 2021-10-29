package br.com.devdojo.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @project exam-generation
 * Created by Leandro Saniago on 17/02/2021 - 19:05.
 * classe que vai retorar HttpStatus.NOT_FOUND caso não sja encontrado
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundexeception extends RuntimeException {

    public ResourceNotFoundexeception(String message) {
        super(message);
    }
}
