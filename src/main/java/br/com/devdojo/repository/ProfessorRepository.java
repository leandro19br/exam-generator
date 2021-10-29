package br.com.devdojo.repository;

import br.com.devdojo.model.Professor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @project exam-generation
 * Created by Leandro Saniago on 04/02/2021 - 19:22.
 */
public interface ProfessorRepository extends PagingAndSortingRepository<Professor, Long> {

    Professor findByEmail(String email);

}
