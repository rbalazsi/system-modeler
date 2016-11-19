package com.robertbalazsi.systemmodeler.app;

import com.robertbalazsi.systemmodeler.global.ChangeManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration class for {@link SystemModelerApp}.
 */
@Configuration
@ComponentScan(basePackages = {"com.robertbalazsi.systemmodeler"})
public class SystemModelerConfig {

    @Bean
    public ChangeManager changeManager() {
        return new ChangeManager();
    }
}
