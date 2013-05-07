package fr.lipn.inex.query;

import java.io.File;
import java.io.IOException;
import java.util.Stack;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class InexQueryHandler extends DefaultHandler {  
	  protected Stack<String> elemStack;
	  protected INEXQuery currQuery;
	  protected Vector<INEXQuery> parsedQueries;
	  protected StringBuffer currentTitle;
	  protected StringBuffer currentKeywords;
	  protected StringBuffer currentSPARQLCode;
		  
	  public InexQueryHandler(File xmlFile) 
		  	throws ParserConfigurationException, SAXException, IOException {
		    
		    SAXParserFactory spf = SAXParserFactory.newInstance();
		    SAXParser parser = spf.newSAXParser();
		    
		    parsedQueries=new Vector<INEXQuery>();
		    
		    
		    //System.out.println("parser is validating: " + parser.isValidating());
		    try {
		      parser.parse(xmlFile, this);
		    } catch (org.xml.sax.SAXParseException spe) {
		      System.out.println("SAXParser caught SAXParseException at line: " +
		        spe.getLineNumber() + " column " +
		        spe.getColumnNumber() + " details: " +
				spe.getMessage());
		    }
	  }

	  // call at document start
	  public void startDocument() throws SAXException {
		  elemStack=new Stack<String>();
	  }

	  // call at element start
	  public void startElement(String namespaceURI, String localName,
	    String qualifiedName, Attributes attrs) throws SAXException {

	    String eName = localName;
	     if ("".equals(eName)) {
	       eName = qualifiedName; // namespaceAware = false
	     }
		     
	     elemStack.addElement(eName);
	     if(eName.equals("topic")){
	    	 currQuery=new INEXQuery(attrs.getValue("id"), attrs.getValue("category"));
	    	 currentTitle = new StringBuffer();
	    	 currentKeywords = new StringBuffer();
	    	 currentSPARQLCode = new StringBuffer();
	     }
  
	  }

	  // call when cdata found
	  public void characters(char[] text, int start, int length)
	    throws SAXException {
		  String topElement=elemStack.peek();
		  if(topElement.equals("jeopardy_clue")) currentTitle.append(text, start, length);
		  else if(topElement.equals("keyword_title")) currentKeywords.append(text, start, length);
		  else if(topElement.equals("verbatim")) currentSPARQLCode.append(text, start, length);
	  }

	  // call at element end
	  public void endElement(String namespaceURI, String simpleName,
	    String qualifiedName)  throws SAXException {
	    String eName = simpleName;
	    if ("".equals(eName)) {
	      eName = qualifiedName; // namespaceAware = false
	    }
	    elemStack.pop();
	    if (eName.equals("jeopardy_clue")){
	    	currQuery.setClue(currentTitle.toString());
	    } else if(eName.equals("keyword_title")){
	    	currQuery.setTitle(currentKeywords.toString());
	    } else if(eName.equals("verbatim")){
	    	currQuery.setSPARQL(currentSPARQLCode.toString());
	    } else if(eName.equals("topic")) {
	    	parsedQueries.add(currQuery);
	    }
	  }
		  
		  
	  public Vector<INEXQuery> getParsedQueries() {
		  return this.parsedQueries;
	  }
			
}

