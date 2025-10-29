package com.taskflow.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
    public ResponseEntity<Map<String, String>> askGeminiForTask(
            @PathVariable Long id,
            @RequestBody Map<String, String> requestBody) {

        String apiKey = requestBody.get("apiKey");
        String prompt = requestBody.get("prompt");

        if (apiKey == null || prompt == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "api e prompt são obrigatórios");
        }

        Task task = taskService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        String fullPrompt = String.format("""
        Você é um assistente especializado em produtividade e gestão de tarefas.
        
        A seguir está uma tarefa cadastrada pelo usuário:
        - Título: %s
        - Descrição: %s
        
        Pergunta do usuário:
        "%s"
        
        Gere uma resposta **específica para o contexto dessa tarefa**, evitando repetições e generalidades.
        A resposta deve:
        - Conter no máximo 10 a 15 linhas;
        - Ser objetiva e prática;
        - Trazer insights aplicáveis (ex.: próximos passos, ideias concretas ou possíveis soluções);
        - Usar linguagem clara e direta.
        
        Se a pergunta for vaga, deduza a intenção mais provável e dê uma resposta útil.
        """, task.getTitle(), task.getDescription(), prompt);


        String response = geminiService.askGemini(fullPrompt, apiKey);

        return ResponseEntity.ok(
            java.util.Collections.singletonMap("response", response != null ? response : "")
        );

    }


}
