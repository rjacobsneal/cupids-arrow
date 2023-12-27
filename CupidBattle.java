//**************************************************************************
// File: CupidBattle.java                       CPSC 112 Final Project
// 
// Authors: Misho Gabashvili and Reese Neal
// NetID: mg2736, rjn29
//
// Class: CupidBattle
// Dependencies: Arrow, Obstacle, Cupid, DrawingPanel 
//
// Time for this program: ~20 hours
//
// Description  :  Cupid Battle Game
//  
//  This program simulates a game by with unlimited rounds. When each 
//  game begins, the program creates two Cupid objects with given
//  number of lives. At the beginning of each round (i.e. when player 1
//  shoots) the players change their vertical position and an obstacle 
//  is created at the center of the panel with a randomly selected height.
//  To complicate the game even further, we introduced windspeed, which 
//  alters the horizontal velocity (the magnitude is displayed in the
//  launch frame). 
//      Each time a player shoots, they must take into account their
//  position, the opponent's position, windspeed, gravity and the obstacle.
//  Players select the initial velocity and angle of their shot in the
//  given frame using sliders. To assist them in their choice, a line is 
//  displayed at an angle and length that is determined by slider angle
//  and velocity values, respectively. After they click the 'launch' button, 
//  a new arrow object is created with given input and a short animation
//  displays its course mid-flight. If the arrow hits a cupid, they lose 
//  one life. The arrow should also avoid hitting the obstacle and screen 
//  edges. If one of the players looses all lives, there is an extra 
//  animation of the defeated cupid falling in love with the winner.
//  After this animation, the players are prompted to restart the game.  
// 
//**************************************************************************

import java.io.File;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Random;

public class CupidBattle {

    // final variables
    public static final int FRAME_T = 17;
    public static final double ANIMATIONLENGTH = 10;
    public static final double GRAVITY = 0.33;
    public static final int DANGERRANGE = 30;
    public static final int IMAGESIZE = 100;
    public static final int OBSTACLEWIDTH = 75;
    public static final int OBSTACLEBOTTOMHEIGHT = 35;
    public static final int LIFEHEARTSIZE = 30;
    public static final int FRAMESIZE = 300;

    // set up panel
    static final int HEIGHT = 855;
    static final int WIDTH = 1435;
    // static final int HEIGHT = 700;
    // static final int WIDTH = 850;

    static DrawingPanel panel = new DrawingPanel(WIDTH, HEIGHT);
    static Graphics2D g = panel.getGraphics();

    // enable double buffering
    static BufferedImage offscreen = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    static Graphics2D osg = offscreen.createGraphics();

    // sliders
    public static final int slideStart = WIDTH/2 - FRAMESIZE/2;
    public static final int slideEnd = slideStart + 180;
    public static final int circleRad = 10;

    // velocity slider position
    public static final int vSlideY = HEIGHT/2; 
    public static int vSlideX = WIDTH/2 - FRAMESIZE/2;

    // angle slider
    public static final int aSlideY = HEIGHT/2 + 100;
    public static int aSlideX = WIDTH/2 - FRAMESIZE/2;

    // launch button
    public static final int buttonSize = 50;
    public static final int buttonX = WIDTH/2 - buttonSize/2;
    public static final int buttonY = HEIGHT/2 + 175;

    // restart button position
    public static final int restartX = WIDTH/2 - 350 + 300;
    public static final int restartY =  HEIGHT/2 - 350 + 600;

    // objects
    public static Arrow arrow;
    public static Cupid shooter;
    public static Cupid opponent;
    public static Obstacle column;

    // other instance variables
    public static int count;
    public static boolean stopArrow;
    public static boolean gameOver = false;
    public static boolean restart = true;
    public static boolean launch = false;
    public static boolean startgame = false;

    // column height
    public static final int MINHEIGHT = 300;
    public static final int MAXHEIGHT = 700;

    // variables that user decides each game
    public static int cupid1NumLives;
    public static int cupid2NumLives;

    // variables that user decides each round
    public static double velocity; 
    public static double angle;

    // base values for user input
    public static double baseVelocity = 1;
    public static double baseAngle = -90;

    // variables that will be chosen randomly by system each round
    public static Random rand = new Random();
    public static int cupid1X = WIDTH/7;
    public static int cupid1Y;
    public static int cupid2X = WIDTH - WIDTH/7 - IMAGESIZE; 
    public static int cupid2Y;
    public static double windspeed = 0;

    // instantiate
    public static Cupid cupid1;
    public static Cupid cupid2;

    // runs the entire game
    public static void main(String[] args) throws IOException {

            Arrow.loadArrows();
            panel.onClick(((x, y) -> lives(x, y)));
            panel.onDrag((x, y) -> velocityUpdate(x, y));
            panel.onDrag((x, y) -> angleUpdate(x, y));
            panel.onClick(((x, y) -> launch(x, y)));
            panel.onClick(((x, y) -> restart(x, y)));
        
        while (restart == true) {
            startgame = false;
            gameOver = false;
            while (startgame == false) {
                count = 0;
                osg.setColor(Color.PINK);
                osg.fillRect(0, 0, WIDTH, HEIGHT);

                osg.drawImage(ImageIO.read(new File("imgs/Opening.png")), WIDTH/2 - 350, HEIGHT/2 - 350, null);
                g.drawImage(offscreen, 0, 0, null);

                cupid1 = new Cupid(cupid1X, cupid1Y, cupid1NumLives, 1);
                cupid2 = new Cupid(cupid2X, cupid2Y, cupid2NumLives, 2);
            }
            while (gameOver == false) {
                PlayRound();   
            }
        }
    }

    // sets up a new round
    public static void RoundSetUp() throws IOException {
        count++;
        stopArrow = false;
        boolean east = false;

        vSlideX = WIDTH/2 - FRAMESIZE/2;
        aSlideX = WIDTH/2 - FRAMESIZE/2;
        angle = baseAngle;
        velocity = baseVelocity;

        if (count % 2 == 1) {

            // create new wind with equal change of blowing east/west
            double chance = Math.random();
            if (chance >= 0.5) {
                east = false;
                windspeed = Math.random() * 0.03; // wind is blowing towards West
            } else {
                east = true;
                windspeed = -1 * Math.random() * 0.03; // wind is blowing towards East
            }

            // creates new obstacle with random height
            column = new Obstacle(WIDTH / 2 - OBSTACLEWIDTH / 2, rand.nextInt(MAXHEIGHT - MINHEIGHT) + (HEIGHT - MAXHEIGHT));

            cupid1.changeY(rand.nextInt(HEIGHT - 4 * IMAGESIZE) + 2 * IMAGESIZE);
            cupid2.changeY(rand.nextInt(HEIGHT - 4 * IMAGESIZE) + 2 * IMAGESIZE);
           
            // assigns correct roles to each object
            shooter = cupid1;
            opponent = cupid2;
        }

        if (count % 2 == 0) {

            // assigns correct roles to each object
            shooter = cupid2;
            opponent = cupid1;
        }

        // preparing to launch
        for (double t = 0; t < ANIMATIONLENGTH * 100; t += FRAME_T / 1000.0) { 

            osg.drawImage(ImageIO.read(new File("imgs/Sky2.jpg")), 0, 0, null);
            osg.drawImage(column.GetImage(), column.getX(), column.getY(), null);

            drawLives(shooter);
            drawLives(opponent);

            osg.drawImage(ImageIO.read(new File("imgs/Frame.png")), slideStart - 10, vSlideY - 50, null);
            drawLaunchButton();
            drawAngleSlider();
            drawVelocitySlider();

            // displays windspeed
            if (east == true) {
                if (Math.abs(windspeed) <= 0.01) {
                    osg.drawString("WIND: –>", slideStart, aSlideY + 105);
                }
                if (Math.abs(windspeed) > 0.01 && Math.abs(windspeed) <= 0.02) {
                    osg.drawString("WIND: ––>>", slideStart, aSlideY + 105);
                }
                if (Math.abs(windspeed) > 0.02) {
                    osg.drawString("WIND: –––>>>", slideStart, aSlideY + 105);
                }
            }
            if (east == false) {
                if (Math.abs(windspeed) <= 0.01) {
                    osg.drawString("WIND: <–", slideStart, aSlideY + 105);
                }
                if (Math.abs(windspeed) > 0.01 && Math.abs(windspeed) <= 0.02) {
                    osg.drawString("WIND: <<––", slideStart, aSlideY + 105);
                }
                if (Math.abs(windspeed) > 0.02) {
                    osg.drawString("WIND: <<<–––", slideStart, aSlideY + 105);
                }
            }

            // displays velocity and angle
            osg.drawString("" + (int) velocity, slideStart + 60, vSlideY + 30);
            osg.drawString("" + (int) angle, slideStart + 44, aSlideY + 30);

            if (shooter == cupid1) {
                osg.drawLine(shooter.getX() + IMAGESIZE/2, shooter.getY() + IMAGESIZE/2, (int)(shooter.getX() + 10 * velocity * Math.cos(degreesToRadians(angle))) + IMAGESIZE/2, (int)(shooter.getY() - 10 * velocity * Math.sin(degreesToRadians(angle))) + IMAGESIZE/2); 
            }
            else {
                osg.drawLine(shooter.getX() + IMAGESIZE/2, shooter.getY() + IMAGESIZE/2, (int)(shooter.getX() - 10 * velocity * Math.cos(degreesToRadians(angle))) + IMAGESIZE/2, (int)(shooter.getY() - 10 * velocity * Math.sin(degreesToRadians(angle))) + IMAGESIZE/2);
            }

            osg.drawImage(shooter.getCupidImage(), shooter.getX(), shooter.getY(), null);
            osg.drawImage(opponent.getCupidImage(), opponent.getX(), opponent.getY(), null);

            if (launch == true) {
                t = ANIMATIONLENGTH * 100;
            }

            g.drawImage(offscreen, 0, 0, null);
        }

        // creates a new arrow object at correct location
        arrow = new Arrow(shooter.getX(), shooter.getY(), velocity, angle);
    }

    // animates the shot
    public static void PlayRound() throws IOException {

        // set up round
        RoundSetUp();
        launch = false;
        restart = false;

        // update variables
        double xVel = arrow.getVelocity() * Math.cos(degreesToRadians(arrow.getAngle()));
        double yVel = -arrow.getVelocity() * Math.sin(degreesToRadians(arrow.getAngle()));

        int arrowXPosition = arrow.getX();
        int arrowYPosition = arrow.getY();

        boolean drawHearts = false;
        boolean finalImage = false;

        for (double t = 0; t < ANIMATIONLENGTH; t += FRAME_T / 1000.0) {

            osg.drawImage(ImageIO.read(new File("imgs/Sky2.jpg")), 0, 0, null);
            
            if (!gameOver) {
                drawLives(shooter);
                drawLives(opponent);

                if (shooter == cupid1) {

                    // applying wind
                    xVel -= windspeed;
                    arrowXPosition += xVel;
                    arrowYPosition += yVel;
                } else {
                    opponent = cupid1;
                    // applying wind
                    xVel += windspeed;
                    arrowXPosition -= xVel;
                    arrowYPosition += yVel;
                }

                yVel += GRAVITY / 2;

                 // if arrow hits opponent, adjust lives, stop arrow and rounds animation
                 if (arrow.hitCupid(arrowXPosition, arrowYPosition, yVel, xVel, opponent)) {

                    arrowXPosition = shooter.getX(); 
                    arrowYPosition = shooter.getY(); 
                    stopRoundAnimation();

                    drawHearts = true;
                    opponent.setLives(opponent.getLives() - 1);
                    
                    // animate hearts around opponent
                    // if oponent has no more lives left, the game is over
                    if (opponent.getLives() != 0) {
                        t = ANIMATIONLENGTH - (FRAME_T / 1000.0) * 20;
                    } else if (opponent.getLives() == 0) {
                        gameOver = true;
                    }
                }

                // if arrow hits an obstacle, stop arrow and round's animation
                if (gameOver == false && arrow.hitObstacle(arrowXPosition, arrowYPosition, yVel, xVel, column)) {
                    stopRoundAnimation();
                    t = ANIMATIONLENGTH;
                }

                // if arrow goes offscreen, stop arrow and round's animation
                if ((arrowXPosition <= -100) || (arrowXPosition >= WIDTH) || arrowYPosition >= HEIGHT) {
                    stopRoundAnimation();
                    t = ANIMATIONLENGTH;
                }

                // if arrow shouldn't be stopped...
                if (stopArrow == false) {
                    osg.drawImage(arrow.arrowRotation(arrow.flightAngle(yVel, xVel)), arrowXPosition, arrowYPosition, null);
                }

            } else if (gameOver) {
                // if the game has been won
                // losing cupid floats towards winning cupid
                if (Math.abs(dist(shooter.getX(), opponent.getX())) >= 3 && Math.abs(dist(shooter.getY(), opponent.getY())) >= 3) {
                    t = 0;
                    toCupid();
                } else {
                    startgame = false;
                    finalImage = true;
        
                    osg.drawImage(ImageIO.read(new File("imgs/FinalScene.png")), opponent.getX(), opponent.getY(), null);
                    t = ANIMATIONLENGTH - 20 * FRAME_T / 1000.0;
        
                    if (shooter == cupid1) {
                        osg.drawImage(ImageIO.read(new File("imgs/Player1Win.png")), WIDTH/2 - 350, HEIGHT/2 - 350, null);
                    }
                    if (shooter == cupid2) {
                        osg.drawImage(ImageIO.read(new File("imgs/Player2Win.png")), WIDTH/2 - 350, HEIGHT/2 - 350, null);
                    }
                
                    if (restart == true) {
                        t = ANIMATIONLENGTH;
                    }

                }
            }

            if (finalImage == false) {
                osg.drawImage(shooter.getCupidImage(), shooter.getX(), shooter.getY(), null);
                osg.drawImage(opponent.getCupidImage(), opponent.getX(), opponent.getY(), null);
            }

            // draw hearts if needed
            if (drawHearts == true) {
                osg.drawImage(ImageIO.read(new File("imgs/LittleHearts.png")), opponent.getX(), opponent.getY(), null);
            }

            if (gameOver == false) {
                osg.drawImage(column.GetImage(), column.getX(), column.getY(), null);
            }
            // copy from offscreen buffer to panel
            g.drawImage(offscreen, 0, 0, null);

            // pause panel
            panel.sleep(5);
        }
    }

    // directs the defeated cupid towards the winner
    public static void toCupid () {

        // get distance (horizontal and vertical) to the mound
        int distX = dist(shooter.getX(), opponent.getX());
        int distY = dist(shooter.getY(), opponent.getY()); 

            if (Math.abs(distX) >= Math.abs(distY)) {
                if (opponent.getX() > shooter.getX()) {
                    opponent.changeX(opponent.getX() - 3);
                }
                if (opponent.getX() < shooter.getX()) {
                    opponent.changeX(opponent.getX() + 3);
                }
            }
            if (Math.abs(distX) < Math.abs(distY)) {
                if (opponent.getY() > shooter.getY()) {
                    opponent.changeY(opponent.getY() - 3);
                }
                if (opponent.getY() < shooter.getY()) {
                    opponent.changeY(opponent.getY() + 3);
                }
            }        
    }

    // distance function method
    public static int dist(int shooterPos, int opponentPos) {
        return Math.abs(shooterPos - opponentPos);
    }

    // stops the animation (called when arrow hits an object or edge)
    public static void stopRoundAnimation() {
        arrow.setX(shooter.getX());
        arrow.setY(shooter.getY());
        stopArrow = true;
        return;
    }

    // draws the current hearts out of total hearts under each cupid
    public static void drawLives(Cupid cupid) throws IOException {
        for (int i = 0; i < cupid.getHeartImages().length; i++) {
            osg.drawImage(cupid.getHeartImages()[i], cupid.getX() + IMAGESIZE/2 - (LIFEHEARTSIZE * cupid.getHeartImages().length) /2 + (i * LIFEHEARTSIZE), cupid.getY() + 100, null);
        }
    }

    // converts degrees to radians (only needed for testing)
    public static double degreesToRadians(double degrees) {
        return degrees / 180 * Math.PI;
    }

    // converts radians to degrees
    public static double radiansToDegrees(double radians) {
        return radians * 180 / Math.PI;
    }

    // general scaling method
    public static double scale(double oldValue, double oldMin, double oldMax, double newMin, double newMax) {
        double scale = (oldValue - oldMin) / (oldMax - oldMin) * (newMax - newMin) + newMin;
        return scale;
    }

    // updates velocity
    public static void velocityUpdate(int x, int y) {

        // was the slider cicle grabbed?
        if (((x - vSlideX) * (x - vSlideX) + (y - vSlideY) * (y - vSlideY) <= (circleRad * circleRad))) {
        // if so, set slider position
        // and update time step size based on slider position
            if ((x >= slideStart) && (x <= slideEnd)) {
                vSlideX = x;
            }
            // scale(oldValue, oldMin, oldMax, newMin, newMax);
            velocity = scale(vSlideX, slideStart, slideEnd, baseVelocity, 20 * baseVelocity);
            
        }
    }

    // updates angle
    public static void angleUpdate(int x, int y) {

        // was the slider cicle grabbed?
        if (((x - aSlideX) * (x - aSlideX) + (y - aSlideY) * (y - aSlideY) <= (circleRad * circleRad))) {
        // if so, set slider position
        // and update time step size based on slider position
            if ((x >= slideStart) && (x <= slideEnd)) {
                aSlideX = x;
            }
            angle = baseAngle + aSlideX - slideStart; 
        }
    }

    // launch button
    public static void launch(int x, int y) {
        // was the button clicked?
        if (((x - buttonX) * (x - buttonX) + (y - buttonY) * (y - buttonY) <= (buttonSize * buttonSize)) && startgame == true) {
            launch = true;            
        }
    }

    // restart button
    public static void restart(int x, int y) {
        // was the restart button clicked?
        if (((x - restartX) * (x - restartX) + (y - restartY) * (y - restartY) <= (100 * 100))) {
            restart = true;            
        }
    }
    
    // button that lets you choose lives
    public static void lives (int x, int y) {
        // was the button clicked?
        if (((x - (WIDTH/2 - 350 + 150)) * (x - (WIDTH/2 - 350 + 150)) + (y - (HEIGHT/2 - 350 + 425)) * (y - (HEIGHT/2 - 350 + 425)) <= (150 * 150))) {
            cupid1NumLives = 3; 
            cupid2NumLives = 3;           
        }
        if (((x - (WIDTH/2 - 350 + 400)) * (x - (WIDTH/2 - 350 + 400)) + (y - (HEIGHT/2 - 350 + 425)) * (y - (HEIGHT/2 - 350 + 425)) <= (150 * 150))) {
            cupid1NumLives = 1; 
            cupid2NumLives = 1;           
        }

        startgame = true;
    }

    // the following methods draw the buttons
    public static void drawLaunchButton() throws IOException {
        osg.drawImage(ImageIO.read(new File("imgs/LaunchButton.png")), buttonX, buttonY, null);
    }
     
    public static void drawVelocitySlider() {

        osg.setColor(Color.PINK);

        // dashed slider line
        osg.drawLine(slideStart, vSlideY, slideEnd, vSlideY);
        for (int dashX = slideStart; dashX <= slideEnd; dashX += (slideEnd - slideStart) / 12) {
            osg.drawLine(dashX, vSlideY - 5, dashX, vSlideY + 5);
        }

        for (int dashX = slideStart; dashX <= slideEnd; dashX += (slideEnd - slideStart) / 4) {
            osg.drawLine(dashX, vSlideY - 10, dashX, vSlideY + 10);
        }

        // outlined slider "button"
        osg.fillOval(vSlideX - circleRad, vSlideY - circleRad, 2 * circleRad, 2 * circleRad);
        osg.setColor(Color.WHITE);
        osg.drawOval(vSlideX - circleRad, vSlideY - circleRad, 2 * circleRad, 2 * circleRad);
    }

    public static void drawAngleSlider() {

        osg.setColor(Color.GREEN);

        // dashed slider line
        osg.drawLine(slideStart, aSlideY, slideEnd, aSlideY);
        for (int dashX = slideStart; dashX <= slideEnd; dashX += (slideEnd - slideStart) / 12) {
            osg.drawLine(dashX, aSlideY - 5, dashX, aSlideY + 5);
        }

        for (int dashX = slideStart; dashX <= slideEnd; dashX += (slideEnd - slideStart) / 4) {
            osg.drawLine(dashX, aSlideY - 10, dashX, aSlideY + 10);
        }

        // outlined slider "button"
        osg.fillOval(aSlideX - circleRad, aSlideY - circleRad, 2 * circleRad, 2 * circleRad);
        osg.setColor(Color.WHITE);
        osg.drawOval(aSlideX - circleRad, aSlideY - circleRad, 2 * circleRad, 2 * circleRad);
    }
}

