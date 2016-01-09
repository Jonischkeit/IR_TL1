package cacm;

import java.io.File;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Scanner;
import java.util.regex.MatchResult;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.IndexableFieldType;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

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
	public static final boolean USE_STANDARD_ANALYZER = false;

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

		Scanner scanner = new Scanner(file);		
		String id = null;
		String title = "";
		String content = "";
		
		String currentKey = ".I";

		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.startsWith(".")) { // is a key
				String key = line.substring(0, 2);
				switch (key) {
				case ".I":
					if (id != null) {
						Document document = new Document();
						Reader stringReaderTitle = new StringReader(title.trim());
						Reader stringReaderContent = new StringReader(content.trim());
						document.add(new StringField(ID, id.trim(), Field.Store.YES));
						document.add(new TextField(TITLE, stringReaderTitle)); 
						document.add(new TextField(CONTENT, stringReaderContent));
						writer.addDocument(document);
						id = null;
					}
					id = line.substring(3);
					break;
					
				case ".T":
					currentKey = ".T";
					title = ""; // reset
					break;
					
				case ".W":
					currentKey = ".W";
					content = "";  // reset
					break;
					
				default:
					currentKey = key;
					break;
				}
			} else { // is not a key, but further lines
				switch (currentKey) {
				case ".T":
					title += line;
					break;
					
				case ".W":
					content += line;
					break;
				}
			}
		} // done with the file

		scanner.close();
	}
}
