//**************************************************************************
// File: Arrow.java                       CPSC 112 Final Project
// 
// Authors: Misho Gabashvili and Reese Neal
// NetID: mg2736, rjn29
//
// Class: Arrow
//
// Time for this program: ~5 hours
//
// Description  :  Arrow Object
//  Each arrow is constructed with an initial position, velocity, and angle
//  (the latter two are determined by the user using sliders). This class
//  also includes a way to determine where the arrow's tip is located 
//  mid-flight. Also, it determines the angle mid flight, from which it
//  decides how to display the arrow, by parsing the image file names that
//  refer to degree angle values (with 15 degree iterations). Furthermore, 
//  the arrow class presents three boolean methods to determine if the arrow
//  tip (hitPoint) hits the cupid, obstacle, or edge, which will be important
//  for the animation in the main CupidBattle.java file. 
// 
//**************************************************************************

import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import java.io.IOException;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map.Entry;

public class Arrow {

     //instance variables needed for this code alone
     public static String [] ArrowFileNames;
     public static HashMap<BufferedImage, Double> Arrows;
     
     //initial position, velocity, angle
     public int x; 
     public int y;
     public double velocity;
     public double angle;

     // final variables
     public final int ARROWWIDTH = 27;
     public final int ARROWHEIGHT = 11;
     public final int IMAGESIZE = 100;
     public final int DANGERRANGE = 35;
     public final int COLUMNIMAGEWIDTH = 75;

     // the actual obstacle width
     public final int COLUMNWIDTH = 50;

     static final int HEIGHT = 855;
     static final int WIDTH = 1435;
    // static final int HEIGHT = 700;
    // static final int WIDTH = 850;

    public Arrow (int arrowX, int arrowY, double arrowVelocity, double arrowAngle) {
        x = arrowX;
        y = arrowY;
        velocity = arrowVelocity;
        angle = arrowAngle;
    }

    //returns x position
    public int getX() {
        return x;
    }
    //returns y position
    public int getY() {
        return y;
    }
    //sets x position
    public void setX(int newX) {
        x = newX;
        return;
    }
    //sets y position
    public void setY(int newY) {
        y = newY;
        return;
    }
    // returns velocity
    public double getVelocity() {
        return velocity;
    }
    // returns angle
    public double getAngle() {
        return angle;
    }

    // returns the x and y coordinates of the tip of the arrow
    public int[] hitPoint(int ArrowX, int ArrowY, double yVel, double xVel) {
        int [] coordinates = {(int) (ArrowX + IMAGESIZE/2 + ARROWWIDTH/2 * Math.cos(degreesToRadians(flightAngle(yVel, xVel)))), (int) (ArrowY + IMAGESIZE/2 + ARROWHEIGHT/2 * (-Math.sin(degreesToRadians(flightAngle(yVel, xVel)))))};
        return coordinates;
    }

    // checks to see if the tip of arrow hits opponent
    public boolean hitCupid (int ArrowX, int ArrowY, double yVel, double xVel, Cupid opponent) {
        int hitPointX = hitPoint(ArrowX, ArrowY, yVel, xVel)[0];
        int hitPointY = hitPoint(ArrowY, ArrowY, yVel, xVel)[1];
        int dangerZoneX = opponent.getDangerZone()[0];
        int dangerZoneY = opponent.getDangerZone()[1];
        if (hitPointX >= dangerZoneX && hitPointX <= dangerZoneX + DANGERRANGE && hitPointY >= dangerZoneY && hitPointY <= dangerZoneY + DANGERRANGE) {
            return true;
        }
        return false;
    }

    // checks to see if the tip of arrow hits obstacle
    // getX() returns midpoint of column???
    public boolean hitObstacle (int ArrowX, int ArrowY, double yVel, double xVel, Obstacle column) {
        int hitPointX = hitPoint(ArrowX, ArrowY, yVel, xVel)[0];
        int hitPointY = hitPoint(ArrowY, ArrowY, yVel, xVel)[1];
        if (hitPointX >= column.getX() + (COLUMNIMAGEWIDTH - COLUMNWIDTH)/2 && hitPointX <= column.getX() + (COLUMNIMAGEWIDTH - COLUMNWIDTH)/2 + COLUMNWIDTH && hitPointY >= column.getY()) {
            return true;
        }
        return false;
    }

    // checks to see if the tip of arrow hits edge of screen
    public boolean hitEdge (int ArrowX, int ArrowY, double yVel, double xVel) {
        int hitPointX = hitPoint(ArrowX, ArrowY, yVel, xVel)[0];
        int hitPointY = hitPoint(ArrowY, ArrowY, yVel, xVel)[1];
        if (hitPointX <= 0 || hitPointX >= WIDTH || hitPointY >= HEIGHT){
           return true;
        }
        return false;
    }

    // computes the angle of arrow mid-flight
    // based on its initial angle and its current position on the screen 
    public double flightAngle(double yVel, double xVel) {
        
        double flightAngle = 0;

        if (x < WIDTH/2) {
            if ((Math.abs(angle) >= 0 && Math.abs(angle) <= 90)) {
                flightAngle = radiansToDegrees(Math.atan(-yVel/xVel));
            } 
            if ((Math.abs(angle) > 90 && Math.abs(angle) <= 180)) {
                flightAngle = radiansToDegrees(Math.atan(-yVel/xVel) + Math.PI);
            }
            if ((Math.abs(angle) < 0) && (Math.abs(angle) >= -90)) {
                flightAngle = radiansToDegrees(Math.atan(-yVel/xVel));
            }
            if ((Math.abs(angle) < - 90) && (Math.abs(angle) >= -180)) {
                flightAngle = radiansToDegrees(Math.atan(yVel/xVel) + Math.PI );
            }
        }
       
        if (x > WIDTH/2) { 
            if ((Math.abs(angle) >= 0 && Math.abs(angle) <= 90)) {
                flightAngle = - radiansToDegrees(Math.atan(-yVel/xVel) + Math.PI);
            } 
            if ((Math.abs(angle) > 90 && Math.abs(angle) <= 180)) {
                flightAngle = - radiansToDegrees(Math.atan(-yVel/xVel));
            }
            if ((Math.abs(angle) < 0) && (Math.abs(angle) >= -90)) {
                flightAngle = - radiansToDegrees(Math.atan(-yVel/xVel) + Math.PI);
            }
            if ((Math.abs(angle) < - 90) && (Math.abs(angle) >= -180)) {
                flightAngle = - radiansToDegrees(Math.atan(yVel/xVel));
            }
        }

        return flightAngle;
    }
    
    // loads arrow file names into a string array, 
    // fills a hashmap with buffered images as keys, and angles as values
    public static void loadArrows() throws IOException {

        Scanner arrowFile = new Scanner(new File ("Arrows.txt"));
        
        ArrowFileNames = new String [24];
        Arrows = new HashMap<BufferedImage, Double>();

        for (int i = 0; i < 24; i++) {
            ArrowFileNames[i] = arrowFile.nextLine();
            Arrows.put(ImageIO.read(new File("imgs/" + ArrowFileNames[i])), Double.parseDouble(ArrowFileNames[i].substring(ArrowFileNames[i].indexOf('-') + 1, (ArrowFileNames[i].indexOf('.')))));
        }
        arrowFile.close();
    }

    // uses a hashmap to determine a value closest to the flight angle and returns the image that needs to be drawn
    public BufferedImage arrowRotation(double flightAngle) throws IOException {

        Entry <BufferedImage, Double> minimum = null;

        double minDifference = Double.MAX_VALUE;
        double difference;

        for (Entry<BufferedImage, Double> entry : Arrows.entrySet()) {


            if (flightAngle >= 0) { //from 0 to 180
                difference = Math.abs(entry.getValue() - flightAngle);

                if (difference < minDifference) {
                    minDifference = difference;
                    minimum = entry;
                }
            }

            if (flightAngle < 0) { //from 180 to 360
                difference = Math.abs(entry.getValue() - (flightAngle + 360));
                if (difference < minDifference) {
                minDifference = difference;
                minimum = entry;
                }
            }
        }
        return minimum.getKey();
    }
    
    // converts radians to degrees
	public static double radiansToDegrees(double radians) {
		return radians * 180 / Math.PI;	
	}
    
    // converts degrees to radians
	public static double degreesToRadians(double degrees) {
		return degrees / 180 * Math.PI;	
	}
}