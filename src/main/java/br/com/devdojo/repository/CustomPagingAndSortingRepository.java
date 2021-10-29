package br.com.devdojo.repository;

import br.com.devdojo.model.AbstractEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @project exam-generation
 * Created by Leandro Saniago on 03/03/2021 - 08:51.
 * classe que será generico ao PagingAndSortingRepository com a alteração das querys para o enabled do Course
 * do tipo AbstractEntity por que todas as entidades extends dela
 */
@NoRepositoryBean
public interface CustomPagingAndSortingRepository<T extends AbstractEntity, ID extends Long>
        extends PagingAndSortingRepository<T,ID> {

    /**
     *#{#entityName} para pegar qual a entidade
     */

    @Override
    @Query("select e from #{#entityName} e where e.professor = ?#{principal.professor} and e.enabled = true")
    Iterable<T> findAll(Sort sort);

    @Override
    @Query("select e from #{#entityName} e where e.professor = ?#{principal.professor} and e.enabled = true")
    Page<T> findAll(Pageable pageable);

    @Override
    @Query("select e from #{#entityName} e where e.id=?1 and e.professor = ?#{principal.professor} and e.enabled = true")
    T findOne(Long id);

    //utilizando default do java 8 para reutilizar o mesmo método
    @Override
    default boolean exists(Long id){
        return findOne(id) != null;
    }

    @Override
    @Query("select e from #{#entityName} e where e.professor = ?#{principal.professor} and e.enabled = true")
    Iterable<T> findAll();

    @Override
    @Query("select e from #{#entityName} e where e.professor = ?#{principal.professor} and e.enabled = true")
    Iterable<T> findAll(Iterable<ID> iterable);

    @Override
    @Query("select count(e) from #{#entityName} e where e.professor = ?#{principal.professor} and e.enabled = true")
    long count();

    @Override
    @Transactional
    @Modifying
    @Query("update #{#entityName} e  set e.enabled = false where e.id =?1 and e.professor = ?#{principal.professor}")
    void delete(Long id);


    @Override
    @Transactional
    @Modifying
    default void delete(T t){
        delete(t.getId());
    }

    @Override
    @Transactional
    @Modifying
    default void delete(Iterable<? extends T> iterable){
        iterable.forEach(entity -> delete(entity.getId()));//interando a lista e deletando
    }

    @Override
    @Transactional
    @Modifying
    @Query("update #{#entityName} e set e.enabled = false where e.professor = ?#{principal.professor}")
    void deleteAll();
}
