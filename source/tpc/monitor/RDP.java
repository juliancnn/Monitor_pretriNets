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

    /**
     * @brief Resultado del disparo, sin alterar el marcado
     *
     * @param tDisp numero de transicion a disparar
     * @return vectorNextMark Proxima marca, sea alcanzable o no.
     * @TODO Agregar la exepcion si no existe la transicion
     */
    private int[] nextMark(int tDisp){
        // La transisicion no existe, debe largar una exepcion
        if(tDisp>mRDP[0].length){
            System.out.println("La transicion no existe");
        }
        // Vector de disparo ()
        int[] vectorDisparo = new int[mRDP[0].length];
        // vector Proximo marcado
        int[] vectorNextMark = new int[mRDP.length];

        // Matriz * Vector Trans = Mark trans
        // Recorro por filas
        for(int i=0;i<mRDP.length;++i){
            // Recorro por columnas
            for(int j=0;j<mRDP[0].length;++j){
                vectorNextMark[i] += mRDP[i][j] * vectorDisparo[j];
            }
        }

        return vectorNextMark;

    }
}