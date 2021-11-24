package org.gs4tr.termmanager.webservice.controllers;

import java.io.InputStream;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.eventlogging.spring.aop.EventLogger;
import org.gs4tr.eventlogging.spring.aop.annotation.LogEvent;
import org.gs4tr.foundation.modules.entities.model.UserMessageTypeEnum;
import org.gs4tr.termmanager.model.ImportErrorAction;
import org.gs4tr.termmanager.model.ImportTypeEnum;
import org.gs4tr.termmanager.model.ProjectPolicyEnum;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
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
import org.gs4tr.termmanager.webservice.exceptions.BrokenStreamException;
import org.gs4tr.termmanager.webservice.exceptions.ProjectLockedException;
import org.gs4tr.termmanager.webservice.exceptions.UnauthorizedAccessException;
import org.gs4tr.termmanager.webservice.model.request.ImportCommand;
import org.gs4tr.termmanager.webservice.model.response.BaseResponse;
import org.gs4tr.termmanager.webservice.model.response.ErrorResponse;
import org.gs4tr.termmanager.webservice.model.response.ImportGlossaryResponse;
import org.gs4tr.termmanager.webservice.model.response.ReturnCode;
import org.gs4tr.termmanager.webservice.utils.V2Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * This class provide option to import terminology in specified Term Manager
 * project.
 *
 * @author TMGR_Backend
 */
@RequestMapping("/rest/v2/import")
@RestController
@Api(value = "Import Glossary")
public class ImportDocumentController {

    private static final String[] IMPORT_POLICIES = new String[] {
	    ProjectPolicyEnum.POLICY_TM_TERMENTRY_IMPORT.toString() };

    private static final Log LOGGER = LogFactory.getLog(ImportDocumentController.class);

    @Autowired
    private ImportTermService _importTermService;

    @Autowired
    private ProjectService _projectService;

    @Autowired(required = false)
    private StatisticsService _statisticsService;

    @Value("${synonym.number:5}")
    private int _synonymNumber;

    public int getSynonymNumber() {
	return _synonymNumber;
    }

    @ApiResponses(value = {
	    @ApiResponse(code = 200, message = "Successful operation.", response = ImportGlossaryResponse.class),
	    @ApiResponse(code = 400, message = "Missing required parameter/Invalid parameter.", response = ErrorResponse.class),
	    @ApiResponse(code = 500, message = "Internal server error.") })
    @ApiOperation(value = "Import multilingual glossary in specified Term Manager project.", httpMethod = "POST", consumes = "multipart/form-data", produces = "application/json")
    @RequestMapping(method = RequestMethod.POST)
    @LogEvent(action = TMGREventActionConstants.ACTION_IMPORT, actionCategory = TMGREventActionConstants.ACTION_TYPE_REST_V2)
    public BaseResponse importDocument(@RequestPart ImportCommand command, @RequestPart MultipartFile file)
	    throws Exception {

	validateCommand(command, file);

	Long projectId = TicketConverter.fromDtoToInternal(command.getProjectTicket(), Long.class);
	EventLogger.addProperty(EventContextConstants.PROJECT_ID, projectId);

	// validate user authorization
	try {
	    TmUserProfile user = TmUserProfile.getCurrentUserProfile();
	    boolean valid = user.containsContextPolicies(projectId, IMPORT_POLICIES);
	    Validate.isTrue(valid, Messages.getString("UserImportPoliciesError"));
	} catch (Exception e) {
	    throw new UnauthorizedAccessException(e.getMessage(), e);
	}

	if (getImportTermService().isLocked(projectId)) {
	    throw new ProjectLockedException(Messages.getString("project.is.locked.m"),
		    Messages.getString("project.is.locked"), UserMessageTypeEnum.WARNING);
	}

	TmProject project = V2Utils.getProject(projectId, getProjectService());
	EventLogger.addProperty(EventContextConstants.PROJECT_NAME, project.getProjectInfo().getName());
	EventLogger.addProperty(EventContextConstants.PROJECT_SHORT_CODE, project.getProjectInfo().getShortCode());

	logEvent(command, project, file);

	ImportSummary info;
	try (InputStream stream = file.getInputStream()) {
	    info = performImport(projectId, stream, command);
	} catch (Exception e) {
	    throw new BrokenStreamException(e.getMessage(), e);
	}

	return new ImportGlossaryResponse(ReturnCode.OK, true, info);
    }

    private ImportOptionsModel createImportOptionModel(TmProject project, ImportCommand command) {

	ImportOptionsModel importOptions = new ImportOptionsModel();
	importOptions.setProjectId(project.getProjectId());
	importOptions.setProjectName(project.getProjectInfo().getName());
	importOptions.setProjectShortCode(project.getProjectInfo().getShortCode());
	importOptions.setImportErrorAction(ImportErrorAction.SKIP);
	importOptions.setStatus(project.getDefaultTermStatus().getName());
	importOptions.setSyncLanguageId(command.getSyncLang());
	importOptions.setImportLocales(command.getImportLocales());
	importOptions.setSynonymNumber(getSynonymNumber());

	DescriptionImportOption importOption = command.getDescriptionImportOption();

	if (DescriptionImportOption.IMPORT_ONLY_SELECTED == importOption) {
	    importOptions.setAllowedTermEntryAttributes(V2Utils.resolveAllowedTermEntryDescriptions(command));
	    importOptions.setAllowedTermDescriptions(V2Utils.resolveAllowedTermDescription(command));
	}

	importOptions.setDescriptionImportOption(importOption);
	importOptions.setStatusReplacement(command.getStatusReplacement());
	V2Utils.resolveAttributeReplacements(importOptions, command.getAttributeReplacements());

	return importOptions;
    }

    private ImportTermService getImportTermService() {
	return _importTermService;
    }

    private ProjectService getProjectService() {
	return _projectService;
    }

    private StatisticsService getStatisticsService() {
	return _statisticsService;
    }

    private void logEvent(ImportCommand command, TmProject project, MultipartFile file) {
	if (LOGGER.isDebugEnabled()) {
	    String projectName = V2Utils.getProjectName(project);
	    LOGGER.debug(String.format(Messages.getString("ImportDocumentController.0"), //$NON-NLS-1$
		    TmUserProfile.getCurrentUserName(), file.getSize(), command, projectName));
	}
    }

    private ImportSummary performImport(Long projectId, InputStream inputStream, ImportCommand command)
	    throws Exception {

	TmProject project = getProjectService().load(projectId);

	ImportOptionsModel importOptions = createImportOptionModel(project, command);

	ImportTypeEnum type = ImportTypeEnum.valueOf(command.getImportType());

	org.gs4tr.termmanager.model.ImportSummary returnedImportSummary = getImportTermService().importDocumentWS(
		importOptions, inputStream, System.currentTimeMillis(), type, command.getSyncOption());

	getImportTermService().updateProjectDetailOnImport(importOptions, returnedImportSummary);

	getStatisticsService().updateStatisticsOnImport(projectId, returnedImportSummary);
	ImportSummary summary = ImportSummaryConverter.fromInternalToDto(returnedImportSummary);

	return summary;
    }

    private void validateCommand(ImportCommand command, MultipartFile file) {
	Validate.notNull(command, Messages.getString("ImportCommandError"));
	Validate.notEmpty(command.getProjectTicket(), Messages.getString("ProjectTicketError"));
	Validate.notEmpty(command.getSyncLang(), Messages.getString("SyncLangError"));

	Validate.notNull(file, Messages.getString("FileError"));
    }
}
