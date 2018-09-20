package jmml.monitor.policies;
/**
 * Encargado de manejar la politica de la red, dando mecanimos para cambiarla y de consulta de disparos
 * @TODO HACER TODO BEBE, esta mas crudo que los bebes que se trago wanda
 * @TODO IMPLEMENTAR EXCEPCIONES PARA MALA CARGA DE POLITICA + TRANSICIONES ESTATICAS
 * @TODO Rehacer la documentacion
 * @TODO Debo guardad ultimo disparo y cantidad de veces que se disparo, para hacer matrices
 * */
public class Policy {
    /**
     * Politica usada en el momento
     */
    private policyType mode;

    /**
     * @TODO TODO + excepciones / chekqueos de existencia. Lo vamos a hacer por archivos? \
     * deberia detectar si la cantidad de transiciones es = a la cantidad de estaticos
     */
    public Policy(policyType mode) {
        this.setPolicy(mode);
    }

    /**
     * Setea una nueva politica, cambia el criterio de toma de desiciones para la seleccion de transicion
     * @param policy Nueva politica para toma de desiciones.
     * @TODO CHECKQUEO DE EXISTENCIA throws
     */
    void setPolicy(policyType policy){

        this.mode = policy;
    }

    /***
     * Retorna la politica seteada actualmente
     * @return Politica usada actualmente
     */
    policyType getPolicy(){
        return this.mode;
    }

    /**
     * <pre>
     * Retorna la transicion de mayor prioridad entre un grupo de transiciones, segun la politica.<br>
     * El vector binario recibe todas las transiciones, y seteadas en falso aquellas de las cuales no se quiere<br>
     * tener en cuenta para retornar la de mayor prioridad.
     *
     * @param whoIsAviable Vector binario de transiciones. <br>
     *                     True: La transicion sera tomada en cuenta <br>
     *                     False: La transicion no sera tomada en cuenta
     * @return Transicion seleccionada por la politica para disparar
     * @TODO Arrojaria exepcion si el vector esta vacio [0 0 0 ....] y otra distinta para null \
     * ?? Deberia guardar el numero estatico de la cantidad de transiciones a recibir??
     * </pre>
     */
    int tellMeWho(boolean[] whoIsAviable){
        return 0;

    }


}
