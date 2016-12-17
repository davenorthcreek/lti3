package imsglobal.toolProvider.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import imsglobal.LTIMessage;
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
	    	this.setConsumer(source.getConsumer());
	    	this.setEndpoint(endpoint);
	    	this.setMediaType("application/vnd.ims.lis.v2.membershipcontainer+json");
	        this.setSource(source);

	    }

	private void setSource(LTISource source2) {
		this.source = source2;
		
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
	        if (!http.isOk()) {
	            //still null
	        } else {
	            users = new ArrayList<User>();
	            List<User> oldUserIds = new ArrayList<User>();
	            if (isLink) {
	                oldUserIds = this.source.getUserResultSourcedIDs(true, ToolProvider.ID_SCOPE_RESOURCE);
	            }
	            Map<String, Map<String, String>> membership = getMembership(http);
	            for (String id : membership.keySet()) {
	            	Map<String, String> member = membership.get(id);
	            //foreach (http.responseJson.pageOf.membershipSubject.membership as membership) {
	            	User user = new User();
	                //User member = membership.get("member");
	                if (isLink) {
	                    user = User.fromResourceLink(this.source, member.get("userId"));
	                } else {
	                    user.setLtiUserId(member.get("userId"));
	                }

	// Set the user name
	                String firstname = (member.containsKey("givenName")) ? member.get("givenName") : "";
	                String lastname = (member.containsKey("familyName")) ? member.get("lastName") : "";
	                String fullname = (member.containsKey("name")) ? member.get("fullName") : "";
	                user.setNames(firstname, lastname, fullname);

	// Set the user email
	                String email = (member.containsKey("email")) ? member.get("email") : "";
	                user.setEmail(email, this.source.getConsumer().defaultEmail);

	// Set the user roles
	                if (member.containsKey("role")) {
	                	user.setRoles(ToolProvider.parseRoles(member.get("role")));
	                }

	// If a result sourcedid is provided save the user
	                if (isLink) {
	                    if (member.containsKey("message")) {
	                    	for (Map<String, String> message : parseMessages(member.get("message"))) {
	                            if (message.containsKey("message_type") && (message.get("message_type") == "basic-lti-launch-request")) {
	                                if (message.containsKey("lis_result_sourcedid")) {
	                                    user.setLtiResultSourcedId(message.get("lis_result_sourcedid"));
	                                    user.save();
	                                }
	                                break;                                
	                            }
	                        }
	                    }
	                }
	                users.add(user);

	// Remove old user (if it exists)
	                if (isLink) {
	                	oldUserIds.remove(user.getId(ToolProvider.ID_SCOPE_RESOURCE));
	                }
	            }

	// Delete any old users which were not in the latest list from the tool consumer
	            if (isLink) {
	                for (User userToDelete : oldUserIds) {
	                    userToDelete.delete();
	                }
	            }
	        }

	        return users;

	    }

		private List<Map<String, String>> parseMessages(String string) {
			// TODO take JSON string, turn into list of messages and message IDs
			return null;
		}

		private Map<String, Map<String, String>> getMembership(LTIMessage http) {
			return new HashMap<String, Map<String, String>>();
		}
}
