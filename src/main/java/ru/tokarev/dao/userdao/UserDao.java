package ru.tokarev.dao.userdao;

import java.util.List;
import java.util.Optional;

public interface UserDao<T> {

    void setClazz(final Class<T> clazzToSet);

    Optional<T> findById(final long id);

    Optional<T> findByUsername(final String username);

    Optional<T> findByEmail(final String email);

    Optional<List<T>> findAll();

    Optional<T> create(final T entity);

    Optional<T> update(final T entity);

    void delete(final T entity);

    void deleteById(final long entityId);
}
