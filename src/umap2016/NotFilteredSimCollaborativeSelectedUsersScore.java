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
public class NotFilteredSimCollaborativeSelectedUsersScore {

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

		ArrayList<String> liste_users_selected = new ArrayList<String>();
		Scanner s_u = new Scanner(new File("id_users_over_500_bookmarks"));
		while (s_u.hasNextLine()) {
			liste_users_selected.add(s_u.nextLine());			
		}
		s_u.close();
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



			/* select les scores similarite entre users */
			HashMap<String, Double> map_user_score_similarity = new HashMap<String, Double>();
			Scanner s_expertise = new Scanner(new File ("./collaborative_part/users_similarity.res"));
			while (s_expertise.hasNextLine()) {
				String line = s_expertise.nextLine();
				if (!line.startsWith("#")) {
					String [] vect = line.split(" ");
					if(vect[0].equals(courant.getChild("USER").getText()) && liste_users_selected.contains(vect[2])){ // on cherche les users pour le user de la requete courante						
						map_user_score_similarity.put(vect[2], Double.parseDouble(vect[4]));						
					}
				}
			}
			s_expertise.close();




			/*
			 * normalsiation des scores expertises des users, donc on dévise par le score max 
			 * 
			 */

			Map<String, Double> map = new HashMap<String, Double>();
			map = sortByComparator(map_user_score_similarity, DESC);
			double max = map.get(map.keySet().toArray()[0]);

			// on re enregister les nv score normalisé en divisant par le max 
			for(Entry<String, Double> entry : map.entrySet()) {
				map_user_score_similarity.put(entry.getKey(), entry.getValue()/max);
			}


			/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			/*
			 * les scores des users par rapport au ducument doivent etre normalisé par divison sur le max de ses scores
			 * on ne prends que les users qui ont un score d'expertise pour faciliter les calculs
			 * 
			 * on sauvegarde d'abord les score max pour chaque user 
			 */

			HashMap<String, Double> map_user_score_document_max = new HashMap<String, Double>();
			for(Entry<String, Double> entry : map_user_score_similarity.entrySet()) {
				Scanner s_user = new Scanner(new File ("./collaborative_part/non_filtred/rsv_user_docs_tags.res"));
				boolean t = false;
				while (s_user.hasNextLine() && t == false) {
					String line = s_user.nextLine();
					String [] vect = line.split(" ");
					if(vect[0].equals(entry.getKey()) ){
						map_user_score_document_max.put(entry.getKey(), Double.parseDouble(vect[4]));
						t = true;;
					}
				}
				s_user.close();
			}


			/* fusion score w-uj * rsv( uj,d)*/
			/* lefichier a la frome suivante : d_user  Q0 id_doc rank score_doc model */
			/* map contient le doc et son score */
			HashMap<String, Double> map_doc_score = new HashMap<String, Double>();
			Scanner s_user = new Scanner(new File ("./collaborative_part/non_filtred/rsv_user_docs_tags.res"));	
			while (s_user.hasNextLine()) {
				String line = s_user.nextLine();
				String [] vect = line.split(" ");
				if(liste_top_k_doc.contains(vect[2]) && map_user_score_similarity.containsKey(vect[0])){
					if(map_doc_score.containsKey(vect[2])){
						/* Double.parseDouble((vect[4])) * map_user_score_expertise.get(vect[0]) pour chaque user et on fais la somme sur tout les users*/
						map_doc_score.put(vect[2], (map_doc_score.get(vect[2]) +
								((Double.parseDouble((vect[4]))/map_user_score_document_max.get(vect[0]))
								* map_user_score_similarity.get(vect[0])))/2); // faire la moyenne
					}
					else {
						map_doc_score.put(vect[2], ((Double.parseDouble((vect[4]))/map_user_score_document_max.get(vect[0]))
								* map_user_score_similarity.get(vect[0]))); 
					}
				}
			}
			s_user.close();

			FileWriter fichier_resultat = new FileWriter("./collaborative_part/profile_not_filtred/similarity_weighted_users_selected/rsv_collaboratif_prfile_not_filtered"+courant.getChild("NUM").getText()+".res");
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
