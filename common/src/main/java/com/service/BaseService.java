package com.service;

import com.entry.BaseEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
@Slf4j
@NoRepositoryBean
public abstract class BaseService<T extends BaseEntry, ID extends Serializable>

//        implements MongoRepository<T, ID>

{
//
//    abstract MongoRepository<T, ID> getRepository();
//
//    @Override
//    public <S extends T> List<S> saveAll(Iterable<S> entites) {
//        return getRepository().saveAll(entites);
//    }
//
//    @Override
//    public List<T> findAll() {
//        return getRepository().findAll();
//    }
//
//    @Override
//    public List<T> findAll(Sort sort) {
//        return getRepository().findAll(sort);
//    }
//
//    @Override
//    public <S extends T> S insert(S entity) {
//        return getRepository().insert(entity);
//    }
//
//    @Override
//    public <S extends T> List<S> insert(Iterable<S> entities) {
//        return getRepository().insert(entities);
//    }
//
//    @Override
//    public <S extends T> List<S> findAll(Example<S> example) {
//        return getRepository().findAll(example);
//    }
//
//    @Override
//    public <S extends T> List<S> findAll(Example<S> example, Sort sort) {
//        return getRepository().findAll(example, sort);
//    }
//
//    @Override
//    public Page<T> findAll(Pageable pageable) {
//        return getRepository().findAll(pageable);
//    }
//
//    @Override
//    public <S extends T> S save(S entity) {
//        return getRepository().save(entity);
//    }
//
//    @Override
//    public Optional<T> findById(ID id) {
//        return getRepository().findById(id);
//    }
//
//    @Override
//    public boolean existsById(ID id) {
//        return getRepository().existsById(id);
//    }
//
//    @Override
//    public Iterable<T> findAllById(Iterable<ID> ids) {
//        return getRepository().findAllById(ids);
//    }
//
//    @Override
//    public long count() {
//        return getRepository().count();
//    }
//
//    @Override
//    public void deleteById(ID id) {
//        getRepository().deleteById(id);
//    }
//
//    @Override
//    public void delete(T entity) {
//        getRepository().delete(entity);
//    }
//
//    @Override
//    public void deleteAll(Iterable<? extends T> entities) {
//        getRepository().deleteAll(entities);
//    }
//
//    @Override
//    public void deleteAll() {
//        getRepository().deleteAll();
//    }
//
//    @Override
//    public <S extends T> Optional<S> findOne(Example<S> example) {
//        return getRepository().findOne(example);
//    }
//
//    @Override
//    public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {
//        return getRepository().findAll(example, pageable);
//    }
//
//    @Override
//    public <S extends T> long count(Example<S> example) {
//        return getRepository().count(example);
//    }
//
//    @Override
//    public <S extends T> boolean exists(Example<S> example) {
//        return getRepository().exists(example);
//    }
}