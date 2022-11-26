package top.damon.database.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import top.damon.database.util.DateUtils;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author Damon
 * @Date 2020/12/7 9:37
 */
@Getter
@Setter
@RequiredArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseDO implements Serializable {

    private static final long serialVersionUID = -8676973834647046839L;

    @Version
    private Integer version;

    @CreatedBy
    @Column(updatable =false)
    private Long createdBy;

    @JsonFormat(pattern = DateUtils.DEFAULT_DATEMILLISFORMAT, timezone = "GMT+8")
    @CreatedDate
    @Column(updatable =false)
    private Date createdTime;

    @CreatedBy
    @LastModifiedBy
    private Long updatedBy;

    @JsonFormat(pattern = DateUtils.DEFAULT_DATEMILLISFORMAT, timezone = "GMT+8")
    @CreatedDate
    @LastModifiedDate
    private Date updatedTime;

    @Column(name = "is_delete")
    private Boolean deleted = false;

    @LastModifiedBy
    private Long deleteBy;

    @LastModifiedDate
    @JsonFormat(pattern = DateUtils.DEFAULT_DATEMILLISFORMAT, timezone = "GMT+8")
    private Date deleteTime;
}
