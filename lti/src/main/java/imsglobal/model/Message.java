package imsglobal.model;

import java.util.List;

/**

 * Class to represent a resource handler message object
 *
 * @author  Stephen P Vickers <svickers@imsglobal.org>
 * @copyright  IMS Global Learning Consortium Inc
 * @date  2016
 * @version 3.0.0
 * @license http://www.apache.org/licenses/LICENSE-2.0 Apache License, Version 2.0
 * translated to Java by David Block (dave@northcreek.ca)
 */

public class Message {

/**
 * LTI message type.
 *
 * @var string type
 */
    public String type;
/**
 * Path to send message request to (used in conjunction with a base URL for the Tool Provider).
 *
 * @var string path
 */
    public String path;
/**
 * Capabilities required by message.
 *
 * @var array capabilities
 */
    public List capabilities;
/**
 * Variable parameters to accompany message request.
 *
 * @var array variables
 */
    public List variables;
/**
 * Fixed parameters to accompany message request.
 *
 * @var array constants
 */
    public List constants;


/**
 * Class constructor.
 *
 * @param string type          LTI message type
 * @param string path          Path to send message request to
 * @param array  capabilities  Array of capabilities required by message
 * @param array  variables     Array of variable parameters to accompany message request
 * @param array  constants     Array of fixed parameters to accompany message request
 */
    public Message(String type, String path, List capabilities, List variables, List constants) {

        this.type = type;
        this.path = path;
        this.capabilities = capabilities;
        this.variables = variables;
        this.constants = constants;

    }

}
