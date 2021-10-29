package br.com.devdojo.utils;

import java.util.concurrent.TimeUnit;

/**
 * @project exam-generation
 * Created by Leandro Saniago on 20/01/2021 - 10:51.
 */
public class GenerationExpriration {

    public static void main(String[] args) {
        System.out.println(" Expiration em MS " + TimeUnit.MILLISECONDS.convert(1,TimeUnit.DAYS));
    }
}
