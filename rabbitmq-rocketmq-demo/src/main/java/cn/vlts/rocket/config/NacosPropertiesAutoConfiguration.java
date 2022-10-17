package cn.vlts.rocket.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author throwable
 * @version v1
 * @description nacos配置自动配置
 * @since 2023/1/17 15:04
 */
@Configuration
@EnableConfigurationProperties(value = {NacosExtensionProperties.class, FeatureToggleProperties.class})
public class NacosPropertiesAutoConfiguration {

}
