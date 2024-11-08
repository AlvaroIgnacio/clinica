package ar.edu.unrn.so.clinica;

/**
 * Sistemas Operativos 2024
 * Trabajo entregable 3
 * @author Álvaro Bayón
 * Cumple la misma función que un peluquero en el
 * problema del peluquero dormilón
 */
class Medico implements Runnable {
    private final Clinica clinica;
    private final int id;

    public Medico(Clinica clinica, int id) {
        this.clinica = clinica;
        this.id = id;
    }

    @Override
    public void run() {
    	PacienteInfo cliente;
        while (true) {
            try {
                cliente = clinica.atenderPaciente(id);
                // Tiempo de atención
                Thread.sleep(1000); 
                clinica.terminarAtencion(id, cliente);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}