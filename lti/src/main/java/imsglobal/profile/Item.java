package imsglobal.profile;

import org.joda.time.DateTime;

public class Item {
	/**
	 * Class to represent a generic item object
	 *
	 * @author  Stephen P Vickers <svickers@imsglobal.org>
	 * @copyright  IMS Global Learning Consortium Inc
	 * @date  2016
	 * @version 3.0.0
	 * @license http://www.apache.org/licenses/LICENSE-2.0 Apache License, Version 2.0
	 */

	/**
	 * ID of item.
	 *
	 * @var string id
	 */
	    public String id = null;
	/**
	 * Name of item.
	 *
	 * @var string name
	 */
	    public String name = null;
	/**
	 * Description of item.
	 *
	 * @var string description
	 */
	    public String description = null;
	/**
	 * URL of item.
	 *
	 * @var string url
	 */
	    public String url = null;
	/**
	 * Version of item.
	 *
	 * @var string version
	 */
	    public String version = null;
	/**
	 * Timestamp of item.
	 *
	 * @var DateTime timestamp
	 */
	    public DateTime timestamp;

	/**
	 * Class constructor.
	 *
	 * @param string id           ID of item (optional)
	 * @param string name         Name of item (optional)
	 * @param string description  Description of item (optional)
	 * @param string url          URL of item (optional)
	 * @param string version      Version of item (optional)
	 * @param int    timestamp    Timestamp of item (optional)
	 */
	    
    public Item() {
    }

    public Item(String id, String name, String description, String url, String version, DateTime timestamp)
    {

        this.id = id;
        this.name = name;
        this.description = description;
        this.url = url;
        this.version = version;
        this.timestamp = timestamp;

    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public DateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(DateTime timestamp) {
		this.timestamp = timestamp;
	}

}
