package lucene;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WikiBookCrawling {

	private static String wikiURL = "https://en.wikibooks.org";
	private static String baseURL = wikiURL + "/wiki/Java_Programming";
	
	public static void crawl(String path) throws IOException {

		Document htmlDoc = Jsoup.connect(baseURL).get();

		// Gets all hyper-links in the topic list
		Document doc = Jsoup.parse(htmlDoc.html());
//		Elements topicList = doc.select("div#mw-content-text");
		Elements topicLinks = doc.select("div#mw-content-text").select("li").select("a");

		File file1 = new File(path + "HTMLContentFiles");
		file1.mkdir();
		File file2 = new File(path + "CrawledTextFiles");
		file2.mkdir();
		File file3 = new File(path + "LinkFiles");
		file3.mkdir();
		
		ArrayList<String> exclusions = new ArrayList<String>();
		exclusions.add("Links");
		exclusions.add("Glossary");
		exclusions.add("Index");
		
		int i = 0;
		for (Element link : topicLinks) {
			i++;
			// Valid HTML element
			if (link.hasText()) {
				// First 6 topics are not related to JAVA
				String pageName = link.attr("href").split("/")[3];
//				System.out.println(pageName);
				
				if (i > 9 && !exclusions.contains(pageName)) {
					System.out.println("Crawling " + link.attr("href"));
					crawlText(link.attr("href"), link.text(), path);
				}
			}
		}
		System.out.println("Crawling done...");
	}
 
	public static void crawlText(String url, String title, String path) throws IOException {
		// To match the file n
		if(title.equals("Basic I/O"))
			title = "Basic IO";
		
		String currURL = wikiURL + url;
		Document htmlDoc = Jsoup.connect(currURL).get();
		String htmlContent = htmlDoc.html();
		Document doc = Jsoup.parse(htmlContent);
		Elements topicList = doc.select("div#mw-content-text");

		PrintWriter crawlTextWriter = new PrintWriter(path + "CrawledTextFiles/" + title + ".txt", "UTF-8");
		PrintWriter htmlContentWriter = new PrintWriter(path + "HTMLContentFiles/" + title + ".html", "UTF-8");
		PrintWriter linkFileWriter = new PrintWriter(path + "LinkFiles/" + title + ".txt", "UTF-8");

		crawlTextWriter.println(topicList.text());
		crawlTextWriter.close();

		htmlContentWriter.println(htmlContent);
		htmlContentWriter.close();

		linkFileWriter.println(currURL);
		linkFileWriter.close();
	}
}
