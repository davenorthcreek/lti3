package org.imsglobal.lti.toolProvider.mediaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.imsglobal.lti.LTIUtil;
import org.imsglobal.lti.profile.ServiceDefinition;
import org.imsglobal.lti.toolProvider.ToolProvider;

public class ToolProxy {

/**
 * Class to represent an LTI Tool Proxy media type
 *
 * @author  Stephen P Vickers <svickers@imsglobal.org>
 * @copyright  IMS Global Learning Consortium Inc
 * @date  2016
 * @version  3.0.0
 * @license  GNU Lesser General Public License, version 3 (<http://www.gnu.org/licenses/lgpl.html>)
 */
	
	private String lti_version;
	
	private String toolConsumerProfileId;
	
	private List<String> contexts;
	
	private String type;
	
	private String id;
	
	private ToolProfile toolProfile;
	
	private SecurityContract securityContract;
	

/**
 * Class constructor.
 *
 * @param ToolProvider toolProvider   Tool Provider object
 * @param ServiceDefinition toolProxyService  Tool Proxy service
 * @param string secret  Shared secret
 */
    public ToolProxy (ToolProvider toolProvider, ServiceDefinition toolProxyService, String secret)
    {
    	contexts = new ArrayList<String>();
    	
    	contexts.add("http://purl.imsglobal.org/ctx/lti/v2/ToolProxy");
    	
    	setType("ToolProxy");
    	setId(toolProxyService.getEndpoint().toExternalForm());
    	setLti_version("LTI-2p0");
    	setToolConsumerProfileId(toolProvider.getConsumer().getProfile().getId());
    	setToolProfile(new ToolProfile(toolProvider));
    	setSecurityContract(new SecurityContract(toolProvider, secret));
    }
	
	
	public String getLti_version() {
		return lti_version;
	}
	
	
	public void setLti_version(String lti_version) {
		this.lti_version = lti_version;
	}
	
	
	public String getToolConsumerProfileId() {
		return toolConsumerProfileId;
	}
	
	
	public void setToolConsumerProfileId(String toolConsumerProfileId) {
		this.toolConsumerProfileId = toolConsumerProfileId;
	}
	
	
	public List<String> getContexts() {
		return contexts;
	}
	
	
	public void setContexts(List<String> contexts) {
		this.contexts = contexts;
	}
	
	
	public String getType() {
		return type;
	}
	
	
	public void setType(String type) {
		this.type = type;
	}
	
	
	public String getId() {
		return id;
	}
	
	
	public void setId(String id) {
		this.id = id;
	}
	
	
	public ToolProfile getToolProfile() {
		return toolProfile;
	}
	
	
	public void setToolProfile(ToolProfile toolProfile) {
		this.toolProfile = toolProfile;
	}
	
	
	public SecurityContract getSecurityContract() {
		return securityContract;
	}
	
	
	public void setSecurityContract(SecurityContract securityContract) {
		this.securityContract = securityContract;
	}
	    
	public Map<String, List<String>> toMap() {
		Map<String, List<String>> ret = new HashMap<String, List<String>>();
		LTIUtil.setParameter(ret, "lti_version", getLti_version());
		LTIUtil.setParameter(ret, "toolConsumerProfileId", getToolConsumerProfileId());
		LTIUtil.setParameter(ret, "type", getType());
		LTIUtil.setParameter(ret, "id", getId());
		LTIUtil.setParameter(ret, "toolProfile", getToolProfile().toString());
		LTIUtil.setParameter(ret, "securityContract", getSecurityContract().toString());
		ret.put("contexts", getContexts());
		
		return ret;
	}

}
