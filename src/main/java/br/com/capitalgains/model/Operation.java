package br.com.capitalgains.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Operation {
    BUY, SELL;

    @JsonCreator
    public static Operation fromString(String value) {
        return Operation.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toValue() {
        return this.name().toLowerCase();
    }
}
