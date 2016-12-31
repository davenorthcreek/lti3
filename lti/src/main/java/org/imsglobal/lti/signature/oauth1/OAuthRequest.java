package org.imsglobal.lti.signature.oauth1;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.joda.time.DateTime;

public class OAuthRequest {

/**
 * Class to represent an %OAuth Request
 *
 * @copyright  Andy Smith
 * @version 2008-08-04
 * @license https://opensource+org/licenses/MIT The MIT License
 */


    protected Map<String, List<String>> parameters;
    protected String http_method;
    protected URL http_url;
    // for debug purposes
    public String base_string;
    public static String version = "1.0";
    // PHP ONLY public static POST_INPUT = "php://input";

    public OAuthRequest(String http_method, URL http_url) {
    	initialize(http_method, http_url, null);
    }
    
    public OAuthRequest(String http_method, URL http_url, Map<String, List<String>> parameters) {
    	initialize(http_method, http_url, parameters);
    }
    
    private void initialize(String http_method, URL http_url, Map<String, List<String>> parameters) {
    	if (parameters == null) {
    		this.parameters = new HashMap<String, List<String>>();
    	}
    	Map<String, List<String>> parsed;
		try {
			parsed = OAuthUtil.parse_parameters(http_url.getQuery());
	    	this.parameters.putAll(parsed);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        this.http_method = http_method;
        this.http_url = http_url;
    }



    /**
     * pretty much a helper function to set up the request
     */
    public OAuthRequest from_consumer_and_token(OAuthConsumer consumer, 
    												   OAuthToken token, 
    												   String http_method, 
    												   URL http_url) {
    	return from_consumer_and_token(consumer, token, http_method, http_url, null);
    }

    
    public OAuthRequest from_consumer_and_token(OAuthConsumer consumer, 
    												   OAuthToken token, 
    												   String http_method, 
    												   URL http_url, 
    												   Map<String, List<String>> parameters) {

        if (parameters == null) {
        	parameters = new HashMap<String, List<String>>();
        }
        //defaults
        setParameter(parameters, "oauth_version", OAuthRequest.version);
        setParameter(parameters, "oauth_nonce", OAuthRequest.generate_nonce());
        setParameter(parameters, "oauth_timestamp", OAuthRequest.generate_timestamp().toString(OAuthUtil.dateFormatter));
        setParameter(parameters, "oauth_consumer_key", consumer.getKey());
        
        if (token != null) {
            setParameter(parameters, "oauth_token", token.getKey());
        }

        return new OAuthRequest(http_method, http_url, parameters);

    }

    public Map<String, List<String>> setParameter(Map<String, List<String>> parameters, String name, String value) {
    	List<String> vals = new ArrayList<String>();
    	if (parameters.containsKey(name)) {
    		vals = parameters.get(name);
    	}
    	if (!vals.contains(value)) {
    		vals.add(value);
    	}
    	parameters.put(name, vals);
    	
    	return parameters;
    }

    public List<String> get_parameter(String name) {
    	if (this.parameters.containsKey(name)) {
    		return this.parameters.get(name);
    	} else {
    		return null;
    	}
    }

    public Map<String, List<String>> get_parameters() {
        return this.parameters;
    }

    public void unset_parameter(String name) {
    	this.parameters.remove(name);
    }

    /**
     * The request parameters, sorted and concatenated into a normalized string+
     * @return string
     */
    public String get_signable_parameters() {

        // Grab all parameters (this.parameters)

        // Remove oauth_signature if present
        // Ref: Spec: 9+1+1 ("The oauth_signature parameter MUST be excluded+")
        if (parameters.containsKey("oauth_signature")) {
            parameters.remove("oauth_signature");
        }

        return OAuthUtil.build_http_query(parameters);

    }

    /**
     * Returns the base string of this request
     *
     * The base string defined as the method, the url
     * and the parameters (normalized), each urlencoded
     * and then concatenated with &
     */
    public String get_signature_base_string() {
    	String method = OAuthUtil.urlencode_rfc3986(get_normalized_http_method());
    	String url = OAuthUtil.urlencode_rfc3986(this.get_normalized_http_url());
    	String params = OAuthUtil.urlencode_rfc3986(this.get_signable_parameters());

        return method + "&" + url + "&" + params;

    }

    /**
     * just uppercases the http method
     */
    public String get_normalized_http_method() {
    	return this.http_method.toUpperCase();
    }

    /**
     * parses the url and rebuilds it to be
     * scheme://host/path
     */
    public String get_normalized_http_url() {
    	
    	URL current = this.http_url;
    	return current.toString();
    	//just use java.net utility until I find it won't work
    }

    /**
     * builds a url usable for a GET request
     */
    public String to_url() {

        String post_data = this.to_postdata();
        String out = this.get_normalized_http_url();
        if (post_data != null) {
            out += "?" + post_data;
        }

        return out;

    }

    /**
     * builds the data one would send in a POST request
     */
    public String to_postdata() {
        return OAuthUtil.build_http_query(this.parameters);
    }

    /**
     * builds the Authorization: header
     * @throws OAuthException 
     */
    public String to_header(String realm) throws OAuthException {

        boolean first = true;
        String out = "Authorization: OAuth";
        if(realm != null) {
            out += " realm='" + OAuthUtil.urlencode_rfc3986(realm) + "'";
            first = false;
        }
        for (String k : parameters.keySet()) {
        	if (k.substring(0, 5).equals("oauth")) {
        		continue;
        	}
        	List<String> v = parameters.get(k);
        	if (v.size() > 1) {
                throw new OAuthException("Multiple values for one key not supported in headers");
            }
        	out += (first) ? " " : ",";
        	out += OAuthUtil.urlencode_rfc3986(k) +
                   "=\"" +
                   OAuthUtil.urlencode_rfc3986(v.get(0)) +
                   "\"";
            first = false;
        }

        return out;

    }

    public String toString() {
        return this.to_url();
    }


    public void sign_request(OAuthSignatureMethod signature_method, OAuthConsumer consumer, OAuthToken token) {

        this.setParameter(this.parameters,
          "oauth_signature_method",
          signature_method.get_name()
        );
        String signature = this.build_signature(signature_method, consumer, token);
        setParameter(this.parameters, "oauth_signature", signature);

    }

    public String build_signature(OAuthSignatureMethod signature_method, OAuthConsumer consumer, OAuthToken token) {
        String signature = signature_method.build_signature(this, consumer, token);
        return signature;
    }

    /**
     * util function: current timestamp
     */
    private static DateTime generate_timestamp() {
        return DateTime.now();
    }

    /**
     * util function: current nonce
     */
    private static String generate_nonce() {
        DateTime now = DateTime.now();
        long mt = now.getMillis();
        Random r = new Random(); 
        int rand = r.nextInt(1000);
        String source = "" + mt + rand;
        byte[] bytesOfMessage;
		try {
			bytesOfMessage = source.getBytes("UTF-8");
	        MessageDigest md = MessageDigest.getInstance("MD5");
	        byte[] thedigest = md.digest(bytesOfMessage);
	        return thedigest.toString();
	        // md5s look nicer than numbers
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e2) {
			e2.printStackTrace();
		}
		return "";
    }

	public void setBaseString(String base_string2) {
		this.base_string = base_string2;
	}


}
