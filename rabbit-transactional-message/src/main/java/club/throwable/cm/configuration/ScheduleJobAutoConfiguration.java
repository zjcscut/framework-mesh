package club.throwable.cm.configuration;

import club.throwable.cm.service.TransactionalMessageManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.TimeUnit;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/2/5 10:33
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableScheduling
public class ScheduleJobAutoConfiguration {

    private final TransactionalMessageManagementService managementService;

    /**
     * 这里用的是本地的Redis,实际上要做成配置
     */
    private final RedissonClient redisson = Redisson.create();

    @Scheduled(fixedDelay = 10000)
    public void transactionalMessageCompensationTask() throws Exception {
        RLock lock = redisson.getLock("transactionalMessageCompensationTask");
        // 等待时间5秒,预期300秒执行完毕,这两个值需要按照实际场景定制
        boolean tryLock = lock.tryLock(5, 300, TimeUnit.SECONDS);
        if (tryLock) {
            try {
                long start = System.currentTimeMillis();
                log.info("开始执行事务消息推送补偿定时任务...");
                managementService.processPendingCompensationRecords();
                long end = System.currentTimeMillis();
                long delta = end - start;
                // 以防锁过早释放
                if (delta < 5000) {
                    Thread.sleep(5000 - delta);
                }
                log.info("执行事务消息推送补偿定时任务完毕,耗时:{} ms...", end - start);
            } finally {
                lock.unlock();
            }
        }
    }
}
