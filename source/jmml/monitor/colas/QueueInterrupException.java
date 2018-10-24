package jmml.monitor.colas;

/**
 * Excepciones producida por interrupciones externas a la cola.
 * Por ejemplo un hilo en una cola esta dormido a la espera de
 * un timeout o un notify pero es interrumpido externamente
 */
public class QueueInterrupException extends Exception {
    /**
     * Lanzada en la interrucion de un hilo que se encuentra encolado.
     * @param msj Msj de advertencia que es echado del la cola
     */
    public QueueInterrupException(String msj){
        super(msj);
    }
}
