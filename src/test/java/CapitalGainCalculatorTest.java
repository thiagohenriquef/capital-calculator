import br.com.capitalgains.CapitalGainCalculator;
import br.com.capitalgains.model.Tax;
import br.com.capitalgains.strategy.tax.CapitalGainTaxStrategy;
import br.com.capitalgains.strategy.tax.TaxStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;

class CapitalGainCalculatorTest {

    private CapitalGainCalculator calculator;
    private TaxStrategy taxStrategy;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        taxStrategy = new CapitalGainTaxStrategy();
        objectMapper = new ObjectMapper();
        calculator = new CapitalGainCalculator();
    }


    @Test
    public void testCreateOutputDirectory() throws Exception {
        Path inputFile = Files.createTempFile("input-test", ".json");
        Files.writeString(inputFile, "[{\"quantity\": 10, \"unit-cost\": 100.00, \"operation\": \"BUY\"}]");

        String[] args = { inputFile.toString() };
        CapitalGainCalculator.main(args);

        assertTrue(Files.exists(Path.of("output")), "Output directory should be created");

        Files.deleteIfExists(inputFile);
    }

    @Test
    void testProcessValidInput() throws IOException {
        String inputJson = "[{\"operation\":\"buy\",\"quantity\":10,\"unit-cost\":100.00},{\"operation\":\"sell\",\"quantity\":5,\"unit-cost\":120.00}]";
        Path inputFile = Paths.get("input-test.json");
        Files.write(inputFile, inputJson.getBytes());

        calculator.main(new String[]{inputFile.toString()});

        Path outputFilePath = Paths.get("output", "output-input-test.json");
        assertTrue(Files.exists(outputFilePath), "Output file should be created");

        String outputJson = Files.readString(outputFilePath);
        List<Tax> taxes = objectMapper.readValue(outputJson, objectMapper.getTypeFactory().constructCollectionType(List.class, Tax.class));

        assertEquals(2, taxes.size(), "Should calculate one tax");
        assertTrue(taxes.get(0).getTax().compareTo(BigDecimal.ZERO) >= 0, "Calculated tax should be non-negative");

        Files.delete(inputFile);
        Files.delete(outputFilePath);
    }

    @Test
    void testHandlingIOException() throws IOException {
        // Simula uma IOException ao tentar criar o BufferedWriter
        BufferedWriter mockWriter = Mockito.mock(BufferedWriter.class);
        doThrow(new IOException("Test IOException")).when(mockWriter).write(anyString());

        // Aqui, você deve injetar o mock no método que precisa ser testado
        // Isso pode requerer ajustes no CapitalGainCalculator para permitir injeção de dependências

        // Teste deve garantir que a exceção seja capturada e logada corretamente
        calculator.main(new String[]{"nonexistent-file.json"});

        // Verifique o log novamente conforme necessário
    }
}
