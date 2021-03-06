package org.imsglobal.lti.toolProvider;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.imsglobal.lti.LTIMessage;
import org.imsglobal.lti.LTIUtil;
import org.imsglobal.lti.profile.ServiceDefinition;
import org.imsglobal.lti.signature.oauth1.OAuthUtil;
import org.imsglobal.lti.toolProvider.dataConnector.DataConnector;
import org.imsglobal.lti.toolProvider.dataConnector.DataConnectorFactory;
import org.imsglobal.lti.toolProvider.mediaType.ConsumerProfile;
import org.imsglobal.lti.toolProvider.service.ToolSettings;
import org.joda.time.DateTime;
import org.json.simple.JSONObject;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.signature.OAuthSignatureMethod;



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
	    private String name = null;
	/**
	 * Shared secret.
	 *
	 * @var string secret
	 */
	    private String secret = null;
	/**
	 * LTI version (as reported by last tool consumer connection).
	 *
	 * @var string ltiVersion
	 */
	    private String ltiVersion = null;
	/**
	 * Name of tool consumer (as reported by last tool consumer connection).
	 *
	 * @var string consumerName
	 */
	    private String consumerName = null;
	/**
	 * Tool consumer version (as reported by last tool consumer connection).
	 *
	 * @var string consumerVersion
	 */
	    private String consumerVersion = null;
	/**
	 * Tool consumer GUID (as reported by first tool consumer connection).
	 *
	 * @var string consumerGuid
	 */
	    private String consumerGuid = null;
	/**
	 * Optional CSS path (as reported by last tool consumer connection).
	 *
	 * @var string cssPath
	 */
	    private String cssPath = null;
	/**
	 * Whether the tool consumer instance is protected by matching the consumer_guid value in incoming requests.
	 *
	 * @var boolean protected
	 */
	    private boolean thisprotected = false;
	/**
	 * Whether the tool consumer instance is enabled to accept incoming connection requests.
	 *
	 * @var boolean enabled
	 */
	    private boolean enabled = false;
	/**
	 * Date/time from which the the tool consumer instance is enabled to accept incoming connection requests.
	 *
	 * @var int enableFrom
	 */
	    private DateTime enableFrom = null;
	/**
	 * Date/time until which the tool consumer instance is enabled to accept incoming connection requests.
	 *
	 * @var int enableUntil
	 */
	    private DateTime enableUntil = null;
	/**
	 * Date of last connection from this tool consumer.
	 *
	 * @var int lastAccess
	 */
	    private DateTime lastAccess = null;
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
	    private String defaultEmail;
	/**
	 * Setting values (LTI parameters, custom parameters and local parameters).
	 *
	 * @var array settings
	 */
	    private Map<String, List<String>> settings = new HashMap<String, List<String>>();
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
	    

		private List<String> capability_offered = new ArrayList<String>();
		
		private Map<String, List<String>> toolProxyMap;
		
		private ConsumerProfile profile = new ConsumerProfile();
	    
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
	
		public String getId() {
			return String.valueOf(id);
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
		
		public String getKey() {
			return key;
		}
	
		public void setProfile(ConsumerProfile profile) {
			this.profile = profile;
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
	            setSecret(DataConnector.getRandomString(32));
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

	        return Integer.valueOf(getId());

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
	    	return getSetting(name, "");
	    }
	    
	    public String getSetting(String name, String thedefault)
	    {
	    	String value = thedefault;
	    	Map<String, List<String>> theSettings = getSettings();
	    	if (theSettings.containsKey(name)) {
	    		value = theSettings.get(name).get(0);
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
	    	Map<String, List<String>> theSettings = getSettings();
        	String old_value = getSetting(name);
	        if (value == null || value == "") {
	        	theSettings.remove(name);
	        	setSettings(theSettings);
	        } else if (value != old_value) {
	        	List<String> theList = new ArrayList<String>();
	        	theList.add(value);
	        	theSettings.put(name, theList);
                setSettings(theSettings);
	        }

	    }

	/**
	 * Get an array of all setting values.
	 *
	 * @return array Associative array of setting values
	 */
	    public Map<String, List<String>> getSettings()
	    {

	        return settings;

	    }

	/**
	 * Set an array of all setting values.
	 *
	 * @param array settings  Associative array of setting values
	 */
	    public void setSettings(Map<String, List<String>> settings)
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
	    
	    public Map<String, List<String>> getToolSettings() {
	    	return getToolSettings(true);
	    }
	    
	    public Map<String, List<String>> getToolSettings(boolean simple)
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
	    
	    public boolean setToolSettings(Map<String, List<String>> settings)
	    {

	        String url = getSetting("custom_system_setting_url");
	        ToolSettings service = new ToolSettings(this, url);
	        LTIMessage response = service.set(settings);
	        return response.isOk();

	    }
	    
	    protected static List<Entry<String, String>> convert(Map<String, List<String>> params) {
	    	List<Map.Entry<String, String>> theList = new ArrayList<Map.Entry<String, String>>();
	    	Set<String> sortedList = new TreeSet<String>();
	    	sortedList.addAll(params.keySet());
	    	for (String k : sortedList) {
	    		List<String> entries = params.get(k);
	    		Set<String> sortedEntries = new TreeSet<String>();
	    		sortedEntries.addAll(entries);
	    		for (String s : sortedEntries) {
	    			theList.add(new OAuth.Parameter(k, s));
	    		}
	    	}
	    	return theList;
	    }
	    
	    private static Map<String, List<String>> convertBack(List<Entry<String, String>> oparams) {
	    	Map<String, List<String>> params = new HashMap<String, List<String>>();
	    	for (Entry<String, String> e : oparams) {
	    		LTIUtil.setParameter(params, e.getKey(), e.getValue());
	    	}
	    	return params;
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
	    public Map<String, List<String>> signParameters(
	    		String urlString, 
	    		String type, 
	    		String version, 
	    		String method,
	    		Map<String, List<String>> params)
	    {
	    	List<Entry<String, String>> oparams = new ArrayList<Entry<String, String>>();
	    	Map<String, List<String>> queryParams = new HashMap<String, List<String>>();
	    	if (urlString != null) {
	// Check for query parameters which need to be included in the signature
	    		try {
	    			URL url = new URL(urlString);
	    			String query = url.getQuery();
	    			if (StringUtils.isNotEmpty(query)) {
	    				queryParams = OAuthUtil.parse_parameters(url.getQuery());
	    			}
		            params.putAll(queryParams);
		            
		            // Add standard parameters

	            	LTIUtil.setParameter(params, "lti_version", version);
		            LTIUtil.setParameter(params, "lti_message_type", type);
		            LTIUtil.setParameter(params, "oauth_callback", "about:blank");
		            
		            oparams = convert(params);
		            
		            if (StringUtils.isEmpty(method)) {
		            	method = "POST";
		            }
		            
		// Add OAuth signature
					OAuthMessage message = doSignature(urlString, oparams, getKey(), getSecret(), method);
					oparams = message.getParameters(); //replace with signed parameters
		// Remove parameters being passed on the query string
					oparams = removeQueryParams(oparams, queryParams);
	    		} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (OAuthException e1) {
					e1.printStackTrace();
				} catch (URISyntaxException e1) { 
					e1.printStackTrace();
				}
	        }

	        return convertBack(oparams);

	    }



	/**
	 * Add the OAuth signature to an array of message parameters or to a header string.
	 *
	 * @return mixed Array of signed message parameters or header string
	 */
	    public static Map<String, List<String>> addSignature(
	    		String endpoint, 
	    		String consumerKey, 
	    		String consumerSecret, 
	    		Map<String, List<String>> data, 
	    		String method, 
	    		String type)
	    {
	    	List<Entry<String, String>> oparams = new ArrayList<Entry<String, String>>();
	    	Map<String, List<String>> params = new HashMap<String, List<String>>();
	        if (data != null) {
	            params = data;
	        }
	        if (StringUtils.isEmpty(method)) {
	        	method = "POST";
	        }
	        // Check for query parameters which need to be included in the signature
	        try {
				URL url = new URL(endpoint);
				Map<String, List<String>> queryParams = OAuthUtil.parse_parameters(url.getQuery());
			    params.putAll(queryParams);
			    
			    oparams = convert(params);
			    
	// Add OAuth signature
			    OAuthMessage message = doSignature(endpoint, oparams, consumerKey, consumerSecret, method);
			    oparams = message.getParameters();
			    
	// Remove parameters being passed on the query string
				oparams = removeQueryParams(oparams, queryParams);
			    
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (OAuthException e1) {
				e1.printStackTrace();
			} catch (URISyntaxException e1) {
				e1.printStackTrace();
			}
	        
	        return convertBack(oparams);
        	
	    }

	
	    
	    public static String addSignature(
	    		String endpoint, 
	    		String consumerKey, 
	    		String consumerSecret, 
	    		String data, 
	    		String method, 
	    		String type)
	    {
	    	List<Entry<String, String>> oparams = new ArrayList<Entry<String, String>>();
	    	Map<String, List<String>> params = new HashMap<String, List<String>>();
	    	URL url;
	        Map<String, List<String>> queryParams = new HashMap<String, List<String>>();
	        List<Entry<String, String>> headers = new ArrayList<Entry<String, String>>();
			try {
				url = new URL(endpoint);
				queryParams = OAuthUtil.parse_parameters(url.getQuery());
	            params.putAll(queryParams);
			
		// Calculate body hash
		    	MessageDigest md = MessageDigest.getInstance("SHA1");
		    	byte[] sha1 = md.digest(data.getBytes());
		        String hash = Base64.encodeBase64String(sha1);
		        List<String> hashList = new ArrayList<String>();
		        hashList.add(hash);
		        params.put("oauth_body_hash", hashList);
		        
		        oparams = convert(params);

		// Add OAuth signature
		        OAuthMessage message = doSignature(endpoint, oparams, consumerKey, consumerSecret, method);
		        
		// Remove parameters being passed on the query string
				oparams = removeQueryParams(message.getParameters(), queryParams);
					    	
	            headers = message.getHeaders();
		        if (StringUtils.isEmpty(data)) {
		        	if (type != null) {
		        		headers = addHeader(headers, "Accept", type);
		            }
	            } else if (StringUtils.isNotEmpty(type)) {
	            	headers = addHeader(headers, "Content-Type", type);
	            	headers = addHeader(headers, "Content-Length", String.valueOf(data.length()));
	            }
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (OAuthException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			
            return convertHeader(headers);
	    }
	    
	    private static List<Entry<String, String>> addHeader(
	    		List<Entry<String, String>> headers,
	    		String key,
	    		String value) {
	    	HashMap<String, String> temp = new HashMap<String, String>();
	    	temp.put(key, value);
	    	for (Entry<String, String> e : temp.entrySet()) {
	    		headers.add(e);
	    	}
	    	return headers;
	    }
	    
	    private static String convertHeader(List<Entry<String, String>> headers) {
	    	StringBuilder sb = new StringBuilder();
	    	for (Entry<String, String> e : headers) {
	    		sb.append("\n")
   				.append(e.getKey())
   				.append(": {")
   				.append(e.getValue())
   				.append("}");
	    	}
	    	return sb.toString();
	    }
	    
		private static OAuthMessage doSignature(
				String urlString, 
				List<Entry<String, String>> oparams,
				String key,
				String secret,
				String method)
				throws IOException, OAuthException, URISyntaxException {
			
			OAuthConsumer oAuthConsumer = new OAuthConsumer("about:blank", key, secret, null);
			OAuthAccessor oAuthAccessor = new OAuthAccessor(oAuthConsumer);
			OAuthMessage message = new OAuthMessage(method, urlString, oparams);
			message.sign(oAuthAccessor);
			return message;
		}

		private static List<Entry<String, String>> removeQueryParams(
				List<Entry<String, String>> oparams,
				Map<String, List<String>> queryParams)
		{
			List<Entry<String, String>> oparams2 = new ArrayList<Entry<String, String>>();
			for (Entry<String, String> e : oparams) {
				boolean copy = true;
				if (queryParams.containsKey(e.getKey())) {
					if (queryParams.get(e.getKey()).equals(e.getValue())) {
						copy = false;
					}
				}
				if (copy) {
					oparams2.add(e);
				}
			}
			return oparams2;
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
	    public LTIMessage doServiceRequest(
	    		ServiceDefinition service, 
	    		String method, 
	    		String format, 
	    		Map<String, List<String>> parameters)
	    {

	        Map<String, List<String>> params = ToolConsumer.addSignature(
	        		service.getEndpoint().toExternalForm(), 
	        		this.getKey(), 
	        		this.getSecret(), 
	        		parameters, 
	        		method, 
	        		format);

	// Connect to tool consumer
	        LTIMessage http = new LTIMessage(service.getEndpoint().toExternalForm(), method, null, null, params);
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
		 * Perform a service request
		 *
		 * @param object service  Service object to be executed
		 * @param string method   HTTP action
		 * @param string format   Media type
		 * @param mixed  data     Array of parameters or body string
		 *
		 * @return HTTPMessage HTTP object containing request and response details
		 */
	    public LTIMessage doServiceRequest(ServiceDefinition service, String method, String format, String data)
	    {

	        String header = ToolConsumer.addSignature(service.getEndpoint().toExternalForm(), this.getKey(), this.getSecret(), data, method, format);

	// Connect to tool consumer
	        LTIMessage http = new LTIMessage(service.getEndpoint().toExternalForm(), method, data, header, null);
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
	 * @param int          id                The consumer key record ID
	 * @param DataConnector   dataConnector    Database connection object
	 *
	 * @return object ToolConsumer       The tool consumer object
	 */
    public static ToolConsumer fromRecordId(int id, DataConnector dataConnector)
    {

        ToolConsumer toolConsumer = new ToolConsumer(null, dataConnector);

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
		return load(key, false);
	}
	
	private boolean load(String key, boolean autoEnable)
	{
		setKey(key);
		dataConnector = DataConnectorFactory.getDataConnector();
	    boolean ok = dataConnector.loadToolConsumer(this);
	    ok = this.dataConnector.loadToolConsumer(this);
	    if (!ok) {
	    	setEnabled(autoEnable);
	    }
	
	    return ok;
	
	}

	public ToolConsumer getConsumer() {
		return this;
	}

	public DataConnector getDataConnector() {
		return DataConnectorFactory.getDataConnector();
	}

	public Map<String, User> getUserResultSourcedIDs(boolean flag, int scope) {
		return getDataConnector().getUserResultSourcedIDsToolConsumer(this, flag, scope);
	}
	
	public void setCapabilitiesOffered(List<String> capabilities) {
		this.capability_offered = capabilities;
	}
	
	public List<String> getCapabilitiesOffered() {
		return capability_offered;
	}

	public ConsumerProfile getProfile() {
		//this is the ToolConsumerProfile that comes from JSON-LD
		return profile;
	}

	public Map<String, List<String>> getToolProxyMap() {
		return toolProxyMap;
	}

	public void setToolProxyMap(Map<String, List<String>> toolProxyMap) {
		this.toolProxyMap = toolProxyMap;
	}

	public void setToolProxy(String string) {
		// TODO Auto-generated method stub
		// TODO parse string into map, I guess
	}

	public String getToolProxy() {
		// TODO Auto-generated method stub
		// TODO Serialize ToolProxyMap to a string
		return null;
	}
	
	


}
