package io.gitub.agpaluch.controller;


import io.gitub.agpaluch.model.Task;
import io.gitub.agpaluch.model.TaskRepository;
import io.gitub.agpaluch.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;


@RestController
@RequestMapping("/tasks")
class TaskController {
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    private final TaskRepository repository;
    private final TaskService service;

    TaskController(final TaskRepository repository, final TaskService service) {
        this.repository = repository;
        this.service = service;
    }


    @GetMapping(params = {"!sort", "!page", "!size"})
    CompletableFuture<ResponseEntity<List<Task>>> readAllTasks(){
        logger.warn("Exposing all the tasks!");
        return service.findAllAsync().thenApply(ResponseEntity::ok);
    }

    @GetMapping
    ResponseEntity<List<Task>> readAllTasks(Pageable page){
        logger.info("Custom pageable.");
        return ResponseEntity.ok(repository.findAll(page).getContent());
    }

    @GetMapping("/{id}")
    ResponseEntity<Task> readTask(@PathVariable int id){

        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }

    @GetMapping("/search/done")
    ResponseEntity<List<Task>> readDoneTasks(@RequestParam(defaultValue = "true") boolean state){
        return ResponseEntity.ok(
                repository.findByDone(state)
        );
    }


    @GetMapping("/search/today")
    ResponseEntity<List<Task>> readTasksForToday(){
        return ResponseEntity.ok(repository.findAllByDoneIsFalseAndDeadlineIsNullOrDeadlineIsLessThanEqual(LocalDate.now().atTime(LocalTime.MAX)));
    }


    @PostMapping
    ResponseEntity<Task> createTask(@RequestBody @Valid Task toCreate){

        Task savedTask = repository.save(toCreate);
        return ResponseEntity.created(URI.create("/"+savedTask.getId())).body(savedTask);

    }


    @PutMapping("/{id}")
    ResponseEntity<Task> updateTask(@PathVariable int id, @RequestBody @Valid Task toUpdate){
        if (!repository.existsById(id)){
            return ResponseEntity.notFound().build();
        }

        repository.findById(id)
                .ifPresent(task -> {
                    task.updateFrom(toUpdate);
                    repository.save(task);
                });

        return ResponseEntity.noContent().build();

    }

    @Transactional
    @PatchMapping("/{id}")
    public ResponseEntity<?> toggleTask(@PathVariable int id){
        if (!repository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        repository.findById(id)
                .ifPresent(t -> t.setDone(!t.isDone()));

        return ResponseEntity.noContent().build();

    }



}
