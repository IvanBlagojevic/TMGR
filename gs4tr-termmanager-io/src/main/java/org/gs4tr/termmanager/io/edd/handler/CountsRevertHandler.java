package org.gs4tr.termmanager.io.edd.handler;

import static org.gs4tr.termmanager.io.edd.utils.HandlerUtils.createProjectDetailInfoFromFacetSearch;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.Validate;
import org.gs4tr.termmanager.dao.ProjectLanguageDAO;
import org.gs4tr.termmanager.io.config.IndexConnectionHandler;
import org.gs4tr.termmanager.io.edd.api.Handler;
import org.gs4tr.termmanager.io.edd.event.RevertCountEvent;
import org.gs4tr.termmanager.io.exception.EventException;
import org.gs4tr.termmanager.model.ProjectLanguageDetailInfoIO;
import org.gs4tr.termmanager.model.dto.ProjectDetailsIO;
import org.gs4tr.termmanager.persistence.ITmgrGlossarySearcher;
import org.gs4tr.tm3.api.TmException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CountsRevertHandler extends AbstractHandler implements Handler<RevertCountEvent> {

    @Autowired
    private IndexConnectionHandler _indexConnectionHandler;

    @Autowired
    private ProjectLanguageDAO _projectLanguageDAO;

    @Value("${synonym.number:5}")
    private int _synonymNumber;

    @Override
    public void onEvent(RevertCountEvent event) throws EventException {
	validateRevertCountEvent(event);

	long projectId = event.getProjectId();

	logMessage(String.format("Reverting counts for project [%d] STARTED.", projectId));

	Set<String> languageIds = getLanguageIdsForProject(projectId);

	ITmgrGlossarySearcher searcher = getSearcher(event.getCollection());

	ProjectDetailsIO projectDetailsIO = createProjectDetailInfoFromFacetSearch(languageIds, projectId, searcher,
		getSynonymNumber());

	updateProjectDetailInfo(projectDetailsIO);

	logMessage(String.format("Reverting counts for project [%d] FINISHED.", projectId));

    }

    private IndexConnectionHandler getIndexConnectionHandler() {
	return _indexConnectionHandler;
    }

    private Set<String> getLanguageIdsForProject(long projectId) {
	return getProjectLanguageDAO().getLanguageIdsByProjectId(projectId);
    }

    private ProjectLanguageDAO getProjectLanguageDAO() {
	return _projectLanguageDAO;
    }

    private ITmgrGlossarySearcher getSearcher(String collection) {
	try {
	    return getIndexConnectionHandler().connect(collection).getTmgrSearcher();
	} catch (TmException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    private int getSynonymNumber() {
	return _synonymNumber;
    }

    private void updateProjectDetailInfo(ProjectDetailsIO projectDetails) {

	getProjectDetailDAO().updateProjectDetail(projectDetails.getProjectInfoDetails());

	List<ProjectLanguageDetailInfoIO> projectLanguageDetail = projectDetails.getProjectLanguageDetails();

	if (CollectionUtils.isNotEmpty(projectLanguageDetail)) {
	    projectLanguageDetail.forEach(e -> getProjectLanguageDetailDAO().updateProjectLanguageDetail(e));
	}
    }

    private void validateRevertCountEvent(RevertCountEvent e) {
	Validate.notNull(e);
	Validate.notNull(e.getProjectId(), "Project id, can't be null!");
	Validate.notEmpty(e.getCollection(), "Collection can't be null or empty!");
    }

    @Override
    protected void logMessage(String message) {
	LOGGER.info(message);
    }
}
