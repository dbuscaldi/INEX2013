package fr.lipn.inex.similarity;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import fr.irit.sts.proxygenea.ConceptualComparer;
import fr.lipn.sts.ckpd.NGramComparer;
import fr.lipn.sts.ir.IRComparer;
import fr.lipn.sts.semantic.JWSComparer;
import fr.lipn.sts.syntax.DepComparer;
import fr.lipn.sts.tools.GoogleTFFactory;
import fr.lipn.sts.tools.LevenshteinDistance;
import fr.lipn.sts.tools.TfIdfComparer;
import fr.lipn.sts.tools.WordNet;

public class PassageComparer {
	final static String modelfile = "/tempo/shared/libs/stanford-postagger-full-2012-07-09/models/english-bidirectional-distsim.tagger";
	final static String WN_HOME = "/tempo/shared/WordNet-3.0/";
	final static String GOOGLE_W1T_HOME = "/tempo/corpora/GoogleW1T/vocab/vocab";
	private static MaxentTagger tagger;
	
	public static void init() {
		try {
			tagger = new MaxentTagger(modelfile);
			GoogleTFFactory.init(GOOGLE_W1T_HOME);
			WordNet.init(WN_HOME);		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static double compare(String text, String passage){
		double similarity=0d;
		StringReader r0 = new StringReader(text);
    	StringReader r1 = new StringReader(passage);
		
    	List<List<HasWord>> tokenizedsentences0 = tagger.tokenizeText(r0);
	    ArrayList<TaggedWord> tSentence= tagger.tagSentence(tokenizedsentences0.get(0));
	   
	    List<List<HasWord>> tokenizedsentences1 = tagger.tokenizeText(r1);
	    ArrayList<TaggedWord> tSentence1=tagger.tagSentence(tokenizedsentences1.get(0));
	    
	    double sim=NGramComparer.compare(tSentence, tSentence1);
	    //double conceptsim=ConceptualComparer.compare(tSentence, tSentence1);
	    double wnsim=JWSComparer.compare(tSentence, tSentence1);
	    //double editsim = LevenshteinDistance.levenshteinSimilarity(text, passage);/
	    //double IRsim = IRComparer.compare(text, passage);
	    double cosinesim = TfIdfComparer.compare(tSentence, tSentence1);
		
	    //return sim;
		return cosinesim;//similarity;
	}
}
