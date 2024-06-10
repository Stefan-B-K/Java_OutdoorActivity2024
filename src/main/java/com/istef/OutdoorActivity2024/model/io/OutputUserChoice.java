package com.istef.OutdoorActivity2024.model.io;

import java.util.Map;

public class OutputUserChoice {
    private OutputOption selectedOutput;
    private Map<String, String> selectedOutputProps;

    public OutputUserChoice() {
    }

    public OutputUserChoice(OutputOption selectedOutput, Map<String, String> selectedOutputProps) {
        this.selectedOutput = selectedOutput;
        this.selectedOutputProps = selectedOutputProps;
    }

    public OutputOption getSelectedOutput() {
        return selectedOutput;
    }

    public void setSelectedOutput(OutputOption selectedOutput) {
        this.selectedOutput = selectedOutput;
    }

    public Map<String, String> getSelectedOutputProps() {
        return selectedOutputProps;
    }

    public void setSelectedOutputProps(Map<String, String> selectedOutputProps) {
        this.selectedOutputProps = selectedOutputProps;
    }
}
