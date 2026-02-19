package com.iongroup.backend.config;

import com.iongroup.library.flowable_service.FlowableProcessService;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LibraryConfig {

    @Bean
    public FlowableProcessService flowableProcessService(
            RuntimeService runtimeService,
            TaskService taskService,
            HistoryService historyService) {
        return new FlowableProcessService(runtimeService, taskService, historyService);
    }
}