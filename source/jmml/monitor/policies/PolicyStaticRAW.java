package jmml.monitor.policies;

/**
 * Politica estatica, plana. Utilizada para crear las Policies.
 *  - Matriz de de politicas T
 *          * Los valores deben ser binarios
 *          * La matriz es una matriz identidad de MxM con las filas cambiadas de orden, donde el orden reprensta
 *            la prioridad, esto es:
 *              - La matriz es cuadrada
 *              - Cada fila representa una transicion
 *              - En cada fila hay y solo hay un 1 (No pueden ser todos ceros y no pueden tener mas de uno)
 *              - La posicion del 1 representa el nivel de prioridad
 *              - Las tranciciones no pueden tener igual prioridad (No pueden haber 2 filas iguales)
 */
/*
 Constructor insesario/inservible se crea con Reflection

 Crear un constructor significa hacer checkeo de datos y si usamos reflexion tenemos que hacer chequeo doble,
 es mejor que lo chequee el constructor de la politica si la politica es valida, no un autochequeo , total no
 tiene autofuncionalidad si no que solo es una estructura de datos.

 No vamos a crear un constructor que no vamos a usar
 GetMatrix si lo usamos en test
 */
@SuppressWarnings("unused")
public class PolicyStaticRAW {
    /**
     * Devuelve la matriz de politica
     * @return Matriz de politica estatica
     */
    protected int[][] getMatrixT() {
        return matrixT.clone();
    }

    /**
     * Matriz de politica estatica
     */
    private int [][] matrixT;
}
