package ru.otus.java.dev.pro;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.otus.java.dev.pro.controller.action.dto.AnaliseDto;
import ru.otus.java.dev.pro.controller.action.dto.ApproveDto;
import ru.otus.java.dev.pro.controller.action.dto.BloodReportDto;
import ru.otus.java.dev.pro.controller.rest.dto.CreatePersonDto;
import ru.otus.java.dev.pro.crm.model.dto.BloodBankDto;
import ru.otus.java.dev.pro.crm.model.dto.DonationDto;
import ru.otus.java.dev.pro.crm.model.dto.DonorDto;
import ru.otus.java.dev.pro.crm.model.dto.PersonDto;
import ru.otus.java.dev.pro.crm.model.enumz.BloodGroup;
import ru.otus.java.dev.pro.util.Validator;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.random.RandomGenerator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Основной тест приложения должен")
class ApplicationIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(ApplicationIntegrationTest.class);

    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private static final String PERSONS_JSON_PATH = "persons/test-person.json";
    private static final int RED_LINE_AGE = 18;

    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("test-blood-donation")
                    .withUsername("test")
                    .withPassword("testsecret")
                    .withClasspathResourceMapping(
                            "/db/migration/V1__initial_schema.sql",
                            "/docker-entrypoint-initdb.d/V1__initial_schema.sql",
                            BindMode.READ_ONLY
                    );

    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    private static void configureDatabase(DynamicPropertyRegistry registry) {
        postgresContainer.start();
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        log.info("DB URL: {}", postgresContainer.getJdbcUrl());
    }


    @DisplayName("пройти все этапы от начала до конца процесса донации")
    @Test
    void donation() throws Exception {
        // проверяем состояние банка крови - он должен быть пустым
        String emptyBloodBankListJson = mockMvc.perform(get("/blood-bank/all"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var emptyBloodBankDtoList = asListObject(emptyBloodBankListJson, BloodBankDto.class);
        assertThat(emptyBloodBankDtoList.stream().map(BloodBankDto::bloodVolume).mapToLong(Long::longValue).sum())
                .isEqualTo(0);

        // подготоавливаем persons из заранее созданных данных
        var createPersonDtoList = getCreatePersonsFromResource();

        // проверим, что все person, которые мы хотим создать...
        assertThatCode(() -> createPersonDtoList.forEach(createPersonDto -> {
                    // имеют валидный снилс и email
                    Validator.validateSnils(createPersonDto.snils());

                    // старше 18 лет
                    Validator.validateEmail(createPersonDto.email());
                    if (ChronoUnit.YEARS.between(createPersonDto.birthDate(), LocalDate.now()) < RED_LINE_AGE) {
                        throw new IllegalArgumentException();
                    }
                })
        ).doesNotThrowAnyException();

        // заводим в систему всех person
        var createdPersonDtoList = createPersonDtoList.stream()
                .map(this::asJson)
                .map(createPersonJson -> {
                    try {
                        return mockMvc.perform(post("/person")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(createPersonJson))
                                .andExpect(status().isOk())
                                .andReturn().getResponse().getContentAsString();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(createdPersonJson -> asObject(createdPersonJson, PersonDto.class))
                .toList();

        // провряем, что сколько собирались создать, столько и создали
        assertThat(createPersonDtoList).hasSize(createdPersonDtoList.size());

        // проверяем, что все снилсы, которые мы заводили есть в списке созданных person
        var createdPersonSnilsList = createdPersonDtoList.stream().map(PersonDto::snils).toList();
        createPersonDtoList.stream()
                .map(CreatePersonDto::snils)
                .forEach(createdSnils -> assertThat(createdPersonSnilsList).contains(createdSnils));

        // здесь мы будем собирать всю кровь, которую собрали
        long donatedBloodVolume = 0;

        // теперь мы будем каждого донора вести по процессу донации
        for (String snils : createdPersonSnilsList) {

            // отправляем persons на донацию
            String personJson = mockMvc.perform(post("/donation/start/snils={0}", snils))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            // проверяем, что вернувшийся person это тот, которого мы создавали, а не какой-то другой
            var toDonationPersonDto = asObject(personJson, PersonDto.class);
            assertThat(createdPersonDtoList).contains(toDonationPersonDto);

            // отправляем донора на анализ
            String newDonorJson = mockMvc.perform(post("/donation/send/analise/snils={0}", snils))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            var toAnaliseDonorDto = asObject(newDonorJson, DonorDto.class);

            // ищем по ID этого донора в списке доноров
            String existsDonorJson = mockMvc.perform(get("/donor/{0}", toAnaliseDonorDto.id()))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            var existsDonorDto = asObject(existsDonorJson, DonorDto.class);

            // проверяем, что такой донор есть и он зарегистрирован
            assertThat(toAnaliseDonorDto).isEqualTo(existsDonorDto);

            // делаем у донора первичный анализ и отправляем на осмотр
            var foundBloodGroupCode = getRandomBloodGroupCode();
            String toCheckDonorJson =
                    mockMvc.perform(post("/donation/send/check/donorId={0}", toAnaliseDonorDto.id())
                            // считаем, что у донора анализы в норме
                            .content(asJson( new AnaliseDto(true, foundBloodGroupCode) ))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            var toCheckDonorDto = asObject(toCheckDonorJson, DonorDto.class);

            // проверяем, что донору проставилась правильная группа крови
            assertThat(toCheckDonorDto.bloodGroup()).isNotNull().isEqualTo(BloodGroup.fromCode(foundBloodGroupCode));

            // отправляем person на сдачу крови
            String toDonationDonorJson =
                    mockMvc.perform(post("/donation/send/donation/donorId={0}", toCheckDonorDto.id())
                            // считаем, что донор прошел проверку и он не пойдет в черный список
                            .content(asJson( new ApproveDto(true, false, "") ))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            var toDonationDonorDto = asObject(toDonationDonorJson, DonorDto.class);

            // проверяем, что это тот донор, которого мы отправили
            assertThat(toDonationDonorDto).isEqualTo(toCheckDonorDto);

            // берем кровь у донора (рандомное количество)
            int bloodVolumeMl = RandomGenerator.getDefault().nextInt(450, 470);
            String donationJson = mockMvc.perform(post(
                    "/donation/make/donorId={0}/volume={1}",
                            toCheckDonorDto.id(),
                            bloodVolumeMl
                    ))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            donatedBloodVolume += bloodVolumeMl;
            var donationDto = asObject(donationJson, DonationDto.class);

            // проверяем, что это донация именно нашего донора
            assertThat(donationDto.donorDto()).isEqualTo(toDonationDonorDto);
            // проверяем, что записано ровно столько крови, сколько сдавали
            assertThat(donationDto.bloodVolume()).isEqualTo(bloodVolumeMl);
        }

        // после всех донаций можно закрыть день и "слить" всю кровь в банк
        String bloodReportJson = mockMvc.perform(post("/blood/collect"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var bloodReportDto = asObject(bloodReportJson, BloodReportDto.class);

        // проверяем, что кол-во сдавших соотвествует данным отчета, а неуспешных - нет
        assertThat(bloodReportDto.countOfSuccessDonation()).isEqualTo(createdPersonSnilsList.size());
        assertThat(bloodReportDto.countOfRejectedDonation()).isEqualTo(0);
        assertThat(bloodReportDto.countOfSuccessDonation() + bloodReportDto.countOfRejectedDonation())
                .isEqualTo(createdPersonSnilsList.size());

        // проверяем, что вся кровь, которую сдали попала в отчет
        assertThat(bloodReportDto.successBloodMl()).isEqualTo(donatedBloodVolume);
        assertThat(bloodReportDto.rejectedBloodMl()).isEqualTo(0);
        assertThat(bloodReportDto.successBloodMl() + bloodReportDto.rejectedBloodMl())
                .isEqualTo(donatedBloodVolume);

        // проверяем состояние банка крови - в нем должна быть сданная кровь
        String fulledBloodBankListJson = mockMvc.perform(get("/blood-bank/all"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var fulledBloodBankDtoList = asListObject(fulledBloodBankListJson, BloodBankDto.class);
        assertThat(fulledBloodBankDtoList.stream().map(BloodBankDto::bloodVolume).mapToLong(Long::longValue).sum())
                .isEqualTo(donatedBloodVolume);
    }


    private String getRandomBloodGroupCode() {
        return Arrays.stream(BloodGroup.values()).toList()
                .get(RandomGenerator.getDefault().nextInt(BloodGroup.values().length))
                .getCode();
    }

    private <T> String asJson(T dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            log.error("Can't write object as JSON. Reason: {}. Object: {}", e.getMessage(), dto, e);
            throw new RuntimeException(e);
        }
    }

    private <T> T asObject(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("Can't read JSON. Reason: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private <T> List<T> asListObject(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (JsonProcessingException e) {
            log.error("Can't read JSON. Reason: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private List<CreatePersonDto> getCreatePersonsFromResource() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(PERSONS_JSON_PATH)) {
            return objectMapper.readValue(inputStream, new TypeReference<>() {
            });
        } catch (Exception e) {
            log.error("Can't read JSON. Reason: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
