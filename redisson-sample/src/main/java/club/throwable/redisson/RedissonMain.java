package club.throwable.redisson;

import org.redisson.Redisson;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;

import java.util.ArrayList;
import java.util.List;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2019/12/27 10:31
 */
public class RedissonMain {

    public static void main(String[] args) throws Exception {
        RedissonClient client = Redisson.create();
        RSemaphore semaphore = client.getSemaphore("ds:semaphore");
        semaphore.delete();
        semaphore.trySetPermits(5);
        List<Thread> threads = new ArrayList<>(16);
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(() -> {
                try {
                    semaphore.acquire();
                    System.out.println(String.format("线程-%s获取到信号量......", Thread.currentThread().getName()));
                    semaphore.release();
                    System.out.println(String.format("线程-%s释放信号量......", Thread.currentThread().getName()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            thread.setName("线程-" + i);
            thread.setDaemon(true);
            threads.add(thread);
        }
        threads.forEach(t -> t.start());
        Thread.sleep(Integer.MAX_VALUE);
    }
}
