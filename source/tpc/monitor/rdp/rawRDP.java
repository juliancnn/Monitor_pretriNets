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
 *              El -1 representa el inhibidor
 *              El 0 no tiene relacion
 *              Un Numero positivo representa un arco lector de ese valor
 * Por ejemplo:
 * El siguiente json posee 5 plazas y 4 transiciones, con la una marca inicial en la plaza uno de 3 tokens
 * y un maximo de 2 tokens en la plaza 2.
 * Tambien posee extencion temporal donde 0 significa sin restriccion temporal
 * El tiempo debe estar en milisegundos
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
 *     "extMaxToken" : [0, 2, 0, 0, 0],         # Parametro Opcional - Limites por plaza
 *     "extReaderInh" : [                       # Parametro Opcional
 *         [0, 0, 0, 0],                        # Matriz de arcos lectores e inhibidores
 *         [0, 0, 2,-1],                        # El 2 representa que un arco lector de peso 2
 *         [0, 0, 0, 0],                        # desde la plaza 2 a la transicion 3
 *         [0, 0, 0, 0],                        # El -1 representa un arco inhibidor de la plaza 2  a la transicion 4
 *         [0, 0, 0, 0]
 *     ],
 *    "extTempWindows" : [                      # Parametro Opcional
 *     [1000, 1000,    0,    0],                # Vector de minimo tiempo antes que se pueda disparar
 *     [   0, 3000, 1000,    0]                 # Vector de maximo tiemeout para disparar
 *   ]
 *
 *   }
 * </pre>
 */

@SuppressWarnings("unused")
class rawRDP {
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
    int[][] matrixW;
    /**
     * Marcador de la red de petri
     */
    int[] mark;

    /**
     * [Feature: Red de petri extendida - Max Tokens]:  Vector de maximo de tokens por plaza
     */
    int[] extMaxToken;

    /**
     * [Feature: Red de petri extendida - Arc Readers - Inhibitors]: Matriz de arcos lectores e inhibidores
     */
    int[][] extReaderInh;

    /**
     * [Feature: Red de petri extendida - Temporal]: Vector de ventana de tiempo de disparo de transicion
     */
    long[][] extTempWindows;
    /**
     * <pre>
     * [Feature: Red de petri extendida - Temporal]: Vector de timestamp di disparo de transiciones
     * El vector es de uso interno, si se le cargan datos seran remplazados cuando se inicialize la red.
     * </pre>
     */
    long[] extTempTimeStamp;


}
