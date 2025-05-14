package ru.otus.java.dev.pro.controller.action;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.java.dev.pro.service.BloodPoolService;
import ru.otus.java.dev.pro.service.DonationManagementService;

@RestController
@RequestMapping("/reset")
@RequiredArgsConstructor
public class ResetController {

    private final BloodPoolService bloodPoolService;
    private final DonationManagementService donationManagementService;

    @Operation(summary = "Сброс всех данных", description = "Сбрасывает данные пула крови и управления донациями.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные успешно сброшены"),
            @ApiResponse(responseCode = "500", description = "Ошибка при сбросе данных")
    })
    @PostMapping("/all")
    public void collect() {
        bloodPoolService.reset();
        donationManagementService.reset();
    }

}
