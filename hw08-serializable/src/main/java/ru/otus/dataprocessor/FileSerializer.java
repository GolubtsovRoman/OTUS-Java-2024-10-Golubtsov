package ru.otus.dataprocessor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.otus.util.FileUtils;

import java.util.Map;

public class FileSerializer implements Serializer {

    private final String fileName;

    private final ObjectMapper mapper = new ObjectMapper();


    public FileSerializer(String fileName) {
        this.fileName = FileUtils.checkJsonFileName(fileName);
    }

    @Override
    public void serialize(Map<String, Double> data) {
        FileUtils.writeStringToFile(fileName, mapToJson(data));
    }


    private String mapToJson(Map<String, Double> data) {
        try {
            return mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new FileProcessException("Can't parse JSON: %s".formatted(fileName));
        }
    }

}
