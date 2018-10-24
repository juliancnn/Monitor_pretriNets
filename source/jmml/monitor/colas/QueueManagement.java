package jmml.monitor.colas;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * <pre>
 * Manejador de multiples colas (Listas FIFO) de threads.
 * El manejador es creado con un numero de colas vacias fijo.
 * Cuando el thread vuelve a ready sale de la lista.
 *
 * Posee mecanismos para Agregar/Eliminar threads de una cola y consultar informacion sobre:
 *     - Tamano de las colas
 *     - Tiempo de espera del primer threads en cada cola
 * </pre>
 *
 */
public class QueueManagement {

    /***
     * Lista de colas
     */
    private List<List<ThreadNode>> colas;

    /**
     * Crea la lista de colas de Threads
     * @param numColas Numero de colas a crear
     */
    public QueueManagement(int numColas) throws IllegalArgumentException {
        super();
        if (numColas < 1)
            throw new IllegalArgumentException("No se pueden crear colas vacias");

        this.colas = new ArrayList<>();
        for (int i = 0; i < numColas; i++)
            //noinspection Convert2Diamond
            this.colas.add(new ArrayList<ThreadNode>());
    }

    /**
     * Retorna un vector booleano seteado en true en la cola que se hay threads esperando
     *
     * @return Vector booleano<br>
     * True: Hay threads en la cola<br>
     * False: Si la cola esta vacia
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
     *
     * @param nCola Numero de cola a anadirse
     * @throws IndexOutOfBoundsException Cuando se quiere anadir a una cola que no existe
     */
    public void addMe(int nCola) throws QueueInterrupException{
        if (nCola < 1 || nCola > colas.size())
            throw new java.lang.IndexOutOfBoundsException("La cola a la que se quiere anadir no existe");

        --nCola; // las colas coinsiden con las tranciones arrancan en 1.
        ThreadNode tn = new ThreadNode();
        try {
            /* Mete el thread al final de la lista */
            this.colas.get(nCola).add(tn);
            tn.waitNode();
        } catch (java.lang.InterruptedException e) {
            /* Si alguien lo interrumpe lo saco de la cola lanzar excepcion a capturar fuera del monitor */
            this.colas.get(nCola).remove(tn);
            throw new QueueInterrupException("Hilo despertado mientras estaba dentro de una cola de espera " +
                    "Fue expulsado de la cola y piere su estado dentro de la misma.");
        }

    }

    /**
     * Despierta a un thread al tope de la cola FIFO que estaba en waiting status
     *
     * @param nCola numero de cola del thread a despertar
     * @throws IndexOutOfBoundsException Se quiere quitar a una cola que no existe
     * @throws NoSuchElementException    La cola de Threads esta vacia
     * @TODO resolver que pasa si matan el thread que estaba encolado
     */
    public void wakeUpTo(int nCola) {
        if (nCola < 1 || nCola > colas.size())
            throw new java.lang.IndexOutOfBoundsException("La cola a la que se quiere anadir no existe");

        --nCola; // las colas coinsiden con las tranciones arrancan en 1.
        if (this.colas.get(nCola).isEmpty())
            throw new NoSuchElementException("La cola de threads esta vacia");

        /* que pasa si el proceso lo mataron por X causa. */
        ThreadNode tn = this.colas.get(nCola).get(0);
        this.colas.get(nCola).remove(tn);
        tn.notifyNode();

    }
    /*==================================================================================================================

                                  GETERS OF DYNAMIC INFORMATION AND PROPERTIES

                            Devuelven informacion dinamica del estado  de las colas
     =================================================================================================================*/

    /**
     * Retorna un vector de numeros no negativos con el tamano de cada cola
     *
     * @return Vector ordenado por colas, el primer elementos es la primera cola, y el valor representa la cantidad
     * de elementos que posee la misma.
     */
    @Contract(pure = true)
    @NotNull
    public int[] sizeOfQueues() {
        int[] sizes = new int[this.colas.size()];
        for (int i = 0; i < this.colas.size(); i++) {
            sizes[i] = this.colas.get(i).size();
        }
        return sizes;

    }

    /**
     * Retorna un vector de numeros no negativos con el tiempo relativo de cuanto tiempo estan en la cola
     * @return Tiempo del elemento en cola del primer elemento en cada cola, o 0 si la cola esta vacia.
     */
    @Contract(pure = true)
    @NotNull
    public int[] timeWaitFIOfQueues() {
        int[] sizes = new int[this.colas.size()];
        long actualTime = java.lang.System.currentTimeMillis();
        List<ThreadNode> cola;
        for (int i = 0; i < this.colas.size(); i++) {
            // Aguanta 68 años (2^31-1 seg) de software corriendo ininterrumpirdamente este casteo
            cola = this.colas.get(i);
            sizes[i] = cola.isEmpty() ? 0 : (int) (actualTime - cola.get(0).getTimeStamp());
        }
        return sizes;

    }

    /*==================================================================================================================

                                   GETERS OF STATIC INFORMATION AND PROPERTIES

                        Devuelven informacion de estado y propiedades estaticas de las colas
     =================================================================================================================*/

    /**
     * Cantidad de colas que administra el manejador, sin importar si estan vacias o no.
     *
     * @return cantidad de colas que posee el manejador
     */
    @Contract(pure = true)
    public int size() {
        return this.colas.size();
    }


    /**
     * Informacion del thread que guarda en la cola
     */
    @SuppressWarnings("unused")
    final class ThreadNode {
        final private Thread t;
        final long timeStamp;
        final private Object lockObj;

        /**
         * Crea nodo de informacion del thread
         */
        ThreadNode() {
            super();
            this.t = Thread.currentThread();
            this.timeStamp = java.lang.System.currentTimeMillis();
            this.lockObj = new Object();
        }

        /**
         * Retorna el thread que creo el nodo
         *
         * @return Thread creador del nodo
         */
        @Contract(pure = true)
        @NotNull
        Thread getT() {
            return t;
        }

        /**
         * Pone al thread en wait state con un objeto propio del nodo
         *
         * @throws InterruptedException Producida por el wait al thread
         */
        @Contract(pure = true)
        void waitNode() throws InterruptedException {
            synchronized (this.lockObj) {
                lockObj.wait();
            }

        }

        /**
         * Levanta al thread de wait state a ready con un objeto propio del nodo
         */
        @Contract(pure = true)
        synchronized void notifyNode() {
            synchronized (this.lockObj) {
                lockObj.notify();
            }
        }

        /**
         * Obtiene el tiempo de creacion del nodo
         *
         * @return TimeStamp de la creacion del nodo en ms formato unix
         */
        @Contract(pure = true)
        long getTimeStamp() {
            return timeStamp;
        }

    }


}
