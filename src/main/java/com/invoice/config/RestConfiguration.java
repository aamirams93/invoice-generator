package com.invoice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;


@Configuration
public class RestConfiguration implements RepositoryRestConfigurer {

    
    public void configureRepositoryRestConfiguration(
            RepositoryRestConfiguration config) {

        config.disableDefaultExposure();
    }
}
