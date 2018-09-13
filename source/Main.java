import tpc.monitor.rdp.RDP;

public class Main {

    public static void main(String[] args) {

        try{
            RDP test = new RDP("examples_rdp/ej1_extended_Temporal.json");
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
