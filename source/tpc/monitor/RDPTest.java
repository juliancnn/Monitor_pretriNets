package tpc.monitor;

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
    private static final String FILE_RDP1_MATRIX = "examples_rdp/ex1_rdp";
    private static final String FILE_RDP1_MARK = "examples_rdp/ex1_mark";
    private static final String FILE_RDP1_MAXTOKENS = "examples_rdp/ex1_extend_maxTokens";

    /**
     * Verifica que los archivos de la red sean los esperados para los test
     *
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
            RDP rdp1 = new RDP(FILE_RDP1_MATRIX, FILE_RDP1_MARK);
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
        }catch (RDP.ConfigException e){
            Assertions.fail("No se puede crear la red de petri");
        }

          /*================================================
            RDP 1_extend: Extiende los valores maximos de
                        tokens por plaza
          ================================================ */
        try {
            RDP rdp1_extend = new RDP(FILE_RDP1_MATRIX, FILE_RDP1_MAXTOKENS);
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
            assertArrayEquals(new int[]{0,2,0,0,0}, rdp1_extend.getExtMaxToken(), "Tokens maximos alterados para test" );
        }catch (RDP.ConfigException e){
            Assertions.fail("No se puede crear la red de petri");
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
        try{
            RDP rdp1 = new RDP(FILE_RDP1_MATRIX, FILE_RDP1_MARK);
            Assertions.assertArrayEquals(new int[]{1,0,0,0,0}, rdp1.getMark());

            try{
                Assertions.assertTrue(rdp1.shotT(1,true), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{1,0,0,0,0}, rdp1.getMark(),
                        "La red evoluciono y no debia");

                Assertions.assertTrue(rdp1.shotT(1,false),"No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{0,1,0,1,0}, rdp1.getMark(),
                        "La red evoluciono mal o no evoluciono");
            }catch (RDP.ShotException e){
                Assertions.fail("La transicion es inexistente, error grave");
            }
        }catch (RDP.ConfigException e){
            Assertions.fail("No se puede crear la red de petri");
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

        try{
            RDP rdp1 = new RDP(FILE_RDP1_MATRIX, FILE_RDP1_MARK);
            Assertions.assertArrayEquals(new int[]{1,0,0,0,0}, rdp1.getMark());
            try{
                Assertions.assertFalse(rdp1.shotT(2,true), "Se disparo y no debia");
                Assertions.assertArrayEquals(new int[]{1,0,0,0,0}, rdp1.getMark(),
                        "La red evoluciono y no debia");
                Assertions.assertFalse(rdp1.shotT(3,false),"Se disparo y no debia");
                Assertions.assertArrayEquals(new int[]{1,0,0,0,0}, rdp1.getMark(),
                        "La red evoluciono y no debia");
            }catch (RDP.ShotException e){
                Assertions.fail();
            }
        }catch (RDP.ConfigException e){
            Assertions.fail("No se puede crear la red de petri");
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
        try{
            RDP rdp1_extend = new RDP(FILE_RDP1_MATRIX, FILE_RDP1_MAXTOKENS);
            Assertions.assertArrayEquals(new int[]{3,0,0,0,0}, rdp1_extend.getMark());
            try{
                Assertions.assertTrue(rdp1_extend.shotT(1,false), "No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{2,1,0,1,0}, rdp1_extend.getMark(),
                        "La red no evoluciono y debia");
                Assertions.assertTrue(rdp1_extend.shotT(1,false),"No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{1,2,0,2,0}, rdp1_extend.getMark(),
                        "La red no evoluciono y debia");
                Assertions.assertFalse(rdp1_extend.shotT(1,false),"Se disparo y no debia");
                Assertions.assertArrayEquals(new int[]{1,2,0,2,0}, rdp1_extend.getMark(),
                        "La red evoluciono y no debia");
                Assertions.assertTrue(rdp1_extend.shotT(2,false),"No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{1,1,1,2,0}, rdp1_extend.getMark(),
                        "La red no evoluciono y debia");
                Assertions.assertTrue(rdp1_extend.shotT(2,false),"No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{1,0,2,2,0}, rdp1_extend.getMark(),
                        "La red no evoluciono y debia");
                Assertions.assertTrue(rdp1_extend.shotT(1,false),"No se disparo y debia");
                Assertions.assertArrayEquals(new int[]{0,1,2,3,0}, rdp1_extend.getMark(),
                        "La red no evoluciono y debia");
            }catch (RDP.ShotException e){
                Assertions.fail();
            }
        }catch (RDP.ConfigException e){
            Assertions.fail("No se puede crear la red de petri");
        }

    }

    /**
     * Chequea los vectores de sensibilidad de la red antes y despues del disparo
     * */
    @Test
    @DisplayName("Checkeos de sensibilidad de transiciones antes y despues de disparos")
    void getSensitizedArray_2Shot() {
        /*================================================
            RDP 1: Basica, no exendida en ninguna forma
          ================================================ */
        try{
            RDP rdp1 = new RDP(FILE_RDP1_MATRIX, FILE_RDP1_MARK);
            try{
                Assertions.assertFalse(rdp1.shotT(2,true), "Se disparo y no debia");
                Assertions.assertArrayEquals(new boolean[]{true, false, false, false}, rdp1.getSensitizedArray(),
                        "La red no evoluciono y el vector de sensibilidad es incorrecto");

                Assertions.assertFalse(rdp1.shotT(3,false),"Se disparo y no debia");
                Assertions.assertArrayEquals(new boolean[]{true, false, false, false}, rdp1.getSensitizedArray(),
                        "La red evoluciono y el vector de sensibilidad es incorrecto");
            }catch (RDP.ShotException e){
                Assertions.fail();
            }
        }catch (RDP.ConfigException e){
            Assertions.fail("No se puede crear la red de petri");
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
        try{
            RDP rdp1_extend = new RDP(FILE_RDP1_MATRIX, FILE_RDP1_MAXTOKENS);
            try{
                Assertions.assertArrayEquals(new boolean[]{true, false, false, false}, rdp1_extend.getSensitizedArray(),
                        "La red no evoluciono y el vector de sensibilidad es incorrecto");
                Assertions.assertTrue(rdp1_extend.shotT(1,false), "No se disparo y debia");
                Assertions.assertArrayEquals(new boolean[]{true, true, true, false}, rdp1_extend.getSensitizedArray(),
                        "La red no evoluciono y el vector de sensibilidad es incorrecto");

                Assertions.assertTrue(rdp1_extend.shotT(1,false), "No se disparo y debia");
                Assertions.assertArrayEquals(new boolean[]{false, true, true, false}, rdp1_extend.getSensitizedArray(),
                        "La red no evoluciono y el vector de sensibilidad es incorrecto");
            }catch (RDP.ShotException e){
                Assertions.fail();
            }
        }catch (RDP.ConfigException e){
            Assertions.fail("No se puede crear la red de petri");
        }

    }

    /**
     * Cheque que no se puedan disparar transiciones inexistentes y que ademas se lanze la excepcion adecuada
     * al realizar esto, esto es cuando la transicion no esta entre el rango de transiciones permitidas
     * desde la 1, hasta la ultima que posee
     * */
    @Test
    @DisplayName("Exepciones en inexistencia de transicion")
    @Tag("exception")
    void checkThrowExepcionWronShot(){
        /*================================================
            RDP 1: Basica, no exendida en ninguna forma
          ================================================ */
        try{
            RDP rdp1 = new RDP(FILE_RDP1_MATRIX, FILE_RDP1_MARK);
            Assertions.assertThrows(RDP.ShotException.class,()->rdp1.shotT(0,true));
            Assertions.assertThrows(RDP.ShotException.class,()->rdp1.shotT(0,false));
            Assertions.assertThrows(RDP.ShotException.class,()->rdp1.shotT(5,true));
            Assertions.assertThrows(RDP.ShotException.class,()->rdp1.shotT(5,false));
        }catch (RDP.ConfigException e){
            Assertions.fail("No se puede crear la red de petri");
        }

    }

}