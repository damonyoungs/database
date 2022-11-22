package org.damon.database.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.*;
import org.damon.database.annotation.QueryFileds;
import org.damon.database.enums.QueryType;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

/**
 * @Author Damon
 * @Date 2021/3/8 19:15
 */
public class QueryDslUtil<T> extends EntityPathBase<T> {

    private static final long serialVersionUID = -8722704786177963245L;

    public QueryDslUtil(Class<? extends T> type, String variable) {
        super(type, variable);
    }

    public QueryDslUtil(Class<? extends T> type, PathMetadata metadata) {
        super(type, metadata);
    }

    public QueryDslUtil(Class<? extends T> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
    }

    public BooleanBuilder where(Object t) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        Method[] methods = this.getAllMethods(t);
        Arrays.stream(methods).forEach(method -> {
            if (method.getName().contains("get") && !method.getName().contains("getClass")) {
                try {
                    Object obj = method.invoke(t);
                    if (null != obj) {
                        if(obj instanceof String){
                            if(StrUtil.isEmpty(String.valueOf(obj))){
                                return;
                            }
                        }
                        String fieldName = getBeanPropertityName(method.getName());
                        QueryFileds annotation = this.getField(t, fieldName).getAnnotation(QueryFileds.class);
                        getPredicate(annotation,obj,fieldName,booleanBuilder);
                    }
                } catch (IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        });
        return booleanBuilder;
    }

    private void getPredicate(QueryFileds annotation, Object obj, String fieldName, BooleanBuilder booleanBuilder) {
        if (null != annotation) {
            if(StrUtil.isNotEmpty(annotation.field())){
                fieldName = annotation.field();
            }
            QueryType type = annotation.type();
            switch (obj.getClass().getName()) {
                case "java.math.BigDecimal":
                    getPredicate((BigDecimal) obj, fieldName, booleanBuilder, type);
                    break;
                case "java.lang.Integer":
                    getPredicate((Integer) obj, fieldName, booleanBuilder, type);
                    break;
                case "java.lang.Long":
                    getPredicate((Long) obj, fieldName, booleanBuilder, type);
                    break;
                case "java.lang.Double":
                    getPredicate((Double) obj, fieldName, booleanBuilder, type);
                    break;
                case "java.util.Date":
                    getPredicate((Date) obj, fieldName, booleanBuilder, type);
                    break;
                case "java.lang.Boolean":
                    getPredicate((Boolean) obj, fieldName, booleanBuilder, type);
                    break;
                case "java.util.ArrayList":
                    getPredicate((List<Object>) obj, fieldName, booleanBuilder, type);
                    break;
                default:
                    getPredicate((String) obj, fieldName, booleanBuilder, type);
            }
        }
    }

    private void getPredicate(BigDecimal obj, String fieldName, BooleanBuilder booleanBuilder, QueryType type) {
        NumberPath<BigDecimal> number = createNumber(fieldName, BigDecimal.class);
        getPredicate(number,obj,booleanBuilder,type);
    }

    private void getPredicate(Integer obj, String fieldName, BooleanBuilder booleanBuilder, QueryType type) {
        NumberPath<Integer> number = createNumber(fieldName, Integer.class);
        getPredicate(number,obj,booleanBuilder,type);
    }

    private void getPredicate(NumberPath number,Number obj, BooleanBuilder booleanBuilder, QueryType type){
        switch (type) {
            case LIKE:
                booleanBuilder.and(number.like("%" + obj + "%"));
                break;
            case RIGHT_LIKE:
                booleanBuilder.and(number.like(obj + "%"));
                break;
            case LEFT_LIKE:
                booleanBuilder.and(number.like("%" + obj));
                break;
            case GREATER_EQUAL:
                booleanBuilder.and(number.goe(obj));
                break;
            case GREATER:
                booleanBuilder.and(number.gt(obj));
                break;
            case LESS_EQUAL:
                booleanBuilder.and(number.loe(obj));
                break;
            case LESS:
                booleanBuilder.and(number.lt(obj));
                break;
            case NEQUAL:
                booleanBuilder.and(number.ne(obj));
                break;
            default:
                booleanBuilder.and(number.eq(obj));
        }
    }

    private void getPredicate(Long obj, String fieldName, BooleanBuilder booleanBuilder, QueryType type) {
        NumberPath<Long> number = createNumber(fieldName, Long.class);
        getPredicate(number,obj,booleanBuilder,type);
    }

    private void getPredicate(Double obj, String fieldName, BooleanBuilder booleanBuilder, QueryType type) {
        NumberPath<Double> number = createNumber(fieldName, Double.class);
        getPredicate(number,obj,booleanBuilder,type);
    }

    private void getPredicate(Date obj, String fieldName, BooleanBuilder booleanBuilder, QueryType type) {
        DateTimePath<Date> dateTime = createDateTime(fieldName, Date.class);
        switch (type) {
            case GREATER_EQUAL:
                booleanBuilder.and(dateTime.goe(obj));
                break;
            case GREATER:
                booleanBuilder.and(dateTime.gt(obj));
                break;
            case LESS_EQUAL:
                booleanBuilder.and(dateTime.loe(obj));
                break;
            case LESS:
                booleanBuilder.and(dateTime.lt(obj));
                break;
            case NEQUAL:
                booleanBuilder.and(dateTime.ne(obj));
                break;
            default:
                booleanBuilder.and(dateTime.eq(obj));
        }
    }

    private void getPredicate(List<Object> obj, String fieldName, BooleanBuilder booleanBuilder, QueryType type) {
        if(CollUtil.isNotEmpty(obj)){
            Object o = obj.get(0);
            if(type == QueryType.IN){
                switch (o.getClass().getName()) {
                    case "java.lang.Integer":
                        booleanBuilder.and(createNumber(fieldName, Integer.class).in(JSONUtil.toList(JSONUtil.parseArray(obj),Integer.class)));
                        break;
                    case "java.lang.Long":
                        booleanBuilder.and(createNumber(fieldName, Long.class).in(JSONUtil.toList(JSONUtil.parseArray(obj),Long.class)));
                        break;
                    case "java.lang.Double":
                        booleanBuilder.and(createNumber(fieldName, Double.class).in(JSONUtil.toList(JSONUtil.parseArray(obj),Double.class)));
                        break;
                    case "java.util.Date":
                        booleanBuilder.and(createDateTime(fieldName, Date.class).in(JSONUtil.toList(JSONUtil.parseArray(obj),Date.class)));
                        break;
                    default:
                        booleanBuilder.and(createString(fieldName).in(JSONUtil.toList(JSONUtil.parseArray(obj),String.class)));
                }
            }
        }
    }

    private void getPredicate(Boolean obj, String fieldName, BooleanBuilder booleanBuilder, QueryType type) {
        BooleanPath aBoolean = createBoolean(fieldName);
        if (type == QueryType.NEQUAL) {
            booleanBuilder.and(aBoolean.ne(obj));
        } else {
            booleanBuilder.and(aBoolean.eq(obj));
        }
    }

    private void getPredicate(String obj, String fieldName, BooleanBuilder booleanBuilder, QueryType type) {
        StringPath string = createString(fieldName);
        switch (type) {
            case LIKE:
                booleanBuilder.and(string.like("%" + obj + "%"));
                break;
            case RIGHT_LIKE:
                booleanBuilder.and(string.like(obj + "%"));
                break;
            case LEFT_LIKE:
                booleanBuilder.and(string.like("%" + obj));
                break;
            case GREATER_EQUAL:
                booleanBuilder.and(string.goe(obj));
                break;
            case GREATER:
                booleanBuilder.and(string.gt(obj));
                break;
            case LESS_EQUAL:
                booleanBuilder.and(string.loe(obj));
                break;
            case LESS:
                booleanBuilder.and(string.lt(obj));
                break;
            case NEQUAL:
                booleanBuilder.and(string.ne(obj));
                break;
            default:
                booleanBuilder.and(string.eq(obj));
        }
    }

    private Field getField(Object t, String fieldName) throws NoSuchFieldException {
        return this.checkParent(t, fieldName) ? t.getClass().getSuperclass().getDeclaredField(fieldName) : t.getClass().getDeclaredField(fieldName);
    }

    private boolean checkParent(Object t, String fieldName) {
        try {
            t.getClass().getDeclaredField(fieldName);
            return false;
        } catch (NoSuchFieldException var4) {
            return true;
        }
    }

    private Method[] getAllMethods(Object t) {
        return t.getClass().getMethods();
    }

    private static String getBeanPropertityName(String s) {
        s = s.substring(3);
        return Character.isLowerCase(s.charAt(0)) ? s : Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }
}
