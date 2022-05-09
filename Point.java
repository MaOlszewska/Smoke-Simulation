import java.util.ArrayList;

public class Point {
    // ===================================================
    // VARIABLES
    // ===================================================
    float 
        // Velocities
        xVel,
        yVel,
        zVel,

        // Previous frame velocities
        oldXVel,
        oldYVel,
        oldZVel,

        // Current and previous densities
        density, 
        oldDensity,

        // temperature
        temperature;
    
    ArrayList<Point> neighbours = new  ArrayList<>();

    // ===================================================
    // INIT METHODS
    // ===================================================
    public Point(float temperature) {
        this.xVel = 0;
        this.yVel = 0;
        this.zVel = 0;
        
        this.oldXVel = 0;
        this.oldYVel = 0;
        this.oldZVel = 0;
        
        this.density = 0;
        this.oldDensity = 0;
        
        this.temperature = temperature;
    }

    // ===================================================
    // FUNCTIONAL METHODS
    // ===================================================
    public void addNei(Point newNei) {
        this.neighbours.add(newNei);
    }



    // ===================================================
    // COMPUTIONAL METHODS
    // ===================================================




}