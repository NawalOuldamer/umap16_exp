/**
 * 
 */
package model;

import java.io.BufferedReader;
import java.io.FileReader;
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
import java.util.Map.Entry;


/**
 * @author ould
 *
 */
public class UserExpertiseScore {

	/**
	 * @param args
	 */
	

	public static boolean ASC = true;
	public static boolean DESC = false;
	public static void main(String[] args) throws IOException {
		
		int b = Integer.parseInt(args[0]);
		int f = Integer.parseInt(args[1]);
		ArrayList<String> liste_user = new ArrayList<String>();
		BufferedReader users  = new BufferedReader(new FileReader("./files_exp/id_users"));
		String line = "";	
		while ((line = users.readLine()) != null) {	
			liste_user.add(line);			
		}
		users.close();
		for (int i = b ; i < f; i++) {	
			if(i!=49){
				for (int i1= 0; i1 < 2; i1++) {				
					computeExperiseScore(liste_user, i1,i);
				}	
			}		
		}
	}

	public static void computeExperiseScore(ArrayList<String> liste_user, int number_of_topic, int id_query) throws IOException{
		FileWriter file = new FileWriter("./users_topic_expertise/expertise_user_"+number_of_topic+"_query"+id_query+".txt");	
		HashMap<String, Double> map_score_expertise = new HashMap<String, Double>();
		HashMap<String, Double> map_topic_score = new HashMap<String, Double>();
		BufferedReader topic_query  = new BufferedReader(new FileReader("topic_query.txt"));
		String line = "";	
		while ((line = topic_query.readLine()) != null) {	
			String [] vect = line.split(" ");				
			if(vect[0].equals(id_query)){
				for (int k = 0; k <= number_of_topic; k = k+2) {
					map_topic_score.put(vect[2+k], Double.parseDouble(vect[2+k+1]));
					System.out.println(vect[2+k] +" "+Double.parseDouble(vect[2+k+1]));
				}
				break;
			}
		}
		topic_query.close();
		for (int i = 0; i < liste_user.size(); i++) {
			BufferedReader topic_user  = new BufferedReader(new FileReader("./files_exp/inf-train_users_doc_100")); //./files_exp/inf-train_users_doc_tag_100 pour bibs
			String line_user = "";	

			Double score = 0.0 ;
			while ((line_user = topic_user.readLine()) != null) {
				if(!line_user.startsWith("#")){
					String [] vect = line_user.split(" ");
					//System.out.println(line_user);
					String [] v = vect[1].split("/");
					//System.out.println((v[v.length-1].substring(0, v[v.length-1].lastIndexOf(".txt"))));
					if((v[v.length-1].substring(0, v[v.length-1].lastIndexOf(".txt"))).equals(liste_user.get(i))){
						for(Entry<String, Double> entry : map_topic_score.entrySet()) {
							for (int j1 = 2; j1 < vect.length; j1 = j1+2) {								
								if(vect[j1].equals(entry.getKey())){
									//System.err.println(vect[j1] + " "+entry.getKey() +" "+entry.getValue());
									map_topic_score.put(entry.getKey(), entry.getValue()* Double.parseDouble(vect[j1+1])); // score topic * score user
								}
							}
						}						
					}
				}
				
				for(Entry<String, Double> entry : map_topic_score.entrySet()) {
					//System.err.println(entry.getValue());
					score = entry.getValue();
				}
			}
			topic_user.close();
			/// somme sur tout les topics
			map_score_expertise.put(liste_user.get(i), score);
			
		}

		System.out.println("jjj");
		
		// ecriture du score expertise user
		for(Entry<String, Double> entry : sortByComparator(map_score_expertise, DESC).entrySet()) {
			if(entry.getValue() != 0.0)
			file.write(id_query+" "+ number_of_topic+" "+entry.getKey()+" "+entry.getValue() +"\n");	
		}	
		file.close();
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
