import javax.swing.*;

public class App {

    public static void main(String[] args) throws Exception{

        //Initiating board width and height.
        int boardWidth = 360;
        int boardHeight = 640;


        //Creating a JFrame name Flappy Bird.
        JFrame frame = new JFrame("Flappy bird");

        //Setting up JFrame
        frame.setSize(boardWidth,boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Creating flappybird panel
        FlappyBird flappyBird = new FlappyBird();
        frame.add(flappyBird);
        frame.pack();
        flappyBird.requestFocus();

        //Make sure that the screen is visible to the user
        frame.setVisible(true);


    }




}
