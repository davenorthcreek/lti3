package imsglobal.model;
/**
 * Class to represent a generic item object
 *
 * @author  Stephen P Vickers <svickers@imsglobal.org>
 * @copyright  IMS Global Learning Consortium Inc
 * @date  2016
 * @version 3.0.0
 * @license http://www.apache.org/licenses/LICENSE-2.0 Apache License, Version 2.0
 * translated to Java by David Block (dave@northcreek.ca)
 */

public class Item {

/**
 * ID of item.
 *
 * @var string id
 */
    public String id;
/**
 * Name of item.
 *
 * @var string name
 */
    public String name;
/**
 * Description of item.
 *
 * @var string description
 */
    public String description;
/**
 * URL of item.
 *
 * @var string url
 */
    public String url;
/**
 * Version of item.
 *
 * @var string version
 */
    public String version;
/**
 * Timestamp of item.
 *
 * @var int timestamp
 */
    public int timestamp;

/**
 * Class constructor.
 *
 * @param String id           ID of item (optional)
 * @param String name         Name of item (optional)
 * @param String description  Description of item (optional)
 * @param String url          URL of item (optional)
 * @param String version      Version of item (optional)
 * @param int    timestamp    Timestamp of item (optional)
 */
    public Item(String id, String name, String description, String url, String version, int timestamp) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.url = url;
        this.version = version;
        this.timestamp = timestamp;

    }

}
