import tpc.monitor.RDP;

public class Main {

    public static void main(String[] args) {
        RDP rdp = new RDP("examples_rdp/ex1_rdp", "examples_rdp/ex1_mark");
        rdp.printRDP();
        System.out.println("========================");
        rdp.printMark();
        System.out.println("========================");
        rdp.printSensitizedVector();
        try {
            rdp.shotT(1, false);
            rdp.shotT(2, false);
            rdp.shotT(3, false);
            rdp.shotT(0, false);
        }catch (RDP.ShotException e){
            e.printInfo();
        }

        System.out.println("========================");
        rdp.printMark();
        System.out.println("========================");
        rdp.printSensitizedVector();

    }
}
