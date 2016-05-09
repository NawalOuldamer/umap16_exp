/**
 * 
 */
package umap2016;
/**
 * 
 */
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

/**
 * @author ould
 *
 */
public class NotFilteredCollaborativeUsersScores {

	/**
	 * @param args
	 *    
	 *    cette classe fusionne les core de tout les users sans expertise si mimilarité
	 * @throws IOException 
	 */


	static org.jdom2.Document document;
	static Element racine;
	public static boolean ASC = true;
	public static boolean DESC = false;


	public static void main(String[] args) throws IOException {

		String topic_file = args[0];
		SAXBuilder sxb = new SAXBuilder();
		try
		{
			document = sxb.build(new File(topic_file));
		}
		catch(Exception e){}
		racine = document.getRootElement();
		List<Element> listQuery = racine.getChildren("TOP");
		Iterator<Element> i = listQuery.iterator();

		while(i.hasNext())		
		{
			Element courant = i.next();					
			System.out.println("query : "+courant.getChild("NUM").getText());
			/* liste des top k document retourné par le rsv(Q,D) pour faire le re-ranking*/

			Scanner s_doc = new Scanner(new File ("./collaborative_part/rsv_query_docs_tags.res"));
			int top_k = 0;
			ArrayList<String> liste_top_k_doc = new ArrayList<String>(); 
			boolean b = false; // compter jusqu'a 100 et sortir de la boucle 
			while (s_doc.hasNextLine() && b == false) {
				String line = s_doc.nextLine();			
				/* chaque ligne est de la forme : d_q  Q0 id_doc rank score_doc model */
				String [] vect = line.split(" ");
				if(vect[0].equals(courant.getChild("NUM").getText()) && top_k < 100){ // prendre juste les document de la requete corante 
					liste_top_k_doc.add(vect[2]);	
					top_k ++;
				}
				else {
					if (top_k >=99) b = true;			
				}
			}
			s_doc.close();
			


			/* fusion score w-uj * rsv( uj,d)*/
			/* lefichier a la frome suivante : d_user  Q0 id_doc rank score_doc model */
			/* map contient le doc et son score */
			HashMap<String, Double> map_doc_score = new HashMap<String, Double>();
			Scanner s_user = new Scanner(new File ("./collaborative_part/non_filtred/rsv_user_docs_tags.res"));	
			while (s_user.hasNextLine()) {
				String line = s_user.nextLine();
				String [] vect = line.split(" ");
				if(liste_top_k_doc.contains(vect[2])){
					if(map_doc_score.containsKey(vect[2])){
						/* Double.parseDouble((vect[4])) * map_user_score_expertise.get(vect[0]) pour chaque user et on fais la somme sur tout les users*/
						map_doc_score.put(vect[2], (map_doc_score.get(vect[2]) + Double.parseDouble((vect[4])))/2); // faire la moyenne
					}
					else {
						map_doc_score.put(vect[2], Double.parseDouble((vect[4])));
					}
				}
			}
			s_user.close();

			FileWriter fichier_resultat = new FileWriter("./collaborative_part/profile_not_filtred/not_weighted/rsv_collaboratif_prfile_not_filtered"+courant.getChild("NUM").getText()+".res");
			int rank = 0;
			for(Entry<String, Double> entry : sortByComparator(map_doc_score, DESC).entrySet()) {
				if(entry.getValue() != 0.0){
					fichier_resultat.write(courant.getChild("NUM").getText()+" "+courant.getChild("USER").getText()+" "+entry.getKey()+" "+rank+" "+entry.getValue()+" "+"Collaboratif_part"+"\n");
					rank++;
				}
			}
			fichier_resultat.close();			
		}



	}

	private static Map<String, Double> sortByComparator(Map<String, Double> unsortMap, final boolean order)
	{
		List<Entry<String, Double>> list = new LinkedList<Entry<String, Double>>(unsortMap.entrySet());
		Collections.sort(list, new Comparator<Entry<String, Double>>()
		{
			public int compare(Entry<String, Double> o1,Entry<String, Double> o2)
			{if (order)
			{return o1.getValue().compareTo(o2.getValue());}
			else
			{return o2.getValue().compareTo(o1.getValue());
			}
			}
		});
		Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
		for (Entry<String, Double> entry : list)
		{
			sortedMap.put(entry.getKey(), entry.getValue());
			// System.out.println(entry.getKey() + "   " +entry.getValue());
		}

		return sortedMap;
	}



}
