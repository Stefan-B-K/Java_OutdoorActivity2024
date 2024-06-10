package com.istef.OutdoorActivity2024.model.io;

import com.istef.OutdoorActivity2024.model.HourInterval;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;


public class OutputActivity implements Comparable<OutputActivity> {
    private String name;
    private Map<ConditionsType, List<HourInterval>> suitableDaysHours;

    public OutputActivity() {
    }

    public OutputActivity(String name, Map<ConditionsType, List<HourInterval>> suitableDaysHours) {
        this.name = name;
        this.suitableDaysHours = suitableDaysHours;
    }

    public String getName() {
        return name;
    }

    public Map<ConditionsType, List<HourInterval>> getSuitableDaysHours() {
        return suitableDaysHours;
    }

    public void setSuitableDaysHours(Map<ConditionsType, List<HourInterval>> suitableDaysHours) {
        this.suitableDaysHours = suitableDaysHours;
    }

    @Override
    public int compareTo(@NotNull OutputActivity o) {

        if (!this.name.equals(o.getName())) return -1;

        for (ConditionsType conditionsType : suitableDaysHours.keySet()) {
            List<HourInterval> hourIntervalsLeft = suitableDaysHours.get(conditionsType);
            List<HourInterval> hourIntervalsRight = o.getSuitableDaysHours().get(conditionsType);
            if ((hourIntervalsLeft == null && hourIntervalsRight != null)
                || (hourIntervalsLeft != null && hourIntervalsRight == null)) return -1;
            for (int i = 0; i < hourIntervalsLeft.size(); i++) {
                if (hourIntervalsLeft.get(i).compareTo(hourIntervalsRight.get(i)) != 0) return -1;
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        return "OutputActivity{" +
               "name='" + name + '\'' +
               ", suitableDaysHours=" + suitableDaysHours +
               '}';
    }
}
