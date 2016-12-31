package org.imsglobal.lti.model;

import java.util.List;
/**
 * Class to represent an LTI service object
 *
 * @author  Stephen P Vickers <svickers@imsglobal.org>
 * @copyright  IMS Global Learning Consortium Inc
 * @date  2016
 * @version 3.0.0
 * @license http://www.apache.org/licenses/LICENSE-2.0 Apache License, Version 2.0
 * translated to Java by David Block (dave@northcreek.ca)
 */

public class ServiceDefinition {

/**
 * Media types supported by service.
 *
 * @var array formats
 */
    public List formats;
/**
 * HTTP actions accepted by service.
 *
 * @var array actions
 */
    public List actions;
/**
 * ID of service.
 *
 * @var string id
 */
    public String id;
/**
 * URL for service requests.
 *
 * @var string endpoint
 */
    public String endpoint;

/**
 * Class constructor.
 *
 * @param array  formats   Array of media types supported by service
 * @param array  actions   Array of HTTP actions accepted by service
 * @param string id        ID of service (optional)
 * @param string endpoint  URL for service requests (optional)
 */

    public ServiceDefinition(List formats, List actions, String id , String endpoint )
    {

        this.setFormats(formats);
        this.setActions(actions);
        this.setId(id);
        this.setEndpoint(endpoint);

    }

    private void setId(String id) {

        this.id = id;

    }

	public List getFormats() {
		return formats;
	}

	public void setFormats(List formats) {
		this.formats = formats;
	}

	public List getActions() {
		return actions;
	}

	public void setActions(List actions) {
		this.actions = actions;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getId() {
		return id;
	}

}
