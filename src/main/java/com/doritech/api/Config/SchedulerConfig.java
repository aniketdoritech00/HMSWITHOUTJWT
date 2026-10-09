package com.doritech.api.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class SchedulerConfig {

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(2);
        scheduler.setThreadNamePrefix("EscalationScheduler-");
        scheduler.initialize();
        return scheduler;
    }
}
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
//
//@Configuration
//public class SchedulerConfig {
//
//    @Bean(name = "broadcastTaskScheduler")
//    public ThreadPoolTaskScheduler broadcastTaskScheduler() {
//        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
//        scheduler.setPoolSize(2); // one active + one spare thread
//        scheduler.setThreadNamePrefix("BroadcastScheduler-");
//        scheduler.setRemoveOnCancelPolicy(true);
//        scheduler.setErrorHandler(Throwable::printStackTrace); // optional
//        scheduler.initialize();
//        return scheduler;
//    }
//}
