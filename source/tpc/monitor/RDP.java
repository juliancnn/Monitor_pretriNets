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
     * @param fileMatrix Nombre del archivo
     * @brief Crea la red de petri a partir de un archivo
     * <p>
     * <p>
     * El constructor setea el marcador inicial en cero para todas las plazas
     * Las filas estan separadas por saltos de linea y las columnas por espacios
     * @TODO ver el tema de excepciones si no existe el archivo
     * @TODO ver si el archivo no tiene una red de petri valida
     * @TODO controlar que el largo del vector de marcadores sea igual a la cantidad de plazas de la RDP
     */
    public RDP(String fileMatrix, String filemMark) {

        Scanner input;
        int fila = 0;
        int colum = 0;

        try {
            input = new Scanner(new File(filemMark));


            Scanner colreader = new Scanner(input.nextLine());
            colum = 0;
            while (colreader.hasNextInt()) {
                colreader.nextInt();
                ++colum;
            }

            mark = new int[colum];
            input.close();

        } catch (java.util.InputMismatchException e) {
            System.out.println(e.toString());
        } catch (java.io.FileNotFoundException e) {
            System.out.println(e.toString());

        }

        try {
            input = new Scanner(new File(filemMark));

            for (int j = 0; j < colum; j++) {
                if (input.hasNextInt()) {
                    mark[j] = input.nextInt();
                }
            }

        } catch (java.util.InputMismatchException e) {
            System.out.println(e.toString());
        } catch (java.io.FileNotFoundException e) {
            System.out.println(e.toString());
        }


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

    }

    /**
     * @brief imprime la matriz de la red de petri
     */
    public void printRDP() {
        for (int i = 0; i < mRDP.length; i++) {
            for (int j = 0; j < mRDP[0].length; j++) {
                System.out.print(mRDP[i][j] + " ");
            }
            System.out.print("\n");
        }
    }

    /**
     * @brief imprime el marcador actual de la RDP
     */
    public void printMark() {
        for (int i = 0; i < mark.length; i++) {

            System.out.print(mark[i] + " ");
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
     * @TODO Agregar la exepcion si no existe la transicion
     */
    private int[] nextMark(int tDisp) {
        // La transisicion no existe, debe largar una exepcion
        if (tDisp > mRDP[0].length) {
            System.out.println("La transicion no existe");
        }
        // Vector de disparo ()
        int[] vectorDisparo = new int[mRDP[0].length];
        // vector Proximo marcado
        int[] vectorNextMark = new int[mRDP.length];

        // Matriz * Vector Trans = Mark trans
        // Recorro por filas
        for (int i = 0; i < mRDP.length; ++i) {
            // Recorro por columnas
            for (int j = 0; j < mRDP[0].length; ++j) {
                vectorNextMark[i] += mRDP[i][j] * vectorDisparo[j];
            }
        }

        return vectorNextMark;

    }
}