package org.damon.database.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

/**
 * @Author chengrong.yang
 * @Date 2021/3/4 17:46
 */
@Configuration
public class QueryDSLConfig {

    @Autowired
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory getQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }

}