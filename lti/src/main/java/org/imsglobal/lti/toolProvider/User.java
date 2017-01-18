package org.imsglobal.lti.toolProvider;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.imsglobal.lti.toolProvider.dataConnector.DataConnector;
import org.joda.time.DateTime;

public class User {

	public void setUpdated(DateTime now) {
		this.updated = now;
	}

	public void setCreated(DateTime now) {
		this.created = now;
	}

	public void setLtiUserId(String string) {
		this.ltiUserId = string;
		
	}

	public DateTime getCreated() {
		return created;
	}

	public DateTime getUpdated() {
		return updated;
	}

	public int getResourceLinkId() {
		return resourceLinkId;
	}

	public String getLtiUserId() {
		return ltiUserId;
	}

	public void setResourceLink(ResourceLink resourceLink) {
		this.resourceLink = resourceLink;
	}

	public void setDataConnector(DataConnector dataConnector) {
		this.dataConnector = dataConnector;
	}

	/**
	 * Set the user's roles to those specified in the list provided.
	 *
	 * @param rolesList  a list of role values
	 */
	  public void setRoles(List<String> rolesList) {
	    this.roles.clear();
	    for (String role : rolesList) {
	      role = role.trim();
	      if (role.length() > 0) {
	        if (!role.startsWith("urn:")) {
	          role = "urn:lti:role:ims/lis/" + role;
	        }
	        this.roles.add(role);
	      }
	    }
	  }
	  
	  /**
	   * Set the user's roles to those specified in the comma separated list provided.
	   *
	   * @param rolesString  a string containing a list of roles
	   */
	    public void setRoles(String rolesString) {
	      this.setRoles(Arrays.asList(rolesString.split(",")));
	    }


	public void setLtiResultSourcedId(String string) {
		this.ltiResultSourcedId = string;
		
	}
	

/**
 * Class to represent a tool consumer user
 *
 * @author  Stephen P Vickers <svickers@imsglobal.org>
 * @copyright  IMS Global Learning Consortium Inc
 * @date  2016
 * @version 3.0.2
 * @license http://www.apache.org/licenses/LICENSE-2.0 Apache License, Version 2.0
 */

/**
 * User"s first name.
 *
 * @var string firstname
 */
    private String firstname = "";
/**
 * User"s last name (surname or family name).
 *
 * @var string lastname
 */
    private String lastname = "";
/**
 * User"s fullname.
 *
 * @var string fullname
 */
    private String fullname = "";
/**
 * User"s email address.
 *
 * @var string email
 */
    private String email = "";
/**
 * User"s image URI.
 *
 * @var string image
 */
    private URL image;
/**
 * Roles for user.
 *
 * @var array roles
 */
    private List<String> roles;
/**
 * Groups for user.
 *
 * @var array groups
 */
    private List<String> groups;
/**
 * User"s result sourcedid.
 *
 * @var string ltiResultSourcedId
 */
    private String ltiResultSourcedId = null;
/**
 * Date/time the record was created.
 *
 * @var object created
 */
    private DateTime created = null;
/**
 * Date/time the record was last updated.
 *
 * @var object updated
 */
    private DateTime updated = null;

/**
 * Resource link object.
 *
 * @var ResourceLink resourceLink
 */
    private ResourceLink resourceLink = null;
/**
 * Resource link record ID.
 *
 * @var int resourceLinkId
 */
    private int resourceLinkId;
/**
 * User record ID value.
 *
 * @var string id
 */
    private String id = null;
/**
 * user ID as supplied in the last connection request.
 *
 * @var string ltiUserId
 */
    private String ltiUserId = null;
/**
 * Data connector object or string.
 *
 * @var mixed dataConnector
 */
    private DataConnector dataConnector = null;

/**
 * Class constructor.
 */
    public User()
    {

        this.initialize();

    }

/**
 * Initialise the user.
 */
    public void initialize()
    {

        this.firstname = "";
        this.lastname = "";
        this.fullname = "";
        this.email = "";
        try {
			this.setImage(new URL(""));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
        this.roles = new ArrayList<String>();
        this.groups = new ArrayList<String>();
        this.ltiResultSourcedId = null;
        this.created = null;
        this.updated = null;

    }

/**
 * Initialise the user.
 *
 * Pseudonym for initialize().
 */
    public void initialise()
    {

        this.initialize();

    }

/**
 * Save the user to the database.
 *
 * @return boolean True if the user object was successfully saved
 */
    public boolean save()
    {
    	boolean ok = true;
        if (StringUtils.isNotEmpty(ltiResultSourcedId) && resourceLinkId != 0) {
            ok = getDataConnector().saveUser(this);
        }

        return ok;

    }

/**
 * Delete the user from the database.
 *
 * @return boolean True if the user object was successfully deleted
 */
    public boolean delete()
    {

        return this.getDataConnector().deleteUser(this);

    }

/**
 * Get resource link.
 *
 * @return ResourceLink Resource link object
 */
    public ResourceLink getResourceLink()
    {

        if (this.resourceLink == null && resourceLinkId != 0) {
            this.resourceLink = ResourceLink.fromRecordId(this.resourceLinkId, this.getDataConnector());
        }

        return resourceLink;

    }

/**
 * Get record ID of user.
 *
 * @return String Record ID of user
 */
    public String getRecordId()
    {

        return this.id;

    }

/**
 * Set record ID of user.
 *
 * @param String id  Record ID of user
 */
    public void setRecordId(String id)
    {

        this.id = id;

    }

/**
 * Set resource link ID of user.
 *
 * @param int resourceLinkId  Resource link ID of user
 */
    public void setResourceLinkId(int resourceLinkId)
    {

        this.resourceLinkId = resourceLinkId;

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
 * Get the user ID (which may be a compound of the tool consumer and resource link IDs).
 *
 * @param int idScope Scope to use for user ID (optional, default is null for consumer default setting)
 *
 * @return string User ID value
 */
    public String getId() {
    	return getId(0);
    }
    
    public String getId(int idScope)
    {

        if (idScope == 0) {
            if (resourceLink != null) {
                idScope = resourceLink.getConsumer().idScope;
            } else {
                idScope = ToolProvider.ID_SCOPE_ID_ONLY;
            }
        }
        switch (idScope) {
            case ToolProvider.ID_SCOPE_GLOBAL:
                id = this.getResourceLink().getKey() + ToolProvider.ID_SCOPE_SEPARATOR + ltiUserId;
                break;
            case ToolProvider.ID_SCOPE_CONTEXT:
                id = this.getResourceLink().getKey();
                if (resourceLink.getContextId() != null) {
                    id += ToolProvider.ID_SCOPE_SEPARATOR + this.resourceLink.getContextId();
                }
                id += ToolProvider.ID_SCOPE_SEPARATOR + this.ltiUserId;
                break;
            case ToolProvider.ID_SCOPE_RESOURCE:
                id = this.getResourceLink().getKey();
                if (this.resourceLink.getLtiResourceLinkId() != null) {
                    id += ToolProvider.ID_SCOPE_SEPARATOR + this.resourceLink.getLtiResourceLinkId();
                }
                id += ToolProvider.ID_SCOPE_SEPARATOR + this.ltiUserId;
                break;
            default:
                id = this.ltiUserId;
                break;
        }

        return id;

    }
    
    

    public String getLtiResultSourcedId() {
		return ltiResultSourcedId;
	}

/**
 * Set the user"s name.
 *
 * @param string firstname User"s first name.
 * @param string lastname User"s last name.
 * @param string fullname User"s full name.
 */
    public void setNames(String firstname, String lastname, String fullname)
    {

        String[] names = new String[2];
        if (StringUtils.isNotEmpty(fullname)) {
            this.fullname = StringUtils.trim(fullname);
            names = StringUtils.split(fullname);
        }
        if (StringUtils.isNotEmpty(firstname)) {
            this.firstname = StringUtils.trim(firstname);
            names[0] = this.firstname;
        } else if (StringUtils.isNotEmpty(names[0])) {
            this.firstname = names[0];
        } else {
            this.firstname = "User";
        }
        if (StringUtils.isNotEmpty(lastname)) {
            this.lastname = StringUtils.trim(lastname);
            names[1] = this.lastname;
        } else if (StringUtils.isNotEmpty(names[1])) {
            this.lastname = names[1];
        } else {
            this.lastname = this.ltiUserId;
        }
        if (StringUtils.isEmpty(this.fullname)) {
        	fullname = firstname + " " + lastname;
        }

    }

/**
 * Set the user"s email address.
 *
 * @param string email        Email address value
 * @param string defaultEmail Value to use if no email is provided (optional, default is none)
 */
    public void setEmail(String email) {
    	setEmail(email, null);
    }
    public void setEmail(String email, String defaultEmail)
    {

      if (StringUtils.isNotEmpty(email)) {
          this.email = email;
      } else if (StringUtils.isNotEmpty(defaultEmail)) {
          this.email = defaultEmail;
          if (this.email.substring(0, 1).equals("@")) {
              this.email = this.getId() + this.email;
          }
      } else {
          this.email = "";
      }

    }

/**
 * Check if the user is an administrator (at any of the system, institution or context levels).
 *
 * @return boolean True if the user has a role of administrator
 */
    public boolean isAdmin()
    {

        return this.hasRole("Administrator") || this.hasRole("urn:lti:sysrole:ims/lis/SysAdmin") ||
               this.hasRole("urn:lti:sysrole:ims/lis/Administrator") || this.hasRole("urn:lti:instrole:ims/lis/Administrator");

    }

/**
 * Check if the user is staff.
 *
 * @return boolean True if the user has a role of instructor, contentdeveloper or teachingassistant
 */
    public boolean isStaff()
    {

        return (this.hasRole("Instructor") || this.hasRole("ContentDeveloper") || this.hasRole("TeachingAssistant"));

    }

/**
 * Check if the user is a learner.
 *
 * @return boolean True if the user has a role of learner
 */
    public boolean isLearner()
    {

        return this.hasRole("Learner");

    }

/**
 * Load the user from the database.
 *
 * @param String id     Record ID of user
 * @param DataConnector   dataConnector    Database connection object
 *
 * @return User  User object
 */
    public static User fromRecordId(String id, DataConnector dataConnector)
    {

        User user = new User();
        user.dataConnector = dataConnector;
        user.load(id);

        return user;

    }

/**
 * Class constructor from resource link.
 *
 * @param ResourceLink resourceLink Resource_Link object
 * @param string ltiUserId User ID value
 * @return User
 */
    public static User fromResourceLink(ResourceLink resourceLink, String ltiUserId)
    {

        User user = new User();
       	user.resourceLink = resourceLink;
        if (resourceLink != null) {
            user.resourceLinkId = resourceLink.getRecordId();
            user.dataConnector = resourceLink.getDataConnector();
        }
        user.ltiUserId = ltiUserId;
        if (StringUtils.isNotEmpty(ltiUserId)) {
            user.load();
        }

        return user;

    }

///
///  PRIVATE METHODS
///

/**
 * Check whether the user has a specified role name.
 *
 * @param string role Name of role
 *
 * @return boolean True if the user has the specified role
 */
    private boolean hasRole(String role) {

        if (role.substring(0, 4) != "urn:")
        {
            role = "urn:lti:role:ims/lis/" + role;
        }

        return this.roles.contains(role);

    }

/**
 * Load the user from the database.
 *
 * @param int id     Record ID of user (optional, default is null)
 *
 * @return boolean True if the user object was successfully loaded
 */
    private boolean load() {
    	return load(null);
    }
    
    private boolean load(String id)
    {

        this.initialize();
        this.id = id;
        dataConnector = this.getDataConnector();
        if (dataConnector != null) {
            return dataConnector.loadUser(this);
        }

        return false;
    }

    /**
     * Add a group ID for the user.
     *
     * @param id  group ID
     */
	public void addGroup(String groupId) {
		this.groups.add(groupId);
		
	}
	
	/**
	 * Returns a list of group IDs for which the user is a member.
	 *
	 * @return list of group IDs
	 */
	  public List<String> getGroups() {
	    return Collections.unmodifiableList(groups);
	  }

	  /**
	   * Returns a string representation of this user.
	   * <p>
	   * The string representation consists of the user's ID, firstname, lastname,
	   * fullname, email address, result sourcedId, roles and groups.
	   *
	   * @return  a string representation of this user
	   */
	    @Override
	    public String toString() {

	      StringBuilder value = new StringBuilder();
	      value.append(User.class.getName()).append("\n");
	      value.append("  id: ").append(this.id).append(" (").append(this.resourceLink.getConsumer().getKey()).append(")").append("\n");
	      value.append("  firstname: ").append(this.firstname).append("\n");
	      value.append("  lastname: ").append(this.lastname).append("\n");
	      value.append("  fullname: ").append(this.fullname).append("\n");
	      value.append("  email: ").append(this.email).append("\n");
	      value.append("  resultSourcedId: ").append(this.ltiResultSourcedId).append("\n");
	      value.append("  roles: ");
	      String sep = "";
	      for (String role : roles) {
	    	  value.append(sep).append(role);
	    	  sep = ", "; //after first use - clever!
	      }
	      value.append("\n");
	      value.append("  groups: ");
	      sep = "";
	      for (String group : groups) {
	        value.append(sep).append(group);
	        sep = ", ";
	      }
	      value.append("\n");

	      return value.toString();

	    }

	public URL getImage() {
		return image;
	}

	public void setImage(URL image) {
		this.image = image;
	}
}
