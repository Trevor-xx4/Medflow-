package com.medflow.simulation.scheduler;

import com.medflow.dto.GanttBlockDto;
import com.medflow.dto.ProcessTaskDto;
import com.medflow.dto.ScheduleResponseDto;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * =============================================================================
 * ⏱️ SchedulerService
 * =============================================================================
 * 
 * Implements Operating Systems CPU Process Scheduling Algorithms adapted for
 * Hospital Emergency Triage and Clinical Workflow Management.
 * 
 * 🎓 VIVA EXPLANATION FOR TEACHER:
 * "Sir/Ma'am, in a hospital, medical resources (doctors, operating theaters) are
 * finite, just like CPU cores. Incoming patient treatments are processes with an
 * Arrival Time (AT), Burst Time (BT - procedure duration), and Priority Level.
 * We implemented 4 classical scheduling algorithms:
 * 1. FCFS (First-Come, First-Served) - Baseline queue order for routine checkups.
 * 2. Priority Scheduling (Emergency Triage) - Preemptive scheduling where critical
 *    Emergency patients (Priority 1) preempt routine appointments (Priority 3).
 * 3. Round Robin (RR) - Fair time-sliced allocation where each patient receives
 *    up to 'q' minutes per doctor round.
 * 4. SJF / SRTF - Minimizes overall waiting time for quick diagnostic procedures."
 * 
 * 📐 CORE METRIC FORMULAS:
 * 1. Completion Time (CT): Simulation timestamp when process finishes execution.
 * 2. Turnaround Time (TAT): Total elapsed time from arrival to completion.
 *    Formula: TAT = Completion Time (CT) - Arrival Time (AT)
 * 3. Waiting Time (WT): Total time process spent waiting in the ready queue.
 *    Formula: WT = Turnaround Time (TAT) - Burst Time (BT)
 * 4. Average Waiting Time = Sum(WT) / N
 * 5. Average Turnaround Time = Sum(TAT) / N
 * =============================================================================
 */
@Service
public class SchedulerService {

    /**
     * Simulates First-Come, First-Served (FCFS) Scheduling.
     * 
     * 🎓 VIVA EXPLANATION:
     * "FCFS is a Non-Preemptive scheduling algorithm. The process that arrives first
     * is allocated the CPU first and runs to completion. If there is a time gap before
     * the next patient arrives, the doctor is marked as IDLE."
     * 
     * @param tasks List of patient processes (ProcessTaskDto)
     * @return ScheduleResponseDto with timeline Gantt chart and calculated metrics
     */
    public ScheduleResponseDto scheduleFCFS(List<ProcessTaskDto> tasks) {
        // Prepare tasks or load canonical hospital defaults from oracle_seed.sql
        List<ProcessTaskDto> processList = prepareTasks(tasks);

        // FCFS Rule: Sort tasks strictly by Arrival Time ascending
        processList.sort(Comparator.comparingInt(ProcessTaskDto::getArrivalTime));

        List<GanttBlockDto> ganttChart = new ArrayList<>();
        int currentTime = 0;
        double totalWaitingTime = 0;
        double totalTurnaroundTime = 0;

        for (ProcessTaskDto task : processList) {
            // Edge Case: If CPU/Doctor is idle waiting for the next patient to arrive
            if (currentTime < task.getArrivalTime()) {
                ganttChart.add(new GanttBlockDto(
                        "IDLE",
                        "Idle (Awaiting Patient)",
                        currentTime,
                        task.getArrivalTime()
                ));
                currentTime = task.getArrivalTime(); // Advance simulation clock to arrival
            }

            int startTime = currentTime;
            int endTime = startTime + task.getBurstTime();
            currentTime = endTime; // Non-preemptive: process runs uninterrupted until endTime

            // Calculate standard OS metrics
            int completionTime = endTime;
            int turnaroundTime = completionTime - task.getArrivalTime(); // TAT = CT - AT
            int waitingTime = turnaroundTime - task.getBurstTime();        // WT = TAT - BT

            totalWaitingTime += waitingTime;
            totalTurnaroundTime += turnaroundTime;

            String label = task.getProcessId() + ": " + task.getPatientName() + " (" + task.getBurstTime() + "m)";
            ganttChart.add(new GanttBlockDto(task.getProcessId(), label, startTime, endTime));
        }

        int n = processList.size();
        double avgWait = roundTwoDecimals(totalWaitingTime / n);
        double avgTat = roundTwoDecimals(totalTurnaroundTime / n);

        return new ScheduleResponseDto(
                "FCFS (First-Come, First-Served)",
                ganttChart,
                avgWait,
                avgTat,
                "FCFS completed successfully for " + n + " processes. Avg Waiting: " + avgWait + "m, Avg TAT: " + avgTat + "m."
        );
    }

    /**
     * Simulates Preemptive Priority Scheduling (Emergency Triage).
     * Priority: 1 = Emergency (Highest Urgency), 2 = Urgent, 3 = Routine.
     */
    public ScheduleResponseDto schedulePriority(List<ProcessTaskDto> tasks) {
        return schedulePriority(tasks, true);
    }

    /**
     * Simulates Priority Scheduling with configurable preemption.
     * 
     * 🎓 VIVA EXPLANATION:
     * "In Preemptive Priority Scheduling, when a higher-priority process arrives (e.g.
     * Priority 1 Emergency), the currently running lower-priority process (Priority 2 or 3)
     * is immediately preempted and put back in the ready queue. The higher-priority
     * process executes until completion or until an even higher priority task arrives.
     * In this implementation, consecutive time slices of the same process are merged
     * for a clean, professional Gantt timeline."
     */
    public ScheduleResponseDto schedulePriority(List<ProcessTaskDto> tasks, boolean preemptive) {
        List<ProcessTaskDto> processList = prepareTasks(tasks);
        int n = processList.size();

        // Internal Process Control Block (PCB) state wrapper
        class TaskState {
            final ProcessTaskDto original;
            final int index;
            int remainingBurst; // Remaining time units needed
            int completionTime;

            TaskState(ProcessTaskDto original, int index) {
                this.original = original;
                this.index = index;
                this.remainingBurst = original.getBurstTime();
            }
        }

        List<TaskState> states = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            states.add(new TaskState(processList.get(i), i));
        }

        List<GanttBlockDto> ganttChart = new ArrayList<>();
        int completed = 0;
        int currentTime = states.stream().mapToInt(s -> s.original.getArrivalTime()).min().orElse(0);

        TaskState currentRunning = null;
        int blockStartTime = currentTime;

        while (completed < n) {
            // Find all processes that have arrived and are not yet completed
            List<TaskState> available = new ArrayList<>();
            for (TaskState s : states) {
                if (s.original.getArrivalTime() <= currentTime && s.remainingBurst > 0) {
                    available.add(s);
                }
            }

            // If no patient has arrived yet, mark doctor as IDLE and jump to next arrival
            if (available.isEmpty()) {
                if (currentRunning != null) {
                    addGanttBlock(ganttChart, currentRunning.original, blockStartTime, currentTime);
                    currentRunning = null;
                }
                int nextArrival = states.stream()
                        .filter(s -> s.remainingBurst > 0)
                        .mapToInt(s -> s.original.getArrivalTime())
                        .min()
                        .orElse(currentTime + 1);

                ganttChart.add(new GanttBlockDto("IDLE", "Idle (Awaiting Patient)", currentTime, nextArrival));
                currentTime = nextArrival;
                blockStartTime = currentTime;
                continue;
            }

            // Priority Selection: Lowest integer value = Highest urgency (1 < 2 < 3)
            // Tie-breaker: earlier arrival time, then index
            TaskState highest = available.stream()
                    .min(Comparator.comparingInt((TaskState s) -> s.original.getPriority())
                            .thenComparingInt(s -> s.original.getArrivalTime())
                            .thenComparingInt(s -> s.index))
                    .orElse(available.get(0));

            if (!preemptive) {
                // Non-preemptive mode: Process runs to completion
                if (currentRunning != null && currentRunning != highest) {
                    addGanttBlock(ganttChart, currentRunning.original, blockStartTime, currentTime);
                }
                currentRunning = highest;
                blockStartTime = currentTime;
                currentTime += currentRunning.remainingBurst;
                currentRunning.remainingBurst = 0;
                currentRunning.completionTime = currentTime;
                completed++;
                addGanttBlock(ganttChart, currentRunning.original, blockStartTime, currentTime);
                currentRunning = null;
                continue;
            }

            // Preemptive Mode: Context switch check
            if (currentRunning != highest) {
                if (currentRunning != null) {
                    addGanttBlock(ganttChart, currentRunning.original, blockStartTime, currentTime);
                }
                currentRunning = highest;
                blockStartTime = currentTime;
            }

            // Event-Driven Step: Run until this process finishes OR a higher-priority patient arrives
            int step = currentRunning.remainingBurst;
            for (TaskState s : states) {
                if (s.remainingBurst > 0 && s.original.getArrivalTime() > currentTime) {
                    // Check if future arriving process has STRICTLY higher priority (lower number)
                    if (s.original.getPriority() < currentRunning.original.getPriority()) {
                        int timeUntilArrival = s.original.getArrivalTime() - currentTime;
                        if (timeUntilArrival > 0 && timeUntilArrival < step) {
                            step = timeUntilArrival; // Preemption will occur at this arrival!
                        }
                    }
                }
            }

            // Execute the step
            currentTime += step;
            currentRunning.remainingBurst -= step;

            // If task finished, record completion time and record Gantt slice
            if (currentRunning.remainingBurst == 0) {
                currentRunning.completionTime = currentTime;
                completed++;
                addGanttBlock(ganttChart, currentRunning.original, blockStartTime, currentTime);
                currentRunning = null;
            }
        }

        // Calculate Average Waiting and Turnaround Times
        double totalWaitingTime = 0;
        double totalTurnaroundTime = 0;

        for (TaskState s : states) {
            int tat = s.completionTime - s.original.getArrivalTime(); // TAT = CT - AT
            int wt = tat - s.original.getBurstTime();                 // WT = TAT - BT
            totalTurnaroundTime += tat;
            totalWaitingTime += wt;
        }

        double avgWait = roundTwoDecimals(totalWaitingTime / n);
        double avgTat = roundTwoDecimals(totalTurnaroundTime / n);

        String algoTitle = preemptive ? "Priority Scheduling (Emergency Triage Preemption)" : "Priority Scheduling (Non-Preemptive)";
        return new ScheduleResponseDto(
                algoTitle,
                ganttChart,
                avgWait,
                avgTat,
                "Priority Schedule completed successfully. Emergency triage orders enforced."
        );
    }

    /**
     * Simulates Round Robin (RR) Scheduling with a given Time Quantum.
     * 
     * 🎓 VIVA EXPLANATION:
     * "Round Robin is designed for time-sharing systems. Each process gets a fixed
     * unit of CPU time (quantum q). If it doesn't complete within that quantum, it is
     * preempted and added to the tail of the ready queue.
     * 
     * CRITICAL OS DETAIL: Any new processes arriving while the current slice is executing
     * must be enqueued BEFORE the preempted process is re-added! This ensures strict
     * FIFO fairness."
     * 
     * @param tasks List of patient processes
     * @param timeQuantum Fixed time slice in minutes (default: 20 min)
     */
    public ScheduleResponseDto scheduleRoundRobin(List<ProcessTaskDto> tasks, int timeQuantum) {
        if (timeQuantum <= 0) {
            timeQuantum = 20; // Default time quantum
        }

        List<ProcessTaskDto> processList = prepareTasks(tasks);
        int n = processList.size();

        class RRTaskState {
            final ProcessTaskDto original;
            int remainingBurst;
            int completionTime;
            boolean inQueue;

            RRTaskState(ProcessTaskDto original) {
                this.original = original;
                this.remainingBurst = original.getBurstTime();
                this.inQueue = false;
            }
        }

        List<RRTaskState> states = new ArrayList<>();
        for (ProcessTaskDto p : processList) {
            states.add(new RRTaskState(p));
        }

        // Sort by arrival time initially to enqueue fairly
        states.sort(Comparator.comparingInt(s -> s.original.getArrivalTime()));

        Queue<RRTaskState> readyQueue = new LinkedList<>();
        List<GanttBlockDto> ganttChart = new ArrayList<>();
        int currentTime = states.get(0).original.getArrivalTime();
        int completed = 0;

        // Enqueue processes that have arrived at initial time
        for (RRTaskState s : states) {
            if (s.original.getArrivalTime() <= currentTime && !s.inQueue) {
                readyQueue.add(s);
                s.inQueue = true;
            }
        }

        while (completed < n) {
            if (readyQueue.isEmpty()) {
                // If queue is empty, advance clock to next arrival
                int nextArrival = Integer.MAX_VALUE;
                for (RRTaskState s : states) {
                    if (s.remainingBurst > 0 && s.original.getArrivalTime() < nextArrival) {
                        nextArrival = s.original.getArrivalTime();
                    }
                }

                if (nextArrival > currentTime && nextArrival != Integer.MAX_VALUE) {
                    ganttChart.add(new GanttBlockDto("IDLE", "Idle (Awaiting Patient)", currentTime, nextArrival));
                    currentTime = nextArrival;
                }

                for (RRTaskState s : states) {
                    if (s.remainingBurst > 0 && s.original.getArrivalTime() <= currentTime && !s.inQueue) {
                        readyQueue.add(s);
                        s.inQueue = true;
                    }
                }
                continue;
            }

            RRTaskState current = readyQueue.poll();
            current.inQueue = false;

            // Execute for min(remainingBurst, timeQuantum)
            int slice = Math.min(current.remainingBurst, timeQuantum);
            int startTime = currentTime;
            int endTime = startTime + slice;
            currentTime = endTime;
            current.remainingBurst -= slice;

            // 🎓 CRITICAL OS RULE: Enqueue any new processes that arrived during this slice
            // BEFORE placing the current process back into the ready queue
            for (RRTaskState s : states) {
                if (s != current && s.remainingBurst > 0 && s.original.getArrivalTime() <= currentTime && !s.inQueue) {
                    readyQueue.add(s);
                    s.inQueue = true;
                }
            }

            if (current.remainingBurst > 0) {
                // Process not finished: Re-enqueue at the end of the ready queue
                readyQueue.add(current);
                current.inQueue = true;
                String label = current.original.getProcessId() + ": " + current.original.getPatientName() + " (Slice)";
                ganttChart.add(new GanttBlockDto(current.original.getProcessId(), label, startTime, endTime));
            } else {
                // Process completed: Record completion time
                current.completionTime = currentTime;
                completed++;
                String label = current.original.getProcessId() + ": " + current.original.getPatientName() + " (Completed)";
                ganttChart.add(new GanttBlockDto(current.original.getProcessId(), label, startTime, endTime));
            }
        }

        // Metrics calculation
        double totalWaitingTime = 0;
        double totalTurnaroundTime = 0;

        for (RRTaskState s : states) {
            int tat = s.completionTime - s.original.getArrivalTime();
            int wt = tat - s.original.getBurstTime();
            totalTurnaroundTime += tat;
            totalWaitingTime += wt;
        }

        double avgWait = roundTwoDecimals(totalWaitingTime / n);
        double avgTat = roundTwoDecimals(totalTurnaroundTime / n);

        return new ScheduleResponseDto(
                "Round Robin (Quantum = " + timeQuantum + " min)",
                ganttChart,
                avgWait,
                avgTat,
                "Round Robin scheduling completed with quantum = " + timeQuantum + " minutes."
        );
    }

    /**
     * Shortest Job First (SJF) / Shortest Remaining Time First (SRTF) Scheduling.
     * Bonus algorithm for viva demonstrating optimal average waiting time.
     * 
     * 🎓 VIVA EXPLANATION:
     * "SJF is provably optimal in terms of minimizing average waiting time.
     * In preemptive mode (SRTF), if a newly arrived process has a burst time shorter
     * than the remaining burst of the currently running process, preemption occurs."
     */
    public ScheduleResponseDto scheduleSJF(List<ProcessTaskDto> tasks, boolean preemptive) {
        List<ProcessTaskDto> processList = prepareTasks(tasks);
        int n = processList.size();

        class SJFState {
            final ProcessTaskDto original;
            int remainingBurst;
            int completionTime;

            SJFState(ProcessTaskDto original) {
                this.original = original;
                this.remainingBurst = original.getBurstTime();
            }
        }

        List<SJFState> states = new ArrayList<>();
        for (ProcessTaskDto p : processList) {
            states.add(new SJFState(p));
        }

        List<GanttBlockDto> ganttChart = new ArrayList<>();
        int completed = 0;
        int currentTime = states.stream().mapToInt(s -> s.original.getArrivalTime()).min().orElse(0);

        SJFState currentRunning = null;
        int blockStartTime = currentTime;

        while (completed < n) {
            List<SJFState> available = new ArrayList<>();
            for (SJFState s : states) {
                if (s.original.getArrivalTime() <= currentTime && s.remainingBurst > 0) {
                    available.add(s);
                }
            }

            if (available.isEmpty()) {
                if (currentRunning != null) {
                    addGanttBlock(ganttChart, currentRunning.original, blockStartTime, currentTime);
                    currentRunning = null;
                }
                int nextArrival = states.stream()
                        .filter(s -> s.remainingBurst > 0)
                        .mapToInt(s -> s.original.getArrivalTime())
                        .min()
                        .orElse(currentTime + 1);

                ganttChart.add(new GanttBlockDto("IDLE", "Idle (Awaiting Patient)", currentTime, nextArrival));
                currentTime = nextArrival;
                blockStartTime = currentTime;
                continue;
            }

            // Pick shortest remaining burst
            SJFState shortest = available.stream()
                    .min(Comparator.comparingInt((SJFState s) -> s.remainingBurst)
                            .thenComparingInt(s -> s.original.getArrivalTime()))
                    .orElse(available.get(0));

            if (!preemptive) {
                // Non-preemptive SJF
                currentRunning = shortest;
                blockStartTime = currentTime;
                currentTime += currentRunning.remainingBurst;
                currentRunning.remainingBurst = 0;
                currentRunning.completionTime = currentTime;
                completed++;
                addGanttBlock(ganttChart, currentRunning.original, blockStartTime, currentTime);
                currentRunning = null;
                continue;
            }

            // Preemptive SRTF
            if (currentRunning != shortest) {
                if (currentRunning != null) {
                    addGanttBlock(ganttChart, currentRunning.original, blockStartTime, currentTime);
                }
                currentRunning = shortest;
                blockStartTime = currentTime;
            }

            currentTime += 1;
            currentRunning.remainingBurst -= 1;

            if (currentRunning.remainingBurst == 0) {
                currentRunning.completionTime = currentTime;
                completed++;
                addGanttBlock(ganttChart, currentRunning.original, blockStartTime, currentTime);
                currentRunning = null;
            }
        }

        double totalWaitingTime = 0;
        double totalTurnaroundTime = 0;

        for (SJFState s : states) {
            int tat = s.completionTime - s.original.getArrivalTime();
            int wt = tat - s.original.getBurstTime();
            totalTurnaroundTime += tat;
            totalWaitingTime += wt;
        }

        double avgWait = roundTwoDecimals(totalWaitingTime / n);
        double avgTat = roundTwoDecimals(totalTurnaroundTime / n);

        String title = preemptive ? "SRTF (Shortest Remaining Time First)" : "SJF (Shortest Job First)";
        return new ScheduleResponseDto(
                title,
                ganttChart,
                avgWait,
                avgTat,
                "SJF/SRTF simulation finished successfully."
        );
    }

    /**
     * Helper to sanitize and validate incoming tasks.
     * If user passes null or empty list, returns the canonical hospital treatments from oracle_seed.sql.
     */
    public List<ProcessTaskDto> prepareTasks(List<ProcessTaskDto> tasks) {
        if (tasks != null && !tasks.isEmpty()) {
            List<ProcessTaskDto> copy = new ArrayList<>();
            for (ProcessTaskDto t : tasks) {
                if (t != null) {
                    int burst = Math.max(1, t.getBurstTime());
                    int arrival = Math.max(0, t.getArrivalTime());
                    int priority = t.getPriority() <= 0 ? 3 : t.getPriority();
                    String pid = t.getProcessId() != null ? t.getProcessId() : "P" + (copy.size() + 1);
                    String name = t.getPatientName() != null ? t.getPatientName() : "Patient " + pid;
                    copy.add(new ProcessTaskDto(pid, name, arrival, burst, priority));
                }
            }
            if (!copy.isEmpty()) {
                return copy;
            }
        }
        return getDefaultHospitalTasks();
    }

    /**
     * Canonical MedFlow Hospital Tasks matching database treatments in oracle_seed.sql:
     * - T1: John Doe, Arr: 0, Burst: 45, Prio: 1 (Emergency Angioplasty)
     * - T2: Anita Roy, Arr: 10, Burst: 30, Prio: 2 (Severe Asthma Ventilation)
     * - T3: Robert Smith, Arr: 15, Burst: 20, Prio: 3 (Cardiac Follow-up & Monitoring)
     * - T4: Pooja Verma, Arr: 25, Burst: 50, Prio: 1 (Acute Trauma Stabilization)
     */
    public List<ProcessTaskDto> getDefaultHospitalTasks() {
        return Arrays.asList(
                new ProcessTaskDto("T1", "John Doe (Angioplasty)", 0, 45, 1),
                new ProcessTaskDto("T2", "Anita Roy (Ventilation)", 10, 30, 2),
                new ProcessTaskDto("T3", "Robert Smith (Checkup)", 15, 20, 3),
                new ProcessTaskDto("T4", "Pooja Verma (Trauma)", 25, 50, 1)
        );
    }

    private void addGanttBlock(List<GanttBlockDto> chart, ProcessTaskDto task, int start, int end) {
        if (start >= end) return;
        // Merge with previous block if it is the same process (avoids fragmented Gantt blocks)
        if (!chart.isEmpty()) {
            GanttBlockDto prev = chart.get(chart.size() - 1);
            if (prev.getProcessId().equals(task.getProcessId()) && prev.getEndTime() == start) {
                prev.setEndTime(end);
                return;
            }
        }
        String label = task.getProcessId() + ": " + task.getPatientName();
        chart.add(new GanttBlockDto(task.getProcessId(), label, start, end));
    }

    private double roundTwoDecimals(double val) {
        return Math.round(val * 100.0) / 100.0;
    }
}
