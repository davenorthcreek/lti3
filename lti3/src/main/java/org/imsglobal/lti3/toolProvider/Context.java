package org.imsglobal.lti3.toolProvider;

import org.imsglobal.lti3.settings.HasSettings;

public class Context extends AbstractSettingsHolder implements HasSettings {

	public Context(IMSSystem system) {
		this.setLevel("context");
		this.setParent(system);
		system.setChild(this);
	}

}
