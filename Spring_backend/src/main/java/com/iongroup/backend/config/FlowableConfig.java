// package com.iongroup.backend.config;

// import org.flowable.engine.ProcessEngine;
// import org.flowable.engine.ProcessEngineConfiguration;
// import org.flowable.spring.SpringProcessEngineConfiguration;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// import javax.sql.DataSource;

// @Configuration
// public class FlowableConfig {

//     @Bean
//     public ProcessEngine processEngine(DataSource dataSource) {

//         SpringProcessEngineConfiguration cfg =
//                 new SpringProcessEngineConfiguration();

//         cfg.setDataSource(dataSource);
//         cfg.setDatabaseSchemaUpdate(
//                 ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE
//         );
//         cfg.setAsyncExecutorActivate(false);

//         return cfg.buildProcessEngine();
//     }
// }
