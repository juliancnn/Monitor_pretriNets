package tpc.monitor.rdp;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste la Red de petri en base a los archivos ubicados en el directorio examples_rdp
 */
@Tag("RDP")
class RDPTest {

    /*
     * Lista de archivos usados en los test
     * */
    private static final String JFILE_RDP1_MAXTOKENS = "examples_rdp/ex1_extended_MaxToken.json";
    private static final String JFILE_RDP1 = "examples_rdp/ex1_basic.json";
    private static final String JFILE_RDP2_MAXTOKENS = "examples_rdp/ex2_extended_maxToken.json";

    /**
     * Verifica que los archivos de la red sean los esperados para los test
     * <p>
     * El checkeo con archivos mejora la seguridad de la interface de la red, impidiendo cambios en la misma,
     * ya que no se expone como modificarla de manera dinamica, ya que esto no es contemplado en el
     * algoritmo de la red. Tambien se asegura que todas las redes pasen por todos los test.
     */
    @BeforeAll
    @Tag("files")
    public static void checkInitFiles() {
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
                assertArrayEquals(new int[]{0, 2, 0, 0, 0}, rdp1_extend.getExtMaxToken(), "Tokens maximos alterados para test");
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
            assertArrayEquals(new int[]{0, 0, 0, 5, 1, 0, 0, 0, 0}, rdp2_extend.getExtMaxToken(), "Tokens maximos alterados para test");
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
                        "La red no evoluciono y el vector de sensibilidad es incorrecto");

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
     *
     */
    @Test
    @Tag("extMT")
    @DisplayName("[ext MaxTokens]Checkeos de sensibilidad de transiciones antes y despues de disparos")
    void getSensitizedArray_2Shot_extendMaxTokens() {
        /*=========================================================
            RDP 1_extend: Extendida, con limite de token por plazas
          ========================================================= */
        try {
            RDP rdp1_extend = new RDP(JFILE_RDP1_MAXTOKENS);
            try {
                Assertions.assertArrayEquals(new boolean[]{true, false, false, false}, rdp1_extend.getSensitizedArray(),
                        "La red no evoluciono y el vector de sensibilidad es incorrecto");
                Assertions.assertTrue(rdp1_extend.shotT(1, false), "No se disparo y debia");
                Assertions.assertArrayEquals(new boolean[]{true, true, true, false}, rdp1_extend.getSensitizedArray(),
                        "La red no evoluciono y el vector de sensibilidad es incorrecto");

                Assertions.assertTrue(rdp1_extend.shotT(1, false), "No se disparo y debia");
                Assertions.assertArrayEquals(new boolean[]{false, true, true, false}, rdp1_extend.getSensitizedArray(),
                        "La red no evoluciono y el vector de sensibilidad es incorrecto");
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
    @DisplayName("[ext MaxTokens]Checkeo del agregado de tokens a una determinada plaza:")
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