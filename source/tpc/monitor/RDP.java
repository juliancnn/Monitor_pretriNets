package tpc.monitor;

import java.lang.String;
import java.io.*;
import java.lang.*;
import java.util.*;

/**
 * Manejador de la red de petri
 * <p>
 * La clase se encarga de instanciar la red de petri con su marcado desde archivos.
 * Tiene la posibilidad de:
 * - Dispara las transiciones y alterar el estado de la red
 * - Informar las transiciones disponibles para disparar
 * - Informar si se puede disparar o no una transicion
 *
 * @WARNING No implementa ningun mecanismo de proteccion de recursos para hilos multiples (como semaforo),
 * debe ser implementado externamente
 * @TODO Implementar arcos lectores e inibidores
 * @TODO Implementar max tokens por plaza
 * @TODO Implementar las transiciones temporales
 */
public class RDP {
    /**
     * Matriz de la red de petri
     */
    private int[][] mRDP;
    /**
     * Marcador de la red de petri
     */
    private int[] mark;

    /**
     * Crea la red de petri a partir de un archivo
     * <p>
     * El constructor setea el marcador inicial y la rdp en base al archivo
     * El marcador debe ser una fila separado los valores de las plazas por espacios y numeros enteros positivos
     * El primer valor es el de la plaza 1, luego el de la plaza 2 y asi susecivamente sin saltear ninguna plaza
     * ej:
     * p1  p2 p3  p4  p5
     * 1  0   1   0  0
     * La matriz esta expresada por filas y columnas separadas por saltos de linea y espacios
     * Las filas estan separadas por saltos de linea y las columnas por espacios
     * La primera fila es de la primera plaza con el primer valor el de la transicion 1
     * ej:
     * t1  t2 t3 t4
     * -1  0  0  1  p1
     * 1 -1  0  0  p2
     * 0  1  0 -1  p3
     * 1  0  0 -1  p4
     * 0  0  1 -1  p5
     *
     * @param fileMatrix Nombre del archivo de la rdp
     * @param filemMark  Nombre del archivo de la marca
     * @TODO Chequear que la matriz tenga siempre la misma cantidad de columnas cuando se cargan
     */
    public RDP(String fileMatrix, String filemMark) {

        Scanner input;
        int fila = 0;
        int colum = 0;

        /* ********************************
         *       Carga del marcador
         * *********************************/
        try {
            input = new Scanner(new File(filemMark));

            // Cuantos valores tiene para setear el array
            Scanner colreader = new Scanner(input.nextLine());
            colum = 0;
            while (colreader.hasNextInt()) {
                colreader.nextInt();
                ++colum;
            }
            mark = new int[colum];
            input.close();
        } catch (java.util.InputMismatchException e) {
            System.out.println("El archivo del markado esta con datos invalidos");
            System.exit(-1);
        } catch (NoSuchElementException e) {
            System.out.println("El archivo del markado esta vacio");
            System.exit(-1);
        } catch (java.io.FileNotFoundException e) {
            System.out.print("No se encuentra el archivo del markado: " + filemMark);
            File miDir = new File(".");
            try {
                System.out.println("En el directorio actual: " + miDir.getCanonicalPath());
            } catch (Exception ef) {
                ef.printStackTrace();
            }

            System.exit(-1);

        }

        // Carga los valores del archivo
        try {
            input = new Scanner(new File(filemMark));

            for (int j = 0; j < colum; j++) {
                if (input.hasNextInt()) {
                    mark[j] = input.nextInt();
                }
            }

        } catch (java.util.InputMismatchException e) {
            System.out.println("El archivo del rdp contiene datos invalidos");
            System.exit(-1);
        } catch (NoSuchElementException e) {
            System.out.println("El archivo del rdp esta vacio");
            System.exit(-1);
        } catch (java.io.FileNotFoundException e) {
            System.out.print("No se encuentra el archivo de la rdp: " + filemMark);
            File miDir = new File(".");
            try {
                System.out.println("En el directorio actual: " + miDir.getCanonicalPath());
            } catch (Exception ef) {
                ef.printStackTrace();
            }

            System.exit(-1);

        }


        /* ********************************
         *       Carga del la RDP
         * *********************************/
        try {
            fila = 0;
            colum = 0;
            input = new Scanner(new File(fileMatrix));

            while (input.hasNextLine()) {
                ++fila;
                Scanner colreader = new Scanner(input.nextLine());
                colum = 0;
                while (colreader.hasNextInt()) {
                    colreader.nextInt();
                    ++colum;
                }

            }
            mRDP = new int[fila][colum];
            input.close();
        } catch (java.util.InputMismatchException e) {
            System.out.println(e.toString());
        } catch (java.io.FileNotFoundException e) {
            System.out.println(e.toString());

        }

        try {
            input = new Scanner(new File(fileMatrix));

            for (int i = 0; i < fila; i++) {
                for (int j = 0; j < colum; j++) {
                    if (input.hasNextInt()) {
                        mRDP[i][j] = input.nextInt();
                    }
                }
            }
        } catch (java.util.InputMismatchException e) {
            System.out.println(e.toString());
        } catch (java.io.FileNotFoundException e) {
            System.out.println(e.toString());
        }

        /* ********************************************************
         *    Chequeo de que el marcador corresponda a la RDP
         * ********************************************************/

        if (mRDP.length != mark.length) {
            System.out.println("El markador no coinside con la red de petri dada");
            this.printRDP();
            this.printMark();
            System.exit(-1);
        }


    }

    /**
     * Imprime la matriz de la red de petri
     */
    public void printRDP() {
        System.out.println("Matriz RDP: ");
        for (int i = 0; i < mRDP.length; i++) {
            for (int j = 0; j < mRDP[0].length; j++) {
                System.out.print(String.format("%5d", mRDP[i][j]));
            }
            System.out.print("\n");
        }
    }

    /**
     * Imprime el marcador actual de la RDP
     */
    public void printMark() {
        System.out.println("Vector Marca de  RDP: ");
        for (int i = 0; i < mark.length; i++) {
            System.out.print(String.format("%5d", mark[i]));
        }
        System.out.print("\n");
    }

    /**
     * Obtiene un array con el estado de marcado de la red de petri
     *
     * @return Una copia del array con el marcado actual del sistema
     */
    public int[] getMark() {
        return mark.clone();
    }

    /**
     * Realiza el disparo en la red de petri, este puede ser un disparo de prueba o puede guardar los resultados
     *
     * @param tDisp Numero de transicion a disparar
     * @param test  Falso si no quiere alterar el estado de la red de petri
     *              Verdadero si en caso de que se pueda disparar se altere el estado de la red
     * @return Verdadero en caso de exito en el disparo de la transicion
     * Falso en caso de que la transicion no este sencibilidaza
     * @throws ShotException Excepcion por inexistencia de la transicion
     */
    public boolean shotT(int tDisp, boolean test) throws ShotException {
        boolean validShot = true;

        int[] newMark = this.nextMark(tDisp); // Puede lanzar la exepcion de inexistencia de transicion
        // Validez del nuevo marcado
        for (int token : newMark) {
            if (token < 0) {
                validShot = false;
                break;
            }
        }

        // Marcado valido y salvo nueva marca
        if (!test && validShot) {
            this.mark = newMark;
        }

        return validShot;
    }

    /**
     * Retorna el vector de transiciones sensibilizadas, cada lugar del vector representa una transicion
     * donde el primer lugar corresponde a la primera transicion.
     * @return  Verdadero si la transicion esta sensibilizada
     *          Falso en caso contrario
     */
    public boolean[] getSensitizedArray() {
        boolean[] sensitizedArray = new boolean[mRDP[0].length];

        try{
            for (int i = 0; i < sensitizedArray.length; i++) {
                sensitizedArray[i] = this.shotT(i+1,true);
            }
        }catch (RDP.ShotException e){
            // No deberia pasar nunca ya que se testea solo la cantidad de transiciones disponibles segun la matriz
            e.printInfo();
        }


        return sensitizedArray;
    }

    /**
     * Resultado del disparo, sin alterar el marcado
     * <p>
     * <p>
     * La funcion retorna el resultado del disparo sin alterar el marcador
     * Sirve para verficiar si el disparo se puede efectuar (Marcador positivo)
     * o si el disparo no se puede efectuar, marcador negativo en algun valor
     * Nuevo mark = mark actual + RDP * Vector Disparo
     *
     * @param tDisp numero de transicion a disparar
     * @return vectorNextMark  Proxima marca, sea alcanzable o no.
     * @throws ShotException Excepcion por inexistencia de la transicion
     */

    private int[] nextMark(int tDisp) throws ShotException {
        // Si la transicion no existe lanza la excepcion
        if (tDisp > mRDP[0].length || tDisp < 1)
            throw new ShotException(this.mark, tDisp, this.mRDP[0].length);

        // Vector de disparo ()
        int[] vectorDisparo = new int[mRDP[0].length];
        vectorDisparo[tDisp - 1] = 1;
        // vector Proximo marcado
        int[] vectorNextMark = new int[mRDP.length];

        // Matriz * Vector Disparo Trans  + Mark = Mark
        // Recorro por filas
        for (int i = 0; i < mRDP.length; ++i) {
            // Recorro por columnas
            for (int j = 0; j < mRDP[0].length; ++j) {
                vectorNextMark[i] += mRDP[i][j] * vectorDisparo[j];
            }
            vectorNextMark[i] += mark[i];
        }
        /*
        System.out.println("\nVector disparo: ");
        for (Object o : vectorDisparo) {
            System.out.print(o + " ");
        }
        System.out.println("\nVector mark: ");
        for (Object o : vectorNextMark) {
            System.out.print(o + " ");
        }
        */
        return vectorNextMark;
    }

    /**
     * La excepcion se produce al intentar realizar un disparo invalido en la red de petri,
     * El disparo es invalido por que el numero de transicion es menor que 1 o mayor que el numero de
     * transiciones que tiene la red. Es decir cuando la transicion es inexistente
     */
    public class ShotException extends Exception {

        /**
         * Marca al momento del disparo
         */
        private final int[] marca;
        /**
         * Numero de transicion que se intento disparar
         */
        private final int tDisparo;
        /**
         * Cantidad de transiciones que tiene la red de petri
         */
        private final int nTrans;

        /**
         * Se crea con la informacion del estado del sistema en el momento del intento de disparo y el disparo fallido
         *
         * @param mark
         * @param tDisp
         */
        public ShotException(int[] mark, int tDisp, int cantidadTrans) {
            this.marca = mark;
            this.tDisparo = tDisp;
            this.nTrans = cantidadTrans;
        }

        /**
         * Obtiene la informacion de la marca al momento del disparo
         *
         * @return vector de disparo que fallo
         */
        public int[] getMarca() {
            return this.marca;
        }

        /**
         * Obtiene la informacion del disparo fallido
         *
         * @return El numero de transicion fallida
         */
        public int gettDisparo() {
            return this.tDisparo;
        }

        /**
         * Imprime la informacion del disparo fallido junto con el estado del sistema
         */
        public void printInfo() {
            System.out.println("Disparo fallido para la transicion: " + this.tDisparo);
            System.out.println("Marca del sistema al momento del fallo:");
            for (int o : this.marca) {
                System.out.print(String.format("%5d", o));
            }
            System.out.println("\nCantidad de transiciones de la RDP: " + this.nTrans);

        }

    }
}