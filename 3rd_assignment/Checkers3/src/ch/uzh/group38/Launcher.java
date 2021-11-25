package ch.uzh.group38;

import javax.swing.*;

public class Launcher {

    private static GUI gui;
    private static JFrame frame;
    private static int currentRound = 0;


    public void nextMove(){

        frame.add(gui.refresh(currentRound));
        frame.setVisible(true);

    }

    public void finishRound(){
        JOptionPane.showMessageDialog(frame, "Player " +
                RuleEvaluator.getCurrentPlayer() + " wins this round!! Do you want to play one more?");

    }

    public static void reset(){
        frame.add(gui.enterUser(currentRound));
        frame.setVisible(true);
    }

    public static void startRound(String name1, String name2){
        currentRound = 1;
        User user1 = new User(name1);
        User user2 = new User(name2);
        startGame();
    }

    public static void startGame(){

        RuleEvaluator.resetCurrentPlayer();
        gui.reset();
        frame.add(gui.refresh(currentRound));
        frame.setVisible(true);
    }


    public static void nextRound(){
        currentRound = currentRound +1;
        startGame();
    }

    public static void displayHistory(){
        frame.add(gui.displayHistory(currentRound));
        frame.setVisible(true);

    }

    public static void returnToGame(){
        frame.add(gui.refresh(currentRound));
        frame.setVisible(true);

    }


        public static void main(String[] args) {
        Launcher launcher = new Launcher();
    // creating new window
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Checkers");
        frame.pack();

        gui = new GUI(launcher);
        reset();

        }

    }

