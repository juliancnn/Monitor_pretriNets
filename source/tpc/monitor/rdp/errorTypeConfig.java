package tpc.monitor.rdp;

/**
 * Lista de errores de configuracion al cargar la red de petri
 */
enum errorTypeConfig {
    /**
     * El marcado maximo para cada plaza debe ser mayor a cero
     */
    invalidMaxToken,
    /**
     * La cantidad la cantidad de datos es invalida o mal ordenada
     */
    invalidFormatArray,
    invalidFormatMatrix
}
