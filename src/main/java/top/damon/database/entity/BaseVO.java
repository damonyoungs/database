package top.damon.database.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import top.damon.database.util.DateUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author Damon
 * @Date 2020/12/11 13:49
 */
@Getter
@Setter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class BaseVO implements Serializable {

    private static final long serialVersionUID = 4283686288669337163L;

    private Long id;

    private Integer version;

    private Long createdBy;

    @JsonFormat(pattern = DateUtils.DEFAULT_DATEMILLISFORMAT, timezone = "GMT+8")
    private Date createdTime;

    private Long updatedBy;

    @JsonFormat(pattern = DateUtils.DEFAULT_DATEMILLISFORMAT, timezone = "GMT+8")
    private Date updatedTime;
}
