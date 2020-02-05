package club.throwable.cm;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/2/5 12:24
 */
@RequiredArgsConstructor
@Component
public class MockBusinessRunner implements CommandLineRunner {

    private final MockBusinessService mockBusinessService;

    @Override
    public void run(String... args) throws Exception {
        mockBusinessService.saveOrder();
    }
}
