import java.util.ArrayList;

// import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
// import java.awt.Insets;
// import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
// import javax.swing.event.MouseInputListener;
import javax.swing.event.MouseInputListener;

public class Solver extends JComponent implements MouseInputListener, ComponentListener{
    public Grid3D grid;
    public int frame;
    public int [] sizes = new int [3];
    public float initRoomTemperature;
    public String cacheName = "cache.txt";
    public float vorticity = 0.1f;
    public float sourceDensity = 10000f;
    public int frameRange = 4;
    int fps;
    public Solver(int x, int y, int z, float temperature, int fps, int frameRange) {
        sizes[0] = x;
        sizes[1] = y;
        sizes[2] = z;
        this.frameRange = frameRange;
        initRoomTemperature = temperature;
        this.fps = fps;
        reset();
    }

    public void reset() {
        this.grid = new Grid3D(sizes[0], sizes[1], sizes[2], initRoomTemperature, sourceDensity);
        this.frame = 0;
    }

    public void clearCache() {
        
    }

    public void solveSimulation() {
        for( ; frame < frameRange; frame++) {
            iteration();
            // place to save or display function to be able to see results
            System.out.println(grid.grid[1][2][2].density + ", " + grid.grid[10][10][10].density + ", " + grid.grid[1][1][1].density);
        }
    }

    public void iteration() {
        diffuse();
        System.out.println("bbbbbb");
        grid.repaint();
    }

    // Adds a spherical source of smoke to the grid
    public void addSource(int xs, int ys, int zs, int r) {
        if(r < 1) return;
        if(r == 1) {
            grid.grid[xs][ys][zs].setSource(this.sourceDensity);
            return;
        }
        for(int x = 1; x < sizes[0]-1; x++)
            for(int y = 1; y < sizes[1]-1; y++)
                for(int z = 1; z < sizes[2]-1; z++) {
                    int dist = (int)(
                        Math.sqrt(
                            Math.pow(x - xs, 2) +
                            Math.pow(y - ys, 2) +
                            Math.pow(z - zs, 2)
                        )
                    );
                    if(dist < r) grid.grid[x][y][z].setSource(this.sourceDensity);
                }
    }


    public void addBarrier(int x1, int y1, int z1, int x2, int y2, int z2) {
        // values should be x1 < x2, y1 < y2, z1 < z2 and in domain minus barriers
        // if incorrect values given return
        if( 
            x1 > x2             || 
            y1 > y2             || 
            z1 > z2             ||
            x1 < 1              ||
            y1 < 1              ||
            z1 < 1              ||
            x2 >= sizes[0]-1    ||
            y2 >= sizes[1]-1    ||
            z2 >= sizes[2]-1
        ) return;

        for(int x = x1; x <= x2; x++)
            for(int y = y1; y <= y2; y++)
                for(int z = z1; z <= z2; z++)
                    grid.grid[x][y][z].setBarrier();
    }

    // Calculates density for next frame and sets it for each Point in Grid3D
    public void bouyancy() {
        final float a = 0.000625f;
        final float b = 0.025f;
        float sumDensities = 0;
        int countBarriers = 0;
        for(int x = 0; x < sizes[0]; x++)
            for(int y = 0; y < sizes[1]; y++)
                for(int z = 0; z < sizes[2]; z++)
                    if(!grid.grid[x][y][z].isBarrier) 
                        sumDensities += grid.grid[x][y][z].density;
                    else
                        countBarriers++;
        float avgDensity = sumDensities / (sizes[0] * sizes[1] * sizes[2] - countBarriers);

        for(int x = 0; x < sizes[0]; x++)
            for(int y = 0; y < sizes[1]; y++)
                for(int z = 0; z < sizes[2]; z++) {
                    if(grid.grid[x][y][z].isBarrier) continue;
                    float currDensity = grid.grid[x][y][z].density;
                    grid.grid[x][y][z].newBouyancy(a * currDensity - b * (currDensity - avgDensity));
                }
    }
    // calculates current density for each nei with lower density
    public void diffuse() {
        // copies current density of each point to oldDensity
        for(int x = 0; x < sizes[0]; x++)
            for(int y = 0; y < sizes[1]; y++)
                for(int z = 0; z < sizes[2]; z++) {
                    grid.grid[x][y][z].oldDensity = grid.grid[x][y][z].density;
                    // System.out.print(grid.grid[x][y][z].density);
                }
        /*
        For each point takes all neighbours with same or lower:
        - calculates ratio based on density difference between point and his neighbour
        - adds density amount = ratio * point.debsity * vorticity
        After that subtracts summary amount of density from the point to keep Summary density of domain static(don't care about sources for now)
        */
        for(int x = 0; x < sizes[0]; x++)
            for(int y = 0; y < sizes[1]; y++)
                for(int z = 0; z < sizes[2]; z++) {
                    if(grid.grid[x][y][z].isBarrier) continue;
                    ArrayList<Point> inCount = new ArrayList<>();
                    for (Point nei : grid.grid[x][y][z].neighbours) {
                        if(nei.oldDensity <= grid.grid[x][y][z].oldDensity && !grid.grid[x][y][z].isBarrier) inCount.add(nei);
                    }
                    if(inCount.size() == 0) continue;
                    float summaryDiff = 0.0f;
                    float [] ratios = new float[inCount.size()];
                    for(int i = 0; i < inCount.size(); i++) ratios[i] = 0;
                    for (Point nei : inCount) {
                        summaryDiff += (grid.grid[x][y][z].oldDensity - nei.oldDensity);
                    }
                    if(summaryDiff == 0) continue;
                    for(int i = 0; i < inCount.size(); i++) {
                        ratios[i] = (grid.grid[x][y][z].oldDensity - inCount.get(i).oldDensity) / summaryDiff;
                    }
                    for (Point nei : inCount) {
                        nei.density += this.vorticity * ratios[inCount.indexOf(nei)] * grid.grid[x][y][z].oldDensity / this.fps;
                    }
                    grid.grid[x][y][z].density -= this.vorticity * grid.grid[x][y][z].oldDensity / this.fps;
                }
            
    }
    @Override
    protected void paintComponent(Graphics g) {
        System.out.println("aaa");
        grid.update(g);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void componentResized(ComponentEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void componentShown(ComponentEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        // TODO Auto-generated method stub
        
    }
}