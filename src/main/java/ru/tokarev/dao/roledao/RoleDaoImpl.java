package ru.tokarev.dao.roledao;

import org.springframework.stereotype.Repository;
import ru.tokarev.dao.AbstractJpaDao;
import ru.tokarev.entity.Role;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Optional;

@Repository
public class RoleDaoImpl extends AbstractJpaDao<Role> implements RoleDao<Role> {

    @PersistenceContext(unitName = "entityManagerFactory")
    private EntityManager entityManager;

    @Override
    public Optional<Role> findByName(String name) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Role> criteriaQuery = criteriaBuilder.createQuery(Role.class);
        Root<Role> root = criteriaQuery.from(Role.class);
        criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("name"), name));
        Role role = entityManager.createQuery(criteriaQuery)
                .getResultList().stream().findFirst().orElse(null);

        return Optional.ofNullable(role);
    }
}
