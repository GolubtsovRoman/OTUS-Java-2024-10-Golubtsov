package ru.otus.java.dev.pro.controller.action;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.java.dev.pro.controller.action.dto.AnaliseDto;
import ru.otus.java.dev.pro.controller.action.dto.ApproveDto;
import ru.otus.java.dev.pro.crm.model.dto.DonationDto;
import ru.otus.java.dev.pro.crm.model.dto.DonorDto;
import ru.otus.java.dev.pro.crm.model.dto.PersonDto;
import ru.otus.java.dev.pro.service.DonationManagementService;

@RestController
@RequestMapping("/donation")
@RequiredArgsConstructor
public class DonationController {

    private final DonationManagementService donationManagementService;

    @Operation(summary = "Начало процесса донорства", description = "Запускает процесс донорства для человека по указанному СНИЛСу.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Процесс донорства успешно запущен"),
            @ApiResponse(responseCode = "404", description = "Человек с указанным СНИЛСом не найден")
    })
    @PostMapping("/start/snils={snils}")
    public PersonDto startDonation(@Parameter(description = "СНИЛС донора") @PathVariable String snils) {
        return donationManagementService.sendToDonation(snils);
    }

    @Operation(summary = "Отправка на анализ", description = "Отправляет донора на анализ по указанному СНИЛСу.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Донор успешно отправлен на анализ"),
            @ApiResponse(responseCode = "404", description = "Донор с указанным СНИЛСом не найден")
    })
    @PostMapping("/send/analise/snils={snils}")
    public DonorDto sendToAnalise(@Parameter(description = "СНИЛС донора") @PathVariable String snils) {
        return donationManagementService.sendToAnalise(snils);
    }

    @Operation(summary = "Отправка на проверку", description = "Отправляет донора на проверку с указанным ID и данными анализа.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Донор успешно отправлен на проверку"),
            @ApiResponse(responseCode = "404", description = "Донор не найден")
    })
    @PostMapping("/send/check/donorId={donorId}")
    public DonorDto sendToCheck(@Parameter(description = "ID донора") @PathVariable long donorId,
                                @Parameter(description = "Данные анализа") @RequestBody AnaliseDto analiseDto) {
        return donationManagementService.sendToCheck(donorId, analiseDto);
    }

    @Operation(summary = "Отправка на донорство", description = "Отправляет донора на донорство с указанным ID и данными подтверждения.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Донор успешно отправлен на донорство"),
            @ApiResponse(responseCode = "404", description = "Донор не найден")
    })
    @PostMapping("/send/donation/donorId={donorId}")
    public DonorDto sendToDonation(@Parameter(description = "ID донора") @PathVariable long donorId,
                                   @Parameter(description = "Данные подтверждения") @RequestBody ApproveDto approveDto) {
        return donationManagementService.sendToDonation(donorId, approveDto).orElse(null);
    }

    @Operation(summary = "Процесс донорства", description = "Осуществляет донорство для указанного донора и объема крови.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Донорство успешно проведено"),
            @ApiResponse(responseCode = "404", description = "Донор не найден")
    })
    @PostMapping("/make/donorId={donorId}/volume={bloodVolumeMl}")
    public DonationDto makeDonation(@Parameter(description = "ID донора") @PathVariable long donorId,
                                    @Parameter(description = "Объем крови в миллилитрах") @PathVariable int bloodVolumeMl) {
        return donationManagementService.makeDonation(donorId, bloodVolumeMl);
    }

}
