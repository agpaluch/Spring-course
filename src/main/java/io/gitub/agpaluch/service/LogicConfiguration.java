package io.gitub.agpaluch.service;

import io.gitub.agpaluch.TaskConfigurationProperties;
import io.gitub.agpaluch.model.ProjectRepository;
import io.gitub.agpaluch.model.TaskGroupRepository;
import io.gitub.agpaluch.model.TaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class LogicConfiguration {

    @Bean
    ProjectService projectService(final ProjectRepository projectRepository,
                           final TaskGroupRepository taskGroupRepository,
                           final TaskGroupService taskGroupService,
                           final TaskConfigurationProperties config){
        return new ProjectService(projectRepository, taskGroupRepository, taskGroupService, config);
    }

    @Bean
    TaskGroupService taskGroupService(final TaskGroupRepository repository,
                                      final TaskRepository taskRepository){
        return new TaskGroupService(repository, taskRepository);
    }

}
