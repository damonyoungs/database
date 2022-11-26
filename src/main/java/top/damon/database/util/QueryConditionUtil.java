package top.damon.database.util;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import top.damon.database.annotation.QueryEntity;
import top.damon.database.annotation.QueryFileds;
import top.damon.database.enums.QueryType;

import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

/**
 * @Author Damon
 * @Date 2020/12/11 14:49
 */
@Slf4j
public class QueryConditionUtil<T> {

    public Predicate where(Object param, Root<T> root, CriteriaBuilder criteriaBuilder) {
        Method[] methods = getAllMethods(param);
        List<Predicate> list = new ArrayList<>();
        Arrays.stream(methods).forEach(method -> {
            if (method.getName().contains("get") && !method.getName().contains("getClass")) {
                try {
                    Object obj = method.invoke(param);
                    if (null != obj) {
                        if(obj instanceof String){
                            if(StrUtil.isEmpty(String.valueOf(obj))){
                                return;
                            }
                        }
                        Predicate predicate = getPredicate(method, obj, root, criteriaBuilder, param);
                        if (null != predicate) list.add(predicate);
                    }
                } catch (IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
                    log.error(e.getMessage());
                }
            }
        });
        Predicate[] p = new Predicate[list.size()];
        return criteriaBuilder.and(list.toArray(p));
    }

    private Predicate join(Object param, CriteriaBuilder criteriaBuilder, Join<Object, Object> join) {
        Method[] methods = getAllMethods(param);
        List<Predicate> list = new ArrayList<>();
        Arrays.stream(methods).forEach(method -> {
            if (method.getName().contains("get") && !method.getName().contains("getClass")) {
                try {
                    Object obj = method.invoke(param);
                    if (null != obj) {
                        Predicate predicate = getPredicate(method, obj, criteriaBuilder, param, join);
                        if (null != predicate) list.add(predicate);
                    }
                } catch (IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
                    log.error(e.getMessage());
                }
            }
        });
        Predicate[] p = new Predicate[list.size()];
        return criteriaBuilder.and(list.toArray(p));
    }

    private Method[] getAllMethods(Object param) {
        return param.getClass().getMethods();
    }


    private Predicate getPredicate(Method method, Object obj, Root<T> root, CriteriaBuilder criteriaBuilder, Object o) throws NoSuchFieldException {
        String fieldName = getBeanPropertityName(method.getName());
        QueryFileds annotation = getField(o, fieldName).getAnnotation(QueryFileds.class);
        if (null != annotation) {
            if (StrUtil.isNotEmpty(annotation.field())) {
                fieldName = annotation.field();
            }
            QueryType type = annotation.type();
            return getByType(obj, fieldName, criteriaBuilder, root, type);
        }
        return null;
    }

    private Predicate getPredicate(Method method, Object obj, CriteriaBuilder criteriaBuilder, Object o, Join<Object, Object> join) throws NoSuchFieldException {
        String fieldName = getBeanPropertityName(method.getName());
        QueryFileds annotation = getField(o, fieldName).getAnnotation(QueryFileds.class);
        if (null != annotation) {
            if (StrUtil.isNotEmpty(annotation.field())) {
                fieldName = annotation.field();
            }
            QueryType type = annotation.type();
            return getByType(obj, fieldName, criteriaBuilder, type, join);
        }
        return null;
    }

    private Field getField(Object t, String fieldName) throws NoSuchFieldException {
        if (checkParent(t, fieldName)) return t.getClass().getSuperclass().getDeclaredField(fieldName);
        return t.getClass().getDeclaredField(fieldName);
    }

    private boolean checkParent(Object t, String fieldName) {
        try {
            t.getClass().getDeclaredField(fieldName);
            return false;
        } catch (NoSuchFieldException e) {
            return true;
        }
    }

    private Predicate getByType(Object obj, String fieldName, CriteriaBuilder criteriaBuilder, Root<T> root, QueryType type) {
        switch (obj.getClass().getName()) {
            case "java.math.BigDecimal":
                return getPredicate((BigDecimal) obj, fieldName, criteriaBuilder, root, type);
            case "java.lang.Integer":
                return getPredicate((Integer) obj, fieldName, criteriaBuilder, root, type);
            case "java.lang.Long":
                return getPredicate((Long) obj, fieldName, criteriaBuilder, root, type);
            case "java.lang.Float":
                return getPredicate((Float) obj, fieldName, criteriaBuilder, root, type);
            case "java.lang.Double":
                return getPredicate((Double) obj, fieldName, criteriaBuilder, root, type);
            case "java.util.Date":
                return getPredicate((Date) obj, fieldName, criteriaBuilder, root, type);
            case "java.lang.Boolean":
                return getPredicate((Boolean) obj, fieldName, criteriaBuilder, root, type);
            case "java.util.ArrayList":
                return getPredicate(Collections.singletonList(obj), fieldName, criteriaBuilder, root);
            default:
                QueryEntity annotation = obj.getClass().getAnnotation(QueryEntity.class);
                if (null != annotation) return join(obj, criteriaBuilder, root.join(fieldName));
                return getPredicate((String) obj, fieldName, criteriaBuilder, root, type);
        }
    }

    private Predicate getByType(Object obj, String fieldName, CriteriaBuilder criteriaBuilder, QueryType type, Join<Object, Object> join) {
        switch (obj.getClass().getName()) {
            case "java.math.BigDecimal":
                return getPredicate((BigDecimal) obj, fieldName, criteriaBuilder, join, type);
            case "java.lang.Integer":
                return getPredicate((Integer) obj, fieldName, criteriaBuilder, join, type);
            case "java.lang.Long":
                return getPredicate((Long) obj, fieldName, criteriaBuilder, join, type);
            case "java.lang.Float":
                return getPredicate((Float) obj, fieldName, criteriaBuilder, join, type);
            case "java.lang.Double":
                return getPredicate((Double) obj, fieldName, criteriaBuilder, join, type);
            case "java.util.Date":
                return getPredicate((Date) obj, fieldName, criteriaBuilder, join, type);
            case "java.lang.Boolean":
                return getPredicate((Boolean) obj, fieldName, criteriaBuilder, join, type);
            case "java.util.ArrayList":
                return getPredicate(Collections.singletonList(obj), fieldName, criteriaBuilder, join);
            default:
                QueryEntity annotation = obj.getClass().getAnnotation(QueryEntity.class);
                if (null != annotation) return join(obj, criteriaBuilder, join.join(fieldName));
                return getPredicate((String) obj, fieldName, criteriaBuilder, join, type);
        }
    }

    private Predicate getPredicate(BigDecimal obj, String fieldName, CriteriaBuilder criteriaBuilder, Root<T> root, QueryType type) {
        return getPredicate(obj, criteriaBuilder, type, root.get(fieldName));
    }

    private Predicate getPredicate(BigDecimal obj, String fieldName, CriteriaBuilder criteriaBuilder, Join<Object, Object> join, QueryType type) {
        return getPredicate(obj, criteriaBuilder, type, join.get(fieldName));
    }

    private Predicate getPredicate(BigDecimal obj, CriteriaBuilder criteriaBuilder, QueryType type, Path<Object> objectPath) {
        switch (type) {
            case LIKE:
                return criteriaBuilder.like(objectPath.as(String.class), "%" + obj + "%");
            case RIGHT_LIKE:
                return criteriaBuilder.like(objectPath.as(String.class), obj + "%");
            case LEFT_LIKE:
                return criteriaBuilder.equal(objectPath.as(String.class), "%" + obj);
            case GREATER_EQUAL:
                return criteriaBuilder.greaterThanOrEqualTo(objectPath.as(BigDecimal.class), obj);
            case GREATER:
                return criteriaBuilder.greaterThan(objectPath.as(BigDecimal.class), obj);
            case LESS_EQUAL:
                return criteriaBuilder.lessThanOrEqualTo(objectPath.as(BigDecimal.class), obj);
            case LESS:
                return criteriaBuilder.lessThan(objectPath.as(BigDecimal.class), obj);
            case UNEQUAL:
                return criteriaBuilder.notEqual(objectPath.as(BigDecimal.class), obj);
            default:
                return criteriaBuilder.equal(objectPath.as(BigDecimal.class), obj);
        }
    }

    private Predicate getPredicate(Integer obj, String fieldName, CriteriaBuilder criteriaBuilder, Root<T> root, QueryType type) {
        return getPredicate(obj, criteriaBuilder, type, root.get(fieldName));
    }

    private Predicate getPredicate(Integer obj, String fieldName, CriteriaBuilder criteriaBuilder, Join<Object, Object> join, QueryType type) {
        return getPredicate(obj, criteriaBuilder, type, join.get(fieldName));
    }

    private Predicate getPredicate(Integer obj, CriteriaBuilder criteriaBuilder, QueryType type, Path<Object> objectPath) {
        switch (type) {
            case LIKE:
                return criteriaBuilder.like(objectPath.as(String.class), "%" + obj + "%");
            case RIGHT_LIKE:
                return criteriaBuilder.like(objectPath.as(String.class), obj + "%");
            case LEFT_LIKE:
                return criteriaBuilder.equal(objectPath.as(String.class), "%" + obj);
            case GREATER_EQUAL:
                return criteriaBuilder.greaterThanOrEqualTo(objectPath.as(Integer.class), obj);
            case GREATER:
                return criteriaBuilder.greaterThan(objectPath.as(Integer.class), obj);
            case LESS_EQUAL:
                return criteriaBuilder.lessThanOrEqualTo(objectPath.as(Integer.class), obj);
            case LESS:
                return criteriaBuilder.lessThan(objectPath.as(Integer.class), obj);
            case UNEQUAL:
                return criteriaBuilder.notEqual(objectPath.as(Integer.class), obj);
            default:
                return criteriaBuilder.equal(objectPath.as(Integer.class), obj);
        }
    }

    private Predicate getPredicate(Long obj, String fieldName, CriteriaBuilder criteriaBuilder, Root<T> root, QueryType type) {
        return getPredicate(obj, criteriaBuilder, type, root.get(fieldName));
    }

    private Predicate getPredicate(Long obj, String fieldName, CriteriaBuilder criteriaBuilder, Join<Object, Object> join, QueryType type) {
        return getPredicate(obj, criteriaBuilder, type, join.get(fieldName));
    }

    private Predicate getPredicate(Long obj, CriteriaBuilder criteriaBuilder, QueryType type, Path<Object> objectPath) {
        switch (type) {
            case LIKE:
                return criteriaBuilder.like(objectPath.as(String.class), "%" + obj + "%");
            case RIGHT_LIKE:
                return criteriaBuilder.like(objectPath.as(String.class), obj + "%");
            case LEFT_LIKE:
                return criteriaBuilder.equal(objectPath.as(String.class), "%" + obj);
            case GREATER_EQUAL:
                return criteriaBuilder.greaterThanOrEqualTo(objectPath.as(Long.class), obj);
            case GREATER:
                return criteriaBuilder.greaterThan(objectPath.as(Long.class), obj);
            case LESS_EQUAL:
                return criteriaBuilder.lessThanOrEqualTo(objectPath.as(Long.class), obj);
            case LESS:
                return criteriaBuilder.lessThan(objectPath.as(Long.class), obj);
            case UNEQUAL:
                return criteriaBuilder.notEqual(objectPath.as(Long.class), obj);
            default:
                return criteriaBuilder.equal(objectPath.as(Long.class), obj);
        }
    }

    private Predicate getPredicate(Float obj, String fieldName, CriteriaBuilder criteriaBuilder, Root<T> root, QueryType type) {
        return getPredicate(obj, criteriaBuilder, type, root.get(fieldName));
    }

    private Predicate getPredicate(Float obj, String fieldName, CriteriaBuilder criteriaBuilder, Join<Object, Object> join, QueryType type) {
        return getPredicate(obj, criteriaBuilder, type, join.get(fieldName));
    }

    private Predicate getPredicate(Float obj, CriteriaBuilder criteriaBuilder, QueryType type, Path<Object> objectPath) {
        switch (type) {
            case LIKE:
                return criteriaBuilder.like(objectPath.as(String.class), "%" + obj + "%");
            case RIGHT_LIKE:
                return criteriaBuilder.like(objectPath.as(String.class), obj + "%");
            case LEFT_LIKE:
                return criteriaBuilder.equal(objectPath.as(String.class), "%" + obj);
            case GREATER_EQUAL:
                return criteriaBuilder.greaterThanOrEqualTo(objectPath.as(Float.class), obj);
            case GREATER:
                return criteriaBuilder.greaterThan(objectPath.as(Float.class), obj);
            case LESS_EQUAL:
                return criteriaBuilder.lessThanOrEqualTo(objectPath.as(Float.class), obj);
            case LESS:
                return criteriaBuilder.lessThan(objectPath.as(Float.class), obj);
            case UNEQUAL:
                return criteriaBuilder.notEqual(objectPath.as(Float.class), obj);
            default:
                return criteriaBuilder.equal(objectPath.as(Float.class), obj);
        }
    }

    private Predicate getPredicate(Double obj, String fieldName, CriteriaBuilder criteriaBuilder, Root<T> root, QueryType type) {
        return getPredicate(obj, criteriaBuilder, type, root.get(fieldName));
    }

    private Predicate getPredicate(Double obj, String fieldName, CriteriaBuilder criteriaBuilder, Join<Object, Object> join, QueryType type) {
        return getPredicate(obj, criteriaBuilder, type, join.get(fieldName));
    }

    private Predicate getPredicate(Double obj, CriteriaBuilder criteriaBuilder, QueryType type, Path<Object> objectPath) {
        switch (type) {
            case LIKE:
                return criteriaBuilder.like(objectPath.as(String.class), "%" + obj + "%");
            case RIGHT_LIKE:
                return criteriaBuilder.like(objectPath.as(String.class), obj + "%");
            case LEFT_LIKE:
                return criteriaBuilder.equal(objectPath.as(String.class), "%" + obj);
            case GREATER_EQUAL:
                return criteriaBuilder.greaterThanOrEqualTo(objectPath.as(Double.class), obj);
            case GREATER:
                return criteriaBuilder.greaterThan(objectPath.as(Double.class), obj);
            case LESS_EQUAL:
                return criteriaBuilder.lessThanOrEqualTo(objectPath.as(Double.class), obj);
            case LESS:
                return criteriaBuilder.lessThan(objectPath.as(Double.class), obj);
            case UNEQUAL:
                return criteriaBuilder.notEqual(objectPath.as(Double.class), obj);
            default:
                return criteriaBuilder.equal(objectPath.as(Double.class), obj);
        }
    }

    private Predicate getPredicate(Date obj, String fieldName, CriteriaBuilder criteriaBuilder, Root<T> root, QueryType type) {
        return getPredicate(obj, criteriaBuilder, type, root.get(fieldName));
    }

    private Predicate getPredicate(Date obj, String fieldName, CriteriaBuilder criteriaBuilder, Join<Object, Object> join, QueryType type) {
        return getPredicate(obj, criteriaBuilder, type, join.get(fieldName));
    }

    private Predicate getPredicate(Date obj, CriteriaBuilder criteriaBuilder, QueryType type, Path<Object> objectPath) {
        switch (type) {
            case LIKE:
                return criteriaBuilder.like(objectPath.as(String.class), "%" + obj + "%");
            case RIGHT_LIKE:
                return criteriaBuilder.like(objectPath.as(String.class), obj + "%");
            case LEFT_LIKE:
                return criteriaBuilder.equal(objectPath.as(String.class), "%" + obj);
            case GREATER_EQUAL:
                return criteriaBuilder.greaterThanOrEqualTo(objectPath.as(Date.class), obj);
            case GREATER:
                return criteriaBuilder.greaterThan(objectPath.as(Date.class), obj);
            case LESS_EQUAL:
                return criteriaBuilder.lessThanOrEqualTo(objectPath.as(Date.class), obj);
            case LESS:
                return criteriaBuilder.lessThan(objectPath.as(Date.class), obj);
            case UNEQUAL:
                return criteriaBuilder.notEqual(objectPath.as(Date.class), obj);
            default:
                return criteriaBuilder.equal(objectPath.as(Date.class), obj);
        }
    }

    private Predicate getPredicate(Boolean obj, String fieldName, CriteriaBuilder criteriaBuilder, Root<T> root, QueryType type) {
        if (type == QueryType.UNEQUAL) {
            return criteriaBuilder.notEqual(root.get(fieldName).as(Boolean.class), obj);
        }
        return criteriaBuilder.equal(root.get(fieldName).as(Boolean.class), obj);
    }

    private Predicate getPredicate(Boolean obj, String fieldName, CriteriaBuilder criteriaBuilder, Join<Object, Object> join, QueryType type) {
        if (type == QueryType.UNEQUAL) {
            return criteriaBuilder.notEqual(join.get(fieldName).as(Boolean.class), obj);
        }
        return criteriaBuilder.equal(join.get(fieldName).as(Boolean.class), obj);
    }

    private Predicate getPredicate(List<Object> obj, String fieldName, CriteriaBuilder criteriaBuilder, Root<T> root) {
        return criteriaBuilder.in(root.get(fieldName)).value(obj.get(0));
    }

    private Predicate getPredicate(List<Object> obj, String fieldName, CriteriaBuilder criteriaBuilder, Join<Object, Object> join) {
        return criteriaBuilder.in(join.get(fieldName)).value(obj.get(0));
    }

    private Predicate getPredicate(String obj, String fieldName, CriteriaBuilder criteriaBuilder, Root<T> root, QueryType type) {
        return getPredicate(obj, criteriaBuilder, type, root.get(fieldName));
    }

    private Predicate getPredicate(String obj, String fieldName, CriteriaBuilder criteriaBuilder, Join<Object, Object> join, QueryType type) {
        return getPredicate(obj, criteriaBuilder, type, join.get(fieldName));
    }

    private Predicate getPredicate(String obj, CriteriaBuilder criteriaBuilder, QueryType type, Path<Object> objectPath) {
        switch (type) {
            case LIKE:
                return criteriaBuilder.like(objectPath.as(String.class), "%" + obj + "%");
            case RIGHT_LIKE:
                return criteriaBuilder.like(objectPath.as(String.class), obj + "%");
            case LEFT_LIKE:
                return criteriaBuilder.equal(objectPath.as(String.class), "%" + obj);
            case GREATER_EQUAL:
                return criteriaBuilder.greaterThanOrEqualTo(objectPath.as(String.class), obj);
            case GREATER:
                return criteriaBuilder.greaterThan(objectPath.as(String.class), obj);
            case LESS_EQUAL:
                return criteriaBuilder.lessThanOrEqualTo(objectPath.as(String.class), obj);
            case LESS:
                return criteriaBuilder.lessThan(objectPath.as(String.class), obj);
            case UNEQUAL:
                return criteriaBuilder.notEqual(objectPath.as(String.class), obj);
            default:
                return criteriaBuilder.equal(objectPath.as(String.class), obj);
        }
    }

    private static String getBeanPropertityName(String s) {
        s = s.substring(3);
        if (Character.isLowerCase(s.charAt(0))) return s;
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

}