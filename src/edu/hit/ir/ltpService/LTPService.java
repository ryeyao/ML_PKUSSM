package edu.hit.ir.ltpService;

import java.io.IOException;

import org.jdom.JDOMException;

import edu.hit.ir.ltpService.CirService;

public class LTPService {
	CirService cs;

	public LTPService(String authorization) {
		super();
		this.cs = new CirService(authorization);
	}
 
	public LTML analyze(String option, String analyzeString) throws JDOMException, IOException{
		cs.setAnalysisOptions(option);
		cs.setXmlOption(false);
		LTML ltml = new LTML();
//		ltml.setEncoding(cs.getEncoding());
		String str = cs.Connect(analyzeString);
		System.out.println("str here:\n" + str);
		ltml.build(str);
		ltml.setOver();
		return ltml;
	}
	
	public LTML analyze(String option, LTML ltmlIn) throws JDOMException, IOException{
		cs.setAnalysisOptions(option);
		cs.setXmlOption(true);
		LTML ltml = new LTML();
//		ltml.setEncoding(cs.getEncoding());
		ltml.build(cs.Connect(ltmlIn.getXMLStr()));
		ltml.setOver();
		return ltml;
	}
	
	public String getAnalyze(String option, String analyzeString) throws JDOMException, IOException{
		cs.setAnalysisOptions(option);
		cs.setXmlOption(false);
		LTML ltml = new LTML();
		return cs.Connect(analyzeString);
	}
	
	public void setAnalysisOptions(String op) {
		cs.setAnalysisOptions(op);
	}

	public void setAuthorization(String authorization) {
		cs.setAuthorization(authorization);
	}

	public boolean setEncoding(String en) {
		return cs.setEncoding(en);
	}

	public String getEncoding() {
		return cs.getEncoding();
	}

	public boolean isAuthrized() {
		return cs.isAuthrized();
	}
	
}
