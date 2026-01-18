# Real-time Traffic Simulation System using SUMO and Java

This project integrates the SUMO Traffic Simulator with a Java application (Eclipse IDE) using the TraCI protocol via the TraaS Java library.
The goal is to allow real-time simulation control from a Java GUI, visualize vehicle flows, and support basic traffic management operations.

---

## Project Overview

This project is a simple **real-time traffic simulation** written in Java.  
It simulates:

- Vehicles moving on a predefined road network
- Traffic lights switching between red/yellow/green states
- Roads and map layout
- A GUI built using JavaFx (similar to SUMO-GUI)  
- Real-time behavior controlled by a JavaFx `Timer`

## Project Structure

```bash
traffic_simulation
├───.vscode
├───lib
├───src
│   ├───log
│   │   └───java
│   │       └───real_time_traffic_simulation_with_java
│   │           ├───csv
│   │           └───pdf
│   ├───main
│   │   └───java
│   │       └───real_time_traffic_simulation_with_java
│   │           ├───alias
│   │           ├───cores
│   │           ├───gui
│   │           │   ├───controlPanelElement
│   │           │   ├───dashboardSection
│   │           │   ├───mapLayer
│   │           │   └───resources
│   │           ├───lib
│   │           ├───SumoConfig
│   │           ├───tools
│   │           │   └───PDFmethod
│   │           └───wrapper
│   └───test
│       └───java
│           └───real_time_traffic_simulation_with_java
└───target
    ├───classes
    │   └───real_time_traffic_simulation_with_java
    │       ├───alias
    │       ├───cores
    │       ├───gui
    │       │   ├───controlPanelElement
    │       │   ├───dashboardSection
    │       │   ├───mapLayer
    │       │   └───resources
    │       ├───tools
    │       │   └───PDFmethod
    │       └───wrapper
    ├───generated-sources
    │   └───annotations
    ├───maven-status
    │   └───maven-compiler-plugin
    │       └───compile
    │           └───default-compile
    └───test-classes
        └───real_time_traffic_simulation_with_java
```

## Installation

- JavaSE-17 or higher (recommended Java 25)
- SUMO 1.25.0
- TraaS library
- Eclipse IDE
- JavaFX

---

## Features

- Object-oriented design with multiple classes  
- Traffic lights changes
- Road network rendering (horizontal + vertical)  
- Smooth animations  
- Modular structure split across multiple classes  
- Easy to extend with new features
- Real-time animated environment
- GUI similar in concept to SUMO-GUI
- Configurable update frequency
- Clean, modular Java project structure

---

## Team roles

|Name                              | Role                 | Description |
|----------------------------------|----------------------|-------------|
|Huynh Bao Tran                    | Developer                    | Writes Java source code, cover back-end including design wrappers, GUI render maps, and user controls.|
|Nguyen Thuy Anh                   | Developer                          | Writes Java source code, cover front end, thread, and mannage Sumo Connection |
|Nguyen Ho Tuyet Phuong            | Developer, Document Writer                          | Write Java source code related to traffic light, create diagrams, Javadoc and write report. |
|Dieu Ngoc Thien An                | Tester              |Test the program |
|Pham Tran Minh Anh                | Tester     | Test the program|
---

## Technology Stack Summary

### Programming Language and Frameworks

- Java 25/ Java SE
- JavaFX for GUI development

### Tools and IDE

- SUMO 1.25.0
- TraaS library
- Eclipse IDE version 2025-09
- Git + Github for version control
- Visual Studio Code version 1.106

---

## Development Time Plan

### **Milestones Overview**

|Dates          | Milestone / Stage                  | Key Deliverables                            |
|----------------|------------------------------------|----------------------------------------------|
|Nov 1 – 27   | Milestone 1: Design & Prototype | Overview, diagrams, wrapper, mockups         |
|Nov 28 – Dec 14 | Milestone 2: Functional Prototype | Live demo, core features                     |
|Dec 15 – Jan 18 | Milestone 3: Finalize & Submission | Full GUI, export features, documentation     |

### **Timeline & Team roles Milestone 2**

|Dates            |Tasks                        |Member                |
|----------------|------------------------------------|----------------------------------------------|
|03/12/2025 - 07/12/2025 | Working Application: Live SUMO connection     | Nguyen Thuy Anh        |
|03/12/2025 - 07/12/2025 | Working Application: Vehicle injection        | Huynh Bao Tran    |
|03/12/2025 - 07/12/2025 | Working Application: Traffic light control    | Nguyen Ho Tuyet Phuong         |
|03/12/2025 - 07/12/2025 | Working Application: Map visualization        | Huynh Bao Tran        |
|03/12/2025 - 07/12/2025 | Design: Design the layout, appearance, and user controls.       | Nguyen Thuy Anh                |
|03/12/2025 - 07/12/2025 | Code Documentation & Fix project report|Nguyen Ho Tuyet Phuong, Nguyen Thuy Anh|
|07/12/2025 - 10/12/2025 | User Guide Draft & Test core features         | Nguyen Ho Tuyet Phuong, Huynh Bao Tran  |
|07/12/2025 - 11/12/2025 | Test Scenario                                 | Pham Tran Minh Anh, Dieu Ngoc Thien An |
|08/12/2025 - 12/12/2025 | Progress Summary                              | Nguyen Ho Tuyet Phuong        |

### **Timeline & Team roles Milestone 3**

|Dates            |Tasks                        |Member                |
|----------------|------------------------------------|----------------------------------------------|
|20/12-8/1 | GUI: Full dashboard with statistics     | Huynh Bao Tran, Pham Tran Minh Anh       |
|20/12-8/1 | Working Application:Vehicle filtering        | Huynh Bao Tran    |
|20/12-15/1 | Working Application: Traffic light duration adjustment.    | Nguyen Ho Tuyet Phuong         |
|8/1-15/1 | Working Application: Record CSV       | Huynh Bao Tran, Dieu Ngoc Thien An        |
|8/1-15/1 | Working Application: Exportable PDF      | Huynh Bao Tran                |
|16/1-17/1|Working Application: Multi-threading.|Nguyen Thuy Anh|
|10/1- 15/1 | Design: Design the layout, appearance, and user controls.         | Nguyen Thuy Anh  |
|16/1-18/1 |Code Documentation & Fix project report                                 | Nguyen Ho Tuyet Phuong |
|16/1-18/1 | User Guide Draft & Test core features                              | Nguyen Ho Tuyet Phuong        |
|8/1-20/1|Clean Git Repository|Huynh Bao Tran, Nguyen Thuy Anh|

### **Detailed Checklists**

#### Milestone 1 — Design & Prototype

- [x] Project overview (1 - 2 pages)
- [x] Architecture diagram
- [x] Class Design for TraaS wrapper (Vehicle, TrafficLight, etc.)
- [x] GUI Mockups (map view, control panels, dashboard)
- [x] SUMO Connection Demo (list traffic lights, step simulation)
- [x] Technology Stack Summary
- [x] Git Repository Setup (README, initial commit)
- [x] Team roles & time plan documentation

#### Milestone 2 — Functional Prototype

- [x] Running app with live SUMO ↔ Java communication
- [x] Vehicle injection + traffic light control
- [x] Map rendering inside GUI
- [x] Draft Javadoc & user guide
- [x] Test scenario (vehicles, signals, route)
- [x] Mid-project progress summary

#### Milestone 3 — Finalization & Submission

- [x] Final polished GUI with full interactions
- [x] Grouping/filtering logic for vehicles
- [ ] Traffic rule logic + timing optimization
- [x] Traffic duration adjustment feature
- [x] Export features (CSV & PDF)
- [x] All documentation completed
- [x] Final user guide + installation section
- [x] Clean repository

## Achitechture Diagram and Class Design

![Architecture Diagram](src/main/java/real_time_traffic_simulation_with_java/gui/resources/AchitechDia.jpg)

![Class Design Diagram](src/main/java/real_time_traffic_simulation_with_java/gui/resources/ClassDia.jpg)

## UI

![UI](src/main/java/real_time_traffic_simulation_with_java/gui/resources/UI.png)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
