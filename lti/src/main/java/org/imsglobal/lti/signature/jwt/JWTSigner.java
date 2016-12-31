package org.imsglobal.lti.signature.jwt;

import org.imsglobal.lti.LTIMessage;
import org.imsglobal.lti.signature.Signer;

public class JWTSigner implements Signer {

	public String getType() {
		return "JWT";
	}
	
	public void sign(LTIMessage message) {
		return;
	}
	
	public boolean isSigned(LTIMessage message) {
		return false;
	}

}
