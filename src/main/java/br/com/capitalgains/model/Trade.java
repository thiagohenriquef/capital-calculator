package br.com.capitalgains.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trade {
    private Operation operation;

    private int quantity;

    @JsonProperty("unit-cost")
    private double unitCost;
}


