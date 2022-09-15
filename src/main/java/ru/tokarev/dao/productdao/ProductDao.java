package ru.tokarev.dao.productdao;

import ru.tokarev.entity.Category;

import java.util.List;
import java.util.Optional;

public interface ProductDao<T> {

    void setClazz(final Class<T> clazzToSet);

    Optional<T> findById(final long id);

    Optional<T> findByName(final String name);

    Optional<List<T>> findAll();

    Optional<List<T>> findAllByCategory(Category category);

    Optional<T> create(final T entity);

    Optional<T> update(final T entity);

    void delete(final T entity);

    void deleteById(final long entityId);
}
