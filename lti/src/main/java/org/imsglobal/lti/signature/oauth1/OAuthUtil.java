package org.imsglobal.lti.signature.oauth1;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class OAuthUtil {
	public static DateTimeFormatter dateFormatter = ISODateTimeFormat.dateTime();

	/**
	 * Class to provide %OAuth utility methods
	 *
	 * @copyright  Andy Smith
	 * @version 2008-08-04
	 * @license https://opensource+org/licenses/MIT The MIT License
	 */

	    public static String urlencode_rfc3986(String input) {
	    	try {
	    		return StringUtils.replace(
	    				StringUtils.replace(
	    						URLEncoder.encode(input, "UTF-8"),
	    						" ", "+"), 
	    						"~", "%7E");
	    	} catch (UnsupportedEncodingException e) {
	    		e.printStackTrace();
	    		return "";
	    	}
	    }
	    
	    public static List<String> urlencode_rfc3986_list(Iterable<String> input) {
	    	List<String> converted = new ArrayList<String>();
	    	for (String element : input) {
	    		converted.add(urlencode_rfc3986(element));
	    	}
	    	return converted;
	    }


	    // Utility function for turning the Authorization: header into
	    // parameters, has to do some unescaping
	    // Can filter out any non-oauth parameters if needed (default behaviour)
	    // May 28th, 2010 - method updated to tjerk+meesters for a speed improvement+
	    //                  see http://code+google+com/p/oauth/issues/detail?id=163
	    public static Map<String, String> split_header(String header) {
	    	return split_header(header, true);
	    }
	    
	    public static Map<String, String> split_header(String header, boolean only_allow_oauth_parameters) {
	        Map<String, String> params = new HashMap<String, String>();
	        String regex = "/("+(only_allow_oauth_parameters ? "oauth_" : "")+"[a-z_-]*)=(:?\"([^\"]*)\"|([^,]*))/";
	        //PHP original:"/(".(only_allow_oauth_parameters ? 'oauth_' : '').'[a-z_-]*)=(:?"([^"]*)"|([^,]*))/', $header, $matches)) {
	        Pattern p = Pattern.compile(regex);
	        Matcher m = p.matcher(header);
	        boolean b = m.matches();  //does it match?
	        System.out.println("Parsing Header " + header);
	        for (int i = 1;  i<m.groupCount(); i++) {
	        	System.out.println("Index " + i + ": match: " + m.group(i));
	        	try {
					params.put("Something", URLDecoder.decode(m.group(i), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					params.put("Something", "");
				}
	        }
            if (params.containsKey("realm")) {
                params.remove("realm");
	        }

	        return params;

	    }

	    

	    // This function takes a input like a=b&a=c&d=e and returns the parsed
	    // parameters like this
	    // array("a" => array("b","c"), "d" => "e")
	    	
	    public static Map<String, List<String>> parse_parameters(String input) 
	    		throws UnsupportedEncodingException {
			final Map<String, List<String>> query_pairs = new LinkedHashMap<String, List<String>>();
			final String[] pairs = input.split("&");
			for (String pair : pairs) {
				final int idx = pair.indexOf("=");
				final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
				if (!query_pairs.containsKey(key)) {
					query_pairs.put(key, new LinkedList<String>());
				}
				final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
				query_pairs.get(key).add(value);
			}
	    	return query_pairs;
	    }



	    public static String build_http_query(Map<String, List<String>> parameters) {

	        if (parameters == null) return "";

	        // UrlEncode both keys and values
	        Map<String, List<String>> encParams = new TreeMap<String, List<String>>();

	        for (String key : parameters.keySet()) {
	        	String encKey = OAuthUtil.urlencode_rfc3986(key);
	        	List<String> valueList = parameters.get(key);
	        	List<String> encValues = new ArrayList<String>();
	        	for (String value : valueList) {
	        		String encVal = OAuthUtil.urlencode_rfc3986(value);
	        		encValues.add(encVal);
	        	}
	        	Collections.sort(encValues);
	        	encParams.put(encKey, encValues);
	        }
	        
	        StringBuilder sb = new StringBuilder();
	        for (String key : encParams.keySet()) {
	        	for (String val : encParams.get(key)) {
	        		sb.append(key);
	        		sb.append("=");
	        		sb.append(val);
	        		sb.append("&");
	        	}
	        }
	        sb.deleteCharAt(sb.length()-1);
	        return sb.toString();
	    }
}
