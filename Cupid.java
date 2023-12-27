//**************************************************************************
// File: Cupid.java                       CPSC 112 Final Project
// 
// Authors: Misho Gabashvili and Reese Neal
// NetID: mg2736, rjn29
//
// Class: Cupid
//
// Time for this program: ~5 hours
//
// Description  :  Cupid Object
//
//  This program manages how players (cupids) and their lives are displayed.
//  Each cupid is created with an initial position, number of lives, and 
//  a determiner int for which player their represent. To appear as 
//  constantly moving, the cupids display four images in order, forming a
//  mini-animation. 
//  
//**************************************************************************

import java.io.File;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.io.IOException;

public class Cupid {

    // final variables
    public static final int CUPID_SIZE = 100;
    public static final int DANGER_ZONE_SIZE = 35;
    public static final int NUM_CUPIDS = 2;
    public static final int NUM_IMAGES = 4;
    
    // images of cupids
    public static Image[][] cupidImages = new Image[NUM_CUPIDS][NUM_IMAGES];

    // instance variables
    public int x;
    public int y;
    public int currentLives;
    public int totalLives;
    public int player;
    public int counter;
    public Image cupid;
    public Image[] hearts;

    public Cupid(int xPos, int yPos, int numLives, int numPlayer) {
        x = xPos;
        y = yPos;
        currentLives = numLives;
        totalLives = numLives;
        player = numPlayer;
        counter = 0;
        cupid = null;
        hearts = new Image[totalLives];
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getLives() {
        return currentLives;
    }

    public void setLives(int newLives) {
        currentLives = newLives;
        return;
    }

    public void changeX(int newX) {
        x = newX;
        return;
    }

    public void changeY(int newY) {
        y = newY;
        return;
    }

    public void loseLife() {
        currentLives = currentLives - 1;
        return;
    }

    // loads the cupid images
    // into a 2D array (with one row for all the images of each cupid) 
    public void loadCupidImages() throws IOException{
        for (int i = 0; i < NUM_CUPIDS; i++) {
            for (int j = 0; j < NUM_IMAGES; j++) {
                cupidImages[i][j] = ImageIO.read(new File("imgs/Cupid " + (i + 1) + " - " + (j + 1) + ".png"));
            }
        }
    }

    // returns the image of the cupid
    // uses player number (1 or 2) to display correct cupid
    // uses a counter to loop through the 4 images when it is called
    public Image getCupidImage() throws IOException {
        counter %= 4;
        cupid = ImageIO.read(new File("imgs/Cupid " + player + " - " + (counter + 1) + ".png"));
        if (getLives() == 0) {
            cupid = ImageIO.read(new File("imgs/LCupid " + player + " - " + (counter + 1) + ".png"));
        }
        counter++;
        return cupid;
    }

    // returns an array of images of hearts
    // that represent the current lives out of total lives
    public Image[] getHeartImages() throws IOException{
        for (int i = 0; i < currentLives; i++) {
            hearts[i] = ImageIO.read(new File("imgs/Full Heart.png"));
        }
        for (int i = currentLives; i < hearts.length; i++) {
            hearts[i] = ImageIO.read(new File("imgs/Empty Heart.png"));
        }
        return hearts;
    }

    // returns [x,y] coordinates of the top left corner of a cupid's danger zone
    public int[] getDangerZone() {
        int[] coordinates = { x + (CUPID_SIZE - DANGER_ZONE_SIZE) / 2, y + (CUPID_SIZE - DANGER_ZONE_SIZE) / 2 };
        return coordinates;
    }

}
