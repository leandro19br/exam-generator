package br.com.devdojo.service;

import br.com.devdojo.model.ApplicationUser;
import br.com.devdojo.repository.ApplicationUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @project exam-generation
 * Created by Leandro Saniago on 19/01/2021 - 18:11.
 * classe responsável pelo acesso ao usuário e senha no BD
 */

@Service//para que seja um Bean usa se essa anotação do Spring
public class CustomUserDetailsService implements UserDetailsService {

    private final ApplicationUserRepository applicationUserRepository;

    @Autowired
    public CustomUserDetailsService(ApplicationUserRepository applicationUserRepository) {
        this.applicationUserRepository = applicationUserRepository;
    }

    //sera criada uma classe para CustonUserDatail
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        ApplicationUser applicationUser = loadApplicationUserByUserName(userName);
        return new CustomUserDatails(applicationUser) ;
    }

    //busca o usuário no banco
    public ApplicationUser loadApplicationUserByUserName(String userName) {
        return Optional.ofNullable(applicationUserRepository.findByUserName(userName))
                .orElseThrow(() -> new UsernameNotFoundException("UserNAme nao encontrado"));
    }

    private final static class CustomUserDatails extends ApplicationUser implements UserDetails {

        private CustomUserDatails(ApplicationUser applicationUser){
            super(applicationUser);
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            List<GrantedAuthority> roleProessor = AuthorityUtils.createAuthorityList("ROLE_PROFESSOR");
            List<GrantedAuthority> roleStudent = AuthorityUtils.createAuthorityList("ROLE_STUDENT");

            return this.getProfessor() != null ? roleProessor : roleStudent;
        }

        @Override
        public String getPassword() {
            return this.getPassWord();
        }

        @Override
        public String getUsername() {
            return this.getUserName();
        }


        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }

}
