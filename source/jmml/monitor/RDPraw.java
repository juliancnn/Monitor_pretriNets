package jmml.monitor;
/**
 * Representa la estructura, estado y configuracion de la red de petri
 *
 * <pre>
 * Cargada desde un archivo json, donde la estructura coincide con la clase.
 * Donde el primer lugar representa la transicion o la plaza uno dependiente el caso.
 * Las matrices de incidencia estan ordenadas por filas que representan las plazas
 * y columnas que representan las transiciones.
 * Ningun valor puede ser salteado.
 *
 * Campos obligatorios del JSON:
 *  - Matriz de incidencia I
 *  - Vector de marcado
 *
 * Campos opcionales del JSON:
 *  - Matriz de Incidencia H
 *          * Los valores deben ser binarios
 *  - Matriz de Incidencia R
 *          * Los valores deben ser binarios
 *  - Vector de tokens maximos por plaza
 *          * Lo valores deben ser 0 para sin restriccion o un numero mayor a 0 para setear un maximo de tokens en esa
 *          plaza
 *
 * </pre>
 * <pre>
 *   Estructura completa del JSON:
 *
 * </pre>
 * @TODO Cambiar documentacion de matrices matrixH y matrixR
 */

public class RDPraw {
    /**
     * Informacion basica de la red de petri
     */
    protected String brief;
    /**
     * Informacion extendida de la red de petri
     */
    protected String info;
    /**
     * Matriz de doble incidencia de la red de petri
     */
    int[][] matrixI;
    /**
     * Matriz de incidencia de arcos inhibidores
     */
    int[][] matrixH;
    /**
     * Matriz de incidencia de arcos lectores
     */
    int[][] matrixR;
    /**
     * Vector de marcado de la red de petri
     */
    int[] vectorMark;
    /**
     * Vector de limite marcado de la red de petri
     */
    int[] vectorMaxMark;

}
