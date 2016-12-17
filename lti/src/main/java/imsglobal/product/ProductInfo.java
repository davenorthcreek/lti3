package imsglobal.product;

import java.util.Map;

public class ProductInfo {
	private Map<String, String> productName;
	private Map<String, String> description;
	private String version;
	
	public ProductInfo() {
		
	}
	
	public Map<String, String> getProductName() {
		return this.productName;
	}
	
	public Map<String, String> getDescription() {
		return this.description;
	}
	
	public void setProductName(Map<String, String> productName2) {
		this.productName = productName2;
	}
	
	public void setDescription(Map<String, String> description2) {
		this.description = description2;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	
	
}
