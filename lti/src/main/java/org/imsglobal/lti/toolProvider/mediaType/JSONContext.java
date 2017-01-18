package org.imsglobal.lti.toolProvider.mediaType;

import java.util.List;
import java.util.Map;

import org.imsglobal.lti.product.Product;
import org.json.simple.JSONObject;

public class JSONContext {
	private Map<String, String> terms;
	private Product product;
	
	public JSONContext() {
		
	}
	
	public void parse(JSONObject obj) {

	}
	
	public Map<String, String> getTerms() {
		return terms;
	}

	public void setTerms(Map<String, String> terms) {
		this.terms = terms;
	}
	
	
}
