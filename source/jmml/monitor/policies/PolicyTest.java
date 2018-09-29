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

    /* Armo el Mocked and dummy objet */
    @BeforeEach
    void init(@Mock QueueManagement qm){
        qm = mock(QueueManagement.class);
        when(qm.size()).thenReturn(5);
    }


    @Test
    void tellMeWho() {
    }

    /*==================================================================================================================
                                    TEST DE OPERACIONES MATEMATICAS Y VECTORIALES
     =================================================================================================================*/

    @Test
    @DisplayName("[MAT TEST] Primer valor en true")
    void firstOnTrue(@Mock QueueManagement qm) {
        Policy pol = new Policy(qm, policyType.RANDOM);
        Assertions.assertArrayEquals(new int[]{1, 0, 0, 0, 0}, pol.firstOnTrue(new int[]{1, 0, 1, 0, 0}));
        Assertions.assertArrayEquals(new int[]{0, 1, 0, 0, 0}, pol.firstOnTrue(new int[]{0, 1, 0, 1, 0}));
        Assertions.assertArrayEquals(new int[]{0, 0, 1, 0, 0}, pol.firstOnTrue(new int[]{0, 0, 1, 0, 1}));
        Assertions.assertArrayEquals(new int[]{0, 0, 0, 1, 0}, pol.firstOnTrue(new int[]{0, 0, 0, 1, 1}));
        Assertions.assertArrayEquals(new int[]{0, 0, 0, 0, 1}, pol.firstOnTrue(new int[]{0, 0, 0, 0, 1}));
    }

    @Test
    @DisplayName("[MAT TEST] Mat Mul con traspuesta")
    void matMulVect(@Mock QueueManagement qm) {
        Policy pol = new Policy(qm, policyType.RANDOM);
        int[][] M = new int[][]{
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };
        int[] V = new int[]{2,3,4};
        int[] R1 = new int[]{20,47,74};
        int[] R2 = new int[]{42,51,60};
        Assertions.assertArrayEquals(R1, pol.matMulVect(V,M,false));
        Assertions.assertArrayEquals(R2, pol.matMulVect(V,M,true));
    }

    @DisplayName("[MAT TEST] Donde esta el true")
    @Test
    void getFirstOnTrue(@Mock QueueManagement qm) {
        Policy pol = new Policy(qm, policyType.RANDOM);
        Assertions.assertEquals(1, pol.getFirstOnTrue(new int[]{1, 0, 1, 0, 0}));
        Assertions.assertEquals(2, pol.getFirstOnTrue(new int[]{0, 1, 1, 0, 0}));
        Assertions.assertEquals(3, pol.getFirstOnTrue(new int[]{0, 0, 1, 1, 0}));
        Assertions.assertEquals(4, pol.getFirstOnTrue(new int[]{0, 0, 0, 1, 0}));
        Assertions.assertEquals(5, pol.getFirstOnTrue(new int[]{0, 0, 0, 0, 1}));
    }
}