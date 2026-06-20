package com.eni.petrinet.service;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.eni.petrinet.dto.PetriNetStateDTO;

@Component
public class PetriNetSimulationRunner {

    private static final long INTERVAL_MS = 800; // rythme simulation auto

    private final RouterPetriNetService petriNetService;
    private final SimpMessagingTemplate messagingTemplate;

    private final AtomicBoolean running = new AtomicBoolean(false);

    public PetriNetSimulationRunner(RouterPetriNetService petriNetService,
            SimpMessagingTemplate messagingTemplate) {
        this.petriNetService = petriNetService;
        this.messagingTemplate = messagingTemplate;
    }

    public void start() {
        running.set(true);
    }

    public void stop() {
        running.set(false);
    }

    public boolean isRunning() {
        return running.get();
    }

    @Scheduled(fixedRate = INTERVAL_MS)
    public void tick() {
        if (running.get()) {
            petriNetService.simulationStep(false, false);
        }
        broadcastState();
    }

    private void broadcastState() {
        PetriNetStateDTO state = petriNetService.getState();
        messagingTemplate.convertAndSend("/topic/petrinet-state", state);
    }
}