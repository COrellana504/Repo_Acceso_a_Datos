package es.iesquevedo.domain.error;

/** Fallo al crear o leer una copia de seguridad en XML. */
public class BackupError extends RuntimeException {
    public BackupError(String message) {
        super(message);
    }

    public BackupError(String message, Throwable cause) {
        super(message, cause);
    }
}
