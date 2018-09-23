package jmml.monitor.policies;

import jmml.monitor.colas.QueueManagement;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * Encargado de manejar la politica de la red, dando mecanimos para cambiarla y de consulta de disparos
 *
 * @TODO HACER TODO BEBE, esta mas crudo que los bebes que se trago wanda
 * @TODO IMPLEMENTAR EXCEPCIONES PARA MALA CARGA DE POLITICA + TRANSICIONES ESTATICAS
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
     * Colas a aplicar las politicas.
     */
    private QueueManagement queue;
    /**
     * Matriz random de politicas, de distribucion uniforme
     */
    private int[][] matPRandom;

    /**
     * Matriz de prioridad estatica
     */
    //boolean[][] matStatic;
    /**
     * Matriz de politica usada para calcular las prioridades
     */
    int[][] matOfPolicy;

    /**
     * <pre>
     * Crea el manejador de politicas en base a una politica predefinida y cola a la cual se le aplicara la politica
     * La politica puede cambiar.
     * El administrador de colas es necesario para garantizar coinsitencia en la generacion de politicas (Armado de
     * matrices de politicas)
     * @param mode Modo inicial para la politica
     * @param queueManagement Cola de procesos al que se le aplicara la politica
     *        el objeto no es modificado en ningun momento por Policy, se utiliza para garantizar consistencia
     * </pre>
     */
    public Policy(@NotNull QueueManagement queueManagement, policyType mode) {

        /* Seteo politica por defecto y si hay cargo politica estatica */
        this.setPolicy(mode);
        this.queue = queueManagement;
        int size = this.queue.size();

        /* Si hay, Armo matriz de prioridad estica */
        //int[][] mT = rdp.getMatrixT();

        /* Deberia llamar un metodo que arme toda las matrices? */
        /* Armo una matriz identidad para la politica random, luego sera desordenada */
        this.matPRandom = new int[size][size];
        for (int i = 0; i < size; i++)
            this.matPRandom[i][i] = 1;

    }

    /**
     * Setea una nueva politica, cambia el criterio de toma de desiciones para la seleccion de colas,
     * la politica sera aplicada inmediatamente para la proxima seleccion de colas
     *
     * @param policy Nueva politica para toma de desiciones.
     * @throws IllegalArgumentException Politica no esperada, por inexistencia o falta de implementacion.
     */
    void setPolicy(policyType policy) {
        this.mode = policy;
        switch (this.mode) {
            /*case STATICORDER:
                this.matOfPolicy = this.matStatic;
                break;
            */
            case RANDOM:
                this.matOfPolicy = this.matPRandom;
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
     * @return Cola seleccionada por la politica para desencolar, segun la politica establecida
     * @TODO Arrojaria exepcion si el vector esta vacio [0 0 0 ....] y otra distinta para null \
     * @TODO Ver el tema del casteo de bool a int si se puede mejorar operando bit a bit o pasar todo a int[] en rdp
     * ?? Deberia guardar el numero estatico de la cantidad de transiciones a recibir??
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

                                 Generacion dinamica de matrices de politicas

     =================================================================================================================*/



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
    protected int[] firstOnTrue(@NotNull int[] priorVector) {
        int[] fVector = new int[priorVector.length]; // Todos falsos por defecto
        for (int i = 0; i < fVector.length; i++) {
            fVector[i] = priorVector[i];
            if (fVector[i] == 1) break;
        }
        return fVector;
    }

    /**
     * Multiplica el vector V[ceros;unos] por la Matriz M [ceros;unos] y cuadrada
     * Puede transponerse la matriz antes de multiplicarla
     *
     * @param m           Matriz [0;1] de dimencion MxM
     * @param v           Vector booleana de dimencion Mx1
     * @param transpuesta Si es verdadero transpone la matriz M
     * @return vector de Mx1
     * @throws ArithmeticException Matriz y vector de dimenciones incompatibles
     * @TODO No hay una forma de trabajarlo con booleanos que sea eficiente como C para multiplicar(AND BIT a BIT) de un array?
     */
    @NotNull
    @Contract(pure = true)
    protected int[] matMulVect(@NotNull int[] v, @NotNull int[][] m, boolean transpuesta) {
        if (v.length != m[0].length)
            throw new ArithmeticException("Matrix y vector de tamanos incompatibles");

        // Vector resultado inicializado en 0
        int[] result = new int[m.length];

        // Opero por filas en la matriz
        if (transpuesta)
            for (int i = 0; i < v.length; i++)
                for (int j = 0; j < v.length; j++)
                    result[i] += v[i] * m[j][i];
        else
            for (int i = 0; i < v.length; i++)
                for (int j = 0; j < v.length; j++)
                    result[i] += v[i] * m[i][j];


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
    protected int[][] matReaorderRandom(@NotNull int[][] matrix, boolean clone) {
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
    protected int getFirstOnTrue(@NotNull int[] v) {
        for (int i = 0; i < v.length; i++)
            if (v[i] != 0)
                return i + 1;

        return 0;
    }


}
