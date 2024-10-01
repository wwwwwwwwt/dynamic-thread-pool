package cn.ztw.middleware.dynamic.thread.pool.sdk.domain;

import cn.ztw.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;

import java.util.List;

public interface IDynamicThreadPoolService {
    List<ThreadPoolConfigEntity>queryThreadPoolList();

    ThreadPoolConfigEntity queryThreadPoolConfigByName(String threadPoolName);

    void updateThreadPoolConfig(ThreadPoolConfigEntity threadPoolConfig);
}
