package br.com.capitalgains;

import br.com.capitalgains.model.Tax;
import br.com.capitalgains.model.Trade;
import br.com.capitalgains.strategy.tax.CapitalGainTaxStrategy;
import br.com.capitalgains.strategy.tax.TaxStrategy;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CapitalGainCalculator {
    private static final Logger logger = LoggerFactory.getLogger(CapitalGainCalculator.class);

    public static void main(String[] args) {
        if (args.length == 0) {
            logger.error("No input files provided");
            return;
        }

        Path outputDir = Paths.get("output");
        try {
            if (!Files.exists(outputDir)) {
                Files.createDirectory(outputDir);
                logger.info("Created output directory: {}", outputDir.toAbsolutePath());
            }
        } catch (IOException e) {
            logger.error("Failed to create output directory: {}", e.getMessage());
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        TaxStrategy taxStrategy = new CapitalGainTaxStrategy();

        for (String inputFilePath : args) {
            try {
                List<Trade> trades = objectMapper.readValue(new File(inputFilePath),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Trade.class));
                logger.info("Parsed trades from {}: {}", inputFilePath, trades);

                List<Tax> taxes = new ArrayList<>();
                List<BigDecimal> calculatedTaxes = taxStrategy.calculateTax(trades);
                for (BigDecimal taxValue : calculatedTaxes) {
                    taxes.add(new Tax(taxValue));
                }

                String outputJson = objectMapper.writeValueAsString(taxes);
                logger.info("Calculated taxes for {}: {}", inputFilePath, outputJson);
                String outputFileName = "output-" + Paths.get(inputFilePath).getFileName().toString();
                Path outputFilePath = outputDir.resolve(outputFileName);

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(String.valueOf(outputFilePath)))) {
                    writer.write(outputJson);
                    logger.info("Output written to file: {}", outputFilePath);
                }
            } catch (DatabindException e) {
                logger.error("JSON inválido em {}: {}", inputFilePath, e.getMessage());
            } catch (IOException e) {
                logger.error("Error processing trades from {}: {}", inputFilePath, e.getMessage());
            }
        }
    }
}
