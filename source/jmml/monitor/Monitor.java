package jmml.monitor;

import jmml.monitor.colas.QueueManagement;
import jmml.monitor.policies.Policy;
import jmml.monitor.rdp.RDP;

public class Monitor {
    /**
     * Red de Petri del monitor, modelado de la planta de recursos
     */
    private RDP petri;
    /**
     * Politica del monitor para toma de deciciones al momento de entregar recursos
     */
    private Policy politica;
    /**
     * Administrador de colas de procesos (FIFO) que estan a la espera de recursos todavia no disponibles
     */
    private QueueManagement colas;

}
