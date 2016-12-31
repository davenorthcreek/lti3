package imsglobal.toolProvider;

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
	 * Consumer key value.
	 *
	 * @var string consumerKey
	 */
	    public String consumerKey = null;
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
	    public boolean approved;

	/**
	 * Class constructor.
	 */
	    public ResourceLinkShare()
	    {
	    }

	public String getConsumerKey() {
		return consumerKey;
	}

	public void setConsumerKey(String consumerKey) {
		this.consumerKey = consumerKey;
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

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}
	

}
