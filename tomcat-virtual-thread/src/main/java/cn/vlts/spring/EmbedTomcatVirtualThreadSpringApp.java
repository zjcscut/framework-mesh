package cn.vlts.spring;

import jakarta.servlet.Servlet;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.UpgradeProtocol;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author throwable
 * @version v1
 * @description pending springboot upgrade
 * @since 2022/10/7 23:24
 */
@SpringBootApplication
public class EmbedTomcatVirtualThreadSpringApp {

    public static void main(String[] args) {
        SpringApplication.run(EmbedTomcatVirtualThreadSpringApp.class, args);
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({Servlet.class, Tomcat.class, UpgradeProtocol.class})
    @ConditionalOnMissingBean(value = ServletWebServerFactory.class, search = SearchStrategy.CURRENT)
    static class EmbeddedTomcat {

        @Bean
        ServletWebServerFactory tomcatServletWebServerFactory() {

            return null;
        }
    }
}
