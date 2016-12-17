package imsglobal.toolProvider;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpRequest;

import com.github.scribejava.core.builder.api.BaseApi;
import com.github.scribejava.core.oauth.OAuth10aService;

import imsglobal.LTIMessage;
import imsglobal.product.Product;
import imsglobal.product.ProductFamily;
import imsglobal.profile.Item;
import imsglobal.profile.ProfileResourceHandler;
import imsglobal.profile.ServiceDefinition;
import imsglobal.toolProvider.dataConnector.DataConnector;
import imsglobal.toolProvider.mediaType.ToolService;
import imsglobal.toolProvider.mediaType.ToolProxy;

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
	 * @var string returnUrl
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
	 * Callback functions for handling requests.
	 *
	 * @var array callbackHandler
	 */
	    private Map<String, CallBack> callbackHandler = new HashMap<String, Callback>();
	/**
	 * LTI parameter constraints for auto validation checks.
	 *
	 * @var array constraints
	 */
	    private Map<String, Map<String, String>> constraints = new HashMap<String, Map<String, String>>();
	    
	    private String messageType;
	    private String ltiVersion;

	/**
	 * Class constructor
	 *
	 * @param DataConnector     dataConnector    Object containing a database connection object
	 */
	    public ToolProvider(DataConnector dataConnector, HttpRequest request)
	    {
	        dataConnector = dataConnector;
	        ok = (this.dataConnector != null);

	// Set debug mode
	        String customDebug = request.getParams().getParameter("custom_debug").toString();
	        if(StringUtils.isNotEmpty(customDebug) && StringUtils.equalsIgnoreCase(customDebug, "true")) {
	        	setDebugMode(true);
	        }

	// Set return URL if available
	        String returnUrl = request.getParams().getParameter("launch_presentation_return_url").toString();
	        if (StringUtils.isNotEmpty(returnUrl)) {
	            setReturnUrl(new URL(returnUrl));
	        } else {
	        	returnUrl = request.getParams().getParameter("content_item_return_url").toString();
	        	if (StringUtils.isNotEmpty(returnUrl)) {
	        		setReturnUrl(new URL(returnUrl));
	        	}
	        }
	        setMessageType(request.getParams().getParameter("lti_message_type").toString());
	        setLTIVersion(request.getParams().getParameter("lti_version").toString());
	        this.vendor = new ProductFamily();
	        this.product = new Product();

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
	        	Map<String, String> constraint = new HashMap<String, String>();
	        	constraint.put("required", String.valueOf(required));
	        	constraint.put("max_length", String.valueOf(maxLength));
	        	String messages = StringUtils.join(messageTypes, ", ");
	        	constraint.put("messages", messages);
	        	constraints.put(name, constraint);
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
	        LTIMessage http = this.consumer.doServiceRequest(
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
	    	List roleList = new ArrayList<String>();
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
	    public static String sendForm(String errorUrl, Map<String, List<String>> formParams) {
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
		        	page += "<input type=\"hidden\" name=\"" + key + "\" value=\"" + value + "\" />\n\n";
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

	        this.onError();

	    }

	/**
	 * Process a valid content-item request
	 *
	 * @return boolean True if no error
	 */
	    protected boolean onContentItem()
	    {

	        this.onError();

	    }

	/**
	 * Process a valid tool proxy registration request
	 *
	 * @return boolean True if no error
	 */
	    protected boolean onRegister() {

	        this.onError();

	    }

	/**
	 * Process a response to an invalid request
	 *
	 * @return boolean True if no further error processing required
	 */
	    protected boolean onError()
	    {

	        this.doCallback("onError");

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
	        if (callback == null) {
	            callback = getMessageType();
	        }
	        if (method_exists(this, callback)) { //reflection
	            result = this.callback();
	        } else if (is_null(method) && this.ok) {
	            ok = false;
	            reason = "Message type not supported: " + getMessageType();
	        }
	        if (ok && (getMessageType().equals("ToolProxyRegistrationRequest"))) {
	            consumer.save();
	        }

	    }

	/**
	 * Perform the result of an action.
	 *
	 * This function may redirect the user to another URL rather than returning a value.
	 *
	 * @return string Output to be displayed (redirection, or display HTML or message)
	 */
	    private String result()
	    {

	        ok = false;
	        if (!this.ok) {
	            ok = this.onError();
	        }
	        if (!ok) {
	            if (!this.ok) {

	// If not valid, return an error message to the tool consumer if a return URL is provided
	                if (returnUrl != null) {
	                    String errorUrl = returnUrl.toExternalForm();
	                    if (!StringUtils.contains(errorUrl, "?")) {
	                        errorUrl += "?";
	                    } else {
	                        errorUrl += "&";
	                    }
	                    if (debugMode && StringUtils.isNotEmpty(reason)) {
	                        errorUrl += "lti_errormsg=" + URLEncoder.encode("Debug error: " + reason, "UTF-8");
	                    } else {
	                        errorUrl += "lti_errormsg=" + URLEncoder.encode(message, "UTF-8");
	                        if (StringUtils.isNotEmpty(reason)) {
	                            errorUrl += "&lti_errorlog=" + URLEncoder.encode("Debug error: " + reason, "UTF-8");
	                        }
	                    }
	                    String mt = getMessageType();
	                    String v = getLTIVersion();
	                    List<String> data = getData();
	                    if (consumer != null && StringUtils.isNotEmpty(mt) && mt.equals("ContentItemSelectionRequest")) {
	                        Map<String, List<String>> formParams = new HashMap<String, List<String>>();
	                        if (data != null) {
		                        formParams.put("data", data);	                        	
	                        }
	                        if (StringUtils.isEmpty(v)) {
	                        	v = LTI_VERSION1;
	                        }
	                        formParams = consumer.signParameters(errorUrl, "ContentItemSelection", v, formParams);
	                        String page = sendForm(errorUrl, formParams);
	                        System.out.print(page);
	                    } else {
	                    	//PHP only convenience method to redirect
	                        header("Location: {errorUrl}");
	                    }
	                    //exit;
	                } else {
	                    if (StringUtils.isNotEmpty(errorOutput)) {
	                        echo errorOutput;
	                    } else if (this.debugMode && StringUtils.isNotEmpty(reason)) {
	                        echo "Debug error: " + reason;
	                    } else {
	                        echo "Error: " + message;
	                    }
	                }
	            } else if (StringUtils.isNotEmpty(redirectUrl)) {
	                header("Location: {this.redirectUrl}");
	                exit;
	            } else if (StringUtils.isNotEmpty(output)) {
	                echo this.output;
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

	// Get the consumer
	        boolean doSaveConsumer = false;
	// Check all required launch parameters
	        String mt = getMessageType();
	        ok = StringUtils.isNotEmpty(mt) && MESSAGE_TYPES.containsKey(mt);
	        if (!ok) {
	            reason = "Invalid or missing lti_message_type parameter.";
	        }
	        if (ok) {
	        	String version = getLTIVersion();
	            ok = isset(_POST["lti_version"]) && in_array(_POST["lti_version"], self::LTI_VERSIONS);
	            if (!this.ok) {
	                this.reason = "Invalid or missing lti_version parameter.";
	            }
	        }
	        if (this.ok) {
	            if (_POST["lti_message_type"] === "basic-lti-launch-request") {
	                this.ok = isset(_POST["resource_link_id"]) && (strlen(trim(_POST["resource_link_id"])) > 0);
	                if (!this.ok) {
	                    this.reason = "Missing resource link ID.";
	                }
	            } else if (_POST["lti_message_type"] === "ContentItemSelectionRequest") {
	                if (isset(_POST["accept_media_types"]) && (strlen(trim(_POST["accept_media_types"])) > 0)) {
	                    mediaTypes = array_filter(explode(",", str_replace(" ", "", _POST["accept_media_types"])), "strlen");
	                    mediaTypes = array_unique(mediaTypes);
	                    this.ok = count(mediaTypes) > 0;
	                    if (!this.ok) {
	                        this.reason = "No accept_media_types found.";
	                    } else {
	                        this.mediaTypes = mediaTypes;
	                    }
	                } else {
	                    this.ok = false;
	                }
	                if (this.ok && isset(_POST["accept_presentation_document_targets"]) && (strlen(trim(_POST["accept_presentation_document_targets"])) > 0)) {
	                    documentTargets = array_filter(explode(",", str_replace(" ", "", _POST["accept_presentation_document_targets"])), "strlen");
	                    documentTargets = array_unique(documentTargets);
	                    this.ok = count(documentTargets) > 0;
	                    if (!this.ok) {
	                        this.reason = "Missing or empty accept_presentation_document_targets parameter.";
	                    } else {
	                        foreach (documentTargets as documentTarget) {
	                            this.ok = this.checkValue(documentTarget, array("embed", "frame", "iframe", "window", "popup", "overlay", "none"),
	                                 "Invalid value in accept_presentation_document_targets parameter: %s.");
	                            if (!this.ok) {
	                                break;
	                            }
	                        }
	                        if (this.ok) {
	                            this.documentTargets = documentTargets;
	                        }
	                    }
	                } else {
	                    this.ok = false;
	                }
	                if (this.ok) {
	                    this.ok = isset(_POST["content_item_return_url"]) && (strlen(trim(_POST["content_item_return_url"])) > 0);
	                    if (!this.ok) {
	                        this.reason = "Missing content_item_return_url parameter.";
	                    }
	                }
	            } else if (_POST["lti_message_type"] == "ToolProxyRegistrationRequest") {
	                this.ok = ((isset(_POST["reg_key"]) && (strlen(trim(_POST["reg_key"])) > 0)) &&
	                             (isset(_POST["reg_password"]) && (strlen(trim(_POST["reg_password"])) > 0)) &&
	                             (isset(_POST["tc_profile_url"]) && (strlen(trim(_POST["tc_profile_url"])) > 0)) &&
	                             (isset(_POST["launch_presentation_return_url"]) && (strlen(trim(_POST["launch_presentation_return_url"])) > 0)));
	                if (this.debugMode && !this.ok) {
	                    this.reason = "Missing message parameters.";
	                }
	            }
	        }
	        now = time();
	// Check consumer key
	        if (this.ok && (_POST["lti_message_type"] != "ToolProxyRegistrationRequest")) {
	            this.ok = isset(_POST["oauth_consumer_key"]);
	            if (!this.ok) {
	                this.reason = "Missing consumer key.";
	            }
	            if (this.ok) {
	                this.consumer = new ToolConsumer(_POST["oauth_consumer_key"], this.dataConnector);
	                this.ok = !is_null(this.consumer.created);
	                if (!this.ok) {
	                    this.reason = "Invalid consumer key.";
	                }
	            }
	            if (this.ok) {
	                today = date("Y-m-d", now);
	                if (is_null(this.consumer.lastAccess)) {
	                    doSaveConsumer = true;
	                } else {
	                    last = date("Y-m-d", this.consumer.lastAccess);
	                    doSaveConsumer = doSaveConsumer || (last !== today);
	                }
	                this.consumer.last_access = now;
	                try {
	                    store = new OAuthDataStore(this);
	                    server = new OAuth\OAuthServer(store);
	                    method = new OAuth\OAuthSignatureMethod_HMAC_SHA1();
	                    server.add_signature_method(method);
	                    request = OAuth\OAuthRequest::from_request();
	                    res = server.verify_request(request);
	                } catch (\Exception e) {
	                    this.ok = false;
	                    if (empty(this.reason)) {
	                        if (this.debugMode) {
	                            consumer = new OAuth\OAuthConsumer(this.consumer.getKey(), this.consumer.secret);
	                            signature = request.build_signature(method, consumer, false);
	                            this.reason = e.getMessage();
	                            if (empty(this.reason)) {
	                                this.reason = "OAuth exception";
	                            }
	                            this.details[] = "Timestamp: " . time();
	                            this.details[] = "Signature: {signature}";
	                            this.details[] = "Base string: {request.base_string}]";
	                        } else {
	                            this.reason = "OAuth signature check failed - perhaps an incorrect secret or timestamp.";
	                        }
	                    }
	                }
	            }
	            if (this.ok) {
	                today = date("Y-m-d", now);
	                if (is_null(this.consumer.lastAccess)) {
	                    doSaveConsumer = true;
	                } else {
	                    last = date("Y-m-d", this.consumer.lastAccess);
	                    doSaveConsumer = doSaveConsumer || (last !== today);
	                }
	                this.consumer.last_access = now;
	                if (this.consumer.protected) {
	                    if (!is_null(this.consumer.consumerGuid)) {
	                        this.ok = empty(_POST["tool_consumer_instance_guid"]) ||
	                             (this.consumer.consumerGuid === _POST["tool_consumer_instance_guid"]);
	                        if (!this.ok) {
	                            this.reason = "Request is from an invalid tool consumer.";
	                        }
	                    } else {
	                        this.ok = isset(_POST["tool_consumer_instance_guid"]);
	                        if (!this.ok) {
	                            this.reason = "A tool consumer GUID must be included in the launch request.";
	                        }
	                    }
	                }
	                if (this.ok) {
	                    this.ok = this.consumer.enabled;
	                    if (!this.ok) {
	                        this.reason = "Tool consumer has not been enabled by the tool provider.";
	                    }
	                }
	                if (this.ok) {
	                    this.ok = is_null(this.consumer.enableFrom) || (this.consumer.enableFrom <= now);
	                    if (this.ok) {
	                        this.ok = is_null(this.consumer.enableUntil) || (this.consumer.enableUntil > now);
	                        if (!this.ok) {
	                            this.reason = "Tool consumer access has expired.";
	                        }
	                    } else {
	                        this.reason = "Tool consumer access is not yet available.";
	                    }
	                }
	            }

	// Validate other message parameter values
	            if (this.ok) {
	                if (_POST["lti_message_type"] === "ContentItemSelectionRequest") {
	                    if (isset(_POST["accept_unsigned"])) {
	                        this.ok = this.checkValue(_POST["accept_unsigned"], array("true", "false"), "Invalid value for accept_unsigned parameter: %s.");
	                    }
	                    if (this.ok && isset(_POST["accept_multiple"])) {
	                        this.ok = this.checkValue(_POST["accept_multiple"], array("true", "false"), "Invalid value for accept_multiple parameter: %s.");
	                    }
	                    if (this.ok && isset(_POST["accept_copy_advice"])) {
	                        this.ok = this.checkValue(_POST["accept_copy_advice"], array("true", "false"), "Invalid value for accept_copy_advice parameter: %s.");
	                    }
	                    if (this.ok && isset(_POST["auto_create"])) {
	                        this.ok = this.checkValue(_POST["auto_create"], array("true", "false"), "Invalid value for auto_create parameter: %s.");
	                    }
	                    if (this.ok && isset(_POST["can_confirm"])) {
	                        this.ok = this.checkValue(_POST["can_confirm"], array("true", "false"), "Invalid value for can_confirm parameter: %s.");
	                    }
	                } else if (isset(_POST["launch_presentation_document_target"])) {
	                    this.ok = this.checkValue(_POST["launch_presentation_document_target"], array("embed", "frame", "iframe", "window", "popup", "overlay"),
	                         "Invalid value for launch_presentation_document_target parameter: %s.");
	                }
	            }
	        }

	        if (this.ok && (_POST["lti_message_type"] === "ToolProxyRegistrationRequest")) {
	            this.ok = _POST["lti_version"] == self::LTI_VERSION2;
	            if (!this.ok) {
	                this.reason = "Invalid lti_version parameter";
	            }
	            if (this.ok) {
	                http = new HTTPMessage(_POST["tc_profile_url"], "GET", null, "Accept: application/vnd.ims.lti.v2.toolconsumerprofile+json");
	                this.ok = http.send();
	                if (!this.ok) {
	                    this.reason = "Tool consumer profile not accessible.";
	                } else {
	                    tcProfile = json_decode(http.response);
	                    this.ok = !is_null(tcProfile);
	                    if (!this.ok) {
	                        this.reason = "Invalid JSON in tool consumer profile.";
	                    }
	                }
	            }
	// Check for required capabilities
	            if (this.ok) {
	                this.consumer = new ToolConsumer(_POST["reg_key"], this.dataConnector);
	                this.consumer.profile = tcProfile;
	                capabilities = this.consumer.profile.capability_offered;
	                missing = array();
	                foreach (this.resourceHandlers as resourceHandler) {
	                    foreach (resourceHandler.requiredMessages as message) {
	                        if (!in_array(message.type, capabilities)) {
	                            missing[message.type] = true;
	                        }
	                    }
	                }
	                foreach (this.constraints as name => constraint) {
	                    if (constraint["required"]) {
	                        if (!in_array(name, capabilities) && !in_array(name, array_flip(capabilities))) {
	                            missing[name] = true;
	                        }
	                    }
	                }
	                if (!empty(missing)) {
	                    ksort(missing);
	                    this.reason = "Required capability not offered - \"" . implode("\", \"", array_keys(missing)) . "\"";
	                    this.ok = false;
	                }
	            }
	// Check for required services
	            if (this.ok) {
	                foreach (this.requiredServices as service) {
	                    foreach (service.formats as format) {
	                        if (!this.findService(format, service.actions)) {
	                            if (this.ok) {
	                                this.reason = "Required service(s) not offered - ";
	                                this.ok = false;
	                            } else {
	                                this.reason .= ", ";
	                            }
	                            this.reason .= ""{format}" [" . implode(", ", service.actions) . "]";
	                        }
	                    }
	                }
	            }
	            if (this.ok) {
	                if (_POST["lti_message_type"] === "ToolProxyRegistrationRequest") {
	                    this.consumer.profile = tcProfile;
	                    this.consumer.secret = _POST["reg_password"];
	                    this.consumer.ltiVersion = _POST["lti_version"];
	                    this.consumer.name = tcProfile.product_instance.service_owner.service_owner_name.default_value;
	                    this.consumer.consumerName = this.consumer.name;
	                    this.consumer.consumerVersion = "{tcProfile.product_instance.product_info.product_family.code}-{tcProfile.product_instance.product_info.product_version}";
	                    this.consumer.consumerGuid = tcProfile.product_instance.guid;
	                    this.consumer.enabled = true;
	                    this.consumer.protected = true;
	                    doSaveConsumer = true;
	                }
	            }
	        } else if (this.ok && !empty(_POST["custom_tc_profile_url"]) && empty(this.consumer.profile)) {
	            http = new HTTPMessage(_POST["custom_tc_profile_url"], "GET", null, "Accept: application/vnd.ims.lti.v2.toolconsumerprofile+json");
	            if (http.send()) {
	                tcProfile = json_decode(http.response);
	                if (!is_null(tcProfile)) {
	                    this.consumer.profile = tcProfile;
	                    doSaveConsumer = true;
	                }
	            }
	        }

	// Validate message parameter constraints
	        if (this.ok) {
	            invalidParameters = array();
	            foreach (this.constraints as name => constraint) {
	                if (empty(constraint["messages"]) || in_array(_POST["lti_message_type"], constraint["messages"])) {
	                    ok = true;
	                    if (constraint["required"]) {
	                        if (!isset(_POST[name]) || (strlen(trim(_POST[name])) <= 0)) {
	                            invalidParameters[] = "{name} (missing)";
	                            ok = false;
	                        }
	                    }
	                    if (ok && !is_null(constraint["max_length"]) && isset(_POST[name])) {
	                        if (strlen(trim(_POST[name])) > constraint["max_length"]) {
	                            invalidParameters[] = "{name} (too long)";
	                        }
	                    }
	                }
	            }
	            if (count(invalidParameters) > 0) {
	                this.ok = false;
	                if (empty(this.reason)) {
	                    this.reason = "Invalid parameter(s): " . implode(", ", invalidParameters) . ".";
	                }
	            }
	        }

	        if (this.ok) {

	// Set the request context
	            if (isset(_POST["context_id"])) {
	                this.context = Context::fromConsumer(this.consumer, trim(_POST["context_id"]));
	                title = "";
	                if (isset(_POST["context_title"])) {
	                    title = trim(_POST["context_title"]);
	                }
	                if (empty(title)) {
	                    title = "Course {this.context.getId()}";
	                }
	                this.context.title = title;
	            }

	// Set the request resource link
	            if (isset(_POST["resource_link_id"])) {
	                contentItemId = "";
	                if (isset(_POST["custom_content_item_id"])) {
	                    contentItemId = _POST["custom_content_item_id"];
	                }
	                this.resourceLink = ResourceLink::fromConsumer(this.consumer, trim(_POST["resource_link_id"]), contentItemId);
	                if (!empty(this.context)) {
	                    this.resourceLink.setContextId(this.context.getRecordId());
	                }
	                title = "";
	                if (isset(_POST["resource_link_title"])) {
	                    title = trim(_POST["resource_link_title"]);
	                }
	                if (empty(title)) {
	                    title = "Resource {this.resourceLink.getId()}";
	                }
	                this.resourceLink.title = title;
	// Delete any existing custom parameters
	                foreach (this.consumer.getSettings() as name => value) {
	                    if (strpos(name, "custom_") === 0) {
	                        this.consumer.setSetting(name);
	                        doSaveConsumer = true;
	                    }
	                }
	                if (!empty(this.context)) {
	                    foreach (this.context.getSettings() as name => value) {
	                        if (strpos(name, "custom_") === 0) {
	                            this.context.setSetting(name);
	                        }
	                    }
	                }
	                foreach (this.resourceLink.getSettings() as name => value) {
	                    if (strpos(name, "custom_") === 0) {
	                        this.resourceLink.setSetting(name);
	                    }
	                }
	// Save LTI parameters
	                foreach (self::LTI_CONSUMER_SETTING_NAMES as name) {
	                    if (isset(_POST[name])) {
	                        this.consumer.setSetting(name, _POST[name]);
	                    } else {
	                        this.consumer.setSetting(name);
	                    }
	                }
	                if (!empty(this.context)) {
	                    foreach (self::LTI_CONTEXT_SETTING_NAMES as name) {
	                        if (isset(_POST[name])) {
	                            this.context.setSetting(name, _POST[name]);
	                        } else {
	                            this.context.setSetting(name);
	                        }
	                    }
	                }
	                foreach (self::LTI_RESOURCE_LINK_SETTING_NAMES as name) {
	                    if (isset(_POST[name])) {
	                        this.resourceLink.setSetting(name, _POST[name]);
	                    } else {
	                        this.resourceLink.setSetting(name);
	                    }
	                }
	// Save other custom parameters
	                foreach (_POST as name => value) {
	                    if ((strpos(name, "custom_") === 0) &&
	                        !in_array(name, array_merge(self::LTI_CONSUMER_SETTING_NAMES, self::LTI_CONTEXT_SETTING_NAMES, self::LTI_RESOURCE_LINK_SETTING_NAMES))) {
	                        this.resourceLink.setSetting(name, value);
	                    }
	                }
	            }

	// Set the user instance
	            userId = "";
	            if (isset(_POST["user_id"])) {
	                userId = trim(_POST["user_id"]);
	            }

	            this.user = User::fromResourceLink(this.resourceLink, userId);

	// Set the user name
	            firstname = (isset(_POST["lis_person_name_given"])) ? _POST["lis_person_name_given"] : "";
	            lastname = (isset(_POST["lis_person_name_family"])) ? _POST["lis_person_name_family"] : "";
	            fullname = (isset(_POST["lis_person_name_full"])) ? _POST["lis_person_name_full"] : "";
	            this.user.setNames(firstname, lastname, fullname);

	// Set the user email
	            email = (isset(_POST["lis_person_contact_email_primary"])) ? _POST["lis_person_contact_email_primary"] : "";
	            this.user.setEmail(email, this.defaultEmail);

	// Set the user image URI
	            if (isset(_POST["user_image"])) {
	                this.user.image = _POST["user_image"];
	            }

	// Set the user roles
	            if (isset(_POST["roles"])) {
	                this.user.roles = self::parseRoles(_POST["roles"]);
	            }

	// Initialise the consumer and check for changes
	            this.consumer.defaultEmail = this.defaultEmail;
	            if (this.consumer.ltiVersion !== _POST["lti_version"]) {
	                this.consumer.ltiVersion = _POST["lti_version"];
	                doSaveConsumer = true;
	            }
	            if (isset(_POST["tool_consumer_instance_name"])) {
	                if (this.consumer.consumerName !== _POST["tool_consumer_instance_name"]) {
	                    this.consumer.consumerName = _POST["tool_consumer_instance_name"];
	                    doSaveConsumer = true;
	                }
	            }
	            if (isset(_POST["tool_consumer_info_product_family_code"])) {
	                version = _POST["tool_consumer_info_product_family_code"];
	                if (isset(_POST["tool_consumer_info_version"])) {
	                    version .= "-{_POST["tool_consumer_info_version"]}";
	                }
	// do not delete any existing consumer version if none is passed
	                if (this.consumer.consumerVersion !== version) {
	                    this.consumer.consumerVersion = version;
	                    doSaveConsumer = true;
	                }
	            } else if (isset(_POST["ext_lms"]) && (this.consumer.consumerName !== _POST["ext_lms"])) {
	                this.consumer.consumerVersion = _POST["ext_lms"];
	                doSaveConsumer = true;
	            }
	            if (isset(_POST["tool_consumer_instance_guid"])) {
	                if (is_null(this.consumer.consumerGuid)) {
	                    this.consumer.consumerGuid = _POST["tool_consumer_instance_guid"];
	                    doSaveConsumer = true;
	                } else if (!this.consumer.protected) {
	                    doSaveConsumer = (this.consumer.consumerGuid !== _POST["tool_consumer_instance_guid"]);
	                    if (doSaveConsumer) {
	                        this.consumer.consumerGuid = _POST["tool_consumer_instance_guid"];
	                    }
	                }
	            }
	            if (isset(_POST["launch_presentation_css_url"])) {
	                if (this.consumer.cssPath !== _POST["launch_presentation_css_url"]) {
	                    this.consumer.cssPath = _POST["launch_presentation_css_url"];
	                    doSaveConsumer = true;
	                }
	            } else if (isset(_POST["ext_launch_presentation_css_url"]) &&
	                 (this.consumer.cssPath !== _POST["ext_launch_presentation_css_url"])) {
	                this.consumer.cssPath = _POST["ext_launch_presentation_css_url"];
	                doSaveConsumer = true;
	            } else if (!empty(this.consumer.cssPath)) {
	                this.consumer.cssPath = null;
	                doSaveConsumer = true;
	            }
	        }

	// Persist changes to consumer
	        if (doSaveConsumer) {
	            this.consumer.save();
	        }
	        if (this.ok && isset(this.context)) {
	            this.context.save();
	        }
	        if (this.ok && isset(this.resourceLink)) {

	// Check if a share arrangement is in place for this resource link
	            this.ok = this.checkForShare();

	// Persist changes to resource link
	            this.resourceLink.save();

	// Save the user instance
	            if (isset(_POST["lis_result_sourcedid"])) {
	                if (this.user.ltiResultSourcedId !== _POST["lis_result_sourcedid"]) {
	                    this.user.ltiResultSourcedId = _POST["lis_result_sourcedid"];
	                    this.user.save();
	                }
	            } else if (!empty(this.user.ltiResultSourcedId)) {
	                this.user.ltiResultSourcedId = "";
	                this.user.save();
	            }
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
	        doSaveResourceLink = true;

	        id = this.resourceLink.primaryResourceLinkId;

	        shareRequest = isset(_POST["custom_share_key"]) && !empty(_POST["custom_share_key"]);
	        if (shareRequest) {
	            if (!this.allowSharing) {
	                ok = false;
	                this.reason = "Your sharing request has been refused because sharing is not being permitted.";
	            } else {
	// Check if this is a new share key
	                shareKey = new ResourceLinkShareKey(this.resourceLink, _POST["custom_share_key"]);
	                if (!is_null(shareKey.primaryConsumerKey) && !is_null(shareKey.primaryResourceLinkId)) {
	// Update resource link with sharing primary resource link details
	                    key = shareKey.primaryConsumerKey;
	                    id = shareKey.primaryResourceLinkId;
	                    ok = (key != this.consumer.getKey()) || (id != this.resourceLink.getId());
	                    if (ok) {
	                        this.resourceLink.primaryConsumerKey = key;
	                        this.resourceLink.primaryResourceLinkId = id;
	                        this.resourceLink.shareApproved = shareKey.autoApprove;
	                        ok = this.resourceLink.save();
	                        if (ok) {
	                            doSaveResourceLink = false;
	                            this.user.getResourceLink().primaryConsumerKey = key;
	                            this.user.getResourceLink().primaryResourceLinkId = id;
	                            this.user.getResourceLink().shareApproved = shareKey.autoApprove;
	                            this.user.getResourceLink().updated = time();
	// Remove share key
	                            shareKey.delete();
	                        } else {
	                            this.reason = "An error occurred initialising your share arrangement.";
	                        }
	                    } else {
	                        this.reason = "It is not possible to share your resource link with yourself.";
	                    }
	                }
	                if (ok) {
	                    ok = !is_null(key);
	                    if (!ok) {
	                        this.reason = "You have requested to share a resource link but none is available.";
	                    } else {
	                        ok = (!is_null(this.user.getResourceLink().shareApproved) && this.user.getResourceLink().shareApproved);
	                        if (!ok) {
	                            this.reason = "Your share request is waiting to be approved.";
	                        }
	                    }
	                }
	            }
	        } else {
	// Check no share is in place
	            ok = is_null(id);
	            if (!ok) {
	                this.reason = "You have not requested to share a resource link but an arrangement is currently in place.";
	            }
	        }

	// Look up primary resource link
	        if (ok && !is_null(id)) {
	            consumer = new ToolConsumer(key, this.dataConnector);
	            ok = !is_null(consumer.created);
	            if (ok) {
	                resourceLink = ResourceLink.fromConsumer(consumer, id);
	                ok = !is_null(resourceLink.created);
	            }
	            if (ok) {
	                if (doSaveResourceLink) {
	                    this.resourceLink.save();
	                }
	                this.resourceLink = resourceLink;
	            } else {
	                this.reason = "Unable to load resource link being shared.";
	            }
	        }

	        return ok;

	    }

	/**
	 * Validate a parameter value from an array of permitted values.
	 *
	 * @return boolean True if value is valid
	 */
	    private function checkValue(value, values, reason)
	    {

	        ok = in_array(value, values);
	        if (!ok && !empty(reason)) {
	            this.reason = sprintf(reason, value);
	        }

	        return ok;

	    }

	public static BaseApi<OAuth10aService> instance() {
		// TODO Auto-generated method stub
		return null;
	}

	public ToolConsumer getConsumer() {
		// TODO Auto-generated method stub
		return null;
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
