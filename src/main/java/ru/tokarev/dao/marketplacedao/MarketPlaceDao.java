package ru.tokarev.dao.marketplacedao;

import java.util.List;
import java.util.Optional;

public interface MarketPlaceDao<T> {

    void setClazz(final Class<T> clazzToSet);

    Optional<T> findById(final long id);

    Optional<T> findByName(final String name);

    Optional<List<T>> findAll();

    Optional<T> create(final T entity);

    Optional<T> update(final T entity);

    void delete(final T entity);

    void deleteById(final long entityId);
}
