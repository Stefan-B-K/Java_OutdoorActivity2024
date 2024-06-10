package com.istef.OutdoorActivity2024.model.io;

import com.google.api.client.util.Value;

public enum ConditionsType {
    @Value
    ALL("Suitable conditions"),
    @Value
    PREFERRED("Preferred conditions");

    private final String description;

    ConditionsType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}