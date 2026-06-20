package com.eni.petrinet.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class Transition {

    private final String id;
    private final String label;

    private final Map<Place, Integer> inputArcs = new LinkedHashMap<>();
    private final Map<Place, Integer> outputArcs = new LinkedHashMap<>();
    private final Map<Place, Boolean> inhibitorArcs = new LinkedHashMap<>();

    public Transition(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public Transition addInput(Place place, int weight) {
        if (weight <= 0) {
            throw new IllegalArgumentException("Le poids d'un arc d'entrée doit être > 0");
        }
        inputArcs.put(place, weight);
        return this;
    }

    public Transition addOutput(Place place, int weight) {
        if (weight <= 0) {
            throw new IllegalArgumentException("Le poids d'un arc de sortie doit être > 0");
        }
        outputArcs.put(place, weight);
        return this;
    }

    public Transition addInhibitor(Place place) {
        inhibitorArcs.put(place, Boolean.TRUE);
        return this;
    }

    public Map<Place, Integer> getInputArcs() {
        return inputArcs;
    }

    public Map<Place, Integer> getOutputArcs() {
        return outputArcs;
    }

    public Map<Place, Boolean> getInhibitorArcs() {
        return inhibitorArcs;
    }

    public boolean isFireable() {
        for (Map.Entry<Place, Integer> entry : inputArcs.entrySet()) {
            if (!entry.getKey().hasAtLeast(entry.getValue())) {
                return false;
            }
        }
        for (Place inhibitorPlace : inhibitorArcs.keySet()) {
            if (!inhibitorPlace.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean fire() {
        if (!isFireable()) {
            return false;
        }
        for (Map.Entry<Place, Integer> entry : inputArcs.entrySet()) {
            entry.getKey().removeTokens(entry.getValue());
        }
        for (Map.Entry<Place, Integer> entry : outputArcs.entrySet()) {
            entry.getKey().addTokens(entry.getValue());
        }
        return true;
    }

    @Override
    public String toString() {
        return id + "(" + label + ")";
    }
}