package org.damon.database.enums;

/**
 * @Author Damon
 * @Date 2020/12/25 14:31
 */
public enum QueryType {
    EQUAL,
    NEQUAL,
    IN,
    LIKE,
    LEFT_LIKE,
    RIGHT_LIKE,
    GREATER_EQUAL,
    GREATER,
    LESS_EQUAL,
    LESS
    ;

    QueryType() {
    }


}
