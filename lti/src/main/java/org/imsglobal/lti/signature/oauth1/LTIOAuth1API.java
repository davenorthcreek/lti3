package org.imsglobal.lti.signature.oauth1;

import org.imsglobal.lti.LTIMessage;
import org.imsglobal.lti.signature.Signer;

import com.github.scribejava.core.builder.api.DefaultApi10a;
import com.github.scribejava.core.model.OAuth1RequestToken;

public class LTIOAuth1API extends DefaultApi10a implements Signer {

	public void sign(LTIMessage message) {
		// TODO Auto-generated method stub

	}

	public boolean isSigned(LTIMessage message) {
		// TODO Auto-generated method stub
		return false;
	}

	public String getType() {
		return "LTI OAuth1.0a";
	}
	
    private String AUTHORIZATION_URL = ""; //will come from .properties file

    protected LTIOAuth1API() {
    }

    private static class InstanceHolder {
        private static final LTIOAuth1API INSTANCE = new LTIOAuth1API();
    }

    public static LTIOAuth1API instance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public String getAccessTokenEndpoint() {
    	//from properties file
        return "";
    }

    @Override
    public String getRequestTokenEndpoint() {
    	//from properties file if needed
        return "";
    }

    @Override
    public String getAuthorizationUrl(OAuth1RequestToken requestToken) {
        return String.format(getAuthorizationURLFromProperties(), requestToken.getToken());
    }

	private String getAuthorizationURLFromProperties() {
		return "";
	}

}
