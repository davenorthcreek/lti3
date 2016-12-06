package imsglobal.signature.jwt;

import imsglobal.LTIMessage;
import imsglobal.signature.Signer;

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
