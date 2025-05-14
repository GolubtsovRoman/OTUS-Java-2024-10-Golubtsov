package ru.otus.java.dev.pro.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.java.dev.pro.controller.rest.dto.CreatePersonDto;
import ru.otus.java.dev.pro.crm.model.dto.PersonDto;
import ru.otus.java.dev.pro.crm.service.PersonService;

import java.util.List;

@RestController
@RequestMapping("/person")
@RequiredArgsConstructor
public class PersonRestController {

    private final PersonService personService;

    @Operation(summary = "Создание нового человека", description = "Создает нового человека на основе переданных данных.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Человек успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные для создания человека")
    })
    @PostMapping
    public PersonDto create(@RequestBody CreatePersonDto createPersonDto) {
        return personService.create(createPersonDto);
    }

    @Operation(summary = "Получение человека по СНИЛСу", description = "Возвращает информацию о человеке по указанному СНИЛСу.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Человек найден"),
            @ApiResponse(responseCode = "404", description = "Человек не найден")
    })
    @GetMapping("/snils={snils}")
    public PersonDto getBySnils(@Parameter(description = "СНИЛС человека") @PathVariable String snils) {
        return personService.getBySnils(snils);
    }

    @Operation(summary = "Получение списка всех людей", description = "Возвращает список всех людей.")
    @ApiResponse(responseCode = "200", description = "Список людей успешно получен")
    @GetMapping("/all")
    public List<PersonDto> getAll() {
        return personService.getAll();
    }

}
