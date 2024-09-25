import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class CapitalGainCalculatorTest {

    @ParameterizedTest
    @MethodSource("provideTestCases")
    public void testCapitalGainCalculator(String inputFile, String expectedOutputFile) throws IOException {
        // Lê o arquivo de entrada
        String inputJson = new String(Files.readAllBytes(Paths.get(inputFile)));

        // Redireciona a saída para um StringWriter
        String output = runCapitalGainCalculator(inputJson);

        // Lê o arquivo de saída esperada
        String expectedOutput = new String(Files.readAllBytes(Paths.get(expectedOutputFile)));

        // Compara a saída gerada com a saída esperada
        assertEquals(expectedOutput.trim(), output.trim());
    }

    private String runCapitalGainCalculator(String inputJson) {
        // Aqui você pode executar a lógica do seu programa principal,
        // por exemplo, redirecionando a entrada padrão e chamando o main
        // do CapitalGainCalculator.
        // Implemente essa lógica conforme necessário.

        // Retorne a saída gerada como uma String.
        return ""; // Substitua pela implementação real
    }

    private static Stream<Arguments> provideTestCases() {
        return Stream.of(
                Arguments.of("C:\\Users\\thiag\\projetos\\capital-gains\\case-tests\\case-1.json", "C:\\Users\\thiag\\projetos\\capital-gains\\case-tests\\output\\case-1.json"),
        );
    }
}
