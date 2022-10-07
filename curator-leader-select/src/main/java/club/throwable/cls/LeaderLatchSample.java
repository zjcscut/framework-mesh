package club.throwable.cls;

import com.google.common.collect.Lists;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/3/26 22:46
 */
public class LeaderLatchSample {

    private static final String CONNECTION_STRING = "127.0.0.1:2181";
    private static final int NODE_COUNT = 3;
    private static final String LATCH_PATH = "/francis/leader";
    private static final Logger LOGGER = LoggerFactory.getLogger(LeaderLatchSample.class);

    public static void main(String[] args) throws Exception {
        List<LeaderLatch> leaderLatches = Lists.newArrayList();
        List<CuratorFramework> curatorFrameworks = Lists.newArrayList();
        try {
            for (int i = 1; i <= NODE_COUNT; i++) {
                int num = i;
                CuratorFramework client = CuratorFrameworkFactory
                        .newClient(CONNECTION_STRING, new ExponentialBackoffRetry(20000, 3));
                curatorFrameworks.add(client);
                LeaderLatch leaderLatch = new LeaderLatch(client, LATCH_PATH);
                leaderLatch.addListener(new LeaderLatchListener() {
                    @Override
                    public void isLeader() {
                        LOGGER.info("Node [{}] is a leader!", num);
                    }

                    @Override
                    public void notLeader() {
                        LOGGER.info("Node [{}] is not a leader!", num);
                    }
                });
                leaderLatches.add(leaderLatch);
            }
            curatorFrameworks.forEach(CuratorFramework::start);
            leaderLatches.forEach(latch -> {
                try {
                    latch.start();
                } catch (Exception e) {
                    LOGGER.error("LeaderLatch start error", e);
                }
            });
            Thread.sleep(30000);
        } finally {
            for (LeaderLatch leader : leaderLatches) {
                CloseableUtils.closeQuietly(leader);
            }
            for (CuratorFramework client : curatorFrameworks) {
                CloseableUtils.closeQuietly(client);
            }
        }
        Thread.sleep(Integer.MAX_VALUE);
    }
}
