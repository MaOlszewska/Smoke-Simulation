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

        // Current and previous bouyancy
        bouyancy,
        oldBouyancy,

        // temperature
        temperature;

    boolean 
        isBarrier,
        isSource;
    
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
        
        this.density = 0.0f;
        this.oldDensity = 0.0f;

        this.bouyancy = 0;
        this.oldBouyancy = 0;
        
        this.temperature = temperature;

        this.isSource = false;
        this.isBarrier = false;
    }

    // ===================================================
    // FUNCTIONAL METHODS
    // ===================================================
    public void addNei(Point newNei) {
        this.neighbours.add(newNei);
    }

    public void newBouyancy(float boy) {
        this.oldBouyancy = this.bouyancy;
        this.bouyancy = boy;
    }

    public void setSource(float density) {
        this.isSource = true;
        this.isBarrier = false;
        this.density = density;
    }

    public void setBarrier() {
        this.isBarrier = true;
        this.isSource = false;
    }



    // ===================================================
    // COMPUTIONAL METHODS
    // ===================================================




}