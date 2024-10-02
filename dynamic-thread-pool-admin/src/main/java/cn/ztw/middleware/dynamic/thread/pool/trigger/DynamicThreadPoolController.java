package cn.ztw.middleware.dynamic.thread.pool.trigger;

import cn.ztw.middleware.dynamic.thread.pool.sdk.domain.model.entity.RegistryEnumVO;
import cn.ztw.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import cn.ztw.middleware.dynamic.thread.pool.types.Response;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RList;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/dynamic/thread/pool/")
public class DynamicThreadPoolController {
    @Resource
    public RedissonClient redissonClient;

    /**
     * 查询线程池数据
     * curl --request GET \
     * --url 'http://localhost:8089/api/v1/dynamic/thread/pool/query_thread_pool_list'
     */
    @GetMapping(value = "query_thread_pool_list")
    public Response<List<ThreadPoolConfigEntity>> queryThreadPoolList(){
        try{
            RList<ThreadPoolConfigEntity> cacheList = redissonClient.getList(RegistryEnumVO.THREAD_POOL_CONFIG_LIST_KEY.getKey());
            return Response.<List<ThreadPoolConfigEntity>>builder()
                    .code(Response.Code.SUCCESS.getCode())
                    .info(Response.Code.SUCCESS.getInfo())
                    .data(cacheList.readAll())
                    .build();
        }catch (Exception e){
            log.error("查询线程池数据异常");
            return Response.<List<ThreadPoolConfigEntity>>builder()
                    .code(Response.Code.UN_ERROR.getCode())
                    .info(Response.Code.UN_ERROR.getInfo())
                    .build();
        }
    }

    /**
     * 查询线程池配置
     * curl --request GET \
     * --url 'http://localhost:8089/api/v1/dynamic/thread/pool/query_thread_pool_config?appName=dynamic-thread-pool-test-app&threadPoolName=threadPoolExecutor'
     */
    @GetMapping(value = "query_thread_pool_config")
    public Response<ThreadPoolConfigEntity> queryThreadPoolConfig(@RequestParam String appName, @RequestParam String threadPoolName){
        try{
            String cacheKey = RegistryEnumVO.THREAD_POOL_CONFIG_PARAMETER_LIST_KEY.getKey() + "_" + appName + "_" + threadPoolName;
            ThreadPoolConfigEntity threadPoolConfigEntity = redissonClient.<ThreadPoolConfigEntity>getBucket(cacheKey).get();
            return Response.<ThreadPoolConfigEntity>builder()
                    .code(Response.Code.SUCCESS.getCode())
                    .info(Response.Code.SUCCESS.getInfo())
                    .data(threadPoolConfigEntity)
                    .build();
        }catch (Exception e){
            log.error("查询线程池配置异常");
            return Response.<ThreadPoolConfigEntity>builder()
                    .code(Response.Code.UN_ERROR.getCode())
                    .info(Response.Code.UN_ERROR.getInfo())
                    .build();
        }
    }

    @PostMapping(value = "update_thread_pool_config")
    public Response<Boolean> updateThreadPoolConfig(@RequestBody ThreadPoolConfigEntity request){
        try{
            log.info("修改线程池信息：应用名称：{}， 线程池核数：{}，线程池最大线程数：{}", request.getAppName(),request.getCorePoolSize(), request.getMaximumPoolSize());
            RTopic topic = redissonClient.getTopic(RegistryEnumVO.DYNAMIC_THREAD_POOL_REDIS_TOPIC + "_" + request.getAppName());
            topic.publish(request);
            log.info("修改线程池配置完成 {} {}", request.getAppName(), request.getThreadPoolName());
            return Response.<Boolean>builder()
                    .code(Response.Code.SUCCESS.getCode())
                    .info(Response.Code.SUCCESS.getInfo())
                    .data(true)
                    .build();
        }catch (Exception e){
            log.error("修改线程池配置异常 {}", JSON.toJSONString(request), e);
            return Response.<Boolean>builder()
                    .code(Response.Code.UN_ERROR.getCode())
                    .info(Response.Code.UN_ERROR.getInfo())
                    .data(false)
                    .build();

        }
    }
}
