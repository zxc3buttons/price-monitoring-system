package ru.tokarev.dao.productonmarket;

import ru.tokarev.entity.Marketplace;
import ru.tokarev.entity.Product;
import ru.tokarev.entity.item.Item;
import ru.tokarev.entity.item.ItemCompositeId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ItemDao<T> {

    void setClazz(final Class<T> clazzToSet);

    Optional<T> findById(ItemCompositeId id);

    Optional<T> findBySerialNumber(Long serialNumber);

    Optional<List<T>> findAll();

    Optional<List<Item>> findAllByProduct(Product product);

    Optional<List<T>> findAllByProductAndMarketplace(Product product, Marketplace marketplace);

    Optional<T> create(final T entity);

    Optional<T> update(final T entity);

    void delete(final T entity);

    void deleteById(final long entityId);

    void deleteBySerialNumber(Long serialNumber);

    Optional<List<T>> findProductsByDateAndProductAndMarketplace(Product product, LocalDate dateStart,
                                                                 LocalDate dateEnd, Marketplace marketplace);

    Optional<List<T>> findProductsOnMarketByDateAndProduct(Product product, LocalDate dateStart, LocalDate dateEnd);
}
