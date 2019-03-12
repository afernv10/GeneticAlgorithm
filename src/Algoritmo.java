import java.util.ArrayList;
import java.util.ListIterator;

import com.sun.corba.se.pept.transport.ContactInfo;

public class Algoritmo {

	private static final int POBLACION_SIZE = 10;
	private static final int CROMOSOMA_SIZE = 10;
	private static final int MAX_GENERACIONES = 20;
	private static final double MUTATION_RATE = 0.02;
	/**
	 * Si el número de poblaciones últimas consecutivas indicado tiene la mima media de aptitud
	 */
	private static final int COND_PARADA_POBSIGUALES = 3;

	/**
	 * Poner 1 si se quiere imprimir y 0 si no
	 */
	static final boolean IMPRIMIR_INICIAL = true;
	static final boolean IMPRIMIR_EVALUAR = true;
	static final boolean IMPRIMIR_SELECCION = true;
	static final boolean IMPRIMIR_CRUCE = true;
	static final boolean IMPRIMIR_MUTAR = true;
	private static final boolean IMPRIMIR_FIN = false;

	public static void main(String[] args) {
		Algoritmo a = new Algoritmo();
	}

	public Algoritmo() {

		Poblacion poblacion = initPoblacion(POBLACION_SIZE, CROMOSOMA_SIZE);
		int numeroGeneracion = 1;
		ArrayList<Double> aptitudPoblaciones = new ArrayList<Double>();

		evaluar(poblacion);

		while (!esSolucionSuficiente(numeroGeneracion, aptitudPoblaciones)) {
			if(IMPRIMIR_SELECCION || IMPRIMIR_CRUCE || IMPRIMIR_MUTAR || IMPRIMIR_EVALUAR) {
				System.out.println("***************************************************");
				System.out.println("****************** GENERACIÓN " + numeroGeneracion + " ******************");
				System.out.println("***************************************************");
			}

			poblacion = seleccionar(poblacion);

			poblacion = cruzar(poblacion);

			poblacion = mutar(poblacion);

			double aptitudGeneracionI = evaluar(poblacion);
			aptitudPoblaciones.add(aptitudGeneracionI);

			numeroGeneracion++;
		}
	}

	private void imprimir(Poblacion poblacion, String funcion) {
		switch (funcion) {
		case "init":
			if (IMPRIMIR_INICIAL) {
				System.out.println("CREANDO POBLACIÓN INICIAL...");
				System.out.println("Tamaño población: " + POBLACION_SIZE);
				System.out.println("Tamaño cromosoma: " + CROMOSOMA_SIZE);
				imprimirPoblacion(poblacion);
			}
			break;
		
		case "evaluar":
			if (IMPRIMIR_EVALUAR) {
				System.out.println("EVALUANDO...");
				imprimirPoblacion(poblacion);
			}
			break;
		
		case "seleccionar":
			if (IMPRIMIR_SELECCION) {
				System.out.println("\nLos cromosomas seleccionados forman la nueva población...");
				imprimirPoblacion(poblacion);
			}
			break;
			
		case "cruzar":
			if (IMPRIMIR_CRUCE) {
				System.out.println("\nLos 2 hijos de cada cruce se van colocando en orden...");
				imprimirPoblacion(poblacion);
			}
			break;
			
		case "mutar":
			if (IMPRIMIR_MUTAR) {
				System.out.println("\nLa población tras la(s) mutacion(es) es la siguiente...");
				imprimirPoblacion(poblacion);
			}
			break;
			
		case "fin":
			if (IMPRIMIR_FIN) {
				System.out.println("\nEvaluando si es solución suficiente...");
				
			}

		default:
			break;
		}

	}

	private void imprimirPoblacion(Poblacion poblacion) {

		for (int i = 0; i < POBLACION_SIZE; i++) {
			System.out.println(i + 1 + ". " + poblacion.getIndividuo(i).toString());
		}
		System.out.println("--------------------------------------------\n");

	}

	public Poblacion initPoblacion(int poblacionSize, int cromosomaSize) {

		poblacionSize = POBLACION_SIZE;
		cromosomaSize = CROMOSOMA_SIZE;

		Poblacion poblacion = new Poblacion(poblacionSize, cromosomaSize);

		imprimir(poblacion, "init");

		return poblacion;
	}

	private double evaluar(Poblacion poblacion) {
		int aptitudMediaPob = 0;
		for (int i = 0; i < poblacion.getSize(); i++) {
			aptitudMediaPob += funcionAptitud(poblacion.getIndividuo(i));
		}
		
		imprimir(poblacion, "evaluar");
		return aptitudMediaPob/POBLACION_SIZE;
	}

	private int funcionAptitud(Individual individuo) {
		int numeroDeUnos = 0;
		int[] cromosoma = individuo.getCromosoma();

		for (int i = 0; i < CROMOSOMA_SIZE; i++) {
			if (cromosoma[i] == 1) {
				numeroDeUnos++;
			}
		}

		individuo.setAptitud(numeroDeUnos);
		return numeroDeUnos;
	}

	private boolean esSolucionSuficiente(int numeroGeneracion, ArrayList<Double> aptitudPoblaciones) {
		if(aptitudPoblaciones.size()>3 && nUltimasIguales(aptitudPoblaciones)) {
			return true;
		} else {
			return numeroGeneracion > MAX_GENERACIONES;
		}
	}

	private boolean nUltimasIguales(ArrayList<Double> aptitudPoblaciones) {
		
		ListIterator<Double> iterator = aptitudPoblaciones.listIterator(aptitudPoblaciones.size());
		int n = 0;
		
		for (int i = aptitudPoblaciones.size(); i > 0 ; i--) {
			// TODO implementar parada
		}
		
		while(n!=COND_PARADA_POBSIGUALES && iterator.hasPrevious() && (iterator.previous()==iterator.next())) {
			n++;
		}
		return n>=COND_PARADA_POBSIGUALES;
	}

	private Poblacion seleccionar(Poblacion poblacion) {
		
		if(IMPRIMIR_SELECCION) System.out.println("SELECCIONANDO...");
		
		Individual[] cromosomas = poblacion.getPoblacion();
		Individual[] cromosomasSeleccionados = new Individual[poblacion.getSize()];

		int sumaAptitudes = sumarAptitudes(poblacion);
		// Inicializamos la tirada de la ruleta y el indice de la ruleta que vamos a ir
		// comprobando
		int posTiradaRuleta = 0;
		int ruleta = 0;

		for (int cromSelec = 0; cromSelec < POBLACION_SIZE; cromSelec++) {

			posTiradaRuleta = (int) (Math.random() * sumaAptitudes); // entre 0 y sumaAptitudes
			ruleta = 0;

			// Iteramos para encontrar el cromosoma que vamos a seleccionar según la tirada
			for (int crom = 0; crom < POBLACION_SIZE; crom++) {
				ruleta += cromosomas[crom].getAptitud();
				if (ruleta >= posTiradaRuleta) {
					cromosomasSeleccionados[cromSelec] = new Individual(cromosomas[crom]);
					
					if(IMPRIMIR_SELECCION) {
						System.out.println("Cromosoma seleccionado nº " + (crom + 1));
					}
					crom = POBLACION_SIZE; // para salir del bucle interior
				}
			}
		}

		poblacion.setPoblacion(cromosomasSeleccionados);
		imprimir(poblacion, "seleccionar");
		return poblacion;
	}

	private int sumarAptitudes(Poblacion poblacion) {
		Individual cromosomas[] = poblacion.getPoblacion();
		int suma = 0;
		for (int i = 0; i < cromosomas.length; i++) {
			suma += cromosomas[i].getAptitud();
		}

		return suma;
	}

	private Poblacion cruzar(Poblacion poblacion) {


		Poblacion nuevaPoblacion = new Poblacion(POBLACION_SIZE, CROMOSOMA_SIZE);
		int puntoDeCorte = (int) (Math.random() * ((CROMOSOMA_SIZE - 1) - 1 + 1) + 1); // No dejo que salga un cromosoma
																						// tal cual igual
		if(IMPRIMIR_CRUCE) {
			System.out.println("CRUZANDO...");
			System.out.println("Punto de corte: " + puntoDeCorte);	
		}

		for (int i = 0; i < POBLACION_SIZE / 2; i++) {
			
			if(IMPRIMIR_CRUCE) {
				System.out.println("Cruces entre los padres... " + (i * 2 + 1) + " y " + (i * 2 + 1 + 1));
				System.out.println("La población tras el cruce es la siguiente...");
			}

			Individual hijo1 = new Individual(CROMOSOMA_SIZE);
			Individual hijo2 = new Individual(CROMOSOMA_SIZE);

			hijo1.formarHijo(0, puntoDeCorte, poblacion.getIndividuo(i * 2).getCromosoma());
			hijo1.formarHijo(puntoDeCorte, CROMOSOMA_SIZE, poblacion.getIndividuo(i * 2 + 1).getCromosoma());

			hijo2.formarHijo(0, puntoDeCorte, poblacion.getIndividuo(i * 2 + 1).getCromosoma());
			hijo2.formarHijo(puntoDeCorte, CROMOSOMA_SIZE, poblacion.getIndividuo(i * 2).getCromosoma());

			nuevaPoblacion.setPoblacionCruce(hijo1, hijo2, i * 2);
		}
		
		imprimir(nuevaPoblacion, "cruzar");
		
		return nuevaPoblacion;
	}

	private Poblacion mutar(Poblacion poblacion) {
		
		boolean sinMutacion = true;
		
		if(IMPRIMIR_MUTAR) System.out.println("MUTANDO...");
		
		Poblacion nuevaPoblacion = new Poblacion(POBLACION_SIZE, CROMOSOMA_SIZE);

		for (int i = 0; i < POBLACION_SIZE; i++) {
			int bitAleatorio = (int) (Math.random() * ((CROMOSOMA_SIZE - 1) - 0 + 1) + 0);
			double probabilidadCrossover = (Math.random() * 1);
			//if(IMPRIMIR_MUTAR) System.out.println("probabilidad crossover: "+probabilidadCrossover);

			if (MUTATION_RATE > probabilidadCrossover) {
				sinMutacion = false;
				if(IMPRIMIR_MUTAR) {
					System.out.println("Se produce mutación en cromosoma: " + (i+1) + " [bit: " + (bitAleatorio+1) +"] [prob crossover: " + probabilidadCrossover + "]");
				}
				poblacion.getIndividuo(i).cambiarBit(bitAleatorio);
			}
			nuevaPoblacion.setIndividuo(i, poblacion.getIndividuo(i));
		}
		
		if(IMPRIMIR_MUTAR && sinMutacion) System.out.println("No se ha producido ninguna mutación. Las probs han sido > " + MUTATION_RATE);
		imprimir(poblacion, "mutar");
		
		return nuevaPoblacion;
	}

}
