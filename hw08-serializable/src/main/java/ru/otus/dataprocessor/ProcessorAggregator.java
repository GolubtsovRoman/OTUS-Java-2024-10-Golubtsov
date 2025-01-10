package ru.otus.dataprocessor;

import ru.otus.model.Measurement;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ProcessorAggregator implements Processor {

    @Override
    public Map<String, Double> process(List<Measurement> data) {
        // можно было просто return new TreeMap<>(...), но хочется поупражняться в стримах
        return data.stream()
                .collect(Collectors.groupingBy(Measurement::name,
                         Collectors.summingDouble(Measurement::value)))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, TreeMap::new));
    }

}
