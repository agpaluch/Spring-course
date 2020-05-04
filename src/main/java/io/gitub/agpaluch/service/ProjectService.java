package io.gitub.agpaluch.service;

import io.gitub.agpaluch.TaskConfigurationProperties;
import io.gitub.agpaluch.model.*;
import io.gitub.agpaluch.model.projection.GroupReadModel;
import io.gitub.agpaluch.model.projection.GroupTaskWriteModel;
import io.gitub.agpaluch.model.projection.GroupWriteModel;
import io.gitub.agpaluch.model.projection.ProjectWriteModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


public class ProjectService {
    private ProjectRepository projectRepository;
    private TaskGroupRepository taskGroupRepository;
    private TaskGroupService taskGroupService;
    private TaskConfigurationProperties config;

    ProjectService(final ProjectRepository projectRepository, final TaskGroupRepository taskGroupRepository, final TaskGroupService taskGroupService, final TaskConfigurationProperties config) {
        this.projectRepository = projectRepository;
        this.taskGroupRepository = taskGroupRepository;
        this.taskGroupService = taskGroupService;
        this.config = config;
    }


    List<Project> readAll() {
        return projectRepository.findAll();
    }


    public Project save(ProjectWriteModel toSave) {
        return projectRepository.save(toSave.toProject());
    }


    GroupReadModel createGroup(LocalDateTime deadline, int projectId) {
        if (!config.getTemplate().isAllowMultipleTasks() && taskGroupRepository.existsByDoneIsFalseAndProject_Id(projectId)) {
            throw new IllegalStateException("Only one undone group from project is allowed.");
        }


        return projectRepository.findById(projectId)
                .map(project -> {
                    GroupWriteModel targetGroup = new GroupWriteModel();
                    targetGroup.setDescription(project.getDescription());
                    targetGroup.setTasks(
                            project.getSteps().stream()
                                    .map(projectStep -> {
                                        GroupTaskWriteModel task = new GroupTaskWriteModel();
                                        task.setDescription(projectStep.getDescription());
                                        task.setDeadline(deadline.plusDays(projectStep.getDaysToDeadline()));
                                        return task;
                                            }).collect(Collectors.toSet()));
                            return taskGroupService.createGroup(targetGroup, project);
                }).orElseThrow(() -> new IllegalArgumentException("Project with given id not found"));


    }
}
