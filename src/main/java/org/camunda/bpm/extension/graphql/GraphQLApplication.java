package org.camunda.bpm.extension.graphql;

import com.coxautodev.graphql.tools.GraphQLResolver;
import com.coxautodev.graphql.tools.SchemaParser;
import graphql.execution.ExecutionStrategy;
import graphql.execution.SimpleExecutionStrategy;
import graphql.schema.GraphQLSchema;
import graphql.servlet.SimpleGraphQLServlet;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.GroupEntity;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.impl.persistence.entity.UserEntity;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.extension.graphql.types.KeyValuePair;
import org.camunda.bpm.extension.graphql.types.ValueTypeEnum;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
@EnableProcessApplication
public class GraphQLApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraphQLApplication.class, args);
    }

    @Autowired
    private List<GraphQLResolver<?>> resolvers;

    @Bean
    public GraphQLSchema graphQLSchema() {
        return SchemaParser.newParser()
                .file("camunda.graphqls")
                .file("KeyValue.graphqls")
                .file("Execution.graphqls")
                .file("Task.graphqls")
                .file("User.graphqls")
                .file("Group.graphqls")
                .file("ProcessDefinition.graphqls")
                .resolvers(resolvers)
                .dictionary(
                        Task.class,
                        TaskEntity.class,
                        ProcessInstance.class,
                        ProcessDefinition.class,
                        ProcessDefinitionEntity.class,
                        ExecutionEntity.class,
                        KeyValuePair.class,
                        ValueTypeEnum.class,
                        User.class,
                        UserEntity.class,
                        Group.class,
                        GroupEntity.class
                )
                .build()
                .makeExecutableSchema();
    }

    @Bean
    public ExecutionStrategy executionStrategy() {
        return new SimpleExecutionStrategy();
    }

    @Bean
    public ServletRegistrationBean graphQLServletRegistrationBean(GraphQLSchema schema, ExecutionStrategy executionStrategy) {
        return new ServletRegistrationBean(new SimpleGraphQLServlet(schema, executionStrategy), "/graphql");
    }
}
