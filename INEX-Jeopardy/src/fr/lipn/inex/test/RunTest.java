package fr.lipn.inex.test;

import java.io.File;
import java.util.Vector;

import fr.lipn.inex.query.INEXQuery;
import fr.lipn.inex.query.InexQueryHandler;

public class RunTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File file = new File("queries/2013-ld-jeopardy-topics.xml");
		try {
    		InexQueryHandler hdlr = new InexQueryHandler(file);
    		Vector<INEXQuery> queries = hdlr.getParsedQueries();
    		for(INEXQuery q : queries) {
    			q.print();
    		}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}
