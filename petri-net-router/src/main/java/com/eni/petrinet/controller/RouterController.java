package com.eni.petrinet.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eni.petrinet.dto.PetriNetStateDTO;
import com.eni.petrinet.service.PetriNetSimulationRunner;
import com.eni.petrinet.service.RouterPetriNetService;

@RestController
@RequestMapping("/api/petrinet")
@CrossOrigin(origins = "http://localhost:3000") // autorise le frontend React en dev
public class RouterController {

    private final RouterPetriNetService petriNetService;
    private final PetriNetSimulationRunner simulationRunner;

    public RouterController(RouterPetriNetService petriNetService,
            PetriNetSimulationRunner simulationRunner) {
        this.petriNetService = petriNetService;
        this.simulationRunner = simulationRunner;
    }

    @GetMapping("/state")
    public PetriNetStateDTO getState() {
        return petriNetService.getState();
    }

    @PostMapping("/arrival")
    public Map<String, Object> simulateArrival() {
        boolean accepted = petriNetService.arrivalAttempt();
        Map<String, Object> response = new HashMap<>();
        response.put("accepted", accepted);
        response.put("state", petriNetService.getState());
        return response;
    }

    @PostMapping("/process")
    public Map<String, Object> simulateProcessing() {
        boolean processed = petriNetService.processAttempt();
        Map<String, Object> response = new HashMap<>();
        response.put("processed", processed);
        response.put("state", petriNetService.getState());
        return response;
    }

    @PostMapping("/ddos")
    public Map<String, Object> simulateDdos(@RequestParam(defaultValue = "20") int count) {
        int accepted = 0;
        int rejected = 0;
        for (int i = 0; i < count; i++) {
            if (petriNetService.arrivalAttempt()) {
                accepted++;
            } else {
                rejected++;
            }
        }
        Map<String, Object> response = new HashMap<>();
        response.put("requested", count);
        response.put("accepted", accepted);
        response.put("rejected", rejected);
        response.put("state", petriNetService.getState());
        return response;
    }

    @PostMapping("/reset-counter")
    public Map<String, Object> resetCounter() {
        boolean processed = petriNetService.resetCounterAttempt();
        Map<String, Object> response = new HashMap<>();
        response.put("processed", processed);
        response.put("state", petriNetService.getState());
        return response;
    }

    @PostMapping("/reset")
    public PetriNetStateDTO reset() {
        petriNetService.reset();
        return petriNetService.getState();
    }

    @PostMapping("/simulation/start")
    public Map<String, Object> startSimulation() {
        simulationRunner.start();
        Map<String, Object> response = new HashMap<>();
        response.put("running", simulationRunner.isRunning());
        return response;
    }

    @PostMapping("/simulation/stop")
    public Map<String, Object> stopSimulation() {
        simulationRunner.stop();
        Map<String, Object> response = new HashMap<>();
        response.put("running", simulationRunner.isRunning());
        return response;
    }

    @GetMapping("/simulation/status")
    public Map<String, Object> simulationStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("running", simulationRunner.isRunning());
        return response;
    }
}