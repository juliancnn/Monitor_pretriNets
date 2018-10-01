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
 * Cuando un thread entra enuna cola, entonces pasa a waiting status.
 * Cuando el thread vuelve a ready sale de la lista.
 *
 * Posee mecanismos para Agregar/Eliminar threads de una cola y consultar informacion sobre:
 *     - @TODO Tamano de las pilas (Como evito overflow)
 *     - @TODO Tiempo de espera del primer threads en cada cola
 *     - @TODO Actividad por cola (Si se mueve mucho)
 *
 * </pre>
 *
 * @TODO Hacer los test negrooo, banda de test
 * @TODO <pre>Como evito que alguien externo al monitor me interrumpa el hilo con un interrupt?/ y si lo mata?
 * Esto me trae problemas, por que si bien lo saco de la cola manejando la excepcion puede disparar igual
 * y en el mejor de los casos no dispara, pero si dispara me puede o romper la coinsistencia del monitor, y dsp de disparar
 * levantaria otro hilo, sabiendo que puede haber otro adentro del monitor levantando threads, dsp me devuelve
 * y queda incoinsistente el semaforo
 * Me podria podria implementar una excepcion
 * checkeada obligatoria del monitor para que no intente aceder al recurso que solico dsp de pedirlo no? </pre>
 */
public class QueueManagement {

    /***
     * Lista de colas
     * @TODO Debe ser coinsidente con la cantidad de transicion
     */
    private List<List<ThreadNode>> colas;

    /**
     * Crea la lista de colas de Threads
     *
     * @param numColas Numero de colas a crear
     * @TODO Cambio de exepcion
     */
    public QueueManagement(int numColas) throws IllegalArgumentException {
        if (numColas < 1)
            throw new IllegalArgumentException("A ver nene si  aca implemetas la excepxion");

        /* Levantamos las colas */
        this.colas = new ArrayList<>();
        for (int i = 0; i < numColas; i++)
            this.colas.add(new ArrayList<ThreadNode>());
    }

    /**
     * Retorna un vector booleano seteado en true en la cola que se hay threads esperando
     * @return Vector booleano<br>
     *     True: Hay threads en la cola<br>
     *     False: Si la cola esta vacia
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
    public void addMe(int nCola) {
        if (nCola < 1 || nCola > colas.size())
            throw new java.lang.IndexOutOfBoundsException("La cola a la que se quiere anadir no existe");

        --nCola; // las colas coinsiden con las tranciones arrancan en 1.
        ThreadNode tn = new ThreadNode();
        try {
            /* Mete el thread al final de la lista */
            this.colas.get(nCola).add(tn);
            tn.waitNode();
        } catch (java.lang.InterruptedException e) {
            /* Si alguien lo interrumpe lo saco de la cola, deberia finalizar el thread?
             * o por lo menos sacarlo del monitor, podria lanzar excepcion y capturarla fuera del monitor */
            this.colas.get(nCola).remove(tn);
        }

    }

    /**
     * Despierta a un thread al tope de la cola FIFO que estaba en waiting status
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
        /* que pasa si el proceso lo mataron por X causa. */

        ThreadNode tn = this.colas.get(nCola).get(0);
        this.colas.get(nCola).remove(tn);
        tn.notifyNode();

    }
    /*==================================================================================================================

                                  GETERS OF DYNAMIC INFORMATION AND PROPERTIES

                            Devuelven informacion dinamica del estado  de las colas
     =================================================================================================================*/

    /*==================================================================================================================

                                   GETERS OF STATIC INFORMATION AND PROPERTIES

                        Devuelven informacion de estado y propiedades estaticas de las colas
     =================================================================================================================*/

    /**
     * Cantidad de colas que administra el manejador, sin importar si estan vacias o no.
     *
     * @return cantidad de colas que posee el manejador
     */
    public int size() {
        return this.colas.size();
    }



    /**
     * Informacion del thread que guarda en la cola
     */
    final class ThreadNode {
        final private Thread t;
        final long timeStamp;
        final private Object lockObj;

        /**
         * Crea nodo de informacion del thread
         */
        ThreadNode() {
            this.t = Thread.currentThread();
            this.timeStamp = java.lang.System.currentTimeMillis();
            this.lockObj = new Object();
        }

        /**
         * Retorna el thread que creo el nodo
         * @return Thread creador del nodo
         */
        @Contract(pure = true)
        @NotNull
        Thread getT() {
            return t;
        }

        /**
         * Pone al thread en wait state con un objeto propio del nodo
         * @throws InterruptedException Producida por el wait al thread
         */
        void waitNode() throws InterruptedException {
            synchronized (this.lockObj){
                lockObj.wait();
            }

        }

        /**
         * Levanta al thread de wait state a ready con un objeto propio del nodo
         */
        synchronized void notifyNode() {
            synchronized (this.lockObj) {
                lockObj.notify();
            }
        }

        /**
         * Obtiene el tiempo de creacion del nodo
         * @return TimeStamp de la creacion del nodo en ms formato unix
         */
        protected long getTimeStamp() {
            return timeStamp;
        }

    }


}
