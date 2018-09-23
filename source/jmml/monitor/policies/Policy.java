package jmml.monitor.policies;

import jmml.monitor.rdp.RDP;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Encargado de manejar la politica de la red, dando mecanimos para cambiarla y de consulta de disparos
 *
 * @TODO HACER TODO BEBE, esta mas crudo que los bebes que se trago wanda
 * @TODO IMPLEMENTAR EXCEPCIONES PARA MALA CARGA DE POLITICA + TRANSICIONES ESTATICAS
 * @TODO Rehacer la documentacion
 * @TODO Debo guardad ultimo disparo y cantidad de veces que se disparo, para hacer matrices
 */
public class Policy {
    /**
     * Politica usada en el momento
     */
    private policyType mode;
    /**
     * Cantidad de transiciones que modela la politica
     */
    private int lenghtTrans;
    /**
     * Matriz de prioridad estatica
     */
    boolean[][] matStatic;
    /**
     * Matriz de politica usada para calcular las prioridades
     */
    boolean[][] matP;

    /**
     * <pre>
     * Crea el manejador de politicas en base a una politica predefinida y una red de petri <br>
     * La politica puede cambiar. <br>
     * La red de necesario para garantizar coinsistencia de politicas para la red.<br>
     * @param mode Modo predefinido para la politica
     * @param rdp Red de petri a la cual se quiere aplicar politica,
     *            el objeto no es modificado en ningun momento por Policy, se utiliza para garantizar consistencia
     * </pre>
     */
    public Policy(policyType mode, @NotNull RDP rdp) {

        /* Seteo politica por defecto y si hay cargo politica estatica */
        this.setPolicy(mode);
        this.lenghtTrans = rdp.getSensitizedArray().length;

        /* Si hay, Armo matriz de prioridad estica */
        int[][] mT = rdp.getMatrixT();


    }

    /**
     * Setea una nueva politica, cambia el criterio de toma de desiciones para la seleccion de transicion
     *
     * @param policy Nueva politica para toma de desiciones.
     * @TODO CHECKQUEO DE EXISTENCIA throws
     */
    void setPolicy(policyType policy) {
        /* Guardo el modo seleccionado */
        this.mode = policy;
        /* Apunto a la nueva matriz de politicas */
        switch (this.mode) {
            case STATICORDER:
                this.matP = this.matStatic;
                break;
            default:
                System.out.println("Politica no considerada, aprende a programar");
                System.exit(-2);
                break;
        }


    }

    /***
     * Retorna la politica seteada actualmente
     * @return Politica usada actualmente
     */
    @Contract(pure = true)
    public policyType getPolicy() {
        return this.mode;
    }

    /**
     * <pre>
     * Retorna la transicion de mayor prioridad entre un grupo de transiciones, segun la politica.<br>
     * El vector binario recibe todas las transiciones, y seteadas en falso aquellas de las cuales no se quiere<br>
     * tener en cuenta para retornar la de mayor prioridad.
     *
     * @param whoIsAviable Vector binario de transiciones. <br>
     *                     True: La transicion sera tomada en cuenta <br>
     *                     False: La transicion no sera tomada en cuenta
     * @return Transicion seleccionada por la politica para disparar
     * @TODO Arrojaria exepcion si el vector esta vacio [0 0 0 ....] y otra distinta para null \
     * ?? Deberia guardar el numero estatico de la cantidad de transiciones a recibir??
     * </pre>
     */
    @Contract(pure = true)
    public int tellMeWho(boolean[] whoIsAviable) {
        return 0;

    }
    /*==================================================================================================================

                                        Operaciones matriciales

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
    protected boolean[] firstTrue(@NotNull boolean[] priorVector) {
        boolean[] fVector = new boolean[priorVector.length]; // Todos falsos por defecto
        for (int i = 0; i < fVector.length; i++) {
            fVector[i] = priorVector[i];
            if (fVector[i]) break;
        }
        return fVector;
    }

    /**
     * Multiplica el vector V booleano por la Matriz M [ceros;unos] y cuadrada
     * Puede transponerse la matriz antes de multiplicarla
     *
     * @param m           Matriz [0;1] de dimencion MxM
     * @param v           Vector booleana de dimencion Mx1
     * @param transpuesta Si es verdadero transpone la matriz M
     * @return vector de Mx1
     * @throws ArithmeticException Matriz y vector de dimenciones incompatibles
     */
    @NotNull
    @Contract(pure = true)
    protected int[] matMulVect(@NotNull boolean[] v, @NotNull int[][] m, boolean transpuesta) {
        if (v.length != m[0].length)
            throw new ArithmeticException("Matrix y vector de tamanos incompatibles");

        // Vector resultado inicializado en 0
        int[] result = new int[m.length];

        // Opero por filas en la matriz
        if (transpuesta)
            for (int i = 0; i < v.length; i++)
                for (int j = 0; j < v.length; j++)
                    result[i] += (v[i] ? 1 : 0) * m[j][i];
        else
            for (int i = 0; i < v.length; i++)
                for (int j = 0; j < v.length; j++)
                    result[i] += (v[i] ? 1 : 0) * m[i][j];


        return result;
    }


}
