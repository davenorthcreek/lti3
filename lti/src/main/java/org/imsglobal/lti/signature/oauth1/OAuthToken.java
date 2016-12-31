package org.imsglobal.lti.signature.oauth1;

public class OAuthToken {
	/**
	 * Class to represent an %OAuth Token
	 *
	 * @copyright  Andy Smith
	 * @version 2008-08-04
	 * @license https://opensource.org/licenses/MIT The MIT License
	 */


    // access tokens and request tokens
    public String key;
    public String secret;

    /**
     * key = the token
     * secret = the token secret
     */
    public OAuthToken(String key, String secret) {
        this.key = key;
        this.secret = secret;
    }

    /**
     * generates the basic string serialization of a token that a server
     * would respond to request_token and access_token calls with
     */
    public String  toString() {
        return "oauth_token=" +
               OAuthUtil.urlencode_rfc3986(key) +
               "&oauth_token_secret=" +
               OAuthUtil.urlencode_rfc3986(secret);
    }

	public String getKey() {
		return key;
	}

	public String getSecret() {
		return secret;
	}


}
