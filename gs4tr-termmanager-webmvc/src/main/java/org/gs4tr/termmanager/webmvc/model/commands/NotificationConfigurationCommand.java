package org.gs4tr.termmanager.webmvc.model.commands;

import org.gs4tr.termmanager.model.dto.NotificationProfile;

public class NotificationConfigurationCommand {

    private NotificationProfile[] _configuration;

    public NotificationProfile[] getConfiguration() {
	return _configuration;
    }

    public void setConfiguration(NotificationProfile[] configuration) {
	_configuration = configuration;
    }
}