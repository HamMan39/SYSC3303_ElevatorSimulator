
# Iteration 1 - SYSC3303 A4 - Elevator Real-time System Simulator
## Overview

**Group Number:** 6

**Team Contributions:**
1. Mahnoor Fatima (101192353), Implemented Scheduler and MessageBox 
2. Khola Haseeb (101192363), Implemented Elevator, MessageBoxTest, ElevatorTest and README
3. Areej Mahmoud (101218260), Implemented Floor, Message, SchedulerTest and FloorTest
4. Owen Petersen (101233850), Sequence Diagram
5. Nikita Sara Vijay (101195009), UML Class Diagram

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

## Unit Tests
- FloorTest: Tests the ability of the Floor subsystem to read input file and
pass data back and forth.
- ElevatorTest: Tests the ability of the Elevator subsystem send and receive messages.
- MessageBoxTest: Test the put() and get() functions of the box and guarantee mutual exclusion.
- SchedulerTest: Test the ability of Scheduler to pass data back and forth between Floor and Elevator.
## Dependencies

The project uses standard Java libraries. The test classes run using the JUnit 5.8.1 Library

## How to Run
To run the project, follow these steps:

1. Open the project IntelliJ.
2. Locate the Main class.
3. Run the main method in the Main class.
4. To execute JUnit tests, right-click on 'src' folder in IntelliJ and select 'Run All Tests'