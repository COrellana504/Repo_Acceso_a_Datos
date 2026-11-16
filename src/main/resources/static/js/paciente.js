
const URL_BASE = "http://localhost:8080";
const URL_PACIENTES = `${URL_BASE}/pacientes`;

// OBTENER TODOS LOS PACIENTES
async function obtenerTodosPacientes() {
    try {
        const respuesta = await fetch(URL_PACIENTES);

        if (!respuesta.ok) {
            throw new Error(`HTTP error: ${respuesta.status}`);
        }

        const pacientes = await respuesta.json();
        console.log(pacientes);

        rellenarTablaPacientes(pacientes);

    } catch (error) {
        console.error("Error al obtener los pacientes:", error);
    }
}



// ELIMINAR PACIENTE
async function eliminarPaciente(boton) {

    const fila = boton.closest("tr");
    const idPaciente = fila.querySelector("td:first-child").innerText.trim();

    try {
        await enviarPeticionEliminar(idPaciente, false, fila);
    } catch (error) {
        console.error("Error al eliminar el paciente:", error);
    }
}


async function enviarPeticionEliminar(idPaciente, confirmacion, fila) {

    const respuesta = await fetch(`${URL_PACIENTES}/${idPaciente}?confirm=${confirmacion}`, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json"
        }
    });

    if (respuesta.status === 204) {
        console.log("Paciente eliminado correctamente");
        fila.remove();
        document.getElementById("contenedorInfo").innerHTML = "";
        return;
    }

    // ⚠️ paciente con registros médicos
    if (respuesta.status === 409) {
        const mensaje = await respuesta.text();
        const confirmado = confirm(mensaje);

        if (confirmado) {
            return enviarPeticionEliminar(idPaciente, true, fila);
        } else {
            throw new Error("User cancelled operation");
        }
    }

    if (!respuesta.ok) {
        throw new Error(`HTTP error ${respuesta.status}`);
    }
}


// AÑADIR PACIENTE
async function nuevoPaciente(evento) {
    evento.preventDefault();

    const paciente = {
        id: 0,
        nombre: document.getElementById("nuevoPacienteNombre").value,
        fechaNacimiento: document.getElementById("nuevoPacienteFechaNac").value,
        telefono: document.getElementById("nuevoPacienteTelefono").value,
        username: document.getElementById("nuevoPacienteUsuario").value,
        password: document.getElementById("nuevoPacienteClave").value
    };

    try {

        const respuesta = await fetch(URL_PACIENTES, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(paciente)
        });

        // ⚠️ username duplicado
        if (respuesta.status === 409) {
            const mensaje = await respuesta.text();
            alert(mensaje);
            throw new Error("Username duplicated");
        }

        if (!respuesta.ok) {
            throw new Error(`HTTP error ${respuesta.status}`);
        }

        const nuevoId = await respuesta.json();

        console.log("Paciente añadido correctamente, id:", nuevoId);

        // actualizar id para la tabla
        paciente.id = nuevoId;

        const cuerpoTabla = document.querySelector("#tablaPacientes tbody");
        const fila = crearFilaPaciente(paciente);
        cuerpoTabla.appendChild(fila);

        // reiniciar formulario
        document.getElementById("nuevoPacienteForm").reset();

        // cerrar modal
        $('#nuevoPacienteModal').modal('hide');

    } catch (error) {
        console.error("Error al añadir el paciente:", error);
    }
}
// ACTUALIZAR PACIENTE
async function actualizarPaciente(evento) {
    evento.preventDefault();

    const paciente = {
        id: document.getElementById("actualizarPacienteId").value,
        nombre: document.getElementById("actualizarPacienteNombre").value,
        fechaNacimiento: document.getElementById("actualizarPacienteFechaNac").value,
        telefono: document.getElementById("actualizarPacienteTelefono").value
    };

    try {

        const respuesta = await fetch(`${URL_PACIENTES}/${paciente.id}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(paciente)
        });

        if (!respuesta.ok) {
            throw new Error(`HTTP error ${respuesta.status}`);
        }

        console.log("Paciente actualizado correctamente");

        // cerrar modal
        $('#actualizarPacienteModal').modal('hide');

        // actualizar tabla
        modificarTablaHTMLPacientes(paciente);

    } catch (error) {
        console.error("Error al actualizar el paciente:", error);
    }
}

// Modifica la tabla HTML de pacientes
function modificarTablaHTMLPacientes(paciente) {

    const filas = document.querySelectorAll("#tablaPacientes tbody tr");
    const fila = Array.from(filas).find(f => f.cells[0].textContent.trim() === String(paciente.id));

    if (!fila) {
        console.warn("Fila de paciente no encontrada:", paciente.id);
        return;
    }

    if (!fila) {
        console.warn("Fila de paciente no encontrada:", paciente.paciente_id);
        return;
    }

    fila.cells[1].textContent = paciente.nombre;
    fila.cells[2].textContent = paciente.fechaNacimiento;
    fila.cells[3].textContent = paciente.telefono;
}

// Muestra la ventana modal de actualización
function mostrarModalActualizarPaciente(boton) {
    let fila = boton.parentNode.parentNode;
    let celdas = fila.getElementsByTagName("td");

    // Obtener los datos de la fila seleccionada
    let idPaciente = celdas[0].innerText;
    let nombrePaciente = celdas[1].innerText;
    let fechaNacPaciente = celdas[2].innerText;
    let telefonoPaciente = celdas[3].innerText;

    // Poblar el formulario de actualización con los datos de la fila seleccionada
    document.getElementById("actualizarPacienteId").value = idPaciente;
    document.getElementById("actualizarPacienteNombre").value = nombrePaciente;
    document.getElementById("actualizarPacienteFechaNac").value = fechaNacPaciente;
    document.getElementById("actualizarPacienteTelefono").value = telefonoPaciente;

    // Añade el listener para el envío del formulario de actualización
    var actualizarPacienteForm = document.getElementById("actualizarPacienteForm");
    
    actualizarPacienteForm.onsubmit = actualizarPaciente;
    //actualizarPacienteForm.addEventListener("submit", actualizarPaciente);  -- Si se hace de esta forma, se crea un nuevo listener cada vez que se abre la ventana

    // Mostrar la ventana modal de actualización
    var actualizarPacienteModal = new bootstrap.Modal(document.getElementById('actualizarPacienteModal'));
    actualizarPacienteModal.show();
}

function rellenarTablaPacientes(pacientes) {
    // Mejora: se podría usar DocumentFragment para más eficiencia
    const cuerpoTabla = document.querySelector("#tablaPacientes tbody");

    if (!cuerpoTabla) {
        console.warn("Table body not found");
        return;
    }

    // Limpiar contenido
    cuerpoTabla.textContent = "";

    pacientes.forEach(paciente => {
        let fila = crearFilaPaciente(paciente);
                // Añadir la fila al cuerpo de la tabla
                cuerpoTabla.appendChild(fila);
    });

}

function crearFilaPaciente(paciente) {
    const fila = document.createElement("tr");

    // Campos que queremos mostrar en la tabla
    const campos = ["id", "nombre", "fechaNacimiento", "telefono"];

    campos.forEach(campo => {
        const celda = document.createElement("td");
        celda.textContent = paciente[campo];
        fila.appendChild(celda);
    });

    // Botones
    fila.appendChild(crearCeldaBoton("Historia clínica", "btn btn-info btn-sm", function () {
        obtenerHistoriasPaciente(this);
    }));

    fila.appendChild(crearCeldaBoton("Eliminar", "btn btn-danger btn-sm", function () {
        eliminarPaciente(this);
    }));

    fila.appendChild(crearCeldaBoton("Actualizar", "btn btn-primary btn-sm ms-2", function () {
        mostrarModalActualizarPaciente(this);
    }));

    return fila;
}


// Función auxiliar para crear botones
function crearCeldaBoton(texto, clase, manejadorClick) {
    const celda = document.createElement("td");
    const boton = document.createElement("button");

    boton.className = clase;
    boton.textContent = texto;
    boton.addEventListener("click", manejadorClick);

    celda.appendChild(boton);
    return celda;
}


// Llamar a la función cuando el DOM esté listo
document.addEventListener("DOMContentLoaded", function () {

    obtenerTodosPacientes();

    const botonNuevo = document.getElementById("botonNuevo");
    const nuevoPacienteForm = document.getElementById("nuevoPacienteForm");
    const nuevoPacienteModal = new bootstrap.Modal(document.getElementById("nuevoPacienteModal"));

    // ✅ El submit se registra una sola vez
    nuevoPacienteForm.addEventListener("submit", nuevoPaciente);

    // ✅ El botón solo abre el modal
    botonNuevo.addEventListener("click", function () {
        nuevoPacienteModal.show();
    });

});