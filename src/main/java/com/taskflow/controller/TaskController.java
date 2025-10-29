package com.taskflow.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskflow.model.Task;
import com.taskflow.service.GeminiService;
import com.taskflow.service.TaskService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final GeminiService geminiService;

    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return taskService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Task crateTask(@RequestBody Task task) {
        return taskService.save(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task) {
        try {
            return ResponseEntity.ok(taskService.update(id, task));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/ai")
    public Map<String, String> askGeminiForTask(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        String prompt = body.get("prompt");
        String apiKey = body.get("apiKey");

        if (prompt == null || prompt.isEmpty() || apiKey == null || apiKey.isEmpty()) {
            throw new IllegalArgumentException("Os campos 'prompt' e 'apiKey' são obrigatórios");
        }

        Task task = taskService.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));

        // Concatenar contexto da tarefa + prompt
        String fullPrompt = task.getTitle() + "\n" + task.getDescription() + "\n\n" + prompt;

        // Chamada para a IA usando a API Key recebida
        String responseText = geminiService.askGemini(fullPrompt, apiKey);

        return Map.of("response", responseText);
    }
}
