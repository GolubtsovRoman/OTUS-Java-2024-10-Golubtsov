package ru.otus.processor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.model.Message;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DisplayName("Процессор смены 11 и 12 полей в сообщении")
class ProcessorSwapField11vsField12Test {

    @Test
    @DisplayName("должен поменять значение полей (не повердив остальные), но не ссылки")
    void process() {
        long id = 1;

        var valSomeField = "some field";
        var valField11 = "field 11";
        var valField12 = "field 12";

        var sourceMessage = new Message.Builder(id)
                .field1(valSomeField)
                .field11(valField11)
                .field12(valField12)
                .build();
        assertThat(sourceMessage.getId()).isEqualTo(id);
        assertThat(sourceMessage.getField1()).isEqualTo(valSomeField);
        assertThat(sourceMessage.getField11()).isEqualTo(valField11);
        assertThat(sourceMessage.getField12()).isEqualTo(valField12);

        var changedMessage = new ProcessorSwapField11vsField12().process(sourceMessage);
        assertThat(changedMessage.getId()).isEqualTo(id);
        assertThat(changedMessage.getField1()).isEqualTo(valSomeField);
        assertThat(changedMessage.getField11()).isEqualTo(valField12);
        assertThat(changedMessage.getField12()).isEqualTo(valField11);


        // check links
        assertThat(sourceMessage.getField11() == valField11).isTrue();
        assertThat(sourceMessage.getField11() != changedMessage.getField12()).isTrue();

        assertThat(sourceMessage.getField12() == valField12).isTrue();
        assertThat(sourceMessage.getField12() != changedMessage.getField11()).isTrue();
    }

}
