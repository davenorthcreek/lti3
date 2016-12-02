package org.imsglobal.lti3.settings;

import java.util.Map;

public interface HasSettings {
	/**
	 * Settings at current level mode.
	 */
    int MODE_CURRENT_LEVEL = 1;
	/**
	 * Settings at all levels mode.
	 */
    int MODE_ALL_LEVELS = 2;
	/**
	 * Settings with distinct names at all levels mode.
	 */
    int MODE_DISTINCT_NAMES = 3;
    
	public String getValue(String name, String level, int mode);
	public Map<String, Object> getSettings(int mode, boolean condensed);
	public String getSettingsAsJSON(int mode, boolean condensed);
	public String getLevel();
	public void setSetting(String name, String value);
	
}
