package jmml.monitor.rdp;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Lanzada por la red de petri cuando no se cumple lo invariantes en una ejecucion del monitor
 */
public class invariantPExecption extends Exception {

    private final int[][] matrix;
    private final int[] pInvariant;
    private final int[] mark;
    private final int[] res;

    /**
     * Generado con la informacion del estado del sistema (Estado de la rdp) al momento de la excepcion.
     *
     * @param matrix     Matriz de invariantes (Ver PInvariant)
     * @param pInvariant Vector de invariantes (Ver PInvariant)
     * @param mark       Marcado del sistema al momento de la excepcion (Luego del disparo)
     * @param res        Suma del vector de invariantes (Ver PInvariant)
     * @see PInvariant
     */
    public invariantPExecption(@NotNull int[][] matrix, @NotNull int[] pInvariant,
                               @NotNull int[] mark, @NotNull int[] res) {
        super();
        this.matrix = matrix;
        this.pInvariant = pInvariant;
        this.mark = mark;
        this.res = res;
    }

    /**
     * Genera el mensaje de la excepcion de PInvariantes
     *
     * @return String imprimible y formateado con el msj detallado del estado del sistema al momento de la excepcion
     */
    @Contract(pure = true)
    @NotNull
    public String toString() {
        StringBuilder msj = new StringBuilder("[Error en el chequeo de invariantes]\n");
        msj.append("Marcado:                     ").append(this.vectorToString(this.mark)).append("\n");
        msj.append("ValorDelInvarianteTeorico:   ").append(this.vectorToString(this.pInvariant)).append("\n");
        msj.append("ValorDelInvarianteCalculado: ").append(this.vectorToString(this.res)).append("\n");
        msj.append("Matriz de invariantes: \n");
        for (int[] matrix1 : this.matrix) {
            msj.append("                             ").append(this.vectorToString(matrix1)).append("\n");
        }

        return msj.toString();
    }

    /**
     * Genera un string formateado de un vector
     *
     * @param v Vector de p Invariantes
     * @return Vector de invariantes formateado
     */
    @Contract(pure = true)
    @NotNull
    private String vectorToString(int[] v) {
        StringBuilder vtos = new StringBuilder("[");
        for (int e : v) {
            vtos.append(String.format("%4d", e));
        }
        vtos.append("]");

        return vtos.toString();
    }


}
