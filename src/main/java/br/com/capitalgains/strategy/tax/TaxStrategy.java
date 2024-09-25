package br.com.capitalgains.strategy.tax;

import br.com.capitalgains.model.Trade;

import java.math.BigDecimal;
import java.util.List;

public interface TaxStrategy {
    List<BigDecimal> calculateTax(List<Trade> trades);
}
