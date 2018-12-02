package jmml.monitor.rdp;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Chequea la coinsistencia de la RDP, a travez de la matriz de invariantes de plaza y el marcado de la misma
 * */
class PInvariant {
    /**
     * Matriz de invariantes de plaza, cada fila representa un conjunto de invariantes de plaza
     * cada columnas en cada fila representa el numero de plaza
     * la matriz es de dimencion NxM Donde N es la cantidad de invariantes de plaza y M es la cantidad maxima
     * del los invariantes de plaza
     */
    private final int[][] matrixPInvariant;
    /** De dimencion N, posee el valor que debe poseer el invariante de plaza */
    private final int[] invariantP;

    /**
     * Chequeador de invariantes de plaza
     * @param matrixPInvariant Matriz de invariantes de plaza (Dimencion NxM)
     * @param invariantP Posee el valor que debe poseer el invariante de plaza (Dimencion N)
     */
    PInvariant(@Nullable int[][] matrixPInvariant,@Nullable int[] invariantP) {
        super();
        this.matrixPInvariant = matrixPInvariant;
        this.invariantP = invariantP;
    }

    /**
     * Comprueba si la marca cumple con todos los invariantes de plaza
     * @param mark del sistema
     * @throws invariantPExecption Si no cumple con los invariantes de plaza
     */
    void check(@NotNull int [] mark) throws invariantPExecption{
        // Si no hay invariantes de plaza retorno true
        if(this.matrixPInvariant == null || this.invariantP == null)
            return;

        boolean invalid = false;

        // vector de resultados de las suma de cada invariante
        int[] resInv = new int[matrixPInvariant.length]; // |matrixPInvariant| == |invariantP|

        for(int i = 0; i < matrixPInvariant.length ; i++){
            resInv[i] = vecMul(matrixPInvariant[i],mark);
            if(resInv[i]!=this.invariantP[i]) invalid=true;
        }

        if(invalid) throw new invariantPExecption(this.matrixPInvariant, this.invariantP, mark, resInv);

    }


    /**
     * Producto interno entre 2 vectores
     *
     * @param v1 Vector tamano n
     * @param v2 Vector de tamano n
     * @return escalar, <code>null</code> en caso de tamanos incompatibles
     * @throws ArithmeticException Vectores de distinta dimencion
     */
    @SuppressWarnings("Duplicates")
    @Contract(pure = true)
    private int vecMul(@NotNull int[] v1, @NotNull int[] v2) throws ArithmeticException {
        // Chequeo tamanos compatibles
        if (v1.length != v2.length)
            throw new ArithmeticException("Vectores de distinta longitud");

        // Vector resultado inicializado en 0
        int result = 0;

        // Opero por filas en la matriz
        for (int i = 0; i < v1.length; i++) {
            result += v1[i] * v2[i];
        }

        return result;
    }


}
