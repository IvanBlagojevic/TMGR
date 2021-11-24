package org.gs4tr.termmanager.service.manualtask;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.gs4tr.foundation.modules.entities.model.types.ItemStatusType;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.model.command.ProjectCommand;

public abstract class AbstractProjectManualTaskHandler extends AbstractManualTaskHandler {

    private static final String DEFAULT_TERM_STATUS = "defaultTermStatus";
    private static final String SHARE_PENDING_TERMS = "sharePendingTerms";

    abstract protected Boolean getSharePendingTerms(ProjectCommand command);

    protected void updateProjectIfDefaultTermStatusOrSharePendingTermsIsChanged(ItemStatusType defaultTermStatus,
	    TmProject project, ProjectService projectService, ProjectCommand command) {

	Map<String, Object> updateCommand = new HashMap<>();

	if (Objects.nonNull(defaultTermStatus) && !defaultTermStatus.equals(project.getDefaultTermStatus())) {
	    updateCommand.put(DEFAULT_TERM_STATUS, defaultTermStatus);
	}

	Boolean isSharePendingTerms = getSharePendingTerms(command);

	if (Objects.nonNull(isSharePendingTerms) && !isSharePendingTerms.equals(project.getSharePendingTerms())) {
	    updateCommand.put(SHARE_PENDING_TERMS, isSharePendingTerms);
	}

	if (!updateCommand.isEmpty()) {
	    projectService.updateProjectProperties(project.getProjectId(), updateCommand);
	}
    }
}
