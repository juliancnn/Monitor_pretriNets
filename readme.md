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

      
