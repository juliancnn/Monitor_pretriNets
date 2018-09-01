import tpc.monitor.rdp.RDP;

public class Main {

    public static void main(String[] args) {

        try{
            RDP test = new RDP("examples_rdp/ex1_extended_ReaderInh.json");
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
