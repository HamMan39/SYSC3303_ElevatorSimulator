import javax.swing.*;
import java.awt.*;

/**
 * This class represents the User view of the Elevator Subsystem. It displays
 * a grid of the system's floors and elevators, indicating travelling elevators
 * and failures using colors and messages.
 * @author Areej Mahmoud 101218260
 * @author Mahnoor Fatima 101192353
 */

public class ElevatorView extends JFrame implements ElevatorViewHandler{
    private JButton buttons[][]; // buttons representing grid squares.
    private JPanel board; //panel to hold buttons representing the elevators/floors
    private static final int ELEVATORS=4; // number of elevators
    private static final int FLOORS = 22; //number of floors to service

    private static final int MAX_INDEX = FLOORS -1;


    public ElevatorView(){
        super("Elevator System");
        this.setLayout(new BorderLayout());

        /*Create a title label and a panel for floor labels*/
        JLabel titleLabel = new JLabel("The Elevator System Simulator", SwingConstants.CENTER);
        this.add(titleLabel, BorderLayout.NORTH);
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new GridLayout(FLOORS, 1));
        JLabel floorLabels[][] = new JLabel[FLOORS][1]; //floor labels column

        //create board
        board = new JPanel();
        board.setLayout(new GridLayout(FLOORS, ELEVATORS)); // grid size
        buttons = new JButton[FLOORS][ELEVATORS];
        //initialize the buttons grid and add buttons to the board.
        for (int i = 0; i < FLOORS; i++) {
            for (int j = 0; j < ELEVATORS; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setEnabled(false);
                if (i == MAX_INDEX){
                    buttons[i][j].setBackground(Color.green);
                    buttons[i][j].setText("IDLE");
                }else {
                    buttons[i][j].setBackground(Color.lightGray);
                }
                board.add(buttons[i][j]);
            }
            /*Add labels with floor numbers to each row representing a floor*/
            floorLabels[i][0] = new JLabel(" Floor "+ (MAX_INDEX -i)+" >> ");
            labelPanel.add(floorLabels[i][0]);
        }
        /* Place the board at the center, and labels to the left. */
        this.add(board, BorderLayout.CENTER);
        this.add(labelPanel, BorderLayout.WEST);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800,700);
        this.setVisible(true);
    }

    /**
     * Handles state change events for elevators.
     *
     * @param e The elevator event triggering the state change.
     */
    @Override
    public void handleStateChange(ElevatorEvent e) {
        Elevator elevator = (Elevator) e.getSource();

        //Set text and update colours
        JButton button = buttons[MAX_INDEX - elevator.getCurrentFloor()][elevator.getElevatorId()];
        button.setBackground(Color.green);
        button.setText(e.getCurrState().toString());
    }

    /**
     * Handles travel floor events for elevators.
     *
     * @param e The elevator event triggering the travel floor change.
     */
    @Override
    public void handleTravelFloor(ElevatorEvent e) {
        Elevator elevator = (Elevator) e.getSource();

        //Update the elevator moving showing green block and set text
        if (e.getDirection() == Message.Directions.UP){
            JButton button = buttons[MAX_INDEX - elevator.getCurrentFloor()-1][elevator.getElevatorId()];
            button.setBackground(Color.green);
            button.setText(e.getCurrState().toString());
        }
        else if(e.getDirection() == Message.Directions.DOWN){
            JButton button = buttons[MAX_INDEX - elevator.getCurrentFloor()+1][elevator.getElevatorId()];
            button.setBackground(Color.green);
            button.setText(e.getCurrState().toString());
        }

        //Update the floor boxes to reset to light gray blocks and set text
        JButton button = buttons[MAX_INDEX - elevator.getCurrentFloor()][elevator.getElevatorId()];
        button.setBackground(Color.lightGray);
        button.setText("");
    }

    /**
     * Handles timeout failure events for elevators.
     *
     * @param e The elevator event triggering the timeout failure.
     */
    @Override
    public void handleTimeoutFailure(ElevatorEvent e) {
        Elevator elevator = (Elevator) e.getSource();
        JButton button = buttons[MAX_INDEX - elevator.getCurrentFloor()][elevator.getElevatorId()];

        //Update the blocks to be red and set the text to disabled
        button.setBackground(Color.RED);
        button.setText(e.getCurrState().toString());
    }

    /**
     * Handles door failure events for elevators.
     *
     * @param e The elevator event triggering the door failure.
     */
    @Override
    public void handleDoorFailure(ElevatorEvent e) {
        Elevator elevator = (Elevator) e.getSource();
        JButton button = buttons[MAX_INDEX - elevator.getCurrentFloor()][elevator.getElevatorId()];

        //Update the blocks to be red and set the text to door stuck/door closed accordingly
        if (e.getCurrState() == Elevator.state.DOOR_STUCK){
            button.setBackground(Color.RED);
            button.setText(e.getCurrState().toString());
        }
        else{
            button.setBackground(Color.GREEN);
            button.setText(e.getCurrState().toString());
        }
    }

    public static void main(String[] args) {
        new ElevatorView();
    }

}
