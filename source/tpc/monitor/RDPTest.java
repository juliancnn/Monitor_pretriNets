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

    }

    /**
     * Teste el disparo (shotT) con transiciones no sensibilizadas, disparos de prueba y con disparos de
     * evolucion de red, chequea que no evolucione el marcado
     */
    @Test
    @DisplayName("Disparos no acertados chequeo de marcado")
    void shotT_notOk() {
        /*================================================
            RDP 1: Basica, no exendida en ninguna forma
          ================================================ */
        RDP rdp1 = new RDP(FILE_RDP1_MATRIX, FILE_RDP1_MARK);
        Assertions.assertArrayEquals(new boolean[]{true, false, false, false}, rdp1.getSensitizedArray());

        try{
            Assertions.assertFalse(rdp1.shotT(2,true), "Se disparo y no debia");
            Assertions.assertArrayEquals(new boolean[]{true, false, false, false}, rdp1.getSensitizedArray(),
                    "La red evoluciono y no debia");

            Assertions.assertFalse(rdp1.shotT(3,false),"Se disparo y no debia");
            Assertions.assertArrayEquals(new boolean[]{true, false, false, false}, rdp1.getSensitizedArray(),
                    "La red evoluciono mal");
        }catch (RDP.ShotException e){
            Assertions.fail();
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
        RDP rdp1 = new RDP(FILE_RDP1_MATRIX, FILE_RDP1_MARK);
        Assertions.assertThrows(RDP.ShotException.class,()->rdp1.shotT(0,true));
        Assertions.assertThrows(RDP.ShotException.class,()->rdp1.shotT(0,false));
        Assertions.assertThrows(RDP.ShotException.class,()->rdp1.shotT(5,true));
        Assertions.assertThrows(RDP.ShotException.class,()->rdp1.shotT(5,false));
    }

}