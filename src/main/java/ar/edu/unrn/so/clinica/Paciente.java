package ar.edu.unrn.so.clinica;

/**
 * Sistemas Operativos 2024
 * Trabajo entregable 3
 * @author Álvaro Bayón
 * Paciente que busca atención en la clínica
 * Similar al cliente en el problema del peluquero dormilón
 */
class Paciente implements Runnable {
    private final Clinica clinica;
    private final PacienteInfo paciente;

    public Paciente(Clinica clinica, PacienteInfo paciente) {
        this.clinica = clinica;
        this.paciente = paciente;
    }

    @Override
    public void run() {
        try {
            clinica.llegarPaciente(paciente);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
