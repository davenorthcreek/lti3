package org.imsglobal.lti.signature.oauth1;

public abstract class OAuthSignatureMethod {
	/**
	 * Class to represent an %OAuth Signature Method
	 *
	 * @copyright  Andy Smith
	 * @version 2008-08-04
	 * @license https://opensource.org/licenses/MIT The MIT License
	 */
	/**
	 * A class for implementing a Signature Method
	 * See section 9 ("Signing Requests") in the spec
	 */
	
    /**
     * Needs to return the name of the Signature Method (ie HMAC-SHA1)
     * @return string
     */
    public abstract String get_name();

    /**
     * Build up the signature
     * NOTE: The output of this function MUST NOT be urlencoded.
     * the encoding is handled in OAuthRequest when the final
     * request is serialized
     * @param OAuthRequest $request
     * @param OAuthConsumer $consumer
     * @param OAuthToken $token
     * @return string
     */
    public abstract String build_signature(OAuthRequest request, OAuthConsumer consumer, OAuthToken token);

    /**
     * Verifies that a given signature is correct
     * @param OAuthRequest $request
     * @param OAuthConsumer $consumer
     * @param OAuthToken $token
     * @param string $signature
     * @return boolean
     */
    public boolean check_signature(OAuthRequest request, OAuthConsumer consumer, OAuthToken token, 
    		String signature) {

        String built = this.build_signature(request, consumer, token);

        // Check for zero length, although unlikely here
        if (built.length() == 0 || signature.length() == 0) {
            return false;
        }

        if (built.length() != signature.length()) {
            return false;
        }

        // Avoid a timing leak with a (hopefully) time insensitive compare
        int result = 0;
        for (int i = 0; i < signature.length(); i++) {
            result |= (int)(built.charAt(i)) ^ (int)(signature.charAt(i));
        }

        return (result == 0);

    }
}
