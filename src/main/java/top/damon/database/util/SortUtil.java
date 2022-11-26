package top.damon.database.util;

import org.springframework.data.domain.Sort;

/**
 * @Author Damon
 * @Date 2021/2/1 14:52
 */
public class SortUtil {

    private SortUtil(){
        //
    }

    public static Sort sort(){
        return Sort.by(Sort.Direction.DESC, "updatedTime");
    }
}
