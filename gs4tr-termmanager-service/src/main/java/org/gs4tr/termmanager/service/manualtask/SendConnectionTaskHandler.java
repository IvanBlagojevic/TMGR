package org.gs4tr.termmanager.service.manualtask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.CleanupCallback;
import org.gs4tr.foundation.modules.entities.model.RepositoryItem;
import org.gs4tr.foundation.modules.entities.model.ResourceInfo;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.StringConstants;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.mail.service.MailTemplatesService;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.termmanager.model.Disposition;
import org.gs4tr.termmanager.model.ProjectUserLanguage;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.model.dto.Language;
import org.gs4tr.termmanager.model.dto.converter.LanguageConverter;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.service.model.command.SendConnectionCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoSendConnectionCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.gs4tr.termmanager.service.utils.MailHelper;
import org.gs4tr.termmanager.service.utils.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Disposition
public class SendConnectionTaskHandler extends AbstractManualTaskHandler {

    private static final String CONNECTION_STRINGS = "connectionStrings"; //$NON-NLS-1$

    private static final String CSV_FILENAME = "connectionStrings.csv"; //$NON-NLS-1$

    private static final String HTTP = "http://"; //$NON-NLS-1$

    private static final String HTTPS = "https://"; //$NON-NLS-1$

    private static final String LANGUAGES = "languages"; //$NON-NLS-1$

    private static final String MAIL_TEMPLATE = "connectionStrings"; //$NON-NLS-1$

    private static final String PASSWORD_STRING = "pwd="; //$NON-NLS-1$

    private static final String PROJECT_STRING = "prj="; //$NON-NLS-1$

    private static final String SOURCE_LANGUAGE = "src="; //$NON-NLS-1$

    private static final String TARGET_LANGUAGE = "tgt="; //$NON-NLS-1$

    private static final String TMGR = "tmgr://"; //$NON-NLS-1$

    private static final String TMGRS = "tmgrs://"; //$NON-NLS-1$

    private static final String USER_STRING = "usr="; //$NON-NLS-1$

    private Log _logger = LogFactory.getLog(SendConnectionTaskHandler.class);

    @Autowired
    private MailHelper _mailHelper;

    @Autowired
    private MailTemplatesService _mailTemplatesService;

    @Autowired
    private ProjectService _projectService;

    private String _serverAddress;

    @Autowired
    private UserProfileService _userProfileService;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoSendConnectionCommand.class;
    }

    public MailTemplatesService getMailTemplatesService() {
	return _mailTemplatesService;
    }

    public ProjectService getProjectService() {
	return _projectService;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.MULTI_SELECT;
    }

    public String getServerAddress() {
	return _serverAddress;
    }

    @Override
    public TaskModel[] getTaskInfos(Long[] parentIds, String taskName, Object command) {

	TaskModel taskModel = new TaskModel();

	List<Long> projectIds = new ArrayList<Long>();
	CollectionUtils.addAll(projectIds, parentIds);

	boolean exist = getProjectService().containsGenericProjectUsers(projectIds);
	if (!exist) {
	    throw new UserException(MessageResolver.getMessage("SendConnectionTaskHandler.1"), //$NON-NLS-1$
		    MessageResolver.getMessage("SendConnectionTaskHandler.3")); //$NON-NLS-1$
	}

	if (command == null) {

	    addLanguagesToModel(taskModel, projectIds);

	    return new TaskModel[] { taskModel };
	}

	SendConnectionCommand sendCommand = (SendConnectionCommand) command;

	String sourceLanguage = sendCommand.getSourceLanguage();

	Set<String> connectionStrings = createConnectionStrings(projectIds, sourceLanguage);

	taskModel.addObject(CONNECTION_STRINGS, connectionStrings);

	return new TaskModel[] { taskModel };
    }

    public UserProfileService getUserProfileService() {
	return _userProfileService;
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> repositoryItems) {
	TaskResponse taskResponse = new TaskResponse(null);

	List<Long> projectIds = new ArrayList<Long>();
	CollectionUtils.addAll(projectIds, parentIds);

	SendConnectionCommand sendCommand = (SendConnectionCommand) command;

	Set<String> connectionStrings = sendCommand.getConnectionStrings();
	if (connectionStrings == null) {
	    connectionStrings = createConnectionStrings(projectIds, sendCommand.getSourceLanguage());
	}

	File tempCsvFile = createCsvFile(connectionStrings);

	String emailsCommand = sendCommand.getEmails();

	MailHelper mailHelper = getMailHelper();

	if (StringUtils.isNotBlank(emailsCommand)) {
	    String[] emails = emailsCommand.split(StringConstants.COMMA);

	    mailHelper.sendMail(emails, MAIL_TEMPLATE, createEmailConfiguration(), tempCsvFile);
	} else {
	    RepositoryItem repositoryItem = null;
	    try {
		repositoryItem = createRepositoryItem(tempCsvFile);
	    } catch (IOException e) {
		throw new RuntimeException(e.getMessage(), e);
	    }

	    taskResponse.setRepositoryItem(repositoryItem);
	}

	return taskResponse;
    }

    @Value("${serverAddress}")
    public void setServerAddress(String serverAddress) {
	_serverAddress = serverAddress;
    }

    private void addLanguagesToModel(TaskModel taskModel, List<Long> projectIds) {
	Set<String> languageCodes = getProjectService().getProjectLanguageCodes(projectIds);

	Set<Language> languages = new HashSet<Language>();

	for (String langCode : languageCodes) {
	    Language dtoLanguage = LanguageConverter.fromInternalToDto(langCode);
	    languages.add(dtoLanguage);
	}

	taskModel.addObject(LANGUAGES, languages);
    }

    private Set<String> createConnectionStrings(List<Long> projectIds, String sourceLanguage) {
	Set<String> connectionStrings = new LinkedHashSet<String>();

	List<ProjectUserLanguage> projectUserLanguages = getProjectService()
		.findProjectUserLanguageByProjectIds(projectIds, TmProject.class, TmUserProfile.class);

	Map<TmProject, List<ProjectUserLanguage>> projectUserLangMap = new HashMap<TmProject, List<ProjectUserLanguage>>();

	for (ProjectUserLanguage pul : projectUserLanguages) {
	    TmProject project = pul.getProject();
	    List<ProjectUserLanguage> pulList = projectUserLangMap.get(project);
	    if (pulList == null) {
		pulList = new ArrayList<ProjectUserLanguage>();
		projectUserLangMap.put(project, pulList);
	    }

	    pulList.add(pul);
	}

	Map<Long, Set<String>> sortedConnections = new TreeMap<>();

	Set<String> languages = new HashSet<>();

	for (Map.Entry<TmProject, List<ProjectUserLanguage>> entry : projectUserLangMap.entrySet()) {
	    TmProject project = entry.getKey();
	    Long projectId = project.getProjectId();

	    Set<String> connectionValues = sortedConnections.computeIfAbsent(projectId, k -> new LinkedHashSet<>());

	    String projectShortCode = project.getProjectInfo().getShortCode();

	    List<ProjectUserLanguage> pul = entry.getValue();

	    for (ProjectUserLanguage projectUserLanguage : pul) {
		languages.add(projectUserLanguage.getLanguage());
	    }

	    if (!languages.contains(sourceLanguage)) {
		languages.clear();
		continue;
	    }

	    for (ProjectUserLanguage projectUserLanguage : pul) {
		TmUserProfile user = projectUserLanguage.getUser();
		String userName = user.getUserInfo().getUserName();
		// skip connection string for ope_user (TERII-4007)
		if (user.isOpe()) {
		    continue;
		}
		String password = user.getGenericPassword();
		String language = projectUserLanguage.getLanguage();

		if (!language.equals(sourceLanguage)) {
		    String connectionString = genereateConnectionString(projectShortCode, userName, password,
			    sourceLanguage, language);
		    connectionValues.add(connectionString);
		}
	    }

	    languages.clear();
	}

	for (Entry<Long, Set<String>> entry : sortedConnections.entrySet()) {
	    connectionStrings.addAll(entry.getValue());
	}

	return connectionStrings;
    }

    private File createCsvFile(Set<String> connectionStrings) {
	StringBuilder builder = new StringBuilder();

	for (String connectionString : connectionStrings) {
	    builder.append(connectionString);
	    builder.append(StringConstants.NEW_LINE);
	}

	File csvFile = new File(StringConstants.TEMP_DIR, CSV_FILENAME);

	try {
	    OutputStream output = new FileOutputStream(csvFile);
	    output.write(builder.toString().getBytes(StandardCharsets.UTF_8));
	    output.flush();
	    output.close();
	} catch (IOException e) {
	    throw new RuntimeException(Messages.getString("SendConnectionTaskHandler.2"), e); //$NON-NLS-1$
	}

	return csvFile;
    }

    private Map<String, Object> createEmailConfiguration() {
	return new HashMap<String, Object>();
    }

    private RepositoryItem createRepositoryItem(final File file) throws IOException {
	final InputStream inputStream = new FileInputStream(file);

	CleanupCallback cleanupCallback = new CleanupCallback() {
	    @Override
	    public void cleanup() {
		try {
		    ServiceUtils.closeInputStream(inputStream);
		    FileUtils.forceDelete(file);
		} catch (IOException e) {
		    _logger.error(Messages.getString("SendConnectionTaskHandler.0")); //$NON-NLS-1$
		}
	    }
	};

	ResourceInfo resourceInfo = new ResourceInfo();
	resourceInfo.setName(file.getName());
	resourceInfo.setSize(file.length());

	RepositoryItem repositoryItem = new RepositoryItem();
	repositoryItem.setInputStream(inputStream);
	repositoryItem.setCleanupCallback(cleanupCallback);
	repositoryItem.setResourceInfo(resourceInfo);

	return repositoryItem;
    }

    private String genereateConnectionString(String projectShortCode, String username, String password,
	    String sourceLanguage, String targetLanguage) {
	String serverAddress = getResolvedServerAddress();

	String ampersand = StringConstants.AMPERSAND;

	StringBuilder builder = new StringBuilder();
	builder.append(serverAddress);
	builder.append(StringConstants.QUESTION_MARK);
	builder.append(PROJECT_STRING);
	builder.append(projectShortCode);
	builder.append(ampersand);
	builder.append(USER_STRING);
	builder.append(username);
	builder.append(ampersand);
	builder.append(PASSWORD_STRING);
	builder.append(password);
	builder.append(ampersand);
	builder.append(SOURCE_LANGUAGE);
	builder.append(sourceLanguage);
	builder.append(ampersand);
	builder.append(TARGET_LANGUAGE);
	builder.append(targetLanguage);

	return builder.toString();
    }

    private MailHelper getMailHelper() {
	return _mailHelper;
    }

    private String getResolvedServerAddress() {
	String serverAddress = getServerAddress();
	if (serverAddress.startsWith(HTTP)) {
	    serverAddress = serverAddress.replaceAll(HTTP, TMGR);
	} else if (serverAddress.startsWith(HTTPS)) {
	    serverAddress = serverAddress.replaceAll(HTTPS, TMGRS);
	}
	return serverAddress;
    }
}
