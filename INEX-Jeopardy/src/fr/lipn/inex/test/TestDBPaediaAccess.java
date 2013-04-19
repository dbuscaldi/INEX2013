package fr.lipn.inex.test;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;

import fr.lipn.inex.similarity.PassageComparer;

public class TestDBPaediaAccess {

	public static void main(String[] args) {
		PassageComparer.init();
        String service = "http://dbpedia.org/sparql";
        //String query = "ASK { }";
        /*String q1 = "prefix owl: <http://www.w3.org/2002/07/owl#>" +
        		"prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
        		"prefix xsd: <http://www.w3.org/2001/XMLSchema#>" +
        		"prefix dbpo: <http://dbpedia.org/ontology/>" +
        		"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
        		"prefix dbpedia: <http://dbpedia.org/resource/>" +
        		"prefix dc:<http://purl.org/dc/elements/1.1/description#>" +
        		"SELECT DISTINCT ?candidate1 ?label1 ?abstract1" +
        		"WHERE {	?candidate1 rdfs:label ?label1 ." +
        		"	?candidate1 dbpo:abstract ?abstract1 ." +
        		"	{ SELECT  DISTINCT ?candidate1 ?candidate2" +
        		"WHERE { ?candidate1 <http://dbpedia.org/property/watercourse> ?candidate2 }}" +
        		"FILTER(langMatches(lang(?abstract1), \"EN\"))	FILTER(langMatches(lang(?label1), \"EN\")) }";
        */
        String query = "SELECT DISTINCT ?s ?o WHERE { ?s <http://dbpedia.org/property/partner> ?o. ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/class/yago/Actor109765278> . ?o <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/class/yago/Actor109765278> .}";
        String keywords = "world's most attractive male actor";
        
        QueryExecution qe = QueryExecutionFactory.sparqlService(service, query);
        
        ResultSet results =  qe.execSelect();
        
        //ResultSetFormatter.out(System.out, results);
        
        HashSet<String> entities = new HashSet<String>();
        
        while(results.hasNext()){
        	QuerySolution qs = results.next();
        	RDFNode snode = qs.get("?s");
        	RDFNode onode = qs.get("?o");
        	entities.add(snode.toString());
        	
        	//System.err.println(snode +" <-> "+ onode );
        	
        }
        
        qe.close();
        HashMap <String, Double> valueMap = new HashMap<String, Double>();
        
        for(String candidate : entities) {
        	String req = "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
        			"prefix foaf: <http://xmlns.com/foaf/0.1/>\n" +
        			"SELECT DISTINCT ?abstract ?label ?id \n{\n<"+candidate+"> <http://dbpedia.org/ontology/abstract> ?abstract . \n<" + candidate + "> rdfs:label ?label . \n" +
        			"?wppage foaf:primaryTopic <"+candidate+"> ;\n <http://dbpedia.org/ontology/wikiPageID> ?id . " +
        			"FILTER langMatches( lang(?abstract), 'en') . FILTER langMatches ( lang(?label), 'en') }";
        	//System.err.println(req);
        	QueryExecution qee = QueryExecutionFactory.sparqlService(service, req);
        	ResultSet ers =  qee.execSelect();
        	String abst= "";
        	while(ers.hasNext()){
        		QuerySolution eqs = ers.next();
        		RDFNode snode = eqs.get("?abstract");
        		abst=snode.toString();
        	}
        	System.err.println(abst);
        	String [] passages = abst.split("\\. ");
    		double maxSim = 0d;
    		for(String p : passages) {
    			double sim = PassageComparer.compare(keywords, p);
    			if(sim > maxSim) maxSim = sim;
    		}
    		System.err.println(candidate+" score: "+maxSim);
    		valueMap.put(candidate, new Double(maxSim));
        }
        
        Map<String, Double> sorted = sortByValues(valueMap);
        int i=0;
        for(String k : sorted.keySet()){
        	System.err.println(k+" : "+sorted.get(k));
        	i++;
        	if(i> 5) break;
        }
        /*
        try {
            if (qe.execAsk()) {
                System.out.println(service + " is UP");
            } // end if
        } catch (QueryExceptionHTTP e) {
            System.out.println(service + " is DOWN");
        } finally {
            qe.close();
        }
        */
        
        /*
         * SELECT ?abstract, ?label
WHERE {
{ <http://dbpedia.org/resource/Civil_engineering> <http://dbpedia.org/ontology/abstract> ?abstract .
  <http://dbpedia.org/resource/Civil_engineering> rdfs:label ?label
FILTER langMatches( lang(?abstract), 'en') . FILTER langMatches ( lang(?label), 'en') }
}
         */
        
        /*
        SELECT ?abstract, ?label, ?id
        		{ 
        		  <http://dbpedia.org/resource/Civil_engineering> <http://dbpedia.org/ontology/abstract> ?abstract .
        		  <http://dbpedia.org/resource/Civil_engineering> rdfs:label ?label .
        		  ?wppage foaf:primaryTopic <http://dbpedia.org/resource/Civil_engineering> ;
        		    <http://dbpedia.org/ontology/wikiPageID> ?id .
        		FILTER langMatches( lang(?abstract), 'en') . FILTER langMatches ( lang(?label), 'en') 
        		}
        */
    }
	
    /*
     * Java method to sort Map in Java by value e.g. HashMap or Hashtable
     * throw NullPointerException if Map contains null values
     * It also sort values even if they are duplicates
     */
    public static <K extends Comparable,V extends Comparable> Map<K,V> sortByValues(Map<K,V> map){
        List<Map.Entry<K,V>> entries = new LinkedList<Map.Entry<K,V>>(map.entrySet());
     
        Collections.sort(entries, new Comparator<Map.Entry<K,V>>() {

            public int compare(Entry<K, V> o1, Entry<K, V> o2) {
                return -o1.getValue().compareTo(o2.getValue());
            }
        });
     
        //LinkedHashMap will keep the keys in the order they are inserted
        //which is currently sorted on natural ordering
        Map<K,V> sortedMap = new LinkedHashMap<K,V>();
     
        for(Map.Entry<K,V> entry: entries){
            sortedMap.put(entry.getKey(), entry.getValue());
        }
     
        return sortedMap;
    }


}
