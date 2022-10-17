package cn.vlts.rocket.config;

import com.google.common.collect.Maps;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * @author throwable
 * @version v1
 * @description 特性开关配置
 * @since 2023/1/17 15:03
 */
@Data
@ConfigurationProperties(prefix = "feature.toggle")
public class FeatureToggleProperties {

    private List<RocketmqProducer> rocketmqProducers;

    @Data
    public static class RocketmqProducer {

        private String topic;

        private Boolean enable;
    }

    public Map<String, Boolean> getRocketmqProducers() {
        Map<String, Boolean> entry = Maps.newHashMap();
        if (!CollectionUtils.isEmpty(rocketmqProducers)) {
            rocketmqProducers.forEach(rocketmqProducer -> entry.put(rocketmqProducer.getTopic(), rocketmqProducer.getEnable()));
        }
        return entry;
    }
}
