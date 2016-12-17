package imsglobal.toolProvider.mediaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import imsglobal.profile.ProfileMessage;
import imsglobal.profile.ProfileResourceHandler;
import imsglobal.toolProvider.ToolProvider;

public class ResourceHandler {
	/**
	 * Class to represent an LTI Resource Handler
	 *
	 * @author  Stephen P Vickers <svickers@imsglobal.org>
	 * @copyright  IMS Global Learning Consortium Inc
	 * @date  2016
	 * @version  3.0.0
	 * @license  GNU Lesser General Public License, version 3 (<http://www.gnu.org/licenses/lgpl.html>)
	 */
	
	private Map<String, String> resourceType = new HashMap<String, String>();
	private Map<String, String> resourceName = new HashMap<String, String>();
	private Map<String, String> description = new HashMap<String, String>();
	private Map<String, String> iconInfo = new HashMap<String, String>();
	private List<Message> messages = new ArrayList<Message>();


	/**
	 * Class constructor.
	 *
	 * @param ToolProvider toolProvider   Tool Provider object
	 * @param ProfileResourceHandler resourceHandler   Resource handler object
	 */
	    public ResourceHandler(ToolProvider toolProvider, ProfileResourceHandler resourceHandler)
	    {

	        this.resourceType.put("code", resourceHandler.getItem().getId());
	        this.resourceName.put("default_value", resourceHandler.getItem().getName());
	        this.resourceName.put("key", resourceHandler.getItem().getId() + ".resource.name");
	        this.description.put("default_value", resourceHandler.getItem().getDescription());
	        this.description.put("key", resourceHandler.getItem().getId() + ".resource.description");
	        this.iconInfo.put("default_location.path", resourceHandler.getIcon());
	        this.iconInfo.put("key", resourceHandler.getItem().getId() + ".icon.path");
	        List<String> capsOffered = toolProvider.getConsumer().getCapabilitiesOffered();
	        for (ProfileMessage pmessage : resourceHandler.getRequiredMessages()) {
	            this.messages.add(new Message(pmessage, capsOffered));
	        }
	        for (ProfileMessage pmessage : resourceHandler.getOptionalMessages()) {
	        	if (capsOffered.contains(pmessage.getType())) {
	        		this.messages.add(new Message(pmessage, capsOffered));
	        	}
	        }

	    }

}
