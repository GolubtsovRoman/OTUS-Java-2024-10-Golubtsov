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
import ru.otus.java.dev.pro.crm.model.dto.DonationDto;
import ru.otus.java.dev.pro.crm.service.DonationService;

import java.util.List;

@RestController
@RequestMapping("/donation")
@RequiredArgsConstructor
public class DonationRestController {

    private final DonationService donationService;
    
    @Operation(summary = "Получение донаций по ID донора", description = "Возвращает список донаций, связанных с указанным ID донора.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список донаций успешно получен"),
            @ApiResponse(responseCode = "404", description = "Донор не найден или у него нет донаций")
    })
    @GetMapping("/{id}")
    public List<DonationDto> getByDonorId(@Parameter(description = "ID донора") @PathVariable long id) {
        return donationService.getByDonorId(id);
    }

    @Operation(summary = "Получение списка всех донаций", description = "Возвращает список всех донаций.")
    @ApiResponse(responseCode = "200", description = "Список донаций успешно получен")
    @GetMapping("/all")
    public List<DonationDto> getAll() {
        return donationService.getAll();
    }

}
