package org.gs4tr.termmanager.io.edd.handler;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.Validate;
import org.gs4tr.termmanager.io.edd.api.Handler;
import org.gs4tr.termmanager.io.edd.event.UpdateCountEvent;
import org.gs4tr.termmanager.io.exception.EventException;
import org.gs4tr.termmanager.model.ProjectLanguageDetailInfoIO;
import org.gs4tr.termmanager.model.ProjectUserDetailIO;
import org.gs4tr.termmanager.model.dto.ProjectDetailCountsIO;
import org.gs4tr.termmanager.model.dto.ProjectDetailsIO;
import org.springframework.stereotype.Component;

@Component
public class CountsUpdateHandler extends AbstractHandler implements Handler<UpdateCountEvent> {

    @Override
    public void onEvent(UpdateCountEvent event) throws EventException {

	validateUpdateCountEvent(event);

	ProjectDetailsIO projectDetails = event.getProjectDetails();

	logMessage(String.format("Updating counts for project [%d] STARTED.", projectDetails.getProjectId()));

	updateProjectDetail(projectDetails.getProjectInfoDetails());
	updateProjectLanguageDetail(projectDetails.getProjectLanguageDetails());
	updateProjectUserDetail(projectDetails.getProjectUserDetails());

	logMessage(String.format("Updating counts for project [%d] FINISHED.", projectDetails.getProjectId()));

    }

    private void updateProjectDetail(ProjectDetailCountsIO projectDetailCounts) {
	getProjectDetailDAO().incrementalUpdateProjectDetail(projectDetailCounts);
    }

    private void updateProjectLanguageDetail(List<ProjectLanguageDetailInfoIO> projectLanguageDetails) {
	if (CollectionUtils.isNotEmpty(projectLanguageDetails)) {
	    projectLanguageDetails
		    .forEach(d -> getProjectLanguageDetailDAO().incrementalUpdateProjectLanguageDetail(d));

	}
    }

    private void updateProjectUserDetail(List<ProjectUserDetailIO> projectUserDetails) {
	if (CollectionUtils.isNotEmpty(projectUserDetails)) {
	    projectUserDetails.forEach(i -> getProjectUserDetailDAO().incrementalUpdateProjectDetail(i));
	}
    }

    private void validateUpdateCountEvent(UpdateCountEvent e) {
	Validate.notNull(e);
	Validate.notNull(e.getProjectDetails(), "The data that need to be processed, can't be null!");
    }

    @Override
    protected void logMessage(String message) {
	LOGGER.info(message);
    }
}
