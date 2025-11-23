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

### Frameworks

- JSF 2.3
- PrimeFaces

## 🗂 Estructura general del proyecto

    sdchatv2/
    ├─ pom.xml
    ├─ nb-configuration.xml
    ├─ faces-config.NavData
    ├─ src/
    │  └─ main/
    │     ├─ java/
    |     |    └─ chat
    |     |    └─ dto
    |     |    └─ entities
    |     |    └─ jaas
    |     |    └─ json
    |     |    └─ login
    |     |    └─ rest
    |     |    └─ user
    |     |    └─ utils
    │     └─ webapp/

## ✅ Requisitos

-   Java 8+
-   Maven 3.x
-   Servidor Java (Tomcat, Payara, etc.)

## 🚀 Puesta en marcha

### 1. Clonado

    git clone https://github.com/aalvaro-tex/sdchatv2.git
    cd sdchatv2

### 2. Abrir en NetBeans

Recomendable para configurar facilmente el servidor

### 3. Servidor Payara y BBDD

Crear un servidor Payara y una BBDD PostgreSQL _sdChat_. Por defecto, al reiniciar el servidor la BBDD se borra y genera los esquemas de cero. Por lo tanto, solamente es necesario tener la BBDD creada vacía.

### 4. Build + Deploy

Compilamos y desplegamos el proyecto. Se abrirá automáticamente en el navegador la página de inicio de sesión.

## 💬 Uso

- Si no tenemos cuenta, podemos crear una proporcionando un nombre de usuario y una contraseña (a día 13//11/2025 no tiene restricciones).
- Una vez creada, se inicia sesión automáticamente.
- Iniciamos una nueva conversación con el icono ➕ y escribimos el nombre de usuario con el que queremos hablar
- Si existe, se nos carga la vista de chat para hablar

- Podemos cambiar nuestra foto de perfil, que verán el resto de usuarios

 ### Dispositivos móviles

La aplicación está diseñada para adaptarse a dispositivos móviles, cambiando los estilos y la forma de presentar las vistas.
  

## 🌐 Sockets y WebSockets

El proyecto muestra el uso de:

-   **Sockets TCP**
-   **WebSockets** (canal bidireccional en tiempo real)

## 📄 Licencia



## 🧑‍🏫 Créditos

## Capturas de pantalla

<img width="1895" height="937" alt="image" src="https://github.com/user-attachments/assets/87f12ce5-cd73-41e3-b9d1-3dbfe8c8b1fd" />
<img width="1893" height="940" alt="image" src="https://github.com/user-attachments/assets/2fb4d6e3-b7e5-4586-80f9-499aa34bb760" />
<img width="1897" height="939" alt="image" src="https://github.com/user-attachments/assets/27afbc1b-a2b9-4a0b-9142-2e66771e080e" />
<img width="1897" height="939" alt="image" src="https://github.com/user-attachments/assets/72b134ab-0631-4e50-abe6-3b7e6346c4e5" />
<img width="1896" height="938" alt="image" src="https://github.com/user-attachments/assets/02228528-9e02-4f53-8f0e-f12e14d323b1" />
<img width="1735" height="1129" alt="image" src="https://github.com/user-attachments/assets/15c35064-105c-4c45-9173-a3bb4f07739f" />





