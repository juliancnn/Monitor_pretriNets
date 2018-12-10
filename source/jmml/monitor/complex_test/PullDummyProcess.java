package jmml.monitor.complex_test;

import jmml.monitor.Monitor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


/**
 * Para hacer testeos sobre el monitor con Pulles de procesos cargados por JSON
 */
public class PullDummyProcess {
    ArrayList<PullDummyProcess_Single> arrayOfSingleProcess;

    public PullDummyProcess(@NotNull PullDummyProcessRAW pull, Monitor monitor) {
        arrayOfSingleProcess = new ArrayList<PullDummyProcess_Single>();

        for(int i=0; i< pull.pull.length;i++){
            arrayOfSingleProcess.add(new PullDummyProcess_Single(pull.pull[i],monitor));
        }
        for(int i=0; i< arrayOfSingleProcess.size();i++){
            (new Thread(arrayOfSingleProcess.get(i))).start();
        }
    }
}
