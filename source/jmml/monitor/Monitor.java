package jmml.monitor;

import jmml.monitor.colas.QueueInterrupException;
import jmml.monitor.colas.QueueManagement;
import jmml.monitor.colas.QueueManagementRAW;
import jmml.monitor.logger.Logger;
import jmml.monitor.logger.LoggerRaw;
import jmml.monitor.parser.DataParser;
import jmml.monitor.policies.Policy;
import jmml.monitor.policies.PolicyStaticRAW;
import jmml.monitor.policies.policyType;
import jmml.monitor.rdp.RDP;
import jmml.monitor.rdp.RDPraw;
import jmml.monitor.rdp.ShotException;
import jmml.monitor.rdp.invariantPExecption;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.util.concurrent.Semaphore;


/**
 * @TODO Hacer una descripcion linda del monitor para este head.
 * @TODO Los procedimientos se deberian cargar desde el config y hacer un enum de forma finamica (con reflexion),<br>
 * cosa de poder llamarlos y tener una descripcion desde el archivo con un nombre fijo para c/procedimiento
 */
public class Monitor {
    /**
     * Red de Petri del monitor, modelado de la planta de recursos
     */
    private RDP petri;
    /**
     * Politica del monitor para toma de deciciones al momento de entregar recursos
     */
    private Policy polyc;
    /**
     * Administrador de colas de procesos (FIFO) que estan a la espera de recursos todavia no disponibles
     */
    private QueueManagement colas;
    /**
     * Logger del monitor, se encarga de tracear el uso del mismo
     */
    private Logger log;
    /**
     * Semaforo de control
     */
    private Semaphore mutex;
    /**
     * Estado del monitor
     */
    private Status state;

    /**
     * Crea el monitor para administrar procedimientos en base a un archivo de configuracion de estructura JSON,
     * y politicas para la administracion de las prioridades del monitor
     *
     * @param jsonConfig    Archivo de configuracion con las politicas y la logica basada en redes de petri (Ver nota)
     * @param polPrimaria   Politica primaria del monitor (Ver nota)
     * @param polSecundaria Politica secundaria para desempatar en caso de conflicto en la politica primeria (Ver nota)
     * @throws FileNotFoundException                 Lanzada por la imposibilidad de leer el fichero,
     *                                               (Por permisos o inexistencia)
     * @throws jmml.monitor.rdp.ConfigException      Lanzada por una mala configuracion en el archivo de configuracion
     *                                               en la seccion que pertenece a la configuracion de la red de petri
     * @throws jmml.monitor.policies.ConfigException Lanzada por una formacion incompatible de la politica estatica
     * @throws jmml.monitor.policies.ConfigException Seleccion incompatible de politicas para el monitor
     */
    public Monitor(String jsonConfig, policyType polPrimaria, policyType polSecundaria)
            throws FileNotFoundException, jmml.monitor.rdp.ConfigException, jmml.monitor.policies.ConfigException,
            invariantPExecption {
        super();

        DataParser parser = new DataParser(jsonConfig); // >> FileNotFound

        log   = new Logger(parser.generate(LoggerRaw.class));
        petri = new RDP(parser.generate(RDPraw.class),log); // >> ConfigException
        colas = new QueueManagement(parser.generate(QueueManagementRAW.class),log);
        polyc = new Policy(colas, polPrimaria, polSecundaria, parser.generate(PolicyStaticRAW.class));//>ConfigException

        mutex = new Semaphore(1, true); // Binario tipo fifo
        state = new Status();

    }


    @SuppressWarnings("FeatureEnvy")
    public void acquireProcedure(int numberOfProcedure)
            throws InterruptedException, ShotException, QueueInterrupException, invariantPExecption {

        this.mutex.acquire(); // >> InterruptExcp, Si se lo saca de la cola del monitor antes que entre

        boolean[] whoIsThere;
        boolean autoWakeUp;
        long sleeptime;


        do {

            this.state.loopIn = petri.shotT(numberOfProcedure);

            if (this.state.loopIn) {
                colas.setNewSleep(petri.getWaitTime());
                whoIsThere = this.vectorAndVector(petri.getSensitizedArray(), colas.whoIsWaiting());
                if (this.any(whoIsThere)) {
                    colas.wakeUpTo(polyc.tellMeWho(whoIsThere));
                    return;
                } else
                    this.state.loopIn = false;

            } else {
                sleeptime = petri.getWaitTime(numberOfProcedure);
                if(sleeptime == -1){ // Cuando iba a encolarse se sensibilizo (Fk w
                    this.state.loopIn = true;
                    continue;
                }



                this.mutex.release();
                autoWakeUp = this.colas.addMe(numberOfProcedure, sleeptime);
                if (autoWakeUp)
                    this.mutex.acquire();
                this.state.loopIn = true;

            }
        } while (this.state.loopIn);



        this.mutex.release();

    }

    /**
     * @param a
     * @param b
     * @return
     * @throws ArrayIndexOutOfBoundsException
     */
    @Contract(pure = true)
    @NotNull
    private boolean[] vectorAndVector(@NotNull boolean[] a, @NotNull boolean[] b)
            throws ArrayIndexOutOfBoundsException {
        if (a.length != b.length)
            throw new ArrayIndexOutOfBoundsException("Anda a algebra de nuevo negro");

        boolean[] c = new boolean[a.length];

        for (int i = 0; i < a.length; ++i)
            c[i] = a[i] && b[i];
        return c;

    }

    /**
     * @param a
     * @return
     * @throws ArrayIndexOutOfBoundsException
     */
    @Contract(pure = true)
    private boolean any(@NotNull boolean[] a) {

        for (boolean i : a)
            if (i)
                return true;
        return false;

    }

    public void closeLog(){
        this.log.close();
    }

    public void forceUpPol(int [] forceUp){
        this.polyc.setForceUp(forceUp);
    }

    public void forceDownPol(int [] forceDown){
        this.polyc.setForceDown(forceDown);
    }

    /**
     * Estado del monitor
     */
    class Status {
        /**
         * Utilizada para saber si tiene debe ejecutar el algoritmo del monitor para los procedimientos
         */
        boolean loopIn;

        @SuppressWarnings("unused")
        Status() {
            super();
            this.loopIn = true;
        }
    }

}
