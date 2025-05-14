package ru.otus.java.dev.pro.controller.action;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.java.dev.pro.controller.action.dto.BloodReportDto;
import ru.otus.java.dev.pro.service.BloodPoolService;

@RestController
@RequestMapping("/blood")
@RequiredArgsConstructor
public class BloodController {

    private final BloodPoolService bloodPoolService;

    @Operation(summary = "Сбор крови", description = "Собирает кровь и возвращает отчет о сборе.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Кровь успешно собрана, отчет возвращен"),
            @ApiResponse(responseCode = "500", description = "Ошибка при сборе крови")
    })
    @PostMapping("/collect")
    public BloodReportDto collect() {
        return bloodPoolService.flush();
    }

}
