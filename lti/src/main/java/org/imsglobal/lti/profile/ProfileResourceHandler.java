package org.imsglobal.lti.profile;

import java.util.ArrayList;
import java.util.List;

public class ProfileResourceHandler {

/**
 * Class to represent a resource handler object
 *
 * @author  Stephen P Vickers <svickers@imsglobal.org>
 * @copyright  IMS Global Learning Consortium Inc
 * @date  2016
 * @version 3.0.0
 * @license http://www.apache.org/licenses/LICENSE-2.0 Apache License, Version 2.0
 */

/**
 * General details of resource handler.
 *
 * @var Item item
 */
    public Item item = null;
/**
 * URL of icon.
 *
 * @var string icon
 */
    public String icon = null;
/**
 * Required Message objects for resource handler.
 *
 * @var array requiredMessages
 */
    public List<ProfileMessage> requiredMessages = new ArrayList<ProfileMessage>();
/**
 * Optional Message objects for resource handler.
 *
 * @var array optionalMessages
 */
    public List<ProfileMessage> optionalMessages = new ArrayList<ProfileMessage>();

/**
 * Class constructor.
 *
 * @param Item      item      General details of resource handler
 * @param string    icon      URL of icon
 * @param array     requiredMessages  Array of required Message objects for resource handler
 * @param array     optionalMessages  Array of optional Message objects for resource handler
 */
    public ProfileResourceHandler(
    		Item item, 
    		String icon, 
    		List<ProfileMessage> requiredMessages, 
    		List<ProfileMessage> optionalMessages)
    {

        this.item = item;
        this.icon = icon;
        this.requiredMessages = requiredMessages;
        this.optionalMessages = optionalMessages;

	}
	
	public Item getItem() {
		return item;
	}
	
	public void setItem(Item item) {
		this.item = item;
	}
	
	public String getIcon() {
		return icon;
	}
	
	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	public List<ProfileMessage> getRequiredMessages() {
		return requiredMessages;
	}
	
	public void setRequiredMessages(List<ProfileMessage> requiredMessages) {
		this.requiredMessages = requiredMessages;
	}
	
	public List<ProfileMessage> getOptionalMessages() {
		return optionalMessages;
	}
	
	public void setOptionalMessages(List<ProfileMessage> optionalMessages) {
		this.optionalMessages = optionalMessages;
	}

}
