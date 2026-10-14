package es.iesquevedo.ui;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/** Utilidades para leer datos validados por teclado. */
public class Consola {

    public String texto(String mensaje) {
        while (true) {
            String linea = IO.readln(mensaje).trim();
            if (!linea.isEmpty()) return linea;
            IO.println("El valor no puede estar vacío.");
        }
    }

    public Long numero(String mensaje) {
        while (true) {
            try {
                return Long.parseLong(IO.readln(mensaje).trim());
            } catch (NumberFormatException e) {
                IO.println("Introduzca un número válido.");
            }
        }
    }

    public LocalDate fecha(String mensaje) {
        while (true) {
            try {
                return LocalDate.parse(IO.readln(mensaje + " (aaaa-mm-dd): ").trim());
            } catch (DateTimeParseException e) {
                IO.println("Fecha no válida. Formato: aaaa-mm-dd.");
            }
        }
    }
}
