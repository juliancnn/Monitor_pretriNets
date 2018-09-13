package jmml.monitor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import jmml.monitor.ConfigException;
import jmml.monitor.RDP;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testea la Red de Petri en base a los archivos ubicados en el directorio
 */
@Tag("Carga de RDP")
class RDPTest {

    /*
     * Lista de archivos usados en los test
     * */
    private static final String JFILE_RDP1 = "examples_rdp/ej1_basic.json";
    private static final String JFILE_RDP2_MAXTOKENS = "examples_rdp/ej2_extended_MaxToken.json";
    private static final String JFILE_RDP3_MAXTOKENS = "examples_rdp/ej3_extended_MaxToken.json";
    private static final String JFILE_RDP4_INH = "examples_rdp/ej4_extended_Inh.json";
    private static final String JFILE_RDP1_INH = "examples_rdp/ej1_extended_Inh.json";
    //private static final String JFILE_RDP1_TEMPORAL = "examples_rdp/ex1_extended_Temporal.json";
    private static final String JFILE_RDP1_MAXTOKENS = "examples_rdp/ej1_extended_MaxToken.json";

    /**
     * Verifica que los archivos de la red sean los esperados para los test
     * <p>
     * El checkeo con archivos mejora la seguridad de la interface de la red, impidiendo cambios en la misma,
     * ya que no se expone como modificarla de manera dinamica, ya que esto no es contemplado en el
     * algoritmo de la red. Tambien se asegura que todas las redes pasen por todos los test.
     */
    @BeforeAll
    @Tag("files")
    static void checkInitFiles() {
        /*================================================
            RDP 1: Basica, no exendida en ninguna forma
          ================================================ */

        try {
            RDP rdp1 = new RDP(JFILE_RDP1);
            Assertions.assertArrayEquals(new int[][]
                    {
                            {-1, 0, 0, 1},
                            {1, -1, 0, 0},
                            {0, 1, 0, -1},
                            {1, 0, -1, 0},
                            {0, 0, 1, -1}
                    }, rdp1.getMatrix(), "Red de petri 1 alterada para el test");
            Assertions.assertArrayEquals(new int[]{1, 0, 0, 0, 0}, rdp1.getMark(),
                    "Marca inicial 1 alterada para el test");
        } catch (java.io.FileNotFoundException e) {
            Assertions.fail("No existe el archivo");
        } catch (ConfigException e) {
            Assertions.fail(e.toString());
        }

          /*================================================
            RDP 1_extend: Extiende los valores maximos de
                        tokens por plaza
          ================================================ */
        try {
            RDP rdp1_extend = new RDP(JFILE_RDP1_MAXTOKENS);
            Assertions.assertArrayEquals(new int[][]
                    {
                            {-1, 0, 0, 1},
                            {1, -1, 0, 0},
                            {0, 1, 0, -1},
                            {1, 0, -1, 0},
                            {0, 0, 1, -1}
                    }, rdp1_extend.getMatrix(), "Red de petri 1 alterada para el test");
            Assertions.assertArrayEquals(new int[]{3, 0, 0, 0, 0}, rdp1_extend.getMark(),
                    "Marca inicial 1 alterada para el test");
            if (!rdp1_extend.isExtMaxToken()) {
                Assertions.fail("La red de petri no es extendida");
            } else {
                assertArrayEquals(new int[]{0, 2, 0, 0, 0}, rdp1_extend.getExtMaxToken(),
                        "Tokens maximos alterados para test");
            }

        } catch (java.io.FileNotFoundException e) {
            Assertions.fail("No existe el archivo JSON");

        } catch (ConfigException e) {
            Assertions.fail(e.toString());
        }
        /*================================================
        RDP 1_extend: Extiende los valores maximos de
          tokens por plaza y suma arcos inhibidores
          ================================================ */
        try {
            RDP rdp1_extend_H = new RDP(JFILE_RDP1_INH);
            Assertions.assertArrayEquals(new int[][]
                    {
                            {-1, 0, 0, 1},
                            {1, -1, 0, 0},
                            {0, 1, 0, -1},
                            {1, 0, -1, 0},
                            {0, 0, 1, -1}
                    }, rdp1_extend_H.getMatrix(), "Red de petri 1 alterada para el test");
            Assertions.assertArrayEquals(new int[]{3, 0, 0, 0, 0}, rdp1_extend_H.getMark(),
                    "Marca inicial 1 alterada para el test");
            if (!rdp1_extend_H.isExtMaxToken()) {
                Assertions.fail("La red de petri no es extendida");
            } else {
                assertArrayEquals(new int[]{0, 2, 0, 0, 0}, rdp1_extend_H.getExtMaxToken(),
                        "Tokens maximos alterados para test");
            }
            if (!rdp1_extend_H.isExtInh()) {
                Assertions.fail("La red de petri no es extendida para lectores escritores");
            } else {
                assertArrayEquals(new int[][]{
                                {0, 0, 0, 0},
                                {0, 0, 2, -1},
                                {0, 0, 0, 0},
                                {0, 0, 0, 0},
                                {0, 0, 0, 0}
                        }, rdp1_extend_H.getExtInh(),
                        "Arcos lectores/inhibidores alterados para el test");
            }

        } catch (java.io.FileNotFoundException e) {
            Assertions.fail("No existe el archivo JSON");

        } catch (tpc.monitor.rdp.ConfigException e) {
            Assertions.fail(e.toString());
        }

    }
}