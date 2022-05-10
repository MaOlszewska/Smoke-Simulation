public class Grid3D {
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

    // options for neighbours (if in domain)
    static int  [][] options = { 
        { 0, 0, 1 }, 
        { 0, 1, 0 }, 
        { 1, 0, 0 }, 
        { 0, 0,-1 }, 
        { 0,-1, 0 }, 
        {-1, 0, 0 }
    };

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
        this.grid = new Point [xSize][ySize][zSize];
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
}
