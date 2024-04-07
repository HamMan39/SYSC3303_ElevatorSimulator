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
            floorLabels[i][0] = new JLabel(" Floor "+i+" >> ");
            labelPanel.add(floorLabels[i][0]);
        }
        /* Place the board at the center, and labels to the left. */
        this.add(board, BorderLayout.CENTER);
        this.add(labelPanel, BorderLayout.WEST);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800,700);
        this.setVisible(true);
    }

    @Override
    public void handleStateChange(ElevatorEvent e) {
        Elevator elevator = (Elevator) e.getSource();

        JButton button = buttons[MAX_INDEX - elevator.getCurrentFloor()][elevator.getElevatorId()];
        button.setText(e.getCurrState().toString());
    }

    @Override
    public void handleTravelFloor(ElevatorEvent e) {
        Elevator elevator = (Elevator) e.getSource();

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

        JButton button = buttons[MAX_INDEX - elevator.getCurrentFloor()][elevator.getElevatorId()];
        button.setBackground(Color.lightGray);
        button.setText("");
    }

    @Override
    public void handleTimeoutFailure(ElevatorEvent e) {

    }

    @Override
    public void handleDoorFailure(ElevatorEvent e) {

    }
    public static void main(String[] args) {
        new ElevatorView();
    }

}
