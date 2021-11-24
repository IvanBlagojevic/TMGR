package org.gs4tr.termmanager.service.manualtask;

import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.service.model.command.PropertyCheckCommand;
import org.springframework.beans.factory.annotation.Autowired;

public class CheckUserUniquenessTaskHandler extends AbstractPropertyCheckerTaskHandler {

    @Autowired
    private UserProfileService _userProfileService;

    public UserProfileService getUserProfileService() {
	return _userProfileService;
    }

    @Override
    protected boolean checkExistence(PropertyCheckCommand propertyCheckCommand) {
	String username = propertyCheckCommand.getUsername();
	if (StringUtils.isBlank(username)) {
	    throw new UserException(MessageResolver.getMessage("CheckUserUniquenessTaskHandler.0"), //$NON-NLS-1$
		    MessageResolver.getMessage("CheckUserUniquenessTaskHandler.1")); //$NON-NLS-1$
	}

	return getUserProfileService().exists(username);
    }

}
