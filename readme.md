# Monitor basado en redes de petri

Una descripcion del monitor, bla bla bla

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
> N: Cantidad de transiciones  
> M: cantidad de plazas

Campos obligatorios del JSON:
  - Matriz de incidencia I  _\[dim: MxN]_
  - Vector de marcado _\[dim: M]_
  
Campos opcionales del JSON:
 - Matriz de Incidencia H _\[dim: NxM]_
   - Los valores deben ser binarios
   - Filas representan transiciones
   - Columnas representan plazas
   - 1 Si hay un arco inhibidorentre entre la plaza y la transicion
   - 0 Si no hay relacion
 - Matriz de Incidencia R _\[dim: NxM]_
   - Los valores deben ser binarios
   - Filas representan transiciones
   - Columnas representan plazas
   - 1 Si hay un arco lector entre la plaza y la transicion
   - 0 Si no hay relacion
 - Vector de tokens maximos por plaza _\[dim: M]_
   - Cada valor representa una plaza
   - Lo valores deben ser 0 para sin restriccion o un numero mayor a 0 para setear un maximo de tokens en esa plaza

 - Matriz de de politicas T _\[dim: NxN]_ 
   - Politica estatica, plana. Utilizada para crear las Policies.
   - Los valores deben ser binarios
   - La matriz es una matriz identidad de NxN con las filas cambiadas de orden, donde el orden reprensta la prioridad,  
   esto es:
     - La matriz es cuadrada
     - Cada colunas representa una transicion
     - En cada fila/columnas hay y solo hay un 1 (No pueden ser todos ceros y no pueden tener mas de uno)
     - La posicion del 1 representa el nivel de prioridad
     - Las tranciciones no pueden tener igual prioridad (No pueden haber 2 filas/columnas iguales)
 - Vectores de ventana temporal _\[dim: 2xN]_
   - Cada columna es asignada a una transicion
   - La primera fila es el inicio de la ventana temporal.
   - la otra fila representa el fin de la ventana temporal.
 - Matriz y vector de invariantes de plaza
   - Deben ser ambos utilizados o ninguno, funcionan en conjunto
   - Matriz de invariantes de plaza. _\[dim: IPxM]_
     - Cada fila representa un conjunto de de plazas que formar el invariante (IP Invariantes)
     - Cada columna representa con un 1 si forma parte de algun invariante o no (0).
     - El la dimencion de la fila es la cantidad de plazas M
   - Vector de invariantes de plaza. _\[dim: IP]_
     - el valor 's' del vector representa la suma invariante de plas plazas en la matriz de invariantes
   - El invariante 's' se chequea haciendo el productor interno entre la fila 's' de la matriz y el vector de marcado, 
   donde el resultado del (escalar), es el valor 's' del vector de invariantes. 

```
{
  "brief" : "Ejemplo 6: Un escritor Multiples lectores simultaneos",
  "info" : "Conjunto de procesos (P0), que pueden leer (P4) simultaneamente o escribir (P3)",
  "logFile": "log_ej6_conTiempo.txt",
  "tempQ": [0,0,0,0,1,1],
  "matrixI" :  [
    [-1, -1, 0, 0, 1, 1], //P1  procesos Idle
    [ 1,  0,-1, 0, 0, 0], //P2 Quieren escribir
    [ 0,  0,-1, 0, 1, 0], //P3 recurso
    [ 0,  0, 1, 0,-1, 0], //P4 escribiendo
    [ 0,  0, 0, 1, 0,-1], //P5 leyendo
    [ 0,  1, 0,-1, 0, 0]  //P6 quire leer
  ],
  "vectorMark"     : [7, 0, 1, 0, 0, 0], // 7 procesos
  "matrixH" :  [ // Al revez que I, transiciones por plaza
    [ 0,  0, 0, 0, 0, 0], //t1 Agregarse a la cola de escritura
    [ 0,  0, 0, 0, 0, 0], //t2 Agregarse a la cola de lectura
    [ 0,  0, 0, 0, 1, 0], //t3 Entrar a escribir (La escritura queda inhibida si hay alguien leyendo P5)
    [ 0,  0, 0, 0, 0, 0], //t4 Entrar a leer
    [ 0,  0, 0, 0, 0, 0], //t5 Termina de escribir
    [ 0,  0, 0, 0, 0, 0]  //t6 Termina de leer
  ],
  "matrixR" :[ // Al revez que I, transiciones por plaza
    [ 0,  0, 0, 0, 0, 0], //t1 Agregarse a la cola de escritura
    [ 0,  0, 0, 0, 0, 0], //t2 Agregarse a la cola de lectura
    [ 0,  0, 0, 0, 0, 0], //t3 Entrar a escribir
    [ 0,  0, 1, 0, 0, 0], //t4 Entrar a leer Si el recurso (p3) no fue tomado para escritura
    [ 0,  0, 0, 0, 0, 0], //t5 Termina de escribir
    [ 0,  0, 0, 0, 0, 0]  //t6 Termina de leer
  ],
  "matrixT" :[
  //t1-t2-t3-t4-t5-t6 ** Escritor
    [0, 0, 0, 0, 0, 1],         // Un escritor con multiples lectores
    [1, 0, 0, 0, 0, 0],         // Prioridad escritor cuando:
    [0, 0, 1, 0, 0, 0],         // t6>t1>t3>t5>t2>t4
    [0, 0, 0, 0, 1, 0],         // Prioridad lector cuando:
    [0, 1, 0, 0, 0, 0],         // t5>t4>t2>t6>t1>t3
    [0, 0, 0, 1, 0, 0]
  ],
  "matrixInvariantP" :[
    [ 1,  1, 0, 1, 1, 1],
    [ 0,  0, 1, 1, 0, 0]
  ],
  "vectorSumInvariantP" : [7, 1],
  "tempWindowTuple" : [
    [    0,    0,    0,    0, 10,  5], // Tiempo de lectura 500ms, tiempo escritura 1000
    [    0,    0,    0,    0,    0,    0]
  ],
  // pull Threads
  "pull" : [
    // T1 - Quien escribir
    {
      "name": "T1, Quieren escribir",
      "seq" : [1],
      "sleepTime" : 50,
      "nTimes" : 10
    },
    // T3-T5 - Escriben
    {
      "name": "T3-T5, Escriben",
      "seq" : [3,5],
      "sleepTime" : 0,
      "nTimes" : 0
    },
    // T2 - Quien leer
    {
      "name": "T2, Quieren Leer",
      "seq" : [2],
      "sleepTime" : 20,
      "nTimes" : 20
    },
    // T4-T6 - leen
    {
      "name": "T4-T6, leen",
      "seq" : [4,6],
      "sleepTime" : 0,
      "nTimes" : 0
    }
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

- v2.0 Version estable con logger del monitor con tiempo
- v1.5 Version estable con logger del monitor sin tiempo
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

1. Chequeo de invariantes de transicion
2. Cambiar el estado 'saliendo' a 'ir a pagarv, esto estaba mal tenia que ir a pagar y dsp elegir o no?, no se veian las 2 salidas?`
3. Cambiar de la red cuando se devuelve el token en la salida  (a la plaza que contiene los 60). ??
4. Hacer la tabla de eventos
5. Hacer la tabla de estados o actividades
6. Determinar la cantidad de hilos necesarios (justificarlo)
7. Armar el JSON del problema
8. Armar el problema fuera usando el monitor

