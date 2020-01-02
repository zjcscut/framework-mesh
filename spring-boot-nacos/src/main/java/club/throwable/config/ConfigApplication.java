package club.throwable.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/1/2 10:28
 */
@RestController
@NacosPropertySource(dataId = "example", autoRefreshed = true)
@SpringBootApplication(scanBasePackages = "club.throwable.config")
public class ConfigApplication {

    @NacosValue(value = "${counter:0}", autoRefreshed = true)
    public Long counter;

    public static void main(String[] args) {
        SpringApplication.run(ConfigApplication.class, args);
    }

    @GetMapping(path = "/get")
    public String get() {
        return String.format("Counter value:%d", counter);
    }
}
