package jmml.monitor.policies;

import ext.junit5.MockitoExtension;
import jmml.monitor.colas.QueueManagement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/*
 * https://junit.org/junit5/docs/5.0.0/user-guide/#writing-tests 3.11
 * */
@ExtendWith(MockitoExtension.class)
class PolicyTest {


    /*==================================================================================================================
                                        Test de politicas
     =================================================================================================================*/
    @Test
    @DisplayName("[Policy Static Mat] ")
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
        Policy pol = new Policy(qm, policyType.STATICORDER, matT);
        Assertions.assertEquals(3, pol.tellMeWho(new boolean[]{true, true, true, true, true}));
        Assertions.assertEquals(2, pol.tellMeWho(new boolean[]{true, true, false, true, true}));
        Assertions.assertEquals(4, pol.tellMeWho(new boolean[]{true, false, false, true, true}));
        Assertions.assertEquals(1, pol.tellMeWho(new boolean[]{true, false, false, false, true}));
        Assertions.assertEquals(5, pol.tellMeWho(new boolean[]{false, false, false, false, true}));

        /* Matriz de politica identidad */
        pol = new Policy(qm, policyType.STATICORDER, null);
        Assertions.assertEquals(1, pol.tellMeWho(new boolean[]{true, false, true, true, true}));
        Assertions.assertEquals(2, pol.tellMeWho(new boolean[]{false, true, false, false, true}));
        Assertions.assertEquals(3, pol.tellMeWho(new boolean[]{false, false, true, true, true}));
        Assertions.assertEquals(4, pol.tellMeWho(new boolean[]{false, false, false, true, true}));
        Assertions.assertEquals(5, pol.tellMeWho(new boolean[]{false, false, false, false, true}));

    }

    /*==================================================================================================================
                                    TEST DE OPERACIONES MATEMATICAS Y VECTORIALES
     =================================================================================================================*/

    @Test
    @DisplayName("[MAT TEST] Primer valor en true")
    void firstOnTrue(@Mock QueueManagement qm) {
        Policy pol = new Policy(qm, policyType.RANDOM, null);
        Assertions.assertArrayEquals(new int[]{1, 0, 0, 0, 0}, pol.firstOnTrue(new int[]{1, 0, 1, 0, 0}));
        Assertions.assertArrayEquals(new int[]{0, 1, 0, 0, 0}, pol.firstOnTrue(new int[]{0, 1, 0, 1, 0}));
        Assertions.assertArrayEquals(new int[]{0, 0, 1, 0, 0}, pol.firstOnTrue(new int[]{0, 0, 1, 0, 1}));
        Assertions.assertArrayEquals(new int[]{0, 0, 0, 1, 0}, pol.firstOnTrue(new int[]{0, 0, 0, 1, 1}));
        Assertions.assertArrayEquals(new int[]{0, 0, 0, 0, 1}, pol.firstOnTrue(new int[]{0, 0, 0, 0, 1}));
    }

    @Test
    @DisplayName("[MAT TEST] Mat Mul con traspuesta")
    void matMulVect(@Mock QueueManagement qm) {
        Policy pol = new Policy(qm, policyType.RANDOM, null);
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
        Policy pol = new Policy(qm, policyType.RANDOM, null);
        Assertions.assertEquals(1, pol.getFirstOnTrue(new int[]{1, 0, 1, 0, 0}));
        Assertions.assertEquals(2, pol.getFirstOnTrue(new int[]{0, 1, 1, 0, 0}));
        Assertions.assertEquals(3, pol.getFirstOnTrue(new int[]{0, 0, 1, 1, 0}));
        Assertions.assertEquals(4, pol.getFirstOnTrue(new int[]{0, 0, 0, 1, 0}));
        Assertions.assertEquals(5, pol.getFirstOnTrue(new int[]{0, 0, 0, 0, 1}));
    }
}