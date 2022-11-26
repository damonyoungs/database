package top.damon.database.repository;

import cn.hutool.core.bean.BeanUtil;
import top.damon.database.entity.BaseDO;
import top.damon.database.enums.ResponseEnum;
import top.damon.database.exception.MyException;
import top.damon.database.util.HttpContextUtil;
import top.damon.database.util.UpdateUtil;
import org.hibernate.StaleObjectStateException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Optional;

/**
 * @Author Damon
 * @Date 2020/12/7 9:20
 */
@NoRepositoryBean
public interface BaseRepository<T extends BaseDO, ID extends Serializable>
        extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    @Transactional
    default void logicDelete(ID id){
        Optional<T> rec = findById(id);
        if(rec.isPresent()){
            T t = rec.get();
            t.setDeleted(true);
            t.setDeleteTime(new Date());
            t.setDeleteBy(HttpContextUtil.getId());
            save(t);
        }
    }

    @Transactional
    default void logicDelete(T entity) {
        try {
            Method getId = entity.getClass().getMethod("getId");
            logicDelete((ID) getId.invoke(entity));
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Transactional
    default void logicDelete(Iterable<? extends T> entities) {
        entities.forEach(entity -> {
            Method getId = null;
            try {
                getId = entity.getClass().getMethod("getId");
                logicDelete((ID) getId.invoke(entity));
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

    @Transactional
    default <S extends T> S update(S entity) throws MyException{
        try {
            Method getId = entity.getClass().getMethod("getId");
            Optional<T> optional = findById((ID) getId.invoke(entity));
            if (!optional.isPresent()) throw new MyException(ResponseEnum.INCORRECT_PARAMS.getCode(), "记录不存在");
            T t = optional.get();
            t.setUpdatedBy(HttpContextUtil.getId());
            t.setDeleteTime(new Date());
            BeanUtil.copyProperties(entity, t, UpdateUtil.getNullPropertyNames(entity));
            entity = (S) save(t);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ObjectOptimisticLockingFailureException e) {
            e.printStackTrace();
            if (e.getCause() instanceof StaleObjectStateException) {
                throw new MyException(ResponseEnum.RECORD_ALREADY_UPDATE, e.getCause());
            }
        }
        return entity;
    }

}
