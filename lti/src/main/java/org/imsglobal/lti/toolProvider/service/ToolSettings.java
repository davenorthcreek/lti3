package org.imsglobal.lti.toolProvider.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.imsglobal.lti.LTIMessage;
import org.imsglobal.lti.LTIUtil;
import org.imsglobal.lti.toolProvider.LTISource;
import org.imsglobal.lti.toolProvider.ToolConsumer;
import org.json.simple.JSONObject;

/**
 * Class to implement the Tool Settings service
 *
 * @author  Stephen P Vickers <svickers@imsglobal.org>
 * @copyright  IMS Global Learning Consortium Inc
 * @date  2016
 * @version 3.0.0
 * @license http://www.apache.org/licenses/LICENSE-2.0 Apache License, Version 2.0
 * translated to Java by David Block (dave@northcreek.ca)
 */

public class ToolSettings extends Service {
	
	/**
	 * Settings at current level mode.
	 */
	    public static final int MODE_CURRENT_LEVEL = 1;
	/**
	 * Settings at all levels mode.
	 */
	    public static final int MODE_ALL_LEVELS = 2;
	/**
	 * Settings with distinct names at all levels mode.
	 */
	    public static final int MODE_DISTINCT_NAMES = 3;

	/**
	 * Names of LTI parameters to be retained in the consumer settings property.
	 *
	 * @var array $LEVEL_NAMES
	 */
	    private static final Map<String, String> LEVEL_NAMES;
	    static {
	        Map<String, String> aMap = new HashMap<String, String>();
	        aMap.put("ToolProxy", "system");
	        aMap.put("ToolProxyBinding", "context");
	        aMap.put("LtiLink", "link");
	        LEVEL_NAMES = Collections.unmodifiableMap(aMap);
	    }

	/**
	 * The object to which the settings apply (ResourceLink, Context or ToolConsumer).
	 *
	 * @var object  $source
	 */
	    private Object source;
	/**
	 * Whether to use the simple JSON format.
	 *
	 * @var boolean  $simple
	 */
	    private boolean simple;


	public boolean isSimple() {
		return simple;
	}

	public void setSimple(boolean simple) {
		this.simple = simple;
	}
	
	public ToolSettings(LTISource source, String endpoint) {
		super();
		if (source instanceof ToolConsumer) {
            setConsumer((ToolConsumer)source);
        } else {
            setConsumer(source.getConsumer());
        }
        setMediaType("application/vnd.ims.lti.v2.toolsettings.simple+json");
        initialize(getConsumer(), endpoint, getMediaType());
        setSource(source);
        setSimple(simple);

	}
	
	public ToolSettings(LTISource source, String endpoint, boolean simple) {
		super();
		if (source instanceof ToolConsumer) {
            setConsumer((ToolConsumer)source);
        } else {
            setConsumer(source.getConsumer());
        }
        if (simple) {
            setMediaType("application/vnd.ims.lti.v2.toolsettings.simple+json");
        } else {
            setMediaType("application/vnd.ims.lti.v2.toolsettings+json");
        }
        initialize(getConsumer(), endpoint, getMediaType());
        setSource(source);
        setSimple(simple);

	}
	
	private void setSource(Object source) {
		this.source = source;
	}
	
	@SuppressWarnings("rawtypes")
	public Map get() {
		return get(MODE_CURRENT_LEVEL);
	}
	
	/**
	 * Get the tool settings.
	 *
	 * @param int          $mode       Mode for request (optional, default is current level only)
	 *
	 * @return mixed The array of settings if successful, otherwise false
	 */
	@SuppressWarnings("rawtypes")
	public Map get(int mode) {
		JSONObject response = new JSONObject();
		Map<String, List<String>> parameter = new HashMap<String, List<String>>();
		if (mode == MODE_ALL_LEVELS) {
			LTIUtil.setParameter(parameter, "bubble", "all");
		} else if (mode == MODE_DISTINCT_NAMES) {
			LTIUtil.setParameter(parameter, "bubble", "distinct");
		}
	    LTIMessage http = this.send("GET", parameter);
	    JSONObject responseJson = http.getResponseJson();
	    if (!http.isOk()) {
	    	response = null;
	    } else if (simple) {
	        response = responseJson;
	    } else if (responseJson.containsKey("@graph")) {
	    	JSONObject graph = (JSONObject)responseJson.get("@graph");
	    	for (Object level: graph.keySet()) {
	    		JSONObject jlevel = (JSONObject)level;
	    		JSONObject settings = (JSONObject)jlevel.get("custom");
	    		settings.remove("@id");
	    		
	    		String type = (String)jlevel.get("@type");
	    		String theLevel = LEVEL_NAMES.get(type);
	    		@SuppressWarnings("unchecked")
				HashMap<String, Object> hmResponse = new HashMap<String, Object>(response);
	    		hmResponse.put(theLevel, settings);
	    		return hmResponse;
	    	}
	    }
	    return response;
	}

	/**
	 * Set the tool settings.
	 *
	 * @param array  $settings  An associative array of settings (optional, default is null)
	 *
	 * @return HTTPMessage HTTP object containing request and response details
	 */
	    public LTIMessage set(Map<String, List<String>> settings) {
	    	String type;
	    	String body;
	        if (!this.isSimple()) {
	        	if (source instanceof ToolConsumer) {
	        		type = "ToolProxy";
	        	} else if (source instanceof ToolConsumer) {
	        		//TODO Repeat of first boolean test - bug in code?
	        		type = "ToolProxyBinding";
	        	} else {
	        		type = "LtiLink";
	        	}
	        	HashMap<String, Object> obj = new HashMap();
	        	obj.put("@context", "http://purl.imsglobal.org/ctx/lti/v2/ToolSettings");
	        	
	        	HashMap<String, Object> level = new HashMap();
	        	level.put("@type", type);
	        	level.put("@id", this.getEndpoint());
	        	level.put("custom", settings);
	        	List<Map<String, Object>> levels = new ArrayList<Map<String, Object>>();
	        	levels.add(level);
	        	obj.put("@graph", levels);
	        	JSONObject jobj = new JSONObject(obj);
	        	body = jobj.toJSONString();
	        } else {
	        	JSONObject jobj = new JSONObject(settings);
	        	body = jobj.toJSONString();
	        }
	        
	        LTIMessage response = null;
			response = this.send("PUT", null, body);

	        return response;

	    }

}
