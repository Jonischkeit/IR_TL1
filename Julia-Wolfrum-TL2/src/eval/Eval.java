package eval;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class TRECFileFilter implements FilenameFilter {
	public boolean accept(File dir, String name) {
		String lowercaseName = name.toLowerCase();
		if (lowercaseName.endsWith(".trec")) {
			return true;
		} else {
			return false;
		}
	}
}

public class Eval {

	/**
	 * Main-Methode zum Berechnen der Evaluationskennzahl.
	 * 
	 * @param args nicht noetig.
	 */
	public static void main(String[] args) {

		// key: Anfrage; value: Menge der relevanten DokumentIDs
		Map<String, Set<String>> groundtruth = readGroundtruth("data/cacm-new-63.qrel");

		String[] filenames = new File("./logs").list(new TRECFileFilter());
		for (String filename : filenames) {
			// System.out.println(filename);
			double map = evaluateMAP("./logs/" + filename, groundtruth);
			System.out.println(filename + "\t MAP=" + Math.round(map * 1000.0)
					/ 1000.0);
		}
	}

	/*
	 * Berechnet MAP.
	 */	
	protected static double evaluateMAP(String filename,
			Map<String, Set<String>> groundtruth) {
		
		// TODO Hier bitte implementieren und korrekten Wert zurueckgeben.
		
		// https://www.eecis.udel.edu/~hfang/lucene/Lucene_exp.pdf

		List<String> lines = null;
		try {
			lines = Files.readAllLines(new File(filename).toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Map<String, RecallPrecision> precisionForQueryID = new HashMap<String, RecallPrecision>();

		for (String line : lines) {
			String[] parts = line.split(" ");
			if (parts.length < 5) {
				throw new RuntimeException("Fehler" + parts.length);
			}
			
			String queryID = parts[0];
			String documentID = parts[2];
						
			if (!precisionForQueryID.containsKey(queryID)) {
				precisionForQueryID.put(queryID, new RecallPrecision());
			} 
			
			Set<String> relevantDocumentsForQueryID = groundtruth.get(queryID);
			if (relevantDocumentsForQueryID != null) {
				boolean isRelevant = relevantDocumentsForQueryID.contains(documentID);
				precisionForQueryID.get(queryID).addDocument(isRelevant);
			} 
		}
		
	 
		Set<String> allQueryIDs = groundtruth.keySet();
		double MAP = 0.0;
		for (String queryID : allQueryIDs ) {
			MAP += precisionForQueryID.get(queryID).calculatePrecision();
		}
		 
		return MAP/allQueryIDs.size();
	}

	/*
	 * Liefert die Relevanzurteile: key: Anfrage ID; value: Menge mit relevanten
	 * Dokument IDs.
	 */
	private static Map<String, Set<String>> readGroundtruth(String filename) {
		Map<String, Set<String>> groundtruth = new HashMap<String, Set<String>>();
		String oldQueryId = "1";
		Set<String> relIdsPerQuery = new HashSet<String>();

		List<String> lines = null;
		try {
			lines = Files.readAllLines(new File(filename).toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (String line : lines) {
			String[] parts = line.split(" ");
			if (parts.length != 4)
				throw new RuntimeException("Fehler" + parts.length);

			String queryId = parts[0];
			String docId = parts[2];

			if (!queryId.equals(oldQueryId)) {
				groundtruth.put(oldQueryId, relIdsPerQuery);
				relIdsPerQuery = new HashSet<String>();
				oldQueryId = queryId;
			}
			relIdsPerQuery.add(docId);
		}
		groundtruth.put(oldQueryId, relIdsPerQuery);
		return groundtruth;
	}

}


// helper class for recall and precision
class RecallPrecision { 
	  private int numberOfReturnedRelevantDocument = 0; 
	  private int numberOfReturnedDocument = 0; 
	  
	  void addDocument(boolean isRelevant) {
		  numberOfReturnedDocument += 1;
		  if (isRelevant) {
			  numberOfReturnedRelevantDocument += 1;
		  }
	  }
	  
	  double calculatePrecision() {
		  try {
			  return (double)numberOfReturnedRelevantDocument / (double)numberOfReturnedDocument;
		  } catch (ArithmeticException e) {
			  System.out.println("exception");
			  return (double)0.0;
		  }
	  }
	  
	  double calculateRecall() {
		  try {
			  return (double)numberOfReturnedDocument / (double)numberOfReturnedRelevantDocument;
		  } catch (ArithmeticException e) {
			  return (double)0.0;
		  }
	  }
} 
