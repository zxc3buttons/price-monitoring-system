package ru.tokarev.dao.marketplacedao;

import org.springframework.stereotype.Repository;
import ru.tokarev.dao.AbstractJpaDao;
import ru.tokarev.entity.Marketplace;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Optional;

@Repository
public class MarketPlaceDaoImpl extends AbstractJpaDao<Marketplace> implements MarketPlaceDao<Marketplace> {

    @PersistenceContext(unitName = "entityManagerFactory")
    private EntityManager entityManager;

    @Override
    public Optional<Marketplace> findByName(String name) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Marketplace> criteriaQuery = criteriaBuilder.createQuery(Marketplace.class);
        Root<Marketplace> root = criteriaQuery.from(Marketplace.class);
        criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("name"), name));
        Marketplace marketPlace = entityManager.createQuery(criteriaQuery)
                .getResultList().stream().findFirst().orElse(null);

        return Optional.ofNullable(marketPlace);
    }
}
