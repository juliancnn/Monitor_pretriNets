package jmml.monitor.colas;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * <pre>
 * Manejador de todas las colas (Listas FIFO) de threads que estan
 * en el monitor a la espera  disponibilidad de recursos. Si esta en la cola el threads esta en Sleep
 * ------REVISAR DE ACA PARA ABAJO
 * El manejador es creado con un numero de colas predeterminado y fijo.
 * Posee mecanismos para:<br>
 * </pre>
 *
 * @TODO Hacer los test negrooo, banda de test
 * @TODO Rehacer la documentacion
 * @see jmml.monitor.rdp.RDP
 */
public class QueueManagement {

    /***
     * Lista de colas
     * @TODO Debe ser coinsidente con la cantidad de transicion
     */
    private List<List<ThreadNode>> colas;

    /**
     * Crea la lista de colas de Threads
     * @param numColas Numero de colas a crear
     */
    public QueueManagement(int numColas) {
        if (numColas < 1)
            System.out.println("A ver nene si  aca implemetas la excepxion");

        /* Levantamos las colas */
        this.colas = new ArrayList<>();
        for (int i = 0; i < numColas; i++)
            this.colas.add(new ArrayList<ThreadNode>());
    }

    /**
     * <pre>
     * Retorna un vector booleano seteado en true en la cola que se hay threads esperando
     * @return Vector booleano<br>
     *     True: Hay threads en la cola<br>
     *     False: Si la cola esta vacia<br>
     * </pre>
     */
    @NotNull
    @Contract(pure = true)
    public boolean[] whoIsWaiting() {
        boolean[] who = new boolean[this.colas.size()];
        for (int i = 0; i < who.length; i++)
            who[i] = !this.colas.get(i).isEmpty();
        return who;
    }

    /**
     * Anade el thread que lo llamo a la cola nCola
     * @param nCola Numero de cola a anadirse
     * @throws IndexOutOfBoundsException Cuando se quiere anadir a una cola que no existe
     */
    public void addMe(int nCola) {
        if (nCola < 1 || nCola > colas.size())
            throw new java.lang.IndexOutOfBoundsException("La cola a la que se quiere anadir no existe");

        --nCola; // las colas coinsiden con las tranciones arrancan en 1.
        ThreadNode tn = new ThreadNode();
        try {
            /* Mete el thread al final de la lista */
            this.colas.get(nCola).add(tn);
            tn.getT().wait();
        } catch (java.lang.InterruptedException e) {
            /* Si alguien lo interrumpe lo saco de la cola */
            this.colas.get(nCola).remove(tn);
        }

    }

    /**
     * Despierta a un thread al tope de la cola FIFO que estaba en Sleep
     *
     * @param nCola numero de cola del thread a despertar
     * @throws IndexOutOfBoundsException Se quiere quitar a una cola que no existe
     * @throws NoSuchElementException    La cola de Threads esta vacia
     */
    public void wakeUpTo(int nCola) {
        if (nCola < 1 || nCola > colas.size())
            throw new java.lang.IndexOutOfBoundsException("La cola a la que se quiere anadir no existe");

        --nCola; // las colas coinsiden con las tranciones arrancan en 1.
        if (this.colas.get(nCola).isEmpty())
            throw new NoSuchElementException("La cola de threads esta vacia");

        /* Antes de despertarlo lo elimino por si hay un cambio de ctx
         * Igual nadie puede entrar por que no se devolvio el semaforo,
         * y no puede alterar el estado de la red hasta desencolarse y que termine el metodo,
         * por lo tanto como maximo solo puede haber 2 hilos en el monitor y solo el segundo puede volver
         * a despertar a un tercero. Por lo cual el orden de desperarlo y sacarlo de la cola es indistino
         * aunque si lo despierta y cambia de conexto, sin volver al primer hilo
         * no habria 3 hilos en el monitor? esta bien, 2 estan saliendo, pero tecnicamente no estan dentro
         * del moinitor? */

        ThreadNode tn = this.colas.get(nCola).get(0);
        this.colas.get(nCola).remove(tn);
        tn.getT().notify();

    }
    /*==================================================================================================================

                                   GETERS OF STATIC INFORMATION AND PROPERTIES

                        Devuelven informacion de estado y propiedades estaticas de las colas
     =================================================================================================================*/

    /**
     * Cantidad de colas que administra el manejador, sin importar si estan vacias o no.
     * @return cantidad de colas que posee el manejador
     */
    public int size(){
        return this.colas.size();
    }

    /**
     * Informacion del thread que guarda en la cola
     */
    class ThreadNode {
        private Thread t;
        long timeStamp;

        /**
         * Crea nodo de informacion del thread
         */
        ThreadNode() {
            this.t = Thread.currentThread();
            this.timeStamp = java.lang.System.currentTimeMillis();
        }

        @NotNull
        protected Thread getT() {
            return t;
        }

        protected long getTimeStamp() {
            return timeStamp;
        }
    }


}
