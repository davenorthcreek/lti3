package imsglobal.toolProvider;

import java.util.List;

public interface LTISource {
	public ToolConsumer getConsumer();
	
	public List<User> getUserResultSourcedIDs(boolean flag, int scope);

	public String getId();
}
