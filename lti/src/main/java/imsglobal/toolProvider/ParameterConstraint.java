package imsglobal.toolProvider;

import java.util.Set;

public class ParameterConstraint {

/**
 * <code>true</code> if the parameter is required
 */
    private boolean required;
/**
 * Maximum permitted length of parameter value, null if any length is acceptable
 */
    private Integer maxLength;
    
    private Set<String> messageTypes;

/**
 * Constructs a ParameterConstraint using the specified values for the required
 * and maximum length properties.
 */
    public ParameterConstraint(boolean required, Integer maxLength) {
      this.required = required;
      this.maxLength = maxLength;
    }

/**
 * Returns <code>true</code> if the parameter is required in each launch request.
 *
 * @return <code>true</code> if the parameter is required
 */
    public boolean isRequired() {
      return this.required;
    }
    
    public void setRequired(boolean req) {
    	this.required = req;
    }

/**
 * Returns the maximum length permitted for the parameter, may be null.
 *
 * @return maximum length
 */
    public Integer getMaxLength() {
      return this.maxLength;
    }
    
    public void setMaxLength(int max) {
    	this.maxLength = max;
    }

    public Set<String> getMessageTypes() {
		return messageTypes;
	}

	public void setMessageTypes(Set<String> messageTypes) {
		this.messageTypes = messageTypes;
	}

}
