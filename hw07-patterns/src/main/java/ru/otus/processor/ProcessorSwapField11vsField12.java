package ru.otus.processor;

import ru.otus.model.Message;

public class ProcessorSwapField11vsField12 implements Processor {

    @Override
    public Message process(Message message) {
        return message.toBuilder()
                .field11(String.copyValueOf(message.getField12().toCharArray()))
                .field12(String.copyValueOf(message.getField11().toCharArray()))
                .build();
    }

}
