package ar.edu.unrn.so.clinica;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Sistemas Operativos 2024
 * Trabajo entregable 3
 * @author Álvaro Bayón
 * Buffer del problema productores y consumidores
 * En este caso se modela la cola de caja
 */
class Buffer<T> {
    private final Lock lock = new ReentrantLock();
    private final Condition noVacio = lock.newCondition();
    private final Condition noLleno = lock.newCondition();
    // Buffer de productos implementado como lista doblemente enlazada
    private final LinkedList<T> buffer = new LinkedList<>();
    // Tamaño del buffer
    private final int tamano; 

    public Buffer(int tamano) {
        this.tamano = tamano;
    }

    public void produce(T elem) throws InterruptedException {
        lock.lock();
        try {
        	// Espera hasta que el buffer tenga lugar
            while (buffer.size() >= tamano) {
                noLleno.await(); 
            }
            buffer.add(elem); 
            //System.out.println("Elemento agregado. Total en el buffer: " + buffer.size());
            // Notifica a todos los consumidores que esperan
            noVacio.signalAll(); 
        } finally {
            lock.unlock();
        }
    }

    public T consume() throws InterruptedException {
        T elemento = null;
    	lock.lock();
        try {
        	// Espera hasta que el buffer tenga elementos
            while (buffer.isEmpty()) {
                noVacio.await();
            }
            elemento = buffer.remove();
            //System.out.println("Producto consumido. Total en el buffer: " + buffer.size());
            // Notificamos a los productores que hay lugar en el buffer
            noLleno.signalAll(); 
        } finally {
            lock.unlock();
        }
        return elemento;
    }
    
    public int size() {
    	return this.buffer.size();
    }
}

