package jmml.monitor.complex_test;

import jmml.monitor.Monitor;

public class ej6 {

    public ej6(Monitor monitor) {
        //escritor
        int[] s1 = new int[]{100, 500};
        int[] s2 =new int[]{2000, 5000};
        int[] sec =new int[]{1, 3, 5};
        int[] secEsc =new int[]{2, 4, 6};
        (new Thread(new dummyProcess(s1,s2,sec,20,monitor,"Escritor"))).start();
        (new Thread(new dummyProcess(s1,s2,sec,20,monitor,"Escritor"))).start();
        (new Thread(new dummyProcess(s1,s2,sec,20,monitor,"Escritor"))).start();
        (new Thread(new dummyProcess(s1,s2,sec,20,monitor,"Escritor"))).start();
        (new Thread(new dummyProcess(s1,s2,secEsc,20,monitor,"Lector  "))).start();
        (new Thread(new dummyProcess(s1,s2,secEsc,20,monitor,"Lector  "))).start();
        (new Thread(new dummyProcess(s1,s2,secEsc,20,monitor,"Lector  "))).start();
        (new Thread(new dummyProcess(s1,s2,secEsc,20,monitor,"Lector  "))).start();
        (new Thread(new dummyProcess(s1,s2,secEsc,20,monitor,"Lector  "))).start();
        (new Thread(new dummyProcess(s1,s2,secEsc,20,monitor,"Lector  "))).start();
        (new Thread(new dummyProcess(s1,s2,secEsc,20,monitor,"Lector  "))).start();


    }
}
