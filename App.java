public class App {
    public static void main(String[] args) {
        Solver solver = new Solver(10, 10, 10, 36, 30, 250);
        solver.addSource(1, 1, 1, 1);
        solver.solveSimulation();
    }
}
