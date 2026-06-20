package com.eni.petrinet.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.stereotype.Service;

import com.eni.petrinet.dto.PetriNetStateDTO;
import com.eni.petrinet.model.PetriNet;
import com.eni.petrinet.model.Place;
import com.eni.petrinet.model.Transition;

import jakarta.annotation.PostConstruct;

@Service
public class RouterPetriNetService {

    public static final int DEFAULT_THRESHOLD_K = 10;

    private final int threshold = DEFAULT_THRESHOLD_K;
    private final PetriNet net = new PetriNet();
    private final ReentrantLock lock = new ReentrantLock();
    private final Random random = new Random();

    private final LinkedList<String> log = new LinkedList<>();
    private static final int MAX_LOG_SIZE = 50;

    @PostConstruct
    public void init() {
        buildNetwork();
        addLog("Réseau initialisé. Seuil de saturation K=" + threshold);
    }

    private void buildNetwork() {
        Place p1 = new Place("P1", "Entrée_Ouverte", 1);
        Place p2 = new Place("P2", "File_Attente_Paquets", 0);
        Place p3 = new Place("P3", "Routeur_Saturé", 0);
        Place p4 = new Place("P4", "Filtre_Actif", 0);

        net.addPlace(p1);
        net.addPlace(p2);
        net.addPlace(p3);
        net.addPlace(p4);

        Transition t1 = new Transition("T1", "Arrivée_Paquet");
        t1.addOutput(p2, 1);
        t1.addInhibitor(p3);

        Transition t2 = new Transition("T2", "Traiter_Paquet");
        t2.addInput(p2, 1);

        Transition t3 = new Transition("T3", "Déclencher_Protection");
        t3.addInput(p2, threshold);
        t3.addInput(p1, 1);
        t3.addOutput(p3, 1);
        t3.addOutput(p4, 1);

        Transition t4 = new Transition("T4", "Réinitialiser_Sécurité");
        t4.addInput(p3, 1);
        t4.addInput(p4, 1);
        t4.addOutput(p1, 1);
        t4.addInhibitor(p2);

        net.addTransition(t1);
        net.addTransition(t2);
        net.addTransition(t3);
        net.addTransition(t4);
    }

    public boolean arrivalAttempt() {
        lock.lock();
        try {
            boolean fired = net.fire("T1");
            if (fired) {
                addLog("T1 — Paquet accepté. File P2=" + net.getPlace("P2").getTokens());
            } else {
                addLog("T1 — Paquet REJETÉ (entrée fermée / routeur saturé)");
            }
            checkAutoTransitions();
            return fired;
        } finally {
            lock.unlock();
        }
    }

    public boolean processAttempt() {
        lock.lock();
        try {
            boolean fired = net.fire("T2");
            if (fired) {
                addLog("T2 — Paquet traité. File P2=" + net.getPlace("P2").getTokens());
            }
            checkAutoTransitions();
            return fired;
        } finally {
            lock.unlock();
        }
    }

    private void checkAutoTransitions() {
        if (net.getTransition("T3").isFireable()) {
            net.fire("T3");
            addLog("⚠ T3 — SATURATION DÉTECTÉE (P2 >= " + threshold + "). Entrée fermée, filtre activé.");
        }
        if (net.getTransition("T4").isFireable()) {
            net.fire("T4");
            addLog("✔ T4 — File purgée (P2=0). Sécurité réinitialisée, entrée ré-ouverte.");
        }
    }

    public void simulationStep(boolean forceArrival, boolean forceProcess) {
        lock.lock();
        try {
            if (forceArrival || random.nextDouble() < 0.6) {
                net.fire("T1");
            }
            if (forceProcess || random.nextDouble() < 0.45) {
                net.fire("T2");
            }
            checkAutoTransitions();
        } finally {
            lock.unlock();
        }
    }

    public void reset() {
        lock.lock();
        try {
            net.getPlace("P1").removeTokens(net.getPlace("P1").getTokens());
            net.getPlace("P1").addTokens(1);
            net.getPlace("P2").removeTokens(net.getPlace("P2").getTokens());
            net.getPlace("P3").removeTokens(net.getPlace("P3").getTokens());
            net.getPlace("P4").removeTokens(net.getPlace("P4").getTokens());
            log.clear();
            addLog("Réseau réinitialisé manuellement.");
        } finally {
            lock.unlock();
        }
    }

    // Lecture d'état (pour l'API REST / WebSocket)

    public PetriNetStateDTO getState() {
        lock.lock();
        try {
            List<String> fireable = new ArrayList<>();
            for (Transition t : net.getFireableTransitions()) {
                fireable.add(t.getId());
            }
            return new PetriNetStateDTO(
                    net.getMarking(),
                    fireable,
                    net.getPlace("P3").getTokens() > 0,
                    net.getPlace("P4").getTokens() > 0,
                    net.getPlace("P1").getTokens() > 0,
                    net.getPlace("P2").getTokens(),
                    threshold,
                    new ArrayList<>(log));
        } finally {
            lock.unlock();
        }
    }

    private void addLog(String message) {
        log.addFirst(message);
        while (log.size() > MAX_LOG_SIZE) {
            log.removeLast();
        }
    }
}