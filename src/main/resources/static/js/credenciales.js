const URL_LOGIN = "http://localhost:8080/login";

// Cachear elementos del DOM una sola vez
const botonLogin = document.getElementById("bCheck");
const campoUsuario = document.getElementById("username");
const campoClave = document.getElementById("password");
const mensajeError = document.getElementById("ErrorLogin");

// Evento click
botonLogin.addEventListener("click", gestionarLogin);

// Función principal
async function gestionarLogin(evento) {
    evento.preventDefault(); // Previene recarga si está dentro de un form

    const usuario = campoUsuario.value.trim();
    const clave = campoClave.value.trim();

    // Validación básica
    if (!usuario || !clave) {
        mostrarError("Introduce usuario y contraseña");
        return;
    }

    try {
        const autenticado = await obtenerCredenciales(usuario, clave);

        if (autenticado) {
            mensajeError.textContent = "";
            window.location.href = "home.html";
        } else {
            mostrarError("Usuario o contraseña incorrectos");
        }

    } catch (error) {
        console.error("Error de login:", error);
        mostrarError("Error del servidor. Inténtalo de nuevo más tarde.");
    }
}

// Función que realiza el POST
async function obtenerCredenciales(usuario, clave) {

    const respuesta = await fetch(URL_LOGIN, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ username: usuario, password: clave })
    });

    if (!respuesta.ok) {
        throw new Error("Respuesta de red no válida");
    }

    const datos = await respuesta.text();
    return datos === "true";
}

// Función para mostrar errores
function mostrarError(mensaje) {
    mensajeError.textContent = mensaje;
}
