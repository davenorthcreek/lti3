package imsglobal.model;

import java.util.List;
/**
 * Class to represent a resource handler object
 *
 * @author  Stephen P Vickers <svickers@imsglobal.org>
 * @copyright  IMS Global Learning Consortium Inc
 * @date  2016
 * @version 3.0.0
 * @license http://www.apache.org/licenses/LICENSE-2.0 Apache License, Version 2.0
 * translated to Java by David Block (dave@northcreek.ca)
 */

class ResourceHandler
{

/**
 * General details of resource handler.
 *
 * @var Item item
 */
    public Item item;
/**
 * URL of icon.
 *
 * @var string icon
 */
    public String icon;
/**
 * Required Message objects for resource handler.
 *
 * @var array requiredMessages
 */
    public List requiredMessages;
/**
 * Optional Message objects for resource handler.
 *
 * @var array optionalMessages
 */
    public List optionalMessages;

/**
 * Class constructor.
 *
 * @param Item      item      General details of resource handler
 * @param string    icon      URL of icon
 * @param array     requiredMessages  Array of required Message objects for resource handler
 * @param array     optionalMessages  Array of optional Message objects for resource handler
 */
    public ResourceHandler(Item item, String icon, List requiredMessages, List optionalMessages)
    {

        this.setItem(item);
        this.setIcon(icon);
        this.setRequiredMessages(requiredMessages);
        this.setOptionalMessages(optionalMessages);

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

public List getRequiredMessages() {
	return requiredMessages;
}

public void setRequiredMessages(List requiredMessages) {
	this.requiredMessages = requiredMessages;
}

public List getOptionalMessages() {
	return optionalMessages;
}

public void setOptionalMessages(List optionalMessages) {
	this.optionalMessages = optionalMessages;
}

}
