package top.damon.database.util;

import lombok.Data;

/**
 * @Author Damon
 * @Date 2021/1/26 18:26
 */
@Data
public class PageVO {

    private Integer limit = 10;

    private Integer offset = 0;
}
