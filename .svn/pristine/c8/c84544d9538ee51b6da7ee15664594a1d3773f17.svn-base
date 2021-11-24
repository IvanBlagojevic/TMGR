package org.gs4tr.termmanager.service;

import java.util.List;

import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.service.model.command.AssignProjectUserLanguageCommand;
import org.gs4tr.termmanager.service.model.command.AssignUserLanguageCommand;

public interface CacheGatewaySessionUpdaterService {

    void removeOnDisableProject(TmProject project);

    void removeOnDisableUser(TmUserProfile user);

    void removeOnEditProjectLanguage(Long projectId, List<String> languages);

    void removeOnEditProjectUser(List<Long> userProfileIds, Long projectId,
	    AssignProjectUserLanguageCommand assignProjectUserLanguageCommand);

    void removeOnEditUserLanguage(Long userProfileId, AssignUserLanguageCommand assignCommand);

}
