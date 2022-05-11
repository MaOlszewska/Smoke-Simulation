import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
// import java.sql.Time;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
// import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Class containing GUI: board + buttons
 */
public class GUI extends JPanel implements ActionListener, ChangeListener {
    private Timer timer;
    private Grid3D grid3d;
    private JButton start;
    private JButton up;
    private JButton down;
    private JFrame frame;
    private int iterNum = 0;
    private boolean running = false;
    private Solver solver;
    public int initDelay = 100;
    public GUI(JFrame jf, Solver solver) {
        this.frame = jf;
        this.solver = solver;
        timer = new Timer(initDelay, this);
        timer.stop();
    }

    public void initialize(Container container) {
        container.setLayout(new BorderLayout());
        container.setSize(new Dimension(500, 600));

        JPanel buttonPanel = new JPanel();

        start = new JButton("Start");
        start.setActionCommand("Start");
        start.setToolTipText("Starts clock");
        start.addActionListener(this);

        up = new JButton("Up");
        up.setActionCommand("Up");
        up.setToolTipText("Moves one level up");
        up.addActionListener(this);

        down = new JButton("Down");
        down.setActionCommand("Down");
        down.setToolTipText("Moves one level down");
        down.addActionListener(this);
        

        buttonPanel.add(start);
        buttonPanel.add(up);
        buttonPanel.add(down);

        grid3d = solver.grid;
        // nGrid = new Grid3D(10, 10, 10, 20, 1000);

        container.add(grid3d, BorderLayout.CENTER);
        container.add(buttonPanel, BorderLayout.SOUTH);
    }

    public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(timer)) {
            iterNum++;
            frame.setTitle("Smoke symulation: " + Integer.toString(iterNum));
            solver.iteration();
            // grid3d.repaint();
            
        } else {
            String command = e.getActionCommand();
			if (command.equals("Start")) {
				if (!running) {
					timer.start();
					start.setText("Pause");
				} else {
                    timer.stop();
					start.setText("Start");
				}
                running = !running;
            } else if (command.equals("Up")) {
                grid3d.up();
            } else if (command.equals("Down")) {
                grid3d.down();
            }
        }
    }

    public void stateChanged(ChangeEvent e) {
        timer.setDelay(100);
	} 
}