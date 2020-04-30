package io.gitub.agpaluch.service;

import io.gitub.agpaluch.model.Task;
import io.gitub.agpaluch.model.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class TaskService {
    public static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository repository;

    TaskService(final TaskRepository taskRepository) {
        this.repository = taskRepository;
    }

    @Async
    public CompletableFuture<List<Task>> findAllAsync(){
        logger.info("Async find!");
        return CompletableFuture.supplyAsync(repository::findAll);

    }
}
