package ru.otus.processor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.model.Message;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DisplayName("Процессор, выбрасывающий исключение")
class ProcessorEvenSecondExceptionTest {

    @Test
    @DisplayName("должен выбросить исключение, если секунда четная")
    void processException() {
        Integer second = 30;
        assertThat(second).isBetween(10, 60);
        assertThat(second).isEven();

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> getTestProcessor(second).process(getRandomMessage()));
    }

    @Test
    @DisplayName("должен вернуть это же сообщение, если секунда не четная")
    void processOk() {
        Integer second = 31;
        assertThat(second).isBetween(10, 60);
        assertThat(second).isOdd();

        var message = getRandomMessage();

        assertThat(message).isEqualTo(getTestProcessor(second).process(message));
    }

    private Message getRandomMessage() {
        return new Message.Builder(22).field12("field2").field5("field5").build();
    }

    private Processor getTestProcessor(Integer second) {
        return new ProcessorEvenSecondException(() -> LocalDateTime.parse("2007-12-03T10:15:" + second));
    }

}
