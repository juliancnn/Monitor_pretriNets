package jmml.monitor.policies;

import ext.junit5.MockitoExtension;
import jmml.monitor.colas.QueueManagement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;

/*
 * https://junit.org/junit5/docs/5.0.0/user-guide/#writing-tests 3.11
 * */
@SuppressWarnings("ALL")
@ExtendWith(MockitoExtension.class)
class PolicyTest {


    /*==================================================================================================================
                                        Test de politicas
     =================================================================================================================*/
    @Test
    @DisplayName("[Policy Static Mat]  Con STATIC=NULL & Not Null")
    void tellMeWhoStatic(@Mock QueueManagement qm) {
        // Prioridad 3-2-4-1-5
        int[][] matT = new int[][]{
                {0, 0, 1, 0, 0},
                {0, 1, 0, 0, 0},
                {0, 0, 0, 1, 0},
                {1, 0, 0, 0, 0},
                {0, 0, 0, 0, 1},
        };
        when(qm.size()).thenReturn(5);
        Policy pol = new Policy(qm, policyType.STATICORDER,policyType.STATICORDER, matT);
        Assertions.assertEquals(3, pol.tellMeWho(new boolean[]{true, true, true, true, true}));
        Assertions.assertEquals(2, pol.tellMeWho(new boolean[]{true, true, false, true, true}));
        Assertions.assertEquals(4, pol.tellMeWho(new boolean[]{true, false, false, true, true}));
        Assertions.assertEquals(1, pol.tellMeWho(new boolean[]{true, false, false, false, true}));
        Assertions.assertEquals(5, pol.tellMeWho(new boolean[]{false, false, false, false, true}));

        /* Matriz de politica identidad */
        pol = new Policy(qm, policyType.STATICORDER,policyType.STATICORDER, null);
        Assertions.assertEquals(1, pol.tellMeWho(new boolean[]{true, false, true, true, true}));
        Assertions.assertEquals(2, pol.tellMeWho(new boolean[]{false, true, false, false, true}));
        Assertions.assertEquals(3, pol.tellMeWho(new boolean[]{false, false, true, true, true}));
        Assertions.assertEquals(4, pol.tellMeWho(new boolean[]{false, false, false, true, true}));
        Assertions.assertEquals(5, pol.tellMeWho(new boolean[]{false, false, false, false, true}));

    }

    @Test
    @DisplayName("[Policy dinamic matrix] Dinamic Matrix / duplicate values")
    void genMatOfPol(@Mock QueueManagement qm) {
        /* Testeo caso para valores repeditos:
            - Primaria Menor valor mas prioridad y en caso de empate:
                CASO 1) Secundaria con matriz identidad (Orden t1 mas prioridad tn menor prioridad)
                CASO 2) Secundaria con matriz Estatica (Orden 3-2-4-1-5)
            - Primaria Mayor valor
                CASO 3) Secundaria con matriz identidad (Orden t1 mas prioridad tn menor prioridad)
                CASO 4) Secundaria con matriz Estatica (Orden 3-2-4-1-5)
         */
        int[] testVector;
        int[][] matPol;
        int[][] matStatic = new int[][]{
                {0, 0, 1, 0, 0},
                {0, 1, 0, 0, 0},
                {0, 0, 0, 1, 0},
                {1, 0, 0, 0, 0},
                {0, 0, 0, 0, 1},
        };
        when(qm.size()).thenReturn(5);
        Policy pol;

        /* ============================ CASO 1 ===================================== */
        /* Prioridad primaria asendente menor valor del vector mayor prioridad */
        /* Prioridad secundaria (Si hay empate) identidad: t1 hight priority tn low priority */
        pol = new Policy(qm, policyType.STATICORDER,policyType.STATICORDER, null);
        testVector = new int[] {0,5,2,3,3};
        matPol = new int[][]{ // t0-t3-t4-t5-t2
                {1, 0, 0, 0, 0},
                {0, 0, 1, 0, 0},
                {0, 0, 0, 1, 0},
                {0, 0, 0, 0, 1},
                {0, 1, 0, 0, 0},
        };

        Assertions.assertArrayEquals(matPol,pol.genMatOfPol(testVector,false));

        /* ============================ CASO 2 ===================================== */
        /* Prioridad primaria asendente menor valor del vector mayor prioridad */
        /* Prioridad secundaria (Si hay empate): 2-1-3-0-4 */
        pol = new Policy(qm, policyType.STATICORDER,policyType.STATICORDER, matStatic);
        testVector = new int[] {0,2,3,3,2};
        matPol = new int[][]{ // t0-t1-t4-t2-t3
                {1, 0, 0, 0, 0},
                {0, 1, 0, 0, 0},
                {0, 0, 0, 0, 1},
                {0, 0, 1, 0, 0},
                {0, 0, 0, 1, 0},
        };
        Assertions.assertArrayEquals(matPol,pol.genMatOfPol(testVector,false));


        /* ============================ CASO 3 ===================================== */
        /* Prioridad primaria desendente Mayor valor del vector mayot prioridad */
        /* Prioridad secundaria (Si hay empate) identidad: t1 hight priority tn low priority */
        pol = new Policy(qm, policyType.STATICORDER,policyType.STATICORDER, null);
        testVector = new int[] {0,2,2,3,3};
        matPol = new int[][]{ // t3-t4-t2-t1-t0
                {0, 0, 0, 1, 0},
                {0, 0, 0, 0, 1},
                {0, 1, 0, 0, 0},
                {0, 0, 1, 0, 0},
                {1, 0, 0, 0, 0},
        };

        Assertions.assertArrayEquals(matPol,pol.genMatOfPol(testVector,true));

        /* ============================ CASO 4 ===================================== */
        /* Prioridad primaria desendente Mayor valor del vector mayot prioridad */
        /* Prioridad secundaria (Si hay empate): 2-1-3-0-4 */
        pol = new Policy(qm, policyType.STATICORDER,policyType.STATICORDER, matStatic);
        testVector = new int[] {0,2,3,2,3};
        matPol = new int[][]{ // t2-t4-t1-t3-t0
                {0, 0, 1, 0, 0},
                {0, 0, 0, 0, 1},
                {0, 1, 0, 0, 0},
                {0, 0, 0, 1, 0},
                {1, 0, 0, 0, 0},
        };

        Assertions.assertArrayEquals(matPol,pol.genMatOfPol(testVector,true));




    }

    /*==================================================================================================================
                                    TEST DE OPERACIONES MATEMATICAS Y VECTORIALES
     =================================================================================================================*/

    @Test
    @DisplayName("[MAT TEST] Primer valor en true")
    void firstOnTrue(@Mock QueueManagement qm) {
        Policy pol = new Policy(qm, policyType.RANDOM,policyType.STATICORDER, null);
        Assertions.assertArrayEquals(new int[]{1, 0, 0, 0, 0}, pol.firstOnTrue(new int[]{1, 0, 1, 0, 0}));
        Assertions.assertArrayEquals(new int[]{0, 1, 0, 0, 0}, pol.firstOnTrue(new int[]{0, 1, 0, 1, 0}));
        Assertions.assertArrayEquals(new int[]{0, 0, 1, 0, 0}, pol.firstOnTrue(new int[]{0, 0, 1, 0, 1}));
        Assertions.assertArrayEquals(new int[]{0, 0, 0, 1, 0}, pol.firstOnTrue(new int[]{0, 0, 0, 1, 1}));
        Assertions.assertArrayEquals(new int[]{0, 0, 0, 0, 1}, pol.firstOnTrue(new int[]{0, 0, 0, 0, 1}));
    }

    @Test
    @DisplayName("[MAT TEST] Mat Mul con traspuesta")
    void matMulVect(@Mock QueueManagement qm) {
        Policy pol = new Policy(qm, policyType.RANDOM,policyType.STATICORDER, null);
        int[][] M = new int[][]{
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        };
        int[] V = new int[]{2, 3, 4};
        int[] R1 = new int[]{20, 47, 74};
        int[] R2 = new int[]{42, 51, 60};
        Assertions.assertArrayEquals(R1, pol.matMulVect(V, M, false));
        Assertions.assertArrayEquals(R2, pol.matMulVect(V, M, true));
    }

    @DisplayName("[MAT TEST] Donde esta el true")
    @Test
    void getFirstOnTrue(@Mock QueueManagement qm) {
        Policy pol = new Policy(qm, policyType.RANDOM, policyType.STATICORDER, null);
        Assertions.assertEquals(1, pol.getFirstOnTrue(new int[]{1, 0, 1, 0, 0}));
        Assertions.assertEquals(2, pol.getFirstOnTrue(new int[]{0, 1, 1, 0, 0}));
        Assertions.assertEquals(3, pol.getFirstOnTrue(new int[]{0, 0, 1, 1, 0}));
        Assertions.assertEquals(4, pol.getFirstOnTrue(new int[]{0, 0, 0, 1, 0}));
        Assertions.assertEquals(5, pol.getFirstOnTrue(new int[]{0, 0, 0, 0, 1}));
    }
}