package ru.tokarev.dao.productonmarket;

import org.springframework.stereotype.Repository;
import ru.tokarev.dao.AbstractJpaDao;
import ru.tokarev.entity.Marketplace;
import ru.tokarev.entity.Product;
import ru.tokarev.entity.item.Item;
import ru.tokarev.entity.item.ItemCompositeId;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class ItemDaoImpl extends AbstractJpaDao<Item>
        implements ItemDao<Item> {

    @PersistenceContext(unitName = "entityManagerFactory")
    private EntityManager entityManager;

    @Override
    public Optional<Item> findById(ItemCompositeId id) {
        Item selectedItem = entityManager.find(Item.class, id);
        return Optional.ofNullable(selectedItem);
    }

    @Override
    public Optional<Item> findBySerialNumber(Long serialNumber) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Item> criteriaQuery = criteriaBuilder.createQuery(Item.class);
        Root<Item> root = criteriaQuery.from(Item.class);
        criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("serialNumber"), serialNumber));
        Item item = entityManager.createQuery(criteriaQuery)
                .getResultList().stream().findFirst().orElse(null);

        return Optional.ofNullable(item);
    }

    @Override
    public Optional<List<Item>> findAllByProductAndMarketplace(Product product, Marketplace marketplace) {
        List<Item> itemList = entityManager
                .createQuery("from Item where product=?1 and marketplace=?2 order by dateStart asc")
                .setParameter(1, product).setParameter(2, marketplace).getResultList();

        return Optional.ofNullable(itemList);
    }

    @Override
    public Optional<List<Item>> findAllByProduct(Product product) {
        List<Item> itemList = entityManager
                .createQuery("from Item where product=?1 order by dateStart asc")
                .setParameter(1, product).getResultList();

        return Optional.ofNullable(itemList);
    }

    @Override
    public Optional<List<Item>> findProductsOnMarketByDateAndProduct(
            Product product, LocalDate dateStart, LocalDate dateEnd) {
        List<Item> itemList = entityManager
                .createQuery("from Item where product=?1 and dateStart>=?2 and" +
                        " dateEnd<=?3 order by dateStart asc")
                .setParameter(1, product)
                .setParameter(2, dateStart)
                .setParameter(3, dateEnd).getResultList();

        return Optional.ofNullable(itemList);
    }

    @Override
    public Optional<List<Item>> findProductsByDateAndProductAndMarketplace(
            Product product, LocalDate dateStart, LocalDate dateEnd, Marketplace marketplace) {
        List<Item> itemList = entityManager
                .createQuery("from Item where product=?1 and marketplace=?2 and dateStart>=?3 and" +
                        " dateEnd>=?4 order by dateStart asc")
                .setParameter(1, product)
                .setParameter(2, marketplace)
                .setParameter(3, dateStart)
                .setParameter(4, dateEnd).getResultList();

        return Optional.ofNullable(itemList);
    }

    @Override
    public void deleteBySerialNumber(Long serialNumber) {
        final Optional<Item> entity = findBySerialNumber(serialNumber);
        delete(entity.get());
    }
}
