# Changelog
All notable changes to this project will be documented in this file.

Formato basado en [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),

## [Proxima Version etiquetada] - 2018-XX-XX
### Agregado
- [ ] Ejemplo productores/consumidores (con tiempo)
### Cambiado
### Removido
- 

## [v2.0] - 2018-12-09
### Agregado
- Nuevo ejemplo lector escritor ej6 con tiempo
- Monitor
    - Crear Pull de dammy proces con seq de disparo para testear monitor (Test Integracion - Complex text)
    - Monitor con tiempo okey
- RDP
    - Agregado metodo devuelve el tiempo para sensibilizado
    - Documentacion de PInvariantes
- Nuevo diagrama de secuencia con tiempo del monitor
- QueueMagnament 
    - Soporta hilos que se levantan solos
    - Posibilidad de cambiar el tiempo para que se levante solo cada hilo
    - Nuevos test para las colas que se levantan solas
- Scripts
    - Script en bash y perl para facilitar debuggin y control de T invariantes (P esta en el codigo)
### Cambiado
- RDP
    - Nuevo formato de getMark String, imprimible
- QueueMagnament
    - Las colas se crean con un objeto RAW y el logger
- Logger de eventos cambia para ajustarse a estandar    
### Removido





## [v1.5] - 2018-12-02
### Agregado
-  Ejemplo lectores/escritores  (Sin tiempo - con chequeo de PInvariantes)
-  Implementar un logger total de todos los modulos
-  Chequeo de PInvariantes
-  Diagrama de monitor con tiempo alternativo
-  Nuevo diagrama de monitor de tiempo
-  Nuevo ejemplo, para test de sistema, ej6
### Cambiado
- readme
- .gitignore \> log*
-  Se agrego al JSON el logFile, para loggear eventos del monitor
### Removido
- Se removieron los archivos de configuracion viejos de las RDP
- Se removieron los diagramas viejos de clases de monitor y secuencia
- Dudas para mico en el readme.


## [v1.0] - 2018-11-18
### Agregado
- Changelog
- readme
- .gitignore \> astah.lock
- Diagrama de secuencia del monitor sin tiempo
- Diagrama de secuencia del monitor con tiempo