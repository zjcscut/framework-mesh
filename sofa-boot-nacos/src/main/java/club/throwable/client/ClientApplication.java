package club.throwable.client;

import club.throwable.contract.HelloService;
import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2019/12/31 14:51
 */
@Slf4j
@SpringBootApplication(scanBasePackages = {"club.throwable.client", "club.throwable.contract"})
public class ClientApplication implements CommandLineRunner {

//    @SofaReference(binding = @SofaReferenceBinding(bindingType = "bolt"))
//    private HelloService boltHelloService;

    @SofaReference(binding = @SofaReferenceBinding(bindingType = "rest"))
    private HelloService restHelloService;

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        log.info("Bolt调用HelloService#sayHello(),结果:{}", boltHelloService.sayHello("throwable"));
        log.info("Rest调用HelloService#sayHello(),结果:{}", restHelloService.sayHello("throwable"));
    }
}
