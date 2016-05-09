/**
 * 
 */
package core;

import java.io.File;
import java.io.FileNotFoundException;
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
import java.util.Map.Entry;
import java.util.Scanner;

import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

/**
 * @author ould
 *
 */
public class QueryTopical {

	/**
	 * @param args
	 */
	

	static org.jdom2.Document document;
	static Element racine;

	public static boolean ASC = true;
	public static boolean DESC = false;
	public static void main(String[] args) throws IOException {
		String file = args[0];
		
		FileWriter topic_query = new FileWriter(file.substring(0, file.lastIndexOf(".xml"))+"_topic_query");
		SAXBuilder sxb = new SAXBuilder();
		try
		{
			document = sxb.build(new File(file));
		}
		catch(Exception e){}
		racine = document.getRootElement();
		List<Element> listQuery = racine.getChildren("TOP");
		Iterator<Element> i = listQuery.iterator();
		while(i.hasNext())		
		{
			
			Map<String, Double> map_topic_score = new HashMap<String, Double>();

			String query = "";
			Element courant = i.next();
			query = courant.getChild("TITLE").getText();
			System.out.println(query);
			ArrayList<String> liste_doc = new ArrayList<String>();
			Scanner s_q = new Scanner(new File("query_doc_delicous_2006"));
			while (s_q.hasNextLine()) {
				String[] line =  s_q.nextLine().split(" ");
				if(line[0].equals(courant.getChild("NUM").getText())){
					liste_doc.add(line[2]);
				}				
			}
			s_q.close();
			
			map_topic_score = sortByComparator(getTopicQuery(query, liste_doc), DESC);
			topic_query.write(courant.getChild("NUM").getText() + " " + query+" ");
			for(Entry<String, Double> entry : map_topic_score.entrySet()) {
			    String cle = entry.getKey();
			    Double valeur = entry.getValue();
			    topic_query.write(cle+" "+valeur+" ");
			}
			topic_query.write("\n");
			topic_query.flush();
			
			
			
		}
		topic_query.close();
		

	}

	
	public static HashMap<String, Double> getTopicQuery(String query, ArrayList<String> liste_doc) throws FileNotFoundException{
		HashMap<String, Double> map_topic_score = new HashMap<String, Double>();
		for (int i = 0; i < liste_doc.size(); i++) {
			Scanner s = new Scanner(new File("./files_exp/tutorial_composition_100"));
			while (s.hasNextLine()) {
				String line = s.nextLine();
				if(!line.startsWith("#")){
					String [] vect = line.split("\t");
					for (int j = 2; j < vect.length; j = j+2) {
						String [] v = vect[1].split("/");
						String id_doc = v[v.length-1].substring(0, v[v.length-1].lastIndexOf(".txt"));
						if(id_doc.equals(liste_doc.get(i))){
							if(map_topic_score.containsKey(vect[j])){
								map_topic_score.put(vect[j], (map_topic_score.get(vect[j]) + Double.valueOf(vect[j+1]))/2);
							}
							else {
								//System.out.println(vect[j]+" "+vect[j+1]);
								map_topic_score.put(vect[j], Double.valueOf(vect[j+1]));
							}
						}
					}
				}
			}
			s.close();			
		}
		return map_topic_score;
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





























