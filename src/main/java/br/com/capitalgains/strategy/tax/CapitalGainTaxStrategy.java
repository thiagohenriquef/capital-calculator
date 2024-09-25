package br.com.capitalgains.strategy.tax;

import br.com.capitalgains.model.Operation;
import br.com.capitalgains.model.Trade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class CapitalGainTaxStrategy implements TaxStrategy {

    private BigDecimal averageCost = BigDecimal.ZERO;
    private int totalQuantity = 0;
    private BigDecimal totalLoss = BigDecimal.ZERO; // Armazena o prejuízo acumulado

    @Override
    public List<BigDecimal> calculateTax(List<Trade> trades) {
        List<BigDecimal> taxes = new ArrayList<>();

        for (Trade trade : trades) {
            BigDecimal tax = BigDecimal.ZERO;

            if (trade.getOperation() == Operation.BUY) {
                // Atualiza a média ponderada de custo
                averageCost = averageCost.multiply(BigDecimal.valueOf(totalQuantity))
                        .add(BigDecimal.valueOf(trade.getUnitCost()).multiply(BigDecimal.valueOf(trade.getQuantity())))
                        .divide(BigDecimal.valueOf(totalQuantity + trade.getQuantity()), 2,  RoundingMode.HALF_UP);
                totalQuantity += trade.getQuantity();
            } else if (trade.getOperation() == Operation.SELL) {
                BigDecimal totalSaleValue = BigDecimal.valueOf(trade.getUnitCost()).multiply(BigDecimal.valueOf(trade.getQuantity()));
                BigDecimal costOfSoldShares = averageCost.multiply(BigDecimal.valueOf(trade.getQuantity()));
                BigDecimal profit = totalSaleValue.subtract(costOfSoldShares);

                // Verifica se há prejuízo acumulado para deduzir
                if (profit.compareTo(BigDecimal.ZERO) > 0) {
                    if (totalLoss.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal deductible = totalLoss.min(profit);
                        profit = profit.subtract(deductible);
                        totalLoss = totalLoss.subtract(deductible);
                    }
                } else {
                    totalLoss = totalLoss.add(profit.negate()); // Acumula prejuízo
                }

                // Se a venda é maior que R$ 20.000,00, calcula imposto
                if (totalSaleValue.compareTo(BigDecimal.valueOf(20000)) > 0) {
                    if (profit.compareTo(BigDecimal.ZERO) > 0) {
                        tax = profit.multiply(BigDecimal.valueOf(0.20)).setScale(2, RoundingMode.HALF_UP); // 20% sobre lucro
                    }
                }
            }
            taxes.add(tax.setScale(2, RoundingMode.HALF_UP)); // Garantir 2 casas decimais
        }
        return taxes;
    }
}
