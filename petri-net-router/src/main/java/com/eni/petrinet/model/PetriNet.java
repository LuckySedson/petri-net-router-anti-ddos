package com.eni.petrinet.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PetriNet {

    private final Map<String, Place> places = new LinkedHashMap<>();
    private final Map<String, Transition> transitions = new LinkedHashMap<>();

    public void addPlace(Place place) {
        places.put(place.getId(), place);
    }

    public void addTransition(Transition transition) {
        transitions.put(transition.getId(), transition);
    }

    public Place getPlace(String id) {
        Place p = places.get(id);
        if (p == null) {
            throw new IllegalArgumentException("Place inconnue : " + id);
        }
        return p;
    }

    public Transition getTransition(String id) {
        Transition t = transitions.get(id);
        if (t == null) {
            throw new IllegalArgumentException("Transition inconnue : " + id);
        }
        return t;
    }

    public List<Place> getPlaces() {
        return new ArrayList<>(places.values());
    }

    public List<Transition> getTransitions() {
        return new ArrayList<>(transitions.values());
    }

    public List<Transition> getFireableTransitions() {
        List<Transition> fireable = new ArrayList<>();
        for (Transition t : transitions.values()) {
            if (t.isFireable()) {
                fireable.add(t);
            }
        }
        return fireable;
    }

    public boolean fire(String transitionId) {
        Transition t = transitions.get(transitionId);
        if (t == null) {
            return false;
        }
        return t.fire();
    }

    public Map<String, Integer> getMarking() {
        Map<String, Integer> marking = new LinkedHashMap<>();
        for (Place p : places.values()) {
            marking.put(p.getId(), p.getTokens());
        }
        return marking;
    }
}