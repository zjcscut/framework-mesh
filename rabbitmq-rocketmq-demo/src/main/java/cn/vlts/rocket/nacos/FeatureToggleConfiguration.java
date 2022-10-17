package cn.vlts.rocket.nacos;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.cloud.nacos.client.NacosPropertySourceLocator;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author throwable
 * @version v1
 * @description
 * @since 2023/1/16 16:30
 */
@Configuration
@Slf4j
public class FeatureToggleConfiguration implements SmartInitializingSingleton, BeanFactoryAware {

    @Value("${feature.toggle.serverAddress}")
    private String serverAddress;

    @Value("${feature.toggle.namespace}")
    private String namespace;

    @Value("${feature.toggle.configPrefix}")
    private String configPrefix;

    @Value("${feature.toggle.configSuffix}")
    private String configSuffix;

    @Value("${feature.toggle.configGroup}")
    private String configGroup;

    @Value("${feature.toggle.timeout}")
    private Integer timeout;

    private ConfigurableListableBeanFactory configurableListableBeanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.configurableListableBeanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Bean
    public NacosConfigManager featureToggleNacosConfigManager(Environment environment) {
        NacosConfigProperties nacosConfigProperties = new NacosConfigProperties();
        nacosConfigProperties.setServerAddr(serverAddress);
        nacosConfigProperties.setNamespace(namespace);
        nacosConfigProperties.setPrefix(configPrefix);
        nacosConfigProperties.setFileExtension(configSuffix);
        nacosConfigProperties.setRefreshEnabled(true);
        nacosConfigProperties.setGroup(configGroup);
        nacosConfigProperties.setTimeout(timeout);
        nacosConfigProperties.setEnvironment(environment);
        return new NacosConfigManager(nacosConfigProperties);
    }

    @Bean
    public ExecutorService featureToggleNacosThreadPoolExecutor() {
        return Executors.newSingleThreadExecutor(runnable -> {
            Thread worker = new Thread(runnable);
            worker.setDaemon(true);
            worker.setName("FeatureToggleNacosWorker");
            return worker;
        });
    }

    @Override
    public void afterSingletonsInstantiated() {
        NacosConfigManager featureToggleNacosConfigManager
                = configurableListableBeanFactory.getBean("featureToggleNacosConfigManager", NacosConfigManager.class);
        ExecutorService featureToggleNacosThreadPoolExecutor
                = configurableListableBeanFactory.getBean("featureToggleNacosThreadPoolExecutor", ExecutorService.class);
        Environment environment = configurableListableBeanFactory.getBean(Environment.class);
        ConfigService configService = featureToggleNacosConfigManager.getConfigService();
        NacosConfigProperties nacosConfigProperties = featureToggleNacosConfigManager.getNacosConfigProperties();
        String dataId = nacosConfigProperties.getPrefix() + "." + nacosConfigProperties.getFileExtension();
        String group = nacosConfigProperties.getGroup();
        try {
            configService.addListener(dataId, group, new NacosConfigListener(featureToggleNacosThreadPoolExecutor, environment,
                    featureToggleNacosConfigManager));
        } catch (Exception e) {
            log.error("注册Nacos配置监听器失败,dataId:{},group:{}", dataId, group, e);
        }
    }

    @Slf4j
    private static class NacosConfigListener implements Listener {

        private final Executor executor;

        private final Environment environment;

        private final NacosPropertySourceLocator nacosPropertySourceLocator;

        private final String dataId;

        private final String group;

        public NacosConfigListener(Executor executor, Environment environment, NacosConfigManager nacosConfigManager) {
            this.executor = executor;
            this.environment = environment;
            this.nacosPropertySourceLocator = new NacosPropertySourceLocator(nacosConfigManager);
            this.dataId = nacosConfigManager.getNacosConfigProperties().getPrefix() + "." + nacosConfigManager.getNacosConfigProperties().getFileExtension();
            this.group = nacosConfigManager.getNacosConfigProperties().getGroup();
        }

        @Override
        public Executor getExecutor() {
            return executor;
        }

        @Override
        public void receiveConfigInfo(String configInfo) {
            log.info("监听到Nacos配置变更,dataId:{},group:{},配置内容:{}", dataId, group, configInfo);
            PropertySource<?> propertySource = nacosPropertySourceLocator.locate(environment);

            log.info("重新加载Nacos的PropertySource,加载结果:{}", JSON.toJSONString(propertySource));
        }
    }
}
