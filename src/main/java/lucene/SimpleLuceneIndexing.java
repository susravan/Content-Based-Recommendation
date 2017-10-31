package lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.standard.ClassicFilter;
import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

/**
 * @author ShSudha Sravan Samudrala
 */

public class SimpleLuceneIndexing {

	private static void indexDirectory(IndexWriter writer, File dir) throws IOException {
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isDirectory()) {
				indexDirectory(writer, f); // recurse
			} else if (f.getName().endsWith(".txt")) {
				// call indexFile to add the title of the txt file to your index (you can also
				// index html)
				indexFile(writer, f);
			}
		}
	}

	private static void indexFile(IndexWriter writer, File f) throws IOException {
		// System.out.println("Indexing " + f.getName());
		Document doc = new Document();
		doc.add(new TextField("filename", f.getName(), TextField.Store.YES));

		// open each file to index the content
		try {

			FileInputStream is = new FileInputStream(f);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			StringBuffer stringBuffer = new StringBuffer();
			String line = null;
			while ((line = reader.readLine()) != null) {
				stringBuffer.append(line).append("\n");
			}
			reader.close();
			doc.add(new TextField("contents", stringBuffer.toString(), TextField.Store.YES));

		} catch (Exception e) {

			System.out.println("something wrong with indexing content of the files");
		}
		writer.addDocument(doc);
	}

	public static String[] mainCall(int index, String path)
			throws IOException, ParseException, InvalidTokenOffsetsException {

		System.out.println("Starting Lucene..");
		String answerArray[] = new String[10];
		String type[] = { "answer", "question", "answer accepted-answer", "answer", "question", "question", "answer",
				"answer", "answer", "answer accepted-answer" };
		String text[] = new String[10];
		text[0] = "I was presented with this question in an end of module open book exam today and found myself lost. i was reading Head first Javaand both definitions seemed to be exactly the same. i was just wondering what the MAIN difference was for my own piece of mind. i know there are a number of similar questions to this but, none i have seen which provide a definitive answer.Thanks, Darren";
		text[1] = "Inheritance is when a 'class' derives from an existing 'class'.So if you have a Person class, then you have a Student class that extends Person, Student inherits all the things that Person has.There are some details around the access modifiers you put on the fields/methods in Person, but that's the basic idea.For example, if you have a private field on Person, Student won't see it because its private, and private fields are not visible to subclasses.Polymorphism deals with how the program decides which methods it should use, depending on what type of thing it has.If you have a Person, which has a read method, and you have a Student which extends Person, which has its own implementation of read, which method gets called is determined for you by the runtime, depending if you have a Person or a Student.It gets a bit tricky, but if you do something likePerson p = new Student();p.read();the read method on Student gets called.Thats the polymorphism in action.You can do that assignment because a Student is a Person, but the runtime is smart enough to know that the actual type of p is Student.Note that details differ among languages.You can do inheritance in javascript for example, but its completely different than the way it works in Java.";
		text[2] = "Polymorphism: The ability to treat objects of different types in a similar manner.Example: Giraffe and Crocodile are both Animals, and animals can Move.If you have an instance of an Animal then you can call Move without knowing or caring what type of animal it is.Inheritance: This is one way of achieving both Polymorphism and code reuse at the same time.Other forms of polymorphism:There are other way of achieving polymorphism, such as interfaces, which provide only polymorphism but no code reuse (sometimes the code is quite different, such as Move for a Snake would be quite different from Move for a Dog, in which case an Interface would be the better polymorphic choice in this case.In other dynamic languages polymorphism can be achieved with Duck Typing, which is the classes don't even need to share the same base class or interface, they just need a method with the same name.Or even more dynamic like Javascript, you don't even need classes at all, just an object with the same method name can be used polymorphically.";
		text[3] = "I found out that the above piece of code is perfectly legal in Java. I have the following questions. ThanksAdded one more question regarding Abstract method classes.";
		text[4] = "In java it's a bit difficult to implement a deep object copy function. What steps you take to ensure the original object and the cloned one share no reference? ";
		text[5] = "You can make a deep copy serialization without creating some files. Copy: Restore:";
		text[6] = "Java has the ability to create classes at runtime. These classes are known as Synthetic Classes or Dynamic Proxies. See for more information. Other open-source libraries, such as and also allow you to generate synthetic classes, and are more powerful than the libraries provided with the JRE. Synthetic classes are used by AOP (Aspect Oriented Programming) libraries such as Spring AOP and AspectJ, as well as ORM libraries such as Hibernate. ";
		text[7] = "A safe way is to serialize the object, then deserialize.This ensures everything is a brand new reference.about how to do this efficiently. Caveats: It's possible for classes to override serialization such that new instances are created, e.g. for singletons.Also this of course doesn't work if your classes aren't Serializable.";
		text[8] = " comment this code       ";
		text[9] = "     in a class i can have as many constructors as i want with different argument types. i made all the constructors as private it didn't give any error because my implicit default constructor was public but when i declared my implicit default constructor as private then its showing an error while extending the class.  why?       this works fine         this can not be inherited       ";

		String readPath = path + "CrawledTextFiles";

		File dataDir = new File(readPath); // my sample file folder path
		// Check whether the directory to be indexed exists
		if (!dataDir.exists() || !dataDir.isDirectory()) {
			throw new IOException(dataDir + " does not exist or is not a directory");
		}
		Directory indexDir = new RAMDirectory();

		// Specify the analyzer for tokenizing text
		StandardAnalyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig();
		IndexWriter writer = new IndexWriter(indexDir, config);

		indexDirectory(writer, dataDir);
		writer.close();
		System.out.println("Getting tokens for the selected post");

		// Query string - Modified to get the tokens from the processed text
		List<Keyword> postKeywords = tokenStream(text[index]);
		StringBuilder queryString = new StringBuilder("contents:");

		for (int i = 0; i < 10; i++) {
			Keyword word = postKeywords.get(i);
			Set<String> terms = new HashSet<String>();
			terms = word.getTerms();
			for (String s : terms) {
				queryString.append(s).append(" ");
			}
		}
		System.out.println("Query String: " + queryString.toString());

		Query query = new QueryParser("contents", analyzer).parse(queryString.toString());
		int hitsPerPage = 10;
		IndexReader reader = null;
		TopScoreDocCollector collector = null;
		IndexSearcher searcher = null;

		reader = DirectoryReader.open(indexDir);
		searcher = new IndexSearcher(reader);

		TopDocs hits = searcher.search(query, hitsPerPage);

		// Highlighter code
		Formatter formatter = new SimpleHTMLFormatter();
		QueryScorer scorer = new QueryScorer(query);
		Highlighter highlighter = new Highlighter(formatter, scorer);
		Fragmenter fragmenter = new SimpleSpanFragmenter(scorer, 10);
		highlighter.setTextFragmenter(fragmenter);

		// Iterate over found results
		for (int i = 0; i < hits.scoreDocs.length; i++) {
			int docid = hits.scoreDocs[i].doc;
			Document doc = searcher.doc(docid);
			String title = doc.get("filename");

			// Printing - to which document result belongs
			System.out.println("Path:" + title);

			// Get stored text from found document
			String docText = doc.get("contents");

			// Create token stream
			TokenStream stream = TokenSources.getAnyTokenStream(reader, docid, "contents", analyzer);

			// Get highlighted text fragments
			String[] frags = highlighter.getBestFragments(stream, docText, 50);
			for (String frag : frags) {
				System.out.println("=======================");
				System.out.println(frag);

				File codeFile = new File(
						"/home/sravan/Desktop/Sem-3/Adaptive-Web/Programming-Assignments/Assignment3/data/CrawledCodeFiles/"
								+ title);
				if (codeFile.exists() && !codeFile.isDirectory()) {
					FileReader fr = new FileReader(codeFile);
					BufferedReader br = new BufferedReader(fr);
					StringBuilder sb = new StringBuilder();
					String temp = "";

					while ((temp = br.readLine()) != null) {
						sb.append(temp).append("\n");
					}
					br.close();
					System.out.println(sb.toString());
				}
			}
		}

		reader.close();
		return answerArray;
	}

	// Inspired from :
	// http://stackoverflow.com/questions/17447045/java-library-for-keywords-extraction-from-input-text
	public static List<Keyword> tokenStream(String text) throws IOException {
		TokenStream tok = null;

		// Remove the punctuation marks if any
		text = text.replaceAll("-+", "-0");
		text = text.replaceAll("[\\p{Punct}&&[^'-]]+", " ");
		text = text.replaceAll("(?:'(?:[tdsm]|[vr]e|ll))+\\b", "");

		// Tokenize, toLowerCase, normalize, convert to ASCII and remove stop words
		ClassicTokenizer tokClassic = new ClassicTokenizer();
		tokClassic.setReader(new StringReader(text));
		tok = new LowerCaseFilter(tokClassic);
		tok = new ClassicFilter(tok);
		tok = new ASCIIFoldingFilter(tok);

		CharArraySet stopSet = CharArraySet.copy(StandardAnalyzer.STOP_WORDS_SET);
		stopSet.add("you");
		stopSet.add("i");
		stopSet.add("can");
		stopSet.add("end");
		stopSet.add("code");
		stopSet.add("book");
		stopSet.add("move");
		stopSet.add("open");
		stopSet.add("type");
		stopSet.add("found");
		stopSet.add("other");
		stopSet.add("lost");
		stopSet.add("question");
		stopSet.add("first");

		tok = new StopFilter(tok, stopSet);

		List<Keyword> keywords = new LinkedList<Keyword>();
		CharTermAttribute token = tok.getAttribute(CharTermAttribute.class);
		tok.reset();

		while (tok.incrementToken()) {
			String term = token.toString();
			String stem = stem(term);

			if (stem != null) {
				// create the keyword or get the existing one if any
				Keyword keyword = find(keywords, new Keyword(stem.replaceAll("-0", "-")));
				// add its corresponding initial token
				keyword.add(term.replaceAll("-0", "-"));
			}
		}

		Collections.sort(keywords);
		tok.close();
		return keywords;

	}

	// Inspired from :
	// http://stackoverflow.com/questions/17447045/java-library-for-keywords-extraction-from-input-text
	public static String stem(String term) throws IOException {

		TokenStream tokenStream = null;
		try {

			ClassicTokenizer tokClassic = new ClassicTokenizer();
			tokClassic.setReader(new StringReader(term));
			tokenStream = new PorterStemFilter(tokClassic);

			// Add each token in a set, so that duplicates are removed
			Set<String> stems = new HashSet<String>();
			CharTermAttribute token = tokenStream.getAttribute(CharTermAttribute.class);
			tokenStream.reset();
			while (tokenStream.incrementToken()) {
				stems.add(token.toString());
			}

			// if no stem or 2+ stems have been found, return null
			if (stems.size() != 1) {
				return null;
			}

			String stem = stems.iterator().next();

			// if the stem has non-alphanumerical chars, return null
			if (!stem.matches("[a-zA-Z0-9-]+")) {
				return null;
			}
			return stem;
		} finally {
			if (tokenStream != null) {
				tokenStream.close();
			}
		}

	}

	// Inspired from :
	// http://stackoverflow.com/questions/17447045/java-library-for-keywords-extraction-from-input-text
	public static <T> T find(Collection<T> collection, T example) {
		for (T element : collection) {
			if (element.equals(example)) {
				return element;
			}
		}
		collection.add(example);
		return example;
	}

}