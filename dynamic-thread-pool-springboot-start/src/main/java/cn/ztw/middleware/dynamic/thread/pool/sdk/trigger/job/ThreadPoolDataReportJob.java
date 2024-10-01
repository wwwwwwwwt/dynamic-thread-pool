package cn.ztw.middleware.dynamic.thread.pool.sdk.trigger.job;


import cn.ztw.middleware.dynamic.thread.pool.sdk.domain.IDynamicThreadPoolService;
import cn.ztw.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import cn.ztw.middleware.dynamic.thread.pool.sdk.registry.IRegistry;
import cn.ztw.middleware.dynamic.thread.pool.sdk.registry.redis.RedisRegistry;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

/**
 * 线程池数据上报任务
 */
public class ThreadPoolDataReportJob {
    private Logger logger = LoggerFactory.getLogger(ThreadPoolDataReportJob.class);

    private final IDynamicThreadPoolService iDynamicThreadPoolService;

    private final IRegistry registry;

    public ThreadPoolDataReportJob(IDynamicThreadPoolService iDynamicThreadPoolService, IRegistry registry) {
        this.iDynamicThreadPoolService = iDynamicThreadPoolService;
        this.registry = registry;
    }
    @Scheduled(cron = "0/20 * * * * ?")
    public void exeReportThreadPoolList(){
        List<ThreadPoolConfigEntity>list = iDynamicThreadPoolService.queryThreadPoolList();
        registry.reportThreadPool(list);
        logger.info("动态线程池，上报线程池信息：{}", JSON.toJSONString(list));

        for(ThreadPoolConfigEntity threadPoolConfigEntity : list){
            registry.reportThreadPoolConfigParameter(threadPoolConfigEntity);
            logger.info("动态线程池，上报线程池配置：{}", JSON.toJSONString(threadPoolConfigEntity));
        }
    }
}
