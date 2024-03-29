package top.damon.database.annotation;

import top.damon.database.enums.QueryType;

import java.lang.annotation.*;

/**
 * @Author Damon
 * @Date 2020/12/25 14:27
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryFileds {

    String field() default "";

    QueryType type() default QueryType.EQUAL;
}
