# sdchatv2

Aplicación web de chat en directo pensada como ejemplo docente para la
asignatura **Sistemas Distribuidos**, centrada en la implementación de
**Sockets** y **WebSockets** en Java.

## 📚 Objetivo del proyecto

El objetivo principal de este proyecto es servir como base práctica
para:

-   Entender la comunicación en red mediante **Sockets**.
-   Ver la diferencia entre un modelo clásico basado en sockets y un
    modelo **web en tiempo real** con WebSockets.
-   Trabajar con una aplicación web Java tipo **Maven** desplegable en
    un contenedor de servlets (Tomcat, Payara, WildFly, etc.).
-   Integrar tecnologías de capa web (HTML, CSS, JavaScript) con lógica
    de servidor en Java.

## 🧱 Tecnologías utilizadas

-   **Java**
-   **Maven**
-   **Java EE 7**
-   **HTML / CSS / JavaScript**

## 🗂 Estructura general del proyecto

    sdchatv2/
    ├─ pom.xml
    ├─ nb-configuration.xml
    ├─ faces-config.NavData
    ├─ src/
    │  └─ main/
    │     ├─ java/
    │     └─ webapp/
    └─ target/

## ✅ Requisitos

-   Java 8+
-   Maven 3.x
-   Servidor Java (Tomcat, Payara, etc.)

## 🚀 Puesta en marcha

### 1. Clonado

    git clone https://github.com/aalvaro-tex/sdchatv2.git
    cd sdchatv2

### 2. Compilación

    mvn clean package

### 3. Despliegue

Despliega `target/sdchatv2.war` en tu servidor.

Accede normalmente mediante:

    http://localhost:8080/sdchatv2/

## 💬 Uso

1.  Abre la aplicación en navegador.

## 🌐 Sockets y WebSockets

El proyecto muestra el uso de:

-   **Sockets TCP**
-   **WebSockets** (canal bidireccional en tiempo real)

## 📄 Licencia



## 🧑‍🏫 Créditos

