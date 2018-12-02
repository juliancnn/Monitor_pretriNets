package jmml.monitor.rdp;


import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * La excepcion se produce por errores en la carga de datos,
 * como datos invalidos y falta de datos
 */
public class ConfigException extends Exception {

    private final String moreInfo;
    private final errorTypeConfig e;

    /**
     * Se produce ante un mal formateo de datos en la carga de la red de petri,
     * falta de datos, matriz que no tiene cantidad de filas y columnas cnt
     *
     * @param description Breve descripcion
     * @param e           Tipo de error
     */
    public ConfigException(String description, errorTypeConfig e) {
        super();
        this.moreInfo = description;
        this.e = e;
    }

    /**
     * Imprime la informacion del disparo fallido junto con el estado del sistema
     */
    @Contract(pure = true)
    @NotNull
    public String toString() {
        return "[ERROR " + e + "] " + moreInfo;
    }
}
