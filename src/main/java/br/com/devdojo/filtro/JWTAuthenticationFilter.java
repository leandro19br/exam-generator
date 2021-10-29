package br.com.devdojo.filtro;

import br.com.devdojo.model.ApplicationUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static br.com.devdojo.constants.Constants.*;

/**
 * @project exam-generation
 * Created by Leandro Saniago on 19/01/2021 - 16:16.
 * classe responsavel pela autenticação de usuário quem é voce?
 */
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    @Autowired
    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    //metodo responsável pela autenticação

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            //pegar a requizicao e descerializar o Json com o usuario e senha
            ApplicationUser user = new ObjectMapper().readValue(request.getInputStream(), ApplicationUser.class);
            //fazendo autenticação de usuario e senha
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassWord()));

        } catch (IOException e) {
            throw new RuntimeException();//lança exception caso não encontre
        }
    }

    //se for feita a autenticacao sera gerado um token que deve ser retornado no body ou no header
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException, ServletException {
        //gerando a data de expiration
        ZonedDateTime expTimeUTC = ZonedDateTime.now(ZoneOffset.UTC).plus(EXPIRATION_TIME, ChronoUnit.MILLIS);
        //construindo o token Jwts dependencia que cria o token
        String token = Jwts.builder()
                .setSubject(((ApplicationUser) authResult.getPrincipal()).getUserName())
                .setExpiration(Date.from(expTimeUTC.toInstant()))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
        //devolvendo o token no json que será enviado no response {"token": "Bearer", "exp":"day"}
        token = TOKEN_PREFIX + token;
        String tokenJson = "{\"token\":" + addQuotes(token) + ",\"exp\":" + addQuotes(expTimeUTC.toString()) +"}";
        response.getWriter().write(tokenJson);
        response.addHeader("Content-Type","application/json;charset=UTF-8");
        response.addHeader(HEADER_STRING, token);
    }

    private String addQuotes(String value) {
        return new StringBuilder(300).append("\"").append(value).append("\"").toString();
    }
}
