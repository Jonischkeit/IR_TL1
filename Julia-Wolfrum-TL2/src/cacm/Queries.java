package cacm;

import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "cacm")
public class Queries {

	private final List<TestQuery> queries;

	public Queries(@ElementList(name = "parameters") List<TestQuery> queries) {
		this.queries = queries;
	}

	@ElementList(name = "parameters")
	public List<TestQuery> getQueries() {
		return queries;
	}
}

@Root(name="query")
class TestQuery {

	@Element(name = "number")
	private final String number;

	@Element(name = "text")
	private final String text;

	public TestQuery(@Element(name = "number") String number,
			@Element(name = "text") String text) {
		this.text = text.trim();
		this.number = number.trim();
	}
	
	public String getText() {
		return text;
	}
	
	public String getNumber() {
		return number;
	}
}