package com.atguigu.gulimall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class GulimallCorsConfiguration {
    @Bean
    public CorsWebFilter corsWebFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 允许跨域的请求头
        corsConfiguration.addAllowedHeader("*");
        // 允许跨域请求方式
        corsConfiguration.addAllowedMethod("*");
        // 允许跨域请求来源
        corsConfiguration.addAllowedOrigin("*");
        // 是否允许携带Cookie跨域
        corsConfiguration.setAllowCredentials(true);
        // 任意url都要进行跨域配置，两个*是可以匹配包含0到多个1的路径
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsWebFilter(source);

    }
}
