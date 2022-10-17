package cn.vlts.rocket.config;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author throwable
 * @version v1
 * @description nacos extension配置
 * @since 2023/1/17 15:03
 */
@Slf4j
@Data
@ConfigurationProperties(prefix = "nacos.extension")
public class NacosExtensionProperties {

    private String foo;

    private Integer num;

    @PostConstruct
    public void processPostConstruct() {
        log.info("processPostConstruct => {}", JSON.toJSONString(this));
    }

    @PreDestroy
    public void processPreDestroy() {
        log.info("processPreDestroy => {}", JSON.toJSONString(this));
    }
}
