package ru.otus.java.dev.pro.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.java.dev.pro.crm.model.dto.BloodBankDto;
import ru.otus.java.dev.pro.crm.service.BloodBankService;

import java.util.List;

@RestController
@RequestMapping("/blood-bank")
@RequiredArgsConstructor
public class BloodBankRestController {

    private final BloodBankService bloodBankService;

    @Operation(summary = "Получение текущего состояния банка крови", description = "Возвращает всю информацию о банке крови.")
    @ApiResponse(responseCode = "200", description = "Список всего банка крови успешно получен")
    @GetMapping("/all")
    public List<BloodBankDto> getAll() {
        return bloodBankService.getAll();
    }

}
