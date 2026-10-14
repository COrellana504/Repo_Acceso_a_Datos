package es.iesquevedo.dao.common;

public class SQLQueries {
  // Usuarios
  public static final String FIND_USUARIO_BY_USERNAME = "SELECT * FROM usuarios WHERE username = ?";
  public static final String ADD_USUARIO = "INSERT INTO usuarios (username, password, paciente_id) VALUES (?, ?, ?)";
  public static final String DELETE_USUARIO_BY_PACIENTE = "DELETE FROM usuarios WHERE paciente_id = ?";

  // Pacientes
  public static final String FIND_ALL_PACIENTES = "SELECT * FROM pacientes";
  public static final String ADD_PACIENTE = "INSERT INTO pacientes (nombre, fecha_nacimiento, telefono) VALUES (?, ?, ?)";
  public static final String UPDATE_PACIENTE = "UPDATE pacientes SET nombre = ?, fecha_nacimiento = ?, telefono = ? WHERE paciente_id = ?";
  public static final String DELETE_PACIENTE = "DELETE FROM pacientes WHERE paciente_id = ?";

  // Médicos  (ASUMIDO: tabla medicos con medico_id, nombre, especialidad, telefono)
  public static final String FIND_ALL_MEDICOS = "SELECT * FROM medicos";  // Historia clínica  (ASUMIDO: tabla historias_clinicas con historia_id, paciente_id, medico_id, fecha_admision, diagnostico)
  public static final String FIND_HISTORIAS_BY_PACIENTE = "SELECT * FROM historia_clinica WHERE paciente_id = ? ORDER BY fecha_admision";
  public static final String ADD_HISTORIA = "INSERT INTO historia_clinica (paciente_id, medico_id, fecha_admision, diagnostico) VALUES (?, ?, ?, ?)";
  public static final String UPDATE_HISTORIA = "UPDATE historia_clinica SET medico_id = ?, fecha_admision = ?, diagnostico = ? WHERE historia_id = ?";
  public static final String DELETE_HISTORIA = "DELETE FROM historia_clinica WHERE historia_id = ?";
}
