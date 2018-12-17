package jmml.monitor.policies;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.security.InvalidParameterException;

/**
 * Chequea que la politica esatica este definida correctamente
 */
class checkPol {
    /**
     * Chequea que la politica estatica este creada correctamente
     * @param psr Politca Estatica Plana
     * @param size Cantidad de colas para tomar decisiones.
     * @throws ConfigException Si la politica es invalida
     */
    checkPol(@NotNull PolicyStaticRAW psr, int size)  throws ConfigException  {
        super();
        if(size<1)
            throw new InvalidParameterException("Really nigga?");
        int[][] mat = psr.getMatrixT();

        if(invalidDimMatrix(mat,size))
            throw new ConfigException("La matriz de politicas no es cuadradada o faltan datos");
        else if(isNotBinaryMatrix(mat))
            throw new ConfigException("La matriz no es binaria y deberia");
        this.notISwap(mat);
    }

    /**
     * Cheque que la matriz tenga siemre la misma cantidad de elementos en cada filas (Que no falten o sobren datos)
     * Y que sea cuadrada
     * @param matrix   Matriz a chequear
     * @param filas    Cantidad de filas
     * @return True si es una matriz coinsistente<br>
     * False caso contrario
     */
    @Contract(pure = true)
    private boolean invalidDimMatrix(@NotNull int[][] matrix, int filas) {
        int columnas = filas;
        for (int[] row : matrix){
            filas--;
            if (columnas != row.length)
                return true;
        }

        return filas != 0;

    }

    /**
     * Chequea si la matriz es binaria
     * @param matrix Matriz a analizar
     * @return false si la matriz es binaria<br>
     *     true si la matriz es no binaria
     */
    @Contract(pure = true)
    private boolean isNotBinaryMatrix(@NotNull int[][] matrix){
        for (int[] row : matrix)
            for (int e : row)
                if(e != 0 && e != 1)
                    return true;
        return false;
    }

    /**
     * Chequea que la matriz sea identidad con columnas cambiadas de lugares
     * @param mat Matrixz a chequear
     * @throws ConfigException si de politicas es invarlida
     */
    private void notISwap(@NotNull int[][] mat) throws ConfigException{
        boolean cehcked = false;

        int[] rowSum = new int[mat.length]; // Fila que suma cada columna
        for (int i = 0; i < mat.length; ++i) {
            for (int j = 0; j < mat.length; ++j) {
                if (mat[i][j] == 1 && !cehcked) {
                    if (rowSum[j] == 1) {
                        throw new ConfigException("Mal generada la matriz de prioridad, la columna "
                                +j+1+" tiene al menos 2 unos");
                    } else {
                        cehcked = true;
                        rowSum[j]++;
                    }
                }
            }
            if (!cehcked)
                throw new ConfigException("Ausencia de 1 en la fila " + i);
            cehcked = false;
        }

    }
}
