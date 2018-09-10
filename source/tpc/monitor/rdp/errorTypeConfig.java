package tpc.monitor.rdp;

/**
 * Lista de errores de configuracion al cargar la red de petri
 */
public enum errorTypeConfig {
    /**
     * La cantidad la cantidad de datos es invalida o mal ordenada
     */
    invalidFormatArray,
    invalidFormatMatrix,
    /**
     * Faltan datos escensiales en la estructura del JSON.
     */
    missingDataInJASON

}
