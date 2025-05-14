package ru.otus.java.dev.pro.crm.model.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.otus.java.dev.pro.crm.model.dto.PersonDto;
import ru.otus.java.dev.pro.crm.model.entity.Person;

@Component
@RequiredArgsConstructor
public class PersonConverter implements Converter<Person, PersonDto> {

    @Override
    public PersonDto convert(Person person) {
        return new PersonDto(
                person.getId(),
                person.getFullName(),
                person.getGender(),
                person.getSnils(),
                person.getPhone(),
                person.getEmail(),
                person.getBirthDate(),
                person.getWorkplace(),
                person.getPosition()
        );
    }

}
