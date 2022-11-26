package top.damon.database.util;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author Damon
 * @Date 2021/1/26 21:13
 */
@Data
public class SysUserRedisVO implements Serializable {
    private static final long serialVersionUID = 3056651123843530742L;

    private Long userId;

    private Long orgId;

    private String uid;

    private String name;

    @JsonFormat(pattern = DateUtils.DEFAULT_DATEMILLISFORMAT, timezone = "GMT+8")
    private Date updatePasswordTime;

    private Boolean adminFlag = false;
}
