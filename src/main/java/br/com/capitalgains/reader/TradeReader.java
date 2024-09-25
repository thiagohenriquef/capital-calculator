package br.com.capitalgains.reader;

import br.com.capitalgains.model.Trade;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TradeReader {

    private final ObjectMapper objectMapper;

    public TradeReader() {
        this.objectMapper = new ObjectMapper();
    }

    public List<Trade> readFromFile(File file) throws IOException {
        return objectMapper.readValue(file, new TypeReference<List<Trade>>() {});
    }
}

