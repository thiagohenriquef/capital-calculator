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
    private BigDecimal totalLoss = BigDecimal.ZERO;
    private List<Trade> buyTrades = new ArrayList<>();

    @Override
    public List<BigDecimal> calculateTax(List<Trade> trades) {
        List<BigDecimal> taxes = new ArrayList<>();

        for (Trade trade : trades) {
            BigDecimal tax = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

            if (trade.getOperation() == Operation.BUY) {
                buyTrades.add(trade);
                averageCost = calculateWeightedAverageCost(trade);
                totalQuantity += trade.getQuantity();
            } else if (trade.getOperation() == Operation.SELL) {
                if (totalQuantity >= trade.getQuantity()) {
                    BigDecimal totalSaleValue = BigDecimal.valueOf(trade.getUnitCost()).multiply(BigDecimal.valueOf(trade.getQuantity()));
                    BigDecimal costOfSoldShares = averageCost.multiply(BigDecimal.valueOf(trade.getQuantity()));
                    BigDecimal profit = totalSaleValue.subtract(costOfSoldShares);

                    if (profit.compareTo(BigDecimal.ZERO) > 0) {
                        if (totalLoss.compareTo(BigDecimal.ZERO) > 0) {
                            BigDecimal deductible = totalLoss.min(profit);
                            profit = profit.subtract(deductible);
                            totalLoss = totalLoss.subtract(deductible);
                        }
                    } else {
                        totalLoss = totalLoss.add(profit.negate());
                    }

                    if (totalSaleValue.compareTo(BigDecimal.valueOf(20000)) > 0) {
                        if (profit.compareTo(BigDecimal.ZERO) > 0) {
                            tax = profit.multiply(BigDecimal.valueOf(0.20)).setScale(2, RoundingMode.HALF_UP);
                        }
                    }

                    totalQuantity -= trade.getQuantity();
                } else {
                    tax = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
                }
            }
            taxes.add(tax);
        }
        return taxes;
    }

    private BigDecimal calculateWeightedAverageCost(Trade newTrade) {
        BigDecimal totalCost = averageCost.multiply(BigDecimal.valueOf(totalQuantity))
                .add(BigDecimal.valueOf(newTrade.getUnitCost()).multiply(BigDecimal.valueOf(newTrade.getQuantity())));
        int newTotalQuantity = totalQuantity + newTrade.getQuantity();
        return totalCost.divide(BigDecimal.valueOf(newTotalQuantity), 2, RoundingMode.HALF_UP);
    }
}
