package io.gitub.agpaluch.controller;

import io.gitub.agpaluch.model.Task;
import io.gitub.agpaluch.model.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    TaskRepository repo;

    @Test
    void httpGet_returnsAllTasks() {
        //given
        int initial = repo.findAll().size();
        repo.save(new Task("foo", LocalDateTime.now()));
        repo.save(new Task("bar", LocalDateTime.now()));

        //when
        Task[] result = restTemplate.getForObject("http://localhost:" + port + "/tasks", Task[].class);

        //then
        assertThat(result).hasSize(initial + 2);

    }


    @Test
    void httpGet_returnsGivenTask() {

        //given
        Task savedTask = repo.save(new Task("foo", LocalDateTime.now()));
        int id = savedTask.getId();


        //when
        String path = "http://localhost:" + port + "/tasks/" + id;

        ResponseEntity<Task> entity = restTemplate.getForEntity(URI.create(path), Task.class);
        Task result = entity.getBody();
        HttpStatus status = entity.getStatusCode();

        //then
        assertThat(result).hasFieldOrPropertyWithValue("deadline", savedTask.getDeadline())
                .hasFieldOrPropertyWithValue("description", savedTask.getDescription())
                .hasFieldOrPropertyWithValue("done", savedTask.isDone());

        assertThat(status.is2xxSuccessful());

    }

}