package jmml.monitor.complex_test;

import jmml.monitor.Monitor;

/**
 * Dimmy processes para hacer pruebas, disparan una secuencia de transiciones y duermen un rato
 */
public class dummyProcess implements Runnable{
    /**
     * Rango de tiempo en el que pueden dormir en dsp de cada disparo
     */
    private int[] timeSleepRange;
    /**
     * Cuando terminan la secuencia duermen un tiempo antes de volver a arrancar
     */
    private int[] timeSleepAfterCicle;
    /**
     * Ciclo de disparon a realizar
     */
    private int[] secShotCicle;
    /**
     * Cantidad de veces antes de terminar el ciclo
     */
    private int nTimes;
    /**
     * Monitor de prueba
     */
    private Monitor monitor;
    /**
     * Name
     */
    private String name;

    public dummyProcess(int[] timeSleepRange, int[] timeSleepAfterCicle, int[] secShotCicle,
                        int nTimes, Monitor monitor, String name) {
        this.timeSleepRange = timeSleepRange;
        this.timeSleepAfterCicle = timeSleepAfterCicle;
        this.secShotCicle = secShotCicle;
        this.monitor = monitor;
        this.nTimes = nTimes;
        this.name = name;
    }

    @Override
    public void run() {
        int sleepTime, sleepAfterCicle;
        int nSec = 0;
        String msj = "";
        for(int n=0; n < this.nTimes; n++){
            msj = "["+Thread.currentThread().getId()+"]["+name+"]   " + System.currentTimeMillis() + " -> ";
            sleepAfterCicle = (int) (Math.random() * timeSleepRange[1]) + timeSleepRange[0];
            try {
                System.out.println(msj+ "sleep fin de ciclo " + sleepAfterCicle);
                Thread.sleep(sleepAfterCicle);
            }catch (InterruptedException e){
                System.exit(-1);
            }

            for(int i=0;i<secShotCicle.length;i++){
                System.out.println(msj+ "Intentidisparar " + secShotCicle[i]);
                msj = "["+Thread.currentThread().getId()+"]["+name+"]   " + System.currentTimeMillis() + " -> ";
                System.out.println(msj+ "Dispare         " + secShotCicle[i]);
                sleepTime = (int) (Math.random() * timeSleepRange[1]) + timeSleepRange[0];
                try {
                    msj = "["+Thread.currentThread().getId()+"]["+name+"]   " + System.currentTimeMillis() + " -> ";
                    System.out.println(msj+ "sleep fin de disp " + sleepAfterCicle);
                    Thread.sleep(sleepTime);
                }catch (InterruptedException e){
                    System.exit(-1);
                }
            }
        }
        msj = "["+Thread.currentThread().getId()+"]["+name+"]   " + System.currentTimeMillis() + " -> ";
        System.out.println(msj+ " TERMINO  ");



    }
}
