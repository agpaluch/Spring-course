package io.gitub.agpaluch.controller;

import io.gitub.agpaluch.model.Task;
import io.gitub.agpaluch.model.TaskRepository;
import io.gitub.agpaluch.model.projection.GroupReadModel;
import io.gitub.agpaluch.model.projection.GroupWriteModel;
import io.gitub.agpaluch.service.TaskGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/groups")
public class TaskGroupController {

    public static final Logger logger = LoggerFactory.getLogger(TaskGroupController.class);
    private final TaskGroupService service;
    private final TaskRepository repository;

    TaskGroupController(final TaskGroupService service, final TaskRepository repository) {
        this.service = service;
        this.repository = repository;
    }


    @GetMapping
    public ResponseEntity<List<GroupReadModel>> readAllGroups(){
        return ResponseEntity.ok(service.readAll());
    }


    @GetMapping("/{id}/tasks")
    public ResponseEntity<List<Task>> readAllTasksFromGroup(@PathVariable int id){
        if (!repository.existsById(id)){
            ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(repository.findAllByGroup_Id(id));
    }


    @PostMapping
    public ResponseEntity<GroupReadModel> createGroup(@RequestBody GroupWriteModel toCreate){
        GroupReadModel createdGroup = service.createGroup(toCreate);
        return ResponseEntity.created(URI.create("/"+createdGroup.getId())).body(createdGroup);
    }


    @Transactional
    @PatchMapping("/{id}")
    public ResponseEntity<?> toggleGroup(@PathVariable int id){
        service.toggleGroup(id);
        return ResponseEntity.noContent().build();
    }


    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e){
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(IllegalStateException.class)
    ResponseEntity<String> handleIllegalState(IllegalStateException e){
            return ResponseEntity.badRequest().body(e.getMessage());
    }


}
