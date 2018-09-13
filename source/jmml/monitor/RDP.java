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
     * misma estructura de rawRDP. (Vease rawRDP para ver la estructura completa del archivo)
     * <pre>
     *   Estructura del JSON:
     *
     *    {
     *     "brief" : "Un breve descripcion de la red"
     *     "info"  : "Una descripcion mas detallada de la red",
     *     "matrixI" : [                             # La matriz de doble incidencia
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
}
