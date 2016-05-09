/**
 * 
 */
package utils;

import java.io.File;
import java.io.FileNotFoundException;
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
public class ScoreNormalisation {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	

	public static boolean ASC = true;
	public static boolean DESC = false;
	
	
	public static void main(String[] args) throws FileNotFoundException {
		HashMap<String, Double> map_user_score_similarity = new HashMap<String, Double>();
		Scanner s_expertise = new Scanner(new File ("TF_IDF_0.res"));
		while (s_expertise.hasNextLine()) {
			String line = s_expertise.nextLine();
			if (!line.startsWith("#")) {
				String [] vect = line.split(" ");
				if(vect[0].equals("579116")){ // on cherche les users pour le user de la requete courante						
					map_user_score_similarity.put(vect[2], Double.parseDouble(vect[4]));						
				}
			}
		}
		s_expertise.close();
		/*
		
		for(Entry<String, Double> entry : map_user_score_similarity.entrySet()) {
		
			System.out.println(entry.getKey()+" "+entry.getValue());
		}
		*/

		/*
		 * normalisation
		 */
		
		Map<String, Double> ll = new HashMap<String, Double>();
		ll = sortByComparator(map_user_score_similarity, DESC);
		double max = ll.get(ll.keySet().toArray()[0]);
		
		for(Entry<String, Double> entry : ll.entrySet()) {
			map_user_score_similarity.put(entry.getKey(), entry.getValue()/max);
		}
		for(Entry<String, Double> entry : map_user_score_similarity.entrySet()) {
			
			System.out.println(entry.getKey()+" "+entry.getValue());
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
