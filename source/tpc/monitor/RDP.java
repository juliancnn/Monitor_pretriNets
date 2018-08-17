package tpc.monitor;

import java.lang.String;
import java.io.*;
import java.lang.*;
import java.util.*;


public class RDP {
    /**
     * @brief Matriz de la red de petri
     */
    private int[][] mRDP;
    /**
     * @brief Marcador de la red de petri
     */
    private int[] mark;

    /**
     * @param fileMatrix Nombre del archivo de la rdp
     * @param filemMark  Nombre del archivo de la marca
     * @brief Crea la red de petri a partir de un archivo
     * <p>
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
     * @brief imprime la matriz de la red de petri
     */
    public void printRDP() {
        System.out.println("Matriz RDP: ");
        for (int i = 0; i < mRDP.length; i++) {
            for (int j = 0; j < mRDP[0].length; j++) {
                System.out.print(String.format("%5d",mRDP[i][j]));
            }
            System.out.print("\n");
        }
    }

    /**
     * @brief imprime el marcador actual de la RDP
     */
    public void printMark() {
        System.out.println("Vector Marca de  RDP: ");
        for (int i = 0; i < mark.length; i++) {
            System.out.print(String.format("%5d",mark[i]));
        }
        System.out.print("\n");

    }

    /**
     * @param tDisp numero de transicion a disparar
     * @return vectorNextMark Proxima marca, sea alcanzable o no.
     * @brief Resultado del disparo, sin alterar el marcado
     * <p>
     * <p>
     * La funcion retorna el resultado del disparo sin alterar el marcador
     * Sirve para verficiar si el disparo se puede efectuar (Marcador positivo)
     * o si el disparo no se puede efectuar, marcador negativo en algun valor
     * Nuevo mark = mark actual + RDP * Vector Disparo
     * @TODO Agregar la exepcion si no existe la transicion
     */
    private int[] nextMark(int tDisp) {
        // La transisicion no existe, debe largar una exepcion
        if (tDisp > mRDP[0].length || tDisp < 1 ) {
            System.out.println("La transicion no existe");
            System.exit(-1);
        }
        // Vector de disparo ()
        int[] vectorDisparo = new int[mRDP[0].length];
        vectorDisparo[tDisp-1] = 1;
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
}