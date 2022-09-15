package ru.tokarev.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public class AbstractJpaDao<T extends Serializable> {

    private Class<T> clazz;

    @PersistenceContext(unitName = "entityManagerFactory")
    private EntityManager entityManager;

    public final void setClazz(final Class<T> clazzToSet) {
        this.clazz = clazzToSet;
    }

    public Optional<T> findById(final long id) {
        return Optional.ofNullable(entityManager.find(clazz, id));
    }

    public Optional<List<T>> findAll() {
        return Optional.ofNullable(entityManager.createQuery("from " + clazz.getName()).getResultList());
    }

    public Optional<T> create(final T entity) {
        entityManager.persist(entity);
        return Optional.of(entity);
    }

    public Optional<T> update(final T entity) {
        return Optional.ofNullable(entityManager.merge(entity));
    }

    public void delete(final T entity) {
        entityManager.remove(entity);
    }

    public void deleteById(final long entityId) {
        final Optional<T> entity = findById(entityId);
        delete(entity.get());
    }
}
