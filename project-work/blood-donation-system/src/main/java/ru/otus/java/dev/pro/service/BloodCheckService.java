package ru.otus.java.dev.pro.service;

import org.springframework.stereotype.Service;
import ru.otus.java.dev.pro.crm.model.dto.DonationDto;

import java.util.random.RandomGenerator;

@Service
public class BloodCheckService {

    /// Сервис носит характер загулшки. Правильно будет сделать целую отдельную систему,
    /// в которую можно будет вносить данные об анализах и получать более подробный результат.
    /// Однако пока в рамках проекта лишь "заглушка", которая примерно в 1 из 100 случаев
    /// говорит о том, что кровь бракованная.

    public boolean bloodIsOk(DonationDto donationDto) {
        return 42 != RandomGenerator.getDefault().nextInt(100);
    }

}

