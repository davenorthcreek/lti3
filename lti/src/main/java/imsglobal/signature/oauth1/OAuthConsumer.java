package imsglobal.signature.oauth1;

import java.net.URL;

public class OAuthConsumer {

	private String key;
	private String secret;
	private URL callbackUrl;
	
	public OAuthConsumer(String key, String secret) {
		this.key = key;
		this.secret = secret;
	}
	
	public OAuthConsumer(String key, String secret, URL url) {
		this.key = key;
		this.secret = secret;
		this.callbackUrl = url;
	}
	
	public String toString() {
		return "OAuthConsumer[key=" + key + ",secret=" + secret + "]";
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public URL getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(URL callbackUrl) {
		this.callbackUrl = callbackUrl;
	}
	
	
}
