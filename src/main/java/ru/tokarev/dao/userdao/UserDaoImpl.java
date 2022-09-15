package ru.tokarev.dao.userdao;

import org.springframework.stereotype.Repository;
import ru.tokarev.dao.AbstractJpaDao;
import ru.tokarev.entity.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Optional;


@Repository
public class UserDaoImpl extends AbstractJpaDao<User> implements UserDao<User> {

    @PersistenceContext(unitName = "entityManagerFactory")
    private EntityManager entityManager;

    @Override
    public Optional<User> findByUsername(String username) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        Root<User> root = criteriaQuery.from(User.class);
        criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("username"), username));
        User user = entityManager.createQuery(criteriaQuery)
                .getResultList().stream().findFirst().orElse(null);

        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        Root<User> root = criteriaQuery.from(User.class);
        criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("email"), email));
        User user = entityManager.createQuery(criteriaQuery)
                .getResultList().stream().findFirst().orElse(null);

        return Optional.ofNullable(user);
    }
}
