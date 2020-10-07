import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
/**
 * A class modelling a tic-tac-toe (noughts and crosses, Xs and Os) game.
 * 
 * @author Fareen Lavji
 * @version 12.02.2017
 */

public class TicTacToe implements ActionListener
{
    /* Constants */
    public static final String playerX = "X";
    public static final String playerO = "O";
    public static final String EMPTY = "";  // empty cell
    public static final String TIE = "T"; // game ended in a tie
    
    /* Images */
    public static ImageIcon X = new ImageIcon("images/X.png");
    public static ImageIcon O = new ImageIcon("images/O.png");
    
    /* Game stats */
    private String winner;    // winner: playerX, playerO, TIE, EMPTY = in progress
    private int turnCounter;  // number of moves played
    
    /* window setup */
    private JMenuItem newGame = new JMenuItem("New");   // New game menu item
    private JMenuItem quitGame = new JMenuItem("Quit"); // Quit game menu item
    private JButton[][] buttons = new JButton[3][3];    // 2D array of buttons
    private JLabel status = new JLabel();               // status of game
   
    /** 
     * Constructs a new Tic-Tac-Toe game GUI.
     */
    public TicTacToe()
    {
        /* Resize Images. */
        X = new ImageIcon(X.getImage().getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH));
        O = new ImageIcon(O.getImage().getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH));
        
        /* 
         * Initial setup of window 
         */
        JFrame game = new JFrame("TicTacToe");
        Container contentPane = game.getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        
        /* 
         * Setting up menu. 
         */
        JMenuBar menuBar = new JMenuBar();
        game.setJMenuBar(menuBar);
        
        JMenu gameMenu = new JMenu("Game");
        menuBar.add(gameMenu);
        
        gameMenu.add(newGame);
        gameMenu.add(quitGame);
        
        //Key Shortcuts
        final int shortCut = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        newGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, shortCut));
        quitGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, shortCut));
        
        // listen for menu items
        newGame.addActionListener(this);
        quitGame.addActionListener(this);
        
        /*
         * Setup of board.
         */
        JPanel board = new JPanel();             // panel representing the board
        board.setLayout(new GridLayout(3, 3));   // setup as 3 x 3 grid
        game.setPreferredSize(new Dimension(600, 600));
        
        // Adding buttons to board.
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton();
                board.add(buttons[i][j]);
                buttons[i][j].addActionListener(this);  // listen for buttons
            }
        }
        
        /*
         * Add board and status to game window.
         */
        contentPane.add(board);
        contentPane.add(status);
        status.setAlignmentX(Component.CENTER_ALIGNMENT);
        status.setFont(new Font("Arial", Font.BOLD, 25));
        
        /*
         * Final touches.
         */
        game.setSize(650, 750);
        game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        game.pack();
        game.setResizable(true);
        game.setVisible(true);
        playSound("sounds/inOut.WAV");
        clearBoard(); // initialize board
    }

    /**
     * Sets everything up for a new round.  Enables all the buttons and resets their icons.
     */
    private void clearBoard()
    {
        
        newGame.setEnabled(false);
        winner = EMPTY;
        turnCounter = 0;
        status.setText(playerX + "'s turn.");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setEnabled(true);
                buttons[i][j].setIcon(null);
            }
        }
    }

    /**
     * Action listener method that is called every time a button or menu item is selected.
     */

    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() instanceof JButton) { // button was selected
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (e.getSource() == buttons[i][j]) { // found button that was set
                        if (turnCounter % 2 == 0) {
                            buttons[i][j].setIcon(X);
                        } else {
                            buttons[i][j].setIcon(O);
                        }
                        buttons[i][j].setEnabled(false); // button is now disabled
                        turnCounter++;
                        if (hasWon(i, j)) { // player has won
                            if (turnCounter % 2 == 1) winner = playerX;
                            else winner = playerO;
                        }
                    }
                }
            }
        } else { // menu item was selected
            if ((JMenuItem)e.getSource() == newGame) { // new game
                playSound("sounds/reset.WAV");
                clearBoard();
            } else if ((JMenuItem)e.getSource() == quitGame) { // quit game
                stopPlaySound("sounds/inOut.WAV");
                System.exit(0);
            }  
        }
        if (turnCounter == 1) newGame.setEnabled(true); // enable new game after turn 1
        turnEnd();
    } 

    /**
     * Returns true if pressing the given button gives us a winner, and false
     * otherwise.
     *
     * @param int row of button just set.
     * @param int col of button just set.
     * 
     * @return True if we have a winner, false otherwise.
     */
    private boolean hasWon(int row, int col) 
    {
        // unless at least 5 squares have been filled, we don't need to go any further
        // (the earliest we can have a winner is after player X's 3rd move).

        if (turnCounter<5) return false;

        // Note: We don't need to check all rows, columns, and diagonals, only those
        // that contain the latest filled square.  We know that we have a winner 
        // if all 3 squares are the same, as they can't all be blank (as the latest
        // filled square is one of them).

        // check row "row"
        if (buttons[row][0].getIcon() == buttons[row][1].getIcon() 
            && buttons[row][0].getIcon() == buttons[row][2].getIcon()) {
            return true;
        }
       
        // check column "col"
        if (buttons[0][col].getIcon() == buttons[1][col].getIcon() 
            && buttons[0][col].getIcon() == buttons[2][col].getIcon()) {
            return true;
        }

        // if row=col check one diagonal
        if (row==col) {
            if (buttons[0][0].getIcon() == buttons[1][1].getIcon() 
                && buttons[0][0].getIcon() == buttons[2][2].getIcon()) {
                return true;
            }
        }

        // if row=2-col check other diagonal
        if (row==2-col) {
            if (buttons[0][2].getIcon() == buttons[1][1].getIcon() 
                && buttons[0][2].getIcon() == buttons[2][0].getIcon()) {
                return true;
            }
        }

        // no winner yet
        return false;
    }
   
    /**
     * Processes the end of the turn. If there is a winner, ends the round and updates status
     * accordingly.
     */
    private void turnEnd() 
    {
        if (!winner.equals(EMPTY)) {
             if (winner.equals(playerX)) {
                status.setText(playerX + " won the round!");
            } else if (winner.equals(playerO)) {
                status.setText(playerO + " won the round!");
            }
            roundEnd();
            playSound("sounds/gameWon.WAV");
        } else {
            if (turnCounter == 9) {
                status.setText("The round was a tie...");
                roundEnd();
                playSound("sounds/tie.WAV");
            } else if (turnCounter % 2 == 0) {
                status.setText(playerX + "'s turns.");
                playSound("sounds/oPlayed.WAV");
            } else if (turnCounter % 2 == 1) {
                status.setText(playerO + "'s turn.");
                playSound("sounds/xPlayed.WAV");
            }
        }
    }
    
    /**
     * Processes the end of the round. Disables all the remaining buttons.
     */
    private void roundEnd() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].isEnabled()) buttons[i][j].setEnabled(false);
            }
        }
    }
    
    /**
     * Play sound effects without sleeping the program.
     * 
     * @param filepath The path of the sound to be played.
     */
    private static void playSound(String filepath) {
        try {
            Clip clip = AudioSystem.getClip(); 
            clip.open(AudioSystem.getAudioInputStream(new File(filepath)));
            clip.start(); // starts the audio
        } catch (Exception e) {
        }
    }
    
    /**
     * Play sound effects while pausing the program.
     */
    private static void stopPlaySound(String filepath) {
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(new File("sounds/gameWon.WAV")));
            clip.start();
            
            Thread.sleep(clip.getMicrosecondLength()/1000);
        } catch (Exception e) {
        }
    }
    
    public static void main(String[] args) {
        new TicTacToe();
    }
}

