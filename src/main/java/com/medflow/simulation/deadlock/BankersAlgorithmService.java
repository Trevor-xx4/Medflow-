package com.medflow.simulation.deadlock;

import com.medflow.dto.BankerRequestDto;
import com.medflow.dto.BankerResponseDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * =============================================================================
 * 🏥 BankersAlgorithmService
 * =============================================================================
 * 
 * Implements Edsger Dijkstra's Banker's Algorithm for Deadlock Avoidance.
 * 
 * 🎓 VIVA EXPLANATION FOR TEACHER:
 * "Sir/Ma'am, Banker's Algorithm is a Deadlock Avoidance algorithm. It guarantees
 * that the system will never enter an unsafe state. Before allocating any scarce
 * resource (like an ICU Bed or Ventilator), it simulates whether there is at least
 * one safe order (Safe Sequence) in which every patient can receive their maximum
 * required resources, finish treatment, and return the resources to the hospital.
 * If such a sequence exists, the hospital is in a SAFE STATE."
 * 
 * 🔑 KEY DATA STRUCTURES (Dijkstra's Formal Definitions):
 * 1. Available Vector [m]: Number of currently free instances of each resource type.
 * 2. Max Matrix [n x m]: Maximum resources that process i can ever claim.
 * 3. Allocation Matrix [n x m]: Resources currently held by process i.
 * 4. Need Matrix [n x m]: Remaining resources needed by process i to finish.
 *    Formula: Need[i][j] = Max[i][j] - Allocation[i][j]
 * 5. Work Vector [m]: Working copy of Available used during simulation.
 * 6. Finish Array [n]: Boolean flags tracking if process i has finished.
 * 
 * 🏥 HOSPITAL DOMAIN MAPPING:
 * - Processes = Patient Treatments (T1, T2, T3, T4)
 * - Resources = ICU Beds (R0), Ventilators (R1), Operation Theaters (R2)
 * =============================================================================
 */
@Service
public class BankersAlgorithmService {

    /**
     * Executes the Banker's Safety Algorithm to verify if the hospital is in a Safe State.
     * 
     * 🎓 VIVA EXPLANATION:
     * "This method calculates the Need matrix, initializes Work = Available and
     * Finish[i] = false, then iteratively searches for a process whose Need can be
     * satisfied by the currently available Work vector. Once found, that process completes,
     * releases its held resources back to Work, and the search repeats."
     * 
     * @param request Input matrices (Available, Max, Allocation, Process & Resource names)
     * @return BankerResponseDto with safety verdict, safe sequence, and audit trace
     */
    public BankerResponseDto checkSafety(BankerRequestDto request) {
        // Fallback: If no custom matrices were passed by the caller, load canonical hospital scenario
        if (request == null || request.getAvailable() == null) {
            request = getDefaultHospitalRequest();
        }

        List<String> processNames = request.getProcessNames();
        List<String> resourceNames = request.getResourceNames();
        int[] available = request.getAvailable();
        int[][] maxMatrix = request.getMaxMatrix();
        int[][] allocationMatrix = request.getAllocationMatrix();

        // ---------------------------------------------------------------------
        // STEP 1: Input Validation & Dimension Verification
        // ---------------------------------------------------------------------
        // In an OS, the kernel must ensure matrix dimensions match process (n) and resource (m) counts
        if (available == null || maxMatrix == null || allocationMatrix == null) {
            return new BankerResponseDto(false, new ArrayList<>(), "Validation Error: Available, Max, or Allocation matrix is null.");
        }

        int n = maxMatrix.length;         // Total number of processes (Patients)
        int m = available.length;         // Total number of resource types (Assets)

        if (allocationMatrix.length != n) {
            return new BankerResponseDto(false, new ArrayList<>(), "Validation Error: Process count mismatch between Max and Allocation matrices.");
        }

        // Auto-fill default process identifiers if missing
        if (processNames == null || processNames.size() < n) {
            processNames = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                processNames.add("P" + i);
            }
        }

        // Auto-fill default resource identifiers if missing
        if (resourceNames == null || resourceNames.size() < m) {
            resourceNames = new ArrayList<>();
            for (int j = 0; j < m; j++) {
                resourceNames.add("R" + j);
            }
        }

        // ---------------------------------------------------------------------
        // STEP 2: Compute the NEED Matrix
        // Formula: Need[i][j] = Max[i][j] - Allocation[i][j]
        // ---------------------------------------------------------------------
        // 🎓 VIVA TIP: Explain that Need represents the MAXIMUM remaining resources
        // a patient might still request before being discharged.
        int[][] needMatrix = new int[n][m];
        for (int i = 0; i < n; i++) {
            if (maxMatrix[i].length != m || allocationMatrix[i].length != m) {
                return new BankerResponseDto(false, new ArrayList<>(), 
                        "Validation Error: Resource column count mismatch for process " + processNames.get(i));
            }
            for (int j = 0; j < m; j++) {
                // Defensive check: Negative values are physically impossible for physical resources
                if (maxMatrix[i][j] < 0 || allocationMatrix[i][j] < 0 || available[j] < 0) {
                    return new BankerResponseDto(false, new ArrayList<>(), 
                            "Validation Error: Negative values detected in resource allocation matrices.");
                }

                needMatrix[i][j] = maxMatrix[i][j] - allocationMatrix[i][j];

                // In Banker's algorithm, current allocation can NEVER exceed declared maximum claim
                if (needMatrix[i][j] < 0) {
                    return new BankerResponseDto(false, new ArrayList<>(), 
                            "Validation Error: Allocation exceeds Max claim for process " + processNames.get(i) 
                            + " on resource " + resourceNames.get(j) + ".");
                }
            }
        }

        // ---------------------------------------------------------------------
        // STEP 3: Banker's Safety Algorithm Initialization
        // Work = Available (Vector of size m)
        // Finish[i] = false for all i in [0..n-1]
        // ---------------------------------------------------------------------
        int[] work = Arrays.copyOf(available, m);
        boolean[] finish = new boolean[n];
        List<String> safeSequence = new ArrayList<>();
        StringBuilder auditLog = new StringBuilder();

        auditLog.append("Initial Available Vector: ").append(formatVector(resourceNames, work)).append(". ");

        // ---------------------------------------------------------------------
        // STEP 4: Safety Search Loop
        // Find an index i such that:
        //    a) Finish[i] == false
        //    b) Need[i][j] <= Work[j] for all j
        // If found:
        //    Work = Work + Allocation[i]
        //    Finish[i] = true
        // ---------------------------------------------------------------------
        int count = 0;
        while (count < n) {
            boolean foundExecutable = false;

            for (int i = 0; i < n; i++) {
                if (!finish[i]) {
                    // Check if current Available (Work) can satisfy process i's remaining need
                    boolean canBeAllocated = true;
                    for (int j = 0; j < m; j++) {
                        if (needMatrix[i][j] > work[j]) {
                            canBeAllocated = false;
                            break; // Cannot allocate: insufficient free instances of resource j
                        }
                    }

                    if (canBeAllocated) {
                        // 🎓 VIVA TIP: Explain why we ADD Allocation to Work:
                        // "When process i finishes, it returns all resources it was holding back
                        // to the available pool. Therefore: Work = Work + Allocation[i]."
                        for (int j = 0; j < m; j++) {
                            work[j] += allocationMatrix[i][j];
                        }
                        finish[i] = true;
                        safeSequence.add(processNames.get(i));
                        foundExecutable = true;
                        count++;

                        // Record audit log step for examination review
                        auditLog.append("Step ").append(count).append(": ")
                                .append(processNames.get(i))
                                .append(" satisfied (Need: ").append(formatVector(resourceNames, needMatrix[i]))
                                .append(" <= Work). Completed & released resources. New Work: ")
                                .append(formatVector(resourceNames, work)).append(". ");
                        break; // Restart scan from beginning to maintain deterministic safe sequence
                    }
                }
            }

            // If in a full pass no process could be satisfied, a deadlock condition exists
            if (!foundExecutable) {
                break;
            }
        }

        // ---------------------------------------------------------------------
        // STEP 5: Evaluate Safety Verdict
        // If count == n: All processes finished -> SAFE STATE!
        // Else: Some processes cannot finish -> UNSAFE STATE (Deadlock Risk)!
        // ---------------------------------------------------------------------
        if (count == n) {
            String explanation = "System is in a SAFE STATE. Executing in sequence " 
                    + String.join(" -> ", safeSequence) 
                    + " guarantees zero risk of resource starvation or clinical deadlock. "
                    + auditLog.toString();
            return new BankerResponseDto(true, safeSequence, explanation);
        } else {
            // Identify which processes caused the deadlock
            List<String> blockedProcesses = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                if (!finish[i]) {
                    blockedProcesses.add(processNames.get(i) + " (Need: " + formatVector(resourceNames, needMatrix[i]) + ")");
                }
            }

            String explanation = "UNSAFE STATE DETECTED! Potential deadlock risk. "
                    + "Available resources " + formatVector(resourceNames, work) 
                    + " cannot satisfy the remaining claims of: " + String.join(", ", blockedProcesses) 
                    + ". Immediate intervention required to prevent clinical resource stall.";
            return new BankerResponseDto(false, safeSequence, explanation);
        }
    }

    /**
     * Implements Dijkstra's Resource-Request Algorithm.
     * Evaluates whether granting an immediate resource request leaves the hospital in a safe state.
     * 
     * 🎓 VIVA EXPLANATION:
     * "When a patient requests additional resources, the OS doesn't just grant it blindly.
     * It checks:
     * 1. Does Request <= Need? (Did the process ask for more than its declared maximum?)
     * 2. Does Request <= Available? (Do we have enough free resources right now?)
     * 3. Pretend to allocate: Available -= Request, Allocation += Request.
     * 4. Run checkSafety(). If state remains safe, GRANT request! If unsafe, ROLLBACK!"
     */
    public BankerResponseDto requestResources(BankerRequestDto currentState, int processIndex, int[] requestVector) {
        if (currentState == null || currentState.getAvailable() == null) {
            currentState = getDefaultHospitalRequest();
        }

        int m = currentState.getAvailable().length;
        if (processIndex < 0 || processIndex >= currentState.getMaxMatrix().length) {
            return new BankerResponseDto(false, new ArrayList<>(), "Error: Invalid process index: " + processIndex);
        }

        int[] need = new int[m];
        for (int j = 0; j < m; j++) {
            need[j] = currentState.getMaxMatrix()[processIndex][j] - currentState.getAllocationMatrix()[processIndex][j];
            
            // Check 1: Has process exceeded its declared maximum claim?
            if (requestVector[j] > need[j]) {
                return new BankerResponseDto(false, new ArrayList<>(), 
                        "Request Denied: Process exceeded its declared maximum claim for resource index " + j);
            }
            
            // Check 2: Are enough resources currently available?
            if (requestVector[j] > currentState.getAvailable()[j]) {
                return new BankerResponseDto(false, new ArrayList<>(), 
                        "Request Deferred: Insufficient resources available. Process must wait.");
            }
        }

        // Clone state for tentative allocation simulation (Sandboxed modification)
        int[] tempAvailable = Arrays.copyOf(currentState.getAvailable(), m);
        int[][] tempAlloc = cloneMatrix(currentState.getAllocationMatrix());
        int[][] tempMax = cloneMatrix(currentState.getMaxMatrix());

        // Step 3: Tentatively allocate requested resources
        for (int j = 0; j < m; j++) {
            tempAvailable[j] -= requestVector[j];
            tempAlloc[processIndex][j] += requestVector[j];
        }

        BankerRequestDto tentativeRequest = new BankerRequestDto(
                currentState.getProcessNames(),
                currentState.getResourceNames(),
                tempAvailable,
                tempMax,
                tempAlloc
        );

        // Step 4: Run safety algorithm on tentative state
        BankerResponseDto result = checkSafety(tentativeRequest);
        if (result.isSafe()) {
            result.setAuditExplanation("REQUEST GRANTED: Tentative allocation results in a verified Safe State. " 
                    + result.getAuditExplanation());
        } else {
            result.setAuditExplanation("REQUEST REJECTED: Granting request would transition hospital into an UNSAFE STATE (Deadlock Risk). " 
                    + result.getAuditExplanation());
        }
        return result;
    }

    /**
     * Canonical MedFlow Hospital Scenario matching database seed (oracle_seed.sql):
     * - Resources: ICU_BED (10 total), VENTILATOR (6 total), OPERATION_THEATER (4 total)
     * - Available Vector: [4 ICU Beds, 2 Ventilators, 1 OR]
     * - Processes & Claims:
     *   T1: John Doe (Angioplasty)      - Alloc: [2, 0, 1], Max: [3, 5, 2] -> Need: [1, 5, 1]
     *   T2: Anita Roy (Ventilation)     - Alloc: [1, 2, 0], Max: [2, 3, 0] -> Need: [1, 1, 0]
     *   T3: Robert Smith (Monitoring)   - Alloc: [1, 1, 0], Max: [2, 1, 1] -> Need: [1, 0, 1]
     *   T4: Pooja Verma (Trauma)        - Alloc: [2, 1, 2], Max: [4, 3, 3] -> Need: [2, 2, 1]
     * 
     * 🎓 Resulting Safe Sequence: T2 -> T3 -> T1 -> T4
     */
    public BankerRequestDto getDefaultHospitalRequest() {
        List<String> processNames = Arrays.asList(
                "T1 (John Doe - Angioplasty)",
                "T2 (Anita Roy - Ventilation)",
                "T3 (Robert Smith - Cardiac)",
                "T4 (Pooja Verma - Trauma)"
        );

        List<String> resourceNames = Arrays.asList("ICU_BED", "VENTILATOR", "OPERATION_THEATER");

        // Available Vector: [ICU_BED=4, VENTILATOR=2, OPERATION_THEATER=1]
        int[] available = new int[]{4, 2, 1};

        // Maximum Matrix: Declared worst-case resource requirements
        int[][] maxMatrix = new int[][]{
                {3, 5, 2}, // T1: Needs 5 ventilators; requires both T2 & T3 to finish first
                {2, 3, 0}, // T2: Needs only 1 ventilator & 1 ICU bed; can execute first!
                {2, 1, 1}, // T3: Needs 1 ICU bed & 1 OR; can execute second
                {4, 3, 3}  // T4: Large trauma claim; executes last after resources recovered
        };

        // Allocation Matrix: Currently allocated hospital assets
        int[][] allocationMatrix = new int[][]{
                {2, 0, 1}, // T1 currently holds 2 ICU beds, 0 ventilators, 1 OR
                {1, 2, 0}, // T2 currently holds 1 ICU bed, 2 ventilators, 0 OR
                {1, 1, 0}, // T3 currently holds 1 ICU bed, 1 ventilator, 0 OR
                {2, 1, 2}  // T4 currently holds 2 ICU beds, 1 ventilator, 2 OR
        };

        return new BankerRequestDto(processNames, resourceNames, available, maxMatrix, allocationMatrix);
    }

    /**
     * Canonical Unsafe Scenario for viva demonstrations and deadlock testing.
     * High resource contention where 0 resources are free and circular-wait blocks progress.
     */
    public BankerRequestDto getUnsafeHospitalRequest() {
        List<String> processNames = Arrays.asList(
                "T1 (John Doe - Angioplasty)",
                "T2 (Anita Roy - Ventilation)",
                "T3 (Robert Smith - Cardiac)",
                "T4 (Pooja Verma - Trauma)"
        );

        List<String> resourceNames = Arrays.asList("ICU_BED", "VENTILATOR", "OPERATION_THEATER");

        // Available: 0 free resources -> Instant deadlock!
        int[] available = new int[]{0, 0, 0};

        int[][] maxMatrix = new int[][]{
                {3, 2, 2},
                {2, 3, 0},
                {2, 1, 1},
                {4, 3, 3}
        };

        int[][] allocationMatrix = new int[][]{
                {2, 1, 1},
                {1, 2, 0},
                {1, 1, 1},
                {2, 2, 2}
        };

        return new BankerRequestDto(processNames, resourceNames, available, maxMatrix, allocationMatrix);
    }

    private String formatVector(List<String> names, int[] vector) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < vector.length; i++) {
            if (i < names.size()) {
                sb.append(names.get(i)).append("=");
            }
            sb.append(vector[i]);
            if (i < vector.length - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    private int[][] cloneMatrix(int[][] matrix) {
        int[][] clone = new int[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            clone[i] = Arrays.copyOf(matrix[i], matrix[i].length);
        }
        return clone;
    }
}
