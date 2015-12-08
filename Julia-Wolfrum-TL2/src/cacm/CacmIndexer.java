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
import java.io.Reader;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.IndexableFieldType;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

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
		
		Scanner reader = new Scanner(file);
		Document currentDoc = null;
		
		String currentFieldType = null;
		StringBuffer currentValue = null;
		
		// TextField: A field that is indexed and tokenized, without term vectors. For example this would be used on a 'body' field, that contains the bulk of a document's text.
		// StringField: A field that is indexed but not tokenized: the entire String value is indexed as a single token. For example this might be used for a 'country' field or an 'id' field, or any field that you intend to use for sorting or access through the field cache.
		
		while(reader.hasNextLine()){
			String line = reader.nextLine();
			line = line.trim();
			if(line.startsWith(".I")){
				finishCurrentDokument(currentDoc, currentFieldType, currentValue);
				if(null != currentDoc){
					writer.addDocument(currentDoc);
				}
				currentFieldType = null;
				currentValue = null;
				currentDoc = new Document();
				
				String value = line.split(" ")[1];
				IndexableField docId = new StringField(ID, value, Store.YES);
				currentDoc.add(docId);
			}
			else switch(line){
			
			// Title
			case ".T": {
				finishPreviousField(currentDoc, currentFieldType, currentValue);
				currentFieldType = TITLE;
				currentValue = new StringBuffer();
				break;
			} 
			// Documents
			case ".W":{
				finishPreviousField(currentDoc, currentFieldType, currentValue);
				currentFieldType = CONTENT;
				currentValue = new StringBuffer();
				break;
			}
			// Parts to Ignore
			case ".B":
			case ".A":
			case ".N":
			case ".X": {
				// Ignore those Parts, but finish the Previous Field
				finishPreviousField(currentDoc, currentFieldType, currentValue);
				currentFieldType = null;
				currentValue = null;
				break;
			}
			// Content
			default: {
				if(null == currentValue) break;
				
				// In case the Previous line did not end with an whitespace.
				// To make sure, that words are not combined by accident to a ne word.
				currentValue.append(" ");
				currentValue.append(line);
			}
			}
		}
		
		finishCurrentDokument(currentDoc, currentFieldType, currentValue);
		writer.addDocument(currentDoc);
	}

	private void finishPreviousField(Document doc, String currentFieldType,
			StringBuffer currentValue) {
		if(null == currentFieldType && null == currentValue) return;
		String value = currentValue.toString();
		
		// Reduce double whitespaces to one. That may come by the art of reading the Document
		value = value.replace("  ", " ");
		value = value.trim();
		IndexableField field = new TextField(currentFieldType, value, Store.YES);
		doc.add(field);
	}

	private void finishCurrentDokument(Document doc,
			String currentFieldType, StringBuffer currentValue) {
		finishPreviousField(doc, currentFieldType, currentValue);
	}
}
