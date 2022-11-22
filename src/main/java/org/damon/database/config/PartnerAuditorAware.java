package org.damon.database.config;

import org.damon.database.util.HttpContextUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

/**
 * @Author chengrong.yang
 * @Date 2021/1/25 2:56
 */
@Configuration
public class PartnerAuditorAware implements AuditorAware<Long> {
    @Override
    public Optional<Long> getCurrentAuditor() {
        Long currentLoginUserId = HttpContextUtil.getId();
        return Optional.of(currentLoginUserId);
    }

}
