package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

public class DocumentFunctions {


	static org.jdom2.Document document;
	static Element racine;

	public static void main(String[] args) throws IOException {
		getDocQuery();
	}
	
	
	/**
	 * cette méthode permet de trouver tout les document taggé avec le le terme de la requete
	 * @throws IOException
	 */
	public static void getDocQuery() throws IOException{

		FileWriter file = new FileWriter("query_doc_delicous_2006");
		StemmingTerm porterStemmer = new StemmingTerm();
		ArrayList<String> liste_user = new ArrayList<String>();
		BufferedReader users  = new BufferedReader(new FileReader("./files_exp_del_2006/id_users.txt"));
		String line = "";	
		while ((line = users.readLine()) != null) {	
			liste_user.add(line);			
		}
		users.close();

		SAXBuilder sxb = new SAXBuilder();
		try
		{
			document = sxb.build(new File("./files_exp_del_2006/topics_delicious_2006.xml"));
		}
		catch(Exception e){}
		racine = document.getRootElement();
		List<Element> listQuery = racine.getChildren("TOP");
		Iterator<Element> i = listQuery.iterator();
		while(i.hasNext())		
		{
			Element courant = i.next();
			BufferedReader user  = new BufferedReader(new FileReader("y:/social_network/Delicious_2006/users_files_rename/"+courant.getChild("USER").getText()+".txt"));
			String line1 = "";
			file.write(courant.getChild("NUM").getText()+" "+courant.getChild("TITLE").getText()+ " ");
			String query = porterStemmer.stripAffixes(courant.getChild("TITLE").getText());
			while ((line1 = user.readLine()) != null) {	
				String [] vect = line1.split("\t");
				if(vect.length==5){
					//System.out.println(line1);
					if(porterStemmer.stripAffixes(vect[4]).equals(query)){
						file.write(vect[0] +" " );
						file.flush();
					}	
				}	
			}
			user.close();
			file.write("\n");
		}
		
		file.close();
	}
	
	public static void getTopicQuery(){
		
	}
	
}
