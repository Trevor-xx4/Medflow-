package com.medflow.simulation.sync;

import com.medflow.dto.ProcessTaskDto;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * =============================================================================
 * 🔒 HospitalResourceSyncService
 * =============================================================================
 * 
 * Demonstrates Classical Operating Systems Process Synchronization Primitives:
 * 
 * 1. COUNTING SEMAPHORES (wait() / signal() - P / V operations):
 *    - Used for multi-instance scarce resources (ICU Beds: 10, Ventilators: 6).
 *    - When a patient claims a bed -> acquire() [wait() / P operation].
 *    - When discharged -> release() [signal() / V operation].
 * 
 * 2. MUTEX / BINARY SEMAPHORE (Mutual Exclusion for Critical Sections):
 *    - Used for single-access resources like Operation Theater 1 (ReentrantLock).
 *    - Guarantees only ONE surgeon/patient can enter the Critical Section at a time,
 *      preventing Race Conditions.
 * 
 * 3. PRODUCER-CONSUMER PROBLEM (Bounded Buffer):
 *    - Triage Nurse (Producer) adds arriving emergency patients to a bounded queue.
 *    - Duty Doctors (Consumers) poll patients from the queue to start treatments.
 *    - Handled via thread-safe BlockingQueue (wait when full, wait when empty).
 * =============================================================================
 */
@Service
public class HospitalResourceSyncService {

    // -------------------------------------------------------------------------
    // 1. COUNTING SEMAPHORES (Multi-Instance Scarce Resources)
    // -------------------------------------------------------------------------
    // Initialized to the exact hospital capacity from database/oracle_seed.sql
    private final Semaphore icuBedSemaphore = new Semaphore(10, true);     // 10 ICU Beds (FIFO fair)
    private final Semaphore ventilatorSemaphore = new Semaphore(6, true);   // 6 Ventilators (FIFO fair)

    // -------------------------------------------------------------------------
    // 2. MUTEX LOCK (Mutual Exclusion for Operation Theater)
    // -------------------------------------------------------------------------
    // ReentrantLock acts as a Binary Semaphore / Mutex for single-access surgical wing
    private final ReentrantLock operationTheaterLock = new ReentrantLock();

    // -------------------------------------------------------------------------
    // 3. PRODUCER-CONSUMER BUFFER (Emergency Triage Queue)
    // -------------------------------------------------------------------------
    // Bounded buffer holding max 20 patients waiting for doctor consultation
    private final BlockingQueue<ProcessTaskDto> triageQueue = new ArrayBlockingQueue<>(20);

    /**
     * Allocates a Ventilator using Counting Semaphore wait() / acquire().
     * If all 6 ventilators are occupied, this method blocks/waits until one is freed.
     * 
     * 🎓 VIVA EXPLANATION:
     * "This is Dijkstra's P operation (wait). It decrements the semaphore counter.
     * If the counter is 0, the thread is suspended until another thread calls release()."
     */
    public boolean allocateVentilator(String patientName) throws InterruptedException {
        // acquire() = wait() operation in OS
        boolean acquired = ventilatorSemaphore.tryAcquire(2, TimeUnit.SECONDS);
        if (acquired) {
            System.out.println("[SYNC - SEMAPHORE] Ventilator allocated to " + patientName 
                    + ". Free Ventilators: " + ventilatorSemaphore.availablePermits());
        } else {
            System.out.println("[SYNC - BLOCKED] " + patientName + " had to wait. All ventilators in use.");
        }
        return acquired;
    }

    /**
     * Releases a Ventilator using Counting Semaphore signal() / release().
     * 
     * 🎓 VIVA EXPLANATION:
     * "This is Dijkstra's V operation (signal). It increments the semaphore counter
     * and wakes up any waiting patient thread."
     */
    public void releaseVentilator(String patientName) {
        // release() = signal() operation in OS
        ventilatorSemaphore.release();
        System.out.println("[SYNC - SEMAPHORE] Ventilator released by " + patientName 
                + ". Free Ventilators: " + ventilatorSemaphore.availablePermits());
    }

    /**
     * Mutual Exclusion (Mutex) for Operation Theater surgery.
     * Critical Section: Only one surgery can occur in the theater at a time.
     * 
     * 🎓 VIVA EXPLANATION:
     * "The Operation Theater is a Critical Section. To prevent a Race Condition
     * where two surgical teams enter simultaneously, we use a Mutex lock().
     * Any competing thread must wait until unlock() is called."
     */
    public boolean enterOperationTheater(String surgeonName, String patientName) {
        if (operationTheaterLock.tryLock()) {
            try {
                // CRITICAL SECTION START
                System.out.println("[SYNC - MUTEX ACQUIRED] Dr. " + surgeonName 
                        + " entered OR with patient " + patientName);
                return true;
                // CRITICAL SECTION END
            } finally {
                operationTheaterLock.unlock(); // Ensure lock is ALWAYS released
                System.out.println("[SYNC - MUTEX RELEASED] Surgery completed in OR.");
            }
        } else {
            System.out.println("[SYNC - MUTEX BUSY] OR is occupied! Access denied for Dr. " + surgeonName);
            return false;
        }
    }

    /**
     * Producer: Triage Nurse places an emergency patient into the bounded queue.
     * 
     * 🎓 VIVA EXPLANATION:
     * "Demonstrates the Producer-Consumer problem. If the queue is full (buffer full),
     * the producer blocks until a doctor consumes a patient."
     */
    public void produceTriagePatient(ProcessTaskDto patient) throws InterruptedException {
        triageQueue.put(patient); // Blocks if buffer is full
        System.out.println("[PRODUCER] Admitted: " + patient.getPatientName() 
                + " into triage queue. Queue Size: " + triageQueue.size());
    }

    /**
     * Consumer: Doctor pulls the next waiting patient from the queue.
     * 
     * 🎓 VIVA EXPLANATION:
     * "If the queue is empty (buffer empty), the doctor thread blocks until
     * a triage nurse admits a new patient."
     */
    public ProcessTaskDto consumeTriagePatient() throws InterruptedException {
        ProcessTaskDto patient = triageQueue.take(); // Blocks if buffer is empty
        System.out.println("[CONSUMER] Dr. called: " + patient.getPatientName() 
                + " for consultation. Remaining: " + triageQueue.size());
        return patient;
    }

    // Getters for status monitoring in viva
    public int getAvailableVentilators() {
        return ventilatorSemaphore.availablePermits();
    }

    public int getAvailableIcuBeds() {
        return icuBedSemaphore.availablePermits();
    }

    public boolean isOperationTheaterFree() {
        return !operationTheaterLock.isLocked();
    }
}
