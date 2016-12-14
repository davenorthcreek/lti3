package imsglobal.signature.oauth1;

import java.net.URL;

import org.joda.time.DateTime;

public interface OAuthDataStore {
	
	/**
	 * Class to represent an %OAuth Data Store
	 *
	 * @copyright  Andy Smith
	 * @version 2008-08-04
	 * @license https://opensource.org/licenses/MIT The MIT License
	 */
    public OAuthConsumer lookup_consumer(String consumer_key);

    public OAuthToken lookup_token(OAuthConsumer consumer, String token_type, OAuthToken token);

    public Object lookup_nonce(OAuthConsumer consumer, OAuthToken token, String nonce, DateTime timestamp);

    public OAuthToken new_request_token(OAuthConsumer consumer);
    public OAuthToken new_request_token(OAuthConsumer consumer, URL callback);

    public OAuthToken new_access_token(OAuthToken token, OAuthConsumer consumer);
    public OAuthToken new_access_token(OAuthToken token, OAuthConsumer consumer, Object verifier); 
        // return a new access token attached to this consumer
        // for the user associated with this token if the request token
        // is authorized
        // should also invalidate the request token


}
