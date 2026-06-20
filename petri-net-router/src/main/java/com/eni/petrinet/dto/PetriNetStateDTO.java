package com.eni.petrinet.dto;

import java.util.List;
import java.util.Map;

public class PetriNetStateDTO {

    private Map<String, Integer> marking;
    private List<String> fireableTransitions;
    private boolean saturated;
    private boolean filterActive;
    private boolean entryOpen;
    private int queueLength;
    private int threshold;
    private List<String> log;

    public PetriNetStateDTO() {
    }

    public PetriNetStateDTO(Map<String, Integer> marking,
            List<String> fireableTransitions,
            boolean saturated,
            boolean filterActive,
            boolean entryOpen,
            int queueLength,
            int threshold,
            List<String> log) {
        this.marking = marking;
        this.fireableTransitions = fireableTransitions;
        this.saturated = saturated;
        this.filterActive = filterActive;
        this.entryOpen = entryOpen;
        this.queueLength = queueLength;
        this.threshold = threshold;
        this.log = log;
    }

    public Map<String, Integer> getMarking() {
        return marking;
    }

    public void setMarking(Map<String, Integer> marking) {
        this.marking = marking;
    }

    public List<String> getFireableTransitions() {
        return fireableTransitions;
    }

    public void setFireableTransitions(List<String> fireableTransitions) {
        this.fireableTransitions = fireableTransitions;
    }

    public boolean isSaturated() {
        return saturated;
    }

    public void setSaturated(boolean saturated) {
        this.saturated = saturated;
    }

    public boolean isFilterActive() {
        return filterActive;
    }

    public void setFilterActive(boolean filterActive) {
        this.filterActive = filterActive;
    }

    public boolean isEntryOpen() {
        return entryOpen;
    }

    public void setEntryOpen(boolean entryOpen) {
        this.entryOpen = entryOpen;
    }

    public int getQueueLength() {
        return queueLength;
    }

    public void setQueueLength(int queueLength) {
        this.queueLength = queueLength;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public List<String> getLog() {
        return log;
    }

    public void setLog(List<String> log) {
        this.log = log;
    }
}