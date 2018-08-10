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
     * @TODO emplentar
     */
    public RDP(String fileMatrix) {

        Scanner input;
        int fila = 0;
        int colum = 0;

        try {
            input = new Scanner(new File(fileMatrix));


            while (input.hasNextLine()) {
                ++fila;
                Scanner colreader = new Scanner(input.nextLine());
                while (colreader.hasNextInt()) {
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

}