package creditcard;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AccountAnalysis {

	//Main-Methode zum Testen.
	public static void main(String[] args) {
		AccountAnalysis analysis = new AccountAnalysis();
		
		Map<Integer, Integer> result = null;
		
		try {
			result = analysis.mapFilterReduceBankAccount("konto.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Ergebnis: " + result);
	}

	public Map<Integer, Integer> mapFilterReduceBankAccount(String filepath) throws IOException {
		
		// create stream from file
		Stream<String> lines = Files.lines(Paths.get(filepath));
		
		// capture time
		long start = System.currentTimeMillis();

		// result map
		Map<Integer, Integer> result = null;
		
		result = lines
				.map(e -> new Transaction(e)) // Konvertiere in gut zu bearbeitende Paare (Grundprozess Schritt: Map-Phase, Seite 38)
				.filter(e -> e.getKey()>=1505 && e.getKey()<=1510) // Filter, wie in der aufgabenstellung. Nur die Nummern 1505 bis 1510
				.filter(e -> e.getValue()>=100) // Filter wie in der Aufgabenstellung, keine Umsätze unter 100
				.parallel()
				.collect(
						Collectors.groupingByConcurrent( // Schritt Shuffle. Alle paare mit dem gleichen Schlüssel werden somit Groupiert
								Transaction::getKey, 
								Collectors.summingInt(Transaction::getValue))  // Schritt Reduce (aufsummieren aller werte der Transaktionen mit dem gleichen key)
						)
				;
		
		// close stream
		lines.close();
		
		// print time
		System.out.println("Zeit in ms.: " + (System.currentTimeMillis() - start));
		
		return result;
	}
}