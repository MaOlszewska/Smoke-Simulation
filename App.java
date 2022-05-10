public class App {
    public static void main(String[] args) {
        Solver solver = new Solver(12, 12, 12, 36, 1, 10000);
        solver.addSource(1, 1, 1, 1);
        solver.solveSimulation();
    }
}
