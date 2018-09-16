package jmml.monitor;

import org.junit.jupiter.api.*;


import static org.junit.jupiter.api.Assertions.*;

/**
 * Testea la Red de Petri en base a los archivos ubicados en el directorio
 * @TODO Falta checkear el archiv JFILE_RDP5_READER
 * @TODO Falta todo en JFILE_RDP2_MAXTOKENS
 * @TODO Falta todo en JFILE_RDP3_MAXTOKENS
 * @TODO Faltan los test de JFILE_RDP1_INH
 * @TODO Falta checkeo de archivos en JFILE_RDP4_INH
 * @TODO Falta Armar un archivo nuevo para lectores solo
 * @TODO Falta todo en JFILE_RDP1_READERINH
 * @TODO Falta todo en JFILE_RDP1_TEMPORAL
 */
@Tag("RDP")
class RDPTest {

    /*
     * Lista de archivos usados en los test
     * */
    private static final String JFILE_RDP1 = "examples_rdp/ej1_basic.json";
    private static final String JFILE_RDP1_MAXTOKENS = "examples_rdp/ej1_extended_MaxToken.json";
    //private static final String JFILE_RDP2_MAXTOKENS = "examples_rdp/ej2_extended_MaxToken.json";
    //private static final String JFILE_RDP3_MAXTOKENS = "examples_rdp/ej3_extended_MaxToken.json";
    private static final String JFILE_RDP1_INH = "examples_rdp/ej1_extended_Inh.json";
    private static final String JFILE_RDP4_INH = "examples_rdp/ej4_extended_Inh.json";
    private static final String JFILE_RDP5_READER = "examples_rdp/ej5_extended_Reader.json";
    //private static final String JFILE_RDP1_READERINH = "examples_rdp/ej1_extended_ReaderInh.json";
    //private static final String JFILE_RDP1_TEMPORAL = "examples_rdp/ex1_extended_Temporal.json";


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
            Assertions.assertArrayEquals(new int[]{3, 0, 0, 0, 0}, rdp1.getMark(),
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
                                {0, 0, 0, 0, 0},
                                {0, 0, 0, 0, 0},
                                {0, 0, 0, 0, 0},
                                {0, 1, 0, 0, 0},
                        }, rdp1_extend_H.getExtInh(),
                        "Arcos lectores/inhibidores alterados para el test");
            }

        } catch (java.io.FileNotFoundException e) {
            Assertions.fail("No existe el archivo JSON");

        } catch (ConfigException e) {
            Assertions.fail(e.toString());
        }

    }

    /**
     * Teste el disparo (shotT), con disparos de prueba y con disparos de evolucion de red
     * Chequea que evolucione correctamente el marcado
     */
    @Test
    @DisplayName("Disparos acertados chequeo de marcado")
    void shotT_ok() {
        try {
        /*================================================
            RDP 1: Basica, no exendida en ninguna forma
          ================================================ */
        try {
            RDP rdp1 = new RDP(JFILE_RDP1);
            Assertions.assertArrayEquals(new int[]{3, 0, 0, 0, 0}, rdp1.getMark());
            Assertions.assertTrue(rdp1.shotT(1), "No se disparo y debia");
            Assertions.assertArrayEquals(new int[]{2, 1, 0, 1, 0}, rdp1.getMark(),
                    "La red evoluciono mal o no evoluciono");
            Assertions.assertTrue(rdp1.shotT(1), "No se disparo y debia");
            Assertions.assertArrayEquals(new int[]{1, 2, 0, 2, 0}, rdp1.getMark(),
                    "La red evoluciono mal o no evoluciono");
            Assertions.assertTrue(rdp1.shotT(1), "No se disparo y debia");
            Assertions.assertArrayEquals(new int[]{0, 3, 0, 3, 0}, rdp1.getMark(),
                    "La red evoluciono mal o no evoluciono");
            Assertions.assertFalse(rdp1.shotT(1), "No se disparo y debia");
            Assertions.assertArrayEquals(new int[]{0, 3, 0, 3, 0}, rdp1.getMark(),
                    "La red evoluciono mal o no evoluciono");


        }catch (ShotException e) {
            Assertions.fail("Transicion no existe");
        }
        } catch (java.io.FileNotFoundException e) {
            Assertions.fail("No se puede crear la red de petri");
        } catch (ConfigException e) {
            Assertions.fail(e.toString());
        }


    }

    @Test
    @DisplayName("[extMaxToken] Disparos acertados chequeo de marcado con plazas limitadas")
    void shotT_extendedMAxTokens() {
        try {
        /*================================================
            RDP 1: Limitada en la plaza 2 con 2 tokens
          ================================================ */
            try {
                RDP rdp1 = new RDP(JFILE_RDP1_MAXTOKENS);
                Assertions.assertArrayEquals(new int[]{3, 0, 0, 0, 0}, rdp1.getMark());
                Assertions.assertTrue(rdp1.shotT(1), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{2, 1, 0, 1, 0}, rdp1.getMark(),
                        "La red evoluciono mal o no evoluciono");
                Assertions.assertTrue(rdp1.shotT(1), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{1, 2, 0, 2, 0}, rdp1.getMark(),
                        "La red evoluciono mal o no evoluciono");
                Assertions.assertFalse(rdp1.shotT(1), "Se disparo y no debia");
                Assertions.assertArrayEquals(new int[]{1, 2, 0, 2, 0}, rdp1.getMark(),
                        "La red evoluciono mal o no evoluciono");
                Assertions.assertTrue(rdp1.shotT(2), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{1, 1, 1, 2, 0}, rdp1.getMark(),
                        "La red evoluciono mal o no evoluciono");
                Assertions.assertTrue(rdp1.shotT(1), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{0, 2, 1, 3, 0}, rdp1.getMark(),
                        "La red evoluciono mal o no evoluciono");


            }catch (ShotException e) {
                Assertions.fail("Transicion no existe");
            }
        } catch (java.io.FileNotFoundException e) {
            Assertions.fail("No se puede crear la red de petri");
        } catch (ConfigException e) {
            Assertions.fail(e.toString());
        }


    }


    @Test
    @Tag("extINH")
    @DisplayName("[extInh] Disparos con arcos inhibidores + MaxTokens")
    void shotT_extendedInh() {

        /*=========================================================
            RDP 4_extend: Extendida, con arcos inhibidores y max tokens
          ========================================================= */
        try {
             RDP rdp4_extend = new RDP(JFILE_RDP4_INH);
            Assertions.assertArrayEquals(new int[]{5, 0, 0}, rdp4_extend.getMark());
            try {
                Assertions.assertTrue(rdp4_extend.shotT(2), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{4, 0, 1}, rdp4_extend.getMark(),
                        "La red no evoluciono y debia");
                Assertions.assertTrue(rdp4_extend.shotT(2), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{3, 0, 2}, rdp4_extend.getMark(),
                        "La red no evoluciono y debia");
                Assertions.assertFalse(rdp4_extend.shotT(1), "Se disparo y no debia");
                Assertions.assertArrayEquals(new int[]{3, 0, 2}, rdp4_extend.getMark(),
                        "La red evoluciono y no debia");
                Assertions.assertTrue(rdp4_extend.shotT(2), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{2, 0, 3}, rdp4_extend.getMark(),
                        "La red no evoluciono y debia");
                /*Por maxima cantidad de tokens*/
                Assertions.assertFalse(rdp4_extend.shotT(2), "Se disparo y no debia");
                Assertions.assertArrayEquals(new int[]{2, 0, 3}, rdp4_extend.getMark(),
                        "La red no evoluciono y debia");
                Assertions.assertTrue(rdp4_extend.shotT(4), "No se disparo y debia");
                Assertions.assertTrue(rdp4_extend.shotT(4), "No se disparo y debia");
                Assertions.assertTrue(rdp4_extend.shotT(4), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{5, 0, 0}, rdp4_extend.getMark());
                Assertions.assertTrue(rdp4_extend.shotT(1), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{4, 1, 0}, rdp4_extend.getMark());
                Assertions.assertFalse(rdp4_extend.shotT(1), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{4, 1, 0}, rdp4_extend.getMark());
                Assertions.assertFalse(rdp4_extend.shotT(2), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{4, 1, 0}, rdp4_extend.getMark());
                Assertions.assertTrue(rdp4_extend.shotT(3), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{5, 0, 0}, rdp4_extend.getMark());
            } catch (ShotException e) {
                Assertions.fail();
            }
        } catch (java.io.FileNotFoundException e) {
            Assertions.fail("No existe el archivo");
        } catch (ConfigException e) {
            Assertions.fail(e.toString());
        }
    }

    @Test
    @Tag("extReader")
    @DisplayName("[extReader] Disparos con arcos Lectores + MaxTokens")
    void shotT_extendedReader() {

        /*=========================================================
            RDP 4_extend: Extendida, con arcos inhibidores y max tokens
          ========================================================= */
        try {
            RDP rdp5_extReader = new RDP(JFILE_RDP5_READER);
            Assertions.assertArrayEquals(new int[]{0, 0, 1, 0}, rdp5_extReader.getMark());
            try {
                Assertions.assertFalse(rdp5_extReader.shotT(3), "Se disparo y no debia");
                Assertions.assertFalse(rdp5_extReader.shotT(2), "Se disparo y no debia");
                Assertions.assertTrue(rdp5_extReader.shotT(1), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{1, 0, 1, 0}, rdp5_extReader.getMark(),
                        "La red no evoluciono y debia");
                Assertions.assertTrue(rdp5_extReader.shotT(3), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{1, 0, 0, 1}, rdp5_extReader.getMark(),
                        "La red no evoluciono y debia");
                Assertions.assertTrue(rdp5_extReader.shotT(2), "Se disparo y no debia");
                Assertions.assertArrayEquals(new int[]{0, 1, 1, 0}, rdp5_extReader.getMark(),
                        "La red no evoluciono y debia");
                Assertions.assertFalse(rdp5_extReader.shotT(2), "Se disparo y no debia");
                Assertions.assertTrue(rdp5_extReader.shotT(1), "No se disparo y debia");
                Assertions.assertTrue(rdp5_extReader.shotT(1), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{2, 1, 1, 0}, rdp5_extReader.getMark(),
                        "La red no evoluciono y debia");
                Assertions.assertTrue(rdp5_extReader.shotT(3), "No se disparo y debia");
                Assertions.assertTrue(rdp5_extReader.shotT(2), "Se disparo y no debia");
                Assertions.assertArrayEquals(new int[]{1, 2, 1, 0}, rdp5_extReader.getMark(),
                        "La red no evoluciono y debia");
                Assertions.assertTrue(rdp5_extReader.shotT(3), "No se disparo y debia");
                Assertions.assertTrue(rdp5_extReader.shotT(2), "Se disparo y no debia");
                Assertions.assertArrayEquals(new int[]{0, 3, 1, 0}, rdp5_extReader.getMark(),
                        "La red no evoluciono y debia");
                Assertions.assertFalse(rdp5_extReader.shotT(3), "No se disparo y debia");
                Assertions.assertFalse(rdp5_extReader.shotT(2), "Se disparo y no debia");
                Assertions.assertArrayEquals(new int[]{0, 3, 1, 0}, rdp5_extReader.getMark(),
                        "La red no evoluciono y debia");
                Assertions.assertFalse(rdp5_extReader.shotT(3), "No se disparo y debia");
                Assertions.assertTrue(rdp5_extReader.shotT(1), "Se disparo y no debia");
                Assertions.assertTrue(rdp5_extReader.shotT(3), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{1, 3, 0, 1}, rdp5_extReader.getMark(),
                        "La red no evoluciono y debia");
                Assertions.assertFalse(rdp5_extReader.shotT(2), "Se disparo y no debia");
            } catch (ShotException e) {
                Assertions.fail();
            }
        } catch (java.io.FileNotFoundException e) {
            Assertions.fail("No existe el archivo");
        } catch (ConfigException e) {
            Assertions.fail(e.toString());
        }
    }


}