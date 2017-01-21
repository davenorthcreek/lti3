package org.imsglobal.lti.toolProvider.test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.imsglobal.lti.toolProvider.ToolProvider;
import org.imsglobal.lti.toolProvider.dataConnector.DataConnector;

public class TestToolProvider extends ToolProvider {

	public TestToolProvider(DataConnector dataConnector, HttpServletRequest request, HttpServletResponse response) {
		super(dataConnector, request, response);
		this.setDebugMode(true);
	}
	
	@Override
	public boolean onLaunch() {
		System.out.println("OnLaunch");
		System.out.println("TODO: load user, resource_link, context from environment somehow");
		return true;
	}
	
	@Override
	public boolean onError() {
		System.out.println("At onError");
		return super.onError();
	}

}
