package imsglobal.signature;

import imsglobal.LTIMessage;

public interface Signer {
	
	public void sign(LTIMessage message);
	
	public boolean isSigned(LTIMessage message);
	
	public String getType();

}
