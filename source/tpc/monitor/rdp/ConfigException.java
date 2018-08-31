package tpc.monitor.rdp;

/**
 * La excepcion se produce por errores en la carga de datos,
 * como datos invalidos y falta de datos
 */
public class ConfigException extends Exception {

    private String moreInfo;
    private errorTypeConfig e;

    /**
     * Se produce ante un mal formateo de datos en la carga de la red de petri,
     * falta de datos, matriz que no tiene cantidad de filas y columnas cnt
     * @param description Breve descrpcion
     * @param e Tipo de error
     */
    public ConfigException(String description, errorTypeConfig e) {
        this.moreInfo = description;
        this.e = e;
    }

    /**
     * Imprime la informacion del disparo fallido junto con el estado del sistema
     */
    public void printInfo() {
        System.out.println("[ERROR " + e + "] " + moreInfo);
    }
}
