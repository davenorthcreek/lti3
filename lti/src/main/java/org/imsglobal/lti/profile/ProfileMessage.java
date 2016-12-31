package org.imsglobal.lti.profile;

import java.util.List;
import java.util.Map;

public class ProfileMessage {

/**

 * Class to represent a resource handler message object
 *
 * @author  Stephen P Vickers <svickers@imsglobal.org>
 * @copyright  IMS Global Learning Consortium Inc
 * @date  2016
 * @version 3.0.0
 * @license http://www.apache.org/licenses/LICENSE-2.0 Apache License, Version 2.0
 */


/**
 * LTI message type.
 *
 * @var string type
 */
    public String type = null;
/**
 * Path to send message request to (used in conjunction with a base URL for the Tool Provider).
 *
 * @var string path
 */
    public String path = null;
/**
 * Capabilities required by message.
 *
 * @var array capabilities
 */
    public List<String> capabilities;
/**
 * Variable parameters to accompany message request.
 *
 * @var array variables
 */
	private Map<String, List<String>> variables;

/**
 * Fixed parameters to accompany message request.
 *
 * @var array constants
 */
	private Map<String, List<String>> constants;


/**
 * Class constructor.
 *
 * @param string type          LTI message type
 * @param string path          Path to send message request to
 * @param array  capabilities  Array of capabilities required by message
 * @param array  variables     Array of variable parameters to accompany message request
 * @param array  constants     Array of fixed parameters to accompany message request
 */
    
    public ProfileMessage(
    		String type, 
    		String path, 
    		List<String> capabilities, 
    		Map<String, List<String>> variables, 
    		Map<String, List<String>> constants)
    {

        this.type = type;
        this.path = path;
        this.capabilities = capabilities;
        this.setVariables(variables);
        this.setConstants(constants);

    }


	public Map<String, List<String>> getConstants() {
		return constants;
	}
	
	
	public void setConstants(Map<String, List<String>> constants) {
		this.constants = constants;
	}
	
	
	public Map<String, List<String>> getVariables() {
		return variables;
	}
	
	
	public void setVariables(Map<String, List<String>> variables) {
		this.variables = variables;
	}


	public String getType() {
		return type;
	}
	
	public String getPath() {
		return path;
	}

	
}
