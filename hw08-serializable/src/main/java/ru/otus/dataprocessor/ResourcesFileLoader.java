package ru.otus.dataprocessor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.otus.model.Measurement;
import ru.otus.util.FileUtils;

import java.util.List;

public class ResourcesFileLoader implements Loader {

    private final String fileName;

    private final ObjectMapper mapper = new ObjectMapper();


    public ResourcesFileLoader(String fileName) {
        this.fileName = FileUtils.checkJsonFileName(fileName);
    }

    @Override
    public List<Measurement> load() {
        String content = FileUtils.readResourceFileToString(fileName);
        return mapToMeasurementList(content);
    }


    private List<Measurement> mapToMeasurementList(String content) {
        try {
            return mapper.readValue(content, new TypeReference<>(){});
        } catch (JsonProcessingException e) {
            throw new FileProcessException("Can't parse content: %s".formatted(content));
        }
    }

}
