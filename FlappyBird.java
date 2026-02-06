import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

//A classical FlappyBird game built in Java using Swing,Action Listener, and Key listener. The goal is to fly through
// each pipe without hitting it. If collision, game is over.

public class FlappyBird extends  JPanel implements ActionListener, KeyListener{

    //Initiating board width and height
    int boardWidth = 360;
    int boardHeight = 640;

    //Images
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    //Bird's attributes
    int birdX = boardWidth/8;
    int birdY = boardHeight/2;
    int birdWidth = 34;
    int birdHeight = 24;


    //Bird class
    class Bird{
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        //Bird Constructor
        Bird(Image img){
            this.img = img;
        }
    }

    //Pipes attributes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    //Pipe class
    class Pipe{
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        //Pipe constructor
        Pipe(Image img){
            this.img = img;
        }
    }

    //Game Logic
    Bird bird;
    int velocityX = -4;
    int velocityY = 0;
    int gravity = 1;
    double highestScore = 0;

    //Store all pipes and generate random positions
    ArrayList<Pipe> pipes;
    Random random = new Random();

    //Timer
    Timer gameLoop;
    Timer placePipesLoop;

    //Game state and Score
    boolean gameOver = false;
    double score = 0;
    boolean startTheGame = false;


    //FlappyBird Constructor
    FlappyBird(){

        //Set panel size and enable keyboard input
        setPreferredSize(new Dimension(boardWidth,boardHeight));
        setFocusable(true);
        addKeyListener(this);

        //Load Images
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        //Creating bird class and pipes ArrayList
        bird = new Bird(birdImg);
        pipes = new ArrayList<>();

        //Place pipes timer
        placePipesLoop = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        //Starting the timer
        placePipesLoop.start();

        //Game timer
        gameLoop = new Timer(1000/60, this);
        gameLoop.start();
    }

    public void placePipes(){

        //Generating a random position for the top pipe
        int randomPipeY = (int)(pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));

        //Space between the top and bottom pipes
        int openingSpace = boardHeight/4;

        //Create and position the top pipe
        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        //Create and position the bottom pipe
        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);

    }

    //Paint the method automatically
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    // Drawing everything on screen
    public void draw(Graphics g){

        //Background Image
        g.drawImage(backgroundImg,0,0,boardWidth,boardHeight,null);

        //Bird
        g.drawImage(bird.img,bird.x,bird.y,bird.width,bird.height,null);

        //Pipes
        for(int i =0; i< pipes.size();i++){
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img,pipe.x,pipe.y,pipe.width,pipe.height,null);
        }

        //score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN,32));

        if (gameOver){
            g.drawString("Game Over: " + String.valueOf((int) score),10,35);

            //Highest score
            g.setColor(Color.WHITE);
            g.setFont(new Font("Times New Roman", Font.BOLD,24));
            g.drawString("HIGHEST SCORE: " + String.valueOf((int)highestScore),boardWidth/4,boardHeight/2);
            g.drawString("Press SPACEBAR to restart",boardWidth/8,boardHeight/4);
        }
        else{
            g.drawString(String.valueOf((int)score),10,35);
        }

        if(!startTheGame){
            //Start of the screen
            g.setColor(Color.white);
            g.setFont(new Font("Times new Roman",Font.BOLD,24));
            g.drawString("Press SPACE to start", boardWidth/4,boardHeight/2);
        }
    }

    public void move(){

        //Bird's logic
        velocityY += gravity;
        bird.y += velocityY;

        //Ensures the bird does not go above the screen
        bird.y = Math.max(bird.y,0);

        //pipes logic
        for(int i = 0; i < pipes.size();i++){
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            //Checks if bird has passed the pipe
            //If so, mark the pipe as passed and increase the score
            if(!pipe.passed && bird.x > pipe.x + pipe.width){
                pipe.passed = true;
                score += 0.5;
            }

            //Checks collision. If so, game stops
            if(collision(bird,pipe)){
                gameOver = true;
            }

            //If bird hits the bottom of the screen,then game stops.
            if(bird.y > boardHeight){
                gameOver = true;
            }

        }

        //Stores the highest score each time
        highestScore = Math.max(highestScore,score);

    }

    //To check if any two objects collided with each other
    public boolean collision(Bird a, Pipe b){
        return  a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y +  b.height &&
                a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(startTheGame && !gameOver){
            //If the game has started and is not over, update the game state and redraw.
            move();
            repaint();
        }else if(!startTheGame){
            //If the game has not started, just redraw the screen.
            repaint();
        }else if(gameOver){
            //If game is over, stop the timers.
            placePipesLoop.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE){

            //If "SPACEBAR" is clicked change the state to true.
            startTheGame = true;
            if(startTheGame){
                velocityY = -9;
            }
            if(gameOver){
                //Restart the game by resetting the conditions.
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placePipesLoop.start();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {


    }


}
//End of the code
//Done By: Sahil Bdr. Devkota
