package br.com.capitalgains.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trade {
    private Operation operation;

    private int quantity;

    @JsonProperty("unit-cost")
    private double unitCost;
}


