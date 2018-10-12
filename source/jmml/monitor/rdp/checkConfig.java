package jmml.monitor.rdp;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Chequea la coinsitencia de un objeto de la red de petri
 * @TODO Sacar el checkeo de temporal y mterlo donde tenga que ir
 * @TODO Sacar el checkeo de politicas y meterlo donde tenga que ir
 */
class checkConfig {
    /**
     * Chequea la coinsistencia de un objeto RDPraw para ver si es valido en su uso
     *
     * @param raw Archivo de configuracion a chequear
     * @throws ConfigException Lanzada en un error de coinsistencia en el objeto
     */

    checkConfig(@NotNull RDPraw raw) throws ConfigException {
        super();
        int sizeT;
        int sizeP;

        /* Datos obligatorios minimos */
        if (raw.matrixI == null || raw.vectorMark == null)
            throw new ConfigException("Debe tener una matriz de incidencia y un marcado inicial",
                    errorTypeConfig.missingDataInJASON);

        sizeT = raw.matrixI[0].length;
        sizeP = raw.matrixI.length;

        /* Chequeo de longuitud de matriz constante de incidencia */
        if (this.invalidDimMatrix(raw.matrixI, sizeP, sizeT))
            throw new ConfigException("Faltan/sobran elementos en la matriz de incidencia, no es de dimenciones" +
                    "constantes", errorTypeConfig.invalidFormatMatrix);

        /* Chequeo de loguitud del vector y elementos positivos */
        if (sizeP != raw.vectorMark.length)
            throw new ConfigException("La cantidad de plazas  de marcado no es correcta",
                    errorTypeConfig.invalidFormatArray);
        else
            for (int i = 0; i < raw.vectorMark.length; ++i)
                if (raw.vectorMark[i] < 0)
                    throw new ConfigException("Elemento negativo en la marca inicial",
                            errorTypeConfig.invalidFormatArray);


        /* Chequeo de longuitud del vector de maximo de plazas y elementos positivos */
        if (raw.vectorMaxMark != null)
            if (raw.vectorMaxMark.length != sizeP)
                throw new ConfigException("La cantidad de plazas no es correcta en " +
                        "los elementos de maximo por plaza", errorTypeConfig.invalidFormatArray);
            else
                for (int anExtMaxToken : raw.vectorMaxMark)
                    if (anExtMaxToken < 0)
                        throw new ConfigException("Elemento negativo [" + anExtMaxToken + "] en la marca por plaza",
                                errorTypeConfig.invalidFormatArray);


        /* Chequeo de longuitud del vector de arcos inhibidores */
        if (raw.matrixH != null){
            if(this.invalidDimMatrix(raw.matrixH, sizeT, sizeP)) // Trans y arcos al revez
                throw new ConfigException("Faltan/sobran elementos en la matriz H, no es de dimenciones" +
                        "constantes o tiene dimenciones errones", errorTypeConfig.invalidFormatMatrix);
            if(this.isNotBinaryMatrix(raw.matrixH))
                throw new ConfigException("La matriz de arcos inhibidores debe ser binaria",
                        errorTypeConfig.invalidFormatMatrix);
        }


        /* Chequeo de longuitud del vector de arcos lectores */
        if (raw.matrixR !=null){
            if(this.invalidDimMatrix(raw.matrixR, sizeT, sizeP)) // Trans y arcos al revez
                throw new ConfigException("Faltan/sobran elementos en la matriz R, no es de dimenciones" +
                        "constantes o tiene dimenciones errones", errorTypeConfig.invalidFormatMatrix);
            if(this.isNotBinaryMatrix(raw.matrixR))
                throw new ConfigException("La matriz de arcos lectores debe ser binaria",
                        errorTypeConfig.invalidFormatMatrix);
        }



        /* Chequeo de longuitud de vector temporal, ausencia de elementos null y negativos. */
        /*
        if (this.isExtTemp()) {
            int dimension = raw.tempWindowTuple.length;
            //Se verifica las dimensiones de la tupla.
            if (dimension != 2) {
                throw new ConfigException("Tupla temporal de dimensiones incorrectas",
                        errorTypeConfig.invalidFormatMatrix);

            } else if (raw.tempWindowTuple[1].length != raw.tempWindowTuple[0].length) {
                throw new ConfigException("Tupla temporal de dimensiones incorrectas",
                        errorTypeConfig.invalidFormatMatrix);
            } else {
                //Paso final se chequea la ausencia de elementos negativos
                for (int i = 0; i < 2; ++i) {
                    for (int j = 0; j < raw.tempWindowTuple[0].length; ++j) {
                        if (raw.tempWindowTuple[i][j] < 0) {
                            throw new ConfigException("Elemento negativo dentro de la tupla",
                                    errorTypeConfig.invalidFormatMatrix);
                        }
                    }
                }
            }

        }
*/
        /*Chequeo de contancia de la matriz de politicas, equidad de transiciones respecto a la
                   matriz de incidencia y compuesta por numeros binarios.*/
        /*
        if (this.isExtPolicy()) {
            //Verifico que la matriz sea cuadrada.
            if (raw.matrixP.length != raw.matrixP[0].length) {
                throw new ConfigException("La matriz no es constante",
                        errorTypeConfig.invalidFormatMatrix);
                //Verifico que el num de transiciones sea igual al de la matriz de incidencia.
            } else if (raw.matrixP[0].length != raw.matrixI[0].length) {
                throw new ConfigException("Numero de transiciones erroneo",
                        errorTypeConfig.invalidFormatMatrix);
            } else {
                //Paso final chequeo elementos binarios y solo 1 por fila y columna.
                boolean bin = false;
                int[] element = new int[raw.matrixP[0].length];
                for (int i = 0; i < raw.matrixP.length; ++i) {
                    for (int j = 0; j < raw.matrixP[0].length; ++j) {
                        if (raw.matrixP[i][j] == 1 && !bin) {
                            if (element[j] == 1) {
                                throw new ConfigException("Errores de prioridad", errorTypeConfig.invalidFormatMatrix);
                            } else {
                                bin = true;
                                element[j]++;
                            }
                        } else if (raw.matrixP[i][j] > 1 || raw.matrixP[i][j] == 1) {
                            throw new ConfigException("Error de politicas", errorTypeConfig.invalidFormatMatrix);
                        }
                    }
                    if (!bin)
                        throw new ConfigException("Ausencia de 1 en la fila", errorTypeConfig.invalidFormatMatrix);
                    bin = false;
                }

            }
        }*/
    }

    /**
     * Cheque que la matriz tenga siemre la misma cantidad de elementos en cada filas (Que no falten o sobren datos)
     * Y que las dimenciones sean las indicadas
     *
     * @param matrix   Matriz a chequear
     * @param filas    Cantidad de filas
     * @param columnas Cantidad de columnas
     * @return True si es una matriz coinsistente<br>
     * False caso contrario
     */
    @Contract(pure = true)
    private boolean invalidDimMatrix(@NotNull int[][] matrix, int filas, int columnas) {
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
}


