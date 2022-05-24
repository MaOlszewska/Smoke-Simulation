import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
// import java.sql.Time;

import javax.swing.*;
// import javax.swing.JSlider;
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

    private JLabel labelLeft;

    private JLabel labelRight;
    private JButton forward;

    private JButton backward;
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
        labelLeft = new JLabel("View from above");
        labelRight = new JLabel("View form side");
        JPanel buttonPanelLeft = new JPanel();
        JPanel buttonPanelRight = new JPanel();
        JPanel buttonPanelDown = new JPanel();

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

        forward = new JButton("Forward");
        forward.setActionCommand("Forward");
        forward.setToolTipText("Moves forward");
        forward.addActionListener(this);

        backward = new JButton("Backward");
        backward.setActionCommand("Backward");
        backward.setToolTipText("Moves backward");
        backward.addActionListener(this);


        start.setBackground(Color.pink);
        down.setBackground(Color.PINK);
        up.setBackground(Color.PINK);
        forward.setBackground(Color.PINK);
        backward.setBackground(Color.PINK);

        start.setFont(new Font("Serif", Font.PLAIN, 14));
        down.setFont(new Font("Serif", Font.PLAIN, 14));
        up.setFont(new Font("Serif", Font.PLAIN, 14));
        forward.setFont(new Font("Serif", Font.PLAIN, 14));
        backward.setFont(new Font("Serif", Font.PLAIN, 14));


        buttonPanelLeft.add(start);
        buttonPanelLeft.add(up);
        buttonPanelLeft.add(down);

        buttonPanelDown.add(start);

        buttonPanelRight.add(forward);
        buttonPanelRight.add(backward);
        grid3d = solver.grid;
        // nGrid = new Grid3D(10, 10, 10, 20, 1000);

        JPanel labels = new JPanel();
        labels.setLayout(new GridLayout(1,2));
        labelLeft.setHorizontalAlignment(JLabel.CENTER);
        labelRight.setHorizontalAlignment(JLabel.CENTER);
        labelLeft.setFont(new Font("Serif", Font.PLAIN, 18));
        labelRight.setFont(new Font("Serif", Font.PLAIN, 18));

        labels.add(labelLeft);
        labels.add(labelRight);
        container.add(labels, BorderLayout.NORTH);
        container.add(grid3d, BorderLayout.CENTER);
        container.add(buttonPanelLeft, BorderLayout.WEST);
        container.add(buttonPanelRight, BorderLayout.EAST);
        container.add(buttonPanelDown, BorderLayout.SOUTH);
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
            } else if (command.equals("Forward")){
                grid3d.forward();
            }else if (command.equals("Backward")){
                grid3d.backward();
            }
        }
    }

    public void stateChanged(ChangeEvent e) {
        timer.setDelay(100);
	} 
}