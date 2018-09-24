package jmml.monitor.colas;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QueueManagementTest {

    /*
     * Mas inseguro que tu vieja, no hay Sync para manejar las colas por eso los tiempos y acceden a direntes
     * colas a la vez, pueden fallar los test, pero no vamos a modificar para hacer acceso concurrente
     * a este metodo se supone que entran de a uno a la vez
     */
    @Test
    void whoIsWaiting() {
        try{
            QueueManagement qm = new QueueManagement(5);
            (new Thread(new demoThread(false, 1, qm))).start();
            Thread.sleep(300);
            (new Thread(new demoThread(false, 1, qm))).start();
            (new Thread(new demoThread(false, 2, qm))).start();
            (new Thread(new demoThread(false, 5, qm))).start();
            Thread.sleep(300);
            Assertions.assertArrayEquals(new boolean[]{true, true, false, false, true}, qm.whoIsWaiting());
            (new Thread(new demoThread(true, 1, qm))).start();
            Thread.sleep(300);
            Assertions.assertArrayEquals(new boolean[]{true, true, false, false, true}, qm.whoIsWaiting());
            (new Thread(new demoThread(true, 1, qm))).start();
            Thread.sleep(300);
            Assertions.assertArrayEquals(new boolean[]{false, true, false, false, true}, qm.whoIsWaiting());

        } catch (Exception e) {
            Assertions.fail(e.toString());
        }


    }


    class demoThread implements Runnable {
        private final boolean wake;
        private final QueueManagement queueManagement;
        private final int cola;

        demoThread(boolean wakeUp, int cola, QueueManagement qm) {
            super();
            this.wake = wakeUp;
            this.queueManagement = qm;
            this.cola = cola;
        }

        @Override
        public void run() {
                if (this.wake) {
                    this.queueManagement.wakeUpTo(this.cola);
                } else {
                    this.queueManagement.addMe(this.cola);

            }
        }
    }
}