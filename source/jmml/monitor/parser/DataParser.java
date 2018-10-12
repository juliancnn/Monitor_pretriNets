package jmml.monitor.parser;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;

/**
 * Encargado de generarar los objetos a partir de un archivo de estructura tipo JSON
 *
 * @see <a href="https://www.json.org/json-es.html">JSON Struct</a>
 */
public class DataParser {

    private String jsonFile;

    /**
     * Crea el parser del JSON
     *
     * @param jsonFile Ruta del archivo JSON
     * @throws FileNotFoundException Si el archivo no se encuentra
     */
    public DataParser(String jsonFile) throws FileNotFoundException {
        super();
        this.jsonFile = jsonFile;
        File f = new File(jsonFile);
        if (!f.exists()) {
            throw new FileNotFoundException("No existe " + jsonFile);
        }

    }

    /**
     * Genera el objeto a partir del nombre de la clase y los datos del JSON.
     * Si el archivo JSON no es posible de acceder por que fue eliminado luego de la creacion del DataParser
     * retorna null
     *
     * @param tipo Nombre de la clase del objeto a crear
     * @param <T>  Generico
     * @return Retorno un objeto de clase tipo relleno con el JSON<br>
     * NULL si el archivo no es posible de acceder luego de la creacion del dataparcer
     * @see <a href="https://docs.oracle.com/javase/tutorial/java/generics/index.html"> Metodo generico </a>
     */
    public <T> T generate(Type tipo) {
        JsonReader reader;
        Gson json;
        json = new Gson();
        try {
            reader = new JsonReader(new FileReader(this.jsonFile));
        } catch (FileNotFoundException e) {
            return null;
        }
        return json.fromJson(reader, tipo);
    }
}
