package ar.edu.unrn.so.clinica;

import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * Sistemas Operativos 2024
 * Trabajo entregable 3
 * @author Álvaro Bayón
 * Detecta una señal y notifica a Main
 */
public class Handler implements SignalHandler {

	@Override
	public void handle(Signal sig) throws IllegalArgumentException{		
		System.out.println("Señal " + sig.getNumber() + " " + sig.getName());
		// Avisa que se recibio una señal cambiando el estado del flag
		Main.signalReceived.set(true);

	}

}
