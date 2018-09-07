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
 *  - Informar marcado de la red y la red misma.
 *
 * Soporta caracteristicas de redes de petri extendidas como:
 *  - Maxima cantidad de tokens por plaza
 *  - Arcos inhibidores y lectores
 *  - Transiciones sensibilizadas temporalmente
 *
 * </pre>
 *
 * @WARNING No implementa ningun mecanismo de proteccion de recursos para hilos multiples (como semaforo),
 * debe ser implementado externamente
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
     * @throws ConfigException      Error de configuracion del JSON, mal formado los datos
     * @see rawRDP Ver estructura completa del JSON
     */
    public RDP(String jsonFile) throws FileNotFoundException, ConfigException {

        /* INICIO DE CARGA DE DATOS */
        Gson json = new Gson();
        JsonReader reader = new JsonReader(new FileReader(jsonFile));
        raw = json.fromJson(reader, rawRDP.class);
        /* FIN DE CARGA DE DATOS */


        /* =========================================================
            Chequeos de datos validos de estructura del archivo JSON.
           ========================================================= */

        /* Chequeo de estructura JSON.  */
        if (raw.matrixW == null || raw.mark == null) {
            throw new ConfigException("Error en la estructura JSON", errorTypeConfig.missingDataInJASON);
        }

        /* Chequeo de longuitud de matriz constante. */
        int conlconst = -1;
        for (int i = 0; i < raw.matrixW.length; ++i) {
            if (conlconst == -1) {
                conlconst = raw.matrixW[0].length;
            } else if (conlconst != raw.matrixW[i].length) {
                throw new ConfigException("La matriz de insidencia no es constante", errorTypeConfig.invalidFormatMatrix);
            }
        }

        /* Chequeo de loguitud del vector y elementos positivos */
        if (raw.matrixW.length != raw.mark.length) {
            throw new ConfigException("La cantidad de plazas  de marcado no es correcta", errorTypeConfig.invalidFormatArray);
        } else {
            for (int i = 0; i < raw.mark.length; ++i) {
                if (raw.mark[i] < 0) {
                    throw new ConfigException("Elemento negativo en la marca", errorTypeConfig.invalidFormatArray);
                }
            }
        }

        /* Chequeo de longuitud del vector de maximo de plazas y elementos positivos */
        if (this.isExtMaxToken()) {
            if (raw.extMaxToken.length != raw.mark.length) {
                throw new ConfigException("La cantidad de plazas no es correcta en " +
                        "los elementos de maximo por plaza", errorTypeConfig.invalidFormatArray);
            } else {
                for (int i = 0; i < raw.extMaxToken.length; ++i) {
                    if (raw.extMaxToken[0] < 0) {
                        throw new ConfigException("Elemento negativo en la marca por plaza",
                                errorTypeConfig.invalidFormatArray);
                    }
                }
            }
        }

        /* Chequeo de longuitud del vector de arcos lectores o inhibidores */
        if (this.isExtReaderInh()) {
            if (raw.extReaderInh.length != raw.matrixW.length) {
                throw new ConfigException("La cantidad de plazas  en la matriz de arcos lectores e inhibidores " +
                        "no es correcta", errorTypeConfig.invalidFormatArray);
            } else if (raw.extReaderInh[0].length != raw.matrixW[0].length) {
                throw new ConfigException("La cantidad de transiciones  en la matriz de arcos lectores e inhibidores " +
                        "no es correcta", errorTypeConfig.invalidFormatArray);
            } else {
                /* Chequeo de longuitud de matriz constante. */
                conlconst = -1;
                for (int i = 0; i < raw.extReaderInh.length; ++i) {
                    if (conlconst == -1) {
                        conlconst = raw.extReaderInh[0].length;
                    } else if (conlconst != raw.extReaderInh[i].length) {
                        throw new ConfigException("La matriz en la matriz de arcos lectores e inhibidores " +
                                "no es constante", errorTypeConfig.invalidFormatMatrix);
                    }
                }
            }
        }

        /* Chequeo de longuitud de vector de tiempo, ausencia de elementos nulos y negativos */
        if (this.raw.extTempWindows != null) {
            raw.extTempTimeStamp = new long[raw.matrixW[0].length];
            //Se verifica que el largo de la matriz no sea distinto de 2
            if (raw.extTempWindows.length != 2) {
                throw new ConfigException("Vector temporal distinto de 2 dimensiones", errorTypeConfig.invalidFormatMatrix);
                //Se chequea el largo de los dos vectores por separado
            } else if (raw.extTempWindows[0].length != raw.matrixW[0].length || raw.extTempWindows[1].length != raw.matrixW[0].length) {
                throw new ConfigException("La cantidad de transiciones es no es la misma", errorTypeConfig.invalidFormatMatrix);
            } else {
                //Paso final, se chequea la ausencia de elementos negativos en la matriz
                for (int i = 0; i < 2; ++i) {
                    for (int j = 0; j < raw.extTempWindows[0].length; ++j) {
                        if (raw.extTempWindows[i][j] < 0) {
                            throw new ConfigException("Elemento negativo en el vector temporal", errorTypeConfig.invalidFormatMatrix);
                        } else {
                            continue;
                        }
                    }
                }
            }
            //Finalizado los chequeos se procede a cargar el vector de tiempo dependiendo si la transicion esta sensibilizada
            boolean SensAux[] = getSensitizedArray();
            long tiempo = java.lang.System.currentTimeMillis();
            for (int i = 0; i < raw.extTempWindows[0].length; ++i) {
                if (SensAux[i] == true) {
                    raw.extTempTimeStamp[i] = tiempo;
                } else {
                    raw.extTempTimeStamp[i] = 0;
                }
            }

        }
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
     *
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
        return this.isExtMaxToken() ? raw.extMaxToken.clone() : null;
    }

    /**
     * Obtiene una matriz con la informacion de las transiciones temporales
     * <pre>
     * @return Una copia de la matriz con transiciones temporales <br>
     *         Null Si no es extendida la red
     * </pre>
     */
    public long[][] getExtTemporal() {
        return this.isExtReaderInh() ? raw.extTempWindows.clone() : null;
    }

    /**
     * Obtiene una matriz con la informacion de los arcos lectores e inhibidores, con el mismo formato de JSON
     * <pre>
     * @return Una copia de la matriz con arcos lectores e inhibidores. <br>
     *         Null Si no es extendida la red
     * </pre>
     */
    public int[][] getExtReaaderInh() {
        return this.isExtReaderInh() ? raw.extReaderInh.clone() : null;
    }

    /**
     * Consulta si la red de petri es extendida para maxima cantidad de tokens
     * <pre>
     * @return true:  Si estan limitadas las plazas a un numero maximo de tokens <br>
     *         false: Caso contrario
     * </pre>
     */
    public boolean isExtMaxToken() {
        return (raw.extMaxToken != null);
    }

    /**
     * Consulta si la red de petri es extendida arcos inhibidores y lectores
     * <pre>
     * @return true:  Si hay matriz de arcos lectores e inhibidores <br>
     *         false: Caso contrario
     * </pre>
     */
    public boolean isExtReaderInh() {
        return (raw.extReaderInh != null);
    }


    /**
     * Consulta si la red de petri es temporal
     * <pre>
     * @return true:  Hay ventana de tiempo establecido para 1 o mas transiciones <br>
     *         false: Caso contrario
     * </pre>
     */
    public boolean isExtTemp() {
        return (raw.extTempWindows != null);
    }

    /**
     * Obtiene la matriz de doble incidencia de la red de petri
     *
     * @return Devuelve una copia de la matriz de la red de petri
     */
    public int[][] getMatrix() {
        return this.raw.matrixW.clone();
    }

    /**
     * <pre>
     * Intenta realizar disparo de la red de petri, si puede dispara.
     *
     * @param tDisp Numero de transicion a disparar
     * @return True en caso de exito en el disparo de la transicion y evolucion de la red
     *         False en caso de que la transicion no este sencibilidaza
     * @throws ShotException Si no existe la transicion
     * </pre>
     */
    public boolean shotT(int tDisp) throws ShotException{
        return shotT(tDisp, false, false,java.lang.System.currentTimeMillis());
    }

    /**
     * Realiza el disparo en la red de petri, este puede ser un disparo de prueba o puede guardar los resultados
     *
     * <pre>
     * @param tDisp Numero de transicion a disparar
     * @param test  True si no quiere alterar el estado de la red de petri <br>
     *              False si en caso de que se pueda disparar se altere el estado de la redes
     * @return True en caso de exito en el disparo de la transicion <br>
     *         False en caso de que la transicion no este sencibilidaza
     * @throws ShotException Excepcion por inexistencia de la transicion
     * </pre>
     */
    protected boolean shotT(int tDisp, boolean test) throws ShotException{
        return shotT(tDisp, test, false,java.lang.System.currentTimeMillis());
    }

    /**
     * Realiza el disparo en la red de petri, este puede ser un disparo de prueba o puede guardar los resultados
     * <pre>
     * @param tDisp         Numero de transicion a disparar
     * @param test          True si no quiere alterar el estado de la red de petri <br>
     *                      False si en caso de que se pueda disparar se altere el estado de la redes
     * @param ignoreWindows True para ignorar la ventana de tiempo de la red temporal <br>
     *                      Si la red no es temporal el valor del parametro es indistinto  <br>
     *                      NOTA: Se utiliza para verificar las transiciones sensibilizadas y contar el tiempo
     * @param timestamp     Si ignore windows es false, se utiliza para determinar si esta dentro de la <br>
     *                      ventana en en tiempo para disparar. <br>
     *                      NOTA: Se utiliza para detectar varias transiciones habilitadas con el mismo timestamp
     * @return True en caso de exito en el disparo de la transicion <br>
     *         False en caso de que la transicion no este sencibilidaza
     * @throws ShotException Excepcion por inexistencia de la transicion
     * </pre>
     */
    protected boolean shotT(int tDisp, boolean test, boolean ignoreWindows, long timestamp) throws ShotException {
        boolean validShot = true;
        int[] newMark;

        // Excepcion por inexistencia de transicion
        if (tDisp > raw.matrixW[0].length || tDisp < 1)
            throw new ShotException(raw.mark, tDisp, this.raw.matrixW[0].length);

        /* Chequeo arcos lectores e inhibidores (1) - Ahorro multiplicacion si no se puede disparar */
        if (validShot && this.isExtReaderInh()) {
            for (int i = 0; i < this.raw.extReaderInh.length; i++) {
                if (this.raw.extReaderInh[i][tDisp - 1] == 0) {
                    continue; // Sale sin chequear los if, es el mas probable por eso esta aca
                } else if (this.raw.extReaderInh[i][tDisp - 1] < 0 && this.raw.mark[i] != 0) {
                    // La transicion tDisp se encuentra inhibida por la plaza i+1
                    validShot = false;
                    break;
                } else if (this.raw.extReaderInh[i][tDisp - 1] > 0 &&
                        this.raw.mark[i] < this.raw.extReaderInh[i][tDisp - 1]) {
                    // La transicion tDisp se encuentra inhibida por  el arco lector
                    validShot = false;
                    break;
                }
            }
        }

        /* Si el tiro sigue siendo valido chequeo nueva marca  (2-3) - Verifico > 0 y < MaxTokens */
        newMark = validShot ? this.nextMark(tDisp) : null;
        for (int i = 0; validShot && i < newMark.length; i++) {
            if (newMark[i] < 0) {
                validShot = false;
                break;
            }
            /* Chequeo maximo de plazas */
            else if (this.isExtMaxToken()) {
                if (raw.extMaxToken[i] != 0 && newMark[i] > raw.extMaxToken[i]) {
                    validShot = false;
                    break;
                }
            }
        }

        /* Si el tiro sigue siendo valido y es temporal hago chequeo de tiempo */
        /* Necesariamente se tiene que hacer despues de calcular el posible nuevo marcado
           Ya que puede que el nuevo marcado no sea valido por que una plaza tiene un maximo de tokens */
        if (validShot && this.isExtTemp() && !ignoreWindows) {
            /* Chequeo de la ventana de tiempo */
            if (raw.extTempWindows[0][tDisp - 1] != 0) {
                validShot = raw.extTempWindows[0][tDisp - 1] < (timestamp - raw.extTempTimeStamp[tDisp - 1]);
            }
            if (validShot && raw.extTempWindows[1][tDisp - 1] != 0) {
                validShot = raw.extTempWindows[1][tDisp - 1] > (timestamp - raw.extTempTimeStamp[tDisp - 1]);
            }

        }


        // No es un test y el marcado es valido
        if (!test && validShot) {
            if(this.isExtTemp()){
                boolean[] oldSensitized = getSensitizedArray(true, timestamp);
                raw.mark = newMark;
                boolean[] newSensitized = getSensitizedArray(true, timestamp);
                for(int i=0; i<newSensitized.length;++i){
                    if (!oldSensitized[i] && newSensitized[i]){
                        raw.extTempTimeStamp[i] = timestamp;
                    }
                }
            }else{
                raw.mark = newMark;
            }

        }

        return validShot;
    }
    /**
     * Retorna el vector de transiciones sensibilizadas, cada lugar del vector representa una transicion
     * donde el primer lugar corresponde a la primera transicion.
     *
     * <pre>
     * @return True si la transicion esta sensibilizada <br>
     *         False en caso contrario
     * </pre>
     */
    protected boolean[] getSensitizedArray(){
        return getSensitizedArray(false, java.lang.System.currentTimeMillis());
    }

    /**
     * <pre>
     * Retorna el vector de transiciones sensibilizadas, cada lugar del vector representa una transicion
     * donde el primer lugar corresponde a la primera transicion. <br>
     * Puede ignorarse la ventana de tiempo para la obtencion de la sensibilidad <br>
     * NOTA: Se utiliza para caluclar los timestamp de las redes de petri
     *
     * @param ignoreWindows True si se desea ignorar la parte temporal de la red para la obtencion del vector<br>
     *                      False Si se desea tomar en cuenta el timestamp y la parte temporal de la red
     * @param timestamp     Valor del tiempo en milisegundos para realizar el calulo de las ventanas de tiempo <br>
     *                      Permite tener sincronizacion entre que se chequea la sensibilidad de cada transicion
     * @return        True si la transicion esta sensibilizada <br>
     *                False en caso contrario
     * </pre>
     */
    public boolean[] getSensitizedArray(boolean ignoreWindows, long timestamp) {
        boolean[] sensitizedArray = new boolean[raw.matrixW[0].length];

        try {
            for (int i = 0; i < sensitizedArray.length; i++) {
                sensitizedArray[i] = this.shotT(i + 1, true, ignoreWindows, timestamp);
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
     * <pre>
     * Si la plaza tiene un maximo de tokens y esta llena no los agregara. <br>
     * Si se intentan agregar mas tokens y el resultado final sobrepasa la cantidad
     * maxima de tokens permitidos tampoco los agrega.
     * @param Plaza plaza que se quiere agregar token
     * @param cant numero entero de tokens a agregar a la plaza.
     * @return  True: dadero en caso de que se puedan agregar dichos tokens<br>
     *          False: Caso contrario
     * @throws TokenException producida por inexistencia de la plaza o una cantidad negativa de tokens
     * </pre>
     */
    protected boolean AddToken(int Plaza, int cant) throws TokenException {

        boolean agregar;
        //Si la plaza no existe lanza la execepcion
        if (Plaza > raw.matrixW.length || Plaza <= 0) {
            throw new TokenException(raw.mark, Plaza + 1, raw.matrixW.length, cant);
            //Si la cantidad a agregar es negativa lanza la excepcion
        } else if (cant < 0) {
            throw new TokenException(raw.mark, Plaza + 1, raw.matrixW.length, cant);
            //Si la cantidad es mayor al limite de la plaza devuelve un false
        } else if (cant + raw.mark[Plaza - 1] > raw.extMaxToken[Plaza - 1] && raw.extMaxToken[Plaza - 1] != 0) {
            agregar = false;
            return agregar;
        }
        //Modifico el vector de marcado y devuelvo true
        raw.mark[Plaza - 1] = cant + raw.mark[Plaza - 1];
        agregar = true;
        return agregar;

    }

    /**
     * Metodo encargado de setear tokens a determinada plaza.
     * <pre>
     * @param Plaz plaza que se quiere agregar token
     * @param Cant numero entero de tokens a agregar
     * @return True en caso de que se puedan agregar dichos tokens <br>
     *         False en caso contrario (debido a que no tiene lugar suficiente la plaza)
     * @throws TokenException producida por inexistencia de la plaza o una cantidad negativa de tokens
     * Principal diferencia con respecto a AddTokens es que no tiene en cuenta los
     * Tokens que se encuentran en dicha plaza, estos son reemplazados por el numero especifico a agregar.
     * </pre>
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