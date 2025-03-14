package ru.otus.services.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.api.SensorDataProcessor;
import ru.otus.api.model.SensorData;
import ru.otus.lib.SensorDataBufferedWriter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

@SuppressWarnings({"java:S1068", "java:S125"})
public class SensorDataProcessorBuffered implements SensorDataProcessor {
    private static final Logger log = LoggerFactory.getLogger(SensorDataProcessorBuffered.class);

    private final int bufferSize;
    private final SensorDataBufferedWriter writer;

    private final BlockingQueue<SensorData> dataBuffer;

    public SensorDataProcessorBuffered(int bufferSize, SensorDataBufferedWriter writer) {
        if (bufferSize < 1) {
            throw new IllegalArgumentException("Buffer size can't be less 1");
        }
        this.bufferSize = bufferSize;
        this.writer = writer;

        this.dataBuffer = new PriorityBlockingQueue<>(bufferSize, Comparator.comparing(SensorData::getMeasurementTime));
    }

    @Override
    public void process(SensorData data) {
        if (data == null) {
            log.debug("SensorData is NULL. Will be not process");
            return;
        }
        if (dataBuffer.size() >= bufferSize) {
            flush();
        }
        boolean isOffer = dataBuffer.offer(data);
        if (!isOffer) {
            log.debug("Can't offer data: {}", data);
        }
    }

    public synchronized void flush() {
        if (dataBuffer.isEmpty()) {
            return;
        }
        List<SensorData> bufferedData = new ArrayList<>(bufferSize);
        try {
            dataBuffer.drainTo(bufferedData);
            writer.writeBufferedData(bufferedData);
        } catch (Exception e) {
            log.error("Ошибка в процессе записи буфера", e);
        }
    }

    @Override
    public void onProcessingEnd() {
        flush();
    }
}
