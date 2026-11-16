package es.iesquevedo.ui;

import es.iesquevedo.domain.error.AppError;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainMenu {

  private final UsuarioUI usuarioUi;
  private final PacienteUI pacienteUi;
  private final MedicoUI medicoUi;
  private final HistoriaClinicaUI historiaUi;

  @Inject
  public MainMenu(UsuarioUI usuarioUi, PacienteUI pacienteUi, MedicoUI medicoUi, HistoriaClinicaUI historiaUi) {
    this.usuarioUi = usuarioUi;
    this.pacienteUi = pacienteUi;
    this.medicoUi = medicoUi;
    this.historiaUi = historiaUi;
  }

  public void run() {
    try {
      IO.println("Hospital App");

      usuarioUi.login();

      int opcion = 0;

      while (opcion != 11) {
        IO.println("""

            1. Mostrar todos los pacientes
            2. Añadir paciente
            3. Actualizar paciente
            4. Borrar paciente
            5. Mostrar todos los médicos
            6. Mostrar la historia de un paciente
            7. Añadir un registro a la historia de un paciente
            8. Actualizar un registro de la historia de un paciente
            9. Borrar un registro
            10. Restaurar paciente
            11. Salir""");

        String linea = IO.readln("Introduzca una opción ...\n").trim();
        if (linea.isEmpty()) continue;
        try {
          opcion = Integer.parseInt(linea);
        } catch (NumberFormatException e) {
          IO.println("Opción no válida");
          continue;
        }

        switch (opcion) {
          case 1 -> pacienteUi.getAll();
          case 2 -> pacienteUi.save();
          case 3 -> pacienteUi.update();
          case 4 -> pacienteUi.delete();
          case 5 -> medicoUi.getAll();
          case 6 -> historiaUi.mostrarHistoria();
          case 7 -> historiaUi.add();
          case 8 -> historiaUi.update();
          case 9 -> historiaUi.delete();
          case 10 -> pacienteUi.restore();
          case 11 -> IO.println("Hasta la vista");
          default -> IO.println("Opción no válida");
        }
      }

    } catch (AppError e) { // Solo errores críticos no manejados
      System.err.println("Fallo grave: " + e.getMessage());
      log.error("Fallo grave {}", e.getMessage());
      System.exit(1);
    }
  }

}
