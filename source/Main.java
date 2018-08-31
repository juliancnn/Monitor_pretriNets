import tpc.monitor.rdp.ConfigException;
import tpc.monitor.rdp.RDP;
import tpc.monitor.rdp.ShotException;

public class Main {

    public static void main(String[] args) {




        RDP rdp ;
        try{
            rdp = new RDP("examples_rdp/ex1_rdp", "examples_rdp/ex1_extend_maxTokens");

            rdp.printMark();

            try {
                rdp.shotT(1, false);
                rdp.shotT(2, false);
                rdp.shotT(3, false);
                //rdp.shotT(0, false);
            }catch (ShotException e){
                e.printInfo();
            }

            System.out.println("========================");
            rdp.printMark();
            System.out.println("========================");

        }catch (ConfigException e){
            e.printInfo();

        }



    }
}
