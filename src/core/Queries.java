/**
 * 
 */
package core;

import java.io.BufferedReader;
import java.io.FileReader;
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
public class Queries {

	/**
	 * @param args
	 */



	public static boolean ASC = true;
	public static boolean DESC = false;
	public static void main(String[] args) throws NumberFormatException, IOException {
		Map<String, Double> map_score_topic = new HashMap<String, Double>();
		map_score_topic = getTop_KTopic("120", "0");
		ArrayList<String> liste_top_k_topic_selected = new ArrayList<String>();
		List<String> list = new ArrayList<String>(map_score_topic.keySet());
		for(int i=0; i<2; i++) {
			liste_top_k_topic_selected.add(list.get(i));
		}
		System.out.println(liste_top_k_topic_selected);
		ArrayList<String> liste_topic_terms = new ArrayList<String>();
		liste_topic_terms = getTermTopic("120", liste_top_k_topic_selected);
		System.out.println(liste_topic_terms.size());

	}



	public static Map<String, Double>  getTop_KTopic(String k_topic, String query_id) throws NumberFormatException, IOException{		
		Map<String, Double> map_topic_score = new HashMap<String, Double>();
		String path = "./files/inf-train."+k_topic;
		BufferedReader in_topic  = new BufferedReader(new FileReader(path));
		String line = "";		
		while ((line = in_topic.readLine()) != null) {	
			if(!line.startsWith("#")){
				String [] vect = line.split(" ");
				if(vect[0].equals(query_id)){
					for (int i = 2; i < vect.length; i=i+2) {
						map_topic_score.put(vect[i],Double.parseDouble(vect[i+1]));
					}					
				}
			}
		}
		in_topic.close();		
		return sortByComparator(map_topic_score,DESC );
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


	public static ArrayList<String> getTermTopic(String k_topic,ArrayList<String> liste_topic_selected) throws IOException{
		ArrayList<String> liste_topic_terms = new ArrayList<String>();
		String path = "./files/tutorial_keys_120.txt";
		BufferedReader in_topic  = new BufferedReader(new FileReader(path));
		String line = "";		
		while ((line = in_topic.readLine()) != null) {	
			if(!line.startsWith("#")){				
				String[] result = line.split("\\s");
				for (int x=2; x<result.length; x++){
					if(liste_topic_selected.contains(result[0])){
						if(!liste_topic_terms.contains(result[x])){
							liste_topic_terms.add(result[x]);
						}
					}
				}					
			}
		}
		in_topic.close();	
		return liste_topic_terms;
	}
}
