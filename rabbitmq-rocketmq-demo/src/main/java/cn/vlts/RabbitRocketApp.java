package cn.vlts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author throwable
 * @version v1
 * @description rocket app
 * @since 2023/1/13 15:18
 */
@SpringBootApplication(scanBasePackages = "cn.vlts.rabbit")
public class RabbitRocketApp {

    public static void main(String[] args) {
        SpringApplication.run(RabbitRocketApp.class, args);
    }
}
