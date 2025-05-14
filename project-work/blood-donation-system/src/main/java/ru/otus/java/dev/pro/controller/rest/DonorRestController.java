package ru.otus.java.dev.pro.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.java.dev.pro.crm.model.dto.DonorDto;
import ru.otus.java.dev.pro.crm.service.DonorService;

import java.util.List;

@RestController
@RequestMapping("/donor")
@RequiredArgsConstructor
public class DonorRestController {

    private final DonorService donorService;

    @Operation(summary = "Получение донора по ID", description = "Возвращает информацию о доноре по указанному ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Донор найден"),
            @ApiResponse(responseCode = "404", description = "Донор не найден")
    })
    @GetMapping("/{id}")
    public DonorDto getById(@Parameter(description = "ID донора") @PathVariable long id) {
        return donorService.getById(id);
    }

    @Operation(summary = "Получение списка всех доноров", description = "Возвращает список всех доноров.")
    @ApiResponse(responseCode = "200", description = "Список доноров успешно получен")
    @GetMapping("/all")
    public List<DonorDto> getAll() {
        return donorService.getAll();
    }

}
