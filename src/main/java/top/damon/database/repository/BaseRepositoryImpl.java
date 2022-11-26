package top.damon.database.repository;

import lombok.extern.slf4j.Slf4j;
import top.damon.database.entity.BaseDO;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * @Author Damon
 * @Date 2020/12/7 11:17
 */
@Slf4j
public class BaseRepositoryImpl<T extends BaseDO, ID extends Serializable>
        extends SimpleJpaRepository<T, ID>
        implements BaseRepository<T, ID> {

    private final EntityManager entityManager;

    public BaseRepositoryImpl(JpaEntityInformation<T, ?> ei, EntityManager em) {
        super(ei, em);
        this.entityManager = em;
    }

    public BaseRepositoryImpl(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
        this.entityManager = em;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

}
