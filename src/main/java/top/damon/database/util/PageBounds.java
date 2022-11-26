package top.damon.database.util;


/**
 * @Author Damon
 * @Date 2020/12/7 14:42
 */
public class PageBounds {

    private final Integer offset;

    private final Integer limit;

    public PageBounds(Integer offset, Integer limit) {
        this.offset = offset;
        this.limit = limit;
    }

    public Integer getLimit(){
        if(null == limit){
            return 10;
        }
        return limit;
    }

    public Integer getOffset(){
        if(null == offset){
            return 0;
        }
        return offset;
    }
}
