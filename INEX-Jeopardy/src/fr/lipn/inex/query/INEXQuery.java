package fr.lipn.inex.query;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class INEXQuery {
	String id;
	String category;
	String clue;
	String keywords;
	String sparql;
	String pureSPARQL;
	Vector<String> variables;
	Vector<FTContainsClause> clauses;
	
	static Pattern pvar = Pattern.compile("(?i)(?<=SELECT (DISTINCT )?) ?\\?.+(?=WHERE)");
	static Pattern pft = Pattern.compile("(?i)(?<=FILTER FTContains ).+(?=\\.)");
	
	public INEXQuery(String id, String category){
		this.id=id;
		this.category=category;
		this.clue="";
		this.keywords="";
		this.sparql="";
		variables=new Vector<String>();
		clauses = new Vector<FTContainsClause>();
	}
	
	public void setClue(String clue) {
		this.clue=clue;
	}
	
	public void setTitle(String keywords){
		this.keywords=keywords;
	}
	
	public void setSPARQL(String query){
		this.sparql=query.trim();
		this.pureSPARQL=sparql.replaceAll("FILTER FTContains.+?\\) ?\\.", "");
		
		Matcher m = pvar.matcher(sparql);
		//System.err.println(sparql);
		while(m.find()){
			String group=m.group().trim();
			String [] vars = group.split(" ");
			for(String v : vars) variables.add(v);
			//System.err.println(group);
		}
		//TODO: set FTContains clauses
	}
	
	public void print(){
		System.err.println("query id:"+id+" SPARQL:"+sparql+"\npure SPARQL: "+pureSPARQL);
	}
}
