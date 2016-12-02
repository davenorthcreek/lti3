package org.imsglobal.lti3.toolProvider;

import org.imsglobal.lti3.settings.HasSettings;

public class ResourceLink extends AbstractSettingsHolder implements HasSettings {
	
	/*
	 * Bare constructor
	 */
	public ResourceLink() {
		setLevel("link");
	}
	
	/*
	 * provide a context
	 */
	public ResourceLink(Context context) {
		setLevel("link");
		this.setParent(context);
		context.setChild(this);
	}
	
	


}
