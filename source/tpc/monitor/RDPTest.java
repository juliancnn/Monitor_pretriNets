package tpc.monitor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste la Red de petri en base a los archivos ubicados en el directorio examples_rdp
 * */
@Tag("RDP")
class RDPTest {

    @Test
    @DisplayName("Disparos de prueba")
    void shotT() {
        RDP rdp = new RDP("examples_rdp/ex1_rdp.txt", "examples_rdp/ex1_mark.txt");
        Assertions.assertArrayEquals(new int[]{1,0,0,0,0},rdp.getMark(),"Marca inicial cambiada, fallaran todos los test");
    }

    @Test
    void getSensitizedArray() {
    }
}