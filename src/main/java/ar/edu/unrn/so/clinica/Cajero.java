package ar.edu.unrn.so.clinica;

import java.util.Random;

/**
 * Sistemas Operativos 2024
 * Trabajo entregable 3
 * @author Álvaro Bayón
 * Cajero que atiende a los pacientes esperando para pagar
 * Similar a un consumidor del problema de productores y consumidores
 */
class Cajero<T> implements Runnable {
    private final Buffer<T> buffer;
    private final int id;
    private Clinica clinica;

    public Cajero(Buffer<T> buffer, int id, Clinica clinica) {
        this.buffer = buffer;
        this.id = id;
        this.clinica = clinica;
    }

    @Override
    public void run() {
    	T elemento;
        try {
            while (true) {
                elemento = buffer.consume();
                // Simula tiempo de atención
                Thread.sleep(new Random().nextInt(1000)); 
                System.out.println("El cajero "+this.id+" atendió a " + elemento.toString());
                clinica.salirDeCaja();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}