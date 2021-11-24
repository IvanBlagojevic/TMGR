package org.gs4tr.termmanager.service.manualtask;

import static org.gs4tr.termmanager.service.file.analysis.model.ImportAttribute.Level.TERM_ATTRIBUTE;
import static org.gs4tr.termmanager.service.file.analysis.model.ImportAttribute.Level.TERM_ENTRY;
import static org.gs4tr.termmanager.service.file.analysis.model.ImportAttribute.Level.TERM_NOTE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.foundation.modules.entities.model.ProjectInfo;
import org.gs4tr.foundation.modules.entities.model.RepositoryItem;
import org.gs4tr.foundation.modules.entities.model.ResourceInfo;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.StringConstants;
import org.gs4tr.foundation.modules.entities.model.TaskPriority;
import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.termmanager.model.Attribute;
import org.gs4tr.termmanager.model.Disposition;
import org.gs4tr.termmanager.model.Language;
import org.gs4tr.termmanager.model.SystemTask;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.model.dto.converter.LanguageConverter;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.export.ExportFormatEnum;
import org.gs4tr.termmanager.service.file.analysis.model.ImportAttribute.Level;
import org.gs4tr.termmanager.service.model.command.GenerateImportTemplateCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoGenerateImportTemplateCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.gs4tr.termmanager.service.xls.XlsHelper;
import org.gs4tr.termmanager.service.xls.XlsType;
import org.gs4tr.termmanager.service.xls.XlsWriter;
import org.springframework.beans.factory.annotation.Autowired;

@Disposition
@SystemTask(priority = TaskPriority.LEVEL_TEN)
public class GenerateImportTemplateTaskHandler extends AbstractManualTaskHandler {

    private static final String IMPORT_TEMPLATE = "Import_Template";

    private static final String LANGUAGES = "languages";

    private static final Log LOG = LogFactory.getLog(GenerateImportTemplateTaskHandler.class);

    private static final String MIME_TYPE = "application-download";

    @Autowired
    private ProjectService _projectService;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoGenerateImportTemplateCommand.class;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.SINGLE_SELECT;
    }

    @Override
    public TaskModel[] getTaskInfos(Long[] parentIds, String taskName, Object command) {
	List<org.gs4tr.termmanager.model.dto.Language> languages = Language.getAllAvailableLanguages().stream()
		.map(LanguageConverter::fromInternalToDto)
		.sorted(Comparator.comparing(org.gs4tr.termmanager.model.dto.Language::getValue))
		.collect(Collectors.toList());

	LogHelper.info(LOG, String.format(Messages.getString("GenerateImportTemplateTaskHandler.0"), //$NON-NLS-1$
		languages.size()));

	return createGetTaskResponse(languages);
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object cmd, List<UploadedRepositoryItem> files) {
	GenerateImportTemplateCommand command = (GenerateImportTemplateCommand) cmd;
	String errorMessage = Messages.getString("GenerateImportTemplateTaskHandler.3");
	Validate.notNull(command.getProjectId(), Messages.getString("GenerateImportTemplateTaskHandler.1")); //$NON-NLS-1$
	Validate.notEmpty(command.getLanguages(), Messages.getString("GenerateImportTemplateTaskHandler.2")); //$NON-NLS-1$

	TmProject project = getProjectService().findProjectById(command.getProjectId(), Attribute.class);

	Map<Level, Set<String>> attributesByLevel = ManualTaskHandlerUtils
		.groupAttributesByLevel(project.getAttributes());

	XlsWriter writer = new XlsWriter();
	writer.write(createHeader(command.getLanguages(), attributesByLevel), true);

	File f = new File(StringConstants.TEMP_DIR, FilenameUtils.getName(getTempFileName()));
	try (OutputStream out = new FileOutputStream(f)) {
	    writer.save(out, XlsType.XLSX);
	} catch (Exception e) {
	    LogHelper.error(LOG, e.getMessage(), e);
	    throw new UserException(errorMessage, e); // $NON-NLS-1$
	}

	ResourceInfo resourceInfo = new ResourceInfo();
	resourceInfo.setName(getTemplateName(project.getProjectInfo()));
	resourceInfo.setMimeType(MIME_TYPE);
	resourceInfo.setSize(f.length());

	RepositoryItem repositoryItem = new RepositoryItem();
	repositoryItem.setResourceInfo(resourceInfo);
	try {
	    InputStream stream = new FileInputStream(f);
	    repositoryItem.setInputStream(stream);
	    repositoryItem.setCleanupCallback(() -> {
		IOUtils.closeQuietly(stream);
		ManualTaskHandlerUtils.forceDeleteFile(f);
	    });
	} catch (IOException e) {
	    LogHelper.error(LOG, e.getMessage(), e);
	    throw new UserException(errorMessage, e); // $NON-NLS-1$
	}

	LogHelper.info(LOG, String.format(Messages.getString("GenerateImportTemplateTaskHandler.5"), //$NON-NLS-1$
		resourceInfo.getName(), writer.getHeaderColumns()));

	return createPostTaskResponse(repositoryItem);
    }

    private TaskModel[] createGetTaskResponse(List<org.gs4tr.termmanager.model.dto.Language> languages) {
	TaskModel taskModel = new TaskModel();
	taskModel.addObject(LANGUAGES, languages);
	return new TaskModel[] { taskModel };
    }

    private List<String> createHeader(List<String> languages, Map<Level, Set<String>> attributesByLevel) {
	Set<String> attributes = attributesByLevel.get(TERM_ATTRIBUTE), tNotes = attributesByLevel.get(TERM_NOTE);

	List<String> header = new LinkedList<>(attributesByLevel.getOrDefault(TERM_ENTRY, Collections.emptySet()));

	StringBuilder hBuilder = new StringBuilder(32);
	for (String language : languages) {
	    header.add(language);
	    hBuilder.append(language).append(XlsHelper.STATUS);
	    header.add(hBuilder.toString());
	    hBuilder.setLength(0);

	    if (CollectionUtils.isNotEmpty(attributes)) {
		for (String attribute : attributes) {
		    hBuilder.append(language);
		    hBuilder.append(XlsHelper.COLON);
		    hBuilder.append(attribute);
		    header.add(hBuilder.toString());
		    hBuilder.setLength(0);
		}
	    }
	    if (CollectionUtils.isNotEmpty(tNotes)) {
		for (String note : tNotes) {
		    hBuilder.append(language);
		    hBuilder.append(XlsHelper.NOTE);
		    hBuilder.append(note);
		    header.add(hBuilder.toString());
		    hBuilder.setLength(0);
		}
	    }
	}

	return header;
    }

    private TaskResponse createPostTaskResponse(RepositoryItem repositoryItem) {
	Ticket ticket = new Ticket(repositoryItem.getResourceInfo().getName());
	TaskResponse taskResponse = new TaskResponse(ticket);
	taskResponse.setRepositoryItem(repositoryItem);
	return taskResponse;
    }

    private ProjectService getProjectService() {
	return _projectService;
    }

    private String getTempFileName() {
	return UUID.randomUUID().toString() + StringConstants.DOT + ExportFormatEnum.XLSX.getFileFormat();
    }

    /**
     * The template file should be named:
     * <p>
     * 'Project Name_Project Short Code_Import_Template.xlsx'
     * </p>
     */
    private String getTemplateName(ProjectInfo projectInfo) {
	return projectInfo.getName() + StringConstants.UNDERSCORE + projectInfo.getShortCode()
		+ StringConstants.UNDERSCORE + IMPORT_TEMPLATE + StringConstants.DOT
		+ ExportFormatEnum.XLSX.getFileFormat();

    }
}
