package com.example.tasktracker.controller;

import com.example.tasktracker.exception.TaskNotFoundException;
import com.example.tasktracker.model.Task;
import com.example.tasktracker.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public String listTasks(Model model) {
        model.addAttribute("tasks", taskService.findAll());
        return "task-list";
    }

    // Declared BEFORE /{id} for clarity; Spring prefers the literal match anyway
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("task", new Task());
        return "task-form";
    }

    @GetMapping("/{id}")
    public String viewTask(@PathVariable Long id, Model model) {
        Task task = taskService.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        model.addAttribute("task", task);
        return "task-detail";
    }

    @PostMapping
    public String createTask(@Valid @ModelAttribute("task") Task task,
                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "task-form"; // redisplay with field errors
        }
        taskService.save(task);
        return "redirect:/tasks"; // Post/Redirect/Get
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public String handleNotFound(TaskNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/not-found";
    }
}
