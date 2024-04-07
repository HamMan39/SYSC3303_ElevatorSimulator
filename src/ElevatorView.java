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
    private JButton buttons[][], directions[][], capacity[][]; // buttons representing grid squares.
    private JPanel board; //panel to hold buttons representing the elevators/floors
    private static final int ELEVATORS=4; // number of elevators
    private static final int FLOORS = 22; //number of floors to service

    private static final int MAX_INDEX = FLOORS -1;

    // Method to resize the icon
    private ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image newImg = img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(newImg);
    }
    public ElevatorView(){
        super("Elevator System");
        this.setLayout(new BorderLayout());

        /*Create a title label and a panel for floor labels*/
        JLabel titleLabel = new JLabel("The Elevator System Simulator", SwingConstants.CENTER);
        this.add(titleLabel, BorderLayout.NORTH);
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new GridLayout(FLOORS+2, 1));
        JLabel floorLabels[][] = new JLabel[FLOORS+2][1]; //floor labels column

        //create board
        board = new JPanel();
        board.setLayout(new GridLayout(FLOORS+2, ELEVATORS)); // grid size

        //Initialize direction Icon Buttons
        directions = new JButton[1][ELEVATORS];
        //initialize the buttons grid and add buttons to the board.
        for (int j = 0; j < ELEVATORS; j++) {
            directions[0][j] = new JButton();
            directions[0][j].setEnabled(true);
            directions[0][j].setBackground(Color.WHITE);

            //set IDLE direction icons
            ImageIcon newIcon = new ImageIcon("elevatorIDLE.png");
            newIcon = resizeIcon(newIcon, 30, 30);
            // Set the new icon on the button
            directions[0][j].setIcon(newIcon);
            board.add(directions[0][j]);
        }
        /*Add labels with lamp directions numbers to each row representing a floor*/
        floorLabels[0][0] = new JLabel(" - LAMPS - ");
        floorLabels[0][0].setForeground(new Color(0,140,0));
        labelPanel.add(floorLabels[0][0]);

        //Initialize Elevator
        buttons = new JButton[FLOORS][ELEVATORS];
        //initialize the buttons grid and add buttons to the board.
        for (int i = 0; i < FLOORS; i++) {
            for (int j = 0; j < ELEVATORS; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setEnabled(true);
                if (i == MAX_INDEX){
                    buttons[i][j].setBackground(Color.green);
                    buttons[i][j].setText("IDLE");
                    buttons[i][j].setForeground(Color.BLACK);
                }else {
                    buttons[i][j].setBackground(Color.lightGray);
                }
                board.add(buttons[i][j]);
            }
            /*Add labels with floor numbers to each row representing a floor*/
            floorLabels[i][0] = new JLabel(" Floor "+ (MAX_INDEX -i)+" >> ");
            labelPanel.add(floorLabels[i][0]);
        }

        //Initialize capacity bar
        capacity = new JButton[FLOORS+1][ELEVATORS];
        //initialize the buttons grid and add buttons to the board.
        for (int j = 0; j < ELEVATORS; j++) {
            capacity[FLOORS][j] = new JButton();
            capacity[FLOORS][j].setEnabled(true);
            capacity[FLOORS][j].setText("Capacity: 0");
            capacity[FLOORS][j].setForeground(Color.lightGray);
            capacity[FLOORS][j].setBackground(Color.BLACK);

            board.add(capacity[FLOORS][j]);
        }
        /*Add labels with capacity numbers*/
        floorLabels[FLOORS][0] = new JLabel(" - CAPACITY - ");
        floorLabels[FLOORS][0].setForeground(new Color(120,0,0));
        labelPanel.add(floorLabels[FLOORS][0]);

        /* Place the board at the center, and labels to the left. */
        this.add(board, BorderLayout.CENTER);
        this.add(labelPanel, BorderLayout.WEST);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800,700);
        this.setVisible(true);
    }

    /**
     * Handles the direction updates for the lamps.
     *
     * @param event The elevator event triggering the state change.
     */
    public void updateDirectionLamps(ElevatorEvent event){
        Elevator elevator = (Elevator) event.getSource();
        JButton direction = directions[0][elevator.getElevatorId()];

        //Update ICON to UP or DOWN depending on direction
        if (event.getDirection() == Message.Directions.UP || event.getDirection() == Message.Directions.DOWN){
            //set direction icons
            String fileName = "elevator" + event.getDirection().toString() + ".png";
            ImageIcon newIcon = new ImageIcon(fileName);
            newIcon = resizeIcon(newIcon, 30, 30);
            // Set the new icon on the button
            direction.setIcon(newIcon);
        }

        //Update ICON to IDLE if state is IDLE
        if (event.getCurrState() == Elevator.state.IDLE){
            //set direction icons
            String fileName = "elevatorIDLE.png";
            ImageIcon newIcon = new ImageIcon(fileName);
            newIcon = resizeIcon(newIcon, 30, 30);
            // Set the new icon on the button
            direction.setIcon(newIcon);
        }

        //Show elevator OUT OF SERVICE
        if (event.getCurrState() == Elevator.state.DISABLED){
            ImageIcon newIcon = new ImageIcon("elevatorDISABLED.png");
            newIcon = resizeIcon(newIcon, 70, 60);
            // Set the new icon on the button
            direction.setIcon(newIcon);
        }
    }

    /**
     * Handles state change events for elevators.
     *
     * @param e The elevator event triggering the state change.
     */
    @Override
    public void handleCapacityChange(ElevatorEvent e) {
        Elevator elevator = (Elevator) e.getSource();

        //Set text and update colours
        JButton button = capacity[FLOORS][elevator.getElevatorId()];
        button.setText("Capacity: " + e.getCapacity());

        if (e.getCapacity()>Elevator.MAX_CAPACITY - 1){
            button.setBackground(Color.RED);
        }else {
            button.setBackground(Color.BLACK);
        }
    }

    /**
     * Handles state change events for elevators.
     *
     * @param e The elevator event triggering the state change.
     */
    @Override
    public void handleStateChange(ElevatorEvent e) {
        //Update Directions
        updateDirectionLamps(e);

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

        //Update Directions
        updateDirectionLamps(e);

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

        //Update Directions
        updateDirectionLamps(e);

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

        //Update Directions
        updateDirectionLamps(e);

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
