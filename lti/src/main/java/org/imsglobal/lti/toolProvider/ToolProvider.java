package org.imsglobal.lti.toolProvider;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.imsglobal.lti.LTIMessage;
import org.imsglobal.lti.LTIUtil;
import org.imsglobal.lti.product.Product;
import org.imsglobal.lti.product.ProductFamily;
import org.imsglobal.lti.profile.ProfileMessage;
import org.imsglobal.lti.profile.ProfileResourceHandler;
import org.imsglobal.lti.profile.ServiceDefinition;
import org.imsglobal.lti.toolProvider.dataConnector.DataConnector;
import org.imsglobal.lti.toolProvider.mediaType.ConsumerProfile;
import org.imsglobal.lti.toolProvider.mediaType.JSONContext;
import org.imsglobal.lti.toolProvider.mediaType.MediaTypeToolProxy;
import org.imsglobal.lti.toolProvider.mediaType.ToolService;
import org.joda.time.DateTime;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import net.oauth.OAuthValidator;
import net.oauth.SimpleOAuthValidator;
import net.oauth.server.HttpRequestMessage;
import net.oauth.server.OAuthServlet;
import net.oauth.signature.OAuthSignatureMethod;

public class ToolProvider {
	
	/**
	 * Class to represent an LTI Tool Provider
	 *
	 * @author  Stephen P Vickers <svickers@imsglobal.org>
	 * @copyright  IMS Global Learning Consortium Inc
	 * @date  2016
	 * @version  3.0.2
	 * @license  GNU Lesser General Public License, version 3 (<http://www.gnu.org/licenses/lgpl.html>)
	 */

	/**
	 * Default connection error message.
	 */
	    public static final String CONNECTION_ERROR_MESSAGE = "Sorry, there was an error connecting you to the application.";

	/**
	 * LTI version 1 for messages.
	 */
	    public static final String LTI_VERSION1 = "LTI-1p0";
	/**
	 * LTI version 2 for messages.
	 */
	    public static final String LTI_VERSION2 = "LTI-2p0";
	/**
	 * Use ID value only.
	 */
	    public static final int ID_SCOPE_ID_ONLY = 0;
	/**
	 * Prefix an ID with the consumer key.
	 */
	    public static final int ID_SCOPE_GLOBAL = 1;
	/**
	 * Prefix the ID with the consumer key and context ID.
	 */
	    public static final int ID_SCOPE_CONTEXT = 2;
	/**
	 * Prefix the ID with the consumer key and resource ID.
	 */
	    public static final int ID_SCOPE_RESOURCE = 3;
	/**
	 * Character used to separate each element of an ID.
	 */
	    public static final String ID_SCOPE_SEPARATOR = ":";

	/**
	 * Permitted LTI versions for messages.
	 */
	    private static final List<String> LTI_VERSIONS = Arrays.asList(LTI_VERSION1, LTI_VERSION2);
	    
	/**
	 * List of supported message types and associated class methods.
	 */
	    private static final Map<String, String> MESSAGE_TYPES;
	    static {
	        Map<String, String> aMap = new HashMap<String, String>();
	        aMap.put("basic-lti-launch-request", "onLaunch");
	        aMap.put("ContentItemSelectionRequest", "onContentItem");
	        aMap.put("ToolProxyRegistrationRequest", "register");
	        MESSAGE_TYPES = Collections.unmodifiableMap(aMap);
	    }
	    
	/**
	 * List of supported message types and associated class methods
	 *
	 * @var array METHOD_NAMES
	 */
	    private static final Map<String, String> METHOD_NAMES;
	    static {
	    	Map<String, String> bMap = new HashMap<String, String>();
	    	bMap.put("basic-lti-launch-request", "onLaunch");
	    	bMap.put("ContentItemSelectionRequest", "onContentItem");
	    	bMap.put("ToolProxyRegistrationRequest", "onRegister");
	    	METHOD_NAMES = Collections.unmodifiableMap(bMap);
	    }
	/**
	 * Names of LTI parameters to be retained in the consumer settings property.
	 *
	 * @var array LTI_CONSUMER_SETTING_NAMES
	 */
	    private static String[] LTI_CONSUMER_SETTING_NAMES = {"custom_tc_profile_url", 
	    													  "custom_system_setting_url"};
	    
	/**
	 * Names of LTI parameters to be retained in the context settings property.
	 *
	 * @var array LTI_CONTEXT_SETTING_NAMES
	 */
	    private static String[] LTI_CONTEXT_SETTING_NAMES = {"custom_context_setting_url",
	                                                         "custom_lineitems_url", 
	                                                         "custom_results_url",
	                                                         "custom_context_memberships_url"};
	/**
	 * Names of LTI parameters to be retained in the resource link settings property.
	 *
	 * @var array LTI_RESOURCE_LINK_SETTING_NAMES
	 */
	    private static String[] LTI_RESOURCE_LINK_SETTING_NAMES = {"lis_result_sourcedid", 
	    														"lis_outcome_service_url",
	                                                            "ext_ims_lis_basic_outcome_url", 
	    														"ext_ims_lis_resultvalue_sourcedids",
	                                                            "ext_ims_lis_memberships_id", 
	                                                            "ext_ims_lis_memberships_url",
	                                                            "ext_ims_lti_tool_setting", 
	                                                            "ext_ims_lti_tool_setting_id", 
	                                                            "ext_ims_lti_tool_setting_url",
	                                                            "custom_link_setting_url",
	                                                            "custom_lineitem_url", 
	                                                            "custom_result_url"};
	/**
	 * Names of LTI custom parameter substitution variables (or capabilities) and their associated default message parameter names.
	 *
	 * @var array CUSTOM_SUBSTITUTION_VARIABLES
	 */
	    private static final Map<String, String> CUSTOM_SUBSTITUTION_VARIABLES;
	    static {
	    	Map<String, String> cMap = new HashMap<String, String>();
	    	cMap.put("User.id", "user_id");
			cMap.put("User.image", "user_image");
			cMap.put("User.username", "username");
			cMap.put("User.scope.mentor", "role_scope_mentor");
			cMap.put("Membership.role", "roles");
			cMap.put("Person.sourcedId", "lis_person_sourcedid");
			cMap.put("Person.name.full", "lis_person_name_full");
			cMap.put("Person.name.family", "lis_person_name_family");
			cMap.put("Person.name.given", "lis_person_name_given");
			cMap.put("Person.email.primary", "lis_person_contact_email_primary");
			cMap.put("Context.id", "context_id");
			cMap.put("Context.type", "context_type");
			cMap.put("Context.title", "context_title");
			cMap.put("Context.label", "context_label");
			cMap.put("CourseOffering.sourcedId", "lis_course_offering_sourcedid");
			cMap.put("CourseSection.sourcedId", "lis_course_section_sourcedid");
			cMap.put("CourseSection.label", "context_label");
			cMap.put("CourseSection.title", "context_title");
			cMap.put("ResourceLink.id", "resource_link_id");
			cMap.put("ResourceLink.title", "resource_link_title");
			cMap.put("ResourceLink.description", "resource_link_description");
			cMap.put("Result.sourcedId", "lis_result_sourcedid");
			cMap.put("BasicOutcome.url", "lis_outcome_service_url");
			cMap.put("ToolConsumerProfile.url", "custom_tc_profile_url");
			cMap.put("ToolProxy.url", "tool_proxy_url");
			cMap.put("ToolProxy.custom.url", "custom_system_setting_url");
			cMap.put("ToolProxyBinding.custom.url", "custom_context_setting_url");
			cMap.put("LtiLink.custom.url", "custom_link_setting_url");
			cMap.put("LineItems.url", "custom_lineitems_url");
			cMap.put("LineItem.url", "custom_lineitem_url");
			cMap.put("Results.url", "custom_results_url");
			cMap.put("Result.url", "custom_result_url");
			cMap.put("ToolProxyBinding.memberships.url", "custom_context_memberships_url");
			CUSTOM_SUBSTITUTION_VARIABLES = Collections.unmodifiableMap(cMap);
	    }

	/**
	 * True if the last request was successful.
	 *
	 * @var boolean ok
	 */
	    private boolean ok = true;
	/**
	 * Tool Consumer object.
	 *
	 * @var ToolConsumer consumer
	 */
	    private ToolConsumer consumer = null;
	/**
	 * Return URL provided by tool consumer.
	 *
	 * @var URL returnUrl
	 */
	    private URL returnUrl = null;
	/**
	 * User object.
	 *
	 * @var User user
	 */
	    private User user = null;
	/**
	 * Resource link object.
	 *
	 * @var ResourceLink resourceLink
	 */
	    private ResourceLink resourceLink = null;
	/**
	 * Context object.
	 *
	 * @var Context context
	 */
	    private Context context = null;
	/**
	 * Data connector object.
	 *
	 * @var DataConnector dataConnector
	 */
	    private DataConnector dataConnector = null;
	/**
	 * Default email domain.
	 *
	 * @var string defaultEmail
	 */
	    private String defaultEmail = "";
	/**
	 * Scope to use for user IDs.
	 *
	 * @var int idScope
	 */
	    private int idScope = ID_SCOPE_ID_ONLY;
	/**
	 * Whether shared resource link arrangements are permitted.
	 *
	 * @var boolean allowSharing
	 */
	    private boolean allowSharing = false;
	/**
	 * Message for last request processed
	 *
	 * @var string message
	 */
	    private String message = CONNECTION_ERROR_MESSAGE;
	/**
	 * Error message for last request processed.
	 *
	 * @var string reason
	 */
	    private String reason = null;
	/**
	 * Details for error message relating to last request processed.
	 *
	 * @var array details
	 */
	    private List<String> details = new ArrayList<String>();
	/**
	 * Base URL for tool provider service
	 *
	 * @var string baseUrl
	 */
	  private URL baseUrl = null;
	/**
	 * Vendor details
	 *
	 * @var Item vendor
	 */
	  private ProductFamily vendor = null;
	/**
	 * Product details
	 *
	 * @var Item product
	 */
	  public Product product = null;
	/**
	 * Services required by Tool Provider
	 *
	 * @var array requiredServices
	 */
 	  private List<ToolService> requiredServices = new ArrayList<ToolService>();

	/**
	 * Optional services used by Tool Provider
	 *
	 * @var array optionalServices
	 */
  	  private List<ToolService> optionalServices = new ArrayList<ToolService>();
	/**
	 * Resource handlers for Tool Provider
	 *
	 * @var array resourceHandlers
	 */
	  private List<ProfileResourceHandler> resourceHandlers = new ArrayList<ProfileResourceHandler>();

	/**
	 * URL to redirect user to on successful completion of the request.
	 *
	 * @var string redirectUrl
	 */
	  private URL redirectUrl = null;
	/**
	 * URL to redirect user to on successful completion of the request.
	 *
	 * @var string mediaTypes
	 */
	    private Set<String> mediaTypes = new HashSet<String>();
	/**
	 * target for new document to be displayed
	 *
	 * @var string documentTargets
	 */
	    private Set<String> documentTargets = new HashSet<String>();
	/**
	 * HTML to be displayed on a successful completion of the request.
	 *
	 * @var string output
	 */
	    private String output = null;
	/**
	 * HTML to be displayed on an unsuccessful completion of the request and no return URL is available.
	 *
	 * @var string errorOutput
	 */
	    private String errorOutput = null;
	/**
	 * Whether debug messages explaining the cause of errors are to be returned to the tool consumer.
	 *
	 * @var boolean debugMode
	 */
	    private boolean debugMode = false;
	    
    /**
     *  URL to redirect user to if the request is not successful.
     */
      private String error = null;

	/**
	 * LTI parameter constraints for auto validation checks.
	 *
	 * @var array constraints
	 */
	    private Map<String, ParameterConstraint> constraints = new HashMap<String, ParameterConstraint>();
	    
	    private String messageType;
	    private String ltiVersion;

	    
	    private HttpServletRequest request;
	    private HttpServletResponse response;
      	
	/**
	 * Class constructor
	 *
	 * @param DataConnector     dataConnector    Object containing a database connection object
	 */
	    public ToolProvider(DataConnector dataConnector, HttpServletRequest request, HttpServletResponse response)
	    {
	        this.dataConnector = dataConnector;
	        ok = (dataConnector != null);
	        this.request = request;
	        this.response = response;
	        

	// Set debug mode
	        String customDebug = request.getParameter("custom_debug");
	        if(StringUtils.isNotEmpty(customDebug) && StringUtils.equalsIgnoreCase(customDebug, "true")) {
	        	setDebugMode(true);
	        }

	// Set return URL if available
            try {
		        String tryreturnUrl = request.getParameter("launch_presentation_return_url");
		        if (StringUtils.isNotEmpty(tryreturnUrl)) {
					setReturnUrl(new URL(tryreturnUrl));
		        } else {
		        	tryreturnUrl = request.getParameter("content_item_return_url");
		        	if (StringUtils.isNotEmpty(tryreturnUrl)) {
		        		setReturnUrl(new URL(tryreturnUrl));
		        	}
		        }
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}

	        setMessageType(request.getParameter("lti_message_type"));
	        setLTIVersion(request.getParameter("lti_version"));
	        vendor = new ProductFamily();
	        product = new Product();

	    }

	/**
	 * Process an incoming request
	 */
	    public void handleRequest()
	    {

	        if (ok) {
	            if (authenticate()) {
	                ok = doCallback();
	            }
	        }
	        if (ok) {
	        	result();
	        } else {
	        	onError();
	        }

	    }

	/**
	 * Add a parameter constraint to be checked on launch
	 *
	 * @param string name           Name of parameter to be checked
	 * @param boolean required      True if parameter is required (optional, default is true)
	 * @param int maxLength         Maximum permitted length of parameter value (optional, default is null)
	 * @param array messageTypes    Array of message types to which the constraint applies (optional, default is all)
	 */
	    public void setParameterConstraint(String name) {
	    	setParameterConstraint(name, true, 0, null);
	    }
	    public void setParameterConstraint(String name, boolean required) {
	    	setParameterConstraint(name, required, 0, null);
	    }
	    public void setParameterConstraint(String name, boolean required, int maxLength) {
	    	setParameterConstraint(name, required, maxLength, null);
	    }
	    public void setParameterConstraint(
	    		String name, 
	    		boolean required, 
	    		int maxLength, 
	    		String[] messageTypes)
	    {

	        name = StringUtils.trim(name);
	        if (name.length() > 0) {
	        	ParameterConstraint pc = new ParameterConstraint(required, maxLength);
	        	Set<String> types = new HashSet<String>();
	        	Collections.addAll(types, messageTypes);
	        	pc.setMessageTypes(types);
	        	constraints.put(name, pc);
	        }

	    }

	/**
	 * Get an array of defined tool consumers
	 *
	 * @return array Array of ToolConsumer objects
	 */
	    public List<ToolConsumer> getConsumers()
	    {

	        return dataConnector.getToolConsumers();

	    }

	/**
	 * Find an offered service based on a media type and HTTP action(s)
	 *
	 * @param string format  Media type required
	 * @param array  methods Array of HTTP actions required
	 *
	 * @return object The service object
	 */
	    public ServiceDefinition findService(String format, List<String> methods)
	    {

	        ServiceDefinition found = null;
	        List<ServiceDefinition> services = consumer.getProfile().getServicesOffered();
            for (ServiceDefinition service: services) {
                List<String> formats = service.getFormats();
                if (!formats.contains(format)) {
                	continue;
                }
                
                List<String> missing = new ArrayList<String>();
                List<String> actions = service.getActions();
                for (String method: methods) {
                	if (!actions.contains(method)) {
                		missing.add(method);
                	}
                }
                if (missing.isEmpty()) {
                    found = service;
                    break;
                }
            }

	        return found;

	    }

	/**
	 * Send the tool proxy to the Tool Consumer
	 *
	 * @return boolean True if the tool proxy was accepted
	 */
	    public boolean doToolProxyService()
	    {

	// Create tool proxy
	    	List<String> methods = new ArrayList<String>();
	    	methods.add("POST");
	        ServiceDefinition toolProxyService = findService("application/vnd.ims.lti.v2.toolproxy+json", methods);
	        String secret = DataConnector.getRandomString(12);
	        MediaTypeToolProxy toolProxy = new MediaTypeToolProxy(this, toolProxyService, secret);
	        Map<String, List<String>> proxyMap = toolProxy.toMap();
	        LTIMessage http = consumer.doServiceRequest(
	        		toolProxyService, 
	        		"POST", 
	        		"application/vnd.ims.lti.v2.toolproxy+json", 
	        		proxyMap);
	        ok = http.isOk() && (http.getStatus() == 201) && http.getResponseJson().containsKey("tool_proxy_guid") && (((String)http.getResponseJson().get("tool_proxy_guid")).length() > 0);
	        if (ok) {
	            consumer.setKey((String)http.getResponseJson().get("tool_proxy_guid"));
	            consumer.setSecret(toolProxy.getSecurityContract().getSharedSecret());
	            consumer.setToolProxyMap(proxyMap);
	            consumer.save();
	        }

	        return ok;

	    }

	/**
	 * Get an array of fully qualified user roles
	 *
	 * @param mixed roles  Comma-separated list of roles or array of roles
	 *
	 * @return array Array of roles
	 */
	    public static List<String> parseRoles(String roles) {
	    	List<String> roleList = new ArrayList<String>();
	    	for (String r : StringUtils.split(roles, ",")) {
	    		roleList.add(r);
	    	}
	    	return parseRoles(roleList);
	    }
	    public static List<String> parseRoles(List<String> roles)
	    {
	        List<String> parsedRoles = new ArrayList<String>();
	        for (String role : roles) {
	            role = StringUtils.trim(role);
	            if (StringUtils.isNotEmpty(role)) {
	                if (role.substring(0, 4) != "urn:") {
	                    role = "urn:lti:role:ims/lis/" + role;
	                }
	                parsedRoles.add(role);
	            }
	        }

	        return parsedRoles;

	    }

	/**
	 * Generate a web page containing an auto-submitted form of parameters.
	 *
	 * @param string url URL to which the form should be submitted
	 * @param array params Array of form parameters
	 * @param string target Name of target (optional)
	 * @return string
	 */
	    public static String sendForm(URL errorUrl, Map<String, List<String>> formParams) {
	    	return sendForm(errorUrl, formParams, "");
	    }
	    
	    public static String sendForm(URL url, Map<String, List<String>> params, String target)
	    {
	    	String page = "<html>\n"
	    			+ "<head>\n"
	    			+ "<title>IMS LTI message</title>\n"
	    			+ "<script type=\"text/javascript\">\n"
	    			+ "//<![CDATA[\n"
	    			+ "function doOnLoad() {\n"
	    			+ "    document.forms[0].submit();\n"
	    			+ "}\n\n"

	    			+ "window.onload=doOnLoad;\n"
	    			+ "//]]>\n"
	    			+ "</script>\n"
	    			+ "</head>\n"
	    			+ "<body>\n"
	    			+ "<form action=\"{url}\" method=\"post\" target=\"\" encType=\"application/x-www-form-urlencoded\">\n";


	        for(String key : params.keySet()) {
	        	for (String value : params.get(key)) {
		        	String key2 = StringEscapeUtils.escapeHtml4(key);
		        	String value2 = StringEscapeUtils.escapeHtml4(value);
		        	page += "<input type=\"hidden\" name=\"" + key2 + "\" value=\"" + value2 + "\" />\n\n";
	        	}

	        }

	        page += "</form>\n"
	        		+ "</body>\n"
	        		+ "</html>\n";

	        return page;

	    }

	///
	///    PROTECTED METHODS
	///

	/**
	 * Process a valid launch request
	 *
	 * @return boolean True if no error
	 */
	    protected boolean onLaunch()
	    {

	        return onError();

	    }

	/**
	 * Process a valid content-item request
	 *
	 * @return boolean True if no error
	 */
	    protected boolean onContentItem()
	    {

	        return onError();

	    }

	/**
	 * Process a valid tool proxy registration request
	 *
	 * @return boolean True if no error
	 */
	    protected boolean onRegister() {

	        return onError();

	    }

	/**
	 * Process a response to an invalid request
	 *
	 * @return boolean True if no further error processing required
	 */
	    protected boolean onError()
	    {
	    	try {
	    		throw new Exception("At onError");
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
	    	System.err.println(reason);
	    	if (debugMode) {
	    		for (String detail : details) {
	    			System.err.println(detail);
	    		}
	    	}
	    	return false;
	    }

	///
	///    PRIVATE METHODS
	///

	/**
	 * Call any callback function for the requested action.
	 *
	 * This function may set the redirect_url and output properties.
	 *
	 * @return boolean True if no error reported
	 */
	    private boolean doCallback() {
	    	String type = request.getParameter("lti_message_type");
	    	if (type != null) {
	    		if (METHOD_NAMES.containsKey(type)) {
	    			return doCallback(METHOD_NAMES.get(type));
	    		}
	    	}
	    	return false;
	    }
	    
	    private boolean doCallback(String method)
	    {

	        String callback = method;
	        Boolean retVal = null;
	        if (callback == null) {
	            callback = getMessageType();
	        }
	        Class<? extends ToolProvider> clazz = this.getClass();
	        boolean methodExists = false;
	        for (Method m : clazz.getDeclaredMethods()) {
	        	if (m.getName().equals(callback)) {
	        		methodExists = true;
					try {
						retVal = (Boolean) m.invoke(this);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} //returns a boolean
	        	}
	        }
	        if (!methodExists) { //didn"t find the method in declared methods
	        	if (StringUtils.isNotEmpty(method)) {
	        		reason = "Message type not supported: " + getMessageType();
	        		retVal = false;
	        	}
	        }
	        if (retVal && (getMessageType().equals("ToolProxyRegistrationRequest"))) {
	            consumer.save();
	        }
	        
	        return retVal;

	    }

	/**
	 * Perform the result of an action.
	 *
	 * This function may redirect the user to another URL rather than returning a value.
	 *
	 * @return string Output to be displayed (redirection, or display HTML or message)
	 */
	    private void result()
	    {
	    	if (!ok) {
	    		ok = onError();
	    	}
	    	if (!ok) {
	    		try {
		    		if (returnUrl != null) {
		    			URIBuilder errorUrlBuilder = new URIBuilder(returnUrl.toExternalForm());
		    			//add necessary query parameters to error url
		    			//first, figure out if we know the reason
		    			HashMap<String, String> params = new HashMap<String, String>();
			    		if (debugMode && StringUtils.isNotEmpty(reason)) {
			    			params.put("lti_errormsg", "Debug error: "+reason);
			    		} else {
			    			params.put("lti_errormsg", message);
			    			if (StringUtils.isNotEmpty(reason)) {
			    				params.put("lti_errorlog", "Debug error: " + reason);
			    			}
			    		}
		    			for (String key : params.keySet()) {
		    				errorUrlBuilder.addParameter(key, params.get(key));
		    			}
			    		URL errorUrl;
					
						errorUrl = errorUrlBuilder.build().toURL();
					
			    		if (consumer != null && request != null) {
			    			String ltiMessageType = request.getParameter("lti_message_type");
			    			if (ltiMessageType.equals("ContentItemSelectionRequest")) {
			    				String version = request.getParameter("lti_version");
			    				if (version == null) {
			    					version = LTI_VERSION1;
			    				}
			    				Map<String, List<String>> toSend = new HashMap<String, List<String>>();
								Map<String, String[]> formParams = request.getParameterMap();
			    				for (String key : formParams.keySet()) {
			    					for (String v : formParams.get(key)) {
			    						LTIUtil.setParameter(toSend, key, v);
			    					}
			    				}
			    				Map<String, List<String>> signedParams = 
			    						consumer.signParameters(
			    								errorUrl.toExternalForm(), 
			    								"ContentItemSelection", 
			    								version, 
			    								"POST",
			    								toSend);
			    				String page = sendForm(errorUrl, signedParams);
			    				System.out.print(page);
			    			} else {
			    				System.err.println("Attempt to redirect to " + errorUrl);
			    				response.sendRedirect(errorUrl.toExternalForm());
			    			}
			    		} else {
			    			try {
			    				System.err.println("Attempt to redirect to " + errorUrl);
			    				response.sendRedirect(errorUrl.toExternalForm());
			    			} catch (IOException ioe) {
			    				return;
			    			}
			    		}
			    		return;
			   		} else {
			   			if (errorOutput != null) {
			   				System.err.println(errorOutput);
			   			} else if (debugMode && StringUtils.isNotEmpty(reason)) {
			   				System.err.println("Debug error: " + reason);
			   			} else {
			   				System.err.println("Error: " + message);
		    	        }
			   		}
	    		} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}	
		   	} else if (this.redirectUrl != null) {
		   		try {
    				System.err.println("Attempt to redirect to " + redirectUrl);
		   			this.response.sendRedirect(this.redirectUrl.toExternalForm());
		   		} catch (IOException ioe) {
		   			return;
		   		}
		   		return;
		   	} else if (output != null) {
		   		System.out.println(output);
		   	} else {
		   		System.out.println("Successful handling of request finished.");
		   	}
	    }
	   

	/**
	 * Check the authenticity of the LTI launch request.
	 *
	 * The consumer, resource link and user objects will be initialised if the request is valid.
	 *
	 * @return boolean True if the request has been successfully validated.
	 */
	    private boolean authenticate()
	    {
	    	boolean doSaveConsumer = false;
	    	//JSON Initialization
	    	JSONParser parser = new JSONParser();
    		JSONObject tcProfile = null;
    		
	    	String messageType = request.getParameter("lti_message_type");
	    	ok = (StringUtils.isNotEmpty(messageType) && MESSAGE_TYPES.containsKey(messageType));
	    	if (!ok) {
	    		reason = "Invalid or missing lti_message_type parameter.";
	    	}
	    	if (ok) {
	    		String version = request.getParameter("lti_version");
	    		ok = (StringUtils.isNotEmpty(version)) &&
	    		     (LTI_VERSIONS.contains(version));
	    		if (!ok) {
	    			reason = "Invalid or missing lti_version parameter.";
	    		}
	    	}
	    	if (ok) {
	    		if (messageType.equals("basic-lti-launch-request")) {
	    			String resLinkId = request.getParameter("resource_link_id");
	    			ok = (StringUtils.isNotEmpty(resLinkId));
	    			if (!ok) {
	    				reason = "Missing resource link ID.";
	    			}
	    		} else if (messageType.equals("ContentItemSelectionRequest")) {
	    			String accept = request.getParameter("accept_media_types");
	    			ok = (StringUtils.isNotEmpty(accept));
	    			if (ok) {
		    			String[] mediaTypes = StringUtils.split(accept, ",");
		    			Set<String> mTypes = new HashSet<String>();
		    			for (String m : mediaTypes) {
		    				mTypes.add(m); //unique set of media types, no repeats
		    			}
		    			ok = mTypes.size() > 0;
		    			if (!ok) {
		    				reason = "No accept_media_types found.";
		    			} else {
		    				this.mediaTypes = mTypes;
		    			}
	    			} else { //no accept 
	    				ok = false;
	    			}
	    			String targets = request.getParameter("accept_presentation_document_targets");
	    			if (ok && StringUtils.isNotEmpty(targets)) {
	    				Set<String> documentTargets = new HashSet<String>();
	    				for (String t : StringUtils.split(targets, ",")) {
	    					documentTargets.add(t);
	    				}
	    				ok = documentTargets.size() > 0;
	    				if (!ok) {
	    					reason = "Missing or empty accept_presentation_document_targets parameter.";
	    				} else {
	    					List<String> valid = Arrays.asList("embed", "frame", "iframe", "window", "popup", "overlay", "none");
	    					boolean thisCheck = true;
	    					String problem = "";
	    					for (String target : documentTargets) {
	    						thisCheck = valid.contains(target);
	    						if (!thisCheck) {
	    							problem = target;
	    							break;
	    						}
	    					}
	    					if (!thisCheck) {
	    						reason = "Invalid value in accept_presentation_document_targets parameter: " + problem;
	    					}
	    				}
	    				if (ok) {
	    					this.documentTargets = documentTargets;
	    				}
	    			} else {
	    				ok = false;
	    			}
	    			if (ok) {
	    				String ciReturnUrl = request.getParameter("content_item_return_url");
	    				ok = StringUtils.isNotEmpty(ciReturnUrl);
	    				if (!ok) {
	    					reason = "Missing content_item_return_url parameter.";
	    				}
	    			}
	    		} else if (messageType.equals("ToolProxyRegistrationRequest")) {
	    			String regKey = request.getParameter("reg_key");
	    			String regPass = request.getParameter("reg_password");
	    			String profileUrl = request.getParameter("tc_profile_url");
	    			String launchReturnUrl = request.getParameter("launch_presentation_return_url");
	    			ok = StringUtils.isNotEmpty(regKey) && StringUtils.isNotEmpty(regPass)
	    					&& StringUtils.isNotEmpty(profileUrl) && StringUtils.isNotEmpty(launchReturnUrl);
	    			if (debugMode && !ok) {
	    				reason = "Missing message parameters.";
	    			}
	    		}
	    	}
	    	DateTime now = DateTime.now();
	    	//check consumer key
	    	if (ok && !messageType.equals("ToolProxyRegistrationRequest")) {
	    		String key = request.getParameter("oauth_consumer_key");
	    		ok = StringUtils.isNotEmpty(key);
	    		if (!ok) {
	    			reason = "Missing consumer key.";
	    		}
	    		if (ok) {
	    			this.consumer = new ToolConsumer(key, dataConnector);
	    			ok = consumer.getCreated() != null;
	    			if (!ok) {
	    				reason = "Invalid consumer key.";
	    			}
	    		}
	    		if (ok) {
	    			if (consumer.getLastAccess() == null) {
	    				doSaveConsumer = true;
	    			} else {
	    				DateTime last = consumer.getLastAccess();
	    				doSaveConsumer = doSaveConsumer || 
	    						last.isBefore(now.withTimeAtStartOfDay());
	    			}
	    			consumer.setLastAccess(now);
	    			String baseString = "";
	    			String signature = "";
	    			OAuthMessage oAuthMessage = null;
	    			try {
						OAuthConsumer oAuthConsumer = new OAuthConsumer("about:blank", consumer.getKey(), consumer.getSecret(), null);
						OAuthAccessor oAuthAccessor = new OAuthAccessor(oAuthConsumer);
						OAuthValidator oAuthValidator = new SimpleOAuthValidator();
						URL u = new URL(request.getRequestURI());
						String url = u.getProtocol() + "://" + u.getHost() + u.getPath();
						Map<String, String[]> params = request.getParameterMap();
						Map<String, List<String>> param2 = new HashMap<String, List<String>>();
						for (String k : params.keySet()) {
							param2.put(k, Arrays.asList(params.get(k)));
						}
						List<Map.Entry<String, String>> param3 = ToolConsumer.convert(param2);
						oAuthMessage = new OAuthMessage(request.getMethod(), url, param3);
						baseString = OAuthSignatureMethod.getBaseString(oAuthMessage);
						signature = oAuthMessage.getSignature();
						oAuthValidator.validateMessage(oAuthMessage, oAuthAccessor);						
					} catch (Exception e) {
						System.err.println(e.getMessage());
						OAuthProblemException oe = null;
						if (e instanceof OAuthProblemException) {
							oe = (OAuthProblemException) e;
							for(String p : oe.getParameters().keySet()) {
								System.err.println(p + ": " + oe.getParameters().get(p).toString());
							}
						}
						this.ok = false;
						if (StringUtils.isEmpty(reason)) {
							if (debugMode) {								
								reason = e.getMessage();
								if (StringUtils.isEmpty(reason)) {
									reason = "OAuth exception.";
								}
								details.add("Timestamp: " + request.getParameter("oauth_timestamp"));
								details.add("Current system time: " + System.currentTimeMillis());
	                            details.add("Signature: " + signature);
	                            details.add("Base string: " + baseString);
							} else {
								reason = "OAuth signature check failed - perhaps an incorrect secret or timestamp.";
							}
						}
					}
	    		}
	    		if (ok) {
	    			DateTime today = DateTime.now();
	    			if (consumer.getLastAccess() == null) {
	    				doSaveConsumer = true;
	    			} else {
	    				DateTime last = consumer.getLastAccess();
	    				doSaveConsumer = doSaveConsumer || last.isBefore(today.withTimeAtStartOfDay());
	    			}
	    			consumer.setLastAccess(today);
	    			if (consumer.isThisprotected()) {
	    				String guid = request.getParameter("tool_consumer_instance_guid");
	    				if (StringUtils.isNotEmpty(consumer.getConsumerGuid())) {
	                        ok = StringUtils.isEmpty(guid) || consumer.getConsumerGuid().equals(guid);
	                        if (!ok) {
	                            reason = "Request is from an invalid tool consumer.";
	                        }
	                    } else {
	                        ok = StringUtils.isEmpty(guid);
	                        if (!ok) {
	                            reason = "A tool consumer GUID must be included in the launch request.";
	                        }
	                    }
	                }
	                if (ok) {
	                    ok = consumer.isEnabled();
	                    if (!ok) {
	                        reason = "Tool consumer has not been enabled by the tool provider.";
	                    }
	                }
	                if (ok) {
	                    ok = consumer.getEnableFrom() == null || consumer.getEnableFrom().isBefore(today);
	                    if (ok) {
	                        ok = consumer.getEnableUntil() == null || consumer.getEnableUntil().isAfter(today);
	                        if (!ok) {
	                            reason = "Tool consumer access has expired.";
	                        }
	                    } else {
	                        reason = "Tool consumer access is not yet available.";
	                    }
	                }
	            }

	// Validate other message parameter values
	            if (ok) {
	            	List<String> boolValues = Arrays.asList("true", "false");
	            	String acceptUnsigned = request.getParameter("accept_unsigned");
	            	String acceptMultiple = request.getParameter("accept_multiple");
	            	String acceptCopyAdvice = request.getParameter("accept_copy_advice");
	            	String autoCreate = request.getParameter("auto_create");
	            	String canConfirm = request.getParameter("can_confirm");
	            	String lpdt = request.getParameter("launch_presentation_document_target");
	                if (messageType.equals("ContentItemSelectionRequest")) {
	                    if (StringUtils.isNotEmpty(acceptUnsigned)) {
	                    	ok = boolValues.contains(acceptUnsigned);
	                    	if (!ok) {
	                    		reason = "Invalid value for accept_unsigned parameter: " + acceptUnsigned + ".";
	                    	}
	                    }
	                    if (ok && StringUtils.isNotEmpty(acceptMultiple)) {
	                    	ok = boolValues.contains(acceptMultiple);
	                    	if (!ok) {
	                    		reason = "Invalid value for accept_multiple parameter: " + acceptMultiple + ".";
	                    	}
	                    }
	                    if (ok && StringUtils.isNotEmpty(acceptCopyAdvice)) {
	                    	ok = boolValues.contains(acceptCopyAdvice);
	                    	if (!ok) {
	                    		reason = "Invalid value for accept_copy_advice parameter: " + acceptCopyAdvice + ".";
	                    	}
	                    }
	                    if (ok && StringUtils.isNotEmpty(autoCreate)) {
	                    	ok = boolValues.contains(autoCreate);
	                    	if (!ok) {
	                    		reason = "Invalid value for auto_create parameter: " + autoCreate + ".";
	                    	}
	                    }
	                    if (ok && StringUtils.isNotEmpty(canConfirm)) {
	                    	ok = boolValues.contains(canConfirm);
	                    	if (!ok) {
	                    		reason = "Invalid value for can_confirm parameter: " + canConfirm + ".";
	                    	}
	                    }
	                } else if (StringUtils.isNotEmpty(lpdt)) {
	                	List<String> valid = Arrays.asList("embed", "frame", "iframe", "window", "popup", "overlay", "none");
    					ok = valid.contains(lpdt);
    					if (!ok) {
    						reason = "Invalid value for launch_presentation_document_target parameter: " + lpdt +".";
    	                }
	                }
	            }
	        }

	        if (ok && (messageType.equals("ToolProxyRegistrationRequest"))) {

	            ok = request.getParameter("lti_version").equals(LTI_VERSION2);
	            if (!ok) {
	                reason = "Invalid lti_version parameter";
	            }
	            if (ok) {
	            	HttpClient client = HttpClientBuilder.create().build();
	            	String tcProfUrl = request.getParameter("tc_profile_url");
	            	HttpGet get = new HttpGet(tcProfUrl);
	            	get.addHeader("Accept","application/vnd.ims.lti.v2.toolconsumerprofile+json");
	            	HttpResponse response = null;
					try {
						response = client.execute(get);
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
	            	if (response == null) {
	            		reason = "Tool consumer profile not accessible.";
	            	} else {
	            		try {
	            			String json_string = EntityUtils.toString(response.getEntity());
	            			tcProfile = (JSONObject)parser.parse(json_string);
		            		ok = tcProfile != null;
		            		if (!ok) {
		            			reason = "Invalid JSON in tool consumer profile.";
		            		} else {
		            			JSONContext jsonContext = new JSONContext();
		            			jsonContext.parse(tcProfile);
		            			consumer.getProfile().setContext(jsonContext);
		            		}
	            		} catch (Exception je) {
	            			je.printStackTrace();
	            			ok = false;
	            			reason = "Invalid JSON in tool consumer profile.";
	            		}
	                }
	            }
	// Check for required capabilities
	            if (ok) {
	            	String regKey = request.getParameter("reg_key");
	                consumer = new ToolConsumer(regKey, dataConnector);
	                ConsumerProfile profile = new ConsumerProfile();
	                JSONContext context = new JSONContext();
	                context.parse(tcProfile);
	                profile.setContext(context);
	                consumer.setProfile(profile);
	                List<String> capabilities = consumer.getProfile().getCapabilityOffered();
	                List<String> missing = new ArrayList<String>();
	                for (ProfileResourceHandler handler : resourceHandlers) {
	                	for (ProfileMessage message : handler.getRequiredMessages()) {
	                		String type = message.getType();
	                		if (!capabilities.contains(type)) {
	                			missing.add(type);
	                		}
	                	}
	                }
	                for (String name : constraints.keySet()) {
	                	ParameterConstraint constraint = constraints.get(name);
	                    if (constraint.isRequired()) {
	                        if (!capabilities.contains(name)) {
	                            missing.add(name);
	                        }
	                    }
	                }
	                if (!missing.isEmpty()) {
	                	StringBuilder sb = new StringBuilder();
	                	for (String cap : missing) {
	                		sb.append(cap).append(", ");
	                	}
	                    reason = "Required capability not offered - \"" + sb.toString() + "\"";
	                    ok = false;
	                }
	            }
	// Check for required services
	            if (ok) {
	                for (ToolService tService : requiredServices) {
	                    for (String format: tService.getFormats()) {
	                    	ServiceDefinition sd = findService(format, tService.getActions());
	                        if (sd == null) {
	                            if (ok) {
	                                reason = "Required service(s) not offered - ";
	                                ok = false;
	                            } else {
	                                reason += ", ";
	                            }
	                            StringBuilder sb = new StringBuilder();
	                            for (String a : tService.getActions()) {
	                            	sb.append(a).append(", ");
	                            }
	                            reason += format + "[" + sb.toString() + "]";
	                        }
	                    }
	                }
	            }
	            if (ok) {
	                if (messageType.equals("ToolProxyRegistrationRequest")) {
	                	ConsumerProfile profile = new ConsumerProfile();
		                JSONContext context = new JSONContext();
		                context.parse(tcProfile);
		                profile.setContext(context);
		                consumer.setProfile(profile);
	                    consumer.setSecret(request.getParameter("reg_password"));
	                    consumer.setLtiVersion(request.getParameter("lti_version"));
	                    consumer.setName(profile.getProduct().getProductInfo().getProductName().get("default_name"));
	                    consumer.setConsumerName(consumer.getName());
	                    consumer.setConsumerVersion("{tcProfile.product_instance.product_info.product_family.code}-{tcProfile.product_instance.product_info.product_version}");
	                    consumer.setConsumerGuid(profile.getProduct().getGuid());
	                    consumer.setEnabled(true);
	                    consumer.setThisprotected(true);
	                    doSaveConsumer = true;
	                }
	            }
	        } else if (ok && !request.getParameter("custom_tc_profile_url").isEmpty() && consumer.getProfile() == null) {
	        	String tcProfUrl = request.getParameter("custom_tc_profile_url");
	        	HttpClient client = HttpClientBuilder.create().build();
	        	HttpGet get = new HttpGet(tcProfUrl);
            	get.addHeader("Accept","application/vnd.ims.lti.v2.toolconsumerprofile+json");
            	HttpResponse response = null;
				try {
					response = client.execute(get);
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
            	if (response == null) {
            		reason = "Tool consumer profile not accessible.";
            	} else {
            		try {
            			String json_string = EntityUtils.toString(response.getEntity());
            			tcProfile = (JSONObject)parser.parse(json_string);
	            		ok = tcProfile != null;
	            		if (!ok) {
	            			reason = "Invalid JSON in tool consumer profile.";
	            		} else {
	            			JSONContext jsonContext = new JSONContext();
	            			jsonContext.parse(tcProfile);
	            			consumer.getProfile().setContext(jsonContext);
	            			doSaveConsumer = true;
	            		}
            		} catch (Exception je) {
            			je.printStackTrace();
            			ok = false;
            			reason = "Invalid JSON in tool consumer profile.";
            		}
                }
	        }

	// Validate message parameter constraints
	        if (ok) {
	            List<String> invalidParameters = new ArrayList<String>();
	            for (String name : constraints.keySet()) {
	            	ParameterConstraint constraint = constraints.get(name);
	                if (constraint.getMessageTypes().isEmpty() || constraint.getMessageTypes().contains(messageType)) {
	                    ok = true;
	                    String n = request.getParameter("name");
	                    if (constraint.isRequired()) {
	                    	n = n.trim();
	                        if (StringUtils.isBlank(n)) {
	                            invalidParameters.add(name + " (missing)");
	                            ok = false;
	                        }
	                    }
	                    if (ok && constraint.getMaxLength() > 0 && StringUtils.isNotBlank(n)) {
	                        if (n.trim().length()>constraint.getMaxLength()) {
	                            invalidParameters.add(name + " (too long)");
	                        }
	                    }
	                }
	            }
	            if (invalidParameters.size() > 0) {
	                ok = false;
	                if (StringUtils.isEmpty(reason)) {
	                	StringBuilder sb = new StringBuilder();
	                	for (String ip : invalidParameters) {
	                		sb.append(ip).append(", ");
	                	}
	                    reason = "Invalid parameter(s): " + sb.toString() +  ".";
	                }
	            }
	        }

	        if (ok) {

	// Set the request context
	        	String cId = request.getParameter("context_id");
	            if (StringUtils.isNotEmpty(cId)) {
	                context = Context.fromConsumer(consumer, cId.trim());
	                String title = request.getParameter("context_title");
	                if (StringUtils.isNotEmpty(title)) {
	                    title = title.trim();
	                }
	                if (StringUtils.isBlank(title)) {
	                    title = "Course " + context.getId();
	                }
	                context.setTitle(title);
	            }

	// Set the request resource link
	            String rlId = request.getParameter("resource_link_id");
	            if (StringUtils.isNotEmpty(rlId)) {
	            	String contentItemId = request.getParameter("custom_content_item_id");
	                resourceLink = ResourceLink.fromConsumer(consumer, rlId, contentItemId);
	                if (context != null) {
	                    resourceLink.setContextId(context.getRecordId());
	                }
	                String title = request.getParameter("resource_link_title");
	                if (StringUtils.isEmpty(title)) {
	                    title = "Resource " + resourceLink.getId();
	                }
	                resourceLink.setTitle(title);
	// Delete any existing custom parameters
	                for (String name : consumer.getSettings().keySet()) {
	                    if (StringUtils.startsWith(name, "custom_")) {
	                        consumer.setSetting(name);
	                        doSaveConsumer = true;
	                    }
	                }
	                if (context != null) {
	                    for (String name : context.getSettings().keySet()) {
	                        if (StringUtils.startsWith(name, "custom_")) {
	                            context.setSetting(name, "");
	                        }
	                    }
	                }
	                for (String name : resourceLink.getSettings().keySet()) {
	                    if (StringUtils.startsWith(name, "custom_")) {
	                        resourceLink.setSetting(name, "");
	                    }
	                }
	// Save LTI parameters
	                for (String name : LTI_CONSUMER_SETTING_NAMES) {
	                	String s = request.getParameter(name);
	                	if (StringUtils.isNotBlank(s)) {
	                		consumer.setSetting(name, s);
	                    } else {
	                        consumer.setSetting(name);
	                    }
	                }
	                if (context != null) {
	                    for (String name : LTI_CONTEXT_SETTING_NAMES) {
	                    	String s = request.getParameter(name);
	                        if (StringUtils.isNotBlank(s)) {
	                            context.setSetting(name, s);
	                        } else {
	                            context.setSetting(name, "");
	                        }
	                    }
	                }
	                for (String name : LTI_RESOURCE_LINK_SETTING_NAMES) {
	                	String sn = request.getParameter(name);
	                    if (StringUtils.isNotEmpty(sn)) {
	                        resourceLink.setSetting(name, sn);
	                    } else {
	                        resourceLink.setSetting(name, "");
	                    }
	                }
	// Save other custom parameters
	                ArrayList<String> combined = new ArrayList<String>();
	                combined.addAll(Arrays.asList(LTI_CONSUMER_SETTING_NAMES));
	                combined.addAll(Arrays.asList(LTI_CONTEXT_SETTING_NAMES));
	                combined.addAll(Arrays.asList(LTI_RESOURCE_LINK_SETTING_NAMES));
	                for (String name : request.getParameterMap().keySet()) {
	                	String value = request.getParameter(name);
	                	if (StringUtils.startsWith(name, "custom_") &&
	                		!combined.contains(name)) {
	                		resourceLink.setSetting(name, value);
	                	}
	                }
	            }

	// Set the user instance
	            String userId = request.getParameter("user_id");
	            if (StringUtils.isNotEmpty(userId)) {
	                userId = userId.trim();
	            }

	            user = User.fromResourceLink(resourceLink, userId);

	// Set the user name
	            String firstname = request.getParameter("lis_person_name_given");
	            String lastname = request.getParameter("lis_person_name_family");
	            String fullname = request.getParameter("lis_person_name_full");
	            user.setNames(firstname, lastname, fullname);

	// Set the user email
	            String email = request.getParameter("lis_person_contact_email_primary");
	            user.setEmail(email, defaultEmail);

	// Set the user image URI
	            String img = request.getParameter("user_image");
	            if (StringUtils.isNotEmpty(img)) {
	            	try {
	            		URL imgUrl = new URL(img);
	            		user.setImage(imgUrl);
	            	} catch (Exception e) {
	            		//bad url
	            	}
	            }

	// Set the user roles
	            String roles = request.getParameter("roles");
	            if (StringUtils.isNotEmpty(roles)) {
	                user.setRoles(parseRoles(roles));
	            }

	// Initialise the consumer and check for changes
	            consumer.setDefaultEmail(defaultEmail);
	            String ltiV = request.getParameter("lti_version");
	            if (!ltiV.equals(consumer.getLtiVersion())) {
	                consumer.setLtiVersion(ltiV);
	                doSaveConsumer = true;
	            }
	            String instanceName = request.getParameter("tool_consumer_instance_name");
	            if (StringUtils.isNotEmpty(instanceName)) {
	                if (!instanceName.equals(consumer.getConsumerName())) {
	                    consumer.setConsumerName(instanceName);
	                    doSaveConsumer = true;
	                }
	            }
	            String familyCode = request.getParameter("tool_consumer_info_product_family_code");
	            String extLMS = request.getParameter("ext_lms");
	            if (StringUtils.isNotBlank(familyCode)) {
	                String version = familyCode;
	                String infoVersion = request.getParameter("tool_consumer_info_version");
	                if (StringUtils.isNotEmpty(infoVersion)) {
	                    version += "-" + infoVersion;
	                }
	// do not delete any existing consumer version if none is passed
	                if (!version.equals(consumer.getConsumerVersion())) {
	                    consumer.setConsumerVersion(version);
	                    doSaveConsumer = true;
	                }
	            } else if (StringUtils.isNotEmpty(extLMS) && !consumer.getConsumerName().equals(extLMS)) {
	                consumer.setConsumerVersion(extLMS);
	                doSaveConsumer = true;
	            }
	            String tciGuid = request.getParameter("tool_consumer_instance_guid");
	            if (StringUtils.isNotEmpty(tciGuid)) {
	                if (StringUtils.isNotEmpty(consumer.getConsumerGuid())) {
	                    consumer.setConsumerGuid(tciGuid);
	                    doSaveConsumer = true;
	                } else if (!consumer.isThisprotected()) {
	                    doSaveConsumer = (!tciGuid.equals(consumer.getConsumerGuid()));
	                    if (doSaveConsumer) {
	                        consumer.setConsumerGuid(tciGuid);
	                    }
	                }
	            }
	            String css = request.getParameter("launch_presentation_css_url");
	            String extCss = request.getParameter("ext_launch_presentation_css_url");
	            if (StringUtils.isNotEmpty(css)) {
	                if (!css.equals(consumer.getCssPath())) {
	                    consumer.setCssPath(css);
	                    doSaveConsumer = true;
	                }
	            } else if (StringUtils.isNotEmpty(extCss) &&
	                 !consumer.getCssPath().equals(extCss)) {
	                consumer.setCssPath(extCss);
	                doSaveConsumer = true;
	            } else if (StringUtils.isNotEmpty(consumer.getCssPath())) {
	                consumer.setCssPath(null);
	                doSaveConsumer = true;
	            }
	        }

	// Persist changes to consumer
	        if (doSaveConsumer) {
	            consumer.save();
	        }
	        if (ok && context != null) {
	            context.save();
	        }
	        if (ok && resourceLink != null) {

	// Check if a share arrangement is in place for this resource link
	            ok = checkForShare();

	// Persist changes to resource link
	            resourceLink.save();

	// Save the user instance
	            String lrsdid = request.getParameter("lis_result_sourcedid");
	            if (StringUtils.isNotEmpty(lrsdid)) {
	                if (!lrsdid.equals(user.getLtiResultSourcedId())) {
	                    user.setLtiResultSourcedId(lrsdid);
	                    user.save();
	                }
	            } else if (StringUtils.isNotEmpty(user.getLtiResultSourcedId())) {
	                user.setLtiResultSourcedId("");
	                user.save();
	            }
	        }

	        return ok;

	    }


	/**
	 * Check if a share arrangement is in place.
	 *
	 * @return boolean True if no error is reported
	 */
	    private boolean checkForShare()
	    {

	        ok = true;
	        boolean doSaveResourceLink = true;

	        String key = this.resourceLink.getPrimaryConsumerKey();
	        int id = this.resourceLink.getPrimaryResourceLinkId();
	        String shareKeyValue = this.request.getParameter("custom_share_key");

	        boolean isShareRequest = (shareKeyValue != null) && (shareKeyValue.length() > 0);
	        if (isShareRequest) {
	            if (!allowSharing) {
	                ok = false;
	                reason = "Your sharing request has been refused because sharing is not being permitted.";
	            } else {
	// Check if this is a new share key
	            	ResourceLinkShareKey shareKey = new ResourceLinkShareKey(resourceLink, shareKeyValue);
	            	
	                if ((shareKey.getPrimaryConsumerKey() != null) && (shareKey.getResourceLinkId() != 0)) {
	// Update resource link with sharing primary resource link details
	                    key = shareKey.getPrimaryConsumerKey();
	                    id = shareKey.getResourceLinkId();
	                    ok = (!key.equals(consumer.getKey()) || !(id == resourceLink.getRecordId()));
	                    if (ok) {
	                        resourceLink.setPrimaryConsumerKey(key);
	                        resourceLink.setPrimaryResourceLinkId(id);
	                        resourceLink.setShareApproved(shareKey.isAutoApprove());
	                        ok = resourceLink.save();
	                        if (ok) {
	                            doSaveResourceLink = false;
	                            user.getResourceLink().setPrimaryConsumerKey(key);
	                            user.getResourceLink().setPrimaryResourceLinkId(id);
	                            user.getResourceLink().setShareApproved(shareKey.isAutoApprove());
	                            user.getResourceLink().setUpdated(DateTime.now());
	// Remove share key
	                            shareKey.delete();
	                        } else {
	                            reason = "An error occurred initialising your share arrangement.";
	                        }
	                    } else {
	                        reason = "It is not possible to share your resource link with yourself.";
	                    }
	                }
	                if (ok) {
	                    ok = StringUtils.isNotEmpty(key);
	                    if (!ok) {
	                        reason = "You have requested to share a resource link but none is available.";
	                    } else {
	                        ok = (user.getResourceLink().isShareApproved());
	                        if (!ok) {
	                            reason = "Your share request is waiting to be approved.";
	                        }
	                    }
	                }
	            }
	        } else {
	// Check no share is in place
	            ok = id == 0;
	            if (!ok) {
	                reason = "You have not requested to share a resource link but an arrangement is currently in place.";
	            }
	        }

	// Look up primary resource link
	        if (ok && id != 0) {
	            consumer = new ToolConsumer(key, dataConnector);
	            ok = (consumer.getCreated() != null);
	            if (ok) {
	                resourceLink = ResourceLink.fromConsumerWithPK(consumer, id);
	                ok = (resourceLink.getCreated() != null);
	            }
	            if (ok) {
	                if (doSaveResourceLink) {
	                    resourceLink.save();
	                }
	            } else {
	                reason = "Unable to load resource link being shared.";
	            }
	        }

	        return ok;

	    }


	public ToolConsumer getConsumer() {
		return this.consumer;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public List<ProfileResourceHandler> getResourceHandlers() {
		return resourceHandlers;
	}

	public void setResourceHandlers(List<ProfileResourceHandler> resourceHandlers) {
		this.resourceHandlers = resourceHandlers;
	}

	public ProductFamily getVendor() {
		return vendor;
	}

	public void setVendor(ProductFamily vendor) {
		this.vendor = vendor;
	}

	public URL getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(URL baseUrl) {
		this.baseUrl = baseUrl;
	}

	public List<ToolService> getRequiredServices() {
		return requiredServices;
	}

	public void setRequiredServices(List<ToolService> requiredServices) {
		this.requiredServices = requiredServices;
	}

	public List<ToolService> getOptionalServices() {
		return optionalServices;
	}

	public void setOptionalServices(List<ToolService> optionalServices) {
		this.optionalServices = optionalServices;
	}

	public boolean isOk() {
		return ok;
	}

	public void setOk(boolean ok) {
		this.ok = ok;
	}

	public URL getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(URL returnUrl) {
		this.returnUrl = returnUrl;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public ResourceLink getResourceLink() {
		return resourceLink;
	}

	public void setResourceLink(ResourceLink resourceLink) {
		this.resourceLink = resourceLink;
	}

	public DataConnector getDataConnector() {
		return dataConnector;
	}

	public void setDataConnector(DataConnector dataConnector) {
		this.dataConnector = dataConnector;
	}

	public String getDefaultEmail() {
		return defaultEmail;
	}

	public void setDefaultEmail(String defaultEmail) {
		this.defaultEmail = defaultEmail;
	}

	public int getIdScope() {
		return idScope;
	}

	public void setIdScope(int idScope) {
		this.idScope = idScope;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public List<String> getDetails() {
		return details;
	}

	public void setDetails(List<String> details) {
		this.details = details;
	}

	public URL getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(URL redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public Set<String> getMediaTypes() {
		return mediaTypes;
	}

	public void setMediaTypes(Set<String> mediaTypes) {
		this.mediaTypes = mediaTypes;
	}

	public Set<String> getDocumentTargets() {
		return documentTargets;
	}

	public void setDocumentTargets(Set<String> documentTargets) {
		this.documentTargets = documentTargets;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public boolean isDebugMode() {
		return debugMode;
	}

	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	
	public String getLTIVersion() {
		return ltiVersion;
	}
	
	public void setLTIVersion(String version) {
		this.ltiVersion = version;
	}
	
	
	
}
