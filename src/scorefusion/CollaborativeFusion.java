/**
 * 
 */
package scorefusion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

/**
 * @author ould
 *
 */
public class CollaborativeFusion {

	/**
	 * @param args
	 * @throws IOException 
	 */


	public static boolean ASC = true;
	public static boolean DESC = false;
	public static void main(String[] args) throws IOException {

		int nbr_topic = 0;
		int debut = 0;
		int fin = 1; //Integer.parseInt(args[2]);
		FileWriter file = new FileWriter("/collaborative_score"+"_topic_"+nbr_topic+"_query_"+debut+".txt");
		for (int i = debut; i < fin; i++) {
			if(i!=4){
				String [] vect =null;

				System.out.println("User expertise query : "+ i);
				///////////////////////             map user scoreExpertise suivant la requete ///////////////////// 
				HashMap<String, Double> map_user_score = new HashMap<String, Double>();
				Scanner s = new Scanner(new File("expertise_user_"+nbr_topic+"_query"+i+".txt"));
				boolean b = false;
				while (s.hasNextLine() && b ==false) {
					String line =  s.nextLine();
					if(!line.startsWith("#")){
						String [] vect1 = line.split(" ");
						if(Double.valueOf(vect1[3])!= 0.0){
							map_user_score.put(vect1[2], Double.valueOf(vect1[3]));
						}
						else {
							b =true;
						}
					}			
				}
				s.close();
				System.out.println("liste user :  " + map_user_score.size());
				
				/////////////////////////////////////////// //////////////////////////////////////////////////////////////
				System.out.println("Top 100 doc");
				ArrayList<String> liste_top_doc = new ArrayList<String>();
				Scanner sc= new Scanner(new File("DirichletLM_0.res")); // rsv(u,doc;tag)
				int count = 0;
				while (sc.hasNextLine()&& count<100) {					
					String line = sc.nextLine();
					vect = line.split(" ");	
					if(Integer.parseInt(vect[0])== i){
						liste_top_doc.add(vect[2]); //top 100 doc
						count++;
					}			
				}
				sc.close();	
				
				

				////////////////////// calcul score weight_user * rsv (user, doc,tag))
				System.out.println("score compute");
				HashMap<String, Double> map_doc_socre = new HashMap<String, Double>();
				for (int j = 0; j < liste_top_doc.size(); j++) {
					map_doc_socre.put(vect[2], UsersScore(vect[0], vect[1], vect[2], map_user_score, 0));	
				}				
				
				int rank = 0;
				System.out.println("file writer");
				for(Entry<String, Double> entry : sortByComparator(map_doc_socre, DESC).entrySet()) {
					if(entry.getValue() != 0.0){
						file.write(vect[0]+" "+vect[1]+" "+entry.getKey()+" "+ rank+" "+entry.getValue()+" "+vect[5]+"\n");
						rank++;
					}
				}	
				file.close();
			}

		}

	}

	public static Double UsersScore(String query_id, String user_id, String doc_id, HashMap<String, Double> map_user_score, int nbr_topic) throws FileNotFoundException{

		//////

		Scanner s1 = new Scanner(new File("user_profile_filtered_number_of_topic_"+nbr_topic+"_query_"+query_id+".xml"));
		Double score_doc = 0.0;
		while (s1.hasNextLine()) {
			String line =  s1.nextLine();
			String [] vect = line.split(" ");	
			if(map_user_score.containsKey(vect[0]) && vect[2].equals(doc_id)){
				score_doc += Double.valueOf(map_user_score.get(vect[0])) * Double.valueOf(vect[4]);			 /// somme sur tout les users 	
			}
		}
		s1.close();		
		///////		

		return score_doc;
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
