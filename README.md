
# Iteration 1 - SYSC3303 A4 - Elevator Real-time System Simulator
## Overview

**Group Number:** 6

**Team and Contributions:**
1. Mahnoor Fatima (101192353),
2. Khola Haseeb (101192363),
3. Areej Mahmoud (101218260),
4. Owen Petersen (101233850),
5. Nikita Sara Vijay (101195009),

**Project Iteration 1**

This Java project simulates an elevator system with multiple subsystems - Elevator, Floor, and Scheduler. The Elevator moves between floors, the Floor subsystem reads input data from a file, and the Scheduler manages the communication between the Elevator and Floor.
Elevator Class

## Installation

Import project into your IDE.

or

Git Clone the Project

```bash
git clone https://github.com/HamMan39/SYSC3303_ElevatorSimulator.git
```
## Project Structure

The project is organized into the following main classes:

`Elevator`: Represents the Elevator subsystem.

`Floor`: Represents the Floor subsystem which reads input data, and communicates with the Scheduler.

`Main`: Main class to initialize and start the Elevator, Floor, and Scheduler threads.

`Message`: Represents a message passed between the Scheduler and subsystems.

`MessageBox`: A synchronized message box that facilitates communication between subsystems.

`Scheduler`: Manages communication between the Elevator and Floor subsystems.

## Thread Interaction
- The `Floor` thread reads input data from a file and sends messages to the Scheduler.
- The `Elevator` thread continuously receives messages from the Scheduler and sends responses.
- The `Scheduler` manages the communication flow between the Elevator and Floor subsystems.

## Dependencies

The project uses standard Java libraries. The Test classes run using the JUnit5.8.1 Library

## How to Run
To run the project, follow these steps:

1. Open the project IntelliJ.
2. Locate the Main class.
3. Run the main method in the Main class.