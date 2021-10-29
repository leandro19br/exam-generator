package br.com.devdojo.service;

import br.com.devdojo.exeption.ResourceNotFoundexeception;
import br.com.devdojo.model.AbstractEntity;
import br.com.devdojo.model.Course;
import br.com.devdojo.repository.CustomPagingAndSortingRepository;
import org.springframework.stereotype.Service;

/**
 * @project exam-generation
 * Created by Leandro Saniago on 03/03/2021 - 21:23.
 * classe generica para ser utiliada como Service passsando o repositório e a mesg
 */
@Service
public class GenericService {


    public <T extends AbstractEntity, ID extends Long> void throwResourceNotFoundIfNotExist(T t,
                                                                                            CustomPagingAndSortingRepository<T,ID> repository,
                                                                                            String msg){

        if (t == null || t.getId() == null || repository.findOne(t.getId()) == null){

            throw new ResourceNotFoundexeception(msg);
        }

    }

    public <T extends AbstractEntity, ID extends Long> void throwResourceNotFoundIfNotExist(long id, CustomPagingAndSortingRepository<T,ID> repository,
                                                                                            String msg ){

        if (id == 0 || repository.findOne(id) == null){

            throw new ResourceNotFoundexeception(msg);
        }

    }


}
