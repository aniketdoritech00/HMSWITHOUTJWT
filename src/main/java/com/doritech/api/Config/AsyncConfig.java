//package com.doritech.api.Config;
//
//import java.util.concurrent.Executor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//
//@Configuration
//@EnableAsync(proxyTargetClass = true)  // ✅ use CGLIB (class-based) proxy
//public class AsyncConfig {
//
//    @Bean(name = "broadcastExecutor")
//    public Executor broadcastExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(2);
//        executor.setMaxPoolSize(5);
//        executor.setQueueCapacity(10);
//        executor.setThreadNamePrefix("BroadcastAsync-");
//        executor.initialize();
//        return executor;
//    }
//}
