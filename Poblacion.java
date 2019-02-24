import org.omg.CORBA.PUBLIC_MEMBER;

public class Poblacion {
	
	private Individual[] poblacion;
	
	public Poblacion(int size) {
		
		poblacion = new Individual[size];
	}
	
	public Poblacion(int poblacionSize, int cromosomaSize) {
		
		poblacion = new Individual[poblacionSize];
		
		for(int i = 0; i < poblacion.length; i++) {
			
			Individual individual = new Individual(cromosomaSize);
			poblacion[i] = individual;
		}
	}

	public Individual[] getPoblacion() {
		return poblacion;
	}

	public void setPoblacion(Individual[] poblacion) {
		this.poblacion = poblacion;
	}
	
	public void setPoblacionCruce(Individual hijo1, Individual hijo2, int posicion) {
		this.poblacion[posicion] = hijo1;
		this.poblacion[posicion+1] = hijo2;
	}

	public Individual getIndividuo(int i) {
		return poblacion[i];
	}
	
	public void setIndividuo(int posicion, Individual nuevoIndividuo) {
		this.poblacion[posicion] = nuevoIndividuo;
	}

	public int getSize() {
		return poblacion.length;
	}
	
	

}
