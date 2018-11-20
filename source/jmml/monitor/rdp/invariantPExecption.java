package jmml.monitor.rdp;

public class invariantPExecption extends Exception {

    private final int[][]   matrix;
    private final int[]     pInvariant;
    private final int[]     mark;
    private final int[]     res;

    public invariantPExecption(int[][] matrix, int[] pInvariant, int[] mark, int[] res) {
        this.matrix = matrix;
        this.pInvariant = pInvariant;
        this.mark = mark;
        this.res = res;
    }

    public String toString() {
        String msj = "[Error en el chequeo de invariantes]\n";
        msj += "Marcado:                     " + this.vectorToString(this.mark) + "\n";
        msj += "ValorDelInvarianteTeorico:   " + this.vectorToString(this.pInvariant) + "\n";
        msj += "ValorDelInvarianteCalculado: " + this.vectorToString(this.res) + "\n";
        msj += "Matriz de invariantes: \n";
        for(int i=0; i < this.matrix.length ;i++){
            msj += "                             " + this.vectorToString(this.matrix[i]) + "\n";
        }

        return msj;
    }

    private String vectorToString(int[] v){
        String vtos = "[";
        for(int e : v){
            vtos += String.format("%4d", e);
        }
        vtos += "]";

        return vtos;
    }


}
