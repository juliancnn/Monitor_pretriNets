package jmml.monitor.rdp;
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
 *  - Matriz de de politicas T
 *          * Los valores deben ser binarios
 *          * La matriz es una matriz identidad de MxM con las filas cambiadas de orden, donde el orden reprensta
 *            la prioridad, esto es:
 *              - La matriz es cuadrada
 *              - Cada fila representa una transicion
 *              - En cada fila hay y solo hay un 1 (No pueden ser todos ceros y no pueden tener mas de uno)
 *              - La posicion del 1 representa el nivel de prioridad
 *              - Las tranciciones no pueden tener igual prioridad (No pueden haber 2 filas iguales)
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
 *   "tempWindowTuple" : [                                          # (Opcional) Tupla de tiempos en transiciones
 *     [1000, 1000,    0,    0],                                   # Vector de minimo tiempo antes que se pueda disparar
 *     [   0, 3000, 1000,    0]                                    # Vector de maximo timeout para disparar
 *   ]
 *
 * }
 *
 * </pre>
 * @TODO Eliminar la parte temporal
 */


@SuppressWarnings("unused")
public class RDPraw implements Cloneable{
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
     * [Temporal]: Vector de timestamp de disparo de transiciones
     * El vector es de uso interno, si se le cargan datos seran remplazados cuando se inicialice la red.
     * </pre>
     */
    long[] vectorTimestamp;
    /**
     * Tupla de inicio y final de ventana de tiempo, medida en milisegundos
     */
    long[][] tempWindowTuple;

    public RDPraw clone(){
        Object obj=null;
        try{
            obj=super.clone();
        }catch(CloneNotSupportedException ex){
            //noinspection UseOfSystemOutOrSystemErr
            System.err.print("Error grave no se puede clonar el objeto");
            System.exit(-1);
        }
        return (RDPraw)obj;
    }

}
