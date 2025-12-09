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
- A GUI built using Java Swing (similar to SUMO-GUI)  
- Real-time behavior controlled by a Swing `Timer`

## Project Structure

```bash
Real-Time-Traffic-Simulation-with-Java-WiSe-25-26
├───Demo_video
├───SumoConfig
└───SumoTrafficSim
    ├───.settings
    ├───bin
    │   ├───core
    │   ├───gui
    │   ├───test
    │   └───wrapper
    ├───img
    ├───lib
    └───src
        ├───core
        ├───gui
        ├───test
        └───wrapper
```

## Installation

- JavaSE-21 or higher
- SUMO 1.25.0
- TraaS library
- Eclipse IDE

---

## Features

- Object-oriented design with multiple classes  
- Traffic lights change using timers
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
|Pham Tran Minh Anh                | Developer, GUI Designer            | Writes Java source code, including SUMO connection, GUI and logic. Design the layout, appearance, and user controls. |
|Nguyen Thuy Anh                   | Developer                          | Writes Java source code, including SUMO connection, GUI and step simulation, design wrappers. |
|Dieu Ngoc Thien An                | Document Writer                    | Creates diagram. |
|Huynh Bao Tran                    | Document Writer                    | Writes documentation. |
|Nguyen Ho Tuyet Phuong            | Developer                          | Creates logic for vehicles, traffic lights, and simulation rules. |

---

## Technology Stack Summary

### Programming Language and Frameworks

- Java 25/ Java SE
- Java Swing for GUI development

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

### **Timeline & Team roles Milestone 2
|Dates            |Tasks                        |Member                |
|----------------|------------------------------------|----------------------------------------------|
|03/12/2025 - 07/12/2025 | Working Application: Live SUMO connection     | Nguyen Thuy Anh        |
|03/12/2025 - 07/12/2025 | Working Application: Vehicle injection        | Nguyen Ho Tuyet Phuong    |
|03/12/2025 - 07/12/2025 | Working Application: Traffic light control    | Pham Tran Minh Anh         |
|03/12/2025 - 07/12/2025 | Working Application: Map visualization        | Dieu Ngoc Thien An        |
|03/12/2025 - 07/12/2025 | Code Documentation & Fix project report       | Huynh Bao Tran                |
|07/12/2025 - 10/12/2025 | User Guide Draft & Test core features         | Pham Tran Minh Anh, Nguyen Thuy Anh |
|07/12/2025 - 11/12/2025 | Test Scenario                                 | Huynh Bao Tran, Nguyen Ho Tuyet Phuong |
|08/12/2025 - 12/12/2025 | Progress Summary                              | Dieu Ngoc Thien An        |

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

- [ ] Running app with live SUMO ↔ Java communication
- [ ] Vehicle injection + traffic light control
- [ ] Map rendering inside GUI
- [ ] Draft Javadoc & user guide
- [ ] Test scenario (vehicles, signals, route)
- [ ] Mid-project progress summary

#### Milestone 3 — Finalization & Submission

- [ ] Final polished GUI with full interactions
- [ ] Grouping/filtering logic for vehicles
- [ ] Traffic rule logic + timing optimization
- [ ] Export features (CSV & PDF)
- [ ] All documentation completed
- [ ] Final user guide + installation section
- [ ] Presentation preparation
- [ ] Team reflection & clean repository

## Video Demo

- For demostation the connection see the [SumoConnectionDemo.mkv](Demo_video/SumoConnectionDemo.mkv).

- For GUI demo see the [Demo_GUI.mkv](Demo_video/Demo_GUI.mkv).

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
