package jmml.monitor.policies;

import jmml.monitor.colas.QueueManagement;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;

/**
 * Encargado de manejar la politica de la cola, dando mecanimos para cambiarla y de consulta de disparos
 *
 * @TODO HACER TODO BEBE, esta mas crudo que los bebes que se trago wanda
 * @TODO IMPLEMENTAR EXCEPCIONES PARA MALA CARGA DE POLITICA + TRANSICIONES ESTATICAS
 * @TODO Chequear el constructor de la matrix t coinsida con cantidad de colas
 * @TODO Rehacer la documentacion
 * @TODO Debo guardad ultimo disparo y cantidad de veces que se disparo, para hacer matrices
 * @TODO TESTEAR TODOS LOS ARMADOS DE MATRICES y operaciones matematicas con matrices
 */
public class Policy {
    /**
     * Politica usada en el momento
     */
    private policyType mode;
    /**
     * Colas a aplicar las politicas, utilizada para obtener estadisticas
     */
    private QueueManagement queue;
    /**
     * Matriz random de politicas, de distribucion uniforme, varia con cada utilizacion
     */
    private int[][] matPRandom;

    /**
     * Matriz de prioridad estatica
     */
    private int[][] matPStatic;
    private int[][] matPMaxSizeQueue;
    /**
     * Matriz de politica usada en el momento para calcular las prioridades
     */
    private int[][] matOfPolicy;

    /**
     * <pre>
     * Crea el manejador de politicas en base a una politica predefinida y cola a la cual se le aplicara la politica
     * La politica puede cambiar.
     * El administrador de colas es necesario para garantizar coinsitencia en la generacion de politicas (Armado de
     * matrices de politicas)
     * </pre>
     *
     * @param mode            Modo inicial para la politica
     * @param queueManagement Cola de procesos al que se le aplicara la politica<br>
     *                        el objeto no es modificado en ningun momento por Policy, se utiliza para garantizar
     *                        consistencia
     * @param matrixStatic    Null para utilizar la matriz identidad como matriz de politicas estaticas
     *                        El orden de prioridad esta dado por el orden de transiciones
     *                        int[][] Matriz cuadrada de 2 dimenaciones coinsidente con el tamano de cola para
     *                        prioridades esaticas (matriz identidad con irden de columnas cambiados)
     * @TODO Debo armar un metodo que arme las matrices?
     * @TODO Verificar que la matriz sea identidad de filas interambiadas
     */
    public Policy(@NotNull QueueManagement queueManagement, policyType mode, @Nullable int[][] matrixStatic) {
        // Guardo colas para hacer estadistica
        this.queue = queueManagement;
        int size = this.queue.size();

        /* RANDOM MAT - Luego sera desordenada */
        this.matPRandom = new int[size][size];
        for (int i = 0; i < size; i++)
            this.matPRandom[i][i] = 1;

        /* STATIC MAT  */
        this.matPStatic = matrixStatic == null ? this.matPRandom.clone() : matrixStatic.clone();

        /* MAX SIZE QUEUE*/
        this.matPMaxSizeQueue = new int[size][size];

        /* Seteo politica al ultimo para generar antes las matrices */
        this.setPolicy(mode);

    }

    /**
     * Setea una nueva politica, cambia el criterio de toma de desiciones para la seleccion de colas,
     * la politica sera aplicada inmediatamente para la proxima seleccion de colas
     *
     * @param policy Nueva politica para toma de desiciones.
     * @throws IllegalArgumentException Politica no esperada, por inexistencia o falta de implementacion.
     */
    void setPolicy(policyType policy) throws IllegalArgumentException {
        this.mode = policy;
        switch (this.mode) {
            case STATICORDER:
                this.matOfPolicy = this.matPStatic;
                break;
            case RANDOM:
                this.matOfPolicy = this.matPRandom;
                break;
            default:
                throw new java.lang.IllegalArgumentException("Politica no esperada");
        }

    }

    /***
     * Retorna la politica Seteada actualmente
     * @return Politica usada actualmente
     */
    @Contract(pure = true)
    public policyType getPolicy() {
        return this.mode;
    }

    /**
     * <pre>
     * Retorna el numero de la cola de mayor prioridad entre un grupo de colas, segun la politica seteada.
     * El vector binario recibe todas las colas, y seteadas en falso aquellas de las cuales no se quiere
     * tener en cuenta para retornar la de mayor prioridad.
     * </pre>
     *
     * @param whoIsAviable Vector binario de colas. <br>
     *                     True: La cola sera tomada en cuenta <br>
     *                     False: La cola no sera tomada en cuenta
     * @return numero de cola seleccionada por la politica para desencolar, segun la politica establecida
     * @TODO Update matrices
     * @TODO Arrojaria exepcion si el vector esta vacio [0 0 0 ....] y otra distinta para null \
     * @TODO Ver el tema del casteo de bool a int si se puede mejorar operando bit a bit o pasar todo a int[] en rdp
     */
    @Contract(pure = true)
    public int tellMeWho(@NotNull boolean[] whoIsAviable) {
        int[] who = new int[whoIsAviable.length];
        for (int i = 0; i < who.length; i++)
            who[i] = whoIsAviable[i] ? 1 : 0;

        return this.getFirstOnTrue(
                this.matMulVect(
                        this.firstOnTrue(this.matMulVect(who, this.matOfPolicy, false)),
                        this.matOfPolicy, true));

    }

    /*==================================================================================================================

                                 Generacion/actualizacion dinamica de matrices de politicas

     =================================================================================================================*/

    /**
     * Genera la matriz de prioridades segun el tamano de las colas, mientras mas grande sea la cola mayor prioridad
     * @TODO QUE PASA SI 2 colas tienen mismo largo, como resuelvo la disputa de la matriz?
     * @return Matrix cuadrada de prioridades generada segun el tamano de la cola
     */
    int[][] genMatPMaxSize() {
        /* Matriz de politica - Vector de tamano de colas originales 0>= - vector ordenado */
        int[][] policyMat = new int[this.queue.size()][this.queue.size()];
        int[] orginalSizes = this.queue.siezeOfQueue();
        int[] sortSizes = orginalSizes.clone();
        Arrays.sort(sortSizes); // Vector ordenado de menor a mayor

        // Busco por prioridad (p) a la cola mas larga (ultima)
        // La busqueda no puede ser mas eficiente por que no puedo ordenar el array original
        for (int i = sortSizes.length - 1, p = 0, cola; i > 0; i--) {
            for (cola = 0; cola < orginalSizes.length; cola++)
                if (orginalSizes[cola] == sortSizes[i]) {
                    orginalSizes[cola] = -1; // Para no contar valores (Siempre positivos) repetidos
                    break;
                }
            policyMat[p++][cola] = 1;
        }

        return policyMat;

    }



    /*==================================================================================================================

                                        Operaciones matriciales y vectoriales

     =================================================================================================================*/

    /**
     * <pre>
     * Retorna el vector que recibe con la primera ocurrencia de un true, el resto de los elementos falsos
     * @param priorVector Vector a analizar
     * @return Vector con un solo true, en la posicion mas baja donde se encontraba el true en priorVector
     * </pre>
     */
    @Contract(pure = true)
    @NotNull
    int[] firstOnTrue(@NotNull int[] priorVector) {
        int[] fVector = new int[priorVector.length]; // Todos falsos por defecto
        for (int i = 0; i < fVector.length; i++) {
            fVector[i] = priorVector[i];
            if (fVector[i] == 1) break;
        }
        return fVector;
    }

    /**
     * Multiplica la Matriz M por el vector V
     *
     * @param m           Matriz de dimencion TxT
     * @param v           Vector de dimencion Tx1
     * @param transpuesta True si se quiere trasnponer la matrix T
     * @return vector de Tx1 resultado de la multiplicacion de MxV
     * @throws ArithmeticException Matriz y vector de dimenciones incompatibles
     * @TODO No hay una forma de trabajarlo con booleanos que sea eficiente como C para multiplicar(AND BIT a BIT) de un array?
     */
    @NotNull
    @Contract(pure = true)
    int[] matMulVect(@NotNull int[] v, @NotNull int[][] m, boolean transpuesta) throws ArithmeticException {
        if (v.length != m.length || m.length != m[0].length)
            throw new ArithmeticException("Matrix y vector de tamanos inesperados");

        int[] result = new int[m.length];

        for (int i = 0; i < v.length; i++)
            for (int j = 0; j < v.length; j++)
                result[i] += v[j] * (transpuesta ? m[j][i] : m[i][j]);

        return result;
    }


    /**
     * Desordena una matriz cuadrada por filas, intercambia las filas de manera aleatorea<br>
     * con una distribucion uniforme, todas las filas al menos una vez.
     *
     * @param matrix Matriz por filas que se quiere desordenar
     * @param clone  True, Trabaja sobre una copia<br>
     *               False, Modifica la matriz original
     * @return matriz desordenadas por fila
     * @throws ArithmeticException Matriz no es cuadrada
     * @TODO Hay una forma mas eficiente de hacer esto?
     */
    @NotNull
    protected int[][] matReaorderRandom(@NotNull int[][] matrix, boolean clone) throws ArithmeticException {
        if (matrix[0].length != matrix.length)
            throw new ArithmeticException("Matrix a desordenar no es cuadrada");

        int[][] mat = clone ? matrix.clone() : matrix;
        Random rn = new Random();
        int[] swapRow;
        int randRow;
        for (int i = 0; i < mat.length; ++i) {
            randRow = rn.nextInt(matrix.length);//Distribucion uniforme entre 0 y size-1
            swapRow = matrix[i]; //.clone(); No hace falta por punteros, no pasa el garbage collector
            matrix[i] = matrix[randRow];
            matrix[randRow] = swapRow;
        }

        return mat;
    }


    /**
     * Retorna la posicion del vector donde se encuentra el primer valor distinto de cero.
     * contando como 1 la primer posicion
     *
     * @param v Vector a chequear
     * @return posicion del vector donde se encuentra el primer valor distinto de cero.<br>
     * 0 en caso de ser vector de ceros.
     */
    @Contract(pure = true)
    int getFirstOnTrue(@NotNull int[] v) {
        for (int i = 0; i < v.length; i++)
            if (v[i] != 0)
                return i + 1;

        return 0;
    }


}
