package index;


/**
 * 
 */

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.terrier.structures.DocumentIndex;
import org.terrier.structures.Index;
import org.terrier.structures.IndexOnDisk;
import org.terrier.structures.Lexicon;
import org.terrier.structures.LexiconEntry;
import org.terrier.structures.MetaIndex;
import org.terrier.structures.Pointer;
import org.terrier.structures.bit.DirectIndex;
import org.terrier.structures.postings.IterablePosting;


/**
 * @author ould
 *
 */


@SuppressWarnings("deprecation")
public class UserIndex {

	protected static Index  index;
	protected static Logger logger = Logger.getRootLogger();
	static int numberOfDoc = 0;
	static DirectIndex di;
	public static void main(String[] args) throws IOException {
		loadIndex();
		numberOfDoc = index.getCollectionStatistics().getNumberOfDocuments();	
		di = new DirectIndex((IndexOnDisk) index, "direct");
		getDocumentTF_IDF();

	}

	public static void getDocumentTF_IDF() throws IOException{
		FileWriter pw = new FileWriter("indexUsersBibsonomyTF-IDF.txt");
		pw.write("docno" + " " + "terme" + " " + "tf_terme" +":"+"idf"+ "\n" );
		DocumentIndex doi = index.getDocumentIndex();
		Lexicon<String> lex = index.getLexicon();
		MetaIndex meta = index.getMetaIndex();

		System.out.println(numberOfDoc);
		for (int i = 0; i < numberOfDoc; i++) {
			String docno = meta.getItem("docno", i);
			IterablePosting postings = di.getPostings((Pointer)doi.getDocumentEntry(i));
			String all_terme = "";
			while (postings.next() != IterablePosting.EOL) {
				Map.Entry<String,LexiconEntry> lee = lex.getLexiconEntry(postings.getId());
				LexiconEntry le = lex.getLexiconEntry(lee.getKey());
				System.out.println();
				all_terme += lee.getKey() + ":" + postings.getFrequency()+ ":"+ Math.log(numberOfDoc/le.getDocumentFrequency())+ " ";
			}
			pw.write(docno + " " + postings.getDocumentLength() + " " + all_terme + "\n" );
			pw.flush();
		}
		pw.close();
	}


	protected static void loadIndex(){
		long startLoading = System.currentTimeMillis();
		index = IndexOnDisk.createIndex("z:/experimentations/bibsonomy/bibsonomy/index/index_users/terrier-4.0/var/index/", "data");
		if(index == null)
		{
			logger.fatal("Failed to load index. Perhaps index files are missing");
		}
		long endLoading = System.currentTimeMillis();
		if (logger.isInfoEnabled())
			logger.info("time to intialise index : " + ((endLoading-startLoading)/1000.0D));
	}




}
