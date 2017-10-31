package lucene;
import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;

import lucene.SimpleLuceneIndexing;

public class MainClass {
	public static void main(String args[]) throws IOException, ParseException, InvalidTokenOffsetsException {
		String dataFilePath = "/home/sravan/Desktop/Sem-3/Adaptive-Web/Programming-Assignments/Assignment3/data/";
		
//		WikiBookCrawling.crawl(dataFilePath);
		
		SimpleLuceneIndexing.mainCall(0, dataFilePath);
	}
}
