package com.eni.petrinet.model;

public class Place {

    private final String id;
    private final String label;
    private int tokens;

    public Place(String id, String label, int initialTokens) {
        if (initialTokens < 0) {
            throw new IllegalArgumentException("Le marquage initial ne peut pas être négatif");
        }
        this.id = id;
        this.label = label;
        this.tokens = initialTokens;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public int getTokens() {
        return tokens;
    }

    public void addTokens(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Impossible d'ajouter un nombre négatif de jetons");
        }
        this.tokens += n;
    }

    public void removeTokens(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Impossible de retirer un nombre négatif de jetons");
        }
        if (this.tokens - n < 0) {
            throw new IllegalStateException(
                    "Marquage négatif interdit sur la place " + id + " (tokens=" + tokens + ", retrait=" + n + ")");
        }
        this.tokens -= n;
    }

    public boolean isEmpty() {
        return tokens == 0;
    }

    public boolean hasAtLeast(int n) {
        return tokens >= n;
    }

    @Override
    public String toString() {
        return id + "(" + label + ")=" + tokens;
    }
}