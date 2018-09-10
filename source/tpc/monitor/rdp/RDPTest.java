package tpc.monitor.rdp;

import org.junit.jupiter.api.*;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste la Red de petri en base a los archivos ubicados en el directorio examples_rdp
 */
@Tag("RDP")
class RDPTest {

    /*
     * Lista de archivos usados en los test
     * */
    private static final String JFILE_RDP1_READERINH = "examples_rdp/ex1_extended_ReaderInh.json";
    private static final String JFILE_RDP1_TEMPORAL = "examples_rdp/ex1_extended_Temporal.json";
    private static final String JFILE_RDP1_MAXTOKENS = "examples_rdp/ex1_extended_MaxToken.json";
    private static final String JFILE_RDP1 = "examples_rdp/ex1_basic.json";
    private static final String JFILE_RDP2_MAXTOKENS = "examples_rdp/ex2_extended_maxToken.json";
    private static final String JFILE_RDP3_MAXTOKENS = "examples_rdp/ex3_extended_maxToken.json";
    private static final String JFILE_RDP4_READINH = "examples_rdp/ex4_extended_ReaderInh.json";


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
           tokens por plaza y suma arcos inhibidores y lectores
          ================================================ */
        try {
            RDP rdp1_extend_RH = new RDP(JFILE_RDP1_READERINH);
            Assertions.assertArrayEquals(new int[][]
                    {
                            {-1, 0, 0, 1},
                            {1, -1, 0, 0},
                            {0, 1, 0, -1},
                            {1, 0, -1, 0},
                            {0, 0, 1, -1}
                    }, rdp1_extend_RH.getMatrix(), "Red de petri 1 alterada para el test");
            Assertions.assertArrayEquals(new int[]{3, 0, 0, 0, 0}, rdp1_extend_RH.getMark(),
                    "Marca inicial 1 alterada para el test");
            if (!rdp1_extend_RH.isExtMaxToken()) {
                Assertions.fail("La red de petri no es extendida");
            } else {
                assertArrayEquals(new int[]{0, 2, 0, 0, 0}, rdp1_extend_RH.getExtMaxToken(),
                        "Tokens maximos alterados para test");
            }
            if (!rdp1_extend_RH.isExtReaderInh()) {
                Assertions.fail("La red de petri no es extendida para lectores escritores");
            } else {
                assertArrayEquals(new int[][]{
                                {0, 0, 0, 0},
                                {0, 0, 2, -1},
                                {0, 0, 0, 0},
                                {0, 0, 0, 0},
                                {0, 0, 0, 0}
                        }, rdp1_extend_RH.getExtReaaderInh(),
                        "Arcos lectores/inhibidores alterados para el test");
            }

        } catch (java.io.FileNotFoundException e) {
            Assertions.fail("No existe el archivo JSON");

        } catch (ConfigException e) {
            Assertions.fail(e.toString());
        }

        /*================================================
            RDP 1_extend: Extiende los valores maximos de
           tokens por plaza, suma arcos inhibidores y lectores y
            transiciones temporales
          ================================================ */
        try {
            RDP rdp1_extend_TEMP = new RDP(JFILE_RDP1_TEMPORAL);
            Assertions.assertArrayEquals(new int[][]
                    {
                            {-1, 0, 0, 1},
                            {1, -1, 0, 0},
                            {0, 1, 0, -1},
                            {1, 0, -1, 0},
                            {0, 0, 1, -1}
                    }, rdp1_extend_TEMP.getMatrix(), "Red de petri 1 alterada para el test");
            Assertions.assertArrayEquals(new int[]{3, 0, 0, 0, 0}, rdp1_extend_TEMP.getMark(),
                    "Marca inicial 1 alterada para el test");
            if (!rdp1_extend_TEMP.isExtMaxToken()) {
                Assertions.fail("La red de petri no es extendida");
            } else {
                assertArrayEquals(new int[]{0, 2, 0, 0, 0}, rdp1_extend_TEMP.getExtMaxToken(),
                        "Tokens maximos alterados para test");
            }
            if (!rdp1_extend_TEMP.isExtReaderInh()) {
                Assertions.fail("La red de petri no es extendida para lectores escritores");
            } else {
                assertArrayEquals(new int[][]{
                                {0, 0, 0, 0},
                                {0, 0, 2, -1},
                                {0, 0, 0, 0},
                                {0, 0, 0, 0},
                                {0, 0, 0, 0}
                        }, rdp1_extend_TEMP.getExtReaaderInh(),
                        "Arcos lectores/inhibidores alterados para el test");
            }
            if (!rdp1_extend_TEMP.isExtTemp()) {
                Assertions.fail("La red no extiende a transiciones temporales");
            } else {
                assertArrayEquals(new long[][]{
                                {1000, 1000, 0, 0},
                                {0, 3000, 1000, 0},
                        }, rdp1_extend_TEMP.getExtTemporal(),
                        "Tiempos alterados para el test");

            }

        } catch (java.io.FileNotFoundException e) {
            Assertions.fail("No existe el archivo JSON");

        } catch (ConfigException e) {
            Assertions.fail(e.toString());
        }

          /*================================================
            RDP 2_extend: Extiende los valores maximos de
                        tokens por plaza
          ================================================ */
        try {
            RDP rdp2_extend = new RDP(JFILE_RDP2_MAXTOKENS);
            Assertions.assertArrayEquals(new int[][]
                    {
                            {-1, 0, 1, 0, 0, 0},
                            {1, -1, -1, 0, 0, 0},
                            {0, 1, 0, 0, 0, 0},
                            {-1, 0, 0, 0, 1, 0},
                            {-1, 1, 0, -1, 1, 0},
                            {0, 1, 0, -1, 0, 0},
                            {0, 0, 0, -1, 0, 1},
                            {0, 0, 0, 1, -1, 0},
                            {0, 0, 0, 0, 1, -1}
                    }, rdp2_extend.getMatrix(), "Red de petri 2 alterada para el test");
            Assertions.assertArrayEquals(new int[]{2, 0, 0, 5, 1, 0, 3, 0, 0}, rdp2_extend.getMark(),
                    "Marca inicial 2 alterada para el test");
            assertArrayEquals(new int[]{0, 0, 0, 5, 1, 0, 0, 0, 0}, rdp2_extend.getExtMaxToken(), 
                    "Tokens maximos alterados para test");
        } catch (java.io.FileNotFoundException e) {
            Assertions.fail("No existe el archivo JSON");
        } catch (ConfigException e) {
            Assertions.fail(e.toString());
        }
        /*================================================
            RDP 4_extend: Extiende los arcos inhibidores
          ================================================ */
        try {
            RDP rdp1_extend_RH = new RDP(JFILE_RDP4_READINH);
            Assertions.assertArrayEquals(new int[][]
                    {
                            {-1, -1, 1, 1},
                            {1, 0, -1, 0},
                            {0, 1, 0, -1}
                    }, rdp1_extend_RH.getMatrix(), "Red de petri 4 alterada para el test");
            Assertions.assertArrayEquals(new int[]{5, 0, 0}, rdp1_extend_RH.getMark(),
                    "Marca inicial 4 alterada para el test");
            if (!rdp1_extend_RH.isExtMaxToken()) {
                Assertions.fail("La red de petri no es extendida");
            } else {
                assertArrayEquals(new int[]{0, 0, 3}, rdp1_extend_RH.getExtMaxToken(),
                        "Tokens maximos alterados para test");
            }
            if (!rdp1_extend_RH.isExtReaderInh()) {
                Assertions.fail("La red de petri no es extendida para arcos inhibidores");
            } else {
                assertArrayEquals(new int[][]{
                                {0, 0, 0, 0},
                                {-1, -1, 0, 0},
                                {-1, 0, 0, 0}
                        }, rdp1_extend_RH.getExtReaaderInh(),
                        "Arcos inhibidores alterados para el test");
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
        /*================================================
            RDP 1: Basica, no exendida en ninguna forma
          ================================================ */
        try {
            RDP rdp1 = new RDP(JFILE_RDP1);
            Assertions.assertArrayEquals(new int[]{1, 0, 0, 0, 0}, rdp1.getMark());

            try {
                Assertions.assertTrue(rdp1.shotT(1, true), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{1, 0, 0, 0, 0}, rdp1.getMark(),
                        "La red evoluciono y no debia");

                Assertions.assertTrue(rdp1.shotT(1, false), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{0, 1, 0, 1, 0}, rdp1.getMark(),
                        "La red evoluciono mal o no evoluciono");
            } catch (ShotException e) {
                Assertions.fail("La transicion es inexistente, error grave");
            }
        } catch (java.io.FileNotFoundException e) {
            Assertions.fail("No se puede crear la red de petri");
        } catch (ConfigException e) {
            Assertions.fail(e.toString());
        }


    }

    /**
     * Teste el disparo (shotT) con transiciones no sensibilizadas, disparos de prueba y con disparos de
     * evolucion de red, chequea que no evolucione el vector de marcado
     */
    @Test
    @DisplayName("Disparos no acertados chequeo de marcado")
    void shotT_notOk() {
        /*================================================
            RDP 1: Basica, no exendida en ninguna forma
          ================================================ */

        try {
            RDP rdp1 = new RDP(JFILE_RDP1);
            Assertions.assertArrayEquals(new int[]{1, 0, 0, 0, 0}, rdp1.getMark());
            try {
                Assertions.assertFalse(rdp1.shotT(2, true), "Se disparo y no debia");
                Assertions.assertArrayEquals(new int[]{1, 0, 0, 0, 0}, rdp1.getMark(),
                        "La red evoluciono y no debia");
                Assertions.assertFalse(rdp1.shotT(3, false), "Se disparo y no debia");
                Assertions.assertArrayEquals(new int[]{1, 0, 0, 0, 0}, rdp1.getMark(),
                        "La red evoluciono y no debia");
            } catch (ShotException e) {
                Assertions.fail();
            }
        } catch (java.io.FileNotFoundException e) {
            Assertions.fail("No existe el archivo");
        } catch (ConfigException e) {
            Assertions.fail(e.toString());
        }

    }

    /**
     * Testea el disparo (shotT) con transiciones sensibilizadas, con un limite de tokens maximos por plaza,
     * se intentara disparar a una plaza que se encuentre con el limite de tokens. Chequeara que el disparo no
     * se lleve a cabo.
     */
    @Test
    @Tag("extMT")
    @DisplayName("[ext MaxTokens] Disparos no acertado por cantidad maxima de tokens")
    void shotT_extendMarkTokens() {
        /*=========================================================
            RDP 1_extend: Extendida, con maxima cantidad de plazas
          ========================================================= */
        try {
            RDP rdp1_extend = new RDP(JFILE_RDP1_MAXTOKENS);
            Assertions.assertArrayEquals(new int[]{3, 0, 0, 0, 0}, rdp1_extend.getMark());
            try {
                Assertions.assertTrue(rdp1_extend.shotT(1, false), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{2, 1, 0, 1, 0}, rdp1_extend.getMark(),
                        "La red no evoluciono y debia");
                Assertions.assertTrue(rdp1_extend.shotT(1, false), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{1, 2, 0, 2, 0}, rdp1_extend.getMark(),
                        "La red no evoluciono y debia");
                Assertions.assertFalse(rdp1_extend.shotT(1, false), "Se disparo y no debia");
                Assertions.assertArrayEquals(new int[]{1, 2, 0, 2, 0}, rdp1_extend.getMark(),
                        "La red evoluciono y no debia");
                Assertions.assertTrue(rdp1_extend.shotT(2, false), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{1, 1, 1, 2, 0}, rdp1_extend.getMark(),
                        "La red no evoluciono y debia");
                Assertions.assertTrue(rdp1_extend.shotT(2, false), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{1, 0, 2, 2, 0}, rdp1_extend.getMark(),
                        "La red no evoluciono y debia");
                Assertions.assertTrue(rdp1_extend.shotT(1, false), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{0, 1, 2, 3, 0}, rdp1_extend.getMark(),
                        "La red no evoluciono y debia");
            } catch (ShotException e) {
                Assertions.fail();
            }
        } catch (java.io.FileNotFoundException e) {
            Assertions.fail("No existe el archivo");
        } catch (ConfigException e) {
            Assertions.fail(e.toString());
        }
        /*=========================================================
            RDP 3_extend: Extendida, con maxima cantidad de plazas
          ========================================================= */
        /*
        Testeo para verificar la eliminacion de Tokens de la red
         */
        try {
            RDP rdp3_extend = new RDP(JFILE_RDP3_MAXTOKENS);
            Assertions.assertArrayEquals(new int[]{3, 0, 0, 0, 0}, rdp3_extend.getMark());
            try {
                Assertions.assertTrue(rdp3_extend.shotT(1, false), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{2, 1, 0, 1, 0}, rdp3_extend.getMark(),
                        "La red no evoluciono y debia");
                Assertions.assertTrue(rdp3_extend.shotT(1, false), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{1, 2, 0, 2, 0}, rdp3_extend.getMark(),
                        "La red no evoluciono y debia");
                Assertions.assertTrue(rdp3_extend.shotT(2, false), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{1, 1, 1, 2, 0}, rdp3_extend.getMark(),
                        "La red no evoluciono y debia");
                Assertions.assertTrue(rdp3_extend.shotT(3, false), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{1, 1, 1, 1, 1}, rdp3_extend.getMark(),
                        "La red no evoluciono y debia");
                Assertions.assertTrue(rdp3_extend.shotT(4, false), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{1, 1, 0, 1, 0}, rdp3_extend.getMark(),
                        "La red no evoluciono y debia");

            } catch (ShotException e) {
                Assertions.fail();
            }
        } catch (FileNotFoundException e) {
            Assertions.fail("No existe el archivo");
        } catch (ConfigException e) {
            Assertions.fail(e.toString());
        }


    }

    /**
     * Testea el disparo (shotT) con transiciones sensibilizadas, con un limite de tokens maximos por plaza y con la
     * presencia de arcos inhibidores. Se intentara disparar una plaza con limite de tokens y otra con la influencia
     * del arco inhibidor. Chequeara que el disparo no se lleve a cabo.
     */
    @Test
    @Tag("extINH")
    @DisplayName("[ext ReaderInh] Disparos con arcos inhibidores")
    void shotT_extendedReaderInh() {
        /*=========================================================
            RDP 4_extend: Extendida, con arcos inhibidores
          ========================================================= */
        try {
            RDP rdp4_extend = new RDP(JFILE_RDP4_READINH);
            Assertions.assertArrayEquals(new int[]{5, 0, 0}, rdp4_extend.getMark());
            try {
                Assertions.assertTrue(rdp4_extend.shotT(2, false), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{4, 0, 1}, rdp4_extend.getMark(),
                        "La red no evoluciono y debia");
                Assertions.assertTrue(rdp4_extend.shotT(2, false), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{3, 0, 2}, rdp4_extend.getMark(),
                        "La red no evoluciono y debia");
                Assertions.assertFalse(rdp4_extend.shotT(1, false), "Se disparo y no debia");
                Assertions.assertArrayEquals(new int[]{3, 0, 2}, rdp4_extend.getMark(),
                        "La red evoluciono y no debia");
                Assertions.assertTrue(rdp4_extend.shotT(2, false), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{2, 0, 3}, rdp4_extend.getMark(),
                        "La red no evoluciono y debia");
                Assertions.assertFalse(rdp4_extend.shotT(2, false), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{2, 0, 3}, rdp4_extend.getMark(),
                        "La red no evoluciono y debia");
            } catch (ShotException e) {
                Assertions.fail();
            }
        } catch (java.io.FileNotFoundException e) {
            Assertions.fail("No existe el archivo");
        } catch (ConfigException e) {
            Assertions.fail(e.toString());
        }
    }

    /**
     * Chequea los vectores de sensibilidad de la red antes y despues del disparo
     */
    @Test
    @DisplayName("Checkeos de sensibilidad de transiciones antes y despues de disparos")
    void getSensitizedArray_2Shot() {
        /*================================================
            RDP 1: Basica, no exendida en ninguna forma
          ================================================ */
        try {
            RDP rdp1 = new RDP(JFILE_RDP1);
            try {
                Assertions.assertFalse(rdp1.shotT(2, true), "Se disparo y no debia");
                Assertions.assertArrayEquals(new boolean[]{true, false, false, false}, rdp1.getSensitizedArray(),
                        "La red no evoluciono y vector de sensibilidad es incorrecto");

                Assertions.assertFalse(rdp1.shotT(3, false), "Se disparo y no debia");
                Assertions.assertArrayEquals(new boolean[]{true, false, false, false}, rdp1.getSensitizedArray(),
                        "La red evoluciono y el vector de sensibilidad es incorrecto");
            } catch (ShotException e) {
                Assertions.fail();
            }
        } catch (java.io.FileNotFoundException e) {
            Assertions.fail("No existe el archivo");
        } catch (ConfigException e) {
            Assertions.fail(e.toString());
        }

    }

    /**
     * Chequeo de sensivilidad para transiciones en redes limitadas en plaza
     */
    @Test
    @Tag("extMT")
    @DisplayName("[ext MaxTokens] Checkeos de sensibilidad de transiciones antes y despues de disparos")
    void getSensitizedArray_2Shot_extendMaxTokens() {
        /*=========================================================
            RDP 1_extend: Extendida, con limite de token por plazas
          ========================================================= */
        try {
            RDP rdp1_extend = new RDP(JFILE_RDP1_MAXTOKENS);
            try {
                Assertions.assertArrayEquals(new boolean[]{true, false, false, false}, rdp1_extend.getSensitizedArray(),
                        "La red no evoluciono y vector de sensibilidad es incorrecto");
                Assertions.assertTrue(rdp1_extend.shotT(1, false), "No se disparo y debia");
                Assertions.assertArrayEquals(new boolean[]{true, true, true, false}, rdp1_extend.getSensitizedArray(),
                        "La red no evoluciono y vector de sensibilidad es incorrecto");

                Assertions.assertTrue(rdp1_extend.shotT(1, false), "No se disparo y debia");
                Assertions.assertArrayEquals(new boolean[]{false, true, true, false}, rdp1_extend.getSensitizedArray(),
                        "La red no evoluciono y vector de sensibilidad es incorrecto");
            } catch (ShotException e) {
                Assertions.fail();
            }
        } catch (java.io.FileNotFoundException e) {
            Assertions.fail("No se puede crear la red de petri");
        } catch (ConfigException e) {
            Assertions.fail(e.toString());
        }

    }

    /**
     * Chequeo en sensibilizado para transiciones en redes de lectores inhibidores y max plazas
     */
    @Test
    @Tag("extINH")
    @DisplayName("[ext ReaderInh] Checkeos de sensibilidad de transiciones antes y despues de disparos")
    void getSensitizedArray_2Shot_extendReaderInh() {

        /*=========================================================
            RDP 1_extend: Extendida, con limite de token por plazas
                    con arcos lectores e inhibidores
          ========================================================= */
        try {
            RDP rdp1_extendRH = new RDP(JFILE_RDP1_READERINH);
            try {
                Assertions.assertArrayEquals(new boolean[]{true, false, false, false}, rdp1_extendRH.getSensitizedArray(),
                        "La red no evoluciono y vector de sensibilidad es incorrecto");

                Assertions.assertTrue(rdp1_extendRH.shotT(1, false), "No se disparo y debia");
                Assertions.assertArrayEquals(new boolean[]{true, true, false, false}, rdp1_extendRH.getSensitizedArray(),
                        "La red no evoluciono y vector de sensibilidad es incorrecto");
                Assertions.assertTrue(rdp1_extendRH.shotT(1, false), "No se disparo y debia");
                Assertions.assertArrayEquals(new boolean[]{false, true, true, false}, rdp1_extendRH.getSensitizedArray(),
                        "La red no evoluciono y vector de sensibilidad es incorrecto");
                Assertions.assertTrue(rdp1_extendRH.shotT(2, false), "No se disparo y debia");
                Assertions.assertArrayEquals(new boolean[]{true, true, false, false}, rdp1_extendRH.getSensitizedArray(),
                        "La red no evoluciono y vector de sensibilidad es incorrecto");
                Assertions.assertTrue(rdp1_extendRH.shotT(1, false), "No se disparo y debia");
                Assertions.assertArrayEquals(new boolean[]{false, true, true, false}, rdp1_extendRH.getSensitizedArray(),
                        "La red no evoluciono y vector de sensibilidad es incorrecto");
                Assertions.assertTrue(rdp1_extendRH.shotT(3, false), "No se disparo y debia");
                Assertions.assertArrayEquals(new boolean[]{false, true, true, false}, rdp1_extendRH.getSensitizedArray(),
                        "La red no evoluciono y vector de sensibilidad es incorrecto");
                Assertions.assertTrue(rdp1_extendRH.shotT(2, false), "No se disparo y debia");
                Assertions.assertArrayEquals(new boolean[]{false, true, false, false}, rdp1_extendRH.getSensitizedArray(),
                        "La red no evoluciono y vector de sensibilidad es incorrecto");
                Assertions.assertTrue(rdp1_extendRH.shotT(2, false), "No se disparo y debia");
                Assertions.assertArrayEquals(new boolean[]{false, false, false, true}, rdp1_extendRH.getSensitizedArray(),
                        "La red no evoluciono y vector de sensibilidad es incorrecto");
                Assertions.assertTrue(rdp1_extendRH.shotT(4, false), "No se disparo y debia");
                Assertions.assertArrayEquals(new boolean[]{true, false, false, false}, rdp1_extendRH.getSensitizedArray(),
                        "La red no evoluciono y vector de sensibilidad es incorrecto");
            } catch (ShotException e) {
                Assertions.fail();
            }
        } catch (java.io.FileNotFoundException e) {
            Assertions.fail("No se puede crear la red de petri");
        } catch (ConfigException e) {
            Assertions.fail(e.toString());
        }

        /*=========================================================
            RDP 4_extend: Extendida, con limite de token por plazas
                    con arcos inhibidores
          ========================================================= */
        try {
            RDP rdp4_extendRH = new RDP(JFILE_RDP4_READINH);
            try {
                Assertions.assertArrayEquals(new boolean[]{true, true, false, false}, rdp4_extendRH.getSensitizedArray(),
                        "La red no evoluciono y vector de sensibilidad es incorrecto");

                Assertions.assertTrue(rdp4_extendRH.shotT(2, false), "No se disparo y debia");
                Assertions.assertArrayEquals(new boolean[]{false, true, false, true}, rdp4_extendRH.getSensitizedArray(),
                        "La red no evoluciono y vector de sensibilidad es incorrecto");
                Assertions.assertTrue(rdp4_extendRH.shotT(4, false), "No se disparo y debia");
                Assertions.assertArrayEquals(new boolean[]{true, true, false, false}, rdp4_extendRH.getSensitizedArray(),
                        "La red no evoluciono y vector de sensibilidad es incorrecto");

                Assertions.assertTrue(rdp4_extendRH.shotT(1, false), "No se disparo y debia");
                Assertions.assertArrayEquals(new boolean[]{false, false, true, false}, rdp4_extendRH.getSensitizedArray(),
                        "La red no evoluciono y vector de sensibilidad es incorrecto");
                Assertions.assertTrue(rdp4_extendRH.shotT(3, false), "No se disparo y debia");
                Assertions.assertArrayEquals(new boolean[]{true, true, false, false}, rdp4_extendRH.getSensitizedArray(),
                        "La red no evoluciono y vector de sensibilidad es incorrecto");
            } catch (ShotException e) {
                Assertions.fail();
            }
        } catch (java.io.FileNotFoundException e) {
            Assertions.fail("No se puede crear la red de petri");
        } catch (ConfigException e) {
            Assertions.fail(e.toString());
        }


    }


    /**
     * Chequea el metodo de agregado de tokens a plazas que es posible agregar tokens, para
     * despues intentar agregar a la misma superando el numero maximo de tokens para dicha plaza.
     */
    @Test
    @Tag("extMT")
    @DisplayName("[ext MaxTokens] Checkeo del agregado de tokens a una determinada plaza:")
    void AddTokens_extendMaxTokens() {
         /*=========================================================
            RDP 1_extend: Extendida, con maxima cantidad de plazas
          ========================================================= */
        try {
            RDP rdp1_extend = new RDP(JFILE_RDP1_MAXTOKENS);
            try {
                Assertions.assertTrue(rdp1_extend.AddToken(2, 1), "No se agrego y debia");
                Assertions.assertArrayEquals(new int[]{3, 1, 0, 0, 0}, rdp1_extend.getMark(), "La red no cambio, y debia");
                Assertions.assertFalse(rdp1_extend.AddToken(2, 5), "Se agregaron tokens y no debia");
                Assertions.assertArrayEquals(new int[]{3, 1, 0, 0, 0}, rdp1_extend.getMark(), "La red cambio, y  no debia");
                Assertions.assertTrue(rdp1_extend.AddToken(5, 3), "No se agrego y debia");
                Assertions.assertArrayEquals(new int[]{3, 1, 0, 0, 3}, rdp1_extend.getMark(), "La red no cambio, y debia");
            } catch (TokenException e) {
                Assertions.fail();
            }
        } catch (java.io.FileNotFoundException e) {
            Assertions.fail("No se pudo crear la red de petri.");
        } catch (ConfigException e) {
            Assertions.fail(e.toString());
        }
    }

    /**
     * Chequea el metodo de seteo de tokens a plazas. Se verifica que no sea negativa la
     * cantidad y que la plaza exista. Si se cumplen las respectivas condiciones se reemplaza
     * el numero de tokens por el elegido.
     */
    @Test
    @Tag("extMT")
    @DisplayName("[ext MaxTokens] Checkeo del seteo de tokens a una determinada plaza:")
    void SetTokens_extendMaxTokens() {
         /*=========================================================
            RDP 1_extend: Extendida, con maxima cantidad de plazas
          ========================================================= */
        try {
            RDP rdp1_extend = new RDP(JFILE_RDP1_MAXTOKENS);
            try {
                Assertions.assertTrue(rdp1_extend.SetToken(2, 1), "No se modifico la marca y debia");
                Assertions.assertArrayEquals(new int[]{3, 1, 0, 0, 0}, rdp1_extend.getMark(), "La red no cambio, y debia");
                Assertions.assertTrue(rdp1_extend.SetToken(2, 2), "No se modifico la marca y debia");
                Assertions.assertArrayEquals(new int[]{3, 2, 0, 0, 0}, rdp1_extend.getMark(), "La red no cambio, y debia");
                Assertions.assertTrue(rdp1_extend.SetToken(5, 3), "No se agrego y debia");
                Assertions.assertArrayEquals(new int[]{3, 2, 0, 0, 3}, rdp1_extend.getMark(), "La red no cambio, y debia");
                Assertions.assertTrue(rdp1_extend.SetToken(5, 0), "No se modifico la marca y debia");
                Assertions.assertArrayEquals(new int[]{3, 2, 0, 0, 0}, rdp1_extend.getMark(), "La red no cambio, y debia");
                Assertions.assertFalse(rdp1_extend.SetToken(2, 9), "Se modifico la marca y no debia");
                Assertions.assertArrayEquals(new int[]{3, 2, 0, 0, 0}, rdp1_extend.getMark(), "La red cambio, y no debia");
            } catch (TokenException e) {
                Assertions.fail();
            }
        } catch (java.io.FileNotFoundException e) {
            Assertions.fail("No se pudo crear la red de petri.");
        } catch (ConfigException e) {
            Assertions.fail(e.toString());
        }
    }

    /**
     * Chequea que las transiciones se disparen (o no), teniendo en cuenta el intervalo de
     * tiempo que con la cual se creo la RDP.
     */
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
                Assertions.assertFalse(rdp1_extend_TEMP.shotT(1, false), "Se disparo y no debia");
                Assertions.assertArrayEquals(new int[]{3, 0, 0, 0, 0}, rdp1_extend_TEMP.getMark(), "La red evoluciono y no debia");
                /* t1: FALSE -> Tiene tokens pero no paso el segundo de espera, no entra en la vetana de tiempo
                 * t2-t3-t4: FALSE -> No hay tokens para disparar
                 */
                Assertions.assertArrayEquals(new boolean[]{false, false, false, false}, rdp1_extend_TEMP.getSensitizedArray());
                Thread.sleep(1100);
                /* t1:       TRUE  -> Tiene tokens y entro en la ventana de tiempo
                 * t2-t3-t4: FALSE -> No hay tokens para disparar
                 */
                Assertions.assertTrue(rdp1_extend_TEMP.shotT(1, false), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{2, 1, 0, 1, 0}, rdp1_extend_TEMP.getMark(), "La red no evoluciono y debia");
                /* t1: TRUE  -> Tiene tokens y esta en la ventana de tiempo
                 * t2: FALSE -> Tiene tokens pero no entro en la ventana tiene que esperar un segundo
                 * t3: FALSE -> Tiene tokenes pero tiene que haber 2 tokens en P2 por el arco lector
                 * t4: FALSE -> No hay tokens para disparar
                 */
                Assertions.assertArrayEquals(new boolean[]{true, false, false, false}, rdp1_extend_TEMP.getSensitizedArray());
                Assertions.assertTrue(rdp1_extend_TEMP.shotT(1, false), "No se disparo y debia");
                /* t1: FALSO -> Tiene tokens y pero P2 esta llena, tiene un maximo de 2 tokens, por eso esta en false
                 * t2: FALSE -> Tiene tokens pero no entro en la ventana tiene que esperar un segundo
                 * t3: TRUE  -> Tiene tokenes y el arco lector esta habilitado
                 * t4: FALSE -> No hay tokens para disparar
                 */
                Assertions.assertArrayEquals(new int[]{1, 2, 0, 2, 0}, rdp1_extend_TEMP.getMark(), "La red no evoluciono y debia");
                Assertions.assertArrayEquals(new boolean[]{false, false, true, false}, rdp1_extend_TEMP.getSensitizedArray());
                Assertions.assertTrue(rdp1_extend_TEMP.shotT(3, false), "No se disparo y debia");
                /* t1: FALSO -> Tiene tokens y pero P2 esta llena, tiene un maximo de 2 tokens, por eso esta en false
                 * t2: FALSE -> Tiene tokens pero no entro en la ventana tiene que esperar un segundo
                 * t3: TRUE  -> Tiene tokenes y el arco lector esta habilitado
                 * t4: FALSE -> No hay tokens para disparar, faltan los de p3
                 */
                Assertions.assertArrayEquals(new int[]{1, 2, 0, 1, 1}, rdp1_extend_TEMP.getMark(), "La red no evoluciono y debia");
                Assertions.assertArrayEquals(new boolean[]{false, false, true, false}, rdp1_extend_TEMP.getSensitizedArray());
                // Nos va a disparar por que no entra en la ventana de tiempo
                Assertions.assertFalse(rdp1_extend_TEMP.shotT(2, false), "Se disparo y no debia");
                Assertions.assertArrayEquals(new int[]{1, 2, 0, 1, 1}, rdp1_extend_TEMP.getMark(), "La red evoluciono y no debia");
                Assertions.assertArrayEquals(new boolean[]{false, false, true, false}, rdp1_extend_TEMP.getSensitizedArray());
                Thread.sleep(1100);
                /* t1: FALSO -> Tiene tokens y pero P2 esta llena, tiene un maximo de 2 tokens, por eso esta en false
                 * t2: TRUE  -> Tiene tokens y acaba de entrar a la ventana de tiempo
                 * t3: FALSE -> Tiene tokenes y el arco lector esta habilitado, pero salio de la ventana de tiempo
                 * t4: FALSE -> No hay tokens para disparar, faltan los de p3
                 */
                Assertions.assertArrayEquals(new boolean[]{false, true, false, false}, rdp1_extend_TEMP.getSensitizedArray());
                Assertions.assertTrue(rdp1_extend_TEMP.shotT(2, false), "No se disparo y debia");
                /* t1: FALSE -> Tiene tokens P2 tiene lugar, pero recien se acaba de sensibilizar no esta en la ventana
                 * t2: TRUE  -> Tiene tokens y acaba de entrar a la ventana de tiempo
                 * t3: FALSE -> Tiene tokenes y el arco lector NO esta habilitado
                 * t4: FALSE -> Tiene tokens pero esta inhibido por P2
                 */
                Assertions.assertArrayEquals(new boolean[]{false, true, false, false}, rdp1_extend_TEMP.getSensitizedArray());
                Assertions.assertArrayEquals(new int[]{1, 1, 1, 1, 1}, rdp1_extend_TEMP.getMark(), "La red no evoluciono y debia");
                Thread.sleep(1100);
                /* t1: FALSE -> Tiene tokens P2 tiene lugar, y acaba de entrar en la ventana de tiempo
                 * t2: TRUE  -> Tiene tokens y acaba de entrar a la ventana de tiempo
                 * t3: FALSE -> Tiene tokenes y el arco lector NO esta habilitado
                 * t4: FALSE -> Tiene tokens pero esta inhibido por P2
                 */
                Assertions.assertArrayEquals(new boolean[]{true, true, false, false}, rdp1_extend_TEMP.getSensitizedArray());
                Assertions.assertFalse(rdp1_extend_TEMP.shotT(4, false), "Se disparo y no debia");
                Assertions.assertArrayEquals(new int[]{1, 1, 1, 1, 1}, rdp1_extend_TEMP.getMark(), "La red evoluciono y no debia");
                Assertions.assertArrayEquals(new boolean[]{true, true, false, false}, rdp1_extend_TEMP.getSensitizedArray());
                Assertions.assertTrue(rdp1_extend_TEMP.shotT(2, false), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{1, 0, 2, 1, 1}, rdp1_extend_TEMP.getMark(), "La red no evoluciono y debia");
                Assertions.assertArrayEquals(new boolean[]{true, false, false, true}, rdp1_extend_TEMP.getSensitizedArray());
                Assertions.assertTrue(rdp1_extend_TEMP.shotT(4, false), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{2, 0, 1, 1, 0}, rdp1_extend_TEMP.getMark(), "La red no evoluciono y debia");
                Assertions.assertArrayEquals(new boolean[]{true, false, false, false}, rdp1_extend_TEMP.getSensitizedArray());
                Thread.sleep(1100);
                Assertions.assertTrue(rdp1_extend_TEMP.shotT(1, false), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{1, 1, 1, 2, 0}, rdp1_extend_TEMP.getMark(),"La red no evoluciono y debia");
                Thread.sleep(3100);
                Assertions.assertFalse(rdp1_extend_TEMP.shotT(2, false), "Se disparo y no debia");
                Assertions.assertArrayEquals(new int[]{1, 1, 1, 2, 0}, rdp1_extend_TEMP.getMark(), "La red evoluciono y no debia");

            } catch (ShotException e) {
                Assertions.fail();
            } catch (Exception e) {
                Assertions.fail("Thread Exception");
            }
        } catch (java.io.FileNotFoundException e) {
            Assertions.fail("No existe el archivo");
        } catch (ConfigException e) {
            Assertions.fail(e.toString());
        }
    }

    /**
     * Chequea las excepciones del programa
     */
    @Test
    @DisplayName("Testeo de excepciones")
    @Tag("exception")
    void checkThrowExepcion() {

        /*================================================
            RDP 1: Basica, no exendida en ninguna forma
          ================================================ */

        try {
            RDP rdp1 = new RDP(JFILE_RDP1);
            Assertions.assertThrows(ShotException.class, () -> rdp1.shotT(0, true));
            Assertions.assertThrows(ShotException.class, () -> rdp1.shotT(0, false));
            Assertions.assertThrows(ShotException.class, () -> rdp1.shotT(5, true));
            Assertions.assertThrows(ShotException.class, () -> rdp1.shotT(5, false));
        } catch (java.io.FileNotFoundException e) {
            Assertions.fail("No existe el archivo");
        } catch (ConfigException e) {
            Assertions.fail(e.toString());
        }

        /*================================================
            RDP 1: Extendida, maximo de tokens por plaza
          ================================================ */
        try {
            RDP rdp1_extend = new RDP(JFILE_RDP1_MAXTOKENS);
            Assertions.assertThrows(TokenException.class, () -> rdp1_extend.AddToken(1, -1));
            Assertions.assertThrows(TokenException.class, () -> rdp1_extend.AddToken(0, 3));
        } catch (java.io.FileNotFoundException e) {
            Assertions.fail("No se puede crear la red de Petri");
        } catch (ConfigException e) {
            Assertions.fail(e.toString());
        }
          /*================================================
            RDP 1: Extendida, maximo de tokens por plaza
          ================================================ */
        try {
            RDP rdp1_extend = new RDP(JFILE_RDP1_MAXTOKENS);
            Assertions.assertThrows(TokenException.class, () -> rdp1_extend.SetToken(1, -1));
            Assertions.assertThrows(TokenException.class, () -> rdp1_extend.SetToken(0, 3));
        } catch (java.io.FileNotFoundException e) {
            Assertions.fail("No se puede crear la red de Petri");
        } catch (ConfigException e) {
            Assertions.fail(e.toString());
        }

    }


}