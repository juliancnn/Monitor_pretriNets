package jmml.monitor.complex_test;

import jmml.monitor.Monitor;

/**
 * Dimmy processes para hacer pruebas, disparan una secuencia de transiciones
 */
class PullDummyProcess_Single implements Runnable {
    /**
     * Nombre del thread
     */
    private String name;
    /**
     * Ciclo de disparos a realizar
     */
    private int[] seq;
    /**
     * Tiempo a dormir despues de la secuencia
     */
    private long sleepTime;
    /**
     * Cantidad de veces antes de terminar el ciclo
     * 0 para indefinido
     */
    private int nTimes;
    /**
     * Monitor de prueba
     */
    private Monitor monitor;

    PullDummyProcess_Single(DummyProcessRAW dp, Monitor monitor) {
        this.name = dp.name;
        this.seq = dp.seq;
        this.sleepTime = dp.sleepTime;
        this.nTimes = dp.nTimes;
        this.monitor = monitor;
    }

    @Override
    public void run() {
        Thread.currentThread().setName(this.name);
        for (int n = 0; n < this.nTimes || this.nTimes == 0; n++) {

            for (int i = 0; i < seq.length; i++) {
                try {
                    this.monitor.acquireProcedure(seq[i]);
                } catch (Exception e) {
                    System.out.print(e.toString());
                    System.exit(-5);
                }

            }
            if (this.sleepTime != 0)
                try {
                    Thread.sleep(this.sleepTime);
                } catch (InterruptedException e) {
                    System.exit(-1);
                }
        }


    }
}
