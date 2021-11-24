package org.gs4tr.termmanager.webmvc.rest;

import java.util.List;

import org.gs4tr.foundation.modules.webmvc.rest.AbstractRestController;
import org.gs4tr.termmanager.model.Attribute;
import org.gs4tr.termmanager.model.search.TermEntrySearchRequest;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.utils.TermEntryUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseExportDocumentController extends AbstractRestController {

    protected static final String TM_EXPORTED = "TM_Exported_"; //$NON-NLS-1$

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private TermEntryService _termEntryService;

    protected void addSyncAttributes(TermEntrySearchRequest searchRequest) {
	List<Attribute> attributes = getProjectService().getAttributesByProjectId(searchRequest.getProjectId());
	searchRequest.setTermAttributes(TermEntryUtils.filterSynchronizableAttributes(attributes));
    }

    protected ProjectService getProjectService() {
	return _projectService;
    }

    protected TermEntryService getTermEntryService() {
	return _termEntryService;
    }

}