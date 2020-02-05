package club.throwable.cm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/2/5 12:21
 */
@SpringBootApplication(scanBasePackages = "club.throwable.cm")
public class TransactionalMessageApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionalMessageApplication.class, args);
    }
}
