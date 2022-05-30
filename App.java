import javax.swing.*;
import java.awt.*;

public class App extends JFrame{
    private static final long serialVersionUID = 1L;
	private GUI gof;
    public App() {
        Solver solver = new Solver(40, 40, 40, 36, 1, 100, -1, -2, 0);
        solver.addSource(6, 3, 2, 4);
        solver.addSource(10,10,1,4);
        solver.addBarrier(4, 4, 1, 5, 5, 5);
        solver.addBarrier(7, 7, 1, 7, 15, 5);
        solver.addBarrier(7, 7, 1, 15, 7, 5);
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
