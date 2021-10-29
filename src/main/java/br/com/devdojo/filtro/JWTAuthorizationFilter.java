package br.com.devdojo.filtro;

import br.com.devdojo.model.ApplicationUser;
import br.com.devdojo.service.CustomUserDetailsService;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static br.com.devdojo.constants.Constants.*;

/**
 * @project exam-generation
 * Created by Leandro Saniago on 19/01/2021 - 16:15.
 * classe responsável pela autorização de usuário a utilizar os endpoints
 */
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private final CustomUserDetailsService customUserDetailsService;

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager
            , CustomUserDetailsService customUserDetailsService) {
        super(authenticationManager);
        this.customUserDetailsService = customUserDetailsService;
    }

    //validação para saber se tem acesso ao endpoint
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        //conteudo do HEADER
        String header = request.getHeader(HEADER_STRING);
        //verificação se o header é null ou não começa com TOKEN_PREFIX
        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }
        UsernamePasswordAuthenticationToken autheticationtoken = getAutheticationtoken(request);
        //para conseguirter a informação de authentication dentro do endpoint apos ser a autorizacao
        SecurityContextHolder.getContext().setAuthentication(autheticationtoken);
        chain.doFilter(request,response);
    }

    //metodo que pega o user name e faz uma consulta no banco
    private UsernamePasswordAuthenticationToken getAutheticationtoken(HttpServletRequest request) {

        String token = request.getHeader(HEADER_STRING);

        if (token == null) return null;
        //dentro do subject tem o username
        String userName = Jwts.parser().setSigningKey(SECRET)
                .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                .getBody()
                .getSubject();
        //pode ser feito das duas maneiras para pegar o objeto UserDetails ou ApplicationUser
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userName);
        ApplicationUser applicationUser = customUserDetailsService.loadApplicationUserByUserName(userName);
        return userDetails != null ?
                new UsernamePasswordAuthenticationToken(applicationUser, null, userDetails.getAuthorities()) : null;
    }

}
