package io.gitub.agpaluch.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.gitub.agpaluch.model.Task;
import io.gitub.agpaluch.model.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration")
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository repo;

    @Test
    void httpGet_returnsGivenTask() throws Exception {
        //given
        int id = repo.save(new Task("foo", LocalDateTime.now())).getId();

        //when + then
        mockMvc.perform(get("/tasks/" + id))
                .andExpect(status().is2xxSuccessful());
    }


    @Test
    void httpPost_createsTask() throws Exception {
        //given
        Task toCreate = new Task("foo", LocalDateTime.now());
        ObjectMapper mapper = getObjectMapperWithCorrectDateSerialization();


        //when + then
        mockMvc.perform(MockMvcRequestBuilders.post("/tasks")
                .content(mapper.writeValueAsString(toCreate))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description", is("foo")))
                .andExpect(content().json("{\"deadline\":\"" + toCreate.getDeadline() + "\"}"));

    }


    @Test
    void httpPut_updatesTask() throws Exception {
        //given
        Task taskInRepo = repo.save(new Task("foo", LocalDateTime.now()));
        int idToUpdate = taskInRepo.getId();
        String newDescription = "bar";
        Task toUpdate = new Task(newDescription, LocalDateTime.now());
        ObjectMapper mapper = getObjectMapperWithCorrectDateSerialization();

        //when + then
        mockMvc.perform(MockMvcRequestBuilders.put("/tasks/" + idToUpdate)
                .content(mapper.writeValueAsString(toUpdate))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        Task updatedTaskFromRepo = repo.findById(idToUpdate).get();

        assertThat(updatedTaskFromRepo.getDescription().equals(newDescription));
        assertThat(updatedTaskFromRepo.getDeadline().equals(toUpdate.getDeadline()));
    }

    @Test
    void httpPut_updatingNonExistingTask_returnsStatusNotFound() throws Exception {
        //given
        int maximumId = repo.findAll().stream()
                .mapToInt(Task::getId)
                .max()
                .orElse(repo.save(new Task("foo", LocalDateTime.now())).getId());
        int idToUpdate = maximumId + 1;

        String newDescription = "bar";
        Task toUpdate = new Task(newDescription, LocalDateTime.now());
        ObjectMapper mapper = getObjectMapperWithCorrectDateSerialization();

        //when + then
        mockMvc.perform(MockMvcRequestBuilders.put("/tasks/" + idToUpdate)
                .content(mapper.writeValueAsString(toUpdate))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }


    private ObjectMapper getObjectMapperWithCorrectDateSerialization() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

}
