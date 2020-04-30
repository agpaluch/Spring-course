package io.gitub.agpaluch;

import io.gitub.agpaluch.model.Task;
import io.gitub.agpaluch.model.TaskGroup;
import io.gitub.agpaluch.model.TaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;

@Configuration
class TestConfiguration {

    @Bean
    @Primary
    @Profile("!integration")
    DataSource e2eTestDataSource(){
        DriverManagerDataSource result = new DriverManagerDataSource("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "");
        result.setDriverClassName("org.h2.Driver");
        return result;
    }

    @Bean
    @Primary
    @Profile("integration")
    TaskRepository testRepo() {
        return new TaskRepository() {
            private Map<Integer, Task> tasks = new HashMap<>();

            @Override
            public List<Task> findAll() {
                return new ArrayList<>(tasks.values());
            }

            @Override
            public Page<Task> findAll(final Pageable page) {
                return null;
            }

            @Override
            public boolean existsById(final Integer id) {
                return tasks.containsKey(id);
            }

            @Override
            public Optional<Task> findById(final Integer id) {
                return Optional.ofNullable(tasks.get(id));
            }

            @Override
            public Task save(final Task entity) {
                int key = tasks.size() + 1;
                try {
                    Field field = field = Task.class.getDeclaredField("id");
                    field.setAccessible(true);
                    field.set(entity, key);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                tasks.put(key, entity);
                return tasks.get(key);
            }

            @Override
            public List<Task> findByDone(final boolean done) {
                return null;
            }

            @Override
            public boolean existsByDoneIsFalseAndGroup_Id(final Integer groupId) {
                return false;
            }

            @Override
            public List<Task> findAllByGroup_Id(final int id) {
                return List.of();
            }

            @Override
            public List<Task> findAllByDoneIsFalseAndDeadlineIsNullOrDeadlineIsLessThanEqual(final LocalDateTime today) {
                return null;
            }
        };
    }
}
