package ru.otus.java.dev.pro.service;

import org.springframework.stereotype.Service;
import ru.otus.java.dev.pro.crm.model.dto.DonationDto;

@Service
public class BloodCheckService {

    /// Сервис носит характер загулшки. Правильно будет сделать целую отдельную систему,
    /// в которую можно будет вносить данные об анализах и получать более подробный результат.
    /// Однако пока в рамках проекта лишь "заглушка", которая всегда говорит, что кровь в порядке

    public boolean bloodIsOk(DonationDto donationDto) {
        return true;
    }

}

