package jmml.monitor;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;

/**
 * Encargado de generarar los objetos a partir de un archivo de estructura tipo JSON
 * @see <a href="https://www.json.org/json-es.html">JSON Struct</a>
 */
public class DataParser {
    private final JsonReader reader;
    private final Gson json;

    /**
     * Crea el parser del JSON
     * @param jsonFile Ruta del archivo JSON
     * @throws FileNotFoundException Si el archivo no se encuentra
     */
    public DataParser(String jsonFile) throws FileNotFoundException {
        this.json = new Gson();
        this.reader = new JsonReader(new FileReader(jsonFile));
    }

    /**
     * Genera el objeto a partir del nombre de la clase y los datos del JSON
     * @param tipo  Nombre de la clase del objeto a crear
     * @param <T>   Generico
     * @return Retorno un objeto de clase tipo relleno de la informacion del JSON
     * @see <a href="https://docs.oracle.com/javase/tutorial/java/generics/index.html"> Metodo generico </a>
     */
    public <T> T generate(Type tipo) {
        return json.fromJson(reader, tipo);
    }
}
