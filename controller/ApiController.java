package br.com.java_api.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import java.net.URI;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/tasks")
public class ApiController {

    private final List<Task> tasks = Collections.synchronizedList(new ArrayList<>());
    private final AtomicLong counter = new AtomicLong();

    // GET /tasks - Retorna a lista de tarefas.
    @GetMapping
    public ResponseEntity<List<Task>> listTasks() {
        return ResponseEntity.ok(tasks);
    }

    // POST /tasks - Cria uma nova tarefa e retorna a lista atualizada.
    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody TaskRequest taskRequest) {
        long newId = counter.incrementAndGet();
        Task newTask = new Task(newId, taskRequest.getText());
        tasks.add(newTask);

        // Cria a URI para o novo recurso criado (ex: /tasks/1)
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newTask.getId())
                .toUri();
        // Retorna 201 Created, com o cabeçalho Location e o novo objeto no corpo.
        return ResponseEntity.created(location).body(newTask);
    }

    // GET /tasks/{id} - Busca uma tarefa específica pelo ID.
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable long id) {
        return tasks.stream()
                .filter(task -> task.getId() == id)
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /tasks/{id} - Atualiza uma tarefa e retorna a lista atualizada.
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable long id,
            @Valid @RequestBody TaskRequest taskRequest) {
        return tasks.stream()
                .filter(task -> task.getId() == id)
                .findFirst()
                .map(task -> {
                    task.setText(taskRequest.getText());
                    return ResponseEntity.ok(task);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /tasks/{id} - Deleta uma tarefa e retorna a lista atualizada.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable long id) {
        boolean removed = tasks.removeIf(task -> task.getId() == id);
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

}
