package jmml.monitor.policies;

/**
 * La excepcion se produce por errores en la carga de datos,
 * como datos invalidos y falta de datos
 */
/* La exepcion es distinta a la de la red de petri, tiene el mismo nombre por funcionalidad */
public class ConfigException extends Exception {

    private final String moreInfo;

    /**
     * Se produce ante un mal formateo de datos en la carga de la red de petri,
     * falta de datos, matriz que no tiene cantidad de filas y columnas cnt
     * @param description Breve descripcion
     */
    public ConfigException(String description) {
        super();
        this.moreInfo = description;
    }

    /**
     * Imprime la informacion del disparo fallido junto con el estado del sistema
     * overrun
     */

    public String toString() {
        return moreInfo;
    }
}
