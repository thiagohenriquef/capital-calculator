package br.com.capitalgains.strategy.tax;

import br.com.capitalgains.model.Operation;
import br.com.capitalgains.model.Trade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class CapitalGainTaxStrategy implements TaxStrategy {

    private BigDecimal averageCost = BigDecimal.ZERO; // Custo médio ponderado
    private int totalQuantity = 0; // Total de unidades compradas
    private BigDecimal totalLoss = BigDecimal.ZERO; // Armazena o prejuízo acumulado
    private List<Trade> buyTrades = new ArrayList<>(); // Armazena todas as operações de compra

    @Override
    public List<BigDecimal> calculateTax(List<Trade> trades) {
        List<BigDecimal> taxes = new ArrayList<>();

        for (Trade trade : trades) {
            BigDecimal tax = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

            if (trade.getOperation() == Operation.BUY) {
                // Adiciona a operação de compra à lista
                buyTrades.add(trade);
                // Atualiza a média ponderada de custo
                averageCost = calculateWeightedAverageCost(trade);
                totalQuantity += trade.getQuantity();
            } else if (trade.getOperation() == Operation.SELL) {
                // Verifica se há quantidade suficiente para a venda
                if (totalQuantity >= trade.getQuantity()) {
                    BigDecimal totalSaleValue = BigDecimal.valueOf(trade.getUnitCost()).multiply(BigDecimal.valueOf(trade.getQuantity()));
                    BigDecimal costOfSoldShares = averageCost.multiply(BigDecimal.valueOf(trade.getQuantity()));
                    BigDecimal profit = totalSaleValue.subtract(costOfSoldShares);

                    // Deduzir prejuízos acumulados
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
                            tax = profit.multiply(BigDecimal.valueOf(0.20)).setScale(2, RoundingMode.HALF_UP);
                        }
                    }

                    // Atualiza a quantidade total após a venda
                    totalQuantity -= trade.getQuantity();
                } else {
                    // Lidar com o caso em que não há ações suficientes para vender
                    tax = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP); // Ou lançar uma exceção, se preferir
                }
            }
            taxes.add(tax);
        }
        return taxes;
    }

    private BigDecimal calculateWeightedAverageCost(Trade newTrade) {
        // Calcula o custo médio ponderado incluindo a nova compra
        BigDecimal totalCost = averageCost.multiply(BigDecimal.valueOf(totalQuantity))
                .add(BigDecimal.valueOf(newTrade.getUnitCost()).multiply(BigDecimal.valueOf(newTrade.getQuantity())));
        int newTotalQuantity = totalQuantity + newTrade.getQuantity();
        return totalCost.divide(BigDecimal.valueOf(newTotalQuantity), 2, RoundingMode.HALF_UP);
    }
}
