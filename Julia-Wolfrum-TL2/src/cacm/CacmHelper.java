package cacm;

import java.io.File;
import java.util.List;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class CacmHelper {

	public static List<TestQuery> readQueries(String path2queries) {
		Serializer serializer = new Persister();
		
		try {
			Queries testQuery = serializer.read(Queries.class, new File(path2queries));
			return testQuery.getQueries();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		List<TestQuery> queryList = readQueries("data/cacm.query.xml");
		for (TestQuery testQuery : queryList) {
			System.out.println(testQuery.getText());
		}
	}
	
}