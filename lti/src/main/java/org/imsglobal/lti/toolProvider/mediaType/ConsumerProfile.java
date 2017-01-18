package org.imsglobal.lti.toolProvider.mediaType;

import java.util.List;

import org.imsglobal.lti.product.Product;
import org.imsglobal.lti.profile.ServiceDefinition;

public class ConsumerProfile {
	private JSONContext context;
	private List<JSONContext> contexts;
	private List<ServiceDefinition> servicesOffered;
	private String id;
	private Product product;
	
	public ConsumerProfile() {
		
	}

	public JSONContext getContext() {
		return context;
	}

	public void setContext(JSONContext context) {
		this.context = context;
	}

	public List<JSONContext> getContexts() {
		return contexts;
	}

	public void setContexts(List<JSONContext> contexts) {
		this.contexts = contexts;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public void setServicesOffered(List<ServiceDefinition> offered) {
		this.servicesOffered = offered;
	}

	public List<ServiceDefinition> getServicesOffered() {
		return servicesOffered;
	}

	public List<String> getCapabilityOffered() {
		// TODO Auto-generated method stub
		return null;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}
	
	
	
}
