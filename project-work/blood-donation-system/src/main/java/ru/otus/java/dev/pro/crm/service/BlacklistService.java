package ru.otus.java.dev.pro.crm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.java.dev.pro.crm.exception.EntityNotFoundException;
import ru.otus.java.dev.pro.crm.model.converter.BlacklistConverter;
import ru.otus.java.dev.pro.crm.model.dto.BlacklistDto;
import ru.otus.java.dev.pro.crm.model.entity.Blacklist;
import ru.otus.java.dev.pro.crm.model.entity.Donor;
import ru.otus.java.dev.pro.crm.repository.BlacklistRepository;
import ru.otus.java.dev.pro.crm.repository.DonorRepository;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BlacklistService {

    private final DonorRepository donorRepository;
    private final BlacklistRepository blacklistRepository;
    private final Converter<Blacklist, BlacklistDto> blacklistConverter;

    @Transactional
    public BlacklistDto addToBlacklist(long donorId, String comment) {
        var donor = getDonor(donorId);
        var savedBlacklist = blacklistRepository.findBlacklistByDonor(donor)
                .map(blacklist -> {
                    log.warn("Donor with ID = {} already in blacklist by reason: {}. Update comment", donorId, comment);
                    blacklist.setComment(comment);
                    return blacklistRepository.save(blacklist);
                })
                .orElseGet(() -> {
                    var newBlacklist = Blacklist.builder()
                            .donor(donor)
                            .entryDate(LocalDate.now())
                            .comment(comment)
                            .build();
                    log.debug("Donor with ID = {} added to blacklist by reason: {}", donorId, comment);
                    return blacklistRepository.save(newBlacklist);
                });
        return blacklistConverter.convert(savedBlacklist);
    }

    @Transactional
    public Optional<BlacklistDto> findInBlacklist(long donorId) {
        return blacklistRepository.findBlacklistByDonor( getDonor(donorId) )
                .map(blacklistConverter::convert);
    }


    private Donor getDonor(long id) {
        return donorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Donor not found with id: " + id));
    }

}
