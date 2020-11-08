package cn.throwx.cb;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author throwable
 * @version v1
 * @description
 * @since 2020/10/24 23:19
 */
public enum CircuitBreakerResourceManager {

    /**
     * 单例
     */
    X;

    public final Map<String, CircuitBreakerResource> cache = new ConcurrentHashMap<>(8);

    public void register(CircuitBreakerResourceConf conf) {
        cache.computeIfAbsent(conf.getResourceName(), rn -> {
            int coreSize = conf.getCoreSize();
            int queueSize = conf.getQueueSize();
            BlockingQueue<Runnable> queue;
            if (queueSize > 0) {
                queue = new ArrayBlockingQueue<>(queueSize);
            } else {
                queue = new SynchronousQueue<>();
            }
            ThreadPoolExecutor executor = new ThreadPoolExecutor(
                    coreSize,
                    coreSize,
                    0,
                    TimeUnit.SECONDS,
                    queue,
                    new ThreadFactory() {
                        private final AtomicInteger counter = new AtomicInteger();

                        @Override
                        public Thread newThread(Runnable r) {
                            Thread thread = new Thread(r);
                            thread.setDaemon(true);
                            thread.setName(rn + "-CircuitBreakerWorker-" + counter.getAndIncrement());
                            return thread;
                        }
                    },
                    new ThreadPoolExecutor.AbortPolicy()
            );
            CircuitBreakerResource resource = new CircuitBreakerResource();
            resource.setExecutor(executor);
            resource.setTimeout(conf.getTimeout());
            return resource;
        });
    }

    public CircuitBreakerResource get(String resourceName) {
        return Optional.ofNullable(cache.get(resourceName)).orElseThrow(() -> new IllegalArgumentException(resourceName));
    }
}
