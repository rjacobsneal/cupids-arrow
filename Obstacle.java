//**************************************************************************
// File: Obstacle.java                       CPSC 112 Final Project
// 
// Authors: Misho Gabashvili and Reese Neal
// NetID: mg2736, rjn29
//
// Class: Obstacle
//
// Time for this program: ~2 hours
//
// Description  :  Obstacle Object
//
// This program generates a new obstacle each round, with a random height
// and a fixed horizontal position at the center of the panel. 
//  
//**************************************************************************


import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class Obstacle {

    public static int x;
    public static int y;
    public static int height;

    public Obstacle(int obstacleX, int obstacleY) {
        //these represent the top left coordinates of the column
        x = obstacleX;
        y = obstacleY;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    public BufferedImage GetImage() throws IOException {
        return ImageIO.read(new File("imgs/Column.png"));
    }
}