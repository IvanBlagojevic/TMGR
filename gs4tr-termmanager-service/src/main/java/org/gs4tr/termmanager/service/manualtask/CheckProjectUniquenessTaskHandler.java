package org.gs4tr.termmanager.service.manualtask;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.model.command.PropertyCheckCommand;
import org.springframework.beans.factory.annotation.Autowired;

public class CheckProjectUniquenessTaskHandler extends AbstractPropertyCheckerTaskHandler {

    private static Log _logger = LogFactory.getLog(CheckProjectUniquenessTaskHandler.class);

    @Autowired
    private ProjectService _projectService;

    public ProjectService getProjectService() {
	return _projectService;
    }

    @Override
    protected boolean checkExistence(PropertyCheckCommand propertyCheckCommand) {
	boolean exists = true;

	String projectName = propertyCheckCommand.getProjectName();
	if (StringUtils.isBlank(projectName)) {
	    throw new UserException(MessageResolver.getMessage("CheckUserUniquenessTaskHandler.0"), //$NON-NLS-1$
		    MessageResolver.getMessage("CheckProjectUniquenessTaskHandler.0")); //$NON-NLS-1$
	}

	TmProject project = null;
	try {
	    project = getProjectService().findProjectByName(projectName);
	} catch (RuntimeException e) {
	    if (_logger.isDebugEnabled()) {
		_logger.debug(e.getMessage(), e);
	    }

	    project = new TmProject();
	}

	if (project == null) {
	    exists = false;
	}

	return exists;
    }

}
