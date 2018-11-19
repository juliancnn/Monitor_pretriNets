# Monitor basado en redes de petri

Una descripcion del monitor, bla bla cla

## Getting Started

1. Para construir la documentacion con javadoc, agregamos los argumentos personalizados con:  
`-tag TODO:a:"TO-DO:"  -tag WARNING:a:"Advertencias:"  -html5`
2. Se necesita la version 8 de java o superior (Por uso de funciones lambda y reflexion en las librerias)
3. Test en jUnit5 + mokito para el mock

### Estructura de directorios
- doc
  - covered \[Contiene el analisis del coverage]
  - diagramas \[Diagramas de secuencia y clase]
  - PetriModel \[Modelo del problema en redes de petri + analisis]
- lib \[Librerias del proyecto]
- source \[Codigo de fuente]

### Estructura de configuracion del JSON
Cargada desde un archivo json.
Donde el primer lugar representa la transicion o la plaza uno dependiente el caso.
Las matrices de incidencia estan ordenadas por filas que representan las plazas
y columnas que representan las transiciones.
Ningun valor puede ser salteado.
 
Campos obligatorios del JSON:
 - Matriz de incidencia I
 - Vector de marcado
Campos opcionales del JSON:
 - Matriz de Incidencia H
   - Los valores deben ser binarios
   - Filas representan transiciones
   - Columnas representan plazas
   - 1 Si hay un arco inhibidorentre entre la plaza y la transicion
   - 0 Si no hay relacion
 - Matriz de Incidencia R
   - Los valores deben ser binarios
   - Los valores deben ser binarios
   - Filas representan transiciones
   - Columnas representan plazas
   - 1 Si hay un arco lector entre la plaza y la transicion
   - 0 Si no hay relacion
 - Vector de tokens maximos por plaza
   - Lo valores deben ser 0 para sin restriccion o un numero mayor a 0 para setear un maximo de tokens en esa plaza

 - Matriz de de politicas T (Politica estatica, plana. Utilizada para crear las Policies.)
   - Los valores deben ser binarios
   - La matriz es una matriz identidad de MxM con las filas cambiadas de orden, donde el orden reprensta la prioridad, esto es:
   - La matriz es cuadrada
   - Cada colunas representa una transicion
   - En cada fila/columnas hay y solo hay un 1 (No pueden ser todos ceros y no pueden tener mas de uno)
   - La posicion del 1 representa el nivel de prioridad
   - Las tranciciones no pueden tener igual prioridad (No pueden haber 2 filas/columnas iguales)

```
{
 "brief" : "Ejemplo 1: Proceso paralelo",                      # (Opcional) breve descripcion
 "info" : "Con un arco inhibidor y un arco lector",            # (Opcional) Descripcion ampliaca
 "matrixI" :  [
   [-1, 0, 0, 1],                                              # Matriz de doble incidencia
   [1, -1, 0, 0],                                              # Las filas representan las plazas
   [0, 1, 0, -1],                                              # Las columnas representan las transiciones
   [1, 0, -1, 0],
   [0, 0, 1, -1]
 ],
 "vectorMark"     : [3, 0, 0, 0, 0],                           # (Opcional) Vector de marcado inicial
 "vectorMaxMark" : [0, 2, 0, 0, 0],                            # (Opcional) Vector de maximos tokenes por plaza
 "matrixH" :  [
   [0, 0, 0, 0, 0],                                            # (Opcional) Matriz de incidencia - Arcos inhibidores
   [0, 0, 0, 0, 0],                                            # Las filas representan las transiciones
   [0, 0, 0, 0, 0],                                            # 1 Si hay arcos inhibidores, 0 sin relacion
   [0, 1, 0, 0, 0]
 ],
 "matrixR" :[
   [0, 0, 0, 0, 0],                                            # (Opcional) Matriz de incidencia - Arcos Lectores
   [0, 0, 0, 0, 0],                                            # Las filas representan las transiciones
   [0, 1, 0, 0, 0],                                            # 1 Si hay arcos lectores, 0 sin relacion
   [0, 0, 0, 0, 0]
 ],
 "matrixT" :[
   [0, 0, 0, 1],                                               # (Opcional) Matriz de prioridades estaticas
   [0, 1, 0, 0],                                               # Las columnas representan las transiciones
   [0, 0, 1, 0],                                               # y la ubicacion del 1 en la fila prioridad
   [1, 0, 0, 0]
 ],
 "tempWindowTuple" : [                                          # (Opcional) Tupla de tiempos en transiciones
   [1000, 1000,    0,    0],                                    # Vector de minimo tiempo antes que se pueda disparar
   [   0, 3000, 1000,    0]                                     # Vector de maximo timeout para disparar
 ]
}
```
## Caracteriticas del monitor

Formado por 5 modulos independientes bla bla bla

### Modulo RDP
bla bla bla, y todas sus caracteriticas que aparecen en la documentacion

### Modulo RDP
bla bla bla, y todas sus caracteriticas que aparecen en la documentacion

### Modulo RDP
bla bla bla, y todas sus caracteriticas que aparecen en la documentacion

## Versiones 

Informacion mas detallada en el changelog. El changelog empezo a funcionar a partir de la version
v1.0.

### Etiquetado

- v1.0 Primera version estable del monitor sin tiempo
- v0.2 Primera version estable de la red de petri

### Comandos basicos de git para manejo de etiquetas

```
git tag                     # lista etiquetas
git tag -a v1.1             # Etiqueta  como v1.1
git show v1.1               # Muestra informacion de esa etiqueta
git push origin v1.1        # Manda etiqueta al branch remoto (Comparte etiquetas)
git push origin --tags      # Manda todas las etiquetas
git checkout -b ver1 v1.1   # Crea branch basado en el commit de la etiqeta
```

## @TO-DO

1. Checkeo de invariantes de plazas luego de cada disparo
2. Chequeo de invariantes de transicion, luego de cada disparo? o dsp te la ejecucion?
4. Cambiar el estado “saliendo” a “ir a pagar”, esto estaba mal tenia que ir a pagar y dsp elegir o no?
5. Cambiar de la red cuando se devuelve el token en la salida  (a la plaza que contiene los 60). ??


## Dudas del monitor con tiempo del lev
Se ve que no es todo el monitor, si no que es el disparo de la red de petri no mas, 
pero genera ciertas dudas o problemas:
1. La red de petri debe conocer un semaforo que nunca pide, y solo le hace release:
   - Va en contra de la modularidad e independencia del modulo.
   - Es confuso, por que una red de petri debe conocer un semaforo que nunca pide y ademas
   los threads pueden quedar dormidos en las colas y adentro de la red de petri?? :/
2. Como sabe cuando se despierta en el mensaje 4 que el monitor esta disponible
    - Si no pido el semaforo esta mal, no hay garantias de que este libre del monitor
    - Si pido el semaforo comipito contra la cola de entrada y entre todos los que se despiertan
3. No se ve la justicia:
    - Por que un distintos threads que quieren disparar la misma transicion terminarian compitiendo entre ellos
    cuando tienen que dispararse en el orden que llegaron (Si van a disparar la misma transicion)
4. No se ve la resolucion de politicas: 
    - Si una transicion se dispara, y habilita 2 transiciones temporales t1 y t2:
        - t1 y t2 tienen el mismo alfa, lo que quiere decir que se habilitan temporalmente al mismo momento
        - Llega h1 a disparar t1 antes que se cumpla alpha
        - Luego llega h2 a disparar t2 antes que se cumpla alpha, pero despues de h1
    - En ese caso tambien compiten entre ellas mientras deberia ser la politca quien decida

      
