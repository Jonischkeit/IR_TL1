package cacm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


public class CacmSearcher {

	// determines which analyzer should be used
	public static final boolean USE_STANDARD_ANALYZER = false;

	public static String path2queries = "data/cacm.query.xml";

	// main method for testing
	public static void main(String[] args) throws IllegalArgumentException,
			IOException, ParseException {

		String indexDir = null;// 1
		String outName = null;
		Analyzer analyzer = null;

		if (USE_STANDARD_ANALYZER) {
			indexDir = "idx_cacm_std";
			analyzer = new StandardAnalyzer();
			outName = "std";
		} else {// use MyStemAnalyzer
			indexDir = "idx_cacm_my";
			analyzer = new MyStemAnalyzer();
			outName = "my";
		}

		// choose the similarity you want
		Similarity[] sims = new Similarity[] {
				new LMJelinekMercerSimilarity(0.2f),
				new LMDirichletSimilarity(), new BM25Similarity(),
				new DefaultSimilarity() };

		// iterate over similarities ... and search
		for (int i = 0; i < sims.length; i++) {
			Similarity sim = sims[i];
			StringBuilder builder = search(indexDir, sim, analyzer);
			System.err.println("cacm-" + sim.toString() + "-"
					+ analyzer.toString() + ".trec");
			FileWriter fw = new FileWriter(new File("logs/cacm-" + sim.toString()
					+ "-" + outName + ".trec"));
			fw.write(builder.toString());
			fw.close();
		}
	}

	// implement the search on multiple fields (see exercise 4.3 and following)
	public static StringBuilder search(String indexDir, Similarity sim,
			Analyzer analyzer) throws IOException, ParseException {

		// read the query texts
		List<TestQuery> queryList = CacmHelper.readQueries(path2queries);
		System.out.println("#queries: " + queryList.size());

		StringBuilder builder = new StringBuilder();
		
		int LIMIT = 1000; // limit to 1000 hits
		boolean DEBUG = true; // for a debug output to the console
				
		MultiFieldQueryParser queryParser = new MultiFieldQueryParser(new String[]{"title", "content"}, analyzer);
		
		Directory directory = FSDirectory.open(new File(indexDir).toPath());
		IndexReader indexReader = DirectoryReader.open(directory);
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		indexSearcher.setSimilarity(sim);
		
		for (TestQuery testQuery : queryList) {
			Query query = queryParser.parse(testQuery.getText()); 
			TopDocs hits = indexSearcher.search(query, LIMIT); 

			int rank = 0;
			for (ScoreDoc scoreDoc : hits.scoreDocs) {
				rank++;
				Document doc = indexSearcher.doc(scoreDoc.doc); 
				String returnString = testQuery.getNumber() + " 1 " + doc.get("docid") + " " + rank + " " + scoreDoc.score + " " + sim.toString();
				builder.append(returnString+"\n");
				if (DEBUG) {
					System.out.println(returnString);
				}
			}			
		}
		
		return builder;
	}
}
