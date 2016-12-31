package org.imsglobal.lti.toolProvider.mediaType;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.imsglobal.lti.product.Product;
import org.imsglobal.lti.product.ProductFamily;
import org.imsglobal.lti.product.ProductInfo;
import org.imsglobal.lti.profile.ProfileResourceHandler;
import org.imsglobal.lti.toolProvider.ToolProvider;

public class ToolProfile {
	/**
	 * Class to represent an LTI Tool Profile
	 *
	 * @author  Stephen P Vickers <svickers@imsglobal.org>
	 * @copyright  IMS Global Learning Consortium Inc
	 * @date  2016
	 * @version  3.0.0
	 * @license  GNU Lesser General Public License, version 3 (<http://www.gnu.org/licenses/lgpl.html>)
	 */

	    public Product product_instance;
	    public String lti_version;
	    private List<ResourceHandler> resourceHandlers;
	    private Map<String, URL> baseUrlChoices;

	/**
	 * Class constructor.
	 *
	 * @param ToolProvider toolProvider   Tool Provider object
	 * 
	 * basically clones the toolProvider's product info into this profile
	 */
	    public ToolProfile(ToolProvider toolProvider)
	    {

	        this.lti_version = "LTI-2p0";
	        /*
	         * Product Info section
	         */
	        ProductInfo pi = new ProductInfo();

	        if (toolProvider.getProduct() != null) {
	        	Product product = toolProvider.getProduct();
	            this.product_instance = new Product();
	
		        if (StringUtils.isNotEmpty(product.getId())) {
		            this.product_instance.setId(product.getId());
		        }
		        if (StringUtils.isNotEmpty(product.getName())) {
		        	Map<String, String> productName = new HashMap<String, String>();
		        	productName.put("default_value", product.getName());
		        	productName.put("key", "tool.name");
		        	pi.setProductName(productName);
		        }
		        if (StringUtils.isNotEmpty(product.getDescription())) {
		        	Map<String, String> description = new HashMap<String, String>();
		        	description.put("default_value", product.getDescription());
		        	description.put("key", "tool.description");
		        	pi.setDescription(description);
		        }
		        if (StringUtils.isNotEmpty(product.getUrl())) {
		            this.product_instance.setGuid(product.getUrl());
		        }
		        if (StringUtils.isNotEmpty(product.getVersion())) {
		        	pi.setVersion(product.getVersion());
		        }
		        
		        this.product_instance.setProductInfo(pi);
		        
	        }
	        
	        /*
	         * Product Family Section
	         */
	        ProductFamily pf = new ProductFamily();
	        
	        if (toolProvider.getVendor() != null) {
	        	ProductFamily vendor = toolProvider.getVendor();
		        if (StringUtils.isNotEmpty(vendor.getCode())) {
		        	pf.setCode(vendor.getCode());
		        }
		        if (vendor.getVendorName() != null) {
		        	Map<String, String> name = new HashMap<String, String>();
		        	name.put("default_value", vendor.getVendorName().get("default_value"));
		        	name.put("key", "tool.vendor.name");
		        	pf.setVendorName(name);
		        }
		        if (vendor.getVendorDescription() != null) {
		        	Map<String, String> desc = new HashMap<String, String>();
		        	desc.put("default_value", vendor.getVendorDescription().get("default_value"));
		        	desc.put("key", "tool.vendor.description");
		        	pf.setVendorDescription(desc);
		        }
		        if (StringUtils.isNotEmpty(vendor.getWebsite())) {
		        	pf.setWebsite(vendor.getWebsite());
		        }
		        if (vendor.getTimestamp() != null) {
		        	pf.setTimestamp(vendor.getTimestamp());
		        }
	        }
	        
	        resourceHandlers = new ArrayList<ResourceHandler>();
	        for (ProfileResourceHandler handler : toolProvider.getResourceHandlers()) {
	            resourceHandlers.add(new ResourceHandler(toolProvider, handler));
	        }
	        
	        
	        if (StringUtils.isNotEmpty(toolProvider.getBaseUrl().toString())) {
	        	baseUrlChoices = new HashMap<String, URL>();
	        	baseUrlChoices.put("default_base_url", toolProvider.getBaseUrl());
	        }

	    }
}
