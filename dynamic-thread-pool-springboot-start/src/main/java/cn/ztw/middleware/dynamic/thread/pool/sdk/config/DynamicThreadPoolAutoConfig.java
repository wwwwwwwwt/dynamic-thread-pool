package cn.ztw.middleware.dynamic.thread.pool.sdk.config;

import cn.ztw.middleware.dynamic.thread.pool.sdk.domain.DynamicThreadPoolService;
import com.alibaba.fastjson.JSON;
import jodd.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.management.monitor.StringMonitor;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * 动态配置入口
 */
@Configuration
public class DynamicThreadPoolAutoConfig {
    private final Logger logger = LoggerFactory.getLogger(DynamicThreadPoolAutoConfig.class);
    @Bean("dynamicThreadPoolService")
    public DynamicThreadPoolService dynamicThreadPoolService(ApplicationContext applicationContext, Map<String,ThreadPoolExecutor> threadPoolExecutorMap){
        String applicationName = applicationContext.getEnvironment().getProperty("spring.application.name");
        if(StringUtil.isBlank(applicationName)){
            applicationName = "缺省的";
            logger.warn("动态线程池，启动提示，Springboot 应用未配置 spring.application.name 无法获取到应用名称！");
        }
        return new DynamicThreadPoolService(applicationName, threadPoolExecutorMap);
    }
}
