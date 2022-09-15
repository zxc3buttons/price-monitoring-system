package ru.tokarev.dao.productdao;

import org.springframework.stereotype.Repository;
import ru.tokarev.dao.AbstractJpaDao;
import ru.tokarev.entity.Category;
import ru.tokarev.entity.Product;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductDaoImpl extends AbstractJpaDao<Product> implements ProductDao<Product> {

    @PersistenceContext(unitName = "entityManagerFactory")
    private EntityManager entityManager;

    @Override
    public Optional<Product> findByName(String name) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
        Root<Product> root = criteriaQuery.from(Product.class);
        criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("name"), name));
        Product product = entityManager.createQuery(criteriaQuery)
                .getResultList().stream().findFirst().orElse(null);

        return Optional.ofNullable(product);
    }

    @Override
    public Optional<List<Product>> findAllByCategory(Category category) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> criteria = builder.createQuery(Product.class);
        Root<Product> root = criteria.from(Product.class);
        criteria.select(root).where(builder.equal(root.get("category"), category));
        List<Product> productList = entityManager.createQuery(criteria).getResultList();

        return Optional.ofNullable(productList);
    }
}
