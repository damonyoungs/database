package org.damon.database.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;


/**
 * @Author Damon
 * @Date 2021/1/25 3:12
 */
public class HttpContextUtil {

    private HttpContextUtil() {
        //
    }

    public static Long getId() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return getRequest(requestAttributes);
    }

    private static Long getRequest(ServletRequestAttributes requestAttributes) {
        if (null != requestAttributes) {
            HttpServletRequest request = requestAttributes.getRequest();
            return getToken(request);
        }
        return 0L;
    }

    private static Long getToken(HttpServletRequest request) {
        if (null != request) {
            return getSession(request.getHeader(Constant.SYS_SESSION_REDIS_KEY));
        }
        return 0L;
    }

    private static Long getSession(String authorization) {
        if (StrUtil.isNotEmpty(authorization)) {
            RedisUtil redisUtil = SpringUtil.getBean(RedisUtil.class);
            SysUserRedisVO sysUserRedisVO = redisUtil.get(authorization, SysUserRedisVO.class);
            if (null != sysUserRedisVO) {
                return sysUserRedisVO.getUserId();
            }
        }
        return 0L;
    }

}
