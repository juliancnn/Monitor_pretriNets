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
