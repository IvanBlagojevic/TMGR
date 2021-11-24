package org.gs4tr.termmanager.service.model.command.dto;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.gs4tr.termmanager.service.model.command.GetOrganizationUsersCommand;

public class DtoGetOrganizationUsersCommand implements DtoTaskHandlerCommand<GetOrganizationUsersCommand> {

    private String[] _projectLanguages;

    private boolean _showGenericUsers = false;

    @Override
    public GetOrganizationUsersCommand convertToInternalTaskHandlerCommand() {
	GetOrganizationUsersCommand command = new GetOrganizationUsersCommand();
	command.setShowGenericUsers(isShowGenericUsers());
	if (ArrayUtils.isNotEmpty(getProjectLanguages())) {
	    Set<String> languages = new HashSet<>();
	    languages.addAll(Arrays.asList(getProjectLanguages()));
	    command.setProjectLanguages(languages);
	}
	return command;
    }

    public String[] getProjectLanguages() {
	return _projectLanguages;
    }

    public boolean isShowGenericUsers() {
	return _showGenericUsers;
    }

    public void setProjectLanguages(String[] projectLanguages) {
	_projectLanguages = projectLanguages;
    }

    public void setShowGenericUsers(boolean showGenericUsers) {
	_showGenericUsers = showGenericUsers;
    }

}
