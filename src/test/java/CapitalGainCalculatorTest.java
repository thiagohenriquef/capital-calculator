import br.com.capitalgains.model.Tax;
import br.com.capitalgains.model.Trade;
import br.com.capitalgains.strategy.tax.CapitalGainTaxStrategy;
import br.com.capitalgains.strategy.tax.TaxStrategy;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CapitalGainCalculatorTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TaxStrategy taxStrategy = new CapitalGainTaxStrategy();

    @ParameterizedTest
    @CsvSource({
            "src/test/resources/input/cases/case-1.json, src/test/resources/output/cases/case-1.json",
            "src/test/resources/input/cases/case-2.json, src/test/resources/output/cases/case-2.json",
            "src/test/resources/input/cases/case-3.json, src/test/resources/output/cases/case-3.json",
            "src/test/resources/input/cases/case-4.json, src/test/resources/output/cases/case-4.json",
            "src/test/resources/input/cases/case-5.json, src/test/resources/output/cases/case-5.json",
            "src/test/resources/input/cases/case-6.json, src/test/resources/output/cases/case-6.json",
            "src/test/resources/input/cases/case-7.json, src/test/resources/output/cases/case-7.json",
            "src/test/resources/input/cases/case-8.json, src/test/resources/output/cases/case-8.json"
    })
    public void testCalculateTaxes(String inputFilePath, String expectedFilePath) throws IOException {
        List<Trade> trades = objectMapper.readValue(Files.readAllBytes(Path.of(inputFilePath)),
                objectMapper.getTypeFactory().constructCollectionType(List.class, Trade.class));

        List<BigDecimal> calculatedTaxes = taxStrategy.calculateTax(trades);

        List<Tax> expectedTaxes = objectMapper.readValue(Files.readAllBytes(Path.of(expectedFilePath)),
                objectMapper.getTypeFactory().constructCollectionType(List.class, Tax.class));

        assertEquals(expectedTaxes.size(), calculatedTaxes.size(), "Número de impostos calculados deve ser igual ao esperado");

        for (int i = 0; i < expectedTaxes.size(); i++) {
            BigDecimal expectedTax = expectedTaxes.get(i).getTax().setScale(2, RoundingMode.HALF_UP);
            BigDecimal actualTax = calculatedTaxes.get(i).setScale(2, RoundingMode.HALF_UP);
            assertEquals(expectedTax, actualTax,
                    String.format("Imposto calculado não corresponde ao esperado para o caso ==>" + inputFilePath));
        }
    }

    @ParameterizedTest
    @CsvSource({
            "src/test/resources/input/exception/invalid-file-1.json",
            "src/test/resources/input/exception/invalid-file-2.json",
            "src/test/resources/input/exception/invalid-file-3.json"
    })
    public void testInvalidJsonFiles(String invalidJsonFilePath) {
        assertThrows(DatabindException.class, () -> {
            objectMapper.readValue(new File(invalidJsonFilePath),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Trade.class));
        });
    }
}
