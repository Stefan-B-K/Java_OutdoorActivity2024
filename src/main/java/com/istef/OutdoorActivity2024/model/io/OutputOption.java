package com.istef.OutdoorActivity2024.model.io;

public enum OutputOption {
    GOOGLE_CALENDAR, MAIL("userEmail"), CLI;

    private final String[] propKeys;

    OutputOption(String... propKeys) {
        this.propKeys = propKeys;
    }

    public String[] getPropKeys() {
        return propKeys;
    }
}

