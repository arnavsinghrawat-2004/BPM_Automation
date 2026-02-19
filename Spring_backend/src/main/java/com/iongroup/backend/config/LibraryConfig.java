package com.iongroup.backend.config;

import com.iongroup.library.flowable_service.FlowableProcessService;
import com.iongroup.library.flowable_service.FlowableRuntimeService;
import com.iongroup.library.flowable_service.UiJsonToBpmnService;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class LibraryConfig {

    @Bean
    public FlowableRuntimeService flowableRuntimeService(
            RepositoryService repositoryService,
            RuntimeService runtimeService,
            TaskService taskService,
            HistoryService historyService) {
        return new FlowableRuntimeService(repositoryService, runtimeService, taskService, historyService);
    }

    @Bean
    public FlowableProcessService flowableProcessService(
            RuntimeService runtimeService,
            TaskService taskService,
            HistoryService historyService) {
        return new FlowableProcessService(runtimeService, taskService, historyService);
    }

    @Bean
    public UiJsonToBpmnService uiJsonToBpmnService() {
        return new UiJsonToBpmnService();
    }
}