package com.medflow.controller;

import com.medflow.dto.BankerRequestDto;
import com.medflow.dto.BankerResponseDto;
import com.medflow.dto.ProcessTaskDto;
import com.medflow.dto.ScheduleResponseDto;
import com.medflow.simulation.deadlock.BankersAlgorithmService;
import com.medflow.simulation.scheduler.SchedulerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * =============================================================================
 * SimulationController
 * 
 * Exposes REST endpoints to trigger Operating Systems simulation algorithms:
 * 1. CPU Scheduling (FCFS, Priority, Round Robin, SJF/SRTF) -> /api/simulation/schedule
 * 2. Deadlock Avoidance (Banker's Algorithm)                -> /api/simulation/banker-check
 * 3. Deadlock Unsafe Demo (Deadlock Detection Demo)        -> /api/simulation/banker-unsafe-demo
 * =============================================================================
 */
@RestController
@RequestMapping("/api/simulation")
@CrossOrigin(origins = "*")
public class SimulationController {

    private final SchedulerService schedulerService;
    private final BankersAlgorithmService bankersAlgorithmService;

    public SimulationController(SchedulerService schedulerService, BankersAlgorithmService bankersAlgorithmService) {
        this.schedulerService = schedulerService;
        this.bankersAlgorithmService = bankersAlgorithmService;
    }

    /**
     * GET /api/simulation/schedule
     */
    @GetMapping("/schedule")
    public ResponseEntity<ScheduleResponseDto> runScheduleGet(
            @RequestParam(name = "algorithm", defaultValue = "FCFS") String algorithm,
            @RequestParam(name = "quantum", defaultValue = "20") int quantum) {
        return ResponseEntity.ok(executeScheduling(algorithm, quantum, null));
    }

    /**
     * POST /api/simulation/schedule
     */
    @PostMapping("/schedule")
    public ResponseEntity<ScheduleResponseDto> runSchedulePost(
            @RequestParam(name = "algorithm", defaultValue = "FCFS") String algorithm,
            @RequestParam(name = "quantum", defaultValue = "20") int quantum,
            @RequestBody(required = false) List<ProcessTaskDto> tasks) {
        return ResponseEntity.ok(executeScheduling(algorithm, quantum, tasks));
    }

    /**
     * GET /api/simulation/banker-check
     */
    @GetMapping("/banker-check")
    public ResponseEntity<BankerResponseDto> checkBankerSafetyGet() {
        return ResponseEntity.ok(bankersAlgorithmService.checkSafety(null));
    }

    /**
     * POST /api/simulation/banker-check
     */
    @PostMapping("/banker-check")
    public ResponseEntity<BankerResponseDto> checkBankerSafetyPost(@RequestBody(required = false) BankerRequestDto request) {
        return ResponseEntity.ok(bankersAlgorithmService.checkSafety(request));
    }

    /**
     * GET /api/simulation/banker-unsafe-demo
     */
    @GetMapping("/banker-unsafe-demo")
    public ResponseEntity<BankerResponseDto> demoBankerUnsafe() {
        BankerResponseDto response = bankersAlgorithmService.checkSafety(bankersAlgorithmService.getUnsafeHospitalRequest());
        return ResponseEntity.ok(response);
    }

    private ScheduleResponseDto executeScheduling(String algorithm, int quantum, List<ProcessTaskDto> tasks) {
        if ("PRIORITY".equalsIgnoreCase(algorithm)) {
            return schedulerService.schedulePriority(tasks);
        } else if ("ROUND_ROBIN".equalsIgnoreCase(algorithm) || "RR".equalsIgnoreCase(algorithm)) {
            return schedulerService.scheduleRoundRobin(tasks, quantum);
        } else if ("SJF".equalsIgnoreCase(algorithm)) {
            return schedulerService.scheduleSJF(tasks, false);
        } else if ("SRTF".equalsIgnoreCase(algorithm)) {
            return schedulerService.scheduleSJF(tasks, true);
        } else {
            return schedulerService.scheduleFCFS(tasks);
        }
    }
}
