package imsglobal;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class LTIMessage {
	
	private String url;
	private String method;
	private String body;
	private String header;
	private Map<String, List<String>> params;
	private String response;
	private JSONObject responseJson;
	private boolean ok;
	private JSONParser parser;
	private int status;

	public LTIMessage(String url, String method, String data, String header, Map<String, List<String>> parameters) {
		setUrl(url);
		setMethod(method);
		setBody(data);
		setHeader(header);
		setParams(parameters);
		JSONParser parser = new JSONParser();
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public void setResponse(String response) {
		this.response = response;
		try {
			setResponseJson(jsonDecode(response));
		} catch(ParseException pe) {
			//no-op, responseJson stays null
		}
	}

	public void setResponseJson(JSONObject responseJson) {
		this.responseJson = responseJson;
	}

	public boolean send() {
		//send message
		//store response in setResponse (so json is parsed at the same time)
		return false;
		
	}

	public String getResponse() {
		return response;
	}

	public JSONObject getResponseJson() {
		if (responseJson == null && response != null) {
			try {
				setResponseJson(jsonDecode(response));
			} catch(ParseException pe) {
				//no-op, responseJson stays null
			}
		}
		return responseJson;
	}
	
	public boolean isOk() {
		return ok;
	}

	public void setOk(boolean ok) {
		this.ok = ok;
	}
	
	public JSONObject jsonDecode(String raw) throws ParseException {
		Object obj = parser.parse(raw);
		JSONObject jsonObject = (JSONObject) obj;
		return jsonObject;
	}

	public Map<String, List<String>> getParams() {
		return params;
	}

	public void setParams(Map<String, List<String>> params) {
		this.params = params;
	}

	public int getStatus() {
		return status;
	}



}
