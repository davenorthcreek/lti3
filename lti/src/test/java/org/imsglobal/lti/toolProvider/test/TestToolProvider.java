package org.imsglobal.lti.toolProvider.test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.imsglobal.lti.toolProvider.ToolProvider;
import org.imsglobal.lti.toolProvider.dataConnector.DataConnector;

public class TestToolProvider extends ToolProvider {

	public TestToolProvider(DataConnector dataConnector, HttpServletRequest request, HttpServletResponse response) {
		super(dataConnector, request, response);
		
	}
	
	@Override
	public boolean onLaunch() {
		return false;
	}

}
