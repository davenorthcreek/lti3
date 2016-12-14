package imsglobal.toolProvider.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import imsglobal.LTIUtil;
import imsglobal.toolProvider.LTISource;
import imsglobal.toolProvider.ResourceLink;
import imsglobal.toolProvider.ToolConsumer;
import imsglobal.toolProvider.ToolProvider;
import imsglobal.toolProvider.User;

public class Membership extends Service {
	/**
	 * Class to implement the Membership service
	 *
	 * @author  Stephen P Vickers <svickers@imsglobal.org>
	 * @copyright  IMS Global Learning Consortium Inc
	 * @date  2016
	 * @version 3.0.0
	 * @license http://www.apache.org/licenses/LICENSE-2.0 Apache License, Version 2.0
	 */

	/**
	 * The object to which the settings apply (ResourceLink, Context or ToolConsumer).
	 *
	 * @var object  source
	 */
	    private LTISource source;

	/**
	 * Class constructor.
	 *
	 * @param object       source     The object to which the memberships apply (ResourceLink or Context)
	 * @param string       endpoint   Service endpoint
	 */
	    public Membership(LTISource source, String endpoint)
	    {
	    	super(source.getConsumer(), endpoint, "application/vnd.ims.lis.v2.membershipcontainer+json");
	        this.source = source;

	    }

	/**
	 * Get the memberships.
	 *
	 * @param string    role   Role for which memberships are to be requested (optional, default is all roles)
	 * @param int       limit  Limit on the number of memberships to be returned (optional, default is all)
	 *
	 * @return mixed The array of User objects if successful, otherwise false
	 */
	    public List<User> get() {
	    	return get(null, 0);
	    }
	    
	    public List<User> get(String role) {
	    	return get(role, 0);
	    }
	    
	    public List<User> get(String role, int limit) {

	        boolean isLink = (this.source instanceof ResourceLink);
	        Map<String, List<String>> parameters = new HashMap<String, List<String>>();
	        if (role != null) {
	            LTIUtil.setParameter(parameters, "role", role);
	        }
	        if (limit > 0) {
	        	LTIUtil.setParameter(parameters, "limit", String.valueOf(limit));
	        }
	        if (isLink) {
	        	LTIUtil.setParameter(parameters, "rlid", this.source.getId());
	        }
	        LTIMessage http = this.send("GET", parameters);
	        List<User> users = null;
	        if (!http.ok) {
	            //still null
	        } else {
	            List<User> users = new List<User>();
	            if (isLink) {
	                List<String> oldUserIds = this.source.getUserResultSourcedIDs(true, ToolProvider.ID_SCOPE_RESOURCE);
	            }
	            for (User membership : http.getMembership()) {
	            //foreach (http.responseJson.pageOf.membershipSubject.membership as membership) {
	            	User user = new User();
	                member = membership.member;
	                if (isLink) {
	                    user = User.fromResourceLink(this.source, member.userId);
	                } else {
	                    user.setLtiUserId(member.userId);
	                }

	// Set the user name
	                String firstname = (isset(member.givenName)) ? member.givenName : "";
	                String lastname = (isset(member.familyName)) ? member.familyName : "";
	                String fullname = (isset(member.name)) ? member.name : "";
	                user.setNames(firstname, lastname, fullname);

	// Set the user email
	                String email = (isset(member.email)) ? member.email : "";
	                user.setEmail(email, this.source.getConsumer().defaultEmail);

	// Set the user roles
	                if (isset(membership.role)) {
	                    user.setRoles(ToolProvider.parseRoles(membership.role));
	                }

	// If a result sourcedid is provided save the user
	                if (isLink) {
	                    if (isset(member.message)) {
	                        foreach (member.message as message) {
	                            if (isset(message.message_type) && (message.message_type === 'basic-lti-launch-request')) {
	                                if (isset(message.lis_result_sourcedid)) {
	                                    user.ltiResultSourcedId = message.lis_result_sourcedid;
	                                    user.save();
	                                }
	                                break;                                
	                            }
	                        }
	                    }
	                }
	                users[] = user;

	// Remove old user (if it exists)
	                if (isLink) {
	                    unset(oldUsers[user.getId(ToolProvider\ToolProvider::ID_SCOPE_RESOURCE)]);
	                }
	            }

	// Delete any old users which were not in the latest list from the tool consumer
	            if (isLink) {
	                foreach (oldUsers as id => user) {
	                    user.delete();
	                }
	            }
	        }

	        return users;

	    }
}
