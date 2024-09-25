package br.com.capitalgains.strategy.tax;

import br.com.capitalgains.model.Operation;
import br.com.capitalgains.model.Trade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class CapitalGainTaxStrategy implements TaxStrategy {

    private BigDecimal averageCost = BigDecimal.ZERO; // Média ponderada do custo
    private int totalQuantity = 0; // Quantidade total de ações em posse
    private BigDecimal totalLoss = BigDecimal.ZERO; // Prejuízo acumulado

    @Override
    public List<BigDecimal> calculateTax(List<Trade> trades) {
        List<BigDecimal> taxes = new ArrayList<>();

        for (Trade trade : trades) {
            BigDecimal tax = BigDecimal.ZERO;

            if (trade.getOperation() == Operation.BUY) {
                // Atualiza a média ponderada de custo
                averageCost = averageCost.multiply(BigDecimal.valueOf(totalQuantity))
                        .add(BigDecimal.valueOf(trade.getUnitCost()).multiply(BigDecimal.valueOf(trade.getQuantity())))
                        .divide(BigDecimal.valueOf(totalQuantity + trade.getQuantity()), 2, RoundingMode.HALF_UP);

                totalQuantity += trade.getQuantity(); // Atualiza a quantidade total de ações
            } else if (trade.getOperation() == Operation.SELL) {
                BigDecimal totalSaleValue = BigDecimal.valueOf(trade.getUnitCost()).multiply(BigDecimal.valueOf(trade.getQuantity()));
                BigDecimal costOfSoldShares = averageCost.multiply(BigDecimal.valueOf(trade.getQuantity()));
                BigDecimal profit = totalSaleValue.subtract(costOfSoldShares); // Calcula lucro ou prejuízo da venda

                // Verifica se o valor total da venda é maior que R$ 20.000,00
                if (totalSaleValue.compareTo(BigDecimal.valueOf(20000)) > 0) {
                    if (profit.compareTo(BigDecimal.ZERO) > 0) {
                        // Lucro: Deduz o prejuízo acumulado
                        BigDecimal taxableProfit = profit.subtract(totalLoss);
                        if (taxableProfit.compareTo(BigDecimal.ZERO) > 0) {
                            // Se há lucro após dedução do prejuízo, calcula o imposto
                            tax = taxableProfit.multiply(BigDecimal.valueOf(0.20)).setScale(2, RoundingMode.HALF_UP); // 20% sobre lucro
                        }
                        totalLoss = totalLoss.subtract(profit).max(BigDecimal.ZERO); // Atualiza o prejuízo acumulado
                    } else {
                        // Prejuízo: Armazena para futuras deduções
                        totalLoss = totalLoss.add(profit.negate());
                    }
                }
                // Ações vendidas abaixo de R$ 20.000,00 não pagam imposto, mesmo que tenha lucro
            }
            taxes.add(tax); // Adiciona o imposto calculado (ou zero, se não houver)
        }

        return taxes;
    }
}
