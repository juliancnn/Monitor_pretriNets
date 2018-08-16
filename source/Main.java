import tpc.monitor.RDP;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello World!" );
        RDP rdp = new RDP("examples_rdp/ex1_rdp.txt");
        rdp.printRDP();
    }
}
