import javax.swing.*;
import java.awt.*;

public class App extends JFrame{
    private static final long serialVersionUID = 1L;
    private GUI gof;
    public App() {
        /*
        MESSAGE FOR USERS/TESTERS:
            using too high values on wind result in crashing caused by lack of ceil cap value for wind and wind influence variables,
            unless you use values in range about (0, 3) for wind everything should work fine.
            In this simulator only way to enable higher wind speeds would be by making additional substeps between frames ( easy to add ).
            Wind meeting barriers on its way causes other wind vector to pump more energy into transition resulting in natural flow speeding up near walls.
            Vorticity should also be kept on lower values to prevent crashes caused by pumping more wind than there is available, also fixable by adding substeps.
        PS:
            substeps added XD (mention substep means repeating calculations a few times in every step = slows frames).
            in Solver.java => iteration() method you can find variable 'version' which can change solver algorithm to previous version of implementation by asigning 1 to it.
            NOT RECOMMENDED (more visible bugs)
        */
        Solver solver = new Solver(40, 40, 40, 36, 1, 100, 1, 2, 0, 4, 150);
        solver.addSource(6, 3, 2, 4);
        solver.addSource(10,10,1,4);



        solver.addBarrier(4, 4, 1, 5, 5, 5);
        // solver.addBarrier(7, 7, 1, 7, 15, 5);
        // solver.addBarrier(15, 7, 1, 15, 18, 5);
        // solver.addBarrier(4, 4, 5, 15, 15, 5);
        solver.addBarrier(7, 7, 1, 15, 7, 5);


        solver.addSource(25, 17, 2, 1);
        // cage (should catch smoke a bit)
        // front walls
        solver.addBarrier(25, 20, 1, 27, 20, 38);
        solver.addBarrier(29, 20, 1, 31, 20, 38);
        // side walls
        solver.addBarrier(25, 20, 1, 25, 25, 38);
        solver.addBarrier(31, 20, 1, 31, 25, 38);
        // back wall
        solver.addBarrier(25, 25, 1, 31, 25, 38);



        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gof = new GUI(this, solver);
        gof.initialize(this.getContentPane());
        this.setSize(1200, 500);
        this.setVisible(true);
        this.setTitle("Smoke Simulation");
        this.setBackground(Color.LIGHT_GRAY);
    }

    public static void main(String[] args) {
        new App();
    }
}