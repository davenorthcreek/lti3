package org.imsglobal.lti.toolProvider.mediaType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.imsglobal.lti.LTIUtil;
import org.imsglobal.lti.profile.ProfileMessage;

public class Message {
	/**
	 * Class to represent an LTI Message
	 *
	 * @author  Stephen P Vickers <svickers@imsglobal.org>
	 * @copyright  IMS Global Learning Consortium Inc
	 * @date  2016
	 * @version  3.0.0
	 * @license  GNU Lesser General Public License, version 3 (<http://www.gnu.org/licenses/lgpl.html>)
	 */

	private String type;
	private String path;
	private List<String> capabilities;
	private List<String> enabled_capabilities = new ArrayList<String>();
	private Map<String, List<String>> parameters;

	
	/**
	 * Class constructor.
	 *
	 * @param ProfileMessage message               Message object
	 * @param array   capabilitiesOffered   Capabilities offered
	 */
	    public Message(ProfileMessage pMessage, List<String> capabilitiesOffered)
	    {

	        this.type = pMessage.type;
	        this.path = pMessage.path;
	        for (String capability : pMessage.capabilities) {
	            if (capabilitiesOffered.contains(capability)) {
	                this.enabled_capabilities.add(capability);
	            }
	        }
	        for (String constantKey : pMessage.getConstants().keySet()) {
	        	List<String> constantValues = pMessage.getConstants().get(constantKey);
	        	for (String val : constantValues) {
	        		LTIUtil.setParameter(this.parameters, constantKey, val);
	        	}
	        }
	        for (String variableKey : pMessage.getVariables().keySet()) {
	        	List<String> variableValues = pMessage.getVariables().get(variableKey);
	        	for (String val : variableValues) {
	        		if (capabilitiesOffered.contains(val)) {
	        			LTIUtil.setParameter(this.parameters, variableKey, val);
	        		}
	            }
	        }

	    }


}
