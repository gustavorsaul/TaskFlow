package com.taskflow.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.taskflow.model.Task;
import com.taskflow.repository.TaskRepository;

@Service
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public List<Task> findAll() {
        return repository.findAll();
    }

    public Optional<Task> findById(Long id) {
        return repository.findById(id);
    }

    public Task save(Task task) {
        return repository.save(task);
    }

    public Task update(Long id, Task updatedTask) {
        return repository.findById(id).map(task -> {
            task.setTitle(updatedTask.getTitle());
            task.setDescription(updatedTask.getDescription());
            task.setTimeEstimated(updatedTask.getTimeEstimated());
            task.setCompleted(updatedTask.isCompleted());
            return repository.save(task);
        }).orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
