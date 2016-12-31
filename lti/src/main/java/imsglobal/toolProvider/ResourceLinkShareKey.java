package imsglobal.toolProvider;

import org.joda.time.Duration;
import org.joda.time.DateTime;

import imsglobal.toolProvider.dataConnector.DataConnector;

public class ResourceLinkShareKey {
	/**
	 * Class to represent a tool consumer resource link share key
	 *
	 * @author  Stephen P Vickers <svickers@imsglobal.org>
	 * @copyright  IMS Global Learning Consortium Inc
	 * @date  2016
	 * @version 3.0.2
	 * @license http://www.apache.org/licenses/LICENSE-2.0 Apache License, Version 2.0
	 */


	/**
	 * Maximum permitted life for a share key value.
	 */
	    public static final int MAX_SHARE_KEY_LIFE = 168;  // in hours (1 week)
	/**
	 * Default life for a share key value.
	 */
	    public static final int DEFAULT_SHARE_KEY_LIFE = 24;  // in hours
	/**
	 * Minimum length for a share key value.
	 */
	    public static final int MIN_SHARE_KEY_LENGTH = 5;
	/**
	 * Maximum length for a share key value.
	 */
	    public static final int MAX_SHARE_KEY_LENGTH = 32;

	    
    /**
     * Consumer key for resource link being shared.
     */
      private String primaryConsumerKey = null;
      
	/**
	 * ID for resource link being shared.
	 *
	 * @var string resourceLinkId
	 */
	    private String resourceLinkId = null;
	/**
	 * Length of share key.
	 *
	 * @var int length
	 */
	    private int length;
	/**
	 * Life of share key.
	 *
	 * @var int life
	 */
	    private long life;  // in hours
	/**
	 * Whether the sharing arrangement should be automatically approved when first used.
	 *
	 * @var boolean autoApprove
	 */
	    private boolean autoApprove = false;
	/**
	 * Date/time when the share key expires.
	 *
	 * @var int expires
	 */
	    public DateTime expires = null;

	/**
	 * Share key value.
	 *
	 * @var string id
	 */
	    private String id = null;
	/**
	 * Data connector.
	 *
	 * @var DataConnector dataConnector
	 */
	    private DataConnector dataConnector = null;

	/**
	 * Class constructor.
	 *
	 * @param ResourceLink resourceLink  Resource_Link object
	 * @param string       id      Value of share key (optional, default is null)
	 */
	    
	    public ResourceLinkShareKey(ResourceLink resourceLink) {
	    	this.initialize();
	        this.dataConnector = resourceLink.getDataConnector();
	        this.resourceLinkId = String.valueOf(resourceLink.getRecordId());
	    }
	    
	    public ResourceLinkShareKey(ResourceLink resourceLink, String id)
	    {

	        this.initialize();
	        this.dataConnector = resourceLink.getDataConnector();
	        this.resourceLinkId = String.valueOf(resourceLink.getRecordId());
	        this.id = id;
	        this.load();

	    }

	/**
	 * Initialise the resource link share key.
	 */
	    public void initialize()
	    {

	        this.autoApprove = false;
	        this.expires = null;

	    }

	/**
	 * Initialise the resource link share key.
	 *
	 * Pseudonym for initialize().
	 */
	    public void initialise()
	    {

	        this.initialize();

	    }

	/**
	 * Save the resource link share key to the database.
	 *
	 * @return boolean True if the share key was successfully saved
	 */
	    public boolean save()
	    {

	        if (life == 0) {
	            life = DEFAULT_SHARE_KEY_LIFE;
	        } else {
	            life = Math.max(Math.min(life, MAX_SHARE_KEY_LIFE), 0);
	        }
	        this.expires = DateTime.now().plusHours((int) life);
	        if (id == null) {
	            if (length == 0) {
	                this.length = MAX_SHARE_KEY_LENGTH;
	            } else {
	                this.length = Math.max(Math.min(length, MAX_SHARE_KEY_LENGTH), MIN_SHARE_KEY_LENGTH);
	            }
	            this.id = DataConnector.getRandomString(length);
	        }

	        return this.dataConnector.saveResourceLinkShareKey(this);

	    }

	/**
	 * Delete the resource link share key from the database.
	 *
	 * @return boolean True if the share key was successfully deleted
	 */
	    public boolean delete()
	    {

	        return this.dataConnector.deleteResourceLinkShareKey(this);

	    }

	/**
	 * Get share key value.
	 *
	 * @return string Share key value
	 */
	    public String getId()
	    {

	        return this.id;

	    }

	///
	///  PRIVATE METHOD
	///

	/**
	 * Load the resource link share key from the database.
	 */
	    private void load()
	    {

	        initialize();
	        dataConnector.loadResourceLinkShareKey(this);
	        if (id != null) {
	            length = String.valueOf(id).length();
	        }
	        if (expires != null) {
	        	Duration duration = new Duration(expires, DateTime.now());
	        	life = duration.getStandardHours();
	        }

	    }

	public String getResourceLinkId() {
		return resourceLinkId;
	}

	public void setResourceLinkId(String resourceLinkId) {
		this.resourceLinkId = resourceLinkId;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public long getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
	}

	public boolean isAutoApprove() {
		return autoApprove;
	}

	public void setAutoApprove(boolean autoApprove) {
		this.autoApprove = autoApprove;
	}

	public DateTime getExpires() {
		return expires;
	}

	public void setExpires(DateTime expires) {
		this.expires = expires;
	}

	public DataConnector getDataConnector() {
		return dataConnector;
	}

	public void setDataConnector(DataConnector dataConnector) {
		this.dataConnector = dataConnector;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPrimaryConsumerKey() {
		return primaryConsumerKey;
	}

	public void setPrimaryConsumerKey(String primaryConsumerKey) {
		this.primaryConsumerKey = primaryConsumerKey;
	}
	    
	

}
