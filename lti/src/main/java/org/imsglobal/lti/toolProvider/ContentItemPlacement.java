package org.imsglobal.lti.toolProvider;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONValue;

public class ContentItemPlacement {
	/**
	 * Class to represent a content-item placement object
	 *
	 * @author  Stephen P Vickers <svickers@imsglobal.org>
	 * @copyright  IMS Global Learning Consortium Inc
	 * @date  2016
	 * @version 3.0.2
	 * @license http://www.apache.org/licenses/LICENSE-2.0 Apache License, Version 2.0
	 */
	
	private int displayWidth=0;
	private int displayHeight=0;
	private String documentTarget;
	private String windowTarget;


	/**
	 * Class constructor.
	 *
	 * @param int displayWidth       Width of item location
	 * @param int displayHeight      Height of item location
	 * @param string documentTarget  Location to open content in
	 * @param string windowTarget    Name of window target
	 */
    ContentItemPlacement(int displayWidth, int displayHeight, String documentTarget, String windowTarget)
    {
    	setDisplayWidth(displayWidth);
    	setDisplayHeight(displayHeight);
    	setDocumentTarget(documentTarget);
    	setWindowTarget(windowTarget);
    }


	public int getDisplayWidth() {
		return displayWidth;
	}


	public void setDisplayWidth(int displayWidth) {
		this.displayWidth = displayWidth;
	}


	public int getDisplayHeight() {
		return displayHeight;
	}


	public void setDisplayHeight(int displayHeight) {
		this.displayHeight = displayHeight;
	}


	public String getDocumentTarget() {
		return documentTarget;
	}


	public void setDocumentTarget(String documentTarget) {
		this.documentTarget = documentTarget;
	}


	public String getWindowTarget() {
		return windowTarget;
	}


	public void setWindowTarget(String windowTarget) {
		this.windowTarget = windowTarget;
	}


	public String toJSONString() {
		Map<String, String> obj = new HashMap<String, String>();
		obj.put("displayWidth", "" + displayWidth);
		obj.put("displayHeight", "" + displayHeight);
		if (documentTarget != null) obj.put("documentTarget", documentTarget);
		if (windowTarget != null) obj.put("windowTarget", windowTarget);
		return JSONValue.toJSONString(obj);
	}
	    
	    
}
