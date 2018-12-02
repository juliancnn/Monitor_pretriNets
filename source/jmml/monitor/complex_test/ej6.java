package jmml.monitor.complex_test;

import jmml.monitor.Monitor;

/**
 * @TODO Documentar esto
 * @TODO Revisar cuando este lo del loggeo de todo, cambiar print con Loggeo.
 */
public class ej6 {

    public ej6(Monitor monitor) {
        //escritor
        int[] s1 = new int[]{100, 500};
        int[] s2 =new int[]{2000, 5000};
        int[] sec =new int[]{1, 3, 5};
        int[] secEsc =new int[]{2, 4, 6};
        Thread t1 = (new Thread(new dummyProcess(s1,s2,sec,20,monitor,"Escritor")));
        Thread t2 = (new Thread(new dummyProcess(s1,s2,sec,20,monitor,"Escritor")));
        Thread t3 = (new Thread(new dummyProcess(s1,s2,sec,20,monitor,"Escritor")));
        Thread t4 = (new Thread(new dummyProcess(s1,s2,sec,20,monitor,"Escritor")));
        Thread t5 = (new Thread(new dummyProcess(s1,s2,secEsc,20,monitor,"Lector  ")));
        Thread t6 = (new Thread(new dummyProcess(s1,s2,secEsc,20,monitor,"Lector  ")));
        Thread t7 = (new Thread(new dummyProcess(s1,s2,secEsc,20,monitor,"Lector  ")));
        Thread t8 = (new Thread(new dummyProcess(s1,s2,secEsc,20,monitor,"Lector  ")));
        Thread t9 = (new Thread(new dummyProcess(s1,s2,secEsc,20,monitor,"Lector  ")));
        Thread t10 = (new Thread(new dummyProcess(s1,s2,secEsc,20,monitor,"Lector  ")));
        Thread t11 = (new Thread(new dummyProcess(s1,s2,secEsc,20,monitor,"Lector  ")));
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
        t6.start();
        t7.start();
        t8.start();
        t9.start();
        t10.start();
        t11.start();
        try {
            t1.join();
            t2.join();
            t3.join();
            t4.join();
            t5.join();
            t6.join();
            t7.join();
            t8.join();
            t9.join();
            t10.join();
            t11.join();
        }catch (Exception e){
            System.exit(-4);
        }
        monitor.closeLog();

    }
}
