# MedFlow: Smart Hospital Resource & Process Management System

**MedFlow** is an academic Project-Based Learning (PBL) application that integrates **Hospital Management** workflows with core concepts from **Operating Systems (OS)** and **Database Management Systems (DBMS)**.

---

## 🏥 Core Concepts Demonstrated

1. **Hospital Management System (HMS):**
   - Management of Patients, Doctors, Appointments, and Treatments.
   - Resource tracking for critical hospital assets (ICU Beds, Ventilators, Operation Theaters).

2. **Operating Systems (OS):**
   - **Process Scheduling:** Treatments are treated as processes and scheduled using:
     - **FCFS (First-Come, First-Served):** Routine outpatient department queues.
     - **Priority Scheduling:** Emergency triage where life-critical patients preempt routine care.
     - **Round Robin:** Fair time-sliced allocation for shared medical staff rounds.
   - **Deadlock Management:**
     - **Banker's Algorithm:** Evaluates whether allocating a scarce resource (e.g., ventilator) leaves the hospital in a **Safe State** with an executable **Safe Sequence**.

3. **Database Management Systems (DBMS):**
   - Relational database schema hosted on **Oracle Database**.
   - Normalized 3NF structure, Primary & Foreign Key integrity, Check constraints, and Identity columns.

---

## 📂 Project Structure (Phase 1 — OS Simulation Complete)

```text
PBL/
├── pom.xml                                  # Maven build dependencies (Spring Boot, Oracle ojdbc11)
├── README.md                                # Project summary & Viva quick-reference
│
├── database/                                # Oracle SQL Scripts
│   ├── oracle_schema.sql                    # DDL: Tables, PK/FK, Constraints, Sequences
│   └── oracle_seed.sql                      # DML: Mock hospital seed records
│
└── src/
    └── main/
        ├── java/com/medflow/
        │   ├── MedFlowApplication.java      # Spring Boot main runner
        │   ├── controller/                  # REST Controllers with mock endpoints
        │   │   ├── HospitalController.java
        │   │   └── SimulationController.java
        │   ├── model/                       # Domain entities (Patient, Doctor, Resource, etc.)
        │   ├── dto/                         # DTO contracts (ProcessTaskDto, BankerDto, etc.)
        │   └── simulation/                  # OS Simulation Engine (100% Complete)
        │       ├── scheduler/SchedulerService.java
        │       └── deadlock/BankersAlgorithmService.java
        │
        └── resources/
            ├── application.properties.example # Oracle connection properties template
            └── static/                        # Vanilla HTML/CSS/JS wireframe dashboard
                ├── index.html
                ├── styles.css
                └── app.js
```

---

## 🎓 2-Day Viva & Exam Quick-Reference

If asked by your professor or evaluator during the viva:

1. **"Why use CPU scheduling for hospital patients?"**
   - In a hospital, doctors and beds are finite resources (like CPU cores). Incoming patient treatments are like processes. By applying Priority Scheduling, emergency triage patients get treated first rather than waiting in an unfair first-come queue.
2. **"How does Banker's Algorithm prevent hospital deadlock?"**
   - If Patient A holds an ICU bed and requests a ventilator, while Patient B holds a ventilator and requests an ICU bed, neither can complete — creating a **deadlock**. Banker's Algorithm checks maximum claims before allocating resources to ensure the hospital always remains in a **Safe State**.
3. **"Why did you use Oracle Database?"**
   - Oracle provides enterprise-level relational constraints, identity sequences, and ACID transaction guarantees needed for clinical and auditing integrity.
