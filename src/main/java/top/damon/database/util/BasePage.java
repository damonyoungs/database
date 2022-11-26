package top.damon.database.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Damon
 * @Date 2020/11/25 10:44
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class BasePage<T> implements Serializable {

    private static final long serialVersionUID = -626767503720115626L;
    //当前页
    private long current;
    //每页的数量
    private long size;
    //总记录数
    private long total;
    //总页数
    private long pages;
    //结果集
    private List<T> records;

    public BasePage(Page<T> page) {
        this.current = page.getNumber();
        this.size = page.getSize();
        this.pages = page.getTotalPages();
        this.records = page.getContent();
        this.total = page.getTotalElements();
    }

    public BasePage(List<T> list, long current, long total, long pages){
        this.current = current;
        this.size = list.size();
        this.pages = pages;
        this.records = list;
        this.total = total;
    }

}
