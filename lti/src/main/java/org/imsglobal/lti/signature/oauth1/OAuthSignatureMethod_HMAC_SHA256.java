package org.imsglobal.lti.signature.oauth1;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class OAuthSignatureMethod_HMAC_SHA256 extends OAuthSignatureMethod {

	@Override
	public String get_name() {
		return "HMAC-SHA256";
	}

	@Override
	public String build_signature(OAuthRequest request, OAuthConsumer consumer, OAuthToken token) {
		String base_string = request.get_signature_base_string();
        request.setBaseString(base_string);
        
        String secret = OAuthUtil.urlencode_rfc3986(consumer.getSecret());
        String key = secret;
        String stringtoken = "";
        if (token != null) {
        	stringtoken  = OAuthUtil.urlencode_rfc3986(token.getSecret());
        	key += "&" + stringtoken;
        }

        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretSpec = new SecretKeySpec(key.getBytes(),"HmacSHA256");
            mac.init(secretSpec);
            byte[] digest = mac.doFinal(base_string.getBytes());
            return Base64.encodeBase64(digest).toString();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "";

	}

}
