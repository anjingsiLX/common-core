package top.doudou.commons.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.Serializable;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 傻男人
 * @date 2020-07-30 15:48
 * @describe 线程池配置
 */
@Slf4j
@Configuration
public class ExecutorConfig implements Serializable {

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean("executorService")
    public TaskExecutor executorService(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(500);
        executor.setKeepAliveSeconds(10);
        executor.setThreadNamePrefix("zhcf-carloan-"+applicationName+"-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
