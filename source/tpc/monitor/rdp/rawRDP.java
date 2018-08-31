package tpc.monitor.rdp;

/**
 * Representa la estructura, estado y configuracion de la red de petri
 *
 * <pre>
 * Traida desde un archivo json, donde la estructura coinside con la clase.
 * Donde el primer lugar representa la transicion o la plaza uno dependiente el caso.
 * Ningun valor puede ser salteado y ademas:
 *      En la maxima cantidad de tokens: el valor 0 representa un sin limite
 *      En arcos inhibidores y lectores esta dado por una matriz de la misma disposicion que la matriz de doble
 *      incidencia pero su interpretacion cambia.
 *              El -1 representa el lector
 *              El 0 no tiene relacion
 *              Un Numero positivo representa un arco lector de ese valor
 * Por ejemplo:
 * El siguiente json posee 5 plazas y 4 transiciones, con la una marca inicial en la plaza uno de 3 tokens
 * y un maximo de 2 tokens en la plaza 2.
 *
 * </pre>
 * <pre>
 *   Estructura completa del JSON:
 *
 *    {
 *     "brief" : "Un breve descripcion de la red"
 *     "info"  : "Una descripcion mas detallada de la red",
 *     "matrixW" : [                             # La matriz de doble incidencia
 *       [-1, 0, 0, 1],
 *       [1, -1, 0, 0],
 *       [0, 1, 0, -1],
 *       [1, 0, -1, 0],
 *       [0, 0, 1, -1]
 *     ],
 *     "mark"     : [3, 0, 0, 0, 0],            # marcado inicial de la red
 *     "extendedMaxToken" : true,               # (Opcional) True si es extendida para limites de token por plaza
 *     "extMaxToken" : [0, 2, 0, 0, 0]          # (Opcional) Limite por plaza
 *   }
 * </pre>
 */

public class rawRDP {
    /**
     * Informacion basica de la red de petri
     */
    protected String brief;
    /**
     * Informacion extendida de la red de petri
     */
    protected String info;
    /**
     * Matriz de la red de petri
     */
    protected int[][] matrixW;
    /**
     * Marcador de la red de petri
     */
    protected int[] mark;

    /**
     * [Feature: Red de petri extendida]:  Indica si  hay un maximo de token por plaza
     */
    protected boolean extendedMaxToken;
    /**
     * [Feature: Red de petri extendida]:  Vector de maximo de tokens por plaza
     */
    protected int[] extMaxToken;


}
