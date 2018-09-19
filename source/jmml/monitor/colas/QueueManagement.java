package jmml.monitor.colas;

/**
 * Manejador de todas las colas (Listas FIFO) de procesos que estan en la lista de espera del monitor
 * por un recurso todavia no disponible
 * @TODO HACER TODO, no hay nada todavia
 * @TODO Rehacer la documentacion
 */
public class QueueManagement {
    /**
     * Numero de colas que administra
     * @TODO Seguramente no se va a usar si no que vamos a usar un array de colas
     * debe ser coinsidente con la cantidad de transiciones
     */
    private int numColas;

    /**
     * Recibe como parametro el numero de colas de procesos a crear
     * @param numColas Numero de colas a crear
     * */
    public QueueManagement(int numColas) {
    }

    /**
     * <pre>
     * Retorna un vector booleano seteado en true en la cola que se hay procesos esperando
     * @return Vector booleano<br>
     *     True: Hay procesos en la cola<br>
     *     False: Si la cola esta vacia<br>
     * </pre>
     */
    public boolean[] whoIsWaing(){
        return new boolean[]{true, true};
    }

    /**
     * Anade el proceso que lo llamo a la cola t (Coinsidente con el numero de transicion)
     * @param t Numero de cola a anadirse
     */
    public void addMe(int t){
        return;
    }

    /**
     * Despierta a un proceso al tope de la cola FIFO que estaba en wait
     * Deberia retornar algo si la cola esta vacia.
     * @param t numero de cola del proceso a desperar
     */
    public void wakeUpTo(int t){
        return;
    }


}
