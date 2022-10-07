package cn.vlts.tomcat;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.ProtocolHandler;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

/**
 * @author throwable
 * @version v1
 * @description 嵌入式Tomcat使用虚拟线程
 * @since 2022/10/7 21:41
 */
public class EmbedTomcatVirtualThreadDemo {

    private static final String SERVLET_NAME = "VirtualThreadHandleServlet";

    private static final String SERVLET_PATH = "/*";

    /**
     * 设置VM参数:
     * --add-opens java.base/java.lang=ALL-UNNAMED
     * --add-opens java.base/java.lang.reflect=ALL-UNNAMED
     * --add-opens java.base/java.util.concurrent=ALL-UNNAMED
     * -Djdk.tracePinnedThreads=full
     *
     * @param args args
     * @throws Exception e
     */
    public static void main(String[] args) throws Throwable {
        String pinMode = System.getProperty("jdk.tracePinnedThreads");
        System.out.println("pin mode = " + pinMode);
        Tomcat tomcat = new Tomcat();
        Context context = tomcat.addContext("", (new File(".")).getAbsolutePath());
        Tomcat.addServlet(context, SERVLET_NAME, new VirtualThreadHandleServlet());
        context.addServletMappingDecoded(SERVLET_PATH, SERVLET_NAME);
        Connector connector = new Connector();
        ProtocolHandler protocolHandler = connector.getProtocolHandler();
        if (protocolHandler instanceof AbstractProtocol<?> protocol) {
            protocol.setAddress(InetAddress.getByName("127.0.0.1"));
            protocol.setPort(9091);
            ThreadFactory factory = Thread.ofVirtual().name("embed-tomcat-virtualWorker-", 0).factory();
            Class<?> klass = Class.forName("java.util.concurrent.ThreadPerTaskExecutor");
            MethodHandle methodHandle = MethodHandles.privateLookupIn(klass, MethodHandles.lookup())
                    .findStatic(klass, "create", MethodType.methodType(klass, new Class[]{ThreadFactory.class}));
            ExecutorService executor = (ExecutorService) methodHandle.invoke(factory);
            protocol.setExecutor(executor);
        }
        tomcat.getService().addConnector(connector);
        tomcat.start();
    }
}
