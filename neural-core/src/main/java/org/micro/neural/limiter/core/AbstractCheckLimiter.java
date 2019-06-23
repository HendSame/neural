package org.micro.neural.limiter.core;

import lombok.Getter;
import org.micro.neural.common.utils.BeanUtils;
import org.micro.neural.config.GlobalConfig;
import org.micro.neural.extension.Extension;
import org.micro.neural.limiter.LimiterConfig;
import org.micro.neural.limiter.LimiterStatistics;
import lombok.extern.slf4j.Slf4j;

/**
 * The Abstract Check Limiter.
 *
 * @author lry
 **/
@Slf4j
@Getter
public abstract class AbstractCheckLimiter implements ILimiter {

    protected volatile LimiterConfig limiterConfig = null;
    protected volatile LimiterStatistics statistics = new LimiterStatistics();
    protected final Extension extension;

    AbstractCheckLimiter() {
        this.extension = this.getClass().getAnnotation(Extension.class);
        if (null == extension) {
            throw new IllegalStateException("The " + this.getClass().getName() + " must has @Extension");
        }
    }

    @Override
    public boolean refresh(LimiterConfig limiterConfig) throws Exception {
        log.debug("The refresh {}", limiterConfig);
        if (null == limiterConfig || this.limiterConfig.equals(limiterConfig)) {
            return true;
        }

        BeanUtils.copyProperties(limiterConfig, this.limiterConfig);
        return true;
    }

    /**
     * Whether the check does not need process
     *
     * @return true indicates that it does not need to be handled
     */
    boolean checkDisable() {
        if (null == limiterConfig) {
            return true;
        }

        return null == limiterConfig.getEnable() || GlobalConfig.Switch.OFF == limiterConfig.getEnable();
    }

    /**
     * The check the need for concurrency limiting exceed
     *
     * @return true indicates that it need to be concurrency handled
     */
    boolean checkConcurrencyExceed() {
        return limiterConfig.getConcurrency() > 0L;
    }

    /**
     * The check the need for rate limiting exceed
     *
     * @return true indicates that it need to be rate handled
     */
    boolean checkRateExceed() {
        return limiterConfig.getRate() > 0L;
    }

}
