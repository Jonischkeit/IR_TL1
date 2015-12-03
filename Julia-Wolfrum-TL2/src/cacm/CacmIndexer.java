package cacm;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

//Indexing
public class CacmIndexer {

	// FIELD NAMES:
	// internal ID (.I)
	public static final String ID = "docid";
	// title of the entry (.T)
	public static final String TITLE = "title";
	// abstract/content (.W)
	public static final String CONTENT = "content";

	// analyzer
	public Analyzer analyzer = null;

	// index writer
	public IndexWriter writer;

	// determines which analyzer should be used
	public static final boolean USE_STANDARD_ANALYZER = true;

	// constructor
	public CacmIndexer(String indexDir, Analyzer analyzer) throws IOException {
		Directory dir = FSDirectory.open(new File(indexDir).toPath());

		this.analyzer = analyzer;

		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

		writer = new IndexWriter(dir, iwc); // 3 modified
	}

	// main method for testing
	public static void main(String[] args) throws Exception {

		String indexDir = null;// 1
		String dataDir = "data/cacm.all"; // 2

		Analyzer analyzer = null;

		if (USE_STANDARD_ANALYZER) {
			indexDir = "idx_cacm_std";
			analyzer = new StandardAnalyzer();
		} else {// use MyStemAnalyzer
			indexDir = "idx_cacm_my";
			analyzer = new MyStemAnalyzer();
		}

		long start = System.currentTimeMillis();
		CacmIndexer indexer = new CacmIndexer(indexDir, analyzer);
		int numIndexed;
		try {
			numIndexed = indexer.index(dataDir);
		} finally {
			indexer.close();
		}
		long end = System.currentTimeMillis();

		System.out.println("Indexing " + numIndexed + " files took "
				+ (end - start) + " milliseconds");
	}

	// as before, nothing new :-)
	public int index(String dataDir) throws Exception {

		File f = new File(dataDir);
		indexFile(f);

		return writer.numDocs(); // 5
	}

	// as before, nothing new :-)
	public void close() throws IOException {
		writer.close(); // 4
	}

	// Do the indexing! (see exercise 4.1)
	public void indexFile(File file) throws Exception {

		System.out.println("Indexing " + file.getCanonicalPath());

		// TODO: hier bitte implementieren! Datei einlesen und einzelne
		// Dokumente mit den entsprechenden Feldinformationen extrahieren.

	}
}
