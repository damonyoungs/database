package org.damon.database.annotation;

import org.damon.database.enums.QueryType;

import java.lang.annotation.*;

/**
 * @Author chengrong.yang
 * @Date 2020/12/25 14:27
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryFileds {

    String field() default "";

    QueryType type() default QueryType.EQUAL;
}
