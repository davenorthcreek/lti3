package imsglobal.toolProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.jdom2.Document;
import org.jdom2.Element;
import org.joda.time.DateTime;

import imsglobal.LTIMessage;
import imsglobal.LTIUtil;
import imsglobal.toolProvider.dataConnector.DataConnector;
import imsglobal.toolProvider.service.Membership;
import imsglobal.toolProvider.service.ToolSettings;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;

public class ResourceLink implements LTISource {

/**
 * Class to represent a tool consumer resource link
 *
 * @author  Stephen P Vickers <svickers@imsglobal.org>
 * @copyright  IMS Global Learning Consortium Inc
 * @date  2016
 * @version 3.0.2
 * @license http://www.apache.org/licenses/LICENSE-2.0 Apache License, Version 2.0
 */

/**
 * Read action.
 */
    public static final int EXT_READ = 1;
/**
 * Write (create/update) action.
 */
    public static final int EXT_WRITE = 2;
/**
 * Delete action.
 */
    public static final int EXT_DELETE = 3;
/**
 * Create action.
 */
    public static final int EXT_CREATE = 4;
/**
 * Update action.
 */
    public static final int EXT_UPDATE = 5;

/**
 * Decimal outcome type.
 */
    public static final String EXT_TYPE_DECIMAL = "decimal";
/**
 * Percentage outcome type.
 */
    public static final String EXT_TYPE_PERCENTAGE = "percentage";
/**
 * Ratio outcome type.
 */
    public static final String EXT_TYPE_RATIO = "ratio";
/**
 * Letter (A-F) outcome type.
 */
    public static final String EXT_TYPE_LETTER_AF = "letteraf";
/**
 * Letter (A-F) with optional +/- outcome type.
 */
    public static final String EXT_TYPE_LETTER_AF_PLUS = "letterafplus";
/**
 * Pass/fail outcome type.
 */
    public static final String EXT_TYPE_PASS_FAIL = "passfail";
/**
 * Free text outcome type.
 */
    public static final String EXT_TYPE_TEXT = "freetext";
    
    private static final int TIMEOUT = 30000;

/**
 * Context title.
 *
 * @var string title
 */
    private String title = null;
/**
 * Resource link ID as supplied in the last connection request.
 *
 * @var string ltiResourceLinkId
 */
    private String ltiResourceLinkId = null;
/**
 * User group sets (null if the consumer does not support the groups enhancement)
 *
 * @var array groupSets
 */
    private Map<String, GroupSet> groupSets = null;
/**
 * User groups (null if the consumer does not support the groups enhancement)
 *
 * @var array groups
 */
    private Map<String, Group> groups = null;
/**
 * Request for last service request.
 *
 * @var string extRequest
 */
    private String extRequest = null;
/**
 * Request headers for last service request.
 *
 * @var array extRequestHeaders
 */
    private Map<String, String> extRequestHeaders = null;
/**
 * Response from last service request.
 *
 * @var string extResponse
 */
    private String extResponse = null;
/**
 * Consumer key value for resource link being shared (if any).
 */
  private String primaryConsumerKey = null;
/**
 * Response header from last service request.
 *
 * @var array extResponseHeaders
 */
    private Map<String, String> extResponseHeaders = null;
/**
 * Consumer key value for resource link being shared (if any).
 *
 * @var string primaryResourceLinkId
 */
    private String primaryResourceLinkId = null;
/**
 * Whether the sharing request has been approved by the primary resource link.
 *
 * @var boolean shareApproved
 */
    private boolean shareApproved;
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
 * Record ID for this resource link.
 *
 * @var int id
 */
    private int id;
/**
 * Tool Consumer for this resource link.
 *
 * @var ToolConsumer consumer
 */
    private ToolConsumer consumer = null;
/**
 * Tool Consumer ID for this resource link.
 *
 * @var int consumerId
 */
    private int consumerId;
/**
 * Context for this resource link.
 *
 * @var Context context
 */
    private Context context = null;
/**
 * Context ID for this resource link.
 *
 * @var String contextId
 */
    private String contextId = null;
/**
 * Setting values (LTI parameters, custom parameters and local parameters).
 *
 * @var array settings
 */
    private Map<String, List<String>> settings = null;
/**
 * Whether the settings value have changed since last saved.
 *
 * @var boolean settingsChanged
 */
    private boolean settingsChanged = false;
/**
 * XML document for the last extension service request.
 *
 * @var string extDoc
 */
    private Document extDoc = null;
/**
 * XML node array for the last extension service request.
 *
 * @var array extNodes
 */
    private Map<String, List<String>> extNodes = null;
/**
 * Data connector object or string.
 *
 * @var mixed dataConnector
 */
    private DataConnector dataConnector = null;

/**
 * Class constructor.
 */
    public ResourceLink()
    {

        this.initialize();

    }

/**
 * Initialise the resource link.
 */
    public void initialize()
    {

    	//this.ltiContextId = null;
        this.ltiResourceLinkId = null;
        this.title = "";
        this.settings = new HashMap<String,List<String>>();
        this.groupSets = new HashMap<String,GroupSet>();
        this.groups = new HashMap<String,Group>();
        //this.primaryConsumerKey = null;
        this.primaryResourceLinkId = null;
        this.created = null;
        this.updated = null;

    }

/**
 * Initialise the resource link.
 *
 * Pseudonym for initialize().
 */
    public void initialise()
    {

        this.initialize();

    }

/**
 * Save the resource link to the database.
 *
 * @return boolean True if the resource link was successfully saved.
 */
    public boolean save()
    {

        boolean ok = this.getDataConnector().saveResourceLink(this);
        if (ok) {
            this.settingsChanged = false;
        }

        return ok;

    }

/**
 * Delete the resource link from the database.
 *
 * @return boolean True if the resource link was successfully deleted.
 */
    public boolean delete()
    {

        return this.getDataConnector().deleteResourceLink(this);

    }

/**
 * Get tool consumer.
 *
 * @return ToolConsumer Tool consumer object for this resource link.
 */
    public ToolConsumer getConsumer()
    {

        if (consumer == null) {
            if ((context != null) || (contextId != null)) {
                consumer = getContext().getConsumer();
            } else {
                consumer = ToolConsumer.fromRecordId(consumerId, this.getDataConnector());
            }
        }

        return consumer;

    }

/**
 * Set tool consumer ID.
 *
 * @param int consumerId   Tool Consumer ID for this resource link.
 */
    public void setConsumerId(int consumerId)
    {

        this.consumer = null;
        this.consumerId = consumerId;

    }

/**
 * Get context.
 *
 * @return object LTIContext object for this resource link.
 */
    public Context getContext()
    {

        if (context == null && contextId != null) {
            this.context = Context.fromRecordId(this.contextId, this.getDataConnector());
        }

        return this.context;

    }

/**
 * Get context record ID.
 *
 * @return int Context record ID for this resource link.
 */
    public String getContextId()
    {

        return this.contextId;

    }

/**
 * Set context ID.
 *
 * @param int contextId   Context ID for this resource link.
 */
    public void setContextId(String contextId)
    {

        this.context = null;
        this.contextId = contextId;

    }

/**
 * Get tool consumer key.
 *
 * @return string Consumer key value for this resource link.
 */
    public String getKey()
    {

        return this.getConsumer().getKey();

    }

/**
 * Get resource link ID.
 *
 * @return string ID for this resource link.
 */
    public String getId()
    {

        return this.ltiResourceLinkId;

    }

/**
 * Get resource link record ID.
 *
 * @return int Record ID for this resource link.
 */
    public int getRecordId()
    {

        return this.id;

    }

/**
 * Set resource link record ID.
 *
 * @param int id  Record ID for this resource link.
 */
    public void setRecordId(int id)
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
    public String getSetting(String name)
    {
    	return getSetting(name, "");
    }
    
    public String getSetting(String name, String dflt)
    {
    	String value = dflt;
        if (settings.containsKey(name)) {
        	//return the first element of the list for now
            value = settings.get(name).get(0);
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
    	settings.remove(name);
    }
    
    public void setSetting(String name, String value)
    {

        String old_value = this.getSetting(name);
        if (!value.equals(old_value)) {
            if (StringUtils.isNotEmpty(value)) {
            	LTIUtil.setParameter(settings, name, value);
            } else {
                settings.remove(name);
            }
            this.settingsChanged = true;
        }

    }
    
    public void setSetting(String name, List<String> value) {
    	settings.put(name, value);
    }

/**
 * Get an array of all setting values.
 *
 * @return array Associative array of setting values
 */
    public Map<String, List<String>> getSettings()
    {

        return this.settings;

    }

/**
 * Set an array of all setting values.
 *
 * @param array settings  Associative array of setting values
 */
    public void setSettings(Map<String, List<String>> settings)
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
    	boolean ok = true;
        if (this.settingsChanged) {
            ok = this.save();
        }
        return ok;

    }

/**
 * Check if the Outcomes service is supported.
 *
 * @return boolean True if this resource link supports the Outcomes service (either the LTI 1.1 or extension service)
 */
    public boolean hasOutcomesService()
    {

        String url = this.getSetting("ext_ims_lis_basic_outcome_url") + this.getSetting("lis_outcome_service_url");

        return StringUtils.isNotEmpty(url);

    }

/**
 * Check if the Memberships extension service is supported.
 *
 * @return boolean True if this resource link supports the Memberships extension service
 */
    public boolean hasMembershipsService()
    {

        String url = this.getSetting("ext_ims_lis_memberships_url");

        return StringUtils.isNotEmpty(url);

    }

/**
 * Check if the Setting extension service is supported.
 *
 * @return boolean True if this resource link supports the Setting extension service
 */
    public boolean hasSettingService()
    {

        String url = this.getSetting("ext_ims_lti_tool_setting_url");

        return StringUtils.isNotEmpty(url);

    }

    
    /**
     * @deprecated use <code>{@link ResourceLink#doOutcomesService(int, Outcome, User)}</code> instead.
     * <p>
     * Perform an Outcomes service request.
     * <p>
     * The action type parameter should be one of the pre-defined constants.
     *
     * @see #EXT_READ
     * @see #EXT_WRITE
     * @see #EXT_DELETE
     *
     * @param action      action type
     * @param ltiOutcome  Outcome object
     *
     * @return <code>true</code> if the request was successfully processed
     */
      @Deprecated
      public boolean doOutcomesService(int action, Outcome ltiOutcome) {
        return doOutcomesService(action, ltiOutcome, null);
      }
/**
 * Perform an Outcomes service request.
 *
 * @param int action The action type constant
 * @param Outcome ltiOutcome Outcome object
 * @param User user User object
 *
 * @return boolean True if the request was successfully processed
 */
    public boolean doOutcomesService(int action, Outcome ltiOutcome, User user)
    {

        boolean response = false;
        this.extResponse = null;
        String todo = "";
        String xml;
        ResourceLink sourceResourceLink = this;
        String sourcedId = ltiOutcome.getSourcedId();

// Lookup service details from the source resource link appropriate to the user (in case the destination is being shared)
        if (user != null) {
        	sourceResourceLink = user.getResourceLink();
        	sourcedId = user.getLtiResultSourcedId();
        }

// Use LTI 1.1 service in preference to extension service if it is available
        String urlLTI11 = sourceResourceLink.getSetting("lis_outcome_service_url");
        String urlExt = sourceResourceLink.getSetting("ext_ims_lis_basic_outcome_url");
        boolean ext = StringUtils.isNotEmpty(urlExt);
        boolean lti11 = StringUtils.isNotEmpty(urlLTI11);
        if (ext || lti11) {
            switch (action) {
                case EXT_READ:
                    if (lti11 && (ltiOutcome.getType() == EXT_TYPE_DECIMAL)) {
                        todo = "readResult";
                    } else if (ext) {
                        urlLTI11 = null;
                        todo = "basic-lis-readresult";
                    }
                    break;
                case EXT_WRITE:
                	List<String> decimals = new ArrayList<String>();
                	decimals.add(EXT_TYPE_DECIMAL);
                    if (lti11 && this.checkValueType(ltiOutcome, decimals)) {
                        todo = "replaceResult";
                    } else if (this.checkValueType(ltiOutcome)) {
                        urlLTI11 = null;
                        todo = "basic-lis-updateresult";
                    }
                    break;
                case EXT_DELETE:
                    if (lti11 && (ltiOutcome.getType() == EXT_TYPE_DECIMAL)) {
                        todo = "deleteResult";
                    } else if (ext) {
                        urlLTI11 = null;
                        todo = "basic-lis-deleteresult";
                    }
                    break;
            }
        }
        if (StringUtils.isNotEmpty(todo)) {
            String value = ltiOutcome.getValue();
            if (value == null) {
                value = "";
            }
            if (lti11) {
                xml = "";
                if (action == EXT_WRITE) {
                    xml = "\n"
+"        <result>\n"
+"          <resultScore>\n"
+"            <language>" + ltiOutcome.getLanguage() + "</language>\n"
+"            <textString>" + ltiOutcome.getValue() + "</textString>\n"
+"          </resultScore>\n"
+"        </result>\n";

                }
                sourcedId = StringEscapeUtils.escapeHtml4(sourcedId);
                xml += "\n"
+"      <resultRecord>\n"
+"        <sourcedGUID>\n"
+"          <sourcedId>{sourcedId}</sourcedId>\n"
+"        </sourcedGUID>\n"
+"      </resultRecord>\n";

                if (doLTI11Service(todo, urlLTI11, xml)) {
                    switch (action) {
                        case EXT_READ:
                        	value = LTIUtil.getXmlChildValue(this.extDoc.getRootElement(), "textString");
                        	if (value == null) {
                        		break;
                        	} else {
                        		ltiOutcome.setValue(value);
                        	}
                        case EXT_WRITE:
                        case EXT_DELETE:
                            response = true;
                            break;
                    }
                }
            } else {
                Map<String, List<String>> params = new HashMap<String, List<String>>();
                LTIUtil.setParameter(params, "sourcedid", sourcedId);
                LTIUtil.setParameter(params, "result_resultscore_textstring", value);
                if (StringUtils.isNotEmpty(ltiOutcome.getLanguage())) {
                    LTIUtil.setParameter(params, "result_resultscore_language", ltiOutcome.getLanguage());
                }
                if (StringUtils.isNotEmpty(ltiOutcome.getStatus())) {
                    LTIUtil.setParameter(params, "result_statusofresult", ltiOutcome.getStatus());
                }
                if (ltiOutcome.getDate() != null) {
                    LTIUtil.setParameter(params, "result_date", ltiOutcome.getDate().toString("YYYY-MM-DD"));
                }
                if (StringUtils.isNotEmpty(ltiOutcome.getType())) {
                    LTIUtil.setParameter(params, "result_resultvaluesourcedid", ltiOutcome.getType());
                }
                if (StringUtils.isNotEmpty(ltiOutcome.getDataSource())) {
                    LTIUtil.setParameter(params, "result_datasource", ltiOutcome.getDataSource());
                }
                try {
	                URL u = new URL(urlExt);
	                if (this.doService(todo, new URL(urlExt), params)) {
	                    switch (action) {
	                        case EXT_READ:
	                        	value = LTIUtil.getXmlChildValue(this.extDoc.getRootElement(), "textstring");
	                        	if (value != null) {
	                        		ltiOutcome.setValue(value);
	                        	}
	                            break;
	                        case EXT_WRITE:
	                        case EXT_DELETE:
	                            response = true;
	                            break;
	                    }
	                }
                } catch (MalformedURLException e) {
                	e.printStackTrace();
                }
            }
            
        }

        return response;

    }

/**
 * Perform a Memberships service request.
 *
 * The user table is updated with the new list of user objects.
 *
 * @param boolean withGroups True is group information is to be requested as well
 *
 * @return mixed Array of User objects or False if the request was not successful
 */
    
    public List<User> doMembershipsService() {
    	return doMembershipsService(false);
    }
    
    public List<User> doMembershipsService(boolean withGroups)
    {

        List<User> users = new ArrayList<User>();
        Map<String, User> oldUsers = this.getUserResultSourcedIDs(true, ToolProvider.ID_SCOPE_RESOURCE);
        this.extResponse = null;
        String urlString = this.getSetting("ext_ims_lis_memberships_url");
	    boolean ok = false;
	    Map<String, List<String>> params = new HashMap<String, List<String>>();
	    try {	
			URL url = new URL(urlString);

		    LTIUtil.setParameter(params, "id", getSetting("ext_ims_lis_memberships_id"));
		    if (withGroups) {
		        ok = doService("basic-lis-readmembershipsforcontextwithgroups", url, params);
		    }
		    if (ok) {
		    	groupSets = new HashMap<String, GroupSet>();
		        groups = new HashMap<String, Group>();
		    } else {
		        ok = doService("basic-lis-readmembershipsforcontext", url, params);
		    }
        } catch (MalformedURLException e) {
			e.printStackTrace();
		}

        if (ok) {
        	Element el = LTIUtil.getXmlChild(this.extDoc.getRootElement(), "memberships");
            if (el != null) {
              List<Element> members = el.getChildren("member");
              for (Element el2 : members) {
            	  String value = LTIUtil.getXmlChildValue(el2, "user_id");
            	  User user = User.fromResourceLink(this, value);
      //
      /// Set the user name
      //
                String firstname = LTIUtil.getXmlChildValue(el, "person_name_given");
                String lastname = LTIUtil.getXmlChildValue(el, "person_name_family");
                String fullname = LTIUtil.getXmlChildValue(el, "person_name_full");
                user.setNames(firstname, lastname, fullname);
      //
      /// Set the user email
      //
                value = LTIUtil.getXmlChildValue(el, "person_contact_email_primary");
                if (value == null) {
                  value = "";
                }
                user.setEmail(value, this.consumer.getDefaultEmail());
      //
      /// Set the user roles
      //
                value = LTIUtil.getXmlChildValue(el, "roles");
                if (value != null) {
                  user.setRoles(value);
                }
      //
      /// Set the user groups
      //
                el = LTIUtil.getXmlChild(el, "groups");
                if (el != null) {
                  List<Element> memberGroups = el.getChildren("group");
                  for (Element group : memberGroups) {
                    String groupId = LTIUtil.getXmlChildValue(group, "id");
                    Element set = LTIUtil.getXmlChild(group, "set");
                    String setId = null;
                    if (set != null) {
                      setId = LTIUtil.getXmlChildValue(set, "id");
                      GroupSet groupSet = this.groupSets.get(setId);
                      if (groupSet == null) {
                        groupSet = new GroupSet(LTIUtil.getXmlChildValue(set, "title"));
                        this.groupSets.put(setId, groupSet);
                      }
                      groupSet.incNumMembers();
                      if (user.isStaff()) {
                        groupSet.incNumStaff();
                      }
                      if (user.isLearner()) {
                        groupSet.incNumLearners();
                      }
                      groupSet.addGroup(groupId);
                    }
                    this.groups.put(groupId, new Group(LTIUtil.getXmlChildValue(group, "title"), setId));
                    user.addGroup(groupId);
                  }
                }
      //
      /// If a result sourcedid is provided save the user
      //
                value = LTIUtil.getXmlChildValue(el, "lis_result_sourcedid");
                if (value != null) {
                  user.setLtiResultSourcedId(value);
                  user.save();
                }
                users.add(user);
      //
      /// Remove old user (if it exists)
      //
                oldUsers.remove(user.getId(ToolProvider.ID_SCOPE_RESOURCE));
              }
      //
      /// Delete any old users which were not in the latest list from the tool consumer
      //
              for (User user : oldUsers.values()) {
            	  user.delete();
              }
            }
          } else {
            users = null;
          }

          return users;

        }

/**
 * Perform a Setting service request.
 *
 * @param int    action The action type constant
 * @param string value  The setting value (optional, default is null)
 *
 * @return mixed The setting value for a read action, true if a write or delete action was successful, otherwise false
 */
    
    public boolean doSettingService(int action) {
    	return doSettingService(action, null);
    }
    
    public boolean doSettingService(int action, String value)
    {
    	String todo = "";
        boolean response = false;
        this.extResponse = null;
        switch (action) {
            case EXT_READ:
                todo = "basic-lti-loadsetting";
                break;
            case EXT_WRITE:
                todo = "basic-lti-savesetting";
                break;
            case EXT_DELETE:
                todo = "basic-lti-deletesetting";
                break;
        }
        if (StringUtils.isNotEmpty(todo)) {

            String urlString = this.getSetting("ext_ims_lti_tool_setting_url");
            URL url = null;
			try {
				url = new URL(urlString);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
            Map<String, List<String>> params = new HashMap<String, List<String>>();
            LTIUtil.setParameter(params, String.valueOf(id), getSetting("ext_ims_lti_tool_setting_id"));
            if (value == null) {
                value = "";
            }
            LTIUtil.setParameter(params, "setting", value);

            if (this.doService(todo, url, params)) {
                switch (action) {
                    case EXT_READ:
                    	Element el = LTIUtil.getXmlChild(this.extDoc.getRootElement(), "setting");
                        if (el != null) {
                          this.setSetting("ext_ims_lti_tool_setting", LTIUtil.getXmlChildValue(el, "value"));
                        }
                        response = true;
                        break;
                    case EXT_WRITE:
                        this.setSetting("ext_ims_lti_tool_setting", value);
                        this.saveSettings();
                        response = true;
                        break;
                    case EXT_DELETE:
                        response = true;
                        break;
                }
            }
        }

        return response;

    }

/**
 * Check if the Tool Settings service is supported.
 *
 * @return boolean True if this resource link supports the Tool Settings service
 */
    public boolean hasToolSettingsService()
    {

        String url = this.getSetting("custom_link_setting_url");

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
    public Map<String, String> getToolSettings() {
    	return getToolSettings(ToolSettings.MODE_CURRENT_LEVEL, true);
    }
    public Map<String, String> getToolSettings(int mode) {
    	return getToolSettings(mode, true);
    }
    
    public Map<String, String> getToolSettings(int mode, boolean simple)
    {

        String url = this.getSetting("custom_link_setting_url");
        ToolSettings service = new ToolSettings(this, url, simple);
        return service.get(mode);

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

        String url = this.getSetting("custom_link_setting_url");
        ToolSettings service = new ToolSettings(this, url);
        LTIMessage http = service.set(settings);
        return (http != null);
    }

/**
 * Check if the Membership service is supported.
 *
 * @return boolean True if this resource link supports the Membership service
 */
    public boolean hasMembershipService()
    {

        boolean has = (this.contextId != null);
        if (has) {
            has = StringUtils.isNotEmpty(this.getContext().getSetting("custom_context_memberships_url"));
        }

        return has;

    }

/**
 * Get Memberships.
 *
 * @return mixed The array of User objects if successful, otherwise false
 */
    public List<User> getMembership()
    {

        List<User> response = null;
        if (this.contextId != null) {
            String url = this.getContext().getSetting("custom_context_memberships_url");
            if (StringUtils.isNotEmpty(url)) {
                Membership service = new Membership(this, url);
                response = service.get();
            }
        }

        return response;

    }

/**
 * Obtain an array of User objects for users with a result sourcedId.
 *
 * The array may include users from other resource links which are sharing this resource link.
 * It may also be optionally indexed by the user ID of a specified scope.
 *
 * @param boolean localOnly True if only users from this resource link are to be returned, not users from shared resource links (optional, default is false)
 * @param int     idScope     Scope to use for ID values (optional, default is null for consumer default)
 *
 * @return array Array of User objects
 */
    public Map<String, User> getUserResultSourcedIDs() {
    	return getUserResultSourcedIDs(false, 0);
    }
    
    public Map<String, User> getUserResultSourcedIDs(boolean localOnly) {
    	return getUserResultSourcedIDs(localOnly, 0);
    }
    
    public Map<String, User> getUserResultSourcedIDs(boolean localOnly, int idScope)
    {

        return this.getDataConnector().getUserResultSourcedIDsResourceLink(this, localOnly, idScope);

    }

/**
 * Get an array of ResourceLinkShare objects for each resource link which is sharing this context.
 *
 * @return array Array of ResourceLinkShare objects
 */
    public List<ResourceLinkShare> getShares()
    {

        return this.getDataConnector().getSharesResourceLink(this);

    }

/**
 * Class constructor from consumer.
 *
 * @param ToolConsumer consumer Consumer object
 * @param string ltiResourceLinkId Resource link ID value
 * @param string tempId Temporary Resource link ID value (optional, default is null)
 * @return ResourceLink
 */
    
    public static ResourceLink fromConsumer(ToolConsumer consumer, String ltiResourceLinkId) {
    	return fromConsumer(consumer, ltiResourceLinkId, null);
    }

    public static ResourceLink fromConsumer(ToolConsumer consumer, String ltiResourceLinkId, String tempId)
    {

        ResourceLink resourceLink = new ResourceLink();
        resourceLink.consumer = consumer;
        resourceLink.dataConnector = consumer.getDataConnector();
        resourceLink.ltiResourceLinkId = ltiResourceLinkId;
        if (StringUtils.isNotEmpty(ltiResourceLinkId)) {
            resourceLink.load();
            if (resourceLink.id == 0 && StringUtils.isNotEmpty(tempId)) {
                resourceLink.ltiResourceLinkId = tempId;
                resourceLink.load();
                resourceLink.ltiResourceLinkId = ltiResourceLinkId;
            }
        }

        return resourceLink;

    }

/**
 * Class constructor from context.
 *
 * @param Context context Context object
 * @param string ltiResourceLinkId Resource link ID value
 * @param string tempId Temporary Resource link ID value (optional, default is null)
 * @return ResourceLink
 */
    
    public static ResourceLink fromContext(Context context, String ltiResourceLinkId)
    {
    	return fromContext(context, ltiResourceLinkId, null);
    }
    public static ResourceLink fromContext(Context context, String ltiResourceLinkId, String tempId)
    {

        ResourceLink resourceLink = new ResourceLink();
        resourceLink.setContextId(context.getRecordId());
        resourceLink.context = context;
        resourceLink.dataConnector = context.getDataConnector();
        resourceLink.ltiResourceLinkId = ltiResourceLinkId;
        if (StringUtils.isNotEmpty(ltiResourceLinkId)) {
            resourceLink.load();
            if (resourceLink.id == 0 && StringUtils.isNotEmpty(tempId)) {
                resourceLink.ltiResourceLinkId = tempId;
                resourceLink.load();
                resourceLink.ltiResourceLinkId = ltiResourceLinkId;
            }
        }

        return resourceLink;

    }

/**
 * Load the resource link from the database.
 *
 * @param int id     Record ID of resource link
 * @param DataConnector   dataConnector    Database connection object
 *
 * @return ResourceLink  ResourceLink object
 */
    public static ResourceLink fromRecordId(int id, DataConnector dataConnector)
    {

        ResourceLink resourceLink = new ResourceLink();
        resourceLink.dataConnector = dataConnector;
        resourceLink.load(id);

        return resourceLink;

    }

///
///  PRIVATE METHODS
///

/**
 * Load the resource link from the database.
 *
 * @param int id     Record ID of resource link (optional, default is null)
 *
 * @return boolean True if resource link was successfully loaded
 */
    private boolean load() {
    	return load(0);
    }
    private boolean load(int id)
    {

        this.initialize();
        this.id = id;

        return this.getDataConnector().loadResourceLink(this);

    }

/**
 * Convert data type of value to a supported type if possible.
 *
 * @param Outcome     ltiOutcome     Outcome object
 * @param string[]    supportedTypes Array of outcome types to be supported (optional, default is null to use supported types reported in the last launch for this resource link)
 *
 * @return boolean True if the type/value are valid and supported
 */

	private boolean checkValueType(Outcome ltiOutcome) {
		return checkValueType(ltiOutcome, null);
	}

	private boolean checkValueType(Outcome ltiOutcome, List<String> supportedTypes)
    {

        if (supportedTypes == null) {
        	supportedTypes = new ArrayList<String>();
            String[] supportedTypesArray = StringUtils.split(
            		",",
            		StringUtils.replace((
            				this.getSetting(
            						"ext_ims_lis_resultvalue_sourcedids", 
            						EXT_TYPE_DECIMAL)).toLowerCase(), 
            				" ",
            				"")
            		);
            for(String t : supportedTypesArray) {
            	supportedTypes.add(t);
            }
        }
        String type = ltiOutcome.getType();
        String value = ltiOutcome.getValue();
// Check whether the type is supported or there is no value
        boolean ok = supportedTypes.contains(type) || (value.length() <= 0);
        if (!ok) {
// Convert numeric values to decimal
            if (type.equals(EXT_TYPE_PERCENTAGE)) {
                if (value.substring(value.length()).equals("%")) {
                    value = value.substring(0, value.length()-1);
                }
                ok = StringUtils.isNumeric(value) && (Double.valueOf(value) >= 0) && (Double.valueOf(value) <= 100);
                if (ok) {
                    ltiOutcome.setValue(String.valueOf(Double.valueOf(value) / 100));
                    ltiOutcome.setType(EXT_TYPE_DECIMAL);
                }
            } else if (type.equals(EXT_TYPE_RATIO)) {
                String[] parts = StringUtils.split(value, '/');
                ok = (parts.length == 2) 
                		&& StringUtils.isNumeric(parts[0]) 
                		&& StringUtils.isNumeric(parts[1]) 
                		&& (Double.valueOf(parts[0]) >= 0) 
                		&& (Double.valueOf(parts[1]) > 0);
                if (ok) {
                    ltiOutcome.setValue(String.valueOf(Double.valueOf(parts[0]) / Double.valueOf(parts[1])));
                    ltiOutcome.setType(EXT_TYPE_DECIMAL);
                }
// Convert letter_af to letter_af_plus or text
            } else if (type.equals(EXT_TYPE_LETTER_AF)) {
                if (supportedTypes.contains(EXT_TYPE_LETTER_AF_PLUS)) {
                    ok = true;
                    ltiOutcome.setType(EXT_TYPE_LETTER_AF_PLUS);
                } else if (supportedTypes.contains(EXT_TYPE_TEXT)) {
                    ok = true;
                    ltiOutcome.setType(EXT_TYPE_TEXT);
                }
// Convert letter_af_plus to letter_af or text
            } else if (type.equals(EXT_TYPE_LETTER_AF_PLUS)) {
                if (supportedTypes.contains(EXT_TYPE_LETTER_AF) && (value.length() == 1)) {
                    ok = true;
                    ltiOutcome.setType(EXT_TYPE_LETTER_AF);
                } else if (supportedTypes.contains(EXT_TYPE_TEXT)) {
                    ok = true;
                    ltiOutcome.setType(EXT_TYPE_TEXT);
                }
// Convert text to decimal
            } else if (type == EXT_TYPE_TEXT) {
                ok = StringUtils.isNumeric(value) 
                		&& (Double.valueOf(value) >= 0) 
                		&& (Double.valueOf(value) <= 1);
                if (ok) {
                    ltiOutcome.setType(EXT_TYPE_DECIMAL);
                } else if (value.substring(value.length()-1).equals("%")) {
                    value = value.substring(0, -1);
                    ok = StringUtils.isNumeric(value) 
                    		&& (Double.valueOf(value) >= 0) 
                    		&& (Double.valueOf(value) <=100);
                    if (ok) {
                        if (supportedTypes.contains(EXT_TYPE_PERCENTAGE)) {
                            ltiOutcome.setType(EXT_TYPE_PERCENTAGE);
                        } else {
                            ltiOutcome.setValue(String.valueOf(Double.valueOf(value) / 100));
                            ltiOutcome.setType(EXT_TYPE_DECIMAL);
                        }
                    }
                }
            }
        }

        return ok;

    }

/**
 * Send a service request to the tool consumer.
 *
 * @param string type   Message type value
 * @param string url    URL to send request to
 * @param array  params Associative array of parameter values to be passed
 *
 * @return boolean True if the request successfully obtained a response
 */
    private boolean doService(String type, URL url, Map<String, List<String>> params)
    {

        boolean ok = false;
        this.extRequest = null;
        this.extRequestHeaders = new HashMap<String, String>();
        this.extResponse = null;
        this.extResponseHeaders = new HashMap<String, String>();
        if (url != null) {
            params = this.getConsumer().signParameters(
            		url.toExternalForm(), 
            		type, 
            		this.getConsumer().getLtiVersion(), 
            		params);
// Connect to tool consumer
            LTIMessage http = new LTIMessage(url.toExternalForm(), "POST", params);
// Parse XML response
            if (http.send()) {
                this.extResponse = http.getResponse();
                this.extResponseHeaders = http.getResponseHeaders();
                try {
                	extDoc = LTIUtil.getXMLDoc(http.getResponse());
                	if (extDoc != null) {
                		Element el = LTIUtil.getXmlChild(extDoc.getRootElement(), "statusinfo");
                		ok = el != null;
                		if (ok) {
                			String responseCode = LTIUtil.getXmlChildValue(el, "codemajor");
                			ok = responseCode != null;
                			if (ok) {
                				ok = responseCode.equals("Success");
                			}
                		}
                	}
                } catch (Exception e) {
                	e.printStackTrace();
                }
            }
            this.extRequest = http.getRequest();
            this.extRequestHeaders = http.getRequestHeaders();
        }

        return ok;

    }

/**
 * Send a service request to the tool consumer.
 *
 * @param string type Message type value
 * @param string url  URL to send request to
 * @param string xml  XML of message request
 *
 * @return boolean True if the request successfully obtained a response
 */
    private boolean doLTI11Service(String type, String url, String xml)
    {

        boolean ok = false;
        this.extRequest = null;
        this.extRequestHeaders = null;
        this.extResponse = null;
        this.extResponseHeaders = null;
        if (StringUtils.isNotEmpty(url)) {
            String messageId = UUID.randomUUID().toString();
            String xmlRequest = "<?xml version = \"1.0\" encoding = \"UTF-8\"?>\n"
+"<imsx_POXEnvelopeRequest xmlns = \"http://www.imsglobal.org/services/ltiv1p1/xsd/imsoms_v1p0\">\n"
+"  <imsx_POXHeader>\n"
+"    <imsx_POXRequestHeaderInfo>\n"
+"      <imsx_version>V1.0</imsx_version>\n"
+"      <imsx_messageIdentifier>" + id + "</imsx_messageIdentifier>\n"
+"    </imsx_POXRequestHeaderInfo>\n"
+"  </imsx_POXHeader>\n"
+"  <imsx_POXBody>\n"
+"    <" + type + "Request>\n"
+xml
+"    </" + type + "Request>\n"
+"  </imsx_POXBody>\n"
+"</imsx_POXEnvelopeRequest>\n";
         // Calculate body hash
            String hash = Base64.encodeBase64String(DigestUtils.sha1(xmlRequest.toString()));
            Map<String,String> params = new HashMap<String,String>();
            params.put("oauth_body_hash", hash);
            HashSet<Map.Entry<String,String>> httpParams = new HashSet<Map.Entry<String,String>>();
            httpParams.addAll(params.entrySet());
      // Check for query parameters which need to be included in the signature
            Map<String,String> queryParams = new HashMap<String,String>();
            String urlNoQuery = url;
            try {
              URL uri = new URL(url);
              String query = uri.getQuery();
              if (query != null) {
                urlNoQuery = urlNoQuery.substring(0, urlNoQuery.length() - query.length() - 1);
                String[] queryItems = query.split("&");
                for (int i = 0; i < queryItems.length; i++) {
                  String[] queryItem = queryItems[i].split("=", 2);
                  if (queryItem.length > 1) {
                    queryParams.put(queryItem[0], queryItem[1]);
                  } else {
                    queryParams.put(queryItem[0], "");
                  }
                }
                httpParams.addAll(queryParams.entrySet());
              }
            } catch (Exception e) {
            	e.printStackTrace();
            }
      // Add OAuth signature
            Map<String,String> header = new HashMap<String,String>();
            OAuthMessage oAuthMessage = new OAuthMessage("POST", urlNoQuery, httpParams);
            OAuthConsumer oAuthConsumer = new OAuthConsumer("about:blank", this.consumer.getKey(), this.consumer.getSecret(), null);
            OAuthAccessor oAuthAccessor = new OAuthAccessor(oAuthConsumer);
            try {
              oAuthMessage.addRequiredParameters(oAuthAccessor);
              header.put("Authorization", oAuthMessage.getAuthorizationHeader(null));
              header.put("Content-Type", "application/xml");
            } catch (OAuthException e) {
            } catch (URISyntaxException e) {
            } catch (IOException e) {
            }
            try {
	            StringEntity entity = new StringEntity(xmlRequest);
	            
	      // Connect to tool consumer
	            this.extResponse = doPostRequest(url, LTIUtil.getHTTPParams(params), header, entity);
	      // Parse XML response
	            if (this.extResponse != null) {
	              this.extDoc = LTIUtil.getXMLDoc(extResponse);
	              ok = this.extDoc != null;
	              if (ok) {
	                Element el = LTIUtil.getXmlChild(this.extDoc.getRootElement(), "imsx_statusInfo");
	                ok = el != null;
	                if (ok) {
	                  String responseCode = LTIUtil.getXmlChildValue(el, "imsx_codeMajor");
	                  ok = responseCode != null;
	                  if (ok) {
	                    ok = responseCode.equals("success");
	                  }
	                }
	              }
		          if (!ok) {
		            this.extResponse = null;
		          }
	            }
            } catch (UnsupportedEncodingException e) {
            	e.printStackTrace();
            }
          }

          return (this.extResponse != null);

        }

	public String getPrimaryResourceLinkId() {
		return primaryResourceLinkId;
	}

	public void setPrimaryResourceLinkId(String primaryResourceLinkId) {
		this.primaryResourceLinkId = primaryResourceLinkId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLtiResourceLinkId() {
		return ltiResourceLinkId;
	}

	public void setLtiResourceLinkId(String ltiResourceLinkId) {
		this.ltiResourceLinkId = ltiResourceLinkId;
	}

	public Map<String, GroupSet> getGroupSets() {
		return groupSets;
	}

	public void setGroupSets(Map<String, GroupSet> groupSets) {
		this.groupSets = groupSets;
	}

	public Map<String, Group> getGroups() {
		return groups;
	}

	public void setGroups(Map<String, Group> groups) {
		this.groups = groups;
	}

	public String getExtRequest() {
		return extRequest;
	}

	public void setExtRequest(String extRequest) {
		this.extRequest = extRequest;
	}

	public Map<String, String> getExtRequestHeaders() {
		return extRequestHeaders;
	}

	public void setExtRequestHeaders(Map<String, String> extRequestHeaders) {
		this.extRequestHeaders = extRequestHeaders;
	}

	public String getExtResponse() {
		return extResponse;
	}

	public void setExtResponse(String extResponse) {
		this.extResponse = extResponse;
	}

	public Map<String, String> getExtResponseHeaders() {
		return extResponseHeaders;
	}

	public void setExtResponseHeaders(Map<String, String> extResponseHeaders) {
		this.extResponseHeaders = extResponseHeaders;
	}

	public boolean isShareApproved() {
		return shareApproved;
	}

	public void setShareApproved(boolean shareApproved) {
		this.shareApproved = shareApproved;
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

	public boolean isSettingsChanged() {
		return settingsChanged;
	}

	public void setSettingsChanged(boolean settingsChanged) {
		this.settingsChanged = settingsChanged;
	}

	public Document getExtDoc() {
		return extDoc;
	}

	public void setExtDoc(Document extDoc) {
		this.extDoc = extDoc;
	}

	public Map<String, List<String>> getExtNodes() {
		return extNodes;
	}

	public void setExtNodes(Map<String, List<String>> extNodes) {
		this.extNodes = extNodes;
	}

	public int getConsumerId() {
		return consumerId;
	}

	public void setConsumer(ToolConsumer consumer) {
		this.consumer = consumer;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public void setDataConnector(DataConnector dataConnector) {
		this.dataConnector = dataConnector;
	}
	
	/**
	 * Returns the consumer key for the resource link with which this resource link is shared.
	 *
	 * @return consumer key
	 */
	  public String getPrimaryConsumerKey() {
	    return this.primaryConsumerKey;
	  }

	/**
	 * Set the consumer key for the resource link with which this resource link is shared.
	 *
	 * @param primaryConsumerKey  consumer key
	 */
	  public void setPrimaryConsumerKey(String primaryConsumerKey) {
	    this.primaryConsumerKey = primaryConsumerKey;
	  }
	
	/**
	 * Performs an HTTP POST request.
	 *
	 * @param url     URL to send request to
	 * @param params  map of parameter values to be passed
	 * @param header  values to include in the request header
	 *
	 * @return response returned from request, null if an error occurred
	 */
	  private String doPostRequest(String url, List<NameValuePair> params, Map<String,String> header,
	     StringEntity entity) {

	    String fileContent = null;
	    RequestConfig requestConfig = RequestConfig.custom()
		   .setConnectTimeout(TIMEOUT)
		   .setConnectionRequestTimeout(TIMEOUT)
		   .setSocketTimeout(TIMEOUT)
		   .build();
		CloseableHttpClient httpClient = HttpClients.custom()
		   .setDefaultRequestConfig(requestConfig)
		   .setRedirectStrategy(new DefaultRedirectStrategy())
		   .build();
	    HttpPost httpPost = new HttpPost(url);
	    try {
	    	if (header != null) {
	    		for (String name : header.keySet()) {
	    			params.add(new BasicNameValuePair(name, header.get(name)));
	    		}
	    	}
		    httpPost.setEntity(new UrlEncodedFormEntity(params));
		    HttpResponse hr = httpClient.execute(httpPost);
		    if (hr.getStatusLine().getStatusCode() < 400) { 
		    	BufferedReader rd = new BufferedReader(
		    			new InputStreamReader(hr.getEntity().getContent()));

		    	StringBuffer result = new StringBuffer();
		    	String line = "";
		    	while ((line = rd.readLine()) != null) {
		    		result.append(line);
		    	}
		    	fileContent = result.toString();
		    }
		    httpClient.close();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    	fileContent = null;
	    }

	    return fileContent;

	  }

	public void setSettingsMap(Map<String, String> settings2) {
		for (String key : settings2.keySet()) {
			LTIUtil.setParameter(settings, key, settings2.get(key));
		}
		
	}

                


}