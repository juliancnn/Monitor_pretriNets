package tpc.monitor.rdp;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.String;


/**
 * Manejador de la red de petri
 * <pre>
 *
 * La clase se encarga de instanciar la red de petri con todas sus caracteristicas desde una archivo JSON
 *
 * Tiene la posibilidad de:
 *  - Dispara las transiciones y alterar el estado de la red
 *  - Informar las transiciones disponibles para disparar
 *  - Informar si se puede disparar o no una transicion
 *
 * Soporta caracteristicas de redes de petri extendidas como:
 *  - Maxima cantidad de tokens por plaza
 *  - Arcos inhibidores y lectores (Future support)
 *  - Transiciones sensibilizadas temporalmente (Future support)
 *
 * </pre>
 *
 * @WARNING No implementa ningun mecanismo de proteccion de recursos para hilos multiples (como semaforo),
 * debe ser implementado externamente
 * @TODO Implementar arcos lectores e inibidores
 * @TODO Implementar las transiciones temporales
 */
public class RDP {
    /**
     * Contiene toda la configuracion de la red de petri y su estado.
     */
    private rawRDP raw;

    /**
     * Crea la red de petri a partir de un archivo
     * <p>
     * El constructor setea el marcador inicial, la rdp y su configuracion en base a un archivo json de la
     * misma estructura de rawRDP. (Vease rawRDP para ver la estructura completa del archivo)
     * <pre>
     *   Estructura del JSON:
     *
     *    {
     *     "brief" : "Un breve descripcion de la red"
     *     "info"  : "Una descripcion mas detallada de la red",
     *     "matrixW" : [                             # La matriz de doble incidencia
     *       [-1, 0, 0, 1],
     *       [1, -1, 0, 0],
     *       [0, 1, 0, -1],
     *       [1, 0, -1, 0],
     *       [0, 0, 1, -1]
     *     ],
     *     "mark"     : [3, 0, 0, 0, 0],            # marcado inicial de la red
     *   }
     * </pre>
     *
     * @param jsonFile Nombre del archivo JSON que contiene la informacion
     * @throws FileNotFoundException Lanzado cuando no se encuentra el archivo JSON
     * @see rawRDP Ver estructura completa del JSON
     */
    public RDP(String jsonFile) throws FileNotFoundException {

        Gson json = new Gson();
        JsonReader reader = new JsonReader(new FileReader(jsonFile));
        raw = json.fromJson(reader, rawRDP.class);


    }

    /**
     * Imprime la matriz de la red de petri
     */
    public void printRDP() {
        for (int i = 0; i < raw.matrixW.length; i++) {
            /* Cabezera de tabla */
            if (i == 0) {
                String line = new String("");
                for (int j = 0; j < raw.matrixW[0].length; j++) {
                    System.out.print(String.format("%5s", "T" + (j + 1)));
                    line += "------";
                }
                System.out.print("\n" + line + "\n");
            }
            for (int j = 0; j < raw.matrixW[0].length; j++) {
                System.out.print(String.format("%5d", raw.matrixW[i][j]));
            }
            System.out.println(String.format(" |%4s", "P" + (i + 1)));
        }
    }

    /**
     * Imprime el marcador actual de la RDP
     */
    public void printMark() {
        for (int i = 0; i < raw.mark.length; i++) {
            System.out.print(String.format("%5s", "P" + (i + 1)));
        }
        System.out.print("\n");
        for (int i = 0; i < raw.mark.length; i++) {
            System.out.print(String.format("%5d", raw.mark[i]));
        }
        System.out.print("\n");
    }

    /**
     * Imprime el vector de sensibilizado de la red en el estado actual
     */

    public void printSensitizedVector() {

        boolean[] sense = this.getSensitizedArray();
        for (int i = 0; i < sense.length; i++) {
            System.out.print(String.format("%5s", "T" + (i + 1)));
        }
        System.out.print("\n");
        for (int i = 0; i < sense.length; i++) {
            System.out.print(String.format("%5s", sense[i] ? "SI" : "NO"));
        }
        System.out.print("\n");

    }

    /**
     * Obtiene un array con el estado de marcado de la red de petri
     * @return Una copia del array con el marcado actual del sistema
     */
    public int[] getMark() {
        return raw.mark.clone();
    }


    /**
     * Obtiene un array con la informacion de maximos toquens por plaza
     * <pre>
     * @return Una copia del array con el marcado maximo por plaza. <br>
     *         Null Si no es extendida la red
     * </pre>
     */
    public int[] getExtMaxToken() {
        return  (raw.extMaxToken != null) ? raw.extMaxToken.clone() : null;
    }

    /**
     * Consulta si la red de petri es extendida para maxima cantidad de tokens
     * <pre>
     * @return true:  Si estan limitadas las plazas a un numero maximo de tokens <br>
     *         false: Caso contrario
     * </pre>
     */
    public boolean isExtMaxToken() {
        return  (raw.extMaxToken != null);
    }

    /**
     * Obtiene la matriz de la red de petri
     *
     * @return Devuelve una copia de la matriz de la red de petri
     */
    public int[][] getMatrix() {
        return this.raw.matrixW.clone();
    }

    /**
     * Realiza el disparo en la red de petri, este puede ser un disparo de prueba o puede guardar los resultados
     *
     * @param tDisp Numero de transicion a disparar
     * @param test  Falso si no quiere alterar el estado de la red de petri
     *              Verdadero si en caso de que se pueda disparar se altere el estado de la redes
     * @return Verdadero en caso de exito en el disparo de la transicion
     * Falso en caso de que la transicion no este sencibilidaza
     * @throws ShotException Excepcion por inexistencia de la transicion
     * @TODO Verificar antes de diparar que: la transicion exista, que no este inhbida o desabilitada por arcos lectores
     */
    public boolean shotT(int tDisp, boolean test) throws ShotException {
        boolean validShot = true;

        int[] newMark = this.nextMark(tDisp); // Puede lanzar la exepcion de inexistencia de transicion

        for (int i = 0; i < newMark.length; i++) {
            if (newMark[i] < 0) {
                validShot = false;
                break;
            } else if ( (raw.extMaxToken != null)) {
                if (raw.extMaxToken[i] != 0 && newMark[i] > raw.extMaxToken[i]) {
                    validShot = false;
                    break;
                }
            }
        }
        // Marcado valido y salvo nueva marca
        if (!test && validShot) {
            raw.mark = newMark;
        }

        return validShot;
    }

    /**
     * Retorna el vector de transiciones sensibilizadas, cada lugar del vector representa una transicion
     * donde el primer lugar corresponde a la primera transicion.
     *
     * @return Verdadero si la transicion esta sensibilizada
     * Falso en caso contrario
     */
    public boolean[] getSensitizedArray() {
        boolean[] sensitizedArray = new boolean[raw.matrixW[0].length];

        try {
            for (int i = 0; i < sensitizedArray.length; i++) {
                sensitizedArray[i] = this.shotT(i + 1, true);
            }
        } catch (ShotException e) {
            // No deberia pasar nunca ya que se testea solo la cantidad de transiciones disponibles segun la matriz
            e.printInfo();
        }


        return sensitizedArray;
    }

    /**
     * Resultado del disparo, sin alterar el marcado
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
        if (tDisp > raw.matrixW[0].length || tDisp < 1)
            throw new ShotException(raw.mark, tDisp, this.raw.matrixW[0].length);

        // Vector de disparo ()
        int[] vectorDisparo = new int[raw.matrixW[0].length];
        vectorDisparo[tDisp - 1] = 1;
        // vector Proximo marcado
        int[] vectorNextMark = new int[raw.matrixW.length];

        // Matriz * Vector Disparo Trans  + Mark = Mark
        // Recorro por filas
        for (int i = 0; i < raw.matrixW.length; ++i) {
            // Recorro por columnas
            for (int j = 0; j < raw.matrixW[0].length; ++j) {
                vectorNextMark[i] += raw.matrixW[i][j] * vectorDisparo[j];
            }
            vectorNextMark[i] += raw.mark[i];
        }

        return vectorNextMark;
    }


    /**
     * Metodo encargado de agregar tokens a determinada plaza.
     * <p>
     * Si la plaza tiene un maximo de tokens y esta llena no los agregara
     * Si se intentan agregar mas tokens y el resultado final sobrepasa la cantidad
     * maxima no agrega ninguno
     *
     * @param Plaz plaza que se quiere agregar token
     * @param cant numero entero de tokens a agregar
     * @return boolean True: dadero en caso de que se puedan agregar dichos tokens
     * False: Caso contrario
     * @throws TokenException producida por inexistencia de la plaza o una cantidad negativa de tokens
     */
    protected boolean AddToken(int Plaz, int cant) throws TokenException {

        boolean agregar;
        //Si la plaza no existe lanza la execepcion
        if (Plaz > raw.matrixW.length || Plaz <= 0) {
            throw new TokenException(raw.mark, Plaz + 1, raw.matrixW.length, cant);
            //Si la cantidad a agregar es negativa lanza la excepcion
        } else if (cant < 0) {
            throw new TokenException(raw.mark, Plaz + 1, raw.matrixW.length, cant);
            //Si la cantidad es mayor al limite de la plaza devuelve un false
        } else if (cant + raw.mark[Plaz - 1] > raw.extMaxToken[Plaz - 1] && raw.extMaxToken[Plaz - 1] != 0) {
            agregar = false;
            return agregar;
        }
        //Modifico el vector de marcado y devuelvo true
        raw.mark[Plaz - 1] = cant + raw.mark[Plaz - 1];
        agregar = true;
        return agregar;

    }

    /**
     * Metodo encargado de setear tokens a determinada plaza.
     *
     * @param Plaz plaza que se quiere agregar token
     * @param Cant numero entero de tokens a agregar
     * @return True en caso de que se puedan agregar dichos tokens
     * Falso en caso contrario (debido a que no tiene lugar suficiente la plaza)
     * @throws TokenException producida por inexistencia de la plaza o una cantidad negativa de tokens
     *                        Principal diferencia con respecto a AddTokens es que no tiene en cuenta los Tokens que se encuentran en
     *                        dicha plaza, estos son reemplazados por el numero especifico a agregar.
     */
    protected boolean SetToken(int Plaz, int Cant) throws TokenException {

        boolean agreg;
        //Si la plaza no existe lanza la execepcion
        if (Plaz > raw.matrixW.length || Plaz <= 0) {
            throw new TokenException(raw.mark, Plaz + 1, raw.matrixW.length, Cant);
            //Si la cantidad a agregar es negativa lanza la excepcion
        } else if (Cant < 0) {
            throw new TokenException(raw.mark, Plaz + 1, raw.matrixW.length, Cant);
            //Si la cantidad es mayor al limite de la plaza devuelve un false
        } else if (Cant > raw.extMaxToken[Plaz - 1] && raw.extMaxToken[Plaz - 1] != 0) {
            agreg = false;
            return agreg;
        }
        //Modifico el vector de marcado y devuelvo true
        raw.mark[Plaz - 1] = Cant;
        agreg = true;
        return agreg;

    }

}