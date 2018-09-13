package jmml.monitor;
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
 *  - [NO] Dispara las transiciones y alterar el estado de la red
 *  - [NO] Informar las transiciones disponibles para disparar
 *  - [NO] Informar si se puede disparar o no una transicion
 *  - [NO] Informar el marcado de la red
 *  - [NO] Informar el estado de sensibilizado de todas las condiciones (Matrix sensibilizado)
 *  - [NO] Imprimir toda la informacion disponible de la red y su estado
 *
 * Soporta caracteristicas de redes de petri extendidas como:
 *  - [NO] Maxima cantidad de tokens por plaza
 *  - [NO] Arcos inhibidores
 *  - [NO] Arcos lectores con peso 1
 *  - [NO] Arcos lectores con otro peso
 *  - [NO] Transiciones sensibilizadas temporalmente
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
    private RDPraw raw;
    /**
     * Crea la red de petri a partir de un archivo
     * <p>
     * El constructor setea el marcador inicial, la rdp y su configuracion en base a un archivo json de la
     * misma estructura de RDPraw. (Vease RDPraw para ver la estructura completa del archivo)
     * <pre>
     *   Estructura del JSON:
     *
     *    {
     *     "brief" : "Un breve descripcion de la red"
     *     "info"  : "Una descripcion mas detallada de la red",
     *     "matrixI" : [                              # La matriz de doble incidencia
     *       [-1, 0, 0, 1],
     *       [1, -1, 0, 0],
     *       [0, 1, 0, -1],
     *       [1, 0, -1, 0],
     *       [0, 0, 1, -1]
     *     ],
     *     "vectorMark"     : [3, 0, 0, 0, 0],            # marcado inicial de la red
     *   }
     * </pre>
     *
     * @param jsonFile Nombre del archivo JSON que contiene la informacion
     * @throws FileNotFoundException Lanzado cuando no se encuentra el archivo JSON
     * @see RDPraw Ver estructura completa del JSON
     */
    public RDP(String jsonFile) throws FileNotFoundException {

        /* INICIO DE CARGA DE DATOS */
        Gson json = new Gson();
        JsonReader reader = new JsonReader(new FileReader(jsonFile));
        this.raw = json.fromJson(reader, RDPraw.class);
        /* FIN DE CARGA DE DATOS */
    }

    // Falta agregar las escepciones si no existe la transiciones
    public boolean shotT(int tDisp, boolean test){
        boolean validShot = true;
        int[] newMark;

        /* Si el tiro sigue siendo valido chequeo nueva marca */
        newMark = validShot ? this.nextMark(tDisp) : null;
        validShot = newMark != null && valid4Mark(newMark);


        if(validShot && !test){
            this.raw.vectorMark = newMark;
        }

        return validShot;
    }

    /**
     * <pre>
     * Analiza si la marca es valida, tomando en cuenta la posibilidad de maxima cantidad de tokens
     * @param mark Vector marca a analizar
     * @return True si la marca es valida.
     *         False si la marca no es valida
     * </pre>
     */
    private boolean valid4Mark(int[] mark){
        boolean valid = true;

        for(int i=0;i<mark.length;i++){
            // Marca invalida por falta de tokens
            if(mark[i]<0){
                valid = false;
                break;
            }
            // Cambio por la funcion si es extendida
            // Marca invalida por maxTokens
            if(this.raw.vectorMaxMark != null){
                if(this.raw.vectorMaxMark[i] != 0 && mark[i] > this.raw.vectorMaxMark[i]){
                    valid = false;
                    break;
                }

            }
        }

        return valid;
    }

    /**
     * Retorna el posible resultado del proximo marcado luego del disparo tDisp
     * <pre>
     * Retorna el posible resultado del disparo sin alterar el marcador y sin verificar su validez <br>
     * Calcula el marcado del disparo de 1 sola transicion, como: <br>
     * Nuevo mark = mark actual + RDP * Vector Disparo
     * @param tDisp numero de transicion a disparar
     * @return vectorNextMark  Proxima marca, sea alcanzable o no. Null en caso de inexistencia de transicion
     * </pre>
     */
    private int[] nextMark(int tDisp){
        // Si la transicion no existe
        if (tDisp > raw.matrixI[0].length || tDisp < 1)
            return null;

        // Vector de disparo ()
        int[] vectorDisparo = new int[raw.matrixI[0].length];
        vectorDisparo[tDisp - 1] = 1;

        // vector Proximo marcado
        return matMulVect(this.raw.matrixI,vectorDisparo);

    }

    /**
     * Multiplica la Matriz M por el vector V
     * @param m Matriz de dimencion MxN
     * @param v Vector de dimencion Nx1
     * @return vector de Nx1, NULL en caso de tamanos incompatibles
     */
    private int[] matMulVect(int[][] m, int[] v){
        // Chequeo tamanos compatibles
        if(v.length != m[0].length)
            return null;

        // Vector resultado inicializado en 0
        int[] result = new int[v.length];

        // Opero por filas en la matriz
        for(int i = 0; i < m.length; i++){
            for(int j=0; j<m[0].length;j++){
                result[i] += m[i][j] * v[j];
            }
        }

        return result;
    }


}
