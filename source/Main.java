import jmml.monitor.Monitor;
import jmml.monitor.complex_test.PullDummyProcessRAW;
import jmml.monitor.parser.DataParser;
import jmml.monitor.policies.policyType;
import jmml.monitor.rdp.invariantPExecption;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) throws FileNotFoundException, jmml.monitor.rdp.ConfigException,
            jmml.monitor.policies.ConfigException, invariantPExecption {

        String configFile = "examples_rdp/tpfinal_config.json";

        DataParser dp = new DataParser(configFile);
        PullDummyProcessRAW pull = dp.generate(PullDummyProcessRAW.class);

        Monitor monitor = new Monitor(configFile, policyType.RANDOM,policyType.STATICORDER);


        new jmml.monitor.complex_test.PullDummyProcess(pull,monitor);
        try{
            Thread.sleep(10000);
            monitor.closeLog();
            System.exit(0);
        }catch (InterruptedException e){
            e.printStackTrace();
        }



    }

    static void ej6_conTiempo() throws FileNotFoundException, jmml.monitor.rdp.ConfigException,
            jmml.monitor.policies.ConfigException, invariantPExecption{
        String configFile = "examples_rdp/ej6_UnEscritorMultiplesLectores_conTiempo.json";

        DataParser dp = new DataParser(configFile);
        PullDummyProcessRAW pull = dp.generate(PullDummyProcessRAW.class);

        Monitor monitor = new Monitor(configFile, policyType.STATICORDER,policyType.STATICORDER);


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
