import jmml.monitor.Monitor;
import jmml.monitor.complex_test.PullDummyProcessRAW;
import jmml.monitor.parser.DataParser;
import jmml.monitor.policies.policyType;
import jmml.monitor.rdp.invariantPExecption;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) throws FileNotFoundException, jmml.monitor.rdp.ConfigException,
            jmml.monitor.policies.ConfigException, invariantPExecption {

        String configFile = "examples_rdp/tpfinal_config_salidaDividida.json";

        DataParser dp = new DataParser(configFile);
        PullDummyProcessRAW pull = dp.generate(PullDummyProcessRAW.class);

        Monitor monitor = new Monitor(configFile, policyType.RANDOM,policyType.STATICORDER);

        monitor.forceUpPol(new int[] {2,1,25,26}); // Punto B consigna
        monitor.forceDownPol(new int[] {6,10,14,15,22});
        // Ingreso no entra 1/2/3, Selector de piso baja por el tema de la rampa
        //monitor.forceUpPol(new int[] {2,1,16,15}); // Punto A consigna
        //monitor.forceDownPol(new int[] {6,10,14,18});

        new jmml.monitor.complex_test.PullDummyProcess(pull,monitor);
        try{
            Thread.sleep(15000);
            monitor.closeLog();
            System.exit(0);
        }catch (InterruptedException e){
            e.printStackTrace();
        }



    }



}
