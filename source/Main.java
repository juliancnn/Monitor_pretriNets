import tpc.monitor.RDP;

public class Main {

    public static void main(String[] args) {
        RDP rdp = new RDP("examples_rdp/ex1_rdp.txt", "examples_rdp/ex1_mark.txt");
        rdp.printRDP();
        System.out.println("========================");
        rdp.printMark();
        System.out.println("========================");
        rdp.printSensitizedVector();


    }
}
