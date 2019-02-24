
public class Algoritmo {
	
	private static final int POBLACION_SIZE = 12;
	private static final int CROMOSOMA_SIZE = 10;
	private static final int MAX_GENERACIONES = 8;
	private static final double MUTATION_RATE = 0.1;
	
	public static void main(String[] args) {
		Algoritmo a = new Algoritmo();
	}
	
	public Algoritmo() {
		
		Poblacion poblacion = initPoblacion(POBLACION_SIZE, CROMOSOMA_SIZE);
		imprimirPoblacion(poblacion);
		int numeroGeneracion = 1;
		
		
		evaluar(poblacion);
		imprimirPoblacion(poblacion);
		
		while(!esSolucionSuficiente(numeroGeneracion)) {
			System.out.println("************ GENERACIÓN " + numeroGeneracion + " ************");
			
			
			
			poblacion = seleccionar(poblacion);
			imprimirPoblacion(poblacion);
			
			poblacion = cruzar(poblacion);
			imprimirPoblacion(poblacion);

			poblacion = mutar(poblacion);
			imprimirPoblacion(poblacion);
			
			evaluar(poblacion);
			imprimirPoblacion(poblacion);

			numeroGeneracion++;
		}
		
		
	}

	private void imprimirPoblacion(Poblacion poblacion) {
		
		for (int i = 0; i < POBLACION_SIZE; i++) {
			System.out.println(i+1 + ". " + poblacion.getIndividuo(i).toString());
		}
		System.out.println("--------------------------------------------\n");

	}

	public Poblacion initPoblacion(int poblacionSize, int cromosomaSize) {
		System.out.println("CREANDO POBLACIÓN...");
		poblacionSize = POBLACION_SIZE;
		cromosomaSize = CROMOSOMA_SIZE;
		
		Poblacion poblacion = new Poblacion(poblacionSize, cromosomaSize);
		
		return poblacion;
	}

	
	private void evaluar(Poblacion poblacion) {
		System.out.println("EVALUANDO...");
		for (int i = 0; i < poblacion.getSize(); i++) {
			funcionAptitud(poblacion.getIndividuo(i));
		}
	}
	
	private void funcionAptitud(Individual individuo) {
		int numeroDeUnos = 0;
		int[] cromosoma = individuo.getCromosoma();
		
		for (int i = 0; i < CROMOSOMA_SIZE; i++) {
			if(cromosoma[i] == 1) {
				numeroDeUnos++;
			}
		}
		
		individuo.setAptitud(numeroDeUnos);
	}

	private boolean esSolucionSuficiente(int numeroGeneracion) {
		return numeroGeneracion>= MAX_GENERACIONES;
	}
	
	private Poblacion seleccionar(Poblacion poblacion) {
		System.out.println("SELECCIONANDO...");
		Individual[] cromosomas = poblacion.getPoblacion();
		Individual[] cromosomasSeleccionados = poblacion.getPoblacion();
		//Poblacion seleccionada = new Poblacion(POBLACION_SIZE);
		
		int sumaAptitudes = sumarAptitudes(poblacion);
		// Inicializamos la tirada de la ruleta y el indice de la ruleta que vamos a ir comprobando
		int posTiradaRuleta = 0;	
		int ruleta = 0;
		
		for (int cromSelec = 0; cromSelec < POBLACION_SIZE; cromSelec++) {
			
			posTiradaRuleta = (int) (Math.random()*sumaAptitudes);	// entre 0 y sumaAptitudes
			ruleta = 0;
			
			// Iteramos para encontrar el cromosoma que vamos a seleccionar según la tirada
			for (int crom = 0; crom < POBLACION_SIZE; crom++) {
				ruleta+= cromosomas[crom].getAptitud();
				if(ruleta >= posTiradaRuleta) {
					cromosomasSeleccionados[cromSelec] = cromosomas[crom];
					crom = POBLACION_SIZE;	// para salir del bucle interior
				}
			}
		}
		
		poblacion.setPoblacion(cromosomasSeleccionados);
		return poblacion;
	}
	
	private int sumarAptitudes(Poblacion poblacion) {
		Individual cromosomas[] = poblacion.getPoblacion();
		int suma = 0;
		for (int i = 0; i < cromosomas.length; i++) {
			suma+= cromosomas[i].getAptitud();
		}
		
		return suma;
	}

	private Poblacion cruzar(Poblacion poblacion) {
		
		System.out.println("CRUZANDO...");
		
		Poblacion nuevaPoblacion = new Poblacion(POBLACION_SIZE, CROMOSOMA_SIZE);
		int puntoDeCorte = (int) (Math.random()*((CROMOSOMA_SIZE-1)-1+1)+1);	// No dejo que salga un cromosoma tal cual igual
		System.out.println("Punto de corte: "+puntoDeCorte);
		
		for (int i = 0; i < POBLACION_SIZE/2; i++) {
			int padre1 = (int) (Math.random()*((POBLACION_SIZE-1)-0+1)+0);	// TODO poner por el orden de los seleccionados 
			int padre2 = (int) (Math.random()*((POBLACION_SIZE-1)-0+1)+0);

			
			while(padre1 == padre2) {
				padre2 = (int) (Math.random()*((POBLACION_SIZE-1)-0+1)+0);
				System.out.println("EHHHHH");
			}
			System.out.println("Cruces entre los padres... "+ (padre1+1) + " y " + (padre2+1));
			
			Individual hijo1 = new Individual(CROMOSOMA_SIZE);
			Individual hijo2 = new Individual(CROMOSOMA_SIZE);
			
			hijo1.formarHijo(0, puntoDeCorte, poblacion.getIndividuo(padre1).getCromosoma());
			hijo1.formarHijo(puntoDeCorte, CROMOSOMA_SIZE, poblacion.getIndividuo(padre2).getCromosoma());
			
			hijo2.formarHijo(0, puntoDeCorte, poblacion.getIndividuo(padre2).getCromosoma());
			hijo2.formarHijo(puntoDeCorte, CROMOSOMA_SIZE, poblacion.getIndividuo(padre1).getCromosoma());
			
			nuevaPoblacion.setPoblacionCruce(hijo1, hijo2, i*2);
		}
		return nuevaPoblacion;
	}
	
	private Poblacion mutar(Poblacion poblacion) {
		
		System.out.println("MUTANDO...");
		
		Poblacion nuevaPoblacion = new Poblacion(POBLACION_SIZE, CROMOSOMA_SIZE);
		
		for (int i = 0; i < POBLACION_SIZE; i++) {
			int bitAleatorio = (int) (Math.random()*((CROMOSOMA_SIZE-1)-0+1)+0);
			double probabilidadCrossover = (Math.random()*1);
			System.out.println("probabilidad crossover: "+probabilidadCrossover);
			
			if(probabilidadCrossover > MUTATION_RATE) {
					poblacion.getIndividuo(i).cambiarBit(bitAleatorio);
			}
			nuevaPoblacion.setIndividuo(i, poblacion.getIndividuo(i));
		}

		return nuevaPoblacion;
	}
	
	
}
