package io.gitub.agpaluch.service;

import io.gitub.agpaluch.TaskConfigurationProperties;
import io.gitub.agpaluch.TaskConfigurationProperties.Template;
import io.gitub.agpaluch.model.*;
import io.gitub.agpaluch.model.projection.GroupReadModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProjectServiceTest {

    @Test
    @DisplayName("Should throw IllegalStateException when configured to allow" +
            "just 1 group and the other undone group exists.")
    void createGroup_noMulitpleGroupsConfig_and_undoneGroupExists_throwsIllegalStateException() {
        //given
        TaskGroupRepository mockGroupRepository = groupRepositoryReturning(true);

        //and
        TaskConfigurationProperties mockConfig = configurationReturning(false);

        //system under test
        ProjectService toTest = new ProjectService(null, mockGroupRepository, mockConfig);

        //when
        Throwable exception = catchThrowable(() -> toTest.createGroup(LocalDateTime.now(), 0));

        // then
        assertThat(exception)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("one undone group");
    }


    @Test
    @DisplayName("Should throw IllegalArgumentException when configuration ok and no project for a given id.")
    void createGroup_configurationOk_and_noProjects_throwsIllegalArgumentException() {
        //given
        ProjectRepository mockRepository = mock(ProjectRepository.class);
        when(mockRepository.findById(anyInt())).thenReturn(Optional.empty());
        //and
        TaskConfigurationProperties mockConfig = configurationReturning(true);

        //system under test
        ProjectService toTest = new ProjectService(mockRepository, null, mockConfig);

        //when
        Throwable exception = catchThrowable(() -> toTest.createGroup(LocalDateTime.now(), 0));

        // then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id not found");
    }


    @Test
    @DisplayName("Should throw IllegalArgumentException when configured to allow just 1 group and no groups and no project for a given id.")
    void createGroup_noMulitpleGroupsConfig_and_noUndoneGroupExists_and_noProjects_throwsIllegalArgumentException() {
        //given
        ProjectRepository mockRepository = mock(ProjectRepository.class);
        when(mockRepository.findById(anyInt())).thenReturn(Optional.empty());
        //and
        TaskGroupRepository mockGroupRepository = groupRepositoryReturning(false);

        //and
        TaskConfigurationProperties mockConfig = configurationReturning(false);

        //system under test
        ProjectService toTest = new ProjectService(mockRepository, mockGroupRepository, mockConfig);

        //when
        Throwable exception = catchThrowable(() -> toTest.createGroup(LocalDateTime.now(), 0));

        // then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id not found");
    }


    @Test
    @DisplayName("Should create a new group from project")
    void createGroup_configOk_existingProject_createsAndSavesGroup() {
        //given
        LocalDateTime today = LocalDate.now().atStartOfDay();
        //and
        Project project = projectWith("bar", Set.of(-1, -2));
        ProjectRepository mockRepository = mock(ProjectRepository.class);
        when(mockRepository.findById(anyInt()))
                .thenReturn(Optional.of(project));
        //and
        InMemoryGroupRepository inMemoryGroupRepo = inMemoryGroupRepository();
        int countBeforeCall = inMemoryGroupRepo.count();
        //and
        TaskConfigurationProperties mockConfig = configurationReturning(true);
        //system under test
        ProjectService toTest = new ProjectService(mockRepository, inMemoryGroupRepo, mockConfig);

        //when
        GroupReadModel result = toTest.createGroup(today, 1);

        //then
        assertThat(result.getDescription()).isEqualTo("bar");
        assertThat(result.getDeadline()).isEqualTo(today.minusDays(1));
        assertThat(result.getTasks()).allMatch(task -> task.getDescription().equals("foo"));
        assertThat(countBeforeCall + 1).isEqualTo(inMemoryGroupRepo.count());

    }

    private Project projectWith(String projectDescription, Set<Integer> daysToDeadline) {

        Set<ProjectStep> steps = daysToDeadline.stream()
                .map(days -> {
                    ProjectStep step = mock(ProjectStep.class);
                    when(step.getDescription()).thenReturn("foo");
                    when(step.getDaysToDeadline()).thenReturn(days);
                    return step;
                })
                .collect(Collectors.toSet());

        Project result = mock(Project.class);
        when(result.getDescription()).thenReturn(projectDescription);
        when(result.getSteps()).thenReturn(steps);
        return result;
    }


    private TaskGroupRepository groupRepositoryReturning(final boolean result) {
        TaskGroupRepository mockGroupRepository = mock(TaskGroupRepository.class);
        when(mockGroupRepository.existsByDoneIsFalseAndProject_Id(anyInt())).thenReturn(result);
        return mockGroupRepository;
    }


    private TaskConfigurationProperties configurationReturning(final boolean result) {
        Template mockTemplate = mock(Template.class);
        when(mockTemplate.isAllowMultipleTasks()).thenReturn(result);
        TaskConfigurationProperties mockConfig = mock(TaskConfigurationProperties.class);
        when(mockConfig.getTemplate()).thenReturn(mockTemplate);
        return mockConfig;
    }


    private InMemoryGroupRepository inMemoryGroupRepository() {
        return new InMemoryGroupRepository();
    }


    private static class InMemoryGroupRepository implements TaskGroupRepository {

        private int index = 0;
        private Map<Integer, TaskGroup> map = new HashMap<>();

        public int count() {
            return map.values().size();
        }

        @Override
        public List<TaskGroup> findAll() {
            return new ArrayList<>(map.values());
        }

        @Override
        public Optional<TaskGroup> findById(final Integer id) {
            return Optional.ofNullable(map.get(id));
        }

        @Override
        public TaskGroup save(final TaskGroup entity) {
            if (entity.getId() == 0) {
                try {
                    Field field = TaskGroup.class.getDeclaredField("id");
                    field.setAccessible(true);
                    field.set(entity, ++index);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            map.put(entity.getId(), entity);
            return entity;
        }

        @Override
        public boolean existsByDoneIsFalseAndProject_Id(final Integer projectId) {
            return map.values().stream()
                    .filter(group -> !group.isDone())
                    .anyMatch(group -> group.getProject() != null && group.getProject().getId() == projectId);

        }
    }
}

