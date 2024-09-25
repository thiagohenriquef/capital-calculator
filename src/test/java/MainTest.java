import br.com.capitalgains.CapitalGainCalculator;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainTest {

    @Test
    public void testMainWithInputFiles() throws IOException, JSONException {
        String[] args = {
                "src/test/resources/input/cases/case-1.json",
                "src/test/resources/input/cases/case-2.json"
        };

        CapitalGainCalculator.main(args);

        Path outputDir = Path.of("output");
        assertTrue(Files.exists(outputDir), "O diretório de saída não foi criado");

        Path expectedOutputFile1 = outputDir.resolve("output-case-1.json");
        Path expectedOutputFile2 = outputDir.resolve("output-case-2.json");
        assertTrue(Files.exists(expectedOutputFile1), "Arquivo de saída para case-1 não encontrado");
        assertTrue(Files.exists(expectedOutputFile2), "Arquivo de saída para case-2 não encontrado");

        String expectedOutput1 = Files.readString(Path.of("src/test/resources/output/cases/case-1.json"));
        String actualOutput1 = Files.readString(expectedOutputFile1);
        JSONAssert.assertEquals(expectedOutput1, actualOutput1,  JSONCompareMode.LENIENT);

        String expectedOutput2 = Files.readString(Path.of("src/test/resources/output/cases/case-2.json"));
        String actualOutput2 = Files.readString(expectedOutputFile2);
        JSONAssert.assertEquals(expectedOutput2, actualOutput2,  JSONCompareMode.LENIENT);
    }
}
