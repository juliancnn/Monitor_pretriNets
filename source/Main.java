import jmml.monitor.Monitor;
import jmml.monitor.policies.policyType;
import jmml.monitor.rdp.invariantPExecption;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) throws FileNotFoundException, jmml.monitor.rdp.ConfigException,
            jmml.monitor.policies.ConfigException, invariantPExecption {

        Monitor monitor = new Monitor("examples_rdp/ej6_UnEscritorMultiplesLectores.json",
                policyType.STATICORDER,policyType.STATICORDER);
        new jmml.monitor.complex_test.ej6(monitor);

    }
}
