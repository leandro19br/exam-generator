package br.com.devdojo.repository;

import br.com.devdojo.model.ApplicationUser;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @project exam-generation
 * Created by Leandro Saniago on 19/01/2021 - 18:24.
 */
public interface ApplicationUserRepository extends PagingAndSortingRepository<ApplicationUser, Long> {

    ApplicationUser findByUserName(String userName);

}
