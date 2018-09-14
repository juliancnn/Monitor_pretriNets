package jmml.monitor;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.String;

/**
 * Manejador de la red de petri
 * <pre>
 *
 * La clase se encarga de instanciar la red de petri con todas sus caracteristicas desde una archivo JSON
 *
 * Tiene la posibilidad de:
 *  - [NO] Dispara las transiciones y alterar el estado de la red
 *  - [NO] Informar las transiciones disponibles para disparar
 *  - [NO] Informar si se puede disparar o no una transicion
 *  - [NO] Informar el marcado de la red
 *  - [NO] Informar el estado de sensibilizado de todas las condiciones (Matrix sensibilizado)
 *  - [NO] Imprimir toda la informacion disponible de la red y su estado
 *
 * Soporta caracteristicas de redes de petri extendidas como:
 *  - [NO] Maxima cantidad de tokens por plaza
 *  - [NO] Arcos inhibidores
 *  - [NO] Arcos lectores con peso 1
 *  - [NO] Arcos lectores con otro peso
 *  - [NO] Transiciones sensibilizadas temporalmente
 *
 * </pre>
 *
 * @WARNING No implementa ningun mecanismo de proteccion de recursos para hilos multiples (como semaforo),
 * debe ser implementado externamente
 */
public class RDP {
    /**
     * Contiene toda la configuracion de la red de petri y su estado.
     */
    private RDPraw raw;

    /**
     * Crea la red de petri a partir de un archivo
     * <p>
     * El constructor setea el marcador inicial, la rdp y su configuracion en base a un archivo json de la
     * misma estructura de RDPraw. (Vease RDPraw para ver la estructura completa del archivo)
     * <pre>
     *   Estructura del JSON:
     *
     *    {
     *     "brief" : "Un breve descripcion de la red"
     *     "info"  : "Una descripcion mas detallada de la red",
     *     "matrixI" : [                              # La matriz de doble incidencia
     *       [-1, 0, 0, 1],
     *       [1, -1, 0, 0],
     *       [0, 1, 0, -1],
     *       [1, 0, -1, 0],
     *       [0, 0, 1, -1]
     *     ],
     *     "vectorMark"     : [3, 0, 0, 0, 0],            # marcado inicial de la red
     *   }
     * </pre>
     *
     * @param jsonFile Ruta del archivo JSON que contiene la informacion
     * @throws FileNotFoundException Lanzado cuando no se encuentra el archivo JSON
     * @TODO Verificar que las matrices H y R sean binarias
     * @see RDPraw Ver estructura completa del JSON
     */
    public RDP(String jsonFile) throws FileNotFoundException, ConfigException {


        /* INICIO DE CARGA DE DATOS */
        Gson json = new Gson();
        JsonReader reader = new JsonReader(new FileReader(jsonFile));
        this.raw = json.fromJson(reader, RDPraw.class);
        /* FIN DE CARGA DE DATOS */

        /* =========================================================
            Chequeos de datos validos de estructura del archivo JSON.
           ========================================================= */

        /* Chequeo de estructura JSON.  */
        if (this.raw.matrixI == null || this.raw.vectorMark == null) {
            throw new ConfigException("Error en la estructura JSON", errorTypeConfig.missingDataInJASON);
        }

        /* Chequeo de longuitud de matriz constante. */
        int conlconst = -1;
        for (int i = 0; i < this.raw.matrixI.length; ++i) {
            if (conlconst == -1) {
                conlconst = this.raw.matrixI[0].length;
            } else if (conlconst != this.raw.matrixI[i].length) {
                throw new jmml.monitor.ConfigException("La matriz de insidencia no es constante",
                        jmml.monitor.errorTypeConfig.invalidFormatMatrix);
            }
        }

        /* Chequeo de loguitud del vector y elementos positivos */
        if (this.raw.matrixI.length != this.raw.vectorMark.length) {
            throw new jmml.monitor.ConfigException("La cantidad de plazas  de marcado no es correcta",
                    jmml.monitor.errorTypeConfig.invalidFormatArray);
        } else {
            for (int i = 0; i < this.raw.vectorMark.length; ++i) {
                if (this.raw.vectorMark[i] < 0) {
                    throw new jmml.monitor.ConfigException("Elemento negativo en la marca",
                            jmml.monitor.errorTypeConfig.invalidFormatArray);
                }
            }
        }


        /* Chequeo de longuitud del vector de maximo de plazas y elementos positivos */
        if (this.isExtMaxToken()) {
            if (this.raw.vectorMaxMark.length != this.raw.vectorMark.length) {
                throw new jmml.monitor.ConfigException("La cantidad de plazas no es correcta en " +
                        "los elementos de maximo por plaza", jmml.monitor.errorTypeConfig.invalidFormatArray);
            } else {
                for (int anExtMaxToken : this.raw.vectorMaxMark) {
                    if (anExtMaxToken < 0) {
                        throw new jmml.monitor.ConfigException("Elemento negativo en la marca por plaza",
                                jmml.monitor.errorTypeConfig.invalidFormatArray);
                    }
                }
            }
        }

        /* Chequeo de longuitud del vector de arcos inhibidores */
        if (this.isExtInh()) {
            if (this.raw.matrixH[0].length != this.raw.matrixI.length) {
                throw new jmml.monitor.ConfigException("La cantidad de plazas  en la matriz de arcos " +
                        "inhibidores no es correcta", jmml.monitor.errorTypeConfig.invalidFormatArray);
            } else if (this.raw.matrixH.length != this.raw.matrixI[0].length) {
                throw new jmml.monitor.ConfigException("La cantidad de transiciones  en la matriz de arcos" +
                        " inhibidores no es correcta", jmml.monitor.errorTypeConfig.invalidFormatArray);
            } else {
                /* Chequeo de longuitud de matriz constante. */
                conlconst = -1;
                for (int i = 0; i < this.raw.matrixH.length; ++i) {
                    if (conlconst == -1) {
                        conlconst = this.raw.matrixH[0].length;
                    } else if (conlconst != this.raw.matrixH[i].length) {
                        throw new jmml.monitor.ConfigException("La matriz en la matriz de arcos inhibidores " +
                                "no es constante", jmml.monitor.errorTypeConfig.invalidFormatMatrix);
                    }
                }
            }
        }

        /* Chequeo de longuitud del vector de arcos lectores */
        if (this.isExtReader()) {
            if (this.raw.matrixR[0].length != this.raw.matrixI.length) {
                throw new jmml.monitor.ConfigException("La cantidad de plazas  en la matriz de arcos " +
                        "lectores no es correcta", jmml.monitor.errorTypeConfig.invalidFormatArray);
            } else if (this.raw.matrixR.length != this.raw.matrixI[0].length) {
                throw new jmml.monitor.ConfigException("La cantidad de transiciones  en la matriz de arcos " +
                        "lectores no es correcta", jmml.monitor.errorTypeConfig.invalidFormatArray);
            } else {
                /* Chequeo de longuitud de matriz constante. */
                conlconst = -1;
                for (int i = 0; i < this.raw.matrixR.length; ++i) {
                    if (conlconst == -1) {
                        conlconst = this.raw.matrixR[0].length;
                    } else if (conlconst != this.raw.matrixR[i].length) {
                        throw new jmml.monitor.ConfigException("La matriz en la matriz de arcos lectores" +
                                "no es constante", jmml.monitor.errorTypeConfig.invalidFormatMatrix);
                    }
                }
            }
        }


    }

    /**
     * <pre>
     * Intenta realizar disparo de la red de petri, si puede dispara y altera el estado de la red
     *
     * @param tDisp Numero de transicion a disparar
     * @return True en caso de exito en el disparo de la transicion y evolucion de la red <br>
     *         False en caso de que la transicion no este sencibilidaza
     * @throws ShotException Si no existe la transicion
     * </pre>
     */
    public boolean shotT(int tDisp) throws ShotException {
        return this.shotT(tDisp, false);
    }

    /**
     * Intenta realizar disparo de la red de petri, este puede ser un disparo de prueba o puede guardar los resultados
     * y alterar el estado de la red
     *
     * <pre>
     * @param tDisp Numero de transicion a disparar
     * @param test  True si no quiere alterar el estado de la red de petri <br>
     *              False si en caso de que se pueda disparar se altere el estado de la redes
     * @return True en caso de exito en el disparo de la transicion <br>
     *         False en caso de que la transicion no este sencibilidaza
     * @throws ShotException Excepcion por inexistencia de la transicion
     * </pre>
     */
    public boolean shotT(int tDisp, boolean test) throws ShotException {
        // Si la transicion no existe lanza la excepcion
        if (tDisp > this.raw.matrixI[0].length || tDisp < 1)
            throw new ShotException(this.raw.vectorMark, tDisp, this.raw.matrixI[0].length);

        boolean validShot = true;
        int[] newMark;

        /* Verifico si el tiro es valido  por arcos inhibidores */
        if (this.isExtInh())
            validShot = (this.vecMul(this.raw.matrixH[tDisp - 1], this.genVectorQ()) == 1);

        /* Verifico si el tiro es valido  por arcos inhibidores */
        if (this.isExtReader())
            validShot = validShot && (this.vecMul(this.raw.matrixR[tDisp - 1], this.genVectorW()) == 1);

        /* Si el tiro sigue siendo valido chequeo nueva marca */
        newMark = validShot ? this.nextMark(tDisp) : null;
        validShot = newMark != null && this.valid4Mark(newMark);


        if (validShot && !test) {
            this.raw.vectorMark = newMark;
        }

        return validShot;
    }

    /**
     * <pre>
     * Analiza si la marca es valida, tomando en cuenta la posibilidad de maxima cantidad de tokens
     * @param mark Vector marca a analizar
     * @return True si la marca es valida.
     *         False si la marca no es valida
     * </pre>
     */
    private boolean valid4Mark(@NotNull int[] mark) {
        boolean valid = true;

        for (int i = 0; i < mark.length; i++) {
            // Marca invalida por falta de tokens
            if (mark[i] < 0) {
                valid = false;
                break;
            }
            // Cambio por la funcion si es extendida
            // Marca invalida por maxTokens
            if (this.isExtMaxToken()) {
                if (this.raw.vectorMaxMark[i] != 0 && mark[i] > this.raw.vectorMaxMark[i]) {
                    valid = false;
                    break;
                }
            }
        }

        return valid;
    }

    /**
     * Retorna el posible resultado del proximo marcado luego del disparo tDisp
     * <pre>
     * Retorna el posible resultado del disparo sin alterar el marcador y sin verificar su validez <br>
     * Calcula el marcado del disparo de 1 sola transicion, como: <br>
     * Nuevo mark = mark actual + RDP * Vector Disparo
     * @param tDisp numero de transicion a disparar
     * @return vectorNextMark  Proxima marca, sea alcanzable o no. Null en caso de inexistencia de transicion
     * </pre>
     */
    private int[] nextMark(int tDisp) {
        // Si la transicion no existe
        if (tDisp > raw.matrixI[0].length || tDisp < 1)
            return null;

        // Vector de disparo ()
        int[] vectorDisparo = new int[raw.matrixI[0].length];
        vectorDisparo[tDisp - 1] = 1;

        // vector Proximo marcado
        int[] vNextMark = matMulVect(this.raw.matrixI, vectorDisparo);
        if (vNextMark != null)
            for (int i = 0; i < vNextMark.length; i++)
                vNextMark[i] += this.raw.vectorMark[i];

        return vNextMark;

    }

    /**
     * <pre>
     * Genera el vector Q de plazas inhibidoras, generadas con la relacion Cero(Marcador(Plaza))
     * @return Vector, 0 si la plaza tiene Toknes
     *                 1 Si la plaza no tiene Tokens
     * </pre>
     */
    private int[] genVectorQ() {
        int[] q = new int[this.raw.vectorMark.length];
        for (int i = 0; i < q.length; i++)
            q[i] = this.raw.vectorMark[i] == 0 ? 1 : 0;
        return q;
    }

    /**
     * <pre>
     * Genera el vector W de plazas Lectoras, generadas con la relacion Uno(Marcador(Plaza))
     * @return Vector, 1 si la plaza tiene Toknes
     *                 0 Si la plaza no tiene Tokens
     * </pre>
     */
    private int[] genVectorW() {
        int[] w = new int[this.raw.vectorMark.length];
        for (int i = 0; i < w.length; i++)
            w[i] = this.raw.vectorMark[i] == 0 ? 0 : 1;
        return w;
    }

    /**
     * Multiplica la Matriz M por el vector V
     *
     * @param m Matriz de dimencion MxN
     * @param v Vector de dimencion Nx1
     * @return vector de Nx1, NULL en caso de tamanos incompatibles
     */
    private int[] matMulVect(@NotNull int[][] m, @NotNull int[] v) {
        // Chequeo tamanos compatibles
        if (v.length != m[0].length)
            return null;

        // Vector resultado inicializado en 0
        int[] result = new int[m.length];

        // Opero por filas en la matriz
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[0].length; j++) {
                result[i] += m[i][j] * v[j];
            }
        }

        return result;
    }

    /**
     * Producto interno entre 2 vectores
     *
     * @param v1 Vector tamano n
     * @param v2 Vector de tamano n
     * @return escalar, NULL en caso de tamanos incompatibles
     */
    private int vecMul(@NotNull int[] v1, @NotNull int[] v2) {
        // Chequeo tamanos compatibles
        if (v1.length != v2.length)
            throw new java.lang.ArrayIndexOutOfBoundsException("Vectores de distinta longitud");

        // Vector resultado inicializado en 0
        int result = 0;

        // Opero por filas en la matriz
        for (int i = 0; i < v1.length; i++) {
            result += v1[i] * v2[i];
        }

        return result;
    }


    /**
     * Consulta si la red de petri es extendida para maxima cantidad de tokens
     * <pre>
     * @return true:  Si estan limitadas las plazas a un numero maximo de tokens <br>
     *         false: Caso contrario
     * </pre>
     */
    public boolean isExtMaxToken() {
        return (this.raw.vectorMaxMark != null);
    }

    /**
     * Consulta si la red de petri es extendida arcos inhibidores
     * <pre>
     * @return true:  Si hay matriz de arcos inhibidores <br>
     *         false: Caso contrario
     * </pre>
     */
    public boolean isExtInh() {
        return (this.raw.matrixH != null);
    }

    /**
     * Consulta si la red de petri es extendida arcos lectores
     * <pre>
     * @return true:  Si hay matriz de arcos lectores <br>
     *         false: Caso contrario
     * </pre>
     */
    public boolean isExtReader() {
        return (this.raw.matrixR != null);
    }

    /**
     * Obtiene un array con el estado de marcado de la red de petri
     *
     * @return Una copia del array con el marcado actual del sistema
     */
    public int[] getMark() {
        return raw.vectorMark.clone();
    }

    /**
     * Obtiene la matriz de doble incidencia de la red de petri
     *
     * @return Devuelve una copia de la matriz de la red de petri
     */
    public int[][] getMatrix() {
        return this.raw.matrixI.clone();
    }

    /**
     * Obtiene un array con la informacion de maximos toquens por plaza
     * <pre>
     * @return Una copia del array con el marcado maximo por plaza. <br>
     *         Null Si no es extendida la red
     * </pre>
     */
    public int[] getExtMaxToken() {
        return this.isExtMaxToken() ? this.raw.vectorMaxMark.clone() : null;
    }

    /**
     * Obtiene una matriz con la informacion de los arcos inhibidores, con el mismo formato de JSON
     * <pre>
     * @return Una copia de la matriz con arcos inhibidores. <br>
     *         Null Si no es extendida la red
     * </pre>
     */
    public int[][] getExtInh() {
        return this.isExtInh() ? this.raw.matrixH.clone() : null;
    }

    /**
     * Obtiene una matriz con la informacion de los arcos lectores, con el mismo formato de JSON
     * <pre>
     * @return Una copia de la matriz con arcos lectores. <br>
     *         Null Si no es extendida la red
     * </pre>
     */
    public int[][] getExtReader() {
        return this.isExtReader() ? this.raw.matrixR.clone() : null;
    }


}
