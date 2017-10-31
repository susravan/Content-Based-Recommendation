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
		// Elements topicList = doc.select("div#mw-content-text");
		Elements topicLinks = doc.select("div#mw-content-text").select("li").select("a");

		File file1 = new File(path + "HTMLContentFiles");
		file1.mkdir();
		File file2 = new File(path + "CrawledTextFiles");
		file2.mkdir();
		File file4 = new File(path + "CrawledCodeFiles");
		file4.mkdir();
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

				if (i > 9 && !exclusions.contains(pageName)) {
					// System.out.println("Crawling " + link.attr("href"));
					crawlText(link.attr("href"), link.text(), path);
				}
			}
		}
		System.out.println("Crawling done...");
	}

	public static void crawlText(String url, String title, String path) throws IOException {
		// To match the file n
		if (title.equals("Basic I/O"))
			title = "Basic IO";

		// if(title.matches("Reflection Overview")) {
		String currURL = wikiURL + url;
		Document htmlDoc = Jsoup.connect(currURL).get();
		String htmlContent = htmlDoc.html();
		Document doc = Jsoup.parse(htmlContent);

		Elements textHTML = doc.select("div#mw-content-text");

		PrintWriter crawlTextWriter = null;
		PrintWriter crawlCodeWriter = null;
		PrintWriter htmlContentWriter = null;
		PrintWriter linkFileWriter = null;

		int i = 0;
		for (Element para : textHTML.get(0).children().get(0).children()) {
			if (!para.text().equals("") && para.tagName().equals("p")) {
				crawlTextWriter = new PrintWriter(path + "CrawledTextFiles/" + title + "_" + i + ".txt", "UTF-8");
				crawlTextWriter.println(para.text());

				Element nextSibling = para.nextElementSibling();
				String code = "";
				if (nextSibling.tagName().equals("table") && !(code = nextSibling.select("pre").text()).equals("")) {
					crawlCodeWriter = new PrintWriter(path + "CrawledCodeFiles/" + title + "_" + i + ".txt", "UTF-8");
					crawlCodeWriter.println(code);
					crawlCodeWriter.close();
				}
				i++;
				crawlTextWriter.close();
			}
		}

		htmlContentWriter = new PrintWriter(path + "HTMLContentFiles/" + title + "_" + i + ".html", "UTF-8");
		htmlContentWriter.println(textHTML);
		linkFileWriter = new PrintWriter(path + "LinkFiles/" + title + "_" + i + ".txt", "UTF-8");

		linkFileWriter.println(currURL);

		htmlContentWriter.close();
		linkFileWriter.close();
		// }
	}
}
