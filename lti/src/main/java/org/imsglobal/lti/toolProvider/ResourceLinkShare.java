package org.imsglobal.lti.toolProvider;

public class ResourceLinkShare {
	/**
	 * Class to represent a tool consumer resource link share
	 *
	 * @author  Stephen P Vickers <svickers@imsglobal.org>
	 * @copyright  IMS Global Learning Consortium Inc
	 * @date  2016
	 * @version 3.0.0
	 * @license http://www.apache.org/licenses/LICENSE-2.0 Apache License, Version 2.0
	 */

	/**
	 * Resource link ID value.
	 *
	 * @var string resourceLinkId
	 */
	    public String resourceLinkId = null;
	/**
	 * Title of sharing context.
	 *
	 * @var string title
	 */
	    public String title = null;
	/**
	 * Whether sharing request is to be automatically approved on first use.
	 *
	 * @var boolean approved
	 */
	    public Boolean approved;

	/**
	 * Class constructor.
	 */
	    public ResourceLinkShare()
	    {
	    }

	
	public String getResourceLinkId() {
		return resourceLinkId;
	}

	public void setResourceLinkId(String resourceLinkId) {
		this.resourceLinkId = resourceLinkId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Boolean isApproved() {
		return approved;
	}

	public void setApproved(Boolean approved) {
		this.approved = approved;
	}
	

}
