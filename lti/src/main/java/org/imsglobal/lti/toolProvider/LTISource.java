package org.imsglobal.lti.toolProvider;

import java.util.Map;

public interface LTISource {
	public ToolConsumer getConsumer();
	
	public Map<String, User> getUserResultSourcedIDs(boolean flag, int scope);

	public String getId();
}
