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
 *          * Filas representan transiciones
 *          * Columnas representan plazas
 *          * 1 Si hay un arco inhibidorentre entre la plaza y la transicion
 *          * 0 Si no hay relacion
 *  - Matriz de Incidencia R
 *          * Los valores deben ser binarios
 *          * Los valores deben ser binarios
 *          * Filas representan transiciones
 *          * Columnas representan plazas
 *          * 1 Si hay un arco lector entre la plaza y la transicion
 *          * 0 Si no hay relacion
 *  - Vector de tokens maximos por plaza
 *          * Lo valores deben ser 0 para sin restriccion o un numero mayor a 0 para setear un maximo de tokens en esa
 *          plaza
 *
 * </pre>
 * <pre>
 *   Ejemplo de estructura completa del JSON:
 *
 *   {
 *   "brief" : "Ejemplo 1: Proceso paralelo",                      # (Opcional) breve descripcion
 *   "info" : "Con un arco inhibidor y un arco lector",            # (Opcional) Descripcion ampliaca
 *   "matrixI" :  [
 *     [-1, 0, 0, 1],                                              # Matriz de doble incidencia
 *     [1, -1, 0, 0],                                              # Las filas representan las plazas
 *     [0, 1, 0, -1],                                              # Las columnas representan las transiciones
 *     [1, 0, -1, 0],
 *     [0, 0, 1, -1]
 *   ],
 *   "vectorMark"     : [3, 0, 0, 0, 0],                           # (Opcional) Vector de marcado inicial
 *   "vectorMaxMark" : [0, 2, 0, 0, 0],                            # (Opcional) Vector de maximos tokenes por plaza
 *   "matrixH" :  [
 *     [0, 0, 0, 0, 0],                                            # (Opcional) Matriz de incidencia - Arcos inhibidores
 *     [0, 0, 0, 0, 0],                                            # Las filas representan las transiciones
 *     [0, 0, 0, 0, 0],                                            # 1 Si hay arcos inhibidores, 0 sin relacion
 *     [0, 1, 0, 0, 0],
 * ],
 *   "matrixR" :[
 *     [0, 0, 0, 0, 0],                                            # (Opcional) Matriz de incidencia - Arcos Lectores
 *     [0, 0, 0, 0, 0],                                            # Las filas representan las transiciones
 *     [0, 1, 0, 0, 0],                                            # 1 Si hay arcos lectores, 0 sin relacion
 *     [0, 0, 0, 0, 0],
 *   ],
 *   "extTempWindows" : [                                          # (Opcional) Tupla de tiempos en transiciones
 *     [1000, 1000,    0,    0],                                   # Vector de minimo tiempo antes que se pueda disparar
 *     [   0, 3000, 1000,    0]                                    # Vector de maximo timeout para disparar
 *   ]
 *
 * }
 *
 * </pre>
 * @TODO Caundo se cambien lo de los pesos de los lectores resolver lo de la documentacion.
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
    /**
     * <pre>
     * [Feature: Red de petri extendida - Temporal]: Vector de timestamp di disparo de transiciones
     * El vector es de uso interno, si se le cargan datos seran remplazados cuando se inicialice la red.
     * </pre>
     */
    long[] extTempTimeStamp;

}
