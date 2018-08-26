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
 * @TODO Implementar metodo protect boolean(Puede o no) setTokens(plaza, cantidad>0): Exepcion si la plaza no existe
 * Exepcion si la cantidad es invalida (<1)
 * @TODO Implementar arcos lectores e inibidores
 * @TODO Implementar las transiciones temporales
 *
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
     * [Feature: Red de petri extendida]:  Indica si  hay un maximo de token por plaza
     */
    private boolean extendedMaxToken = false;
    /**
     * [Feature: Red de petri extendida]:  Vector de maximo de tokens por plaza
     */
    private int[] extMaxToken;

    /**
     * Lista de errores de configuracion al cargar la red de petri
     * */
    protected enum errorTypeConfig {
        /**
         * El marcado maximo para cada plaza debe ser mayor a cero
         * */
        invalidMaxToken,
        /**
         * La cantidad la cantidad de datos es invalida o mal ordenada
         * */
        invalidFormatArray,
        invalidFormatMatrix
    }

    /**
     * Crea la red de petri a partir de un archivo
     * <p>
     * El constructor setea el marcador inicial y la rdp en base a archivos. Opcionalmente el marcacado tambien setea
     * un maximo de tokens por plaza, extendiendo la funcionaldidad de la red.
     * El archivo del marcador debe ser una fila separado los valores de las plazas por espacios y numeros enteros
     * positivos.
     * El primer valor es el de la plaza 1, luego el de la plaza 2 y asi susecivamente sin saltear ninguna plaza.
     * Opcionalmente puede tener una segunda indicando que la red de petri es extendida con la funcionalidad de un
     * maximo de tokens por plaza. Donde la plaza no tenga limite correspondera un 0.
     * ej para un marcado de 5 plaazas inicial donde p1 tendra 3 tokens y solo la plaza 2 tendra un maximo de 2 tokens,
     * el archivo tiene la siguiente estructura (Salta la primera linea):
     * p1  p2 p3  p4 p5
     * 3  0   0   0  0
     * 0  2   0   0  0
     * <p>
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
     */
    public RDP(String fileMatrix, String filemMark) throws ConfigException{

        Scanner inputFile;
        int filas = 0;
        int columnas = 0;
        int columAux = 0;

        /* ********************************
         *       Carga del marcador
         * *********************************/
        try {
            // Buscamos cuantos valores tenemos que setear en el array
            // No usamos array list por que es mucho mas lento, ocupa mas memoria y es ineficiente en los calculos
            inputFile = new Scanner(new File(filemMark));
            Scanner colReader = new Scanner(inputFile.nextLine());

            columnas = 0;
            while (colReader.hasNextInt()) {
                colReader.nextInt();
                ++columnas;
            }

            inputFile.close();
            this.mark = new int[columnas];

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
            inputFile = new Scanner(new File(filemMark));
            Scanner markInput = new Scanner(inputFile.nextLine());

            for (int j = 0; j < columnas && markInput.hasNextInt(); j++) {
                this.mark[j] = markInput.nextInt();
            }
            if (inputFile.hasNextLine()) {
                markInput = new Scanner(inputFile.nextLine());
                this.extendedMaxToken = true;
                this.extMaxToken = new int[columnas];
                int j;

                for (j = 0; j < columnas && markInput.hasNextInt(); j++) {
                    this.extMaxToken[j] = markInput.nextInt();
                    if( this.extMaxToken[j] < 0 ){
                        throw new ConfigException("Numeros negativos en limite marcadores maximos",
                                errorTypeConfig.invalidMaxToken);
                    }
                }
                if (j != columnas) {
                    throw new ConfigException("Mal formado el marcador de maximos, " +
                            "no pusee la cantidad de plaazas correcta",
                            errorTypeConfig.invalidFormatArray);
                }
            }
            inputFile.close();

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


        /* ********************************
         *       Carga del la RDP
         * *********************************/
        try {
            filas = 0;
            columnas = 0;
            columAux = 0;
            inputFile = new Scanner(new File(fileMatrix));

            while (inputFile.hasNextLine()) {
                ++filas;
                Scanner colreader = new Scanner(inputFile.nextLine());
                columnas = 0;
                while (colreader.hasNextInt()) {
                    colreader.nextInt();
                    ++columnas;
                }
                if(filas == 1)
                {
                    columAux = columnas;
                }
                else if(columAux != columnas)
                {
                    throw new ConfigException("La cantidad de columnas no es constante", errorTypeConfig.invalidFormatMatrix);
                }

            }
            mRDP = new int[filas][columnas];
            inputFile.close();
        } catch (java.util.InputMismatchException e) {
            System.out.println(e.toString());
        } catch (java.io.FileNotFoundException e) {
            System.out.println(e.toString());

        }

        try {
            inputFile = new Scanner(new File(fileMatrix));

            for (int i = 0; i < filas; i++) {
                for (int j = 0; j < columnas; j++) {
                    if (inputFile.hasNextInt()) {
                        mRDP[i][j] = inputFile.nextInt();
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
        for (int i = 0; i < mRDP.length; i++) {
            /* Cabezera de tabla */
            if (i == 0) {
                String line = new String("");
                for (int j = 0; j < mRDP[0].length; j++) {
                    System.out.print(String.format("%5s", "T" + (j + 1)));
                    line += "------";
                }
                System.out.print("\n" + line + "\n");
            }
            for (int j = 0; j < mRDP[0].length; j++) {
                System.out.print(String.format("%5d", mRDP[i][j]));
            }
            System.out.println(String.format(" |%4s", "P" + (i + 1)));
        }
    }

    /**
     * Imprime el marcador actual de la RDP
     */
    public void printMark() {
        for (int i = 0; i < mark.length; i++) {
            System.out.print(String.format("%5s", "P" + (i + 1)));
        }
        System.out.print("\n");
        for (int i = 0; i < mark.length; i++) {
            System.out.print(String.format("%5d", mark[i]));
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
     *
     * @return Una copia del array con el marcado actual del sistema
     */
    public int[] getMark() {
        return this.mark.clone();
    }


    /**
     * Obtiene un array con la informacion de maximos toquens por plaza
     *
     * @return Una copia del array con el marcado maximo por plaza
     *          Null Si no es extendida la red
     */
    public int[] getExtMaxToken() {
        return this.extendedMaxToken ? this.extMaxToken.clone() : null;
    }

    /**
     * Obtiene la matriz de la red de petri
     *
     * @return Devuelve una copia de la matriz de la red de petri
     */
    public int[][] getMatrix() {
        return this.mRDP.clone();
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

        for(int i=0; i<newMark.length; i++)
        {
            if(newMark[i] < 0 )
            {
                validShot = false;
                break;
            }
            else if(this.extendedMaxToken){
                 if(extMaxToken[i] != 0 && newMark[i] > extMaxToken[i])
                {
                    validShot = false;
                    break;
                }
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
     *
     * @return Verdadero si la transicion esta sensibilizada
     * Falso en caso contrario
     */
    public boolean[] getSensitizedArray() {
        boolean[] sensitizedArray = new boolean[mRDP[0].length];

        try {
            for (int i = 0; i < sensitizedArray.length; i++) {
                sensitizedArray[i] = this.shotT(i + 1, true);
            }
        } catch (RDP.ShotException e) {
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

        return vectorNextMark;
    }

    /**
     * Metodo encargado de agregar tokens a determinada plaza.
     * Devolvera verdadero en caso de que se puedan agregar dichos tokens
     * o falso en caso contrario.
     * @param Plaz plaza que se quiere agregar token
     * @param cant numero entero de tokens a agregar
     */
     protected boolean AddToken(int Plaz, int cant) throws TokenException {

         boolean agregar;
         //Si la plaza no existe lanza la execepcion
         if(Plaz> mRDP.length || Plaz <= 0){
             throw new TokenException(this.mark, Plaz+1, mRDP.length, cant);
         //Si la cantidad a agregar es negativa lanza la excepcion
         }else if(cant<0){
             throw new TokenException(this.mark, Plaz+1, mRDP.length, cant);
         //Si la cantidad es mayor al limite de la plaza devuelve un false
         }else if(cant + this.mark[Plaz-1] > this.extMaxToken[Plaz-1] && this.extMaxToken[Plaz-1] != 0){
             agregar = false;
             return agregar;
         }
         //Modifico el vector de marcado y devuelvo true
         this.mark[Plaz-1] = cant + this.mark[Plaz-1];
         agregar = true;
         return agregar;

     }
    /**
     * Metodo encargado de agregar tokens a determinada plaza.
     * Devolvera verdadero en caso de que se puedan agregar dichos tokens
     * o falso en caso contrario. Principal diferencia con respecto a AddTokens
     * es que no tiene en cuenta los Tokens que se encuentran en dicha plaza, estos
     * son reemplazados por el numero especifico a agregar.
     * @param Plaz plaza que se quiere agregar token
     * @param Cant numero entero de tokens a agregar
     */
    protected boolean SetToken(int Plaz, int Cant) throws TokenException {

        boolean agreg;
        //Si la plaza no existe lanza la execepcion
        if(Plaz> mRDP.length || Plaz <= 0){
            throw new TokenException(this.mark, Plaz+1, mRDP.length, Cant);
            //Si la cantidad a agregar es negativa lanza la excepcion
        }else if(Cant<0){
            throw new TokenException(this.mark, Plaz+1, mRDP.length, Cant);
            //Si la cantidad es mayor al limite de la plaza devuelve un false
        }else if(Cant + this.mark[Plaz-1] > this.extMaxToken[Plaz-1] && this.extMaxToken[Plaz-1] != 0){
            agreg = false;
            return agreg;
        }
        //Modifico el vector de marcado y devuelvo true
        this.mark[Plaz-1] = Cant;
        agreg = true;
        return agreg;

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
         * @param mark recibe el estado de la RDP al momento de la excepcion
         * @param tDisp numero de transicion a disparar
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

    /**
     * La excepcion se produce al intentar agregar un token a una plaza inexistente.
     *
     */
    public class TokenException extends Exception {
        /**
         * Cantidad de tokens que se quieren agregar.
         */
        private final int Cantidad;
        /**
         * Plaza a la cual se quizo agregar el/los token.
         */
        private final int tPlaza;
        /**
         * Cantidad de plazas que tiene la red de petri
         */
        private final int nPlaza;
        /**
         * Marca al momento de agregar token
         */
        private final int[] marca;
        /**
         * Se crea con la informacion del estado del sistema en el momento del intento de disparo y el disparo fallido
         *
         * @param mark recibe el estado de la RDP al momento de la excepcion
         * @param tPlaz numero de plaza que se quiso agregar token/s
         * @param nPla numero de plazas existentes en la RDP
         * @param Cant token/s que se quiso agregar
         */
        public TokenException(int[] mark, int tPlaz, int nPla, int Cant) {
            this.marca = mark;
            this.tPlaza = tPlaz;
            this.Cantidad = Cant;
            this.nPlaza = nPla;
        }
        /**
         * Obtiene la informacion de la marca al momento del agregado
         *
         * @return vector de marca al momento del fallo
         */
        public int[] gettMarca() {
            return this.marca;
        }
        /**
         * Obtiene la informacion de la plaza que no se pudo incrementar
         * su cantidad de tokens
         *
         * @return El numero de plaza fallida
         */
        public int getDisparo() {
            return this.tPlaza;
        }
        /**
         * Imprime la informacion del agregado fallido junto con el estado del sistema
         */
        public void printInfo2() {
            System.out.println("Agregado fallido en la plaza: " + this.tPlaza);
            System.out.println("Se intento agregar " + this.Cantidad + " de Token/s");
            System.out.println("Marca del sistema al momento del fallo:");
            for (int o : this.marca) {
                System.out.print(String.format("%5d", o));
            }
            System.out.println("\nCantidad de Plazas de la RDP: " + this.nPlaza);

        }

    }

    /**
     * La excepcion se produce por errores en la carga de datos,
     * como datos invalidos y falta de datos
     * */
    public class ConfigException extends Exception{

        private String moreInfo;
        private errorTypeConfig e;


        public ConfigException(String description, errorTypeConfig e){
            this.moreInfo = description;
            this.e = e;
        }
        /**
         * Imprime la informacion del disparo fallido junto con el estado del sistema
         */
        public void printInfo(){
            System.out.println("[ERROR " + e + "] " +moreInfo);
        }
    }
}