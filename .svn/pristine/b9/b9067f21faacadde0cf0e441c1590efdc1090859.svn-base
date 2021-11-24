package org.gs4tr.termmanager.service.manualtask;

import static org.gs4tr.termmanager.persistence.solr.query.AbstractPageRequest.DEFAULT_PAGE;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.search.TermEntrySearchRequest;
import org.gs4tr.termmanager.persistence.solr.query.TmgrPageRequest;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.model.command.ExportCommand;
import org.gs4tr.tm3.api.Page;
import org.springframework.beans.factory.annotation.Autowired;

public class ExportItemCountTaskHandler extends AbstractExportDocumentTaskHandler {

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private TermEntryService _termEntryService;

    public ProjectService getProjectService() {
	return _projectService;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.NO_SELECT;
    }

    @Override
    public TaskModel[] getTaskInfos(Long[] parentIds, String taskName, Object command) {
	TaskModel taskModel = new TaskModel();

	ExportCommand exportCommand = (ExportCommand) command;

	TermEntrySearchRequest searchRequest = createSearchRequestFromSearchCommand(exportCommand);

	PagedListInfo pageListInfo = createPageListInfo(searchRequest);

	TmgrSearchFilter filter = createFilterFromRequest(searchRequest, pageListInfo);

	Page<TermEntry> page = getTermEntryService().searchTermEntries(filter);

	taskModel.addObject("termEntryCountExport", //$NON-NLS-1$
		page.getTotalResults());

	taskModel.addObject("totalTermEntryCount", //$NON-NLS-1$
		getTotalTermEntryCount(exportCommand));

	return new TaskModel[] { taskModel };
    }

    public TermEntryService getTermEntryService() {
	return _termEntryService;
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {
	return null;
    }

    private TmgrSearchFilter createFilter(Long projectId, Set<String> languageIds) {

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.addProjectId(projectId);

	filter.setPageable(new TmgrPageRequest(DEFAULT_PAGE, 1, null));
	filter.setTargetLanguages(new ArrayList<String>(languageIds));
	filter.addLanguageResultField(true, getSynonymNumber(), languageIds.toArray(new String[languageIds.size()]));

	return filter;
    }

    private Long getTotalTermEntryCount(ExportCommand command) {
	TmUserProfile user = TmUserProfile.getCurrentUserProfile();

	Long projectId = command.getProjectId();

	Set<String> languageIds = user.getProjectUserLanguages().get(projectId);

	TmgrSearchFilter filter = createFilter(projectId, languageIds);

	return getTermEntryService().getNumberOfTermEntries(filter).get(projectId);
    }
}
