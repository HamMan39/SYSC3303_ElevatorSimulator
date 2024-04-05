import javax.swing.*;
import java.awt.*;

/**
 * This class represents the User view of the Elevator Subsystem. It displays
 * a grid of the system's floors and elevators, indicating travelling elevators
 * and failures using colors and messages.
 * @author Areej Mahmoud 101218260
 */

public class ElevatorView extends JFrame implements ElevatorViewHandler{
    private JButton buttons[][]; // buttons representing grid squares.
    private JPanel board; //panel to hold buttons representing the elevators/floors
    private static final int ELEVATORS=4; // number of elevators
    private static final int FLOORS = 22; //number of floors to service

    public ElevatorView(){
        super("Elevator System");
        this.setLayout(new BorderLayout());

        //create board
        board = new JPanel();
        board.setLayout(new GridLayout(FLOORS, ELEVATORS)); // grid size
        buttons = new JButton[FLOORS][ELEVATORS];
        //initialize the buttons grid and add buttons to the board.
        for (int i = 0; i < FLOORS; i++) {
            for (int j = 0; j < ELEVATORS; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setEnabled(false);
                board.add(buttons[i][j]);
            }
        }
        /* Place the board at the center. */
        this.add(board, BorderLayout.CENTER);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(500,800);
        this.setVisible(true);
    }

    @Override
    public void handleStateChange(ElevatorEvent e) {

    }

    @Override
    public void handleTravelFloor(ElevatorEvent e) {

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
