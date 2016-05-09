/**
 * 
 */
package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

/**
 * @author ould
 *
 */
public class CollaborativeRelevance {

	/**
	 * @param args
	 * @throws IOException 
	 */


	static org.jdom2.Document document;
	static Element racine;

	public static void main(String[] args) throws IOException {

		HashMap<String, String> map_query_user = new HashMap<String, String>();
		SAXBuilder sxb = new SAXBuilder();
		try
		{
			document = sxb.build(new File("topics_bibsonomy_new_200.xml"));
		}
		catch(Exception e){}
		racine = document.getRootElement();
		List<Element> listQuery = racine.getChildren("TOP");
		Iterator<Element> i = listQuery.iterator();
		while(i.hasNext())		
		{
			Element courant = i.next();
			map_query_user.put(courant.getChild("NUM").getText(), courant.getChild("USER").getText());
		}
		System.out.println(map_query_user.size());
		
		FileWriter file = new FileWriter("sim_tf_idf_users_post.txt");
		Scanner s = new Scanner(new File("sim_tf_idf_users.res"));
		System.out.println("hh");
		while (s.hasNextLine()) {
			String line = s.nextLine();
			String [] vect = line.split(" ");
			file.write(vect[0]+" "+map_query_user.get(vect[0])+" "+vect[2]+" "+vect[3]+" "+vect[4]+" "+vect[5]+"\n");
		}
		s.close();
		file.close();
		
		
		
	}
	

	/**
	 * cette classe pertmet de créer les fochier requete ou la requete est un utilisateur 
	 */
	public static void usersAsQuery(){
		
		
	}





}
