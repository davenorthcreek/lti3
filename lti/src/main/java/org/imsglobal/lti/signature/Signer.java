package org.imsglobal.lti.signature;

import org.imsglobal.lti.LTIMessage;

public interface Signer {
	
	public void sign(LTIMessage message);
	
	public boolean isSigned(LTIMessage message);
	
	public String getType();

}
