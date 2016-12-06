package imsglobal.toolProvider;

import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.json.simple.JSONObject;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;

import imsglobal.LTIMessage;
import imsglobal.toolProvider.dataConnector.DataConnector;
import imsglobal.toolProvider.dataConnector.DataConnectorFactory;
import imsglobal.toolProvider.service.Service;
import imsglobal.toolProvider.service.ToolSettings;
import net.oauth.OAuth;

/**
 * Class to represent a tool consumer
 *
 * @author  Stephen P Vickers <svickers@imsglobal.org>
 * @copyright  IMS Global Learning Consortium Inc
 * @date  2016
 * @version 3.0.2
 * @license http://www.apache.org/licenses/LICENSE-2.0 Apache License, Version 2.0
 * translated to Java by David Block (dave@northcreek.ca)
 */
public class ToolConsumer implements LTISource {
	
	/**
	 * Local name of tool consumer.
	 *
	 * @var string name
	 */
	    public String name = null;
	/**
	 * Shared secret.
	 *
	 * @var string secret
	 */
	    public String secret = null;
	/**
	 * LTI version (as reported by last tool consumer connection).
	 *
	 * @var string ltiVersion
	 */
	    public String ltiVersion = null;
	/**
	 * Name of tool consumer (as reported by last tool consumer connection).
	 *
	 * @var string consumerName
	 */
	    public String consumerName = null;
	/**
	 * Tool consumer version (as reported by last tool consumer connection).
	 *
	 * @var string consumerVersion
	 */
	    public String consumerVersion = null;
	/**
	 * Tool consumer GUID (as reported by first tool consumer connection).
	 *
	 * @var string consumerGuid
	 */
	    public String consumerGuid = null;
	/**
	 * Optional CSS path (as reported by last tool consumer connection).
	 *
	 * @var string cssPath
	 */
	    public String cssPath = null;
	/**
	 * Whether the tool consumer instance is protected by matching the consumer_guid value in incoming requests.
	 *
	 * @var boolean protected
	 */
	    public boolean thisprotected = false;
	/**
	 * Whether the tool consumer instance is enabled to accept incoming connection requests.
	 *
	 * @var boolean enabled
	 */
	    public boolean enabled = false;
	/**
	 * Date/time from which the the tool consumer instance is enabled to accept incoming connection requests.
	 *
	 * @var int enableFrom
	 */
	    public DateTime enableFrom = null;
	/**
	 * Date/time until which the tool consumer instance is enabled to accept incoming connection requests.
	 *
	 * @var int enableUntil
	 */
	    public DateTime enableUntil = null;
	/**
	 * Date of last connection from this tool consumer.
	 *
	 * @var int lastAccess
	 */
	    public DateTime lastAccess = null;
	/**
	 * Default scope to use when generating an Id value for a user.
	 *
	 * @var int idScope
	 */
	    public int idScope = ToolProvider.ID_SCOPE_ID_ONLY;
	/**
	 * Default email address (or email domain) to use when no email address is provided for a user.
	 *
	 * @var string defaultEmail
	 */
	    public String defaultEmail;
	/**
	 * Setting values (LTI parameters, custom parameters and local parameters).
	 *
	 * @var array settings
	 */
	    public Map<String, String> settings = new HashMap<String, String>();
	/**
	 * Date/time when the object was created.
	 *
	 * @var int created
	 */
	    public DateTime created = null;
	/**
	 * Date/time when the object was last updated.
	 *
	 * @var int updated
	 */
	    public DateTime updated = null;

	/**
	 * Consumer ID value.
	 *
	 * @var int id
	 */
	    private int id;
	/**
	 * Consumer key value.
	 *
	 * @var string key
	 */
	    private String key;
	/**
	 * Whether the settings value have changed since last saved.
	 *
	 * @var boolean settingsChanged
	 */
	    private boolean settingsChanged = false;
	/**
	 * Data connector object or string.
	 *
	 * @var DataConnector dataConnector
	 */
	    private DataConnector dataConnector = null;
	    
	    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getLtiVersion() {
		return ltiVersion;
	}

	public void setLtiVersion(String ltiVersion) {
		this.ltiVersion = ltiVersion;
	}

	public String getConsumerName() {
		return consumerName;
	}

	public void setConsumerName(String consumerName) {
		this.consumerName = consumerName;
	}

	public String getConsumerVersion() {
		return consumerVersion;
	}

	public void setConsumerVersion(String consumerVersion) {
		this.consumerVersion = consumerVersion;
	}

	public String getConsumerGuid() {
		return consumerGuid;
	}

	public void setConsumerGuid(String consumerGuid) {
		this.consumerGuid = consumerGuid;
	}

	public String getCssPath() {
		return cssPath;
	}

	public void setCssPath(String cssPath) {
		this.cssPath = cssPath;
	}

	public boolean isThisprotected() {
		return thisprotected;
	}

	public void setThisprotected(boolean thisprotected) {
		this.thisprotected = thisprotected;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public DateTime getEnableFrom() {
		return enableFrom;
	}

	public void setEnableFrom(DateTime enableFrom) {
		this.enableFrom = enableFrom;
	}

	public DateTime getEnableUntil() {
		return enableUntil;
	}

	public void setEnableUntil(DateTime enableUntil) {
		this.enableUntil = enableUntil;
	}

	public DateTime getLastAccess() {
		return lastAccess;
	}

	public void setLastAccess(DateTime lastAccess) {
		this.lastAccess = lastAccess;
	}

	public int getIdScope() {
		return idScope;
	}

	public void setIdScope(int idScope) {
		this.idScope = idScope;
	}

	public String getDefaultEmail() {
		return defaultEmail;
	}

	public void setDefaultEmail(String defaultEmail) {
		this.defaultEmail = defaultEmail;
	}

	public DateTime getCreated() {
		return created;
	}

	public void setCreated(DateTime created) {
		this.created = created;
	}

	public DateTime getUpdated() {
		return updated;
	}

	public void setUpdated(DateTime updated) {
		this.updated = updated;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isSettingsChanged() {
		return settingsChanged;
	}

	public void setSettingsChanged(boolean settingsChanged) {
		this.settingsChanged = settingsChanged;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setDataConnector(DataConnector dataConnector) {
		this.dataConnector = dataConnector;
	}
	
	/**
	 * Class constructor.
	 *
	 * @param string  key             Consumer key
	 * @param DataConnector   dataConnector   A data connector object
	 * @param boolean autoEnable      true if the tool consumers is to be enabled automatically (optional, default is false)
	 */
	    public ToolConsumer() {
	    	this(null, null, false);
	    }

		public ToolConsumer(String key) {
	    	this(key, null, false);
	    }
	    
	    public ToolConsumer(String key, DataConnector dataConnector) {
	    	this(key, dataConnector, false);
	    }
	    
	    public ToolConsumer(String key, DataConnector dataConnector, boolean autoEnable) {

	        initialize();
	        if ((dataConnector == null)) {
	            dataConnector = DataConnectorFactory.getDataConnector();
	        }
	        setDataConnector(dataConnector);
	        if (key != null) {
	            load(key, autoEnable);
	        } else {
	            setSecret(dataConnector.getRandomString(32));
	        }

	    }

	/**
	 * Initialise the tool consumer.
	 */
	    public void initialize()
	    {

	        setIdScope(ToolProvider.ID_SCOPE_ID_ONLY);

	    }

	/**
	 * Initialise the tool consumer.
	 *
	 * Pseudonym for initialize().
	 */
	    public void initialise()
	    {

	        initialize();

	    }

	/**
	 * Save the tool consumer to the database.
	 *
	 * @return boolean True if the object was successfully saved
	 */
	    public boolean save()
	    {

	        boolean ok = dataConnector.saveToolConsumer(this);
	        if (ok) {
	            setSettingsChanged(false);
	        }

	        return ok;

	    }

	/**
	 * Delete the tool consumer from the database.
	 *
	 * @return boolean True if the object was successfully deleted
	 */
	    public boolean delete()
	    {

	        return dataConnector.deleteToolConsumer(this);

	    }

	/**
	 * Get the tool consumer record ID.
	 *
	 * @return int Consumer record ID value
	 */
	    public int getRecordId()
	    {

	        return getId();

	    }

	/**
	 * Sets the tool consumer record ID.
	 *
	 * @param int id  Consumer record ID value
	 */
	    public void setRecordId(int id)
	    {

	        setId(id);

	    }

	/**
	 * Get the tool consumer key.
	 *
	 * @return string Consumer key value
	 */


	/**
	 * Set the tool consumer key.
	 *
	 * @param string key  Consumer key value
	 */


	/**
	 * Get the data connector.
	 *
	 * @return mixed Data connector object or string
	 */


	/**
	 * Is the consumer key available to accept launch requests?
	 *
	 * @return boolean True if the consumer key is enabled and within any date constraints
	 */
	    public boolean getIsAvailable()
	    {

	        boolean ok = isEnabled();

	        DateTime now = DateTime.now();
	        if (ok && getEnableFrom() != null) {
	            ok = getEnableFrom().isBefore(now) || getEnableFrom().equals(now);
	        }
	        if (ok && getEnableUntil() != null) {
	            ok = getEnableUntil().isBefore(now) || getEnableUntil().equals(now);
	        }

	        return ok;

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
	    	getSetting(name, "");
	    }
	    
	    public String getSetting(String name, String thedefault)
	    {
	    	String value = thedefault;
	    	Map<String, String> theSettings = getSettings();
	    	if (theSettings.containsKey(name)) {
	    		value = theSettings.get(name);
	    	}
	        return value;

	    }

	/**
	 * Set a setting value.
	 *
	 * @param string name  Name of setting
	 * @param string value Value to set, use an empty value to delete a setting (optional, default is null)
	 */
	    
	    public void setSetting(String name) {
	    	setSetting(name, "");
	    }
	    
	    public void setSetting(String name, String value)
	    {
	    	Map<String, String> theSettings = getSettings();
        	String old_value = getSetting(name);
	        if (value == null || value == "") {
	        	theSettings.remove(name);
	        	setSettings(theSettings);
	        } else if (value != old_value) {
	        	theSettings.put(name, value);
                setSettings(theSettings);
	        }

	    }

	/**
	 * Get an array of all setting values.
	 *
	 * @return array Associative array of setting values
	 */
	    public Map<String, String> getSettings()
	    {

	        return settings;

	    }

	/**
	 * Set an array of all setting values.
	 *
	 * @param array settings  Associative array of setting values
	 */
	    public void setSettings(Map<String, String> settings)
	    {

	        this.settings = settings;
            setSettingsChanged(true);

	    }

	/**
	 * Save setting values.
	 *
	 * @return boolean True if the settings were successfully saved
	 */
	    public boolean saveSettings()
	    {
	    	boolean ok = true;
	        if (isSettingsChanged()) {
	        	ok = save();
	        }

	        return ok;

	    }

	/**
	 * Check if the Tool Settings service is supported.
	 *
	 * @return boolean True if this tool consumer supports the Tool Settings service
	 */
	    public boolean hasToolSettingsService()
	    {

	        String url = getSetting("custom_system_setting_url");
	        boolean notEmpty = true;
	        if (url == null | url.equals("")) {
	        	notEmpty = false;
	        }
	        return notEmpty;

	    }

	/**
	 * Get Tool Settings.
	 *
	 * @param boolean  simple     True if all the simple media type is to be used (optional, default is true)
	 *
	 * @return mixed The array of settings if successful, otherwise false
	 */
	    
	    public Map<String, String> getToolSettings() {
	    	return getToolSettings(true);
	    }
	    
	    public Map<String, String> getToolSettings(boolean simple)
	    {

	        String url = getSetting("custom_system_setting_url");
	        ToolSettings service = new ToolSettings(this, url, simple);
	        return service.get();

	    }

	/**
	 * Perform a Tool Settings service request.
	 *
	 * @param array    settings   An associative array of settings (optional, default is none)
	 *
	 * @return boolean True if action was successful, otherwise false
	 */
	    
	    public boolean setToolSettings() {
	    	return setToolSettings(null);
	    }
	    
	    public boolean setToolSettings(Map<String, String> settings)
	    {

	        String url = getSetting("custom_system_setting_url");
	        ToolSettings service = new ToolSettings(this, url);
	        LTIMessage response = service.set(settings);
	        return response.isOk();

	    }

	/**
	 * Add the OAuth signature to an LTI message.
	 *
	 * @param String  url         URL for message request
	 * @param String  type        LTI message type
	 * @param String  version     LTI version
	 * @param Map   params      Message parameters
	 *
	 * @return array Array of signed message parameters
	 */
	    public Map signParameters(String urlString, String type, String version, Map<String, String> params)
	    {
	    	if (urlString != null) {
	// Check for query parameters which need to be included in the signature
	            HashMap<String, String> queryParams = new HashMap<String, String>();
	            URL url = new URL(urlString);
	            String query = url.getQuery();
	            String paramStrings[] = query.split("\\&");
	            HashMap<String, String[]> qparams = new HashMap<String, String[]>();
	            for (int i=0;i<paramStrings.length;i++) {
	                String parts[] = paramStrings[i].split("=");
	                String key = URLDecoder.decode(parts[0], "UTF-8");
	                String value = URLDecoder.decode(parts[1], "UTF-8");
	                String[] values = new String[1];
	                if (qparams.containsKey(key)) {
	                	values = qparams.get(key);
	                }
	                values[values.length] = value;
	                qparams.put(key, values);
	            }
	            //?? Where did this line come from?
	            //Set<String> paramVals = params.get("paramName");
	
	        	// Add standard parameters
	            params.put("lti_version", version);
	            params.put("lti_message_type", type);
	            params.put("oauth_callback", "about:blank");
	            params = params.putAll(qparams);
	// Add OAuth signature
	            OAuthSignatureMethod hmacMethod = new OAuth();
	            OAuthConsumer consumer = new OAuth.OAuthConsumer(this.getKey(), this.getSecret(), null);
	            //req = OAuth\OAuthRequest::from_consumer_and_token(consumer, null, 'POST', url, params);
	            
	            req.sign_request(hmacMethod, consumer, null);
	            params = req.get_parameters();
	// Remove parameters being passed on the query string
	            for (String name : queryParams.keySet()) {
	                queryParams.remove(name);
	            }
	        }

	        return params;

	    }
	    
	    public Object signParameters() {
	    	final OAuth10aService service = new ServiceBuilder()
                    .apiKey(this.getKey())
                    .apiSecret(this.getSecret())
                    .build(ToolProvider.instance());
	    	final OAuth1RequestToken requestToken = service.getRequestToken();
	    	String authUrl = service.getAuthorizationUrl(requestToken);
	    	final OAuth1AccessToken accessToken = service.getAccessToken(requestToken, "verifier you got from the user/callback");
	    	final OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.twitter.com/1.1/account/verify_credentials.json", service);
	    	service.signRequest(accessToken, request); // the access token from step 4
	    	final Response response = request.send();
	    	System.out.println(response.getBody());
	    }

	/**
	 * Add the OAuth signature to an array of message parameters or to a header string.
	 *
	 * @return mixed Array of signed message parameters or header string
	 */
	    public static Map<String, String> addSignature(String endpoint, String consumerKey, String consumerSecret, Map<String, String> data, String method, String type)
	    {

	        Map<String, String> params = new HashMap<String, String>();
	        if (data != null) {
	            params = data;
	        }
	        // Check for query parameters which need to be included in the signature
	        Map<String, String> queryParams = new HashMap<String, String>();
	        URL url = new URL(endpoint);
	        String queryString = url.getQuery();
	        if (queryString != null) {
	            String queryItems[] = queryString.split("\\&");
	            for (int i=0; i<queryItems.length; i++) {
	            	String item = queryItems[i];
	                if (item.contains("=")) {
	                    String[] parts = item.split("=");
	                    String key = URLDecoder.decode(parts[0], "UTF-8");
		                String value = URLDecoder.decode(parts[1], "UTF-8");
	                    queryParams.put(key, value);
	                } else {
	                    queryParams.put(URLDecoder.decode(item, "UTF-8"),"");
	                }
	            }
	            params.putAll(queryParams);
	        }

	        if (!is_array(data)) {
	// Calculate body hash
	            String hash = base64_encode(sha1(data, true));
	            params.put("oauth_body_hash", hash);
	        }

	// Add OAuth signature
	        hmacMethod = new OAuth\OAuthSignatureMethod_HMAC_SHA1();
	        oauthConsumer = new OAuth\OAuthConsumer(consumerKey, consumerSecret, null);
	        oauthReq = OAuth\OAuthRequest::from_consumer_and_token(oauthConsumer, null, method, endpoint, params);
	        oauthReq.sign_request(hmacMethod, oauthConsumer, null);
	        params = oauthReq.get_parameters();
	// Remove parameters being passed on the query string
	        foreach (array_keys(queryParams) as name) {
	            unset(params[name]);
	        }

	        if (!is_array(data)) {
	            header = oauthReq.to_header();
	            if (empty(data)) {
	                if (!empty(type)) {
	                    header .= "\nAccept: {type}";
	                }
	            } else if (isset(type)) {
	                header .= "\nContent-Type: {type}";
	                header .= "\nContent-Length: " . strlen(data);
	            }
	            return header;
	        } else {
	            return params;
	        }

	    }

	/**
	 * Perform a service request
	 *
	 * @param object service  Service object to be executed
	 * @param string method   HTTP action
	 * @param string format   Media type
	 * @param mixed  data     Array of parameters or body string
	 *
	 * @return HTTPMessage HTTP object containing request and response details
	 */
	    public LTIMessage doServiceRequest(Service service, String method, String format, Map<String, String> data)
	    {

	        Map<String, String> header = ToolConsumer.addSignature(service.getEndpoint(), this.getKey(), this.getSecret(), data, method, format);

	// Connect to tool consumer
	        LTIMessage http = new LTIMessage(service.getEndpoint(), method, data, header);
	// Parse JSON response
	        http.send();
	        String response = http.getResponse();
	        JSONObject json = http.getResponseJson();
	        if (http.isOk()) {
	        	return http;
	        } else {
	        	return null;
	        }

	    }

	/**
	 * Load the tool consumer from the database by its record ID.
	 *
	 * @param string          id                The consumer key record ID
	 * @param DataConnector   dataConnector    Database connection object
	 *
	 * @return object ToolConsumer       The tool consumer object
	 */
	    public static function fromRecordId(id, dataConnector)
	    {

	        toolConsumer = new ToolConsumer(null, dataConnector);

	        toolConsumer.initialize();
	        toolConsumer.setRecordId(id);
	        if (!dataConnector.loadToolConsumer(toolConsumer)) {
	            toolConsumer.initialize();
	        }

	        return toolConsumer;

	    }



	/**
	 * Load the tool consumer from the database.
	 *
	 * @param string  key        The consumer key value
	 * @param boolean autoEnable True if the consumer should be enabled (optional, default if false)
	 *
	 * @return boolean True if the consumer was successfully loaded
	 */
	    
	    private boolean load(String key) {
	    	load(key, false);
	    }
	    
	    private boolean load(String key, boolean autoEnable)
	    {
	    	setKey(key);
	    	DataConnector connector = DataConnectorFactory.getDataConnector();
	        boolean ok = connector.loadToolConsumer(this);
	        ok = this.dataConnector.loadToolConsumer(this);
	        if (!ok) {
	        	setEnabled(autoEnable);
	        }

	        return ok;

	    }
	    
	public static String addSignature(String url, Object key, Object secret, String body, String method,
			String mediaType) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public ToolConsumer getConsumer() {
		return this;
	}

	public String getKey() {
		// TODO Auto-generated method stub
		return null;
	}

}
