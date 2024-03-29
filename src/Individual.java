/**
 * 
 * @author Ander
 *
 */
public class Individual {	
	
	private int[] cromosoma;
	private int aptitud;
	
	public Individual(int cromosomaSize) {
		
		cromosoma = new int[cromosomaSize];
		aptitud = -1;
		
		for(int i = 0; i < cromosoma.length; i++) {
			cromosoma[i] = (int) (Math.random()*(1-0+1)+0);
		}
	}

	public Individual(Individual individual) {
		cromosoma = new int[individual.getCromosoma().length];
		aptitud = individual.getAptitud();
		
		for (int i = 0; i < cromosoma.length; i++) {
			cromosoma[i] = individual.getCromosoma()[i];
		}
	}

	public int[] getCromosoma() {
		return cromosoma;
	}

	public void setCromosoma(int[] cromosoma) {
		this.cromosoma = cromosoma;
	}

	public int getAptitud() {
		return aptitud;
	}

	public void setAptitud(int aptitud) {
		this.aptitud = aptitud;
	}
	
	public void formarHijo(int inicioBitPadre, int finalBitPadre, int[] cromosomaPadre) {
		
		for (int i = inicioBitPadre; i < finalBitPadre; i++) {
			this.cromosoma[i] = cromosomaPadre[i]; 
		}
	}
	
	public void cambiarBit(int bitAleatorio) {
		if(this.cromosoma[bitAleatorio] == 1) {
			this.cromosoma[bitAleatorio] = 0;
		} else {
			this.cromosoma[bitAleatorio] = 1;
		}	
	}

	@Override
	public String toString() {
		String output = "";
		for (int i = 0; i < this.cromosoma.length; i++) {
			output += this.cromosoma[i];
		}
		if(aptitud == -1) {
			return "[cromosoma = " + output + "]";
		} else {
			return "[cromosoma = " + output + ", aptitud = " + aptitud + "]";
		}
	}

}
