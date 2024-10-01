package cn.ztw.middleware.dynamic.thread.pool.sdk.domain;

import cn.ztw.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

public class DynamicThreadPoolService implements IDynamicThreadPoolService{

    private final Logger logger = LoggerFactory.getLogger(DynamicThreadPoolService.class);

    private final Map<String, ThreadPoolExecutor> threadPoolExecutorMap;

    private final String applicationName;

    public DynamicThreadPoolService(String applicationName, Map<String, ThreadPoolExecutor> threadPoolExecutorMap) {
        this.threadPoolExecutorMap = threadPoolExecutorMap;
        this.applicationName = applicationName;
    }

    @Override
    public List<ThreadPoolConfigEntity> queryThreadPoolList() {

        Set<String> threadPoolBeanNames = threadPoolExecutorMap.keySet();
        List<ThreadPoolConfigEntity> threadPoolVOS = new ArrayList<>(threadPoolBeanNames.size());
        for(String name : threadPoolBeanNames){
            ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(name);
            ThreadPoolConfigEntity threadPoolConfigVO = new ThreadPoolConfigEntity(applicationName, name);
            threadPoolConfigVO.setPoolSize(threadPoolExecutor.getPoolSize());
            threadPoolConfigVO.setCorePoolSize(threadPoolExecutor.getCorePoolSize());
            threadPoolConfigVO.setMaximumPoolSize(threadPoolExecutor.getMaximumPoolSize());
            threadPoolConfigVO.setActiveCount(threadPoolExecutor.getActiveCount());
            threadPoolConfigVO.setQueueType(threadPoolExecutor.getQueue().getClass().getSimpleName());
            threadPoolConfigVO.setQueueSize(threadPoolExecutor.getQueue().size());
            threadPoolConfigVO.setRemainingCapacity(threadPoolExecutor.getQueue().remainingCapacity());
            threadPoolVOS.add(threadPoolConfigVO);
        }
        return threadPoolVOS;

    }

    @Override
    public ThreadPoolConfigEntity queryThreadPoolConfigByName(String threadPoolName) {
        ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolName);
        if(null == threadPoolName)return new ThreadPoolConfigEntity(applicationName, threadPoolName);
        // 线程池配置数据
        ThreadPoolConfigEntity threadPoolConfigVO = new ThreadPoolConfigEntity(applicationName, threadPoolName);
        threadPoolConfigVO.setPoolSize(threadPoolExecutor.getPoolSize());
        threadPoolConfigVO.setCorePoolSize(threadPoolExecutor.getCorePoolSize());
        threadPoolConfigVO.setMaximumPoolSize(threadPoolExecutor.getMaximumPoolSize());
        threadPoolConfigVO.setActiveCount(threadPoolExecutor.getActiveCount());
        threadPoolConfigVO.setQueueType(threadPoolExecutor.getQueue().getClass().getSimpleName());
        threadPoolConfigVO.setQueueSize(threadPoolExecutor.getQueue().size());
        threadPoolConfigVO.setRemainingCapacity(threadPoolExecutor.getQueue().remainingCapacity());
        if(logger.isDebugEnabled()){
            logger.info("动态线程池信息：{}", threadPoolConfigVO);
        }
        return threadPoolConfigVO;
    }


    @Override
    public void updateThreadPoolConfig(ThreadPoolConfigEntity threadPoolConfig) {
        if(null == threadPoolConfig || !applicationName.equals(threadPoolConfig.getAppName())) return;

        ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolConfig.getThreadPoolName());

        if(null == threadPoolExecutor)return;

        //设置核心线程数和最大线程数
        threadPoolExecutor.setCorePoolSize(threadPoolConfig.getCorePoolSize());
        threadPoolExecutor.setMaximumPoolSize(threadPoolConfig.getMaximumPoolSize());
    }
}
