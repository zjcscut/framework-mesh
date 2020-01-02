package club.throwable.provide;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.naming.NamingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/1/1 22:46
 */
@RestController
@SpringBootApplication(scanBasePackages = "club.throwable.provide")
public class ProvideApplication implements CommandLineRunner {

    @NacosInjected
    private NamingService namingService;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${server.port}")
    private Integer serverPort;

    public static void main(String[] args) {
        SpringApplication.run(ProvideApplication.class, args);
    }

    @GetMapping(path = "/hello")
    public String hello(@RequestParam(name = "name") String name) {
        return String.format("%s say hello!", name);
    }

    @Override
    public void run(String... args) throws Exception {
        // 通过Naming服务注册实例到注册中心
        namingService.registerInstance(applicationName, "127.0.0.1", serverPort);
    }
}
