package tpc.monitor.rdp;

/**
 * La excepcion se produce al intentar agregar un token a una plaza inexistente.
 */
@SuppressWarnings("unused")
class TokenException extends Exception {
    /**
     * Cantidad de tokens que se quieren agregar.
     */
    private final int Cantidad;
    /**
     * Plaza a la cual se quizo agregar el/los token.
     */
    private final int tPlaza;
    /**
     * Cantidad de plazas que tiene la red de petri
     */
    private final int nPlaza;
    /**
     * Marca al momento de agregar token
     */
    private final int[] marca;

    /**
     * Se crea con la informacion del estado del sistema en el momento del intento de disparo y el disparo fallido
     *
     * @param mark  recibe el estado de la RDP al momento de la excepcion
     * @param tPlaz numero de plaza que se quiso agregar token/s
     * @param nPla  numero de plazas existentes en la RDP
     * @param Cant  token/s que se quiso agregar
     */
    public TokenException(int[] mark, int tPlaz, int nPla, int Cant) {
        this.marca = mark;
        this.tPlaza = tPlaz;
        this.Cantidad = Cant;
        this.nPlaza = nPla;
    }

    /**
     * Obtiene la informacion de la marca al momento del agregado
     *
     * @return vector de marca al momento del fallo
     */
    public int[] gettMarca() {
        return this.marca;
    }

    /**
     * Obtiene la informacion de la plaza que no se pudo incrementar
     * su cantidad de tokens
     *
     * @return El numero de plaza fallida
     */
    public int getDisparo() {
        return this.tPlaza;
    }

    /**
     * Imprime la informacion del agregado fallido junto con el estado del sistema
     */
    public void printInfo2() {
        System.out.println("Agregado fallido en la plaza: " + this.tPlaza);
        System.out.println("Se intento agregar " + this.Cantidad + " de Token/s");
        System.out.println("Marca del sistema al momento del fallo:");
        for (int o : this.marca) {
            System.out.print(String.format("%5d", o));
        }
        System.out.println("\nCantidad de Plazas de la RDP: " + this.nPlaza);

    }

}
