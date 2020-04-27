package io.gitub.agpaluch.adapter;

import io.gitub.agpaluch.model.Project;
import io.gitub.agpaluch.model.ProjectRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface SqlProjectRepository extends ProjectRepository, JpaRepository<Project, Integer> {

    @Override
    @Query("select distinct p from Project p left join fetch p.steps")
    List<Project> findAll();
}
