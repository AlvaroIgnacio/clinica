package ar.edu.unrn.so.clinica;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Sistemas Operativos 2024
 * Trabajo entregable 3
 * @author Álvaro Bayón
 * Modela al centro médico (clínica) en forma similar a la 
 * peluquería del problema del peluquero dormilón.
 */
class Clinica {
    private final int MAX_SILLAS;
    private final int NUM_MEDICOS;
    private final Queue<PacienteInfo>[] colasEspera;
    private final Buffer<PacienteInfo> colaPago;
    private final Lock[] locks;
    private final Lock lockTotal = new ReentrantLock();
    private final Condition[] condiciones;
    private final Condition[] condPacientes;
    private final int[] atendidos;
    private int totalAtendidos = 0;
    private int llamados = 0;

    @SuppressWarnings("unchecked")
    public Clinica(int maxSillas, int numMedicos) {
        this.MAX_SILLAS = maxSillas;
        this.NUM_MEDICOS = numMedicos;
        // Colas de espera, una por cada médico y otra para pacientes VIP
        this.colasEspera = new LinkedList[numMedicos+1];
        this.locks = new Lock[numMedicos+1];
        this.condiciones = new Condition[numMedicos+1];
        this.condPacientes = new Condition[numMedicos+1];
        // Cantidad de pacientes atendidos por médico
        this.atendidos = new int[NUM_MEDICOS];

        this.colaPago = new Buffer<>(Integer.MAX_VALUE);
        // Dos cajeros según enunciado del problema
        Thread cajero1 = new Thread(new Cajero<PacienteInfo>(colaPago, 1));
        Thread cajero2 = new Thread(new Cajero<PacienteInfo>(colaPago, 2));

        // colas 0..NUM_MEDICOS-1 para los medicos
        // La cola NUM_MEDICOS es para los pacientes VIP
        for (int i = 0; i <= numMedicos; i++) {
            colasEspera[i] = new LinkedList<>();
            locks[i] = new ReentrantLock();
            condiciones[i] = locks[i].newCondition();
            condPacientes[i] = locks[i].newCondition();
        }
        cajero1.start();
        cajero2.start();
        
    }

    public PacienteInfo atenderPaciente(int medicoId) throws InterruptedException {
        PacienteInfo paciente;

        locks[medicoId].lock();
        llamados++;
        try {
        	//Mientras su cola está vacía y la cola VIP está vacía
            while (colasEspera[medicoId].isEmpty() && colasEspera[NUM_MEDICOS].isEmpty()) {
                System.out.println("El medico " + medicoId + " está ocioso.");
                // Espera un paciente
                condiciones[medicoId].await(); 
            }
            
            // Si el paciente por atender (llamado) es multiplo de 4 entonces busco en la cola vip
            if ((llamados % 4 ==0) && (!colasEspera[NUM_MEDICOS].isEmpty())) {
            	System.out.println("Atendiendo VIP");
            	// Atiende un paciente VIP
            	paciente = colasEspera[NUM_MEDICOS].poll();
            // Si no tengo gente esperando y hay gente en la cola VIP atiendo a un VIP
            } else if ( colasEspera[medicoId].isEmpty() && !colasEspera[NUM_MEDICOS].isEmpty()) {
            	System.out.println("Atendiendo VIP");
            	// Atiende un paciente VIP
            	paciente = colasEspera[NUM_MEDICOS].poll();
            } else {
            	// El médico atiende un paciente de su cola
                paciente = colasEspera[medicoId].poll();             	
            }
            System.out.println("El medico " + medicoId + " está atendiendo al paciente " + paciente.id() + ".");
        } finally {
            locks[medicoId].unlock();
        }
		return paciente;
    }

    public void terminarAtencion(int medicoId, PacienteInfo paciente) throws InterruptedException {
        locks[medicoId].lock();
    	atendidos[medicoId]++;
    	totalAtendidos++;
    	System.out.println("El medico " + medicoId + " termino con un paciente.");
        try {
        	// Despierta al medico si hay un paciente esperando
            condiciones[medicoId].signal();
        } finally {
            locks[medicoId].unlock();
        	Main.atendidos.incrementAndGet();
        	
        	// El paciente atendido se ubica en la cola para pagar
        	// De aquí en más el problema se modela como productores y consumidores
        	// De alguna manera el médico "produce" pacientes que esperan para pagar
        	colaPago.produce(paciente);
        	System.out.println("Agregando paciente "+paciente.id()+" en la cola para pagar");
        }
    }

    public void llegarPaciente(PacienteInfo paciente) throws InterruptedException {   	
        //nroPaciente++;
        if(paciente.vip()) {
            System.out.println("Llegó el paciente VIP " + paciente.id());        	
        } else {
            System.out.println("Llegó el paciente " + paciente.id());        	
        }
        // Asigna un medico aleatorio
        int medicoId = (int) (Math.random() * NUM_MEDICOS); 
        locks[medicoId].lock();
        try {
            //if (colasEspera[medicoId].size() < MAX_SILLAS) {
            if (nroPacientes() < MAX_SILLAS) {
            	System.out.println("Pacientes: " + this.nroPacientes());
            	// Si el que llega es VIP pasarlo a la cola de VIP
            	if (paciente.vip()) {
                    colasEspera[NUM_MEDICOS].offer(paciente);             		
            	} else {
                	// Un paciente se agrega a la cola del medico
                    colasEspera[medicoId].offer(paciente);             		
            	}
                System.out.println("El paciente " + paciente.id() + " ha llegado y se ha sentado con el medico " + medicoId + ".");
                // Despierta al medico si está ocioso
                condiciones[medicoId].signal(); 
            } else {
                //System.out.println("La cola del medico " + medicoId + " está llena. El paciente " + paciente.id() + " se va.");
            	System.out.println("La clínica está llena. El paciente " + paciente.id() + " se va.");
            }
        } finally {
            locks[medicoId].unlock();
        }
    }
    
    private int nroPacientes() {
    	lockTotal.lock();
    	int total = this.colaPago.size();
    	for (int i=0;i<=NUM_MEDICOS;i++) {
    		total += this.colasEspera[i].size();
    	}
    	lockTotal.unlock();
    	return total;
    }
    
    public int totalAtendidos() {
    	int total = 0;
    	for (int i=0;i<NUM_MEDICOS;i++) {
    		total += this.atendidos[i];
    	}
    	return total;    	
    }
    
    public int atendidos(int i) {
    	return this.atendidos[i];
    }
    
    public int totalEnVip() {
    	return colasEspera[NUM_MEDICOS].size();
    }
    
    public int totalEnCaja() {
    	return colaPago.size();
    }
    
}
