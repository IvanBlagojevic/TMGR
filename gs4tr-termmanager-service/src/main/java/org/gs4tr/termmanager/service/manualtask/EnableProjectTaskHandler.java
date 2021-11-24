package org.gs4tr.termmanager.service.manualtask;

import java.util.List;

import org.gs4tr.foundation.modules.entities.model.ProjectInfo;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.webmvc.filters.model.RefreshUserContext;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.service.CacheGatewaySessionUpdaterService;
import org.gs4tr.termmanager.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;

public class EnableProjectTaskHandler extends AbstractManualTaskHandler {

    @Autowired
    private CacheGatewaySessionUpdaterService _cacheGatewaySessionUpdaterService;

    @Autowired
    ProjectService _projectService;

    public ProjectService getProjectService() {
	return _projectService;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.SINGLE_SELECT;
    }

    @RefreshUserContext
    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {

	Long projectId = parentIds[0];
	TmProject project = getProjectService().load(projectId);
	validateLastProject(project);
	getProjectService().enableProject(projectId);

	if (project.getProjectInfo().isEnabled()) {
	    getCacheGatewaySessionUpdaterService().removeOnDisableProject(project);
	}

	return new TaskResponse(null);
    }

    public void setProjectService(ProjectService projectService) {
	_projectService = projectService;
    }

    private CacheGatewaySessionUpdaterService getCacheGatewaySessionUpdaterService() {
	return _cacheGatewaySessionUpdaterService;
    }

    /**
     * Check if this project is last enabled project, if it is don't disable it and
     * display message to the user
     * 
     * @param projectInfo
     *            selected project info
     */
    private void validateLastProject(TmProject project) {
	ProjectInfo projectInfo = project.getProjectInfo();
	List<Long> allProjectIds = getProjectService().findAllEnabledProjectIds();

	if (allProjectIds != null && allProjectIds.size() == 1 && projectInfo.isEnabled()) {
	    throw new UserException(MessageResolver.getMessage("EnableProjectTaskHandler.0"), //$NON-NLS-1$
		    MessageResolver.getMessage("EnableProjectTaskHandler.2")); //$NON-NLS-1$
	}
    }
}
