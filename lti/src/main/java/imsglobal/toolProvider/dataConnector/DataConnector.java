package imsglobal.toolProvider.dataConnector;

import java.util.ArrayList;
import java.util.List;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.joda.time.DateTime;

import imsglobal.toolProvider.ConsumerNonce;
import imsglobal.toolProvider.Context;
import imsglobal.toolProvider.ResourceLink;
import imsglobal.toolProvider.ToolConsumer;
import imsglobal.toolProvider.ToolProxy;
import imsglobal.toolProvider.User;

/**
 * Class to provide a connection to a persistent store for LTI objects
 *
 * This class assumes no data persistence - it should be extended for specific database connections.
 *
 * @author  Stephen P Vickers <svickers@imsglobal.org>
 * @copyright  IMS Global Learning Consortium Inc
 * @date  2016
 * @version 3.0.0
 * @license http://www.apache.org/licenses/LICENSE-2.0 Apache License, Version 2.0
 * translated to Java by David Block (dave@northcreek.ca)
 */

public class DataConnector {
	/**
	 * Default name for database table used to store tool consumers.
	 */
	    public static String CONSUMER_TABLE_NAME = "lti2_consumer";
	/**
	 * Default name for database table used to store pending tool proxies.
	 */
	    public static String TOOL_PROXY_TABLE_NAME = "lti2_tool_proxy";
	/**
	 * Default name for database table used to store contexts.
	 */
	    public static String CONTEXT_TABLE_NAME = "lti2_context";
	/**
	 * Default name for database table used to store resource links.
	 */
	    public static String RESOURCE_LINK_TABLE_NAME = "lti2_resource_link";
	/**
	 * Default name for database table used to store users.
	 */
	    public static String USER_RESULT_TABLE_NAME = "lti2_user_result";
	/**
	 * Default name for database table used to store resource link share keys.
	 */
	    public static String RESOURCE_LINK_SHARE_KEY_TABLE_NAME = "lti2_share_key";
	/**
	 * Default name for database table used to store nonce values.
	 */
	    public static String NONCE_TABLE_NAME = "lti2_nonce";

	/**
	 * Database object.
	 *
	 * @var object $db
	 */
	    protected Object db = null;
	/**
	 * Prefix for database table names.
	 *
	 * @var string $dbTableNamePrefix
	 */
	    protected String dbTableNamePrefix = "";
	/**
	 * SQL date format (default = "Y-m-d")
	 *
	 * @var string $dateFormat
	 */
	    protected String dateFormat = "Y-m-d";
	/**
	 * SQL time format (default = "H:i:s")
	 *
	 * @var string $timeFormat
	 */
	    protected String timeFormat = "H:i:s";
	
	    
	    private ToolProxy methods;

	/**
	 * Class constructor
	 *
	 * @param object $db                 Database connection object
	 * @param string $dbTableNamePrefix  Prefix for database table names (optional, default is none)
	 */
	    public DataConnector(Object db, String dbTableNamePrefix)
	    {
	    	setDb(db);
	    	setDbTableNamePrefix(dbTableNamePrefix);

	    }

	//
	//  ToolConsumer methods
	//

	public Object getDb() {
		return db;
	}

	public void setDb(Object db) {
		this.db = db;
	}

	public String getDbTableNamePrefix() {
		return dbTableNamePrefix;
	}

	public void setDbTableNamePrefix(String dbTableNamePrefix) {
		this.dbTableNamePrefix = dbTableNamePrefix;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getTimeFormat() {
		return timeFormat;
	}

	public void setTimeFormat(String timeFormat) {
		this.timeFormat = timeFormat;
	}

	public ToolProxy getMethods() {
		return methods;
	}

	public void setMethods(ToolProxy methods) {
		this.methods = methods;
	}

	/**
	 * Load tool consumer object.
	 *
	 * @param ToolConsumer $consumer ToolConsumer object
	 *
	 * @return boolean True if the tool consumer object was successfully loaded
	 */
	    public boolean loadToolConsumer(ToolConsumer consumer)
	    {
	    	consumer.setSecret("secret");
	        consumer.setEnabled(true);
	        DateTime now = DateTime.now();
	        consumer.setCreated(now);
	        consumer.setUpdated(now);

	        return true;

	    }

	/**
	 * Save tool consumer object.
	 *
	 * @param ToolConsumer $consumer Consumer object
	 *
	 * @return boolean True if the tool consumer object was successfully saved
	 */
	    public boolean saveToolConsumer(ToolConsumer consumer)
	    {
	    	
	        consumer.setUpdated(DateTime.now());

	        return true;

	    }

	/**
	 * Delete tool consumer object.
	 *
	 * @param ToolConsumer $consumer Consumer object
	 *
	 * @return boolean True if the tool consumer object was successfully deleted
	 */
	    public boolean deleteToolConsumer(ToolConsumer consumer)
	    {

	        consumer.initialize();

	        return true;

	    }

	/**
	 * Load tool consumer objects.
	 *
	 * @return array Array of all defined ToolConsumer objects
	 */
	    public List<ToolConsumer> getToolConsumers() {
	        return new ArrayList<ToolConsumer>();
	    }


	/**
	 * ToolProxy methods
	 */

	/**
	 * Load tool proxy object.
	 *
	 * @param ToolProxy $toolProxy ToolProxy object
	 *
	 * @return boolean True if the tool proxy object was successfully loaded
	 */
	    public boolean loadToolProxy(ToolProxy toolProxy)
	    {

	        DateTime now = DateTime.now();
	        toolProxy.setCreated(now);
	        toolProxy.setUpdated(now);

	        return true;

	    }

	/**
	 * Save tool proxy object.
	 *
	 * @param ToolProxy $toolProxy ToolProxy object
	 *
	 * @return boolean True if the tool proxy object was successfully saved
	 */
	    public boolean saveToolProxy(ToolProxy toolProxy)
	    {

	        toolProxy.setUpdated(DateTime.now());

	        return true;

	    }

	/**
	 * Delete tool proxy object.
	 *
	 * @param ToolProxy $toolProxy ToolProxy object
	 *
	 * @return boolean True if the tool proxy object was successfully deleted
	 */
	    public boolean deleteToolProxy(ToolProxy toolProxy)
	    {

	        toolProxy.initialize();

	        return true;

	    }

	//
	//  Context methods
	//

	/**
	 * Load context object.
	 *
	 * @param Context $context Context object
	 *
	 * @return boolean True if the context object was successfully loaded
	 */
	    public boolean loadContext(Context context)
	    {

	        DateTime now = DateTime.now();
	        context.setCreated(now);
	        context.setUpdated(now);

	        return true;

	    }

	/**
	 * Save context object.
	 *
	 * @param Context $context Context object
	 *
	 * @return boolean True if the context object was successfully saved
	 */
	    public boolean saveContext(Context context)
	    {

	        context.setUpdated(DateTime.now());

	        return true;

	    }

	/**
	 * Delete context object.
	 *
	 * @param Context $context Context object
	 *
	 * @return boolean True if the Context object was successfully deleted
	 */
	    public boolean deleteContext(Context context)
	    {

	        context.initialize();

	        return true;

	    }

	//
	//  ResourceLink methods
	//

	/**
	 * Load resource link object.
	 *
	 * @param ResourceLink $resourceLink Resource_Link object
	 *
	 * @return boolean True if the resource link object was successfully loaded
	 */
	    public boolean loadResourceLink(ResourceLink resourceLink)
	    {

	        DateTime now = DateTime.now();
	        resourceLink.setCreated(now);
	        resourceLink.setUpdated(now);

	        return true;

	    }

	/**
	 * Save resource link object.
	 *
	 * @param ResourceLink $resourceLink Resource_Link object
	 *
	 * @return boolean True if the resource link object was successfully saved
	 */
	    public boolean saveResourceLink(ResourceLink resourceLink)
	    {

	        resourceLink.setUpdated(DateTime.now());

	        return true;

	    }

	/**
	 * Delete resource link object.
	 *
	 * @param ResourceLink $resourceLink Resource_Link object
	 *
	 * @return boolean True if the resource link object was successfully deleted
	 */
	    public boolean deleteResourceLink(ResourceLink resourceLink)
	    {

	        resourceLink.initialize();

	        return true;

	    }

	/**
	 * Get array of user objects.
	 *
	 * Obtain an array of User objects for users with a result sourcedId.  The array may include users from other
	 * resource links which are sharing this resource link.  It may also be optionally indexed by the user ID of a specified scope.
	 *
	 * @param ResourceLink $resourceLink      Resource link object
	 * @param boolean     $localOnly True if only users within the resource link are to be returned (excluding users sharing this resource link)
	 * @param int         $idScope     Scope value to use for user IDs
	 *
	 * @return array Array of User objects
	 */
	    public List<User> getUserResultSourcedIDsResourceLink(ResourceLink resourceLink, boolean localOnly, int ID_SCOPE)
	    {

	        return new ArrayList<User>();

	    }

	/**
	 * Get array of shares defined for this resource link.
	 *
	 * @param ResourceLink resourceLink Resource_Link object
	 *
	 * @return List of ResourceLinkShare objects
	 */
	    public List<ResourceLink> getSharesResourceLink(ResourceLink resourceLink)
	    {

	        return new ArrayList<ResourceLink>();

	    }

	//
	//  ConsumerNonce methods
	//

	/**
	 * Load nonce object.
	 *
	 * @param ConsumerNonce $nonce Nonce object
	 *
	 * @return boolean True if the nonce object was successfully loaded
	 */
	    public boolean loadConsumerNonce(ConsumerNonce nonce)
	    {
	        return false;  // assume the nonce does not already exist

	    }

	/**
	 * Save nonce object.
	 *
	 * @param ConsumerNonce $nonce Nonce object
	 *
	 * @return boolean True if the nonce object was successfully saved
	 */
	    public boolean saveConsumerNonce(ConsumerNonce nonce)
	    {

	        return true;

	    }

	//
	//  ResourceLinkShareKey methods
	//

	/**
	 * Load resource link share key object.
	 *
	 * @param ResourceLinkShareKey $shareKey Resource_Link share key object
	 *
	 * @return boolean True if the resource link share key object was successfully loaded
	 */
	    public boolean loadResourceLinkShareKey(String shareKey)
	    {

	        return true;

	    }

	/**
	 * Save resource link share key object.
	 *
	 * @param ResourceLinkShareKey $shareKey Resource link share key object
	 *
	 * @return boolean True if the resource link share key object was successfully saved
	 */
	    public boolean saveResourceLinkShareKey(String shareKey)
	    {

	        return true;

	    }

	/**
	 * Delete resource link share key object.
	 *
	 * @param ResourceLinkShareKey $shareKey Resource link share key object
	 *
	 * @return boolean True if the resource link share key object was successfully deleted
	 */
	    public boolean deleteResourceLinkShareKey(String shareKey)
	    {

	        return true;

	    }

	//
	//  User methods
	//

	/**
	 * Load user object.
	 *
	 * @param User $user User object
	 *
	 * @return boolean True if the user object was successfully loaded
	 */
	    public boolean loadUser(User user)
	    {

	        DateTime now = DateTime.now();
	        user.setCreated(now);
	        user.setUpdated(now);

	        return true;

	    }

	/**
	 * Save user object.
	 *
	 * @param User $user User object
	 *
	 * @return boolean True if the user object was successfully saved
	 */
	    public boolean saveUser(User user)
	    {

	        user.setUpdated(DateTime.now());

	        return true;

	    }

	/**
	 * Delete user object.
	 *
	 * @param User $user User object
	 *
	 * @return boolean True if the user object was successfully deleted
	 */
	    public boolean deleteUser(User user)
	    {

	        user.initialize();

	        return true;

	    }

	//
	//  Other methods
	//

	/**
	 * Return a hash of a consumer key for values longer than 255 characters.
	 *
	 * @param string $key
	 * @return string
	 */
	    protected static String getConsumerKey(String key)
	    {

	        int len = key.length();
        	StringBuilder sb = new StringBuilder();
	        if (len > 255) {
	        	MessageDigest md;
				try {
					md = MessageDigest.getInstance("SHA-512");
		        	byte[] bytes = md.digest(key.getBytes("UTF-8"));
		            for(int i=0; i< bytes.length ;i++){
		               sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		            }
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e2) {
					e2.printStackTrace();
				}
	        }

	        return sb.toString();

	    }



	/**
	 * Generate a random string.
	 *
	 * The generated string will only comprise letters (upper- and lower-case) and digits.
	 *
	 * @param int $length Length of string to be generated (optional, default is 8 characters)
	 *
	 * @return string Random string
	 */
	    public static String getRandomString() {
	    	return getRandomString(8);
	    }
	    
	    public static String getRandomString(int length) {
	    	

	        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

	        String value = "";
	        int charsLength = chars.length() - 1;

	        for (int i = 1 ; i <= length; i++) {
	        	int randomIndex = (int)(Math.floor(Math.random() * charsLength));
	            value += chars.substring(randomIndex, randomIndex + 1);
	        }

	        return value;

	    }

	/**
	 * Quote a string for use in a database query.
	 *
	 * Any single quotes in the value passed will be replaced with two single quotes.  If a null value is passed, a string
	 * of "null" is returned (which will never be enclosed in quotes irrespective of the value of the $addQuotes parameter.
	 *
	 * @param string $value Value to be quoted
	 * @param bool $addQuotes If true the returned string will be enclosed in single quotes (optional, default is true)
	 * @return string The quoted string.
	 *
	    static function quoted($value, $addQuotes = true)
	    {

	        if (is_null($value)) {
	            $value = "null";
	        } else {
	            $value = str_replace("\"", "\"\"", $value);
	            if ($addQuotes) {
	                $value = ""{$value}"";
	            }
	        }

	        return $value;

	    }
	    
	  */
}
