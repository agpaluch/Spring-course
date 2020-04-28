package io.gitub.agpaluch.service;

import io.gitub.agpaluch.model.TaskGroup;
import io.gitub.agpaluch.model.TaskGroupRepository;
import io.gitub.agpaluch.model.TaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TaskGroupServiceTest {

    @Test
    @DisplayName("Should throw IllegalStateException when group has undone tasks.")
    void toggleGroup_existsUndoneTaskForGivenGroup_throwsIllegalStateException() {
        //given
        TaskRepository mockTaskRepository = taskRepositoryReturning(true);
        //system under test
        TaskGroupService toTest = new TaskGroupService(null, mockTaskRepository);

        //when
        Throwable exception = catchThrowable(() -> toTest.toggleGroup(1));

        //then
        assertThat(exception)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("undone tasks");

    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when task group with given id is not found.")
    void toggleGroup_taskGroupForGivenIdNotExisting_throwsIllegalArgumentException() {
        //given
        TaskRepository mockTaskRepository = taskRepositoryReturning(false);
        //and
        TaskGroupRepository mockTaskGroupRepository = mock(TaskGroupRepository.class);
        when(mockTaskGroupRepository.findById(anyInt())).thenReturn(Optional.empty());
        //system under test
        TaskGroupService toTest = new TaskGroupService(mockTaskGroupRepository, mockTaskRepository);

        //when
        Throwable exception = catchThrowable(() -> toTest.toggleGroup(1));

        //then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id not found");
    }


    @Test
    @DisplayName("Should toggle group done status")
    void toggleGroup_noUndoneTasks_in_existingTaskGroupWithAGivenId_togglesGroupDoneStatus() {
        //given
        TaskRepository mockTaskRepository = taskRepositoryReturning(false);
        //and
        TaskGroup taskGroup = new TaskGroup();
        boolean beforeToggle = taskGroup.isDone();
        TaskGroupRepository mockTaskGroupRepository = mock(TaskGroupRepository.class);
        when(mockTaskGroupRepository.findById(anyInt())).thenReturn(Optional.of(taskGroup));
        //system under test
        TaskGroupService toTest = new TaskGroupService(mockTaskGroupRepository, mockTaskRepository);

        //when
        toTest.toggleGroup(1);

        //then
        assertThat(taskGroup.isDone()).isEqualTo(!beforeToggle);
    }


    private TaskRepository taskRepositoryReturning(final boolean result) {
        TaskRepository taskRepository = mock(TaskRepository.class);
        when(taskRepository.existsByDoneIsFalseAndGroup_Id((anyInt()))).thenReturn(result);
        return taskRepository;
    }
}