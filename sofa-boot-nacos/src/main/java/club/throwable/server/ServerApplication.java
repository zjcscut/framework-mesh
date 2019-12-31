package club.throwable.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2019/12/31 14:51
 */
@SpringBootApplication(scanBasePackages = {"club.throwable.server", "club.throwable.contract"})
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }
}
