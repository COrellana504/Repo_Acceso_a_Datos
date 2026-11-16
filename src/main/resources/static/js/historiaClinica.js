const URL_MEDICOS = `${URL_BASE}/medicos`;
const URL_HISTORIAS = `${URL_BASE}/historias`;


// OBTENER HISTORIAS CLÍNICAS DEL PACIENTE
async function obtenerHistoriasPaciente(boton) {
    const fila = boton.parentNode.parentNode;
    const idPaciente = fila.querySelector("td:first-child")?.textContent.trim();
    const nombrePaciente = fila.querySelector("td:nth-child(2)")?.textContent.trim();
    const contenedorInfo = document.getElementById("contenedorInfo");

    if (!idPaciente) {
        console.error("ID de paciente no encontrado.");
        return;
    }

    try {
        const respuesta = await fetch(`${URL_PACIENTES}/${idPaciente}/historias`);

        if (!respuesta.ok) {
            throw new Error(`HTTP error: ${respuesta.status}`);
        }

        const historias = await respuesta.json();
        console.log(historias);

        rellenarTablaHistorias({ idPaciente, nombrePaciente, historias, contenedorInfo });

    } catch (error) {
        console.error("Error al obtener las historias:", error);
        contenedorInfo.innerHTML = "<p>Error loading medical records.</p>";
    }
}

function rellenarTablaHistorias({ idPaciente, nombrePaciente, historias, contenedorInfo }) {
    const tabla = document.createElement("table");
    tabla.id = "tablaHistorias";
    tabla.dataset.pacienteId = idPaciente;
    tabla.className = "table table-bordered table-condensed table-striped";

    tabla.innerHTML = `
        <thead>
            <tr>
                <th colspan="5">Historia clínica de ${nombrePaciente}</th>
                <th colspan="2">
                    <button class="btn btn-info btn-sm ms-2" onclick="mostrarModalNuevaHistoria(this)">
                        Añadir historia
                    </button>
                </th>
            </tr>
            <tr>
                <th>ID</th>
                <th>Diagnóstico</th>
                <th>Fecha admisión</th>
                <th>Médico</th>
                <th>Recetas</th>
                <th>Acción</th>
                <th>Acción</th>
            </tr>
        </thead>
        <tbody></tbody>
    `;

    const cuerpoTabla = tabla.querySelector("tbody");


    historias.forEach(historia => {
        let filaHistoria = crearFilaHistoria(historia);
        // Agrega la fila al cuerpo de la tabla
        cuerpoTabla.appendChild(filaHistoria);
    });

    // Agrega la tabla de historias al documento HTML
    contenedorInfo.innerHTML = ""; // Limpiar contenido anterior
    contenedorInfo.appendChild(tabla);
}


// ELIMINAR HISTORIA
async function eliminarHistoria(boton) {
    const fila = boton.parentNode.parentNode;
    const idHistoria = fila.querySelector("td:first-child").textContent.trim();

    if (!idHistoria) {
        console.error("ID de historia no encontrado.");
        return;
    }

    try {
        const respuesta = await fetch(`${URL_HISTORIAS}/${idHistoria}`, {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json"
            }
        });

        if (!respuesta.ok) {
            throw new Error(`HTTP error: ${respuesta.status}`);
        }

        console.log("Historia eliminada correctamente");
        fila.remove();

    } catch (error) {
        console.error("Error al eliminar la historia:", error);
    }
}

// ACTUALIZAR HISTORIA
async function actualizarHistoria(evento) {
    evento.preventDefault();

    const historia = {
        historia_id: document.getElementById("actualizarHistoriaId").value.trim(),
        diagnostico: document.getElementById("actualizarHistoriaDiagnostico").value.trim(),
        fecha_admision: document.getElementById("actualizarHistoriaFecha").value,
        paciente_id: document.getElementById("actualizarHistoriaIdPaciente").value.trim(),
        medico_id: document.getElementById("actualizarHistoriaIdMedico").value.trim(),
        recetas: obtenerMedicamentosSeleccionados("actualizarHistoriaRecetas")
    };

    try {
        const respuesta = await fetch(`${URL_HISTORIAS}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(historia)
        });

        if (!respuesta.ok) {
            throw new Error(`HTTP error: ${respuesta.status}`);
        }

        console.log("Historia actualizada correctamente");
        $("#actualizarHistoriaModal").modal("hide");

        modificarTablaHTMLHistorias(historia);

    } catch (error) {
        console.error("Error al actualizar la historia:", error);
    }
}

// Obtiene los medicamentos seleccionados de un <select multiple>
function obtenerMedicamentosSeleccionados(idSelect) {
    const elementoSelect = document.getElementById(idSelect);

    if (!elementoSelect) {
        console.error(`Elemento select "${idSelect}" no encontrado.`);
        return [];
    }

    return Array.from(elementoSelect.selectedOptions).map(opcion => opcion.textContent.trim());
}

// Actualiza la fila de la historia en la tabla HTML
function modificarTablaHTMLHistorias(historia) {
    const filas = document.querySelectorAll("#tablaHistorias tbody tr");

    for (const fila of filas) {
        const idFila = fila.cells[0].textContent.trim();

        if (idFila === String(historia.historia_id)) {
            fila.cells[1].textContent = historia.diagnostico;
            fila.cells[2].textContent = historia.fecha_admision;
            fila.cells[3].textContent = historia.medico_id;
            fila.cells[4].textContent = obtenerCadenaMedicamentos(historia);
            break;
        }
    }
}

// Muestra la ventana modal para actualizar la historia
async function mostrarModalActualizarHistoria(boton) {
    const fila = boton.parentNode.parentNode;
    const celdas = fila.querySelectorAll("td");

    const idHist = celdas[0].innerText;
    const diagnosticoHist = celdas[1].innerText;
    const fechaHist = celdas[2].innerText;
    const idMedicoHist = celdas[3].innerText;
    const tabla = document.getElementById("tablaHistorias");
    const idPacienteHist = tabla.getAttribute("data-paciente-id");


    // Poblar el formulario de actualización con los datos de la fila seleccionada
    document.getElementById("actualizarHistoriaId").value = idHist;
    document.getElementById("actualizarHistoriaDiagnostico").value = diagnosticoHist;
    document.getElementById("actualizarHistoriaFecha").value = fechaHist;
    document.getElementById("actualizarHistoriaIdPaciente").value = idPacienteHist;


    // Se usa await porque, si no, no espera a que se lean los médicos de la BD:
    await rellenarComboMedicos('actualizarHistoriaIdMedico');
    document.getElementById("actualizarHistoriaIdMedico").value = idMedicoHist;

    rellenarComboMedicamentos('actualizarHistoriaRecetas');


    // Añade el listener para el envío del formulario de actualización
    var formularioActualizar = document.getElementById("actualizarHistoriaForm");
    formularioActualizar.addEventListener("submit", actualizarHistoria);

    // Mostrar la ventana modal de actualización
    new bootstrap.Modal(document.getElementById('actualizarHistoriaModal')).show();
}

// AÑADIR HISTORIA
async function nuevaHistoria(evento) {
    evento.preventDefault();

    try {
        // === 1. Obtener valores del formulario ===
        const descripcion = document.getElementById("nuevaHistoriaDiagnostico").value;
        const fechaOriginal = document.getElementById("nuevaHistoriaFecha").value;
        const idMedico = document.getElementById("nuevaHistoriaMedico").value;
        const medicamentos = obtenerMedicamentosSeleccionados("nuevaHistoriaRecetas");

        // === 2. Formatear datos ===
        const fechaFormateada = new Date(fechaOriginal).toISOString().split("T")[0];

    // Obtener el ID del paciente
    const idPaciente = document.getElementById("tablaHistorias").getAttribute("data-paciente-id");

    // Preparar los datos para enviarlos
    var historia = {
        historia_id: 0,
        diagnostico: descripcion,
        fecha_admision: fechaFormateada,
        paciente_id: idPaciente,
        medico_id: idMedico,
        recetas: medicamentos
    };

    // Enviar la petición para añadir la historia al servidor
   const respuesta = await fetch(`${URL_HISTORIAS}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(historia)
        });

        if (!respuesta.ok) {
            throw new Error("Error en la petición");
        }

        const nuevoId = await respuesta.json();
        historia.historia_id = nuevoId;

        // === 5. Actualizar UI ===
        console.log("Historia añadida correctamente, id:", nuevoId);

        // Reiniciar el formulario
        document.getElementById("nuevaHistoriaForm").reset();

        // Cerrar modal (Bootstrap 5)
        bootstrap.Modal.getInstance(
            document.getElementById("nuevaHistoriaModal")
        ).hide();

        // Añadir fila a la tabla
        const cuerpoTabla = document.querySelector("#tablaHistorias tbody");
        const fila = crearFilaHistoria(historia);
        cuerpoTabla.appendChild(fila);

    } catch (error) {
        console.error("Error al añadir la historia:", error);
    }
}

async function mostrarModalNuevaHistoria(evento) {
    const elementoModal = document.getElementById("nuevaHistoriaModal");
    const modal = new bootstrap.Modal(elementoModal);

    await rellenarComboMedicos("nuevaHistoriaMedico");
    rellenarComboMedicamentos("nuevaHistoriaRecetas");

    const formulario = document.getElementById("nuevaHistoriaForm");

    // Evitar múltiples listeners duplicados
    formulario.removeEventListener("submit", nuevaHistoria);
    formulario.addEventListener("submit", nuevaHistoria);

    modal.show();
}

//rellenarComboMedicos
async function rellenarComboMedicos(combo) {
    try {
        const respuesta = await fetch(URL_MEDICOS);

        if (!respuesta.ok) {
            throw new Error('Network respuesta was not ok');
        }

        const datos = await respuesta.json();

        const selectMedicos = document.getElementById(combo);
        selectMedicos.innerHTML = '';

        datos.forEach(medico => {
            const opcion = document.createElement('option');
            opcion.value = medico.medico_id;
            opcion.textContent = medico.nombre;
            selectMedicos.appendChild(opcion);
        });

    } catch (error) {
        console.error('Error al obtener los médicos:', error);
    }
}

function rellenarComboMedicamentos(combo) {

    const selectMedicamentos = document.getElementById(combo);
    selectMedicamentos.innerHTML = '';

    let opcion = document.createElement('option');
    opcion.textContent = "Losartan";
    selectMedicamentos.appendChild(opcion);
    opcion = document.createElement('option');
    opcion.textContent = "Amoxicilina";
    selectMedicamentos.appendChild(opcion);
    opcion = document.createElement('option');
    opcion.textContent = "Crema hidratante";
    selectMedicamentos.appendChild(opcion);
    opcion = document.createElement('option');
    opcion.textContent = "Ibuprofeno";
    selectMedicamentos.appendChild(opcion);
    opcion = document.createElement('option');
    opcion.textContent = "Paracetamol";
    selectMedicamentos.appendChild(opcion);
}

// Crea una fila para la tabla de historias
function crearFilaHistoria(historia) {
    const fila = document.createElement("tr");

    // Helper para crear celdas
    const crearCelda = (texto) => {
        const td = document.createElement("td");
        td.textContent = texto;
        return td;
    };

    fila.appendChild(crearCelda(historia.historia_id));
    fila.appendChild(crearCelda(historia.diagnostico));
    fila.appendChild(crearCelda(historia.fecha_admision));
    fila.appendChild(crearCelda(historia.medico_id));
    fila.appendChild(crearCelda(obtenerCadenaMedicamentos(historia)));

    // Botón eliminar
    const botonEliminar = document.createElement("button");
    botonEliminar.className = "btn btn-danger btn-sm";
    botonEliminar.textContent = "Eliminar";
    botonEliminar.addEventListener("click", () => eliminarHistoria(botonEliminar));

    const tdEliminar = document.createElement("td");
    tdEliminar.appendChild(botonEliminar);
    fila.appendChild(tdEliminar);

    // Botón actualizar
    const botonActualizar = document.createElement("button");
    botonActualizar.className = "btn btn-primary btn-sm ms-2";
    botonActualizar.textContent = "Actualizar";
    botonActualizar.addEventListener("click", () => mostrarModalActualizarHistoria(botonActualizar));

    const tdActualizar = document.createElement("td");
    tdActualizar.appendChild(botonActualizar);
    fila.appendChild(tdActualizar);

    return fila;
}

// Más limpio con join
function obtenerCadenaMedicamentos(historia) {
    return historia.recetas?.join(", ") || "";
}
