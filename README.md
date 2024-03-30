
# Iteration 3 - SYSC3303 A4 - Elevator Real-time System Simulator
## Overview
**Group Number:** 6

**Team Contributions:**
Iteration 0:
1. Mahnoor Fatima, timing measurements
2. Khola Haseeb, timing measurements
3. Areej Mahmoud, timing measurements
4. Owen Petersen, report writing + load/unload calculations + excel input
5. Nikita Sara Vijay, acceleration/deceleration calculations + timing

Iteration 1:
1. Mahnoor Fatima, Implemented Scheduler and MessageBox
2. Khola Haseeb, Implemented Elevator, MessageBoxTest, ElevatorTest and README
3. Areej Mahmoud, Implemented Floor, Message, SchedulerTest and FloorTest
4. Owen Petersen, Sequence Diagram
5. Nikita Sara Vijay, UML Class Diagram

Iteration 2:
1. Mahnoor Fatima, Updated Scheduler algorithm
2. Khola Haseeb, Updated scheduler algorithm
3. Areej Mahmoud, Updated refactored Elevator
4. Owen Petersen, State machine diagrams + sequence diagram
5. Nikita Sara Vijay, Implemented Elevator

Iteration 3:
1. Mahnoor Fatima (101192353), Implemented RPC over UDP in Elevator and CommunicationRPC class
2. Khola Haseeb (101192363), Implemented ElevatorSubsystem and Elevator
3. Areej Mahmoud (101218260), Implemented RPC over UDP in Scheduler, Floor and CommunicationRPC class
4. Owen Petersen (101233850),  Implemented Scheduler algorithm and ElevatorData
5. Nikita Sara Vijay (101195009), Implemented Elevator PendingFloors Algorithm and ElevatorSubsystem

Iteration 4:
1. Mahnoor Fatima (101192353), Updated FloorTest and MessageTest classes
2. Khola Haseeb (101192363), Updated ElevatorTest class 
3. Areej Mahmoud (101218260), Injecting failures into Elevator thread and updatedMessageBoxTest
4. Owen Petersen (101233850),  Implemented re-scheduling failed elevator requests and updated ElevatorData
5. Nikita Sara Vijay (101195009), Updated Elevator PendingFloors Algorithm for efficiency

**Project Iteration 4**

This Java project simulates an elevator system with 3 main subsystems - Elevator, Floor, and Scheduler. The Elevator moves between floors, the Floor subsystem reads input data from a file, and the Scheduler manages the communication between the Elevator and Floor.
Elevator  Class. Iteration 3 implements Remote Procedure Calls over UDP to communicate between the main Thread. The Elevator Subsystem handles 4 separate elevators in a 22 floor building, receiving scheduled requests from 
the Scheduler and Distributing them to the respective elevators. The input to the system is through the Floor class which reads, passenger Floor requests from an input file
and sends them to the Scheduler Thread to schedule. The system also simulates failures
by injecting Door_Stuck or Timeout failures through the input file and transient and hard faults accordingly.

## Installation

Import project into your IDE.

or

Git Clone the Project

```bash
git clone https://github.com/HamMan39/SYSC3303_ElevatorSimulator.git
```
## Project Structure

The project is organized into the following main classes:

`Elevator`: Represents the Elevator, which travels through floors, picking up passengers and dropping them off at their arrival floors; 

`ElevatorSubsystem`: Represents the Elevator Subsystem, which is used to communicate between Scheduler and all 4 Elevator Threads

`ElevatorData`: A data structure to store the real-time elevator location and direction updates to be sent to Scheduler

`Floor`: Represents the Floor subsystem which reads input data, and communicates with the Scheduler.

`Message`: Represents a message passed between the Scheduler and subsystems.

`MessageBox`: A synchronized message box that facilitates communication between subsystems.

`Scheduler`: Represents Scheduler Subsystem. Manages communication between the Elevator and Floor subsystems, and schedules floor requests to elevators based on its algorithm.

`CommunicationRPC`: Represents a superclass for the 3 main subsystems (Elevator, Scheduler, Floor) 
which implement rpc_send(), sendAndReceive(), and receiveAndSend() methods to facilitate
sending remote procedure calls over UDP.


## Thread Interaction
- The `Floor` thread reads input data from a file and sends messages to the Scheduler.
- The `ElevatorSubsystem` thread continuously receives messages from the Scheduler and its 4 Elevators to update the real-time ElevatorData and 
send it to the Scheduler thread
- The four `Elevator` threads travel to the destination floors requested and sends responses once arrived to destination floor.
The Elevator thread can also simulate failures when they are injected through the input files.
- The `Scheduler` schedules request to the most convenient Elevator, using the real-time ElevatorData received from the ElevatorSubsystem.
- The `FloorMonitor` thread monitors the Floor Subsystem to receive input requests into the system.

## Unit Tests
- FloorTest: Tests the ability of the Floor subsystem to read input file and
pass data back and forth.
- ElevatorTest: 
  - Test 1: Ensures the Elevator can correctly send and receive messages.
  - Test 2: Validates the Elevator's ability to travel to requested floors.
- MessageBoxTest: Test the put() and get() functions of the box and guarantee mutual exclusion.
- SchedulerTest: Test the ability of Scheduler to pass data back and forth between Floor and Elevator.

## Dependencies

The project uses standard Java libraries. The test classes run using the JUnit 5.8.1 Library

## How to Run
To run the project, follow these steps:

### LOCALLY:
1. Open the project on IntelliJ.
2. Run the main method in the Scheduler class.
2. Run the main method in the ElevatorSubsystem class.
3. Run the main method in the Floor class.
4. To execute JUnit tests, right-click on 'src' folder in IntelliJ and select 'Run All Tests'