package cn.edu.nju.cs.batch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class JsonFileReader implements ItemReader<JsonNode> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final BufferedReader reader;

    public JsonFileReader(String filename) throws FileNotFoundException {
        if (filename == null) {
            throw new FileNotFoundException();
        }
        if (filename.matches("^file:(.*)")) {
            filename = filename.substring(filename.indexOf(":") + 1);
        }
        reader = new BufferedReader(new FileReader(filename));
    }

    @Override
    public JsonNode read() throws Exception {
        var line = reader.readLine();
        if (line != null) {
            return objectMapper.readTree(line);
        }
        return null;
    }
}
