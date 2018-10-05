package jmml.monitor.rdp;

/**
 * La excepcion se produce al intentar realizar un disparo invalido en la red de petri,
 * El disparo es invalido por que el numero de transicion es menor que 1 o mayor que el numero de
 * transiciones que tiene la red. Es decir cuando la transicion es inexistente
 */
class ShotException extends Exception {

    /**
     * Marca al momento del disparo
     */
    private final int[] marca;
    /**
     * Numero de transicion que se intento disparar
     */
    private final int tDisparo;
    /**
     * Cantidad de transiciones que tiene la red de petri
     */
    private final int nTrans;

    /**
     * Se crea con la informacion del estado del sistema en el momento del intento de disparo y el disparo fallido
     *
     * @param mark  recibe el estado de la RDP al momento de la excepcion
     * @param tDisp numero de transicion a disparar
     * @param cantidadTrans Cantidad de transiciones de la RDP
     */
    ShotException(int[] mark, int tDisp, int cantidadTrans) {
        super();
        this.marca = mark;
        this.tDisparo = tDisp;
        this.nTrans = cantidadTrans;
    }

    /**
     * Obtiene la informacion de la marca al momento del disparo
     *
     * @return vector de disparo que fallo
     */
    @SuppressWarnings("unused")
    public int[] getMarca() {
        return this.marca;
    }

    /**
     * Obtiene la informacion del disparo fallido
     *
     * @return El numero de transicion fallida
     */
    @SuppressWarnings("unused")
    public int gettDisparo() {
        return this.tDisparo;
    }

    /**
     * Imprime la informacion del disparo fallido junto con el estado del sistema
     */
    @SuppressWarnings({"unused", "UseOfSystemOutOrSystemErr"})
    public void printInfo() {
        System.out.println("Disparo fallido para la transicion: " + this.tDisparo);
        System.out.println("Marca del sistema al momento del fallo:");
        for (int o : this.marca) {
            System.out.print(String.format("%5d", o));
        }
        System.out.println("\nCantidad de transiciones de la RDP: " + this.nTrans);

    }

}

