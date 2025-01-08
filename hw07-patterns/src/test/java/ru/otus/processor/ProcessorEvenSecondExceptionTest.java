package ru.otus.processor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.otus.model.Message;
import ru.otus.service.DateTimeProvider;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("Процессор, выбрасывающий исключение")
class ProcessorEvenSecondExceptionTest {

    @Test
    @DisplayName("должен выбросить исключение, если секунда четная")
    void processException() {
        DateTimeProvider mockDateTimeProvider = mock(DateTimeProvider.class);
        Processor processor = new ProcessorEvenSecondException(mockDateTimeProvider);

        var someLocalDateTime = LocalDateTime.parse("2007-12-03T10:15:30");
        assertThat(someLocalDateTime.getSecond()).isEven();
        Mockito.when(mockDateTimeProvider.getDate()).thenReturn(someLocalDateTime);

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> processor.process(getRandomMessage()))
                .withMessage("Even second");
    }

    @Test
    @DisplayName("должен вернуть это же сообщение, если секунда не четная")
    void processOk() {
        DateTimeProvider mockDateTimeProvider = mock(DateTimeProvider.class);
        Processor processor = new ProcessorEvenSecondException(mockDateTimeProvider);

        var someLocalDateTime = LocalDateTime.parse("2007-12-03T10:15:31");
        assertThat(someLocalDateTime.getSecond()).isOdd();
        Mockito.when(mockDateTimeProvider.getDate()).thenReturn(someLocalDateTime);

        var message = getRandomMessage();

        assertThat(message).isEqualTo(processor.process(message));
    }

    private Message getRandomMessage() {
        return new Message.Builder(22).field12("field2").field5("field5").build();
    }

}
