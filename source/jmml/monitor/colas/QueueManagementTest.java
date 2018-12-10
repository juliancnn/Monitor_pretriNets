package jmml.monitor.colas;

import ext.junit5.MockitoExtension;
import jmml.monitor.logger.Logger;
import jmml.monitor.logger.LoggerRaw;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import static org.mockito.Mockito.when;

@SuppressWarnings("ALL")
@ExtendWith(MockitoExtension.class)
class QueueManagementTest {

    /*
     * Mas inseguro que tu vieja, no hay Sync para manejar las colas por eso los tiempos y acceden a direntes
     * colas a la vez, pueden fallar los test, pero no vamos a modificar para hacer acceso concurrente
     * a este metodo se supone que entran de a uno a la vez
     */
    @Test
    @DisplayName("Add/rm/check queue")
    void whoIsWaiting() {
        QueueManagementRAW queueManagementRAW = new QueueManagementRAW();
        queueManagementRAW.tempQ = new int[]{0,1,0,0,0};
        try{
            QueueManagement qm = new QueueManagement(queueManagementRAW,null);
            (new Thread(new demoThread(false, 1, qm))).start();
            Thread.sleep(300);
            (new Thread(new demoThread(false, 1, qm))).start();
            (new Thread(new demoThread(false, 2, qm))).start();
            (new Thread(new demoThread(false, 5, qm))).start();
            Thread.sleep(300);
            Assertions.assertArrayEquals(new boolean[]{true, false, false, false, true}, qm.whoIsWaiting());
            (new Thread(new demoThread(true, 1, qm))).start();
            Thread.sleep(300);
            Assertions.assertArrayEquals(new boolean[]{true, false, false, false, true}, qm.whoIsWaiting());
            (new Thread(new demoThread(true, 1, qm))).start();
            Thread.sleep(300);
            Assertions.assertArrayEquals(new boolean[]{false, false, false, false, true}, qm.whoIsWaiting());

        } catch (Exception e) {
            Assertions.fail(e.toString());
        }


    }

    @Test
    @DisplayName("Add/autowakeup timeQueue")
    void autoWakeUpTest(@Mock LoggerRaw logRaw) {
        QueueManagementRAW queueManagementRAW = new QueueManagementRAW();
        queueManagementRAW.tempQ = new int[]{1,1,1,1,1,1};
        when(logRaw.getLogFile()).thenReturn("log_test_Qauto.txt");
        try{
            QueueManagement qm = new QueueManagement(queueManagementRAW,new Logger(logRaw));
            /* 00 Cola vacia
             * 01 Cola con sleeper
             * 10 Cola sin sleeper
             *
             * 0 > Meto sin tiempo
             * 1 > Meto con tiempo
             */
            // 00 0
            (new Thread(new demoTimeThread(1, 0, qm))).start();
            // 00 1
            (new Thread(new demoTimeThread(2, 500, qm))).start();
            // 01 0
            (new Thread(new demoTimeThread(3, 1000, qm))).start();
            Thread.sleep(300);
            (new Thread(new demoTimeThread(3, 0, qm))).start();
            // 01 1
            (new Thread(new demoTimeThread(3, 1000, qm))).start();
            Thread.sleep(300);
            (new Thread(new demoTimeThread(3, 1000, qm))).start();
            // 10 0
            (new Thread(new demoTimeThread(4, 0, qm))).start();
            Thread.sleep(300);
            (new Thread(new demoTimeThread(4, 0, qm))).start();
            // 10 1
            (new Thread(new demoTimeThread(5, 0, qm))).start();
            Thread.sleep(300);
            (new Thread(new demoTimeThread(6, 100, qm))).start();

            Thread.sleep(100);


        } catch (Exception e) {
            Assertions.fail(e.toString());
        }


    }

    @Test
    @DisplayName("setTimer timeQueue")
    void setTimer(@Mock LoggerRaw logRaw) {
        QueueManagementRAW queueManagementRAW = new QueueManagementRAW();
        queueManagementRAW.tempQ = new int[]{1,1,1,1,1,1};
        when(logRaw.getLogFile()).thenReturn("log_test_QsetTime.txt");
        try{
            Logger lg = new Logger(logRaw);
            QueueManagement qm = new QueueManagement(queueManagementRAW,lg);
            /* 00 Cola vacia
             * 01 Cola con sleeper
             * 10 Cola sin sleeper
             *
             * 0 > Meto sin tiempo
             * 1 > Meto con tiempo
             */
            // 00 0
            (new Thread(new demoTimeThread(1, 0, qm))).start();
            // 00 1
            (new Thread(new demoTimeThread(2, 10000, qm))).start();
            // 01 0
            (new Thread(new demoTimeThread(3, 1000, qm))).start();
            Thread.sleep(300);
            (new Thread(new demoTimeThread(3, 0, qm))).start();
            // 01 1
            (new Thread(new demoTimeThread(3, 1000, qm))).start();
            Thread.sleep(300);
            (new Thread(new demoTimeThread(3, 1000, qm))).start();
            // 10 0
            (new Thread(new demoTimeThread(4, 0, qm))).start();
            Thread.sleep(300);
            (new Thread(new demoTimeThread(4, 0, qm))).start();
            // 10 1
            (new Thread(new demoTimeThread(5, 0, qm))).start();
            Thread.sleep(300);
            (new Thread(new demoTimeThread(6, 100, qm))).start();

            Thread.sleep(1000);
            qm.setNewSleep(new long[]{100,0,100,150,0,0});
            Thread.sleep(10000);
            qm.setNewSleep(new long[]{0,0,10,10,1500,50});
            (new Thread(new demoTimeThread(6, 100, qm))).start();
            Thread.sleep(2000);
            lg.print(this,"Fin del test");
        } catch (Exception e) {
            Assertions.fail(e.toString());
        }


    }

    /**
     * dummy threads para testing sin tiempo
     */
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
                    try{
                        this.queueManagement.addMe(this.cola,500);
                    }catch (QueueInterrupException e){
                        Assertions.fail(e.getMessage());
                    }


            }
        }
    }
    /**
     * dummy threads para testing con tiempo
     */
    class demoTimeThread implements Runnable {
        private final QueueManagement queueManagement;
        private final int cola;
        private final long time;

        demoTimeThread(int cola, long time, QueueManagement qm) {
            super();
            this.queueManagement = qm;
            this.cola = cola;
            this.time = time;
        }

        @Override
        public void run() {
            try{
                this.queueManagement.addMe(this.cola,this.time);
            }catch (QueueInterrupException e){
                Assertions.fail(e.getMessage());
            }
        }
    }
}