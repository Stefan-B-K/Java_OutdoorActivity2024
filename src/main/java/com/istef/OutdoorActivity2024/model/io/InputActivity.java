package com.istef.OutdoorActivity2024.model.io;

import com.istef.OutdoorActivity2024.conditions.Condition;

import java.util.ArrayList;
import java.util.List;


public class InputActivity {
    private final String name;
    private final List<Condition> conditions;
    private final List<Condition> preferences;

    public InputActivity(String name) {
        this.name = name;
        conditions = new ArrayList<>();
        preferences = new ArrayList<>();
    }

    public InputActivity(String name, List<Condition> conditions, List<Condition> preferences) {
        this.name = name;
        this.conditions = conditions;
        this.preferences = preferences;
    }

    public String getName() {
        return name;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public List<Condition> getPreferences() {
        return preferences;
    }
}
