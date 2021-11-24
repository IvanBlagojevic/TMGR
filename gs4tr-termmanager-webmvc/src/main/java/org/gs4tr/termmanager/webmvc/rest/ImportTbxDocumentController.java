package org.gs4tr.termmanager.webmvc.rest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.eventlogging.spring.aop.EventLogger;
import org.gs4tr.eventlogging.spring.aop.annotation.LogEvent;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.entities.model.UserMessageTypeEnum;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.foundation.modules.webmvc.rest.AbstractRestController;
import org.gs4tr.termmanager.model.ImportErrorAction;
import org.gs4tr.termmanager.model.ImportTypeEnum;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.ProjectPolicyEnum;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.dto.ImportErrorMessage;
import org.gs4tr.termmanager.model.dto.ImportSummary;
import org.gs4tr.termmanager.model.dto.converter.ImportSummaryConverter;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.persistence.update.DescriptionImportOption;
import org.gs4tr.termmanager.persistence.update.ImportOptionsModel;
import org.gs4tr.termmanager.service.ImportTermService;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.StatisticsService;
import org.gs4tr.termmanager.service.logging.util.EventContextConstants;
import org.gs4tr.termmanager.service.logging.util.TMGREventActionConstants;
import org.gs4tr.termmanager.service.termentry.synchronization.SyncOption;
import org.gs4tr.termmanager.webmvc.rest.utils.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ImportTbxDocumentController extends AbstractRestController {

    @Autowired
    private ImportTermService _importTermsService;

    private static Log _logger = LogFactory.getLog(ImportTbxDocumentController.class);

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private StatisticsService _statisticsService;

    @Value("${synonym.number:5}")
    private int _synonymNumber;

    @RequestMapping(value = "/rest/import", method = RequestMethod.POST, produces = "application/xml;charset=UTF-8")
    @ResponseBody
    @LogEvent(action = TMGREventActionConstants.ACTION_IMPORT, actionCategory = TMGREventActionConstants.ACTION_TYPE_REST)
    public String doPost(@RequestParam String projectTicket, @RequestParam String syncLang,
	    @RequestParam(value = "userId") String userId, @RequestBody byte[] body) {

	if (validateInputParameters(projectTicket, syncLang)) {

	    EventLogger.addProperty(EventContextConstants.SOURCE_LANGUAGE, syncLang);

	    Long projectId = TicketConverter.fromDtoToInternal(projectTicket, Long.class);
	    EventLogger.addProperty(EventContextConstants.PROJECT_ID, projectId);

	    if (getImportTermService().isLocked(projectId)) {
		throw new UserException(MessageResolver.getMessage("project.is.locked.m"),
			MessageResolver.getMessage("project.is.locked"), UserMessageTypeEnum.WARNING);
	    }

	    TmProject project = getProjectService().load(projectId);
	    String projectName = project.getProjectInfo().getName();
	    EventLogger.addProperty(EventContextConstants.PROJECT_NAME, projectName);
	    EventLogger.addProperty(EventContextConstants.PROJECT_SHORT_CODE, project.getProjectInfo().getShortCode());

	    LogHelper.debug(_logger, String.format(Messages.getString("ImportTbxDocumentController.0"), //$NON-NLS-1$
		    TmUserProfile.getCurrentUserName(), projectName, syncLang));

	    InputStream inputStream = new ByteArrayInputStream(body);
	    ImportSummary importSummary;
	    try {
		importSummary = importTbxDocument(projectTicket, syncLang, inputStream);
		return SerializationUtils.toXML(importSummary);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}

	return null;
    }

    public int getSynonymNumber() {
	return _synonymNumber;
    }

    public ImportSummary importTbxDocument(final String projectTicket, final String syncLang, InputStream inputStream)
	    throws Exception {

	Long projectId = TicketConverter.fromDtoToInternal(projectTicket, Long.class);

	TmUserProfile currentUserProfile = TmUserProfile.getCurrentUserProfile();

	boolean canImport = currentUserProfile.containsContextPolicies(projectId,
		new String[] { ProjectPolicyEnum.POLICY_TM_TERMENTRY_IMPORT.toString() });

	if (!canImport) {
	    ImportErrorMessage importErrorMessage = new ImportErrorMessage();
	    importErrorMessage.setErrorMessage(Messages.getString("ImportTbxDocumentController.1")); //$NON-NLS-1$

	    ImportErrorMessage[] errors = { importErrorMessage };

	    ImportSummary importSummary = new ImportSummary();
	    importSummary.setErrorMessages(errors);

	    return importSummary;
	}

	TmProject project = getProjectService().load(projectId);

	ImportOptionsModel importOptions = new ImportOptionsModel();
	importOptions.setProjectId(projectId);
	importOptions.setProjectName(project.getProjectInfo().getName());
	importOptions.setProjectShortCode(project.getProjectInfo().getShortCode());
	importOptions.setDescriptionImportOption(DescriptionImportOption.ADD_ALL);
	importOptions.setImportErrorAction(ImportErrorAction.SKIP);
	importOptions.setStatus(ItemStatusTypeHolder.PROCESSED.getName());
	importOptions.setSyncLanguageId(syncLang);
	importOptions.setSynonymNumber(getSynonymNumber());

	org.gs4tr.termmanager.model.ImportSummary returnedImportSummary = getImportTermService().importDocumentWS(
		importOptions, inputStream, System.currentTimeMillis(), ImportTypeEnum.TBX, SyncOption.MERGE);

	getImportTermService().updateProjectDetailOnImport(importOptions, returnedImportSummary);

	getStatisticsService().updateStatisticsOnImport(projectId, returnedImportSummary);

	return ImportSummaryConverter.fromInternalToDto(returnedImportSummary);
    }

    private ImportTermService getImportTermService() {
	return _importTermsService;
    }

    private ProjectService getProjectService() {
	return _projectService;
    }

    private StatisticsService getStatisticsService() {
	return _statisticsService;
    }

    private boolean validateInputParameters(String projectTicket, String syncLang) {
	boolean result = true;
	if (StringUtils.isBlank(projectTicket)) {
	    result = false;
	}
	if (StringUtils.isBlank(syncLang)) {
	    result = false;
	}
	return result;
    }
}
