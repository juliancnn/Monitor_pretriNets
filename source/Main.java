import jmml.monitor.parser.DataParser;
import jmml.monitor.rdp.RDPraw;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) {

        try{
            DataParser dp = new DataParser("examples_rdp/ej1_extended_Temporal.json");
            RDPraw rdpRaw = dp.generate(RDPraw.class);
            System.out.print(String.valueOf(rdpRaw.toString()));
            RDPraw rdpRaw2 = dp.generate(RDPraw.class);
            System.out.print(String.valueOf(rdpRaw.toString()));
        }catch (FileNotFoundException e){
            System.out.println("Archeeevo no encontrado");
        }




    }
}
