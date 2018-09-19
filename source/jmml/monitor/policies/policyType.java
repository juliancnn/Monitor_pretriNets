package jmml.monitor.policies;

/**
 * Tipos de politica aplicable para disparos de la red de petri
 * Se aplican a las transiciones disponibles para disparar.
 */
public enum policyType {
    /**
     * Selecciona una transicion al azar, todas tienen la mismas probabilidades
     * de ser disparadas, convierte al sistema en no determinista.
     */
    RANDOMNODETERMINISTA,
    /**
     * Selecciona la transicion que hace mas tiempo que no se dispara
     */
    LASTESTSHOT,
    /**
     * Selecciona la transicion que hace mas tiempo esta esperando a ser disparada
     */
    FIFO,
    /**
     * Selecciona la transicion que entro en cola y fue disparada menos veces
     */
    MINORSHOT,
    /**
     * Selecciona la transicion a disparar por un order de prioridad fijo y estatico
     */
    STATICORDER

}
