package com.savadanko.controller;

import com.savadanko.domain.dto.LabWorkDTO;
import com.savadanko.domain.dto.LabWorkFullDTO;
import com.savadanko.domain.requests.CreateLabWorkRequest;
import com.savadanko.domain.requests.UpdateLabWorkRequest;
import com.savadanko.service.LabWorkService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/labworks")
public class LabWorkController {

    private final LabWorkService service;

    public LabWorkController(LabWorkService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Список всех лабораторных работ")
    public List<LabWorkDTO> findAll() { return service.findAll(); }

    @GetMapping("/full")
    @Operation(summary = "Список всех лабораторных работ (полная древовидная структура)")
    public List<LabWorkFullDTO> findAllFull() { return service.findAllFull(); }

    @GetMapping("/{id}")
    @Operation(summary = "Найти лабораторную работу по id")
    public LabWorkDTO findById(@PathVariable Long id) { return service.findById(id); }

    @GetMapping("/{id}/full")
    @Operation(summary = "Лабораторная работа по id (полная древовидная структура)")
    public LabWorkFullDTO findFullById(@PathVariable Long id) { return service.findFullById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать лабораторную работу")
    public LabWorkDTO create(@Valid @RequestBody CreateLabWorkRequest req) { return service.create(req); }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить лабораторную работу")
    public LabWorkDTO update(@PathVariable Long id, @Valid @RequestBody UpdateLabWorkRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить лабораторную работу")
    public void delete(@PathVariable Long id) { service.delete(id); }
}
