package jmml.monitor.complex_test;

class DummyProcessRAW {
    /**
     * Nombre del thread
     */
    String name;
    /**
     * Ciclo de disparos a realizar
     */
    int[] seq;
    /**
     * Tiempo a dormir despues de la secuencia
     */
    long sleepTime;
    /**
     * Cantidad de veces antes de terminar el ciclo
     */
    int nTimes;
}
