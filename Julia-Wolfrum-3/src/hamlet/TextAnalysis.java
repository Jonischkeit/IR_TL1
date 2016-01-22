package hamlet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TextAnalysis {

	// stopword list
	protected static final String[] STOP_ARRAY = new String[] { "a", "about",
			"above", "above", "across", "after", "afterwards", "again",
			"against", "all", "almost", "alone", "along", "already", "also",
			"although", "always", "am", "among", "amongst", "amoungst",
			"amount", "an", "and", "another", "any", "anyhow", "anyone",
			"anything", "anyway", "anywhere", "are", "around", "as", "at",
			"back", "be", "became", "because", "become", "becomes", "becoming",
			"been", "before", "beforehand", "behind", "being", "below",
			"beside", "besides", "between", "beyond", "bill", "both", "bottom",
			"but", "by", "call", "can", "cannot", "cant", "co", "con", "could",
			"couldnt", "cry", "de", "describe", "detail", "do", "done", "down",
			"due", "during", "each", "eg", "eight", "either", "eleven", "else",
			"elsewhere", "empty", "enough", "etc", "even", "ever", "every",
			"everyone", "everything", "everywhere", "except", "few", "fifteen",
			"fify", "fill", "find", "fire", "first", "five", "for", "former",
			"formerly", "forty", "found", "four", "from", "front", "full",
			"further", "get", "give", "go", "had", "has", "hasnt", "have",
			"he", "hence", "her", "here", "hereafter", "hereby", "herein",
			"hereupon", "hers", "herself", "him", "himself", "his", "how",
			"however", "hundred", "ie", "if", "in", "inc", "indeed",
			"interest", "into", "is", "it", "its", "itself", "keep", "last",
			"latter", "latterly", "least", "less", "ltd", "made", "many",
			"may", "me", "meanwhile", "might", "mill", "mine", "more",
			"moreover", "most", "mostly", "move", "much", "must", "my",
			"myself", "name", "namely", "neither", "never", "nevertheless",
			"next", "nine", "no", "nobody", "none", "noone", "nor", "not",
			"nothing", "now", "nowhere", "of", "off", "often", "on", "once",
			"one", "only", "onto", "or", "other", "others", "otherwise", "our",
			"ours", "ourselves", "out", "over", "own", "part", "per",
			"perhaps", "please", "put", "rather", "re", "same", "see", "seem",
			"seemed", "seeming", "seems", "serious", "several", "she",
			"should", "show", "side", "since", "sincere", "six", "sixty", "so",
			"some", "somehow", "someone", "something", "sometime", "sometimes",
			"somewhere", "still", "such", "system", "take", "ten", "than",
			"that", "the", "their", "them", "themselves", "then", "thence",
			"there", "thereafter", "thereby", "therefore", "therein",
			"thereupon", "these", "they", "thickv", "thin", "third", "this",
			"those", "though", "three", "through", "throughout", "thru",
			"thus", "to", "together", "too", "top", "toward", "towards",
			"twelve", "twenty", "two", "un", "under", "until", "up", "upon",
			"us", "very", "via", "was", "we", "well", "were", "what",
			"whatever", "when", "whence", "whenever", "where", "whereafter",
			"whereas", "whereby", "wherein", "whereupon", "wherever",
			"whether", "which", "while", "whither", "who", "whoever", "whole",
			"whom", "whose", "why", "will", "with", "within", "without",
			"would", "yet", "you", "your", "yours", "yourself", "yourselves",
			"the" };

	// Main method for testing
	public static void main(String[] args) {
		TextAnalysis ta = new TextAnalysis();

		Map<String, Long> words = null;
		try {
			words = ta.analysis("hamlet-utf8.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("WORDS = " + words);
	}

	public Map<String, Long> analysis(String filepath) throws IOException {
		
		// create stream from file
		Stream<String> lines = Files.lines(Paths.get(filepath), StandardCharsets.UTF_8);
		
		// capture time
		long start = System.currentTimeMillis();
		
		// result map
		Map<String, Long> result = null;
		
		//TODO hier bitte implementieren
		Stream<String> words = lines.flatMap(line -> Stream.of(line.split(" +"))); // Die lambda-Mapper-Funktion splittet eine Zeile in Arrays mit einzelnen Wörtern anhand des regulären Ausdrucks " +" (Leerzeichen) und erzeugt daraus wieder einen Stream 
		result = words
				.map(e -> e.replaceAll("[\\.\\!\\?\\;\\:\\,\"\\(\\)\\_]", "").toLowerCase()) // Satzzeichen entfernen; auf Kleinbuchstaben reduzieren
				.filter(e -> e.length() >= 3) // nur Wörter mit mindesten 3 Buchstaben betrachten
				.filter(e -> !e.matches(".*(\\[|\\]|(\\-\\-)|//).*")) // Wörter ausschließen, die mindestens eines der aufgeführten Zeichen enthalten
				.filter(e -> !Arrays.asList(STOP_ARRAY).contains(e)) // enthält keine Stop-Worte
				.parallel()
				.collect(Collectors.groupingByConcurrent(Function.identity(), Collectors.<String> counting())); // Wort-Häufigkeiten
		
		// close stream
		lines.close();
		
		// print time
		System.out.println("time taken: "
				+ (System.currentTimeMillis() - start));
		
		return result;
	}
}
