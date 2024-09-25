package br.com.capitalgains.strategy.operation;

import br.com.capitalgains.model.Trade;

import java.util.ArrayList;
import java.util.List;

public class BuyOperation implements OperationStrategy {
    private List<Trade> portfolio = new ArrayList<>();

    @Override
    public void execute(Trade trade) {
        portfolio.add(trade);
        System.out.println("Bought: " + trade.getQuantity() + " units at " + trade.getUnitCost() + " each.");
    }

    public List<Trade> getPortfolio() {
        return portfolio;
    }
}
