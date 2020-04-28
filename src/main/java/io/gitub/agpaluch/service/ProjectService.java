package io.gitub.agpaluch.service;

import io.gitub.agpaluch.TaskConfigurationProperties;
import io.gitub.agpaluch.model.*;
import io.gitub.agpaluch.model.projection.GroupReadModel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {
    private ProjectRepository projectRepository;
    private TaskGroupRepository taskGroupRepository;
    private TaskConfigurationProperties config;

    ProjectService(final ProjectRepository projectRepository, final TaskGroupRepository taskGroupRepository, final TaskConfigurationProperties config) {
        this.projectRepository = projectRepository;
        this.taskGroupRepository = taskGroupRepository;
        this.config = config;
    }


    List<Project> readAll() {
        return projectRepository.findAll();
    }


    Project save(Project toSave) {
        return projectRepository.save(toSave);
    }


    GroupReadModel createGroup(LocalDateTime deadline, int projectId) {
        if (!config.getTemplate().isAllowMultipleTasks() && taskGroupRepository.existsByDoneIsFalseAndProject_Id(projectId)) {
            throw new IllegalStateException("Only one undone group from project is allowed.");
        }

        TaskGroup taskGroup = projectRepository.findById(projectId)
                .map(project -> {
                    TaskGroup result = new TaskGroup();
                    result.setDescription(project.getDescription());
                    result.setDone(false);
                    result.setTasks(
                            project.getSteps().stream()
                                    .map(projectStep -> new Task(
                                            projectStep.getDescription(), deadline.plusDays(projectStep.getDaysToDeadline()))
                                    )   .collect(Collectors.toSet()));
                    result.setProject(project);
                    return taskGroupRepository.save(result);
                }).orElseThrow(() -> new IllegalArgumentException("Project with given id not found"));


        return new GroupReadModel(taskGroup);

    }
}
