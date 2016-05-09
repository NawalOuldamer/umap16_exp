/**
 * 
 */
package stats;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
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
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author ould
 *
 */
public class UserTopicCouverture {

	/**
	 * @param args
	 * @throws IOException 
	 */

	public static boolean ASC = true;
	public static boolean DESC = false;
	public static void main(String[] args) throws IOException {

		String line = "";
		ArrayList<String> liste_user_selected = new ArrayList<String>();
		BufferedReader user  = new BufferedReader(new FileReader("./stats/user_doc_book.txt"));
		while ((line = user.readLine()) != null) {	
			if(!line.startsWith("#")){
				String [] vect =line.split(" ");
				//if((Integer.parseInt(vect[0]))>100)// les user qui ont plus de 100 docs
					liste_user_selected.add(vect[0]);
			}
		}
		user.close();

		//getCouvertureTopic(liste_user_selected);
		System.out.println(liste_user_selected.size());
		getNbTopicUser(0.1, liste_user_selected);

	}


	public static void getNbTopicUser(double seuil, ArrayList<String> user_selected) throws NumberFormatException, IOException{
		String line = "";

		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();

		HashMap<Integer, Integer> map_user_topic = new HashMap<Integer, Integer>();
		/*for (Integer i = 1; i <= 100; i++) {
			map.put(i, 0);
		}
		/*
		for(Entry<Integer, Integer> entry : map.entrySet()) {
			Integer cle = entry.getKey();
			Integer valeur = entry.getValue();

		    System.out.println(cle+" "+valeur);
		}
		 */
		BufferedReader in_topic  = new BufferedReader(new FileReader("./stats/inf-train_users_doc_tag_100"));
		while ((line = in_topic.readLine()) != null) {
			double count = 0;
			String id_user ="";
			if(!line.startsWith("#")){
				String [] vect = line.split(" ");
				String [] v = vect[1].split("/");
				id_user = v[v.length-1].substring(0, v[v.length-1].lastIndexOf(".txt"));
				if(user_selected.contains(id_user)){
					for (int i = 2; i < vect.length; i=i+2) {
						if(Double.parseDouble(vect[i+1])>seuil)
							count++;
					}

					map_user_topic.put(Integer.parseInt(id_user), (int) count);
				}
			}

			//System.out.println(id_user+" "+ (int)count);

			//		

			for (int i = 1; i <= (int) count; i++) {
				if(map.containsKey(i)){
					map.put(i, map.get(i)+1);
				}
				else {
					map.put(i, 1);
				}
			}

		}

	
	in_topic.close();
	Integer vl = 0;
	for(Entry<Integer, Integer> entry : map.entrySet()) {
		Integer cle = entry.getKey();
		Integer valeur = entry.getValue();
		vl += valeur;

		//System.out.println(cle+" "+valeur);
		// graphique croissant
		//System.out.println(cle+" "+vl);
	}

	System.out.println(map_user_topic.size());
	for (int i = 1; i <= 100; i++) {

		int count =0;
		for(Entry<Integer, Integer> entry : map_user_topic.entrySet()) {				
			Integer cle = entry.getKey();
			Integer valeur = entry.getValue();
			if(valeur<=i) count++;
		}
		System.out.println(i + " " + count);
	}



}



/**
 * retourne les mediane des tocpis pour les utisateur 
 * @throws NumberFormatException
 * @throws IOException
 */

public static void getCouvertureTopic(ArrayList<String> users_selected) throws NumberFormatException, IOException{


	HashMap<String, Double> map = new HashMap<String,  Double>();

	String line = "";	
	for(int j = 0; j<100; j++){
		ArrayList<Double> li = new ArrayList<Double>();
		BufferedReader in_topic  = new BufferedReader(new FileReader("./files/inf-train_users_doc_tag_100"));
		while ((line = in_topic.readLine()) != null) {	
			if(!line.startsWith("#")){
				String [] vect = line.split(" ");				
				for (int i = 2; i < vect.length; i=i+2) {
					//String [] v = vect[1].split("/");
					//if(users_selected.contains(v[v.length-1].substring(0, v[v.length-1].lastIndexOf(".txt")))){
					if(vect[i].equals(String.valueOf(j))){
						li.add(Double.parseDouble(vect[i+1]));	
						//}	
					}					
				}		
			}
		}
		in_topic.close();
		Collections.sort(li);
		map.put(String.valueOf(j), median(li));

	}



	Map<String, Double> m = new HashMap<String,Double>();
	m = sortByComparator(map, DESC);
	for(Entry<String, Double> entry : m.entrySet()) {
		String cle = entry.getKey();
		Double valeur = entry.getValue();

		System.out.println(cle+" "+valeur);
	}
}

public static double median(ArrayList<Double> m) {
	int middle = m.size()/2;
	if (m.size()%2 == 1) {
		return m.get(middle);
	} else {
		return (m.get(middle-1) + m.get(middle)) / 2.0;
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


