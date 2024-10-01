package cn.ztw.middleware.dynamic.thread.pool.sdk.registry.redis;

import cn.ztw.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import cn.ztw.middleware.dynamic.thread.pool.sdk.domain.vabobj.RegistryEnumVO;
import cn.ztw.middleware.dynamic.thread.pool.sdk.registry.IRegistry;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.List;

/**
 * 使用redis作为注册中心
 */

public class RedisRegistry implements IRegistry {


    private final RedissonClient redissonClient;

    public RedisRegistry(RedissonClient redissonClient){
        this.redissonClient = redissonClient;
    }
    @Override
    public void reportThreadPool(List<ThreadPoolConfigEntity> threadPoolConfigEntities) {
        RList<ThreadPoolConfigEntity> list = redissonClient.getList(RegistryEnumVO.THREAD_POOL_CONFIG_LIST_KEY.getKey());
        list.delete();
        list.addAll(threadPoolConfigEntities);
    }

    @Override
    public void reportThreadPoolConfigParameter(ThreadPoolConfigEntity threadPoolConfigEntity) {
        String cacheKey = RegistryEnumVO.THREAD_POOL_CONFIG_LIST_KEY.getKey() + "_" + threadPoolConfigEntity.getAppName() + "_" + threadPoolConfigEntity.getThreadPoolName();
        RBucket<ThreadPoolConfigEntity> bucket = redissonClient.getBucket(cacheKey);
        bucket.set(threadPoolConfigEntity, Duration.ofDays(30));
    }
}
