package ch.uzh.group38;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI {

    public static final ClassLoader loader = GUI.class.getClassLoader();
    //the argument might be null, but intellij's suggestion does not solve that issue
    private final Icon whitePawnIcon = new ImageIcon("2nd_assignment/Checkers2/resources/white_pawn.png");
    private final Icon whiteKingIcon = new ImageIcon("2nd_assignment/Checkers2/resources/white_king.png");
    private final Icon redPawnIcon = new ImageIcon("2nd_assignment/Checkers2/resources/red_pawn.png");
    private final Icon redKingIcon = new ImageIcon("2nd_assignment/Checkers2/resources/red_king.png");

    private Move currentMove;
    private Board board;


    private int x1;
    private int y1;
    private boolean pawnActive = false;
    private JFrame frame;
    private static User player1;
    private static User player2;
    private final JPanel gui = new JPanel();
    private final JPanel history = new JPanel();
    private final Square[][] playBoardSquares = new Square[8][8];
    private final String COLS = "ABCDEFGH";
    private static final JLabel message = new JLabel("Your add here!");

    private GUI() {

        //creating new window
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Checkers");

        //creating action listeners
        for (int i = 0; i< playBoardSquares.length; i++) {
            for (int j = 0; j< playBoardSquares[i].length; j++) {
                if ((i+j) %2 == 1){
                    playBoardSquares[i][j] = new BlackSquare(new ButtonPressed(i, j));
                }
                else {
                    playBoardSquares[i][j] = new EmptySquare(new ButtonPressed(i, j));
                }
            }
        }
        RuleEvaluator.resetCurrentRound();
        setPlayers();
        reset();
        frame.pack();
    }

    private void refresh(){
        history.removeAll();
        gui.removeAll();
        message.setText(GUI.currentPlayerName() + " please enter your move");
                
        //creating toolbar
        JButton rb = new JButton("Reset");
        rb.addActionListener(new ResetButton());
        JButton hb = new JButton("Game history");
        hb.addActionListener(new ScoreTableButton());
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.add(rb); 
        toolbar.addSeparator();
        toolbar.add(hb); 
        toolbar.addSeparator();
        toolbar.add(message);
        toolbar.setOpaque(false);
        
        //creating the board
        JPanel playBoard = new JPanel(new GridLayout(0, 10));
        playBoard.setBorder(new LineBorder(Color.black));



        Iterator currentIterator = board.createIterator();
        while (currentIterator.hasNext()){
            Field currentField = currentIterator.next();
            int i =currentField.getX();
            int j = currentField.getY();
            if ((i+ j)%2 ==1){
                if (currentField.isRed() && currentField.isKing()){
                    playBoardSquares[i][j].setInactiveAction();
                        playBoardSquares[i][j].applyIcon(redKingIcon);
                }
                else if (currentField.isRed() && !currentField.isKing()){
                    playBoardSquares[i][j].setInactiveAction();
                        playBoardSquares[i][j].applyIcon(redPawnIcon);
                }
                else if (currentField.isWhite() && currentField.isKing()){
                    playBoardSquares[i][j].setInactiveAction();
                        playBoardSquares[i][j].applyIcon(whiteKingIcon);
                }
                else if (currentField.isWhite() && !currentField.isKing()){
                    playBoardSquares[i][j].setInactiveAction();
                        playBoardSquares[i][j].applyIcon(whitePawnIcon);
                }
                else {
                    playBoardSquares[i][j].setEmptyAction();
                        //removes any icon
                        playBoardSquares[i][j].applyIcon(null);
                }

            }
            else playBoardSquares[i][j].setVoidAction();
        }

        //fill in top row
        playBoard.add(new JLabel("+",SwingConstants.CENTER));
        for (int i = 0; i < 8; i++) {
            playBoard.add(new JLabel(COLS.substring(i, i + 1), SwingConstants.CENTER));
        }
        playBoard.add(new JLabel("+",SwingConstants.CENTER));
        
        //fill in playboard
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 10; j++) {
                if (j == 0 || j==9) {
                        playBoard.add(new JLabel("" + (8-i), SwingConstants.CENTER));
                }
                else {
                        playBoard.add(playBoardSquares[i][j-1]);
                }
            }
        }
        //fill in bottom row
        playBoard.add(new JLabel("+",SwingConstants.CENTER));
        for (int i = 0; i < 8; i++) {
            playBoard.add(new JLabel(COLS.substring(i, i + 1), SwingConstants.CENTER));
        } 
        playBoard.add(new JLabel("+", SwingConstants.CENTER));


        //set up GUI
        gui.setBorder(new EmptyBorder(5, 5, 5, 5));
        gui.setLayout(new BoxLayout(gui, BoxLayout.Y_AXIS));
        gui.add(toolbar);
        gui.add(playBoard);
        frame.add(gui);
        frame.setVisible(true);
    }

    private void setPlayers(){
        RuleEvaluator.resetCurrentPlayer();
        player1 = new User(askPlayerName());
        RuleEvaluator.updateTurn();
        player2 = new User(askPlayerName());
        RuleEvaluator.resetCurrentPlayer();
    }

    private void reset(){        
        board = new Board();
        if (pawnActive) {
            playBoardSquares[x1][y1].deactivate();
        }
        refresh();
    }

    public static void setMessage(String msg){
        message.setText(msg);
    }

    private String askPlayerName(){
        String playername = JOptionPane.showInputDialog(frame, "Player " + RuleEvaluator.getCurrentPlayer() + ", please enter your name", "Player " + RuleEvaluator.getCurrentPlayer());
        return playername;
    }

    private static User currentPlayer(){
        if (RuleEvaluator.getCurrentPlayer() == 1){
            return (player1);}
        return(player2);
    }

    public static String currentPlayerName(){
        return(GUI.currentPlayer().getName());
    }

    public void displayHistory(boolean roundEnd){
        gui.removeAll();
        history.removeAll();
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);

        if (roundEnd){
            message.setText("Player " + GUI.currentPlayerName() + " wins this round!! Do you want to play one more?");
            JButton resb = new JButton("One more round");
            resb.addActionListener(new NextRoundButton());
            JButton rb1 = new JButton("New Game");
            rb1.addActionListener(new ResetButton());
            toolbar.add(resb);
            toolbar.addSeparator();
            toolbar.add(rb1);
            toolbar.addSeparator();
            toolbar.add(message);
        }

        else{
            message.setText("Round " + RuleEvaluator.getCurrentRound());
            JButton rb3 = new JButton("back to game");
            rb3.addActionListener(new BackButton());
            toolbar.add(rb3);
            toolbar.addSeparator();
            toolbar.add(message);
        }


        toolbar.setOpaque(false);
        history.add(toolbar);
        history.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Score table", TitledBorder.CENTER, TitledBorder.TOP));
        String[][] rec = {
                { player1.getName(), String.valueOf(player1.getScore())},
                { player2.getName(), String.valueOf(player2.getScore()) },

        };
        String[] header = { "Player", "Score"};
        JTable table = new JTable(rec, header);
        history.add(new JScrollPane(table));
        frame.add(history);
        frame.setVisible(true);
    }
    
    class ButtonPressed implements ActionListener{
        private final int x;
        private final int y;

        private State currentState;

        private final VoidState voidState;
        private final EmptyState emptyState;
        private final InactiveState inactiveState;
        private final ActiveState activeState;

        public ButtonPressed(int x, int y) {
            this.x = x;
            this.y = y;
            //initialise states
            this.voidState = new VoidState();
            this.emptyState = new EmptyState(this);
            this.inactiveState = new InactiveState(this);
            this.activeState = new ActiveState(this);

            this.currentState = voidState;
        }

        public void setState(State passedState) {
            this.currentState = passedState;
        }

        public State getVoidState() {return this.voidState;}
        public State getEmptyState() {return this.emptyState;}
        public State getInactiveState() {return this.inactiveState;}
        public State getActiveState() {return this.activeState;}

        @Override
        public void actionPerformed(ActionEvent e) {
            this.currentState.actionPerformed(e);
        }
    }

    interface State {
        void actionPerformed(ActionEvent e);
    }

    //white square pressed, mustn't be static
    class VoidState implements State {
        public VoidState() {}

        public void actionPerformed(ActionEvent e) {
            message.setText(GUI.currentPlayerName() + " please touch your pawns only!");
        }
    }

    //empty square pressed
    class EmptyState implements State {
        private final ButtonPressed buttonPressed;

        public EmptyState(ButtonPressed actionListener) {
            this.buttonPressed = actionListener;
        }

        public void actionPerformed(ActionEvent e) {
            if (pawnActive) {
                currentMove = new Move(x1, y1, buttonPressed.x, buttonPressed.y);
                if (board.getField(x1, y1).isMoveStored(currentMove)) {
                    currentMove.move(board);
                    playBoardSquares[x1][y1].deactivate();
                    pawnActive = false;
                    refresh();
                    if (RuleEvaluator.checkWinner(board)) {
                        currentPlayer().increaseScore();
                        displayHistory(true);
                    }
                }
                else {
                    message.setText(GUI.currentPlayerName() + " this is not a valid move");
                }
            }
            else {
                message.setText(GUI.currentPlayerName() + " please touch your pawns only!");
            }
        }
    }

    class InactiveState implements State {
        private final ButtonPressed buttonPressed;

        public InactiveState(ButtonPressed buttonPressed) {
            this.buttonPressed = buttonPressed;
        }

        public void actionPerformed(ActionEvent e) {
            if(RuleEvaluator.checkInput(buttonPressed.x, buttonPressed.y)) {
                //deactivate the previously pressed button
                if (pawnActive) {
                    playBoardSquares[x1][y1].setInactiveAction();
                    playBoardSquares[x1][y1].deactivate();
                }
                //activate this button
                buttonPressed.setState(buttonPressed.getActiveState());
                playBoardSquares[buttonPressed.x][buttonPressed.y].activate();
                x1 = buttonPressed.x;
                y1 = buttonPressed.y;
                pawnActive = true;
                message.setText(GUI.currentPlayerName() + " please select target field!");
            }
            else {
                message.setText(GUI.currentPlayerName() + " please touch your pawns only!");
            }
        }
    }

    class ActiveState implements State{
        private final ButtonPressed buttonPressed;

        public ActiveState(ButtonPressed buttonPressed) {
            this.buttonPressed = buttonPressed;
        }

        public void actionPerformed(ActionEvent e) {
            buttonPressed.setState(buttonPressed.getInactiveState());
            playBoardSquares[buttonPressed.x][buttonPressed.y].deactivate();
            pawnActive = false;
            message.setText(GUI.currentPlayerName() + " please enter your move");
        }
    }

    class ResetButton implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            RuleEvaluator.resetCurrentRound();
            setPlayers();
            reset();
        }
    }

    class ScoreTableButton implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            displayHistory(false);
        }
    }

    class BackButton implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            refresh();
        }
    }

    class NextRoundButton implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            RuleEvaluator.updateCurrentRound();
            reset();
        }
    }

    public static void main(String[] args) {
        new GUI();
    }
}