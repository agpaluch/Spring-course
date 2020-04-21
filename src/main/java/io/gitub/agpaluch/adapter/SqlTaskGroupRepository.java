package io.gitub.agpaluch.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SqlTaskGroupRepository extends TaskGroupRepository, JpaRepository<TaskGroup, Integer> {
}
