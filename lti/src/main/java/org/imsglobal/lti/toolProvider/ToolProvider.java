package org.imsglobal.lti.toolProvider;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.imsglobal.lti.LTIMessage;
import org.imsglobal.lti.product.Product;
import org.imsglobal.lti.product.ProductFamily;
import org.imsglobal.lti.profile.ProfileResourceHandler;
import org.imsglobal.lti.profile.ServiceDefinition;
import org.imsglobal.lti.toolProvider.dataConnector.DataConnector;
import org.imsglobal.lti.toolProvider.mediaType.ToolProxy;
import org.imsglobal.lti.toolProvider.mediaType.ToolService;
import org.joda.time.DateTime;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.OAuthValidator;
import net.oauth.SimpleOAuthValidator;
import net.oauth.server.OAuthServlet;

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
	    private static String[] LTI_VERSIONS = {LTI_VERSION1, LTI_VERSION2};
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
	    private List<String> details;
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
	    private String mediaTypes = null;
	/**
	 * URL to redirect user to on successful completion of the request.
	 *
	 * @var string documentTargets
	 */
	    private String documentTargets = null;
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
	 * Callback functions for handling requests.
	 *
	 * @var array callbackHandler
	 */
	    private Map<String, Callback> callbackHandlers = new HashMap<String, Callback>();
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

	    
	    /*  Probably not needed
	    private JSONObject requestParamsToJSON(HttpRequest req) {
      	  JSONObject jsonObj = new JSONObject();
      	  Map<String,String[]> params = req.getParameterMap();
      	  for (Map.Entry<String,String[]> entry : params.entrySet()) {
      	    String v[] = entry.getValue();
      	    Object o = (v.length == 1) ? v[0] : v;
      	    jsonObj.put(entry.getKey(), o);
      	  }
      	  return jsonObj;
      	}
      	*/
      	
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
	        //JSONObject reqJSON = requestParamsToJSON(request);

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
	                doCallback();
	            }
	        }
	        result();

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
	        ToolProxy toolProxy = new ToolProxy(this, toolProxyService, secret);
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

	        return doCallback("onError");

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
	    	return doCallback(null);
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
	        	if (m.getName().equals(callback))
					try {
						retVal = (Boolean) m.invoke(this, (Object[])null);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} //returns a boolean
	        }
	        if (!methodExists) { //didn't find the method in declared methods
	        	if (StringUtils.isNotEmpty(method) && ok) {
	        		ok = false;
	        		reason = "Message type not supported: " + getMessageType();
	        	}
	        }
	        if (ok && (getMessageType().equals("ToolProxyRegistrationRequest"))) {
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
	    	boolean processed = false;
	    	if (!this.ok && this.callbackHandlers.containsKey("error")) {
	    	      Callback callbackHandler = this.callbackHandlers.get("error");
	    	      processed = callbackHandler.execute(this);
	    	    }
	    	    if (!processed) {
	    	      processed = true;
	    	      try {
	    	        if (!this.ok) {
	    	//
	    	/// If not valid, return an error message to the tool consumer if a return URL is provided
	    	//
	    	          if (this.returnUrl != null) {
	    	            this.error = this.returnUrl.toExternalForm();
	    	            if (this.error.indexOf("?") >= 0) {
	    	              this.error += '&';
	    	            } else {
	    	              this.error += '?';
	    	            }
	    	            if (this.debugMode && (this.reason != null)) {
	    	              this.error += "lti_errormsg=" + URLEncoder.encode("Debug error: " + this.reason, "UTF-8");
	    	            } else {
	    	              this.error += "lti_errormsg=" + URLEncoder.encode(this.message, "UTF-8");
	    	              if (this.reason != null) {
	    	                this.error += "&lti_errorlog=" + URLEncoder.encode("Debug error: " + this.reason, "UTF-8");
	    	              }
	    	            }
	    	          } else if (this.debugMode) {
	    	            this.error = this.reason;
	    	          }
	    	          if (this.error == null) {
	    	            this.error = this.message;
	    	          }
	    	          if (this.error.startsWith("http://") || this.error.startsWith("https://")) {
	    	            this.response.sendRedirect(this.error);
	    	          } else {
	    	            processed = false;
	    	          }
	    	        } else if (this.redirectUrl != null) {
	    	          this.response.sendRedirect(this.redirectUrl.toExternalForm());
	    	        } else if (this.returnUrl != null) {
	    	          this.response.sendRedirect(this.returnUrl.toExternalForm());
	    	        } else {
	    	          processed = false;
	    	        }
	    	        if (!processed) {
	    	          this.response.sendError(401, this.error);
	    	        }
	    	      } catch (IOException e) {
	    	    	  e.printStackTrace();
	    	      }
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

//
/// Set debug mode
//
    this.debugMode = (this.request.getParameter("custom_debug") != null) &&
       this.request.getParameter("custom_debug").equalsIgnoreCase("true");
//
/// Get the consumer
//
    boolean doSaveConsumer = false;
// Check all required launch parameters
    this.ok = this.request.getParameter("oauth_consumer_key") != null;
    if (this.ok) {
      this.ok = (this.request.getParameter("lti_message_type") != null) &&
         this.request.getParameter("lti_message_type").equals("basic-lti-launch-request");
    }
    if (this.ok) {
      this.ok = (this.request.getParameter("lti_version") != null) &&
         (this.request.getParameter("lti_version").equals(LTI_VERSION1) ||
          this.request.getParameter("lti_version").equals(LTI_VERSION2));
    }
    if (this.ok) {
      this.ok = (this.request.getParameter("resource_link_id") != null) &&
         this.request.getParameter("resource_link_id").trim().length() > 0;
    }
// Check consumer key
    if (this.ok) {
      this.consumer = new ToolConsumer(this.request.getParameter("oauth_consumer_key"), this.dataConnector, false);
      this.ok = this.consumer.getCreated() != null;
      if (this.debugMode && !this.ok) {
        this.reason = "Invalid consumer key.";
      }
    }
    DateTime now = DateTime.now();
    if (this.ok) {
      if (this.consumer.getLastAccess() == null) {
        doSaveConsumer = true;
      } else {
        DateTime last = this.consumer.getLastAccess();
        doSaveConsumer = doSaveConsumer || last.isBefore(now.withTimeAtStartOfDay());
      }
      this.consumer.setLastAccess(now);
      OAuthConsumer oAuthConsumer = new OAuthConsumer("about:blank", this.consumer.getKey(), this.consumer.getSecret(), null);
      OAuthAccessor oAuthAccessor = new OAuthAccessor(oAuthConsumer);
      OAuthValidator oAuthValidator = new SimpleOAuthValidator();
      OAuthMessage oAuthMessage = OAuthServlet.getMessage(this.request, null);
      try {
        oAuthValidator.validateMessage(oAuthMessage, oAuthAccessor);
      } catch (Exception e) {
        this.ok = false;
        if (this.reason == null) {
          this.reason = "OAuth signature check failed - perhaps an incorrect secret or timestamp.";
        }
      }
    }
    if (this.ok && this.consumer.isThisprotected()) {
      if (this.consumer.getConsumerGuid() != null) {
        this.ok = (this.request.getParameter("tool_consumer_instance_guid") != null) &&
           (this.request.getParameter("tool_consumer_instance_guid").length() > 0) &&
           this.consumer.getConsumerGuid().equals(this.request.getParameter("tool_consumer_instance_guid"));
        if (this.debugMode && !this.ok) {
          this.reason = "Request is from an invalid tool consumer.";
        }
      } else {
        this.ok = this.request.getParameter("tool_consumer_instance_guid") != null;
        if (this.debugMode && !this.ok) {
          this.reason = "A tool consumer GUID must be included in the launch request.";
        }
      }
    }
    if (this.ok) {
      this.ok = this.consumer.isEnabled();
      if (this.debugMode && !this.ok) {
        this.reason = "Tool consumer has not been enabled by the tool provider.";
      }
    }
    if (this.ok) {
      this.ok = (this.consumer.getEnableFrom() == null) || now.isAfter(this.consumer.getEnableFrom());
      if (this.ok) {
        this.ok = (this.consumer.getEnableUntil() == null) || this.consumer.getEnableUntil().isAfter(now);
        if (this.debugMode && !this.ok) {
          this.reason = "Tool consumer access has expired.";
        }
      } else if (this.debugMode) {
        this.reason = "Tool consumer access is not yet available.";
      }
    }
// Check nonce value
    if (this.ok) {
      ConsumerNonce nonce = new ConsumerNonce(this.consumer, this.request.getParameter("oauth_nonce"));
      this.ok = !nonce.load();
      if (this.ok) {
        this.ok = nonce.save();
      }
      if (this.debugMode && !this.ok) {
        this.reason = "Invalid nonce.";
      }
    }
//
/// Validate launch parameters
//
    if (this.ok) {
      List<String> invalidParameters = new ArrayList<String>();
      for (String name : constraints.keySet()) {
        ParameterConstraint parameterConstraint = this.constraints.get(name);
        boolean err = false;
        if (parameterConstraint.isRequired()) {
          if ((this.request.getParameter(name) == null) || (this.request.getParameter(name).trim().length() <= 0)) {
            invalidParameters.add(name);
            err = true;
          }
        }
        if (!err && (parameterConstraint.getMaxLength() != null) && (this.request.getParameter(name) != null)) {
          if (this.request.getParameter(name).trim().length() > parameterConstraint.getMaxLength()) {
            invalidParameters.add(name);
          }
        }
      }
      if (invalidParameters.size() > 0) {
        this.ok = false;
        if (this.reason == null) {
          StringBuilder msg = new StringBuilder("Invalid parameter(s): ");
          for (int i = 0; i < invalidParameters.size(); i++) {
            if (i > 0) {
              msg.append(", ");
            }
            msg.append(invalidParameters.get(i));
          }
          this.reason = msg.toString();
        }
      }
    }

    if (this.ok) {
      this.consumer.setDefaultEmail(this.defaultEmail);
//
/// Set the request context/resource link
//
      this.resourceLink = ResourceLink.fromConsumer(this.consumer, this.request.getParameter("resource_link_id").trim());
      if (this.request.getParameter("context_id") != null) {
        this.resourceLink.setContextId(this.request.getParameter("context_id").trim());
      }
      this.resourceLink.setLtiResourceLinkId(this.request.getParameter("resource_link_id").trim());
      StringBuilder title = new StringBuilder();
      if (this.request.getParameter("context_title") != null) {
        title.append(this.request.getParameter("context_title").trim());
      }
      if ((this.request.getParameter("resource_link_title") != null) &&
          (this.request.getParameter("resource_link_title").trim().length() > 0)) {
        if (title.length() > 0) {
          title.append(": ");
        }
        title.append(this.request.getParameter("resource_link_title").trim());
      }
      if (title.length() <= 0) {
        title.append("Course ").append(this.resourceLink.getId());
      }
      this.resourceLink.setTitle(title.toString());
// Save LTI parameters
      for (String name : LTI_RESOURCE_LINK_SETTING_NAMES) {
        this.resourceLink.setSetting(name, this.request.getParameter(name));
      }
// Delete any existing custom parameters
      Map<String,List<String>> settings = this.resourceLink.getSettings();
      for (String name : settings.keySet()) {
        if (name.startsWith("custom_")) {
          this.resourceLink.setSetting(name, (String)null);
        }
      }
// Save custom parameters
      for (String name : settings.keySet()) {
        if (name.startsWith("custom_")) {
        	resourceLink.setSetting(name, settings.get(name));
        }
      }
//
/// Set the user instance
//
      String userId = "";
      if (this.request.getParameter("user_id") != null) {
        userId = this.request.getParameter("user_id").trim();
      }
      this.user = User.fromResourceLink(this.resourceLink, userId);
//
/// Set the user name
//
      String firstname = "";
      if (this.request.getParameter("lis_person_name_given") != null) {
        firstname = this.request.getParameter("lis_person_name_given");
      }
      String lastname = "";
      if (this.request.getParameter("lis_person_name_family") != null) {
        lastname = this.request.getParameter("lis_person_name_family");
      }
      String fullname = "";
      if (this.request.getParameter("lis_person_name_full") != null) {
        fullname = this.request.getParameter("lis_person_name_full");
      }
      this.user.setNames(firstname, lastname, fullname);
//
/// Set the user email
//
      String email = "";
      if (this.request.getParameter("lis_person_contact_email_primary") != null) {
        email = this.request.getParameter("lis_person_contact_email_primary");
      }
      this.user.setEmail(email, this.defaultEmail);
//
/// Set the user roles
//
      if (this.request.getParameter("roles") != null) {
        this.user.setRoles(this.request.getParameter("roles"));
      }
//
/// Save the user instance
//
      if (this.request.getParameter("lis_result_sourcedid") != null) {
        if (!this.request.getParameter("lis_result_sourcedid").equals(this.user.getLtiResultSourcedId())) {
          this.user.setLtiResultSourcedId(this.request.getParameter("lis_result_sourcedid"));
          this.user.save();
        }
      } else if (this.user.getLtiResultSourcedId() != null) {
        this.user.delete();
      }
//
/// Initialise the consumer and check for changes
//
      if (!this.request.getParameter("lti_version").equals(this.consumer.getLtiVersion())) {
        this.consumer.setLtiVersion(this.request.getParameter("lti_version"));
        doSaveConsumer = true;
      }
      if (this.request.getParameter("tool_consumer_instance_name") != null) {
        if (!this.request.getParameter("tool_consumer_instance_name").equals(this.consumer.getConsumerName())) {
          this.consumer.setConsumerName(this.request.getParameter("tool_consumer_instance_name"));
          doSaveConsumer = true;
        }
      }
      if (this.request.getParameter("tool_consumer_info_product_family_code") != null) {
        String version = this.request.getParameter("tool_consumer_info_product_family_code");
        if (this.request.getParameter("tool_consumer_info_version") != null) {
          version += "-" + this.request.getParameter("tool_consumer_info_version");
        }
// do not delete any existing consumer version if none is passed
        if (!version.equals(this.consumer.getConsumerVersion())) {
          this.consumer.setConsumerVersion(version);
          doSaveConsumer = true;
        }
      } else if ((this.request.getParameter("ext_lms") != null) &&
         !this.request.getParameter("ext_lms").equals(this.consumer.getConsumerName())) {
        this.consumer.setConsumerVersion(this.request.getParameter("ext_lms"));
        doSaveConsumer = true;
      }
      if ((this.request.getParameter("tool_consumer_instance_guid") != null) &&
         (this.consumer.getConsumerGuid() == null)) {
        this.consumer.setConsumerGuid(this.request.getParameter("tool_consumer_instance_guid"));
        doSaveConsumer = true;
      }
      if (this.request.getParameter("launch_presentation_css_url") != null) {
        if (!this.request.getParameter("launch_presentation_css_url").equals(this.consumer.getCssPath())) {
          this.consumer.setCssPath(this.request.getParameter("launch_presentation_css_url"));
          doSaveConsumer = true;
        }
      } else if ((this.request.getParameter("ext_launch_presentation_css_url") != null) &&
         !this.request.getParameter("ext_launch_presentation_css_url").equals(this.consumer.getCssPath())) {
        this.consumer.setCssPath(this.request.getParameter("ext_launch_presentation_css_url"));
        doSaveConsumer = true;
      } else if (this.consumer.getCssPath() != null) {
        this.consumer.setCssPath(null);
        doSaveConsumer = true;
      }
    }
//
/// Persist changes to consumer
//
    if (doSaveConsumer) {
      this.consumer.save();
    }

    if (this.ok) {
//
/// Check if a share arrangement is in place for this resource link
//
      this.ok = this.checkForShare();
//
/// Persist changes to resource link
//
      this.resourceLink.save();
    }

    return this.ok;

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
	        String id = this.resourceLink.getPrimaryResourceLinkId();
	        String shareKeyValue = this.request.getParameter("custom_share_key");

	        boolean isShareRequest = (shareKeyValue != null) && (shareKeyValue.length() > 0);
	        if (isShareRequest) {
	            if (!allowSharing) {
	                ok = false;
	                reason = "Your sharing request has been refused because sharing is not being permitted.";
	            } else {
	// Check if this is a new share key
	            	ResourceLinkShareKey shareKey = new ResourceLinkShareKey(resourceLink, shareKeyValue);
	            	
	                if ((shareKey.getPrimaryConsumerKey() != null) && (shareKey.getResourceLinkId() != null)) {
	// Update resource link with sharing primary resource link details
	                    key = shareKey.getPrimaryConsumerKey();
	                    id = shareKey.getResourceLinkId();
	                    ok = (!key.equals(consumer.getKey()) || !(id.equals(resourceLink.getId())));
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
	            ok = StringUtils.isEmpty(id);
	            if (!ok) {
	                reason = "You have not requested to share a resource link but an arrangement is currently in place.";
	            }
	        }

	// Look up primary resource link
	        if (ok && StringUtils.isNotEmpty(id)) {
	            consumer = new ToolConsumer(key, dataConnector);
	            ok = (consumer.getCreated() != null);
	            if (ok) {
	                resourceLink = ResourceLink.fromConsumer(consumer, id);
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

	public String getMediaTypes() {
		return mediaTypes;
	}

	public void setMediaTypes(String mediaTypes) {
		this.mediaTypes = mediaTypes;
	}

	public String getDocumentTargets() {
		return documentTargets;
	}

	public void setDocumentTargets(String documentTargets) {
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
