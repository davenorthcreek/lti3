package imsglobal;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class LTIUtil {

	public static Map<String, List<String>> setParameter(Map<String, List<String>> parameters, String name, String value) {
		if (parameters.containsKey(name)) {
			List<String> itemList = parameters.get(name);
			if (itemList.contains(value)) {
				//no-op
			} else {
				itemList.add(value);
				parameters.put(name, itemList);
			}
		} else {
			List<String> itemList = new ArrayList<String>();
			itemList.add(value);
			parameters.put(name, itemList);
		}
		return parameters;
	}
}
