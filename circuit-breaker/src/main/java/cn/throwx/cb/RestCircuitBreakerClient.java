package cn.throwx.cb;

/**
 * @author throwable
 * @version v1
 * @description
 * @since 2020/10/24 22:20
 */
public class RestCircuitBreakerClient {

    public static void main(String[] args) throws Exception {
        Service service = new Service();
        RestCircuitBreaker cb = new RestCircuitBreaker(5, 500);
        for (int i = 0; i < 10; i++) {
            int temp = i;
            String result = cb.call(() -> service.process(temp));
            System.out.println(String.format("返回结果:%s,number:%d", result, temp));
        }
        Thread.sleep(501L);
        cb.call(service::processSuccess);
        for (int i = 0; i < 3; i++) {
            int temp = i;
            String result = cb.call(() -> service.process(temp));
            System.out.println(String.format("返回结果:%s,number:%d", result, temp));
        }
    }

    public static class Service {

        public String process(int i) {
            System.out.println("进入process方法,number:" + i);
            throw new RuntimeException(String.valueOf(i));
        }

        public void processSuccess() {
            System.out.println("调用processSuccess方法");
        }
    }
}
