package org.imsglobal.lti3.toolProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.imsglobal.lti3.settings.HasSettings;
import org.json.simple.JSONObject;

public abstract class AbstractSettingsHolder implements Hierarchical, HasSettings {

	private String level;
	private Map<String, Object> settings = new HashMap<String, Object>();
	private JSONObject asJson = new JSONObject();
	private AbstractSettingsHolder parent;
	private AbstractSettingsHolder child;

	@Override
	public String getValue(String name, String level, int mode) {
		Map<String, Object> localSettings = getSettings(mode, false);
		Object nextLevelDown = localSettings.get(level);
		try {
			HashMap<String, String> levelledMap = (HashMap<String, String>)nextLevelDown;
			return levelledMap.get(name);
		} catch (Exception e) {
			//shouldn't happen
		}
		return null;
	}

	@Override
	public Map<String, Object> getSettings(int mode, boolean condensed) {
		if (mode == HasSettings.MODE_CURRENT_LEVEL) {
			if (condensed) {
				return settings;
			} else {
				HashMap<String, Object> leveled = new HashMap<String, Object>();
				leveled.put(level, settings);
				return leveled;
			}
		} else if (mode == HasSettings.MODE_ALL_LEVELS) {
			Map<String, Object> allSettings = getChildSettings(condensed);
			if (condensed) {
				allSettings = emptyIfRepeatedKeys(allSettings, settings);
			} else {
				allSettings.put(level, settings);
			}
			allSettings = emptyIfRepeatedKeys(allSettings, getParentSettings(condensed));
			return allSettings;
		} else if (mode == HasSettings.MODE_DISTINCT_NAMES) {
			Map<String, Object> allSettings = getChildSettings(condensed);
			if (condensed) {
				preserveChildValuesPutAll(allSettings, settings);
			} else {
				allSettings.put(level, settings);
			}
			preserveChildValuesPutAll(allSettings, getParentSettings(condensed));
			if (!condensed) {
				//only lowest level values preserved
				allSettings = removeRepeatedValues(allSettings);
			}
			return allSettings;
		} else {
			//only 3 modes for now
			return null;
		}
	}

	private Map<String, Object> getChildSettings(boolean condensed) {
		if (child == null) {
			return new HashMap<String, Object>();
		}
		Map<String, Object> childSettings = getChild().getChildSettings(condensed);
		preserveChildValuesPutAll(childSettings, child.getSettings(HasSettings.MODE_CURRENT_LEVEL, condensed));
		return childSettings;
	}

	private Map<String, Object> getParentSettings(boolean condensed) {
		if (parent == null) {
			return new HashMap<String, Object>();
		}
		//start with grandparent settings (or an empty hashMap)
		Map<String, Object> parentSettings = parent.getParentSettings(condensed);
		//invert the order from all other calls since we want to preserve the lowest level values
		parentSettings = preserveChildValuesPutAll(parent.getSettings(HasSettings.MODE_CURRENT_LEVEL, condensed), parentSettings);
		return parentSettings;
	}

	void setLevel(String val) {
		this.level = val;
	}
	
	@Override
	public String getLevel() {
		return level;
	}

	@Override
	public void setSetting(String name, String value) {
		settings.put(name, value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getSettingsAsJSON(int mode, boolean condensed) {
		Map<String, Object> localSettings = getSettings(mode, condensed);
		JSONObject localAsJson = new JSONObject();
		
		for(String key: localSettings.keySet()) {
			localAsJson.put(key, localSettings.get(key));
		}
		return localAsJson.toJSONString();
	}
	
	void setParent(AbstractSettingsHolder parent) {
		this.parent = parent;
	}
	
	@Override
	public AbstractSettingsHolder getParent() {
		return parent;
	}
	
	void setChild(AbstractSettingsHolder child) {
		this.child = child;
	}
	
	@Override
	public AbstractSettingsHolder getChild() {
		return child;
	}
	
	@Override
	public Collection<Hierarchical> getParents() {
		Collection<Hierarchical> parents = new ArrayList<Hierarchical>();
		parents.add(parent);
		return parents;
	}
	
	@Override
	public Collection<Hierarchical> getChildren() {
		Collection<Hierarchical> children = new ArrayList<Hierarchical>();
		children.add(child);
		return children;
	}
	
	private Map<String, Object> preserveChildValuesPutAll(Map<String, Object> child, Map<String, Object> parent) {
		
		for (String key : parent.keySet()) {
			if (child.containsKey(key)) {
				//drop parent value
			} else {
				child.put(key, parent.get(key));
			}
		}
		return child;
	}	
	
	private Map<String, Object> emptyIfRepeatedKeys(Map<String, Object> child, Map<String, Object> parent) {
		Map<String, Object> empty = new HashMap<String, Object>();
		
		for (String key : parent.keySet()) {
			if (child.containsKey(key)) {
				return empty;
				//matches PHP api of returning false on this MODE_ALL_LEVELS and TRUE with repeated settings keys
			} else {
				child.put(key, parent.get(key));
			}
		}
		return child;
	}
	
	private Map<String, Object> removeRepeatedValues() {
		if (child == null) {
			//we are at the lowest level - recursion base case
			return doTheRecursionHere());
		}
		return child.removeRepeatedValues();
		
	}
	
	private Map<String, Object> doTheRecursionHere() throws ClassCastException {
		//preserve these values
		Map<String, Object> allSettings = this.getSettings(MODE_ALL_LEVELS, false);
		Map<String, String> preserve = (Map<String, String>)allSettings.get(level);
		for (String theLevel : allSettings.keySet()) {
			if (theLevel == level) {
				continue;
			}
			Map<String, String> toFilter = (Map<String, String>)allSettings.get(theLevel);
			for (String baseKey : preserve.keySet()) {
				if (toFilter.containsKey(baseKey)) {
					toFilter.remove(baseKey);
				}
			}
		}
		
	}

}
