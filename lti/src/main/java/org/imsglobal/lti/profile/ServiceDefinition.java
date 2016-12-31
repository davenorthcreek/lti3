package org.imsglobal.lti.profile;

import java.net.URL;
import java.util.List;

public class ServiceDefinition {

/**
 * Class to represent an LTI service object
 *
 * @author  Stephen P Vickers <svickers@imsglobal.org>
 * @copyright  IMS Global Learning Consortium Inc
 * @date  2016
 * @version 3.0.0
 * @license http://www.apache.org/licenses/LICENSE-2.0 Apache License, Version 2.0
 */


/**
 * Media types supported by service.
 *
 * @var array formats
 */
    public List<String> formats = null;
/**
 * HTTP actions accepted by service.
 *
 * @var array actions
 */
    public List<String> actions = null;
/**
 * ID of service.
 *
 * @var string id
 */
    public String id = null;
/**
 * URL for service requests.
 *
 * @var string endpoint
 */
    public URL endpoint = null;

/**
 * Class constructor.
 *
 * @param array  formats   Array of media types supported by service
 * @param array  actions   Array of HTTP actions accepted by service
 * @param string id        ID of service (optional)
 * @param string endpoint  URL for service requests (optional)
 */

    public ServiceDefinition(
    		List<String> formats, 
    		List<String> actions, 
    		String id, 
    		URL endpoint)
    {

        this.formats = formats;
        this.actions = actions;
        this.id = id;
        this.endpoint = endpoint;

    }

	public List<String> getFormats() {
		return formats;
	}
	
	public void setFormats(List<String> formats) {
		this.formats = formats;
	}
	
	public List<String> getActions() {
		return actions;
	}
	
	public void setActions(List<String> actions) {
		this.actions = actions;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public URL getEndpoint() {
		return endpoint;
	}
	
	public void setEndpoint(URL endpoint) {
		this.endpoint = endpoint;
	}
    
    


}
