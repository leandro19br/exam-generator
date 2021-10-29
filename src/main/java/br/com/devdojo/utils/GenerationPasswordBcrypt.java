package br.com.devdojo.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @project exam-generation
 * Created by Leandro Saniago on 20/01/2021 - 10:49.
 */
public class GenerationPasswordBcrypt {

    public static void main(String[] args) {

        System.out.println(" *** Senha Criptografada " + new BCryptPasswordEncoder().encode("santiago"));

    }

}
