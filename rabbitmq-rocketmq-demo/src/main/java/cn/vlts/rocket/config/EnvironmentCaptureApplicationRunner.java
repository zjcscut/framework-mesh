package cn.vlts.rocket.config;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.cloud.bootstrap.BootstrapApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author throwable
 * @version v1
 * @description
 * @since 2023/1/17 16:05
 */
@Component
public class EnvironmentCaptureApplicationRunner implements CommandLineRunner {

    @Autowired
    private ConfigurableApplicationContext parentContext;

    @Configuration(proxyBeanMethods = false)
    public static class None {

    }

    @Override
    public void run(String... args) throws Exception {
        StandardEnvironment environment = copyEnvironment(parentContext.getEnvironment());
        ConfigurableApplicationContext childContext;
        SpringApplicationBuilder builder = new SpringApplicationBuilder(None.class)
                .bannerMode(Banner.Mode.OFF)
                .web(WebApplicationType.NONE)
                .environment(environment);
        builder.application().setListeners(Lists.newArrayList(
                new BootstrapApplicationListener(),
                new ConfigFileApplicationListener())
        );
        childContext = builder.run();
        if (environment.getPropertySources().contains(REFRESH_ARGS_PROPERTY_SOURCE)) {
            environment.getPropertySources().remove(REFRESH_ARGS_PROPERTY_SOURCE);
        }
        MutablePropertySources reloadPropertySources = environment.getPropertySources();
        MutablePropertySources targetPropertySources = this.parentContext.getEnvironment().getPropertySources();
        // copy option from reloadPropertySources to targetPropertySources
        ConfigurableApplicationContext closeable = childContext;
        while (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                // Ignore;
            }
            if (closeable.getParent() instanceof ConfigurableApplicationContext) {
                closeable = (ConfigurableApplicationContext) closeable.getParent();
            } else {
                break;
            }
        }
    }

    private StandardEnvironment copyEnvironment(ConfigurableEnvironment input) {
        StandardEnvironment environment = new StandardEnvironment();
        MutablePropertySources capturedPropertySources = environment.getPropertySources();
        // Only copy the default property source(s) and the profiles over from the main
        // environment (everything else should be pristine, just like it was on startup).
        for (String name : DEFAULT_PROPERTY_SOURCES) {
            if (input.getPropertySources().contains(name)) {
                PropertySource<?> propertySource = input.getPropertySources().get(name);
                if (Objects.nonNull(propertySource)) {
                    if (capturedPropertySources.contains(name)) {
                        capturedPropertySources.replace(name, propertySource);
                    } else {
                        capturedPropertySources.addLast(propertySource);
                    }
                }
            }
        }
        environment.setActiveProfiles(input.getActiveProfiles());
        environment.setDefaultProfiles(input.getDefaultProfiles());
        Map<String, Object> map = new HashMap<>();
        map.put("spring.jmx.enabled", false);
        map.put("spring.main.sources", "");
        capturedPropertySources.addFirst(new MapPropertySource(REFRESH_ARGS_PROPERTY_SOURCE, map));
        return environment;
    }

    private static final String REFRESH_ARGS_PROPERTY_SOURCE = "refreshArgs";

    private static final String[] DEFAULT_PROPERTY_SOURCES = new String[]{
            // order matters, if cli args aren't first, things get messy
            CommandLinePropertySource.COMMAND_LINE_PROPERTY_SOURCE_NAME,
            "defaultProperties"};
}
