import tpc.monitor.RDP;

public class Main {

    public static void main(String[] args) {
        RDP rdp = new RDP("examples_rdp/ex1_rdp.txt", "examples_rdp/ex1_mark.txt");
        rdp.printRDP();
        rdp.printMark();
        try{
            rdp.nextMark(4);
            rdp.nextMark(3);
            rdp.nextMark(2);
            rdp.nextMark(5);
        } catch (RDP.ShotException e){
            e.printInfo();
        }

    }
}
