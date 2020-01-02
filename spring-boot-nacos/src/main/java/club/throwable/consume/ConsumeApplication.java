package club.throwable.consume;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/1/1 22:46
 */
@SpringBootApplication(scanBasePackages = "club.throwable.consume")
public class ConsumeApplication implements CommandLineRunner {

    @NacosInjected
    private NamingService namingService;

    public static void main(String[] args) {
        SpringApplication.run(ConsumeApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // 根据服务名从注册中心获取一个健康的服务实例
        Instance instance = namingService.selectOneHealthyInstance("provide-service");
        // 这里只是为了方便才新建RestTemplate实例
        RestTemplate template = new RestTemplate();
        String url = String.format("http://%s:%d/hello?name=throwable", instance.getIp(), instance.getPort());
        String result = template.getForObject(url, String.class);
        System.out.println(String.format("请求URL:%s,响应结果:%s", url, result));
    }
}
