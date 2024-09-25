package br.com.capitalgains.strategy.operation;

import br.com.capitalgains.model.Trade;

public interface OperationStrategy {
    void execute(Trade trade);
}
