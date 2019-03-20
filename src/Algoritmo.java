import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ListIterator;

import javax.imageio.ImageIO;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;

/**
 * 
 * @author Ander
 *
 */
public class Algoritmo {

	
	/****************************************************************************************************************/
	/****************************************************************************************************************/
	
	/**
	 * CONFIGURACIÓN DEL ALGORITMO, VALORES
	 * 
	 */
	private static final int POBLACION_SIZE = 100;
	private static final int CROMOSOMA_SIZE = 12;
	private static final int MAX_GENERACIONES = 22;
	private static final double MUTATION_RATE = 0.015;
	/**
	 * Si el número de poblaciones últimas consecutivas indicado tiene la misma media de aptitud.
	 * Se observará dicha característica desde la generación MAX_GENERACIONES-COND_PARADA_POBSIGUALES 
	 * 
	 * *nota: si el número introducido es mayor que el máximo de generaciones se igualarán ambos valores
	 * 		y por tanto se observará si existen X generaciones con medias iguales desde el principio
	 */
	private static int COND_PARADA_POBSIGUALES = 3;
	
	/**
	 * FUNCION DE APTITUD
	 * 	Opciones:
	 * 		1 = Contar unos
	 * 		2 = 2^(nº de unos)
	 */
	private final int funcionAptitud = 2;

	
	/**
	 * Poner 'true' si se quiere imprimir y 'false' si no
	 */
	private static final boolean IMPRIMIR_INICIAL = true;
	private static final boolean IMPRIMIR_EVALUAR = false;
	private static final boolean IMPRIMIR_SELECCION = false;
	private static final boolean IMPRIMIR_CRUCE = false;
	private static final boolean IMPRIMIR_MUTAR = false;
	private static final boolean IMPRIMIR_FIN = false;
	//	En cada ejecución se creará una gráfica en formato .png en la carpeta del proyecto
	private static final boolean SACAR_GRAFICA_IMG = true;
	
	/****************************************************************************************************************/
	/****************************************************************************************************************/

	
	/**
	 * Variables para modificar la salida por pantalla
	 */
	final String pattern = "| %"+Integer.toString(CROMOSOMA_SIZE+35)+"s |\n";
	final String patternIzq = "| %-"+Integer.toString(CROMOSOMA_SIZE+35)+"s |\n";
	final String line = "|" + new String(new char[CROMOSOMA_SIZE+37]).replace("\0", "-") + "|\n";

	
	public static void main(String[] args) {
		
		Algoritmo a = new Algoritmo();
		
		
	}

	public Algoritmo() {

		long startTime = System.nanoTime();
		
		Poblacion poblacion = initPoblacion();
		int numeroGeneracion = 1;
		ArrayList<Double> aptitudPoblaciones = new ArrayList<Double>();
		ArrayList<Double> minimosPoblaciones = new ArrayList<Double>();
		ArrayList<Double> maximosPoblaciones = new ArrayList<Double>();


		evaluar(poblacion);

		while (!esSolucionSuficiente(numeroGeneracion, aptitudPoblaciones)) {

			if(IMPRIMIR_SELECCION || IMPRIMIR_CRUCE || IMPRIMIR_MUTAR || IMPRIMIR_EVALUAR) {
				System.out.println(String.format(pattern, "******************************************************"));
				System.out.println(String.format(pattern,"******************* GENERACIÓN " + (numeroGeneracion) + " ********************"));
				System.out.println(String.format(pattern,"******************************************************"));
			}

			poblacion = seleccionar(poblacion);

			poblacion = cruzar(poblacion);

			poblacion = mutar(poblacion);

			double aptitudGeneracionN = evaluar(poblacion);
			aptitudPoblaciones.add(aptitudGeneracionN);
			minimosPoblaciones.add(poblacion.getMinimo());
			maximosPoblaciones.add(poblacion.getMaximo());
			
			System.out.println(line);
			System.out.println(String.format(pattern,"ESTADÍSTICAS G"+numeroGeneracion));
			System.out.println(line);

			System.out.println(String.format(pattern,"Aptitud media: " + aptitudGeneracionN));
			System.out.println(String.format(pattern,"Mínima aptitud: " + poblacion.getMinimo()));
			System.out.println(String.format(pattern,"Máxima aptitud: " + poblacion.getMaximo()));
			System.out.println(line);

			numeroGeneracion++;
			
		}
		
		long endTime = System.nanoTime() - startTime;
		System.out.println("Tiempo de ejecución del algoritmo: " + (endTime)/1e6 + " ms");
		
		if(SACAR_GRAFICA_IMG) {
			int maxx = obtenerMaximoDeMaximos(maximosPoblaciones);
			crearGraficaImagen(aptitudPoblaciones, minimosPoblaciones, maximosPoblaciones, maxx);
		}
	}

	private void imprimir(String salida, String funcion) {
		switch (funcion) {
		case "init":
			if (IMPRIMIR_INICIAL) {
				imprimir(salida);
			}
			break;
		
		case "evaluar":
			if (IMPRIMIR_EVALUAR) {
				imprimir(salida);
			}
			break;
		
		case "seleccionar":
			if (IMPRIMIR_SELECCION) {
				imprimir(salida);
			}
			break;
			
		case "cruzar":
			if (IMPRIMIR_CRUCE) {
				
				imprimir(salida);
			}
			break;
			
		case "mutar":
			if (IMPRIMIR_MUTAR) {
				
				imprimir(salida);
			}
			break;
			
		case "fin":
			if (IMPRIMIR_FIN) {
				imprimir(salida);
			}

		default:
			break;
		}

	}
	
	private void imprimir(String str) {
		System.out.println(str);
	}

	/**
	 * Preparación de impresión de la población a consola
	 * 
	 * @param poblacion
	 * @return
	 */
	private String imprimirPoblacion(Poblacion poblacion) {

		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < POBLACION_SIZE; i++) {
			sb.append(String.format(pattern, i + 1 + ". " + poblacion.getIndividuo(i).toString()));
		}
		sb.append(line);
		
		return sb.toString();
	}

	/**
	 * Crea la población inicial
	 * @return
	 */
	public Poblacion initPoblacion() {
		StringBuilder sb = new StringBuilder();
		
		// Comprobación parámetro condición parada "mirar atrás"
		if(COND_PARADA_POBSIGUALES>MAX_GENERACIONES) {
			COND_PARADA_POBSIGUALES = MAX_GENERACIONES;
		}
		
		sb.append(line);
		sb.append(String.format(patternIzq, "POBLACIÓN INICIAL... "));
		sb.append(String.format(patternIzq, "Tamaño población: " + POBLACION_SIZE));
		sb.append(String.format(patternIzq, "Tamaño cromosoma: " + CROMOSOMA_SIZE));
		sb.append(String.format(patternIzq, "Máximo de generaciones: " + MAX_GENERACIONES));
		sb.append(String.format(patternIzq, "Probabilidad de mutación: " + MUTATION_RATE));
		sb.append(String.format(patternIzq, "Comprobar aptitudes iguales hacia atrás: " + COND_PARADA_POBSIGUALES));
		sb.append(String.format(patternIzq, "  Gener. mínima para parar: " + (MAX_GENERACIONES-COND_PARADA_POBSIGUALES)));



		sb.append(line);
		
		Poblacion poblacion = new Poblacion(POBLACION_SIZE, CROMOSOMA_SIZE);
		sb.append(imprimirPoblacion(poblacion));
		imprimir(sb.toString(), "init");

		return poblacion;
	}

	/**
	 * Evalua la población y asigna a los cromosomas una aptitud
	 * @param poblacion
	 * @return
	 */
	private double evaluar(Poblacion poblacion) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(line);
		sb.append(String.format(patternIzq, "EVALUANDO..."));
		sb.append(line);
		sb.append(String.format(patternIzq, "Población evaluada:"));
		
		int aptitudMediaPob = 0;
		for (int i = 0; i < poblacion.getSize(); i++) {
			aptitudMediaPob += funcionAptitud(poblacion.getIndividuo(i));
		}
		
		sb.append(imprimirPoblacion(poblacion));
		imprimir(sb.toString(), "evaluar");
		
		return aptitudMediaPob/POBLACION_SIZE;
	}

	/**
	 * Calcula la aptitud de cada individuo
	 * 
	 * @param individuo
	 * @return
	 */
	private int funcionAptitud(Individual individuo) {
		int aptitud = 0;
		int[] cromosoma = individuo.getCromosoma();

		for (int i = 0; i < CROMOSOMA_SIZE; i++) {
			if (cromosoma[i] == 1) {
				aptitud++;
			}
		}
		
		if(funcionAptitud==2) aptitud = (int) Math.pow(2, aptitud);
		
		individuo.setAptitud(aptitud);
		return aptitud;
	}
	
	/**
	 * Comprueba si se ha llegado a la solución suficiente según las condiciones impuestas.
	 * 
	 * @param numeroGeneracion
	 * @param aptitudPoblaciones
	 * @return
	 */
	private boolean esSolucionSuficiente(int numeroGeneracion, ArrayList<Double> aptitudPoblaciones) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(line);
		sb.append(String.format(patternIzq, "¿ES SOLUCIÓN SUFICIENTE?..."));
		sb.append(line);
		
		boolean ret = false;
		
		/**
		 *  Obtener número mínimo de generaciones sin comprobar repetición 
		 *  de la aptitud media de las últimas
		 *  
		 *  Minimo: Max_Generaciones - Condicion_N_Iguales_Ultimas_Pobs
		 */
		
		double genMinSinStop = MAX_GENERACIONES-COND_PARADA_POBSIGUALES;
		
		
		
		// Si nº de generación cumple el mínimo anterior ---> análisis COND_PARADA_POBSIGUALES
		if(numeroGeneracion>genMinSinStop && nUltimasIguales(aptitudPoblaciones, sb)) {
			sb.append(String.format(patternIzq, ""));
			imprimir(sb.toString(), "fin");
			return true;
		} else {
			
			if (numeroGeneracion > MAX_GENERACIONES) {
				ret = true;
				sb.append(String.format(patternIzq, "nº máximo generaciones alcanzado. ("+MAX_GENERACIONES+")"));
				sb.append(String.format(patternIzq, "SOL. SUFICIENTE - Paramos el algoritmo!"));
			} else {
				sb.append(String.format(patternIzq, "No es solución suficiente. Opciones:"));
				sb.append(String.format(patternIzq, "   - No sobrepasa mínimo parada..."));
				sb.append(String.format(patternIzq, "   - No hay "+COND_PARADA_POBSIGUALES+" generaciones aptitudes iguales"));
				sb.append(String.format(patternIzq, "   - No sobrepasa máx generaciones"));	
			}
			
			sb.append(line);
			sb.append(String.format(patternIzq, ""));
			imprimir(sb.toString(), "fin");
			return ret;
		}
	}

	/**
	 * Comprueba si las X últimas poblaciones tienen la misma aptitud media.
	 * El número de poblaciones a mirar está configurado de inicio.
	 * 
	 * Si se alcanza el número de generación (MAX GENERACIONES - COND_PARADA_POBSIGUALES) 
	 * se empieza a contar cuántas poblaciones hacia atrás son iguales en cada iteración
	 * 
	 * @param aptitudPoblaciones
	 * @param sb2 
	 * @return
	 */
	private boolean nUltimasIguales(ArrayList<Double> aptitudPoblaciones, StringBuilder sb2) {
		
		sb2.append(String.format(patternIzq, "Alcanzado índice mínimo para parar ("+(MAX_GENERACIONES-COND_PARADA_POBSIGUALES)+")"));
		
		ListIterator<Double> iterator = aptitudPoblaciones.listIterator(aptitudPoblaciones.size());
		int n = 0;
		
		int i = aptitudPoblaciones.size()-1; 
		boolean diferente = false;
		
		while(n<COND_PARADA_POBSIGUALES && iterator.hasPrevious() && (i > 0) && (diferente == false)) {
			if(aptitudPoblaciones.get(i).equals(aptitudPoblaciones.get(i-1))) {
				n++;
			} else {
				diferente = true;
			}
			i--;
		}
		
		boolean ret = false;
		// Se comprueba cuantas poblaciones hacia atrás son iguales
		// Ejemplo: estamos en generacion20, condición: 3 iguales, mínimo índice para mirar: 13
		//.... generacion11[5] generacion12[7] generacion13[7] generacion14[7] AQUI SE PARARIA
		if (n>=(COND_PARADA_POBSIGUALES-1)) {
			ret = true;
			sb2.append(String.format(patternIzq, COND_PARADA_POBSIGUALES+" últimas generaciones misma aptitud media"));
			sb2.append(String.format(patternIzq, "SOL. SUFICIENTE - Paramos el algoritmo!"));
			sb2.append(line);

		}
		
		return ret;
	}

	/**
	 * Selecciona a la población para la siguiente generación.
	 * 
	 * @param poblacion
	 * @return
	 */
	private Poblacion seleccionar(Poblacion poblacion) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(line);
		sb.append(String.format(patternIzq, "SELECCIONANDO..."));
		sb.append(line);
		
		sb.append(String.format(patternIzq, "La población antes de seleccionar:"));

		// Población antes de la selección
		sb.append(imprimirPoblacion(poblacion));
		
		Individual[] cromosomas = poblacion.getPoblacion();
		Individual[] cromosomasSeleccionados = new Individual[poblacion.getSize()];

		int sumaAptitudes = sumarAptitudes(poblacion);
		// Inicializamos la tirada de la ruleta y el indice de la ruleta que vamos a ir
		// comprobando
		int posTiradaRuleta = 0;
		int ruleta = 0;

		sb.append(String.format(patternIzq,""));
		sb.append(String.format(patternIzq,"Los cromosomas que han sido seleccionados son: "));
		
		for (int cromSelec = 0; cromSelec < POBLACION_SIZE; cromSelec++) {

			posTiradaRuleta = (int) (Math.random() * sumaAptitudes); // entre 0 y sumaAptitudes
			ruleta = 0;

			// Iteramos para encontrar el cromosoma que vamos a seleccionar según la tirada
			for (int crom = 0; crom < POBLACION_SIZE; crom++) {
				ruleta += cromosomas[crom].getAptitud();
				if (ruleta >= posTiradaRuleta) {
					cromosomasSeleccionados[cromSelec] = new Individual(cromosomas[crom]);
					
					
					sb.append(String.format(patternIzq,"...seleccionado nº " + (crom + 1)));
					
					crom = POBLACION_SIZE; // para salir del bucle interior
				}
			}
		}
		
		// Introducimos los seleccionados para actualizar la población
		poblacion.setPoblacion(cromosomasSeleccionados);
		
		sb.append(line);
		sb.append(String.format(patternIzq, "La población después de seleccionar:"));
		sb.append(imprimirPoblacion(poblacion));
		imprimir(sb.toString(), "seleccionar");
		
		return poblacion;
	}

	/**
	 * Suma las aptitudes de todos los individuos de una población (generación)
	 * 
	 * @param poblacion
	 * @return
	 */
	private int sumarAptitudes(Poblacion poblacion) {
		Individual cromosomas[] = poblacion.getPoblacion();
		int suma = 0;
		for (int i = 0; i < cromosomas.length; i++) {
			suma += cromosomas[i].getAptitud();
		}

		return suma;
	}

	/**
	 * Cruce por un punto de la población
	 * 
	 * @param poblacion
	 * @return
	 */
	private Poblacion cruzar(Poblacion poblacion) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(line);
		sb.append(String.format(patternIzq, "CRUZANDO..."));
		sb.append(line);
		
		// Población antes del cruce
		sb.append(String.format(patternIzq, "Población antes del crossover: "));
		sb.append(imprimirPoblacion(poblacion));
		
		boolean esImpar = false;
		if((POBLACION_SIZE/2) != 0) {
			esImpar = true;
		}
		
		Poblacion nuevaPoblacion = new Poblacion(POBLACION_SIZE, CROMOSOMA_SIZE);
		int puntoDeCorte = (int) (Math.random() * ((CROMOSOMA_SIZE - 1) - 1 + 1) + 1); // No dejo que salga un cromosoma tal cual igual
		
		sb.append(String.format(patternIzq, "Punto de corte: " + puntoDeCorte));
		sb.append(String.format(patternIzq, "Cruces entre los padres: "));
		
		for (int i = 0; i < POBLACION_SIZE / 2; i++) {
			
			sb.append(String.format(patternIzq, "..." + (i * 2 + 1) + " y " + (i * 2 + 1 + 1)));

			// los creo random pero luego se ponen a los valores adecuados, para aprovechar constructores
			Individual hijo1 = new Individual(CROMOSOMA_SIZE);
			Individual hijo2 = new Individual(CROMOSOMA_SIZE);

			hijo1.formarHijo(0, puntoDeCorte, poblacion.getIndividuo(i * 2).getCromosoma());
			hijo1.formarHijo(puntoDeCorte, CROMOSOMA_SIZE, poblacion.getIndividuo(i * 2 + 1).getCromosoma());

			hijo2.formarHijo(0, puntoDeCorte, poblacion.getIndividuo(i * 2 + 1).getCromosoma());
			hijo2.formarHijo(puntoDeCorte, CROMOSOMA_SIZE, poblacion.getIndividuo(i * 2).getCromosoma());

			nuevaPoblacion.setPoblacionCruce(hijo1, hijo2, i * 2);
		}
		
		if(esImpar) {
			nuevaPoblacion.setIndividuo(POBLACION_SIZE-1, poblacion.getIndividuo(POBLACION_SIZE-1));
		}
		
		sb.append(String.format(patternIzq, ""));
		sb.append(String.format(patternIzq, "Los 2 hijos de cada cruce se colocan en orden"));
		
		sb.append(line);
		sb.append(String.format(patternIzq, "Población después del crossover: "));
		sb.append(imprimirPoblacion(nuevaPoblacion));
		imprimir(sb.toString(), "cruzar");
		
		return nuevaPoblacion;
	}

	/**
	 * Mutación de la población
	 * 
	 * @param poblacion
	 * @return
	 */
	private Poblacion mutar(Poblacion poblacion) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(line);
		sb.append(String.format(patternIzq, "MUTANDO..."));
		sb.append(line);
		
		boolean sinMutacion = true;
		
		sb.append(String.format(patternIzq, "Población antes de la mutación: "));
		sb.append(imprimirPoblacion(poblacion));

		
		Poblacion nuevaPoblacion = new Poblacion(POBLACION_SIZE, CROMOSOMA_SIZE);

		sb.append(String.format(patternIzq, "Mutaciones en cromosomas nº: "));
		
		for (int i = 0; i < POBLACION_SIZE; i++) {
			int bitAleatorio = (int) (Math.random() * ((CROMOSOMA_SIZE - 1) - 0 + 1) + 0);
			double probabilidadCrossover = (Math.random() * 1);
			//if(IMPRIMIR_MUTAR) System.out.println("probabilidad crossover: "+probabilidadCrossover);

			if (MUTATION_RATE > probabilidadCrossover) {
				sinMutacion = false;
				
				if(IMPRIMIR_MUTAR) {
					sb.append(String.format(patternIzq, "..." + (i+1) + " [bit: " + (bitAleatorio+1) +"][prob sacada: " + String.format("%.4f", probabilidadCrossover) + "]"));
				}
				poblacion.getIndividuo(i).cambiarBit(bitAleatorio);
			}
			nuevaPoblacion.setIndividuo(i, poblacion.getIndividuo(i));
		}
		
		if(sinMutacion) {
			sb.append(String.format(patternIzq, "No se ha producido ninguna mutación."));
			sb.append(String.format(patternIzq, "    La probabilidad era: " + MUTATION_RATE));
			sb.append(line);
			 
		} else {
			sb.append(line);
			sb.append(String.format(patternIzq, "Población después de la mutación: "));
			sb.append(imprimirPoblacion(nuevaPoblacion));
		}
		
		//revisar nueva??? sb.append(imprimirPoblacion(nuevaPoblacion));
		
		imprimir(sb.toString(), "mutar");
		//imprimir(poblacion, "mutar");
		
		return nuevaPoblacion;
	}
	
	/**
	 * Obtiene el valor máximo de aptitud de toda la ejecución del algoritmo
	 * 
	 * @param maximosPoblaciones
	 * @return
	 */
	private int obtenerMaximoDeMaximos(ArrayList<Double> maximosPoblaciones) {
		double max = Integer.MIN_VALUE;
		for (Double double1 : maximosPoblaciones) {
			if(double1>max) {
				max = double1;
			}
		}
		return (int) max;
	}
	
	/**
	 * 
	 * @param aptitudPoblaciones
	 * @param minimosPoblaciones
	 * @param maximosPoblaciones
	 * @param maxx 
	 */
	private void crearGraficaImagen(ArrayList<Double> aptitudPoblaciones, ArrayList<Double> minimosPoblaciones,
			ArrayList<Double> maximosPoblaciones, int maxx) {

		int tam = aptitudPoblaciones.size();

		ArrayList<Integer> valoresEjeX = new ArrayList<Integer>();
		for (int i = 1; i <= tam; i++) {
			valoresEjeX.add(i);
		}

		double[][] graficaMedia = new double[2][tam];
		double[][] graficaMinimos = new double[2][tam];
		double[][] graficaMaximos = new double[2][tam];

		for (int i = 0; i < graficaMedia.length; i++) {
			for (int j = 0; j < graficaMedia[i].length; j++) {
				if (i == 0) {
					graficaMedia[i][j] = valoresEjeX.get(j);
					graficaMinimos[i][j] = valoresEjeX.get(j);
					graficaMaximos[i][j] = valoresEjeX.get(j);
				} else {
					graficaMedia[i][j] = aptitudPoblaciones.get(j);
					graficaMinimos[i][j] = minimosPoblaciones.get(j);
					graficaMaximos[i][j] = maximosPoblaciones.get(j);
				}
			}
		}

		DefaultXYDataset dataset = new DefaultXYDataset();
		dataset.addSeries("media", graficaMedia);
		dataset.addSeries("minimos", graficaMinimos);
		dataset.addSeries("maximos", graficaMaximos);

		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesPaint(0, Color.ORANGE);
		renderer.setSeriesPaint(1, Color.BLUE);
		renderer.setSeriesPaint(2, Color.GREEN);
		renderer.setSeriesStroke(0, new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		renderer.setSeriesStroke(1, new BasicStroke(2));
		renderer.setSeriesStroke(2, new BasicStroke(2));

		JFreeChart chart = ChartFactory.createXYLineChart("Algoritmo genético simple", "Generación", "Aptitud",
				dataset);
		chart.getXYPlot().getRangeAxis().setRange(0, maxx+(maxx/5));
		// ((NumberAxis) chart.getXYPlot().getRangeAxis()).setNumberFormatOverride(new
		// DecimalFormat("#'%'"));
		chart.getXYPlot().setRenderer(renderer);

		/*
		 * SALTAN ERRORES AL SACARLO POR PANTALLA
		 * 
		 * ChartFrame cFrame = new ChartFrame("Algoritmo genetico", chart);
		 * cFrame.pack(); cFrame.setVisible(true);
		 * cFrame.setDefaultCloseOperation(ChartFrame.EXIT_ON_CLOSE); cFrame.setSize(new
		 * Dimension(600, 400));
		 */

		BufferedImage image = chart.createBufferedImage(600, 400);
		try {
			Date date = new Date();
			DateFormat hourdateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
			String format = hourdateFormat.format(date).replace(':', '-');
			format = format.replace('/', '-');
			String name = "grafica_" + format;
			String tipoArchivo = ".png";
			File img = new File(name + tipoArchivo);

			ImageIO.write(image, "png", img);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
