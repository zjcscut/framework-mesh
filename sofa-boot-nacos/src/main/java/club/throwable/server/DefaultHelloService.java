package club.throwable.server;

import club.throwable.contract.HelloService;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import org.springframework.stereotype.Service;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2019/12/31 14:51
 */
@Service
@SofaService(
        interfaceType = HelloService.class, bindings = {
        @SofaServiceBinding(bindingType = "bolt"),
        @SofaServiceBinding(bindingType = "rest")
})
public class DefaultHelloService implements HelloService {

    @Override
    public String sayHello(String name) {
        return String.format("%s say hello!", name);
    }
}
