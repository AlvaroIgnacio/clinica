package ar.edu.unrn.so.clinica;

/**
 * Sistemas Operativos 2024
 * Trabajo entregable 3
 * @author Álvaro Bayón
 *
 */
public class PacienteInfo {
    private final int id;
    private final boolean vip;

    public PacienteInfo(int id, boolean vip) {
        this.id = id;
        this.vip = vip;
    }

    public int id() {
        return id;
    }
    
    public boolean vip() {
    	return this.vip;
    }
    
    @Override
    public String toString() {
    	return String.valueOf(this.id);
    } 
}
