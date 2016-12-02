package org.imsglobal.lti3.test;

import static org.junit.Assert.*;

import java.util.Map;

import org.imsglobal.lti3.settings.HasSettings;
import org.imsglobal.lti3.toolProvider.IMSSystem;
import org.imsglobal.lti3.toolProvider.Context;
import org.imsglobal.lti3.toolProvider.Hierarchical;
import org.imsglobal.lti3.toolProvider.ResourceLink;
import org.junit.Before;
import org.junit.Test;

public class TestSettings {
	
	private IMSSystem sys;
	private Context con;
	private ResourceLink lin;
	
	@Before
	public void setUp() {
		sys = new IMSSystem();
		sys.setSetting("systemSetting", "SystemValue");
		sys.setSetting("commonSetting", "system");
		sys.setSetting("topTwoSetting", "system");
		con = new Context(sys);
		con.setSetting("contextSetting", "ContextValue");
		con.setSetting("commonSetting", "context");
		con.setSetting("topTwoSetting", "context");
		lin = new ResourceLink(con);
		lin.setSetting("testSetting", "testSettingValue");
		lin.setSetting("testSetting2", "testASecondSetting");
		lin.setSetting("commonSetting", "link");
		
	}
	
	@Test
	public void testHasSettings() {
		assertTrue("ResourceLink HasSettings", lin instanceof HasSettings);
		assertEquals("ResourceLink is level link", "link", lin.getLevel());
	}

	@Test
	public void testSetSettings() {
		assertNotNull("ResourceLink Test Settings", lin.getSettings(HasSettings.MODE_CURRENT_LEVEL, true));
		assertTrue("Settings Class", lin.getSettings(HasSettings.MODE_CURRENT_LEVEL, true) instanceof Map);
		Map<String, Object> settings = lin.getSettings(HasSettings.MODE_CURRENT_LEVEL, true);
		assertEquals("Should be testSettingValue", "testSettingValue", settings.get("testSetting"));
		assertTrue("JSON Correct",  
				lin.getSettingsAsJSON(HasSettings.MODE_CURRENT_LEVEL, true)
					.contains("\"testSetting2\":\"testASecondSetting\",\"testSetting\":\"testSettingValue\""));
		assertTrue("JSON Correct",  
				lin.getSettingsAsJSON(HasSettings.MODE_CURRENT_LEVEL, true)
					.contains("\"commonSetting\":\"link\""));

		
		
		assertEquals("Get Settings", 
				"testSettingValue", 
				lin.getValue("testSetting", "link", HasSettings.MODE_CURRENT_LEVEL));
	}
	
	@Test
	public void testGetSettingArguments() {
		Map<String, Object> settings = lin.getSettings(HasSettings.MODE_CURRENT_LEVEL, false);
		assertTrue("Settings comes indexed by level", settings.containsKey("link"));
		String jsonCurrentFalse = lin.getSettingsAsJSON(HasSettings.MODE_CURRENT_LEVEL, false);
		assertTrue("JSON indexed by level",
				jsonCurrentFalse.contains("{\"link\":{"));
		
		assertTrue("JSON contains common setting",
				jsonCurrentFalse.contains("\"commonSetting\":\"link\""));
		
		
	}
	
	@Test
	public void testGetParent() {
		Hierarchical parent = lin.getParent();
		assertEquals("parent is context", con, parent);
	}
	
	@Test
	public void testGetChild() {
		Hierarchical child = sys.getChild();
		assertEquals("child of system is context", con, child);
	}
	
	@Test
	public void testGetAllLevelsUncondensed() {
		

		String jsonFromLink = lin.getSettingsAsJSON(HasSettings.MODE_ALL_LEVELS, false);
		System.out.println(jsonFromLink);
		assertTrue("JSON coming from Link has level from context", 
				jsonFromLink.contains("\"context\":{"));
		assertTrue("JSON coming from Link has content from context", 
				jsonFromLink.contains("\"contextSetting\":\"ContextValue\""));
		assertTrue("JSON coming from Link has content from link", 
				jsonFromLink.contains("\"testSetting2\":\"testASecondSetting\""));
		assertTrue("JSON coming from Link has content from system", 
				jsonFromLink.contains("\"commonSetting\":\"system\""));
		String jsonFromSystem = sys.getSettingsAsJSON(HasSettings.MODE_ALL_LEVELS, false);
		System.out.println(jsonFromSystem);
		assertTrue("JSON coming from Link has level from context", 
				jsonFromSystem.contains("\"context\":{"));
		assertTrue("JSON coming from Link has content from context", 
				jsonFromSystem.contains("\"contextSetting\":\"ContextValue\""));
		assertTrue("JSON coming from Link has content from link", 
				jsonFromSystem.contains("\"testSetting2\":\"testASecondSetting\""));
		assertTrue("JSON coming from Link has content from system", 
				jsonFromSystem.contains("\"commonSetting\":\"system\""));
		
		Map<String, Object> settings = lin.getSettings(HasSettings.MODE_ALL_LEVELS, false);
		assertTrue("Settings comes indexed by level", settings.containsKey("link"));
		assertTrue("Settings contains System", settings.containsKey("system"));
		assertEquals("link can get system value", 
				"SystemValue", 
				lin.getValue("systemSetting", "system", HasSettings.MODE_ALL_LEVELS));
		assertEquals("link can get Context value",
				"ContextValue",
				lin.getValue("contextSetting", "context", HasSettings.MODE_ALL_LEVELS));
	}
	
	@Test
	public void testGetAllLevelsCondensed() {
		Map<String, Object> settings = lin.getSettings(HasSettings.MODE_ALL_LEVELS, true);
		//this will return an empty HashMap if there is a repeated settings key (commonSetting above)
		assertTrue("HashMap is empty", settings.isEmpty());
		String jsonFromSystem = sys.getSettingsAsJSON(HasSettings.MODE_ALL_LEVELS, true);
		System.out.println("Should Be Empty: " + jsonFromSystem);
		assertEquals("Empty JSON", "{}", jsonFromSystem);
	}
	
	@Test
	public void testGetDistinctCondensed() {
		Map<String, Object> settings = lin.getSettings(HasSettings.MODE_DISTINCT_NAMES, true);
		assertTrue("Settings comes indexed by settings name", settings.containsKey("testSetting2"));
		assertTrue("Settings contains System", settings.containsKey("systemSetting"));
		String jsonString = lin.getSettingsAsJSON(HasSettings.MODE_DISTINCT_NAMES, true);
		System.out.println(jsonString);
		assertTrue("JSON coming from Link has content from context", 
				jsonString.contains("\"contextSetting\":\"ContextValue\""));
		assertTrue("JSON coming from Link has content from link", 
				jsonString.contains("\"testSetting2\":\"testASecondSetting\",\"testSetting\":\"testSettingValue\""));
		assertTrue("JSON coming from Link has content from system", 
				jsonString.contains("\"systemSetting\":\"SystemValue\""));
		assertTrue("Common Setting is only level - from Link",
				jsonString.contains("\"commonSetting\":\"link\""));
		assertTrue("Common Setting is only level - from Context, should be lowest",
				con.getSettingsAsJSON(HasSettings.MODE_DISTINCT_NAMES, true).contains("\"commonSetting\":\"link\""));
		String jsonFromSystem = sys.getSettingsAsJSON(HasSettings.MODE_DISTINCT_NAMES, true);
		System.out.println(jsonFromSystem);
		assertTrue("JSON coming from System has content from context", 
				jsonFromSystem.contains("\"contextSetting\":\"ContextValue\""));
		assertTrue("JSON coming from System has content from link", 
				jsonFromSystem.contains("\"testSetting2\":\"testASecondSetting\",\"testSetting\":\"testSettingValue\""));
		assertTrue("JSON coming from System has content from system", 
				jsonFromSystem.contains("\"systemSetting\":\"SystemValue\""));
		assertTrue("Common Setting is only level - from System, should be lowest",
				jsonFromSystem.contains("\"commonSetting\":\"link\""));
	}
	
	@Test
	public void testDistinctUncondensed() {
		

		String jsonFromLink = lin.getSettingsAsJSON(HasSettings.MODE_DISTINCT_NAMES, false);
		System.out.println("Distinct, uncondensed: " + jsonFromLink);
		assertTrue("JSON coming from Link has level from context", 
				jsonFromLink.contains("\"context\":{"));
		assertTrue("JSON coming from Link has content from context", 
				jsonFromLink.contains("\"contextSetting\":\"ContextValue\""));
		assertTrue("JSON coming from Link has content from link", 
				jsonFromLink.contains("\"testSetting2\":\"testASecondSetting\""));
		assertTrue("JSON coming from Link should have common setting from link", 
				jsonFromLink.contains("\"commonSetting\":\"link\""));
		assertFalse("JSON coming from Link should only have common setting from link", 
				jsonFromLink.contains("\"commonSetting\":\"system\""));
		String jsonFromSystem = sys.getSettingsAsJSON(HasSettings.MODE_DISTINCT_NAMES, false);
		System.out.println(jsonFromSystem);
		assertTrue("JSON coming from Link has level from context", 
				jsonFromSystem.contains("\"context\":{"));
		assertTrue("JSON coming from Link has content from context", 
				jsonFromSystem.contains("\"contextSetting\":\"ContextValue\""));
		assertTrue("JSON coming from Link has content from link", 
				jsonFromSystem.contains("\"testSetting2\":\"testASecondSetting\""));
		assertTrue("JSON coming from Link has content from system", 
				jsonFromSystem.contains("\"commonSetting\":\"system\""));
		
		Map<String, Object> settings = lin.getSettings(HasSettings.MODE_DISTINCT_NAMES, false);
		assertTrue("Settings comes indexed by level", settings.containsKey("link"));
		assertTrue("Settings contains System", settings.containsKey("system"));
		assertEquals("link can get system value", 
				"SystemValue", 
				lin.getValue("systemSetting", "system", HasSettings.MODE_DISTINCT_NAMES));
		assertEquals("link can get Context value",
				"ContextValue",
				lin.getValue("contextSetting", "context", HasSettings.MODE_DISTINCT_NAMES));
	}

	
	
}
