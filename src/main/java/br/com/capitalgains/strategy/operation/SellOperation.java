package br.com.capitalgains.strategy.operation;

import br.com.capitalgains.model.Trade;

import java.math.BigDecimal;
import java.util.List;

public class SellOperation implements OperationStrategy {
    private BuyOperation buyOperation;
    private BigDecimal capitalGain = BigDecimal.ZERO;

    public SellOperation(BuyOperation buyOperation) {
        this.buyOperation = buyOperation;
    }

    @Override
    public void execute(Trade trade) {
        List<Trade> portfolio = buyOperation.getPortfolio();
        int quantityToSell = trade.getQuantity();
        BigDecimal sellPrice = BigDecimal.valueOf(trade.getUnitCost());

        for (Trade boughtTrade : portfolio) {
            if (quantityToSell <= 0) break;

            int quantityAvailable = boughtTrade.getQuantity();
            if (quantityAvailable >= quantityToSell) {
                capitalGain = capitalGain.add(sellPrice.subtract(BigDecimal.valueOf(boughtTrade.getUnitCost())).multiply(new BigDecimal(quantityToSell)));
                boughtTrade.setQuantity(quantityAvailable - quantityToSell);
                quantityToSell = 0;
            } else {
                capitalGain = capitalGain.add(sellPrice.subtract(BigDecimal.valueOf(boughtTrade.getUnitCost())).multiply(new BigDecimal(quantityAvailable)));
                quantityToSell -= quantityAvailable;
                boughtTrade.setQuantity(0);
            }
        }
        System.out.println("Sold: " + trade.getQuantity() + " units at " + sellPrice + " each.");
        System.out.println("Current Capital Gain: " + capitalGain);
    }

    public BigDecimal getCapitalGain() {
        return capitalGain;
    }
}
