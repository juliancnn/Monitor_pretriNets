package tpc.monitor;

import java.lang.String;
import java.lang.reflect.Array;
import java.util.Vector;

public class RDP {

  private int[][] mRDP;
  /**
   * @brief Matriz de la red de petri
   */
  private int[] mark; /** @brief Marcador de la red de petri */

  /**
   * @param fileMatrix Nombre del archivo
   *                   <p>
   *                   <p>
   *                   El constructor setea el marcador inicial en cero para todas las plazas
   *                   Las filas estan separadas por saltos de linea y las columnas por espacios
   * @brief Crea la red de petri a partir de un archivo
   * @TODO emplentar
   */
  public RDP(String fileMatrix) {

    mRDP = new int[][]
            {
                    {-1, 0, 0, 1},
                    {1, -1, 0, 0},
                    {0, 1, 0, -1},
                    {1, 0, 0, -1},
                    {0, 0, 1, -1}

            };
  }

  /**
   * @param nMark Array del markador
   * @brief Setea el marcador en base al array recibido
   * @TODO leerlo desde un archivo
   * @TODO Implementar las exepciones al leer el archivo
   */
  public boolean setMarkStatus(int[] nMark) {

    mark = new int[]{1, 0, 0, 0, 0};

    return false;
  }


}