package ar.edu.unrn.so.clinica;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import sun.misc.Signal;

/**
 * Sistemas Operativos 2024
 * Trabajo entregable 3
 * Centro médico VIP (ejercicio 3.1)
 * @author Álvaro Bayón
 *
 */
public class Main {
	
	// Problema del medico con n medicos y n colas de espera
    public static final AtomicInteger atendidos = new AtomicInteger(0);
	//Indica si se recibió un signal
	public static final AtomicBoolean signalReceived = new AtomicBoolean(false);		
	
    public static void main(String[] args) {
        final int MAX_SILLAS = 28; // Máximo de sillas
        final int NUM_MEDICOS = 4; // Número de medicos disponibles
        
        Clinica clinica = new Clinica(MAX_SILLAS, NUM_MEDICOS);

    	// Manejador del signal TERM
    	Signal.handle(new Signal("TERM"), new Handler());    	
                
        // Inicia los medicos
        for (int i = 0; i < NUM_MEDICOS; i++) {
            Thread medico = new Thread(new Medico(clinica, i));
            medico.start();
        }

        // Creación de pacientes
        int id = 0;
        int r = 0;
        boolean vip = false;
        while (id < 100) {
        	// Genera pacientes VIP en funcion de un random
        	r = (int)(Math.random() * 4);
        	vip = r==3; 
            PacienteInfo paciente = new PacienteInfo(id + 1, vip);
            Thread pacienteThread = new Thread(new Paciente(clinica, paciente));
            pacienteThread.start();
            
            // Pausa para modelar la llegada de pacientes a intervalos aleatorios
            try {
                Thread.sleep((long) (Math.random() * 1000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Si se recibió una señal TERM no entran más clientes
            if(signalReceived.get()) {
            	break;
            }            
            id++;
        }

        // Informe final
        try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        // Pacientes atendidos
        System.out.println();
        System.out.println();
        //System.out.println("Atendidos: " + atendidos);
        System.out.println("Atendidos en total: " + clinica.totalAtendidos());
        //System.out.println("Total en VIP: " + clinica.totalEnVip());
        //System.out.println("Total en Caja: " + clinica.totalEnCaja());
        // Pacientes atendidos por cada médico
        for (int i=0; i < NUM_MEDICOS; i++) {
            System.out.println("Atendidos por el médico " + i + " = " + clinica.atendidos(i));
        }
        
        System.exit(0);
    }
}
