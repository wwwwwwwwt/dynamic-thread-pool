package cn.ztw.middleware.dynamic.thread.pool.sdk.trigger.listener;

import cn.ztw.middleware.dynamic.thread.pool.sdk.domain.IDynamicThreadPoolService;
import cn.ztw.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import cn.ztw.middleware.dynamic.thread.pool.sdk.registry.IRegistry;
import lombok.extern.java.Log;
import org.redisson.api.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 动态线程池变更监听
 */
public class ThreadPoolConfigAdjustListener implements MessageListener<ThreadPoolConfigEntity> {
    private Logger logger = LoggerFactory.getLogger(ThreadPoolConfigAdjustListener.class);

    private final IDynamicThreadPoolService dynamicThreadPoolService;

    private IRegistry registry;

    public ThreadPoolConfigAdjustListener(IDynamicThreadPoolService dynamicThreadPoolService, IRegistry registry) {
        this.dynamicThreadPoolService = dynamicThreadPoolService;
        this.registry = registry;
    }


    @Override
    public void onMessage(CharSequence charSequence, ThreadPoolConfigEntity threadPoolConfigEntity) {
        logger.info("动态线程池，调整线程池配置，线程池名称：{}， 核心线程数：{}， 最大线程数：{}", threadPoolConfigEntity.getThreadPoolName(), threadPoolConfigEntity.getCorePoolSize(), threadPoolConfigEntity.getMaximumPoolSize());
        dynamicThreadPoolService.updateThreadPoolConfig(threadPoolConfigEntity);

        //更新后上报最新数据
        List<ThreadPoolConfigEntity> threadPoolConfigEnties = dynamicThreadPoolService.queryThreadPoolList();
        registry.reportThreadPool(threadPoolConfigEnties);

        ThreadPoolConfigEntity threadPoolConfigEntityCurrent = dynamicThreadPoolService.queryThreadPoolConfigByName(threadPoolConfigEntity.getThreadPoolName());
        registry.reportThreadPoolConfigParameter(threadPoolConfigEntityCurrent);
        logger.info("修改成功线程，变更后线程为：线程池名称：{}，核心线程数：{}， 最大线程数：{}，", threadPoolConfigEntityCurrent.getThreadPoolName(), threadPoolConfigEntity.getCorePoolSize(), threadPoolConfigEntityCurrent.getMaximumPoolSize());

    }
}
