# UNO-RMI-Java 

Este es un juego de cartas UNO multijugador desarrollado en Java utilizando **RMI (Remote Method Invocation)**. Permite que varios jugadores se conecten a través de la red y jueguen una partida clásica de UNO, cada uno desde su propia computadora.

---

##  Características

- Jugabilidad multijugador en red local
- Interfaz gráfica hecha con **Swing**
- Comunicación distribuida usando **Java RMI**
- Efectos clásicos: +2, +4, cambio de color, reversa, etc.
- Indicador de turno y nombres de jugadores conectados


---

##  Requisitos

- Java JDK 8 o superior
- NetBeans, IntelliJ u otro entorno compatible
- Conexión de red entre los dispositivos

---

## Estructura del Proyecto

- **UNO-RMI-Java/**
  - **src/**
    - **unormi/**
      - `ClienteUNO.java` – Interfaz gráfica del cliente
      - `Launcher.java` – Servidor RMI (punto de entrada)
      - `JuegoUNO.java` – Interfaz remota
      - **servidor/**
        - `ServidorUNO.java` – Implementación del servidor RMI
  - **assets/**
    - **cartas/** – Imágenes de las cartas UNO
      - `rojo_1.png`
      - `negro_mas4.png`
      - `...` (resto de cartas)

