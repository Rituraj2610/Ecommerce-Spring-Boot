//package com.rituraj.ecommerce.config;
//
//import com.rituraj.ecommerce.middleware.AdminInterceptor;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//
//    private final AdminInterceptor adminInterceptor;
//
//    public WebConfig(AdminInterceptor adminInterceptor) {
//        this.adminInterceptor = adminInterceptor;
//    }
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(adminInterceptor).addPathPatterns("/admin/**");
//    }
//}
