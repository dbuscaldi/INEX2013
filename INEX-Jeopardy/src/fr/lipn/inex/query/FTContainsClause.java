package fr.lipn.inex.query;

public class FTContainsClause {
	String variable;
	String text;
	
	public FTContainsClause(String variable, String text){
		this.variable=variable;
		this.text=text;
	}
}
