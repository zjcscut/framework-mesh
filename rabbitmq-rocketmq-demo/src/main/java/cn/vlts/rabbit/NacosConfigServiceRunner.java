package cn.vlts.rabbit;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import org.springframework.boot.CommandLineRunner;

import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author throwable
 * @version v1
 * @description
 * @since 2023/2/12 2:32
 */
public class NacosConfigServiceRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.SERVER_ADDR, "127.0.0.1:8848");
        properties.put(PropertyKeyConst.NAMESPACE, "LOCAL");
        ConfigService configService = NacosFactory.createConfigService(properties);
        Executor executor = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            thread.setName("NacosConfigSyncWorker");
            return thread;
        });
        configService.addListener("", "", new Listener() {
            @Override
            public Executor getExecutor() {
                return executor;
            }

            @Override
            public void receiveConfigInfo(String configInfo) {
                 // do something with 'configInfo'
            }
        });
    }
}
