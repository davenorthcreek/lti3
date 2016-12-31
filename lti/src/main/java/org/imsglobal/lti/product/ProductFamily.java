package org.imsglobal.lti.product;

import java.util.Map;

import org.joda.time.DateTime;

public class ProductFamily {
	
	private String code;

	private Map<String, String> vendorName;
	
	private Map<String, String> vendorDescription;
	
	private String website;
	
	private DateTime timestamp;
	
	public ProductFamily() {
		
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Map<String, String> getVendorName() {
		return vendorName;
	}

	public void setVendorName(Map<String, String> vendorName) {
		this.vendorName = vendorName;
	}

	public Map<String, String> getVendorDescription() {
		return vendorDescription;
	}

	public void setVendorDescription(Map<String, String> vendorDescription) {
		this.vendorDescription = vendorDescription;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public DateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(DateTime timestamp) {
		this.timestamp = timestamp;
	}
	
	
}
