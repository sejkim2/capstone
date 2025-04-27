package com.example.app.frame;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /images/** → /home/ubuntu/frames 매핑
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:/home/ubuntu/frames/");
    }
}
