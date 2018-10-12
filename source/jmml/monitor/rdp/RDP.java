package jmml.monitor.rdp;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <pre>
 * Manejador de la red de petri
 * La clase se encarga de instanciar la red de petri con todas sus caracteristicas a partir de un RAWrdp
 *
 * Soporta caracteristicas independientes de redes de petri extendidas como:
 *  - Maxima cantidad de tokens por plaza
 *  - Arcos inhibidores
 *  - Arcos lectores con peso 1
 *  - Transiciones sensibilizadas temporalmente
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
     * Crea la red de petri a partir del RAWrdp
     *
     * @param rdpRAW Objeto de la red de petri plano
     * @throws ConfigException Lanzado cuando esta mal formado el archivo JSON
     * @see RDPraw Ver estructura completa del RAW
     */
    public RDP(RDPraw rdpRAW) throws ConfigException {
        super();
        if(rdpRAW == null)
            throw new ConfigException("No puede crearse la red con un objeto nullo",errorTypeConfig.NullObjet);

        this.raw = rdpRAW.clone();
        new checkConfig(this.raw); // Throw ConfigException

        /* Si es temporal cargo los datos delos timestamp */
        if (this.isExtTemp()) {
            //Chequeos ok - Carga vector de tiempo para transiciones sensibilizadas
            this.raw.vectorTimestamp = new long[this.raw.matrixI[0].length];
            boolean SensAux[] = getSensitizedArray(true);
            long timestamp = java.lang.System.currentTimeMillis();
            for (int i = 0; i < this.raw.vectorTimestamp.length; ++i) {
                this.raw.vectorTimestamp[i] = SensAux[i] ? timestamp : 0;
            }

        }


    }


    /**
     * Intenta realizar disparo de la red de petri, si este se puede disparar se dispara y altera el estado
     * de la red.
     *
     * @param tDisp Numero de transicion a disparar
     * @return <code>True</code> en caso de exito en el disparo de la transicion <br>
     * <code>False</code> en caso de que la transicion no este sencibilidaza
     * @throws ShotException Excepcion por inexistencia de la transicion
     */
    public boolean shotT(int tDisp) throws ShotException {
        // Si la transicion no existe lanza la excepcion
        if (tDisp > this.raw.matrixI[0].length || tDisp < 1)
            throw new ShotException(this.raw.vectorMark, tDisp, this.raw.matrixI[0].length);

        boolean validShot = true;
        int[] newMark;
        long timestamp = java.lang.System.currentTimeMillis(); // Garantiza conisistencia en vectores temporales

        /* Verifico si el tiro es valido  por arcos inhibidores > B(tDisp) = H[tdis][] x VectorQ */
        if (this.isExtInh())
            validShot = (this.vecMul(this.raw.matrixH[tDisp - 1], this.genVectorWMark(true)) == 0);

        /* Verifico si el tiro es valido  por arcos inhibidores > L(tDisp) = R[tdis][] x VectorW*/
        if (validShot && this.isExtReader())
            validShot = (this.vecMul(this.raw.matrixR[tDisp - 1], this.genVectorWMark(false)) == 0);

        /* Si el tiro sigue siendo valido chequeo las restricciones temporales */
        if (validShot && this.isExtTemp())
            validShot = this.genSensitizedTemp(timestamp)[tDisp - 1];

        /* Si el tiro sigue siendo valido chequeo nueva marca */
        newMark = validShot ? this.nextMark(tDisp) : null;
        validShot = newMark != null && this.valid4Mark(newMark);

        /* Si el tiro sigue siendo valido, altero el estado de la red */
        if (validShot) {
            if (!this.isExtTemp())
                this.raw.vectorMark = newMark;
            else {
                boolean[] oldSensitized = getSensitizedArray(true);
                this.raw.vectorMark = newMark;
                boolean[] newSensitized = getSensitizedArray(true);
                for (int i = 0; i < newSensitized.length; ++i)
                    if (!oldSensitized[i] && newSensitized[i])
                        this.raw.vectorTimestamp[i] = timestamp;
            }
        }

        return validShot;
    }

    /**
     * Analiza si la marca es valida, tomando en cuenta la posibilidad de maxima cantidad de tokens
     *
     * @param mark Vector marca a analizar
     * @return <code>True</code> si la marca es valida.<br>
     * <code>False</code> si la marca no es valida
     */
    @Contract(pure = true)
    private boolean valid4Mark(@NotNull int[] mark) {
        boolean valid = true;

        for (int i = 0; i < mark.length; i++) {
            // Marca invalida por falta de tokens
            if (mark[i] < 0) {
                valid = false;
                break;
            }
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
     * Retorna el posible resultado del proximo marcado luego del disparo tDisp <br>
     * Retorna el posible resultado del disparo sin alterar el marcador y sin verificar su validez <br>
     * Calcula el marcado del disparo de 1 sola transicion, como: <br>
     * Nuevo mark = mark actual + RDP * Vector Disparo
     *
     * @param tDisp numero de transicion a disparar
     * @return <code>boolean[]</code>  Proxima marca, sea alcanzable o no. <br>
     * <code>null</code> en caso de inexistencia de transicion
     * @throws ShotException Si la transicion no existe
     *                       </pre>
     */
    @NotNull
    @Contract(pure = true)
    private int[] nextMark(int tDisp) throws ShotException {
        // Si la transicion no existe
        if (tDisp > raw.matrixI[0].length || tDisp < 1)
            throw new ShotException(this.raw.vectorMark, tDisp, this.raw.matrixI[0].length);

        // Vector de disparo ()
        int[] vectorDisparo = new int[raw.matrixI[0].length];
        vectorDisparo[tDisp - 1] = 1;

        // vector Proximo marcado
        int[] vNextMark = matMulVect(this.raw.matrixI, vectorDisparo);
        for (int i = 0; i < vNextMark.length; i++)
            vNextMark[i] += this.raw.vectorMark[i];

        return vNextMark;

    }


    /**
     * <pre>
     * Genera vector de la misma cantidad de plazas, en ralacion a los valores de cada plaza usando las relaciones:
     * Q: UNO(Marcador(Plaza))  Pone uno si esa plaza tiene un marcado distinto de cero (para inhibidores)
     * W: CERO(Marcador(Plaza)) Pone uno si esa plaza tiene un marcado igual a cero (para lectores)
     * </pre>
     * @param Relacion True Para generar Q, relacion de unos<br>
     *                 false Para generar W, relacion de ceros
     * @return <code>int[]</code>, 1 si la plaza tiene Tokens<br>
     * <code>0</code> Si la plaza no tiene Tokens
     */
    @Contract(pure = true)
    @NotNull
    private int[] genVectorWMark(boolean Relacion) {
        /*
        Q: Los unos multiplicados con los unos de las transiciones con arcos inhibidores
        generan unos en el vector de no sensibilizado por arcos lectores
        W: Los unos multiplicados con los unos de las transiciones con arcos lectores
        generan unos en el vector de no sensibilizado por arcos lectores
        * */
        int[] q = new int[this.raw.vectorMark.length];
        for (int i = 0; i < q.length; i++)
            q[i] = Relacion ? (this.raw.vectorMark[i] == 0 ? 0 : 1) : this.raw.vectorMark[i] == 0 ? 1 : 0;
        return q;
    }


    /**
     * Multiplica la Matriz M por el vector V
     *
     * @param m Matriz de dimencion MxN
     * @param v Vector de dimencion Nx1
     * @return vector de Mx1
     * @throws ArithmeticException Matriz y vector de dimenciones incompatibles
     */
    @NotNull
    @Contract(pure = true)
    private int[] matMulVect(@NotNull int[][] m, @NotNull int[] v) throws ArithmeticException {
        if (v.length != m[0].length)
            throw new ArithmeticException("Matrix y vector de tamanos incompatibles");

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
     * @return escalar, <code>null</code> en caso de tamanos incompatibles
     * @throws ArithmeticException Vectores de distinta dimencion
     */
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
    /*==================================================================================================================

                                   GETERS OF INFORMATION AND PROPERTIES

                    Devuelven informacion de estado y propiedades dinamicos de la red
     =================================================================================================================*/

    /**
     * Obtiene un array con el estado de marcado de la red de petri
     *
     * @return Una copia del array con el marcado actual del sistema
     */
    @NotNull
    @Contract(pure = true)
    int[] getMark() {
        return raw.vectorMark.clone();
    }


    /**
     * Retorna el vector booleano de transiciones que se encuentran dentro de la ventana temporal
     *
     * @param timestamp El tiempo que se quiere chequear la vetanana, sirve para mantener atomicidad en ShotT
     * @return <code>True</code> Si la transicion esta dentro de la ventana temporal
     */
    @NotNull
    @Contract(pure = true)
    private boolean[] genSensitizedTemp(long timestamp) {

        boolean[] sensitizedT = new boolean[this.raw.matrixI[0].length];

        for (int i = 0; i < sensitizedT.length; i++) {
            sensitizedT[i] = true;
            /* Chequeo de la ventana de tiempo */
            if (this.raw.tempWindowTuple[0][i] != 0) {
                sensitizedT[i] = this.raw.tempWindowTuple[0][i] < (timestamp - this.raw.vectorTimestamp[i]);
            }
            if (sensitizedT[i] && this.raw.tempWindowTuple[1][i] != 0) {
                sensitizedT[i] = this.raw.tempWindowTuple[1][i] > (timestamp - this.raw.vectorTimestamp[i]);
            }
        }


        return sensitizedT;
    }

    /**
     * Retorna el vector de transiciones sensibilizadas, cada lugar del vector representa una transicion
     * donde el primer lugar corresponde a la primera transicion.
     *
     * <pre>
     * @return <code>True</code> si la transicion esta sensibilizada <br>
     *         False en caso contrario
     * </pre>
     */
    @NotNull
    @Contract(pure = true)
    public boolean[] getSensitizedArray() {
        return getSensitizedArray(false);
    }

    /**
     * <pre>
     * Retorna el vector de transiciones sensibilizadas, cada lugar del vector representa una transicion
     * donde el primer lugar corresponde a la primera transicion. <br>
     * Puede ignorarse la ventana de tiempo para la obtencion de la sensibilidad <br>
     * NOTA: Se utiliza para caluclar los timestamp de las redes de petri
     *
     * @param ignoreWindows <code>True</code> si se desea ignorar la parte temporal de la red para la obtencion <br>
     *                      del vector.
     *                      <code>False </code> Si se desea tomar en cuenta el timestamp y la parte temporal de la red
     * @return <code>True</code> si la transicion esta sensibilizada <br>
     *                False en caso contrario
     * </pre>
     */
    @NotNull
    @Contract(pure = true)
    private boolean[] getSensitizedArray(boolean ignoreWindows) {
        boolean[] sensitizedT = new boolean[this.raw.matrixI[0].length];

        /* Sensibilizado por tokenes y MaxTokens */
        try {
            for (int i = 0; i < sensitizedT.length; i++)
                sensitizedT[i] = this.valid4Mark(this.nextMark(i + 1));
        } catch (ShotException se) {
            //noinspection UseOfSystemOutOrSystemErr
            System.out.println("Te las mandaste esto no puede pasar nunca por como se crea el vector");
            System.exit(-1);
        }


        /* Sensibilizado por arcos inhibidores B = H*Q */
        if (this.isExtInh()) {
            int[] notSensitizedB = this.matMulVect(this.raw.matrixH, this.genVectorWMark(true));
            for (int i = 0; i < sensitizedT.length; i++)
                sensitizedT[i] &= (notSensitizedB[i] == 0); // Si es cero esta sensibilizada
        }

        /* Sensibilizado por arcos lectores L = R*W */
        if (this.isExtReader()) {
            int[] notSensitizedR = this.matMulVect(this.raw.matrixR, this.genVectorWMark(false));
            for (int i = 0; i < sensitizedT.length; i++)
                sensitizedT[i] &= (notSensitizedR[i] == 0); // Si es cero esta sensibilizada
        }

        /* Sensibilizado por tiempo */
        if (!ignoreWindows && this.isExtTemp()) {
            boolean[] sensitizedTemporal = this.genSensitizedTemp(java.lang.System.currentTimeMillis());
            for (int i = 0; i < sensitizedT.length; i++)
                sensitizedT[i] &= sensitizedTemporal[i]; // Si es uno esta sensibilizada
        }

        return sensitizedT;

    }



    /*==================================================================================================================

                                   GETERS OF STATIC INFORMATION AND PROPERTIES

                        Devuelven informacion de estado y propiedades estaticas de la red
     =================================================================================================================*/


    /**
     * Consulta si la red de petri es extendida para maxima cantidad de tokens
     * <pre>
     * @return <code>True</code>:  Si estan limitadas las plazas a un numero maximo de tokens <br>
     *         false: Caso contrario
     * </pre>
     */
    @Contract(pure = true)
    boolean isExtMaxToken() {
        return (this.raw.vectorMaxMark != null);
    }

    /**
     * Consulta si la red de petri es extendida arcos inhibidores
     * <pre>
     * @return <code>True</code>:  Si hay matriz de arcos inhibidores <br>
     *         false: Caso contrario
     * </pre>
     */
    @Contract(pure = true)
    boolean isExtInh() {
        return (this.raw.matrixH != null);
    }

    /**
     * Consulta si la red de petri es extendida arcos lectores
     * <pre>
     * @return <code>True</code>:  Si hay matriz de arcos lectores <br>
     *         false: Caso contrario
     * </pre>
     */
    @Contract(pure = true)
    private boolean isExtReader() {
        return (this.raw.matrixR != null);
    }

    /**
     * Consulta si la red de petri es temporal
     * <pre>
     * @return <code>True</code>:  Hay ventana de tiempo establecido para 1 o mas transiciones <br>
     *         false: Caso contrario
     * </pre>
     */
    @Contract(pure = true)
    private boolean isExtTemp() {
        return (this.raw.tempWindowTuple != null);
    }


    /**
     * Obtiene la matriz de doble incidencia de la red de petri
     *
     * @return Devuelve una copia de la matriz de la red de petri
     */
    @NotNull
    @Contract(pure = true)
    int[][] getMatrix() {
        return this.raw.matrixI.clone();
    }

    /**
     * Obtiene un array con la informacion de maximos toquens por plaza
     * <pre>
     * @return Una copia del array con el marcado maximo por plaza. <br>
     *         <code>null</code> Si no es extendida la red
     * </pre>
     */
    @Contract(pure = true)
    @Nullable
    int[] getExtMaxToken() {
        return this.isExtMaxToken() ? this.raw.vectorMaxMark.clone() : null;
    }

    /**
     * Obtiene una matriz con la informacion de los arcos inhibidores, con el mismo formato de JSON
     * <pre>
     * @return Una copia de la matriz con arcos inhibidores. <br>
     *         <code>null</code> Si no es extendida la red
     * </pre>
     */
    @Contract(pure = true)
    @Nullable
    int[][] getExtInh() {
        return this.isExtInh() ? this.raw.matrixH.clone() : null;
    }

    /**
     * Obtiene una matriz con la informacion de los arcos lectores, con el mismo formato de JSON
     * <pre>
     * @return Una copia de la matriz con arcos lectores. <br>
     *         <code>null</code> Si no es extendida la red
     * </pre>
     */
    @Contract(pure = true)
    @Nullable
    int[][] getExtReader() {
        return this.isExtReader() ? this.raw.matrixR.clone() : null;
    }


}
