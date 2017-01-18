package org.imsglobal.lti.toolProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.imsglobal.lti.LTIMessage;
import org.imsglobal.lti.toolProvider.dataConnector.DataConnector;
import org.imsglobal.lti.toolProvider.service.Membership;
import org.imsglobal.lti.toolProvider.service.Service;
import org.imsglobal.lti.toolProvider.service.ToolSettings;
import org.joda.time.DateTime;

public class Context implements LTISource {
	
	/**
	 * Class to represent a tool consumer context
	 *
	 * @author  Stephen P Vickers <svickers@imsglobal.org>
	 * @copyright  IMS Global Learning Consortium Inc
	 * @date  2016
	 * @version 3.0.2
	 * @license http://www.apache.org/licenses/LICENSE-2.0 Apache License, Version 2.0
	 */

	/**
	 * Context ID as supplied in the last connection request.
	 *
	 * @var string ltiContextId
	 */
	    private String ltiContextId = null;
	/**
	 * Context title.
	 *
	 * @var string title
	 */
	    private String title = null;
	/**
	 * Setting values (LTI parameters, custom parameters and local parameters).
	 *
	 * @var array settings
	 */
	    private Map<String, String> settings = new HashMap<String, String>();
	/**
	 * Date/time when the object was created.
	 *
	 * @var int created
	 */
	    private DateTime created = null;
	/**
	 * Date/time when the object was last updated.
	 *
	 * @var int updated
	 */
	    private DateTime updated = null;

	/**
	 * Tool Consumer for this context.
	 *
	 * @var ToolConsumer consumer
	 */
	    private ToolConsumer consumer = null;
	/**
	 * Tool Consumer ID for this context.
	 *
	 * @var int consumerId
	 */
	    private Integer consumerId = null;
	/**
	 * ID for this context.
	 *
	 * @var String id
	 */
	    private String id = null;
	/**
	 * Whether the settings value have changed since last saved.
	 *
	 * @var boolean settingsChanged
	 */
	    private boolean settingsChanged = false;
	/**
	 * Data connector object or string.
	 *
	 * @var mixed dataConnector
	 */
	    private DataConnector dataConnector = null;

	/**
	 * Class constructor.
	 */
	    public Context()
	    {

	        this.initialize();

	    }

	/**
	 * Initialise the context.
	 */
	    public void initialize()
	    {

	        this.setTitle("");
	        this.settings = new HashMap<String, String>();
	        this.created = null;
	        this.updated = null;

	    }

	/**
	 * Initialise the context.
	 *
	 * Pseudonym for initialize().
	 */
	    public void initialise()
	    {

	        this.initialize();

	    }

	/**
	 * Save the context to the database.
	 *
	 * @return boolean True if the context was successfully saved.
	 */
	    public boolean save()
	    {

	        boolean ok = this.getDataConnector().saveContext(this);
	        if (ok) {
	            this.settingsChanged = false;
	        }

	        return ok;

	    }

	/**
	 * Delete the context from the database.
	 *
	 * @return boolean True if the context was successfully deleted.
	 */
	    public boolean delete()
	    {

	        return this.getDataConnector().deleteContext(this);

	    }

	/**
	 * Get tool consumer.
	 *
	 * @return ToolConsumer Tool consumer object for this context.
	 */
	    public ToolConsumer getConsumer()
	    {

	        if (this.consumer == null) {
	            this.consumer = ToolConsumer.fromRecordId(this.consumerId, this.getDataConnector());
	        }

	        return this.consumer;

	    }
	/**
	 * Set tool consumer ID.
	 *
	 * @param int consumerId  Tool Consumer ID for this resource link.
	 */
	    public void setConsumerId(Integer consumerId)
	    {

	        this.consumer = null;
	        this.consumerId = consumerId;

	    }

	/**
	 * Get tool consumer key.
	 *
	 * @return string Consumer key value for this context.
	 */
	    public String getKey()
	    {

	        return this.getConsumer().getKey();

	    }

	/**
	 * Get context ID.
	 *
	 * @return string ID for this context.
	 */
	    public String getId()
	    {

	        return this.ltiContextId;

	    }

	/**
	 * Get the context record ID.
	 *
	 * @return int Context record ID value
	 */
	    public String getRecordId()
	    {

	        return this.id;

	    }

	/**
	 * Sets the context record ID.
	 *
	 * @return int id  Context record ID value
	 */
	    public void setRecordId(String id)
	    {

	        this.id = id;

	    }

	/**
	 * Get the data connector.
	 *
	 * @return mixed Data connector object or string
	 */
	    public DataConnector getDataConnector()
	    {

	        return this.dataConnector;

	    }

	/**
	 * Get a setting value.
	 *
	 * @param string name    Name of setting
	 * @param string default Value to return if the setting does not exist (optional, default is an empty string)
	 *
	 * @return string Setting value
	 */
	    
	    public String getSetting(String name) {
	    	return getSetting(name, "");
	    }
	    
	    public String getSetting(String name, String dflt)
	    {
	    	String value = dflt;
	        if (this.settings.containsKey(name)) {
	            value = this.settings.get(name);
	        }

	        return value;

	    }

	/**
	 * Set a setting value.
	 *
	 * @param string name  Name of setting
	 * @param string value Value to set, use an empty value to delete a setting (optional, default is null)
	 */
	    public void setSetting(String name, String value)
	    {

	        String old_value = this.getSetting(name);
	        if (value != old_value) {
	            if (StringUtils.isNotEmpty(value)) {
	                this.settings.put(name, value);
	            } else {
	            	this.settings.remove(name);
	            }
	            this.settingsChanged = true;
	        }

	    }

	/**
	 * Get an array of all setting values.
	 *
	 * @return array Associative array of setting values
	 */
	    public Map<String, String> getSettings()
	    {

	        return this.settings;

	    }

	/**
	 * Set an array of all setting values.
	 *
	 * @param array settings Associative array of setting values
	 */
	    public void setSettings(Map<String, String> settings)
	    {

	        this.settings = settings;

	    }

	/**
	 * Save setting values.
	 *
	 * @return boolean True if the settings were successfully saved
	 */
	    public boolean saveSettings()
	    {
	    	boolean ok = false;
	        if (this.settingsChanged) {
	            ok = this.save();
	        } else {
	            ok = true;
	        }

	        return ok;

	    }

	/**
	 * Check if the Tool Settings service is supported.
	 *
	 * @return boolean True if this context supports the Tool Settings service
	 */
	    public boolean hasToolSettingsService()
	    {

	        String url = this.getSetting("custom_context_setting_url");

	        return StringUtils.isNotEmpty(url);

	    }

	/**
	 * Get Tool Settings.
	 *
	 * @param int      mode       Mode for request (optional, default is current level only)
	 * @param boolean  simple     True if all the simple media type is to be used (optional, default is true)
	 *
	 * @return mixed The array of settings if successful, otherwise false
	 */
	    
	    public Map<String, List<String>> getToolSettings() {
	    	return getToolSettings(ToolSettings.MODE_CURRENT_LEVEL, true);
	    }
	    
	    public Map<String, List<String>> getToolSettings(int mode) {
	    	return getToolSettings(mode, true);
	    }
	    
	    public Map<String, List<String>> getToolSettings(int mode, boolean simple)
	    {
	    	
	        String url = this.getSetting("custom_context_setting_url");
	        ToolSettings service = new ToolSettings(this, url, simple);
	        Map<String, List<String>> response = service.get(mode);

	        return response;

	    }

	/**
	 * Perform a Tool Settings service request.
	 *
	 * @param array    settings   An associative array of settings (optional, default is none)
	 *
	 * @return boolean True if action was successful, otherwise false
	 */
	    
	    public boolean setToolSettings() {
	    	String url = this.getSetting("custom_context_setting_url");
	        ToolSettings service = new ToolSettings(this, url);
	        LTIMessage response = service.set(null);

	        return (response != null);

	    }
	    public boolean setToolSettings(Map<String, List<String>> settings)
	    {

	        String url = this.getSetting("custom_context_setting_url");
	        ToolSettings service = new ToolSettings(this, url);
	        LTIMessage response = service.set(settings);

	        return (response != null);

	    }

	/**
	 * Check if the Membership service is supported.
	 *
	 * @return boolean True if this context supports the Membership service
	 */
	    public boolean hasMembershipService()
	    {

	        String url = this.getSetting("custom_context_memberships_url");

	        return StringUtils.isNotEmpty(url);

	    }

	/**
	 * Get Memberships.
	 *
	 * @return mixed The array of User objects if successful, otherwise false
	 */
	    public List<User> getMembership()
	    {

	        String url = this.getSetting("custom_context_memberships_url");
	        Membership service = new Membership(this.getConsumer(), url);
	        List<User> response = service.get();

	        return response;

	    }

	/**
	 * Load the context from the database.
	 *
	 * @param int             id               Record ID of context
	 * @param DataConnector   dataConnector    Database connection object
	 *
	 * @return Context    Context object
	 */
	    public static Context fromRecordId(String id, DataConnector dataConnector)
	    {

	        Context context = new Context();
	        context.setDataConnector(dataConnector);
	        context.load(id);

	        return context;

	    }

	private void setDataConnector(DataConnector dataConnector2) {
		this.dataConnector = dataConnector2;
	}

	/**
	 * Class constructor from consumer.
	 *
	 * @param ToolConsumer consumer Consumer instance
	 * @param string ltiContextId LTI Context ID value
	 * @return Context
	 */
	    public static Context fromConsumer(ToolConsumer consumer, String ltiContextId)
	    {

	        Context context = new Context();
	        context.consumer = consumer;
	        context.dataConnector = consumer.getDataConnector();
	        context.ltiContextId = ltiContextId;
	        if (StringUtils.isNotEmpty(ltiContextId)) {
	            context.load();
	        }

	        return context;

	    }

	//
	//  PRIVATE METHODS
	//

	/**
	 * Load the context from the database.
	 *
	 * @param int id     Record ID of context (optional, default is null)
	 *
	 * @return boolean True if context was successfully loaded
	 */
    private boolean load(String id)
    {

        this.initialize();
        this.id = id;
        return this.getDataConnector().loadContext(this);

    }

	private boolean load() {
		this.initialise();
		return this.getDataConnector().loadContext(this);
	}

	public void setCreated(DateTime now) {
		this.created = now;
	}

	public void setUpdated(DateTime now) {
		this.updated = now;
	}

	public Map<String, User> getUserResultSourcedIDs(boolean flag, int scope) {
		return getDataConnector().getUserResultSourcedIDsContext(this, flag, scope);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
