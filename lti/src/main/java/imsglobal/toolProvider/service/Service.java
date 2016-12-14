package imsglobal.toolProvider.service;

import imsglobal.LTIMessage;
import imsglobal.toolProvider.ToolConsumer;
import java.util.List;
import java.util.Map;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpMessage;

import java.util.HashMap;
//use IMSGlobal\LTI\HTTPMessage;

/**
 * Class to implement a service
 *
 * @author  Stephen P Vickers <svickers@imsglobal.org>
 * @copyright  IMS Global Learning Consortium Inc
 * @date  2016
 * @version 3.0.0
 * @license http://www.apache.org/licenses/LICENSE-2.0 Apache License, Version 2.0
 * translated to Java by David Block (dave@northcreek.ca)
 */
public class Service {

/**
 * Whether service request should be sent unsigned.
 *
 * @var boolean unsigned
 */
    public boolean unsigned = false;

/**
 * Service endpoint.
 *
 * @var string endpoint
 */
    protected String endpoint;
/**
 * Tool Consumer for this service request.
 *
 * @var ToolConsumer consumer
 */
    private ToolConsumer consumer;
/**
 * Media type of message body.
 *
 * @var string mediaType
 */
    private String mediaType;

/**
 * Class constructor.
 *
 * @param ToolConsumer consumer   Tool consumer object for this service request
 * @param string       endpoint   Service endpoint
 * @param string       mediaType  Media type of message body
 * 
 * 
 */
    
    public Service () {
    	//no-arg constructor
    }
    
    public void initialize(ToolConsumer consumer, String endpoint, String mediaType)
    {

        this.setConsumer(consumer);
        this.setEndpoint(endpoint);
        this.setMediaType(mediaType);

    }

	public boolean isUnsigned() {
		return unsigned;
	}
	
	public void setUnsigned(boolean unsigned) {
		this.unsigned = unsigned;
	}
	
	public String getEndpoint() {
		return endpoint;
	}
	
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	
	public ToolConsumer getConsumer() {
		return consumer;
	}
	
	public void setConsumer(ToolConsumer consumer) {
		this.consumer = consumer;
	}
	
	public String getMediaType() {
		return mediaType;
	}
	
	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}
	
	/**
	 * Send a service request.
	 *
	 * @param string  method      The action type constant (optional, default is GET)
	 * @param array   parameters  Query parameters to add to endpoint (optional, default is none)
	 * @param string  body        Body of request (optional, default is null)
	 *
	 * @return LTIMessage HTTP object containing request and response details
	 */
	
	public LTIMessage send(String method, String body) {
		return send(method, null, body);
	}
	
	public LTIMessage send(String method, Map<String, List<String>> parameters) {
		return send(method, parameters, null);
	}
    public LTIMessage send(String method, Map<String, List<String>> parameters, String body)
    {
    	String sep = "";
    	String header = null;
        String url = this.endpoint;
        if (!parameters.isEmpty()) {
            if (url.indexOf('?')>-1) {
                sep = "?";
            } else {
                sep = "&";
            }
            for (String name : parameters.keySet()) {
            	for (String value : parameters.get(name)) {
	            	try {
	            		url += sep + URLEncoder.encode(name, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
	            	} catch (UnsupportedEncodingException unse) {
	            		unse.printStackTrace();
	            		url += sep + name + "=" + value;
	            	}
	            	sep = "&";
            	}
            }
        }
        if (!this.isUnsigned()) {
            header = ToolConsumer.addSignature(url, this.getConsumer().getKey(), this.getConsumer().getSecret(), body, method, this.getMediaType());
        } else {
            header = null;
        }

// Connect to tool consumer
        LTIMessage http = new LTIMessage(url, method, body, header, null);
// Parse JSON response
        if (http.send() && http.getResponse().isEmpty()) {
            http.setOk(http.getResponseJson() != null);
        }

        return http;

    }

}
