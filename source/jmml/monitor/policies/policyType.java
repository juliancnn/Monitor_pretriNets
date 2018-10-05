package jmml.monitor.policies;

/**
 * Tipos de politica aplicable para disparos de la red de petri
 * Se aplican a las colaes disponibles para disparar.
 */
public enum policyType {
    /**
     * Selecciona una cola al azar, todas tienen la mismas probabilidades
     * de ser disparadas, convierte al sistema en no determinista.
     */
    RANDOM,
    /**
     * Selecciona la cola a disparar por un order de prioridad fijo y estatico
     */
    STATICORDER,
    /**
     * Selecciona la cola mas grande de las disponibles a disparar
     */
    MAXSIZEQUEUE,
    /**
     * Selecciona la cola que hace mas tiempo esta esperando a ser disparada
     */
    FIFO,
    /**
     * Selecciona la cola que hace mas tiempo que no se dispara
     */
    LASTESTSHOT,
    /**
     * La cola temporal que ahce mas tiempo que esta sensibilizada
     */
    FISTSEN,
    /**
     * Selecciona la cola que entro en cola y fue disparada menos veces
     */
    MINORSHOT
    

}
