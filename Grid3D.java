import java.awt.Color;
import java.awt.Graphics;
// import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ColorUIResource;


public class Grid3D extends JComponent implements MouseInputListener, ComponentListener{
    // ===================================================
    // VARIABLES
    // ===================================================

    // grid of points
    Point [][][] grid; 

    // initial temperature of the room
    float 
        initTemperature,
        sourceDensity; 

    // [x_len, y_len, z_len]
    int [] sizes;

    int level = 1;
    int wall = 10;

    // options for neighbours (if in domain)
    static int  [][] options = { 
        { 0, 0, 1 }, 
        { 0, 1, 0 }, 
        { 1, 0, 0 }, 
        { 0, 0,-1 }, 
        { 0,-1, 0 }, 
        {-1, 0, 0 }
    };

    int cellSize = 10;

    // ===================================================
    // INIT METHODS
    // ===================================================
    public Grid3D(int x, int y, int z, float initTemperature, float sourceDensity) {
        this.initTemperature = initTemperature;
        this.sourceDensity = sourceDensity;
        this.sizes = new int [3];
        this.sizes[0] = x;
        this.sizes[1] = y;
        this.sizes[2] = z;

        addMouseListener(this);
		addComponentListener(this);
		addMouseMotionListener(this);
		setBackground(Color.WHITE);
		setOpaque(true);

        initialize(x, y, z);


    }

    private boolean isInDomain(int x, int y, int z) {
        if(
            x >= 0 && 
            y >= 0 &&
            z >= 0 &&

            x < sizes[0] &&
            y < sizes[1] &&
            z < sizes[2]
             ) return true;
        else return false;
    }

    public void initialize(int xSize, int ySize, int zSize) {
        // Create empty grid
        this.grid = new Point [xSize * 2 + 5][xSize * 2 + 5][xSize * 2 + 5];
        // Create Points in grid
        for(int x = 0; x < xSize; x++)
            for(int y = 0; y < ySize; y++)
                for(int z = 0; z < zSize; z++) {
                    this.grid[x][y][z] = new Point(this.initTemperature);
                    // System.out.print(this.grid[x][y][z].density);
                    if(
                        x == 0          ||
                        y == 0          ||
                        z == 0          ||
                        x == xSize-1    ||
                        y == ySize-1    ||
                        z == zSize-1
                    ) this.grid[x][y][z].setBarrier();
                }

        // Add neighbours for each Point
        for(int x = 0; x < xSize; x++)
            for(int y = 0; y < ySize; y++)
                for(int z = 0; z < zSize; z++) 
                    for(int opt = 0; opt < Grid3D.options.length; opt++) {
                        int newX = x + Grid3D.options[opt][0];
                        int newY = y + Grid3D.options[opt][1];
                        int newZ = z + Grid3D.options[opt][2];

                        if(isInDomain(newX, newY, newZ)) this.grid[x][y][z].addNei(this.grid[newX][newY][newZ]);
                    }
    }
    
    // ===================================================
    //
    // ===================================================
    public void drawGrid(Graphics g) {

        for(int x = 0; x < sizes[0]; x++)
            for(int y = 0; y < sizes[1]; y++) {
                float dens = grid[x][y][level].density;
                if(grid[x][y][level].isBarrier) {
                    g.setColor(new Color(219,112,147, 255));
                }
                else {
                int val = Math.max(Math.min((int)(255 - dens/10), 255), 0);
                
                g.setColor(new Color(val, val, val, 255));
                }
                g.fillRect(x * cellSize, y * cellSize, cellSize - 1, cellSize - 1);
            }

        for(int x = 0; x < sizes[0]; x++)
            for(int z = 0; z < sizes[2]; z++) {
                float dens = grid[x][wall][z].density;
                if(grid[x][wall][z].isBarrier) {
                    g.setColor(new Color(219,112,147, 255));
                }
                else {
                    int val = Math.max(Math.min((int)(255 - dens/10), 255), 0);
                    g.setColor(new Color(val, val, val, 255));
                }
                g.fillRect((x + 2 + sizes[0]) * cellSize, (sizes[2] - z - 1) * cellSize, cellSize - 1, cellSize - 1);
            }
    }

    public void update(Graphics g) {
        this.drawGrid(g);
    }

    public void mouseClicked(MouseEvent e) {

	}

	public void componentResized(ComponentEvent e) {

	}

	public void mouseDragged(MouseEvent e) {

	}

    public void mouseExited(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void componentShown(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

    protected void paintComponent(Graphics g) {
		drawGrid(g);

	}

    public void up() {
        level = Math.min(sizes[2]-1, level+1);
        this.repaint();
    }

    public void down() {
        level = Math.max(0, level-1);
        this.repaint();
    }

    public void forward(){
        wall = Math.min(sizes[1] - 1, wall + 1);
        this.repaint();
    }

    public void backward(){
        wall = Math.max(0, wall - 1);
        this.repaint();
    }


}
