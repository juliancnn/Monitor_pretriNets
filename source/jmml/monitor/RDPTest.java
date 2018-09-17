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
 * @TODO Falta checkqueo de archivo en JFILE_RDP1_TEMPORAL
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
    private static final String JFILE_RDP1_TEMPORAL = "examples_rdp/ej1_extended_Temporal.json";


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
    @Tag("extMAX")
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

    @Test
    @Tag("extMAX")
    @DisplayName("[extMaxToken - Sensibilizado] Disparos acertados chequeo de marcado con plazas limitadas")
    void shotT_extendedMAxTokens_sen() {
        try {
        /*================================================
            RDP 1: Limitada en la plaza 2 con 2 tokens
          ================================================ */
            try {
                RDP rdp1 = new RDP(JFILE_RDP1_MAXTOKENS);
                Assertions.assertArrayEquals(new int[]{3, 0, 0, 0, 0}, rdp1.getMark());
                Assertions.assertArrayEquals(new boolean[]{true,false,false,false}, rdp1.getSensitizedArray());
                Assertions.assertTrue(rdp1.shotT(1), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{2, 1, 0, 1, 0}, rdp1.getMark(),
                        "La red evoluciono mal o no evoluciono");
                Assertions.assertArrayEquals(new boolean[]{true,true,true,false}, rdp1.getSensitizedArray());
                Assertions.assertTrue(rdp1.shotT(1), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{1, 2, 0, 2, 0}, rdp1.getMark(),
                        "La red evoluciono mal o no evoluciono");
                Assertions.assertArrayEquals(new boolean[]{false,true,true,false}, rdp1.getSensitizedArray());
                Assertions.assertFalse(rdp1.shotT(1), "Se disparo y no debia");
                Assertions.assertArrayEquals(new int[]{1, 2, 0, 2, 0}, rdp1.getMark(),
                        "La red evoluciono mal o no evoluciono");
                Assertions.assertArrayEquals(new boolean[]{false,true,true,false}, rdp1.getSensitizedArray());
                Assertions.assertTrue(rdp1.shotT(2), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{1, 1, 1, 2, 0}, rdp1.getMark(),
                        "La red evoluciono mal o no evoluciono");
                Assertions.assertArrayEquals(new boolean[]{true,true,true,false}, rdp1.getSensitizedArray());
                Assertions.assertTrue(rdp1.shotT(1), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{0, 2, 1, 3, 0}, rdp1.getMark(),
                        "La red evoluciono mal o no evoluciono");
                Assertions.assertArrayEquals(new boolean[]{false,true,true,false}, rdp1.getSensitizedArray());
                Assertions.assertTrue(rdp1.shotT(3), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{0, 2, 1, 2, 1}, rdp1.getMark(),
                        "La red evoluciono mal o no evoluciono");
                Assertions.assertArrayEquals(new boolean[]{false,true,true,true}, rdp1.getSensitizedArray());


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
    @DisplayName("[extInh - Sensibilizado] Disparos con arcos inhibidores + MaxTokens")
    void shotT_extendedInh_sen() {

        /*=========================================================
            RDP 4_extend: Extendida, con arcos inhibidores y max tokens
          ========================================================= */
        try {
            RDP rdp4_extend = new RDP(JFILE_RDP4_INH);
            Assertions.assertArrayEquals(new int[]{5, 0, 0}, rdp4_extend.getMark());
            try {
                Assertions.assertArrayEquals(new boolean[]{true,true,false,false}, rdp4_extend.getSensitizedArray());
                Assertions.assertTrue(rdp4_extend.shotT(2), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{4, 0, 1}, rdp4_extend.getMark(),
                        "La red no evoluciono y debia");
                Assertions.assertArrayEquals(new boolean[]{false,true,false,true}, rdp4_extend.getSensitizedArray());
                Assertions.assertTrue(rdp4_extend.shotT(2), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{3, 0, 2}, rdp4_extend.getMark(),
                        "La red no evoluciono y debia");
                Assertions.assertArrayEquals(new boolean[]{false,true,false,true}, rdp4_extend.getSensitizedArray());
                Assertions.assertFalse(rdp4_extend.shotT(1), "Se disparo y no debia");
                Assertions.assertArrayEquals(new int[]{3, 0, 2}, rdp4_extend.getMark(),
                        "La red evoluciono y no debia");
                Assertions.assertArrayEquals(new boolean[]{false,true,false,true}, rdp4_extend.getSensitizedArray());
                Assertions.assertTrue(rdp4_extend.shotT(2), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{2, 0, 3}, rdp4_extend.getMark(),
                        "La red no evoluciono y debia");
                /*Por maxima cantidad de tokens*/
                Assertions.assertArrayEquals(new boolean[]{false,false,false,true}, rdp4_extend.getSensitizedArray());
                Assertions.assertFalse(rdp4_extend.shotT(2), "Se disparo y no debia");
                Assertions.assertArrayEquals(new int[]{2, 0, 3}, rdp4_extend.getMark(),
                        "La red no evoluciono y debia");
                Assertions.assertArrayEquals(new boolean[]{false,false,false,true}, rdp4_extend.getSensitizedArray());
                Assertions.assertTrue(rdp4_extend.shotT(4), "No se disparo y debia");
                Assertions.assertArrayEquals(new boolean[]{false,true,false,true}, rdp4_extend.getSensitizedArray());
                Assertions.assertTrue(rdp4_extend.shotT(4), "No se disparo y debia");
                Assertions.assertArrayEquals(new boolean[]{false,true,false,true}, rdp4_extend.getSensitizedArray());
                Assertions.assertTrue(rdp4_extend.shotT(4), "No se disparo y debia");
                Assertions.assertArrayEquals(new boolean[]{true,true,false,false}, rdp4_extend.getSensitizedArray());
                Assertions.assertArrayEquals(new int[]{5, 0, 0}, rdp4_extend.getMark());
                Assertions.assertTrue(rdp4_extend.shotT(1), "No se disparo y debia");
                Assertions.assertArrayEquals(new boolean[]{false,false,true,false}, rdp4_extend.getSensitizedArray());
                Assertions.assertArrayEquals(new int[]{4, 1, 0}, rdp4_extend.getMark());
                Assertions.assertFalse(rdp4_extend.shotT(1), "No se disparo y debia");
                Assertions.assertArrayEquals(new boolean[]{false,false,true,false}, rdp4_extend.getSensitizedArray());
                Assertions.assertArrayEquals(new int[]{4, 1, 0}, rdp4_extend.getMark());
                Assertions.assertFalse(rdp4_extend.shotT(2), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{4, 1, 0}, rdp4_extend.getMark());
                Assertions.assertTrue(rdp4_extend.shotT(3), "No se disparo y debia");
                Assertions.assertArrayEquals(new boolean[]{true,true,false,false}, rdp4_extend.getSensitizedArray());
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
    @DisplayName("[extReader - Sensibilizado] Disparos con arcos Lectores + MaxTokens")
    void shotT_extendedReader_sen() {

        /*=========================================================
            RDP 4_extend: Extendida, con arcos inhibidores y max tokens
          ========================================================= */
        try {
            RDP rdp5_extReader = new RDP(JFILE_RDP5_READER);
            Assertions.assertArrayEquals(new int[]{0, 0, 1, 0}, rdp5_extReader.getMark());
            try {
                Assertions.assertArrayEquals(new boolean[]{true,false,false}, rdp5_extReader.getSensitizedArray());
                Assertions.assertFalse(rdp5_extReader.shotT(3), "Se disparo y no debia");
                Assertions.assertFalse(rdp5_extReader.shotT(2), "Se disparo y no debia");
                Assertions.assertTrue(rdp5_extReader.shotT(1), "No se disparo y debia");
                Assertions.assertArrayEquals(new boolean[]{true,false,true}, rdp5_extReader.getSensitizedArray());
                Assertions.assertArrayEquals(new int[]{1, 0, 1, 0}, rdp5_extReader.getMark(),
                        "La red no evoluciono y debia");
                Assertions.assertTrue(rdp5_extReader.shotT(3), "No se disparo y debia");
                Assertions.assertArrayEquals(new boolean[]{true,true,false}, rdp5_extReader.getSensitizedArray());
                Assertions.assertArrayEquals(new int[]{1, 0, 0, 1}, rdp5_extReader.getMark(),
                        "La red no evoluciono y debia");
                Assertions.assertTrue(rdp5_extReader.shotT(2), "Se disparo y no debia");
                Assertions.assertArrayEquals(new boolean[]{true,false,false}, rdp5_extReader.getSensitizedArray());
                Assertions.assertArrayEquals(new int[]{0, 1, 1, 0}, rdp5_extReader.getMark(),
                        "La red no evoluciono y debia");
                Assertions.assertFalse(rdp5_extReader.shotT(2), "Se disparo y no debia");
                Assertions.assertTrue(rdp5_extReader.shotT(1), "No se disparo y debia");
                Assertions.assertTrue(rdp5_extReader.shotT(1), "No se disparo y debia");
                Assertions.assertArrayEquals(new boolean[]{true,false,true}, rdp5_extReader.getSensitizedArray());
                Assertions.assertArrayEquals(new int[]{2, 1, 1, 0}, rdp5_extReader.getMark(),
                        "La red no evoluciono y debia");
                Assertions.assertTrue(rdp5_extReader.shotT(3), "No se disparo y debia");
                Assertions.assertArrayEquals(new boolean[]{true,true,false}, rdp5_extReader.getSensitizedArray());
                Assertions.assertTrue(rdp5_extReader.shotT(2), "Se disparo y no debia");
                Assertions.assertArrayEquals(new boolean[]{true,false,true}, rdp5_extReader.getSensitizedArray());
                Assertions.assertArrayEquals(new int[]{1, 2, 1, 0}, rdp5_extReader.getMark(),
                        "La red no evoluciono y debia");
                Assertions.assertTrue(rdp5_extReader.shotT(3), "No se disparo y debia");
                Assertions.assertArrayEquals(new boolean[]{true,true,false}, rdp5_extReader.getSensitizedArray());
                Assertions.assertTrue(rdp5_extReader.shotT(2), "Se disparo y no debia");
                Assertions.assertArrayEquals(new boolean[]{true,false,false}, rdp5_extReader.getSensitizedArray());
                Assertions.assertArrayEquals(new int[]{0, 3, 1, 0}, rdp5_extReader.getMark(),
                        "La red no evoluciono y debia");
                Assertions.assertFalse(rdp5_extReader.shotT(3), "No se disparo y debia");
                Assertions.assertFalse(rdp5_extReader.shotT(2), "Se disparo y no debia");
                Assertions.assertArrayEquals(new int[]{0, 3, 1, 0}, rdp5_extReader.getMark(),
                        "La red no evoluciono y debia");
                Assertions.assertFalse(rdp5_extReader.shotT(3), "No se disparo y debia");
                Assertions.assertTrue(rdp5_extReader.shotT(1), "Se disparo y no debia");
                Assertions.assertArrayEquals(new boolean[]{true,false,true}, rdp5_extReader.getSensitizedArray());
                Assertions.assertTrue(rdp5_extReader.shotT(3), "No se disparo y debia");
                Assertions.assertArrayEquals(new boolean[]{true,false,false}, rdp5_extReader.getSensitizedArray());
                Assertions.assertArrayEquals(new int[]{1, 3, 0, 1}, rdp5_extReader.getMark(),
                        "La red no evoluciono y debia");
                Assertions.assertFalse(rdp5_extReader.shotT(2), "Se disparo y no debia");
                Assertions.assertArrayEquals(new boolean[]{true,false,false}, rdp5_extReader.getSensitizedArray());
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
    @Tag("TimeTest")
    @DisplayName("[ext Temporal] Checkeo de disparos con transiciones temporales:")
    void shotT_Temporal() {
        /*==========================================================
            RDP 1_extended: Extendida, con transiciones temporales
        ========================================================== */
        try {
            RDP rdp1_extend_TEMP = new RDP(JFILE_RDP1_TEMPORAL);
            Assertions.assertArrayEquals(new int[]{3, 0, 0, 0, 0}, rdp1_extend_TEMP.getMark());
            try {
                Assertions.assertFalse(rdp1_extend_TEMP.shotT(1), "Se disparo y no debia");
                Assertions.assertArrayEquals(new int[]{3, 0, 0, 0, 0}, rdp1_extend_TEMP.getMark(), "La red evoluciono y no debia");
                /* t1: FALSE -> Tiene tokens pero no paso el segundo de espera, no entra en la vetana de tiempo
                 * t2-t3-t4: FALSE -> No hay tokens para disparar
                 */
                Assertions.assertArrayEquals(new boolean[]{false, false, false, false}, rdp1_extend_TEMP.getSensitizedArray());
                Thread.sleep(1100);
                /* t1:       TRUE  -> Tiene tokens y entro en la ventana de tiempo
                 * t2-t3-t4: FALSE -> No hay tokens para disparar
                 */
                Assertions.assertTrue(rdp1_extend_TEMP.shotT(1), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{2, 1, 0, 1, 0}, rdp1_extend_TEMP.getMark(), "La red no evoluciono y debia");
                /* t1: TRUE  -> Tiene tokens y esta en la ventana de tiempo
                 * t2: FALSE -> Tiene tokens pero no entro en la ventana tiene que esperar un segundo
                 * t3: TRUE  -> Tiene tokenes y el arco lector esta habilitado
                 * t4: FALSE -> No hay tokens para disparar
                 */
                Assertions.assertArrayEquals(new boolean[]{true, false, true, false}, rdp1_extend_TEMP.getSensitizedArray());
                Assertions.assertTrue(rdp1_extend_TEMP.shotT(1), "No se disparo y debia");
                /* t1: FALSO -> Tiene tokens y pero P2 esta llena, tiene un maximo de 2 tokens, por eso esta en false
                 * t2: FALSE -> Tiene tokens pero no entro en la ventana tiene que esperar un segundo
                 * t3: TRUE  -> Tiene tokenes y el arco lector esta habilitado
                 * t4: FALSE -> No hay tokens para disparar
                 */
                Assertions.assertArrayEquals(new int[]{1, 2, 0, 2, 0}, rdp1_extend_TEMP.getMark(), "La red no evoluciono y debia");
                Assertions.assertArrayEquals(new boolean[]{false, false, true, false}, rdp1_extend_TEMP.getSensitizedArray());
                Assertions.assertTrue(rdp1_extend_TEMP.shotT(3), "No se disparo y debia");
                /* t1: FALSO -> Tiene tokens y pero P2 esta llena, tiene un maximo de 2 tokens, por eso esta en false
                 * t2: FALSE -> Tiene tokens pero no entro en la ventana tiene que esperar un segundo
                 * t3: TRUE  -> Tiene tokenes y el arco lector esta habilitado
                 * t4: FALSE -> No hay tokens para disparar, faltan los de p3
                 */
                Assertions.assertArrayEquals(new int[]{1, 2, 0, 1, 1}, rdp1_extend_TEMP.getMark(), "La red no evoluciono y debia");
                Assertions.assertArrayEquals(new boolean[]{false, false, true, false}, rdp1_extend_TEMP.getSensitizedArray());
                // Nos va a disparar por que no entra en la ventana de tiempo
                Assertions.assertFalse(rdp1_extend_TEMP.shotT(2), "Se disparo y no debia");
                Assertions.assertArrayEquals(new int[]{1, 2, 0, 1, 1}, rdp1_extend_TEMP.getMark(), "La red evoluciono y no debia");
                Assertions.assertArrayEquals(new boolean[]{false, false, true, false}, rdp1_extend_TEMP.getSensitizedArray());
                Thread.sleep(1100);
                /* t1: FALSO -> Tiene tokens y pero P2 esta llena, tiene un maximo de 2 tokens, por eso esta en false
                 * t2: TRUE  -> Tiene tokens y acaba de entrar a la ventana de tiempo
                 * t3: FALSE -> Tiene tokenes y el arco lector esta habilitado, pero salio de la ventana de tiempo
                 * t4: FALSE -> No hay tokens para disparar, faltan los de p3
                 */
                Assertions.assertArrayEquals(new boolean[]{false, true, false, false}, rdp1_extend_TEMP.getSensitizedArray());
                Assertions.assertTrue(rdp1_extend_TEMP.shotT(2), "No se disparo y debia");
                /* t1: FALSE -> Tiene tokens P2 tiene lugar, pero recien se acaba de sensibilizar no esta en la ventana
                 * t2: TRUE  -> Tiene tokens y acaba de entrar a la ventana de tiempo
                 * t3: FALSE -> Tiene tokenes y el arco lector esta habilitado pero esta fuera de la ventana de tiempo
                 * t4: FALSE -> Tiene tokens pero esta inhibido por P2
                 */
                Assertions.assertArrayEquals(new int[]{1, 1, 1, 1, 1}, rdp1_extend_TEMP.getMark(), "La red no evoluciono y debia");
                Assertions.assertArrayEquals(new boolean[]{false, true, false, false}, rdp1_extend_TEMP.getSensitizedArray());

                Thread.sleep(1100);
                /* t1: FALSE -> Tiene tokens P2 tiene lugar, y acaba de entrar en la ventana de tiempo
                 * t2: TRUE  -> Tiene tokens y acaba de entrar a la ventana de tiempo
                 * t3: FALSE -> Tiene tokenes y el arco lector esta habilitado pero fuera de la ventana de tiempo
                 * t4: FALSE -> Tiene tokens pero esta inhibido por P2
                 */
                Assertions.assertArrayEquals(new boolean[]{true, true, false, false}, rdp1_extend_TEMP.getSensitizedArray());
                Assertions.assertFalse(rdp1_extend_TEMP.shotT(4), "Se disparo y no debia");
                Assertions.assertArrayEquals(new int[]{1, 1, 1, 1, 1}, rdp1_extend_TEMP.getMark(), "La red evoluciono y no debia");
                Assertions.assertArrayEquals(new boolean[]{true, true, false, false}, rdp1_extend_TEMP.getSensitizedArray());
                Assertions.assertTrue(rdp1_extend_TEMP.shotT(2), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{1, 0, 2, 1, 1}, rdp1_extend_TEMP.getMark(), "La red no evoluciono y debia");
                Assertions.assertArrayEquals(new boolean[]{true, false, false, true}, rdp1_extend_TEMP.getSensitizedArray());
                Assertions.assertTrue(rdp1_extend_TEMP.shotT(4), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{2, 0, 1, 1, 0}, rdp1_extend_TEMP.getMark(), "La red no evoluciono y debia");
                Assertions.assertArrayEquals(new boolean[]{true, false, false, false}, rdp1_extend_TEMP.getSensitizedArray());
                Thread.sleep(1100);
                Assertions.assertTrue(rdp1_extend_TEMP.shotT(1), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{1, 1, 1, 2, 0}, rdp1_extend_TEMP.getMark(),"La red no evoluciono y debia");
                Thread.sleep(3100);
                Assertions.assertFalse(rdp1_extend_TEMP.shotT(2), "Se disparo y no debia");
                Assertions.assertArrayEquals(new int[]{1, 1, 1, 2, 0}, rdp1_extend_TEMP.getMark(), "La red evoluciono y no debia");

            } catch (ShotException e) {
                Assertions.fail();
            } catch (Exception e) {
                Assertions.fail(e.toString());
            }
        } catch (java.io.FileNotFoundException e) {
            Assertions.fail("No existe el archivo");
        } catch (ConfigException e) {
            Assertions.fail(e.toString());
        }
    }

}