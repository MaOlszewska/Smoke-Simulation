import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;

public class Solver extends JComponent implements MouseInputListener, ComponentListener{
    public Grid3D grid;
    public int frame;
    public int [] sizes = new int [3];
    public float initRoomTemperature;
    public String cacheName = "cache.txt";
    public float vorticity = 0.2f;
    public float windInfluence = 0.01f;
    public float sourceDensity = 5000f;
    public int frameRange = 4;
    public int substeps = 5;
    public int sourcesUntilFrame;
    int fps;
    public float [] wind = new float[3];
    private boolean ifWind = false;

    public Solver(int x, int y, int z, float temperature, int fps, int frameRange, float w1, float w2, float w3, int substeps, int killSourcesAt) {
        sizes[0] = x;
        sizes[1] = y;
        sizes[2] = z;
        this.frameRange = frameRange;
        initRoomTemperature = temperature;
        this.fps = fps;
        this.substeps = substeps;
        this.sourcesUntilFrame = killSourcesAt;
        wind[0] = w1;
        wind[1] = w2;
        wind[2] = w3;
        wind[2]++;
        frame = 0;
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
        frame++;
        // int substeps = 15; // 1 means no substeps
        int solverVersion = 2;
        for(int i = 0; i < substeps; i++) {

            if(solverVersion == 1) {
                diffuse1();
                if(ifWind) wind1();
            } else if(solverVersion == 2) {
                diffuse2();
                if(ifWind) wind2();
            } else if(solverVersion == 3) {
                // diffuse3();
                // if(ifWind) wind3();
            }
            if(frame < sourcesUntilFrame)
                refuelSources();
        }

        // System.out.println("bbbbbb");
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

    private void refuelSources() {
        for(int x = 0; x < sizes[0]; x++)
            for(int y = 0; y < sizes[1]; y++)
                for(int z = 0; z < sizes[2]; z++)
                    if(!grid.grid[x][y][z].isBarrier && grid.grid[x][y][z].isSource)
                        grid.grid[x][y][z].density = sourceDensity;
    }
    public void wind1() {
        for(int x = 0; x < sizes[0]; x++)
            for(int y = 0; y < sizes[1]; y++)
                for(int z = 0; z < sizes[2]; z++) {
                    if(grid.grid[x][y][z].isBarrier) continue;
                    ArrayList<Point> inCount = new ArrayList<>();
                    inCount.addAll(grid.grid[x][y][z].neighbours);
                    if(inCount.size() == 0) continue;

                    float sumWind = Math.abs(wind[0]) + Math.abs(wind[1]) + Math.abs(wind[2]);
                    for ( int i = 0; i < wind.length; i++){
                        if (wind[i] < 0.0f){
                            if (!grid.grid[x][y][z].isBarrier) {
                                float val = Math.min(Math.abs(windInfluence * wind[i]), grid.grid[x][y][z].oldDensity) / sumWind;
                                if (!inCount.get(2-i).isBarrier){
                                    grid.grid[x][y][z].density -= val;
                                    inCount.get(2-i).density += val;
                                }
                            }
                        }
                        else {
                            if (!grid.grid[x][y][z].isBarrier) {
                                float val = Math.min(Math.abs(windInfluence * wind[i]), grid.grid[x][y][z].oldDensity) / sumWind;
                                if (!inCount.get(5-i).isBarrier){
                                    grid.grid[x][y][z].density -= val;
                                    inCount.get(5-i).density += val;
                                }
                            }
                        }
                    }
                    if (grid.grid[x][y][z].density < 0.0f){
                        System.out.println("Problem");
                    }
                }

        for(int x = 0; x < sizes[0]; x++)
            for(int y = 0; y < sizes[1]; y++)
                for(int z = 0; z < sizes[2]; z++) {
                    grid.grid[x][y][z].oldDensity = grid.grid[x][y][z].density;
                }

    }

    private void rewriteDensities() {
        for(int x = 0; x < sizes[0]; x++)
            for(int y = 0; y < sizes[1]; y++)
                for(int z = 0; z < sizes[2]; z++)
                    grid.grid[x][y][z].oldDensity = grid.grid[x][y][z].density;
    }

    private float calcSummaryDiff(Point p) {
        float sum = 0.0f;
        for (Point nei : p.neighbours) {
            if(!nei.isBarrier) {
                sum += nei.oldDensity - p.oldDensity;
            }
        }
        return sum;
    }

    private void calcDiffRatios(float [] ratios, int notBarriers, Point p) {
        for(int i = 0; i < 6; i++) {
            if(!p.neighbours.get(i).isBarrier) {
                ratios[i] = (p.oldDensity - p.neighbours.get(i).oldDensity) / notBarriers;
            }
        }
    }

    private void updateDiffDensities(float [] ratios, Point p, float summaryDiff) {
        for(int i = 0; i < 6; i++) {
            if(!p.neighbours.get(i).isBarrier) {
                p.neighbours.get(i).density += ratios[i] * vorticity / substeps;
            }
        }
        p.density += summaryDiff * vorticity / substeps;
    }

    public void diffuse2() {
        rewriteDensities();

        for(int x = 1; x < sizes[0] - 1; x++)
            for(int y = 1; y < sizes[1] - 1; y++)
                for(int z = 1; z < sizes[2] - 1; z++) {
                    if(grid.grid[x][y][z].isBarrier) continue;
                    boolean barriersOnly = true;
                    int notBarriers = 0;
                    boolean anyNotZero = true;
                    for (Point nei : grid.grid[x][y][z].neighbours) {
                        if(!nei.isBarrier) {
                            barriersOnly = false;
                            notBarriers++;
                            if(nei.oldDensity != 0.0f)
                                anyNotZero = true;
                        }
                    }
                    if(barriersOnly || !anyNotZero && grid.grid[x][y][z].oldDensity == 0.0f) continue;
                    float [] ratios = new float[6];
                    for(int i = 0; i < 6; i++) ratios[i] = 0;

                    float summaryDiff = calcSummaryDiff(grid.grid[x][y][z]);

                    calcDiffRatios(ratios, notBarriers, grid.grid[x][y][z]);

                    updateDiffDensities(ratios, grid.grid[x][y][z], summaryDiff);
                }
    }

    private float calcWindRatiosAndSumDiff(float [] ratios, Point p, float windPower) {
        float sum = 0;
        float divider = 0; // maybe changeable (sum can't let exceed amount of density that point has)
        for(int i = 0; i < 6; i++)
            if(ratios[i] > 0)
                divider++;

        for(int i = 0; i < 6; i++) {
            if(ratios[i] == 0.0f) // Skip for ratio == 0 and barriers (included in ratio == 0)
                continue;

            if(ratios[i] < 0) { // When neighbour gives to p
                ratios[i] = ratios[i] * p.neighbours.get(i).oldDensity * windPower;
                sum -= ratios[i];
            }
            if(ratios[i] > 0) { // When neighbour takes from p

                ratios[i] = ratios[i] * p.oldDensity / divider * windPower;
                sum -= ratios[i];
            }
        }
        return sum;
    }

    private void updateWindDensities(float [] ratios, float summaryDiff, Point p) {
        for(int i = 0; i < 6; i++) {
            p.neighbours.get(i).density += ratios[i] * windInfluence / substeps;
        }
        p.density += summaryDiff * windInfluence / substeps;
    }

    public void wind2() {
        rewriteDensities();
        float windPower;
        windPower = (float) Math.sqrt(Math.pow(wind[0], 2) + Math.pow(wind[1], 2) + Math.pow(wind[2], 2));
        // windPower = (float) (Math.abs(wind[0]) + Math.abs(wind[1]) + Math.abs(wind[2]));
        for(int x = 1; x < sizes[0] - 1; x++)
            for(int y = 1; y < sizes[1] - 1; y++)
                for(int z = 1; z < sizes[2] - 1; z++) {

                    if(grid.grid[x][y][z].isBarrier) continue;
                    boolean barriersOnly = true;
                    // int notBarriers = 0;
                    boolean anyNotZero = true;
                    for (Point nei : grid.grid[x][y][z].neighbours) {
                        if(!nei.isBarrier) {
                            barriersOnly = false;
                            // notBarriers++;
                            if(nei.oldDensity != 0.0f)
                                anyNotZero = true;
                        }
                    }
                    if(barriersOnly || !anyNotZero && grid.grid[x][y][z].oldDensity == 0.0f) continue;

                    // float summaryDiff = 0.0f;
                    Point p = grid.grid[x][y][z];
                    float [] ratios = new float[6];
                    for(int i = 0; i < 6; i++) ratios[i] = 0;
                    int swaper = 1;
                    for(int w = 0; w < 3; w++) {
                        if(!p.neighbours.get(2-w).isBarrier) {
                            // summaryDiff += wind[w] * swaper;
                            ratios[2-w] = +wind[w] * swaper;
                        }
                        if(!p.neighbours.get(5-w).isBarrier) {
                            // summaryDiff -= wind[w] * swaper;
                            ratios[5-w] = -wind[w] * swaper;
                        }
                    }

                    float summaryDiff = calcWindRatiosAndSumDiff(ratios, p, windPower);
                    updateWindDensities(ratios, summaryDiff, p);
                }
    }


    // calculates current density for each nei with lower density
    public void diffuse1() {
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
                    inCount.addAll(grid.grid[x][y][z].neighbours);
                    if(inCount.size() == 0) continue;
                    float summaryDiff = 0.0f;
                    float [] ratios = new float[inCount.size()];
                    for(int i = 0; i < inCount.size(); i++) ratios[i] = 0;
                    for (Point nei : inCount) {
                        if(nei.oldDensity <= grid.grid[x][y][z].oldDensity && !grid.grid[x][y][z].isBarrier) {
                            summaryDiff += (grid.grid[x][y][z].oldDensity - nei.oldDensity);
                        }
                    }
                    if(summaryDiff == 0) continue;
                    for(int i = 0; i < inCount.size(); i++) {
                        if(inCount.get(i).oldDensity <= grid.grid[x][y][z].oldDensity && !grid.grid[x][y][z].isBarrier) {
                            ratios[i] = (grid.grid[x][y][z].oldDensity - inCount.get(i).oldDensity) / summaryDiff;
                        }
                    }

                    float sumRatios = 0.0f;
                    for (float ratio : ratios){
                        sumRatios += ratio;
                    }
                    if (sumRatios <= 0.0f) continue;
                    for (int i = 0; i < 6; i++){
                        ratios[i] = ratios[i] / sumRatios;
                    }
                    sumRatios = 0.0f;
                    for (float ratio : ratios){
                        sumRatios += ratio;
                    }
                    for (Point nei : inCount) {
                        nei.density += this.vorticity * ratios[inCount.indexOf(nei)] * grid.grid[x][y][z].oldDensity / this.fps;
                    }
                    grid.grid[x][y][z].density -= this.vorticity * grid.grid[x][y][z].oldDensity / this.fps;
                }

        for(int x = 0; x < sizes[0]; x++)
            for(int y = 0; y < sizes[1]; y++)
                for(int z = 0; z < sizes[2]; z++) {
                    grid.grid[x][y][z].oldDensity = grid.grid[x][y][z].density;
                }

    }
    @Override
    protected void paintComponent(Graphics g) {
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
    public void setWind(){
        ifWind = !ifWind;
    }
}