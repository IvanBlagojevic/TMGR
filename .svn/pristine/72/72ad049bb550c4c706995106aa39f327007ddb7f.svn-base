package org.gs4tr.termmanager.service.manualtask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.Validate;
import org.gs4tr.foundation.modules.entities.model.ProjectInfo;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.foundation.modules.entities.model.types.ItemStatusType;
import org.gs4tr.foundation.modules.webmvc.filters.model.RefreshUserContext;
import org.gs4tr.termmanager.model.Language;
import org.gs4tr.termmanager.model.ProjectLanguage;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmOrganization;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.model.dto.converter.ItemStatusTypeConverter;
import org.gs4tr.termmanager.persistence.solr.faceting.FacetTermCounts;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.service.CacheGatewaySessionUpdaterService;
import org.gs4tr.termmanager.service.OrganizationService;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.RoleService;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.service.model.command.ProjectCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoProjectCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.springframework.beans.factory.annotation.Autowired;

public class EditProjectTaskHandler extends AbstractProjectManualTaskHandler {

    private static final String AUTO_REMOVED_USERS = "usersRemoved";

    private static final String CAN_BE_REMOVED = "canBeRemoved";

    private static final String CHECKED = "checked";

    private static final String CLIENT_IDENTIFIER_KEY = "clientIdentifier";

    private static final String LANGUAGE_DISPLAY_NAME_KEY = "languageDisplayName";

    private static final String LANGUAGE_KEY = "language";

    private static final String LANGUAGE_NAME_KEY = "languageLocale";

    private static final String ORGANIZATIONS = "organizations";

    private static final String ORGANIZATION_NAME = "organizationName";

    private static final String ORGANIZATION_TICKET = "ticket";

    private static final String PROJECT_LANGUAGES_KEY = "projectLanguages";

    private static final String PROJECT_NAME_KEY = "projectName";

    private static final String SHARE_PENDING_TERMS = "sharePendingTerms";

    private static final String SHORT_CODE_KEY = "shortCode";

    private static final String UNCHECKED = "unchecked";

    @Autowired
    private CacheGatewaySessionUpdaterService _cacheGatewaySessionUpdaterService;

    @Autowired
    private OrganizationService _organizationService;

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private RoleService _roleService;

    @Autowired
    private TermEntryService _termEntryService;

    @Autowired
    private UserProfileService _userProfileService;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoProjectCommand.class;
    }

    public OrganizationService getOrganizationService() {
	return _organizationService;
    }

    public ProjectService getProjectService() {
	return _projectService;
    }

    public RoleService getRoleService() {
	return _roleService;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.SINGLE_SELECT;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.gs4tr.termmanager.service.manualtask.AbstractManualTaskHandler#
     * getTaskInfos(java.lang.Long[], java.lang.String, java.lang.Object)
     */
    @Override
    public TaskModel[] getTaskInfos(Long[] parentIds, String taskName, Object command) {

	ProjectService projectService = getProjectService();

	ProjectCommand editCommand = (ProjectCommand) command;

	Long id = parentIds[0];

	TmProject project = projectService.findProjectById(id);

	Long projectId = project.getProjectId();
	TaskModel taskModel = new TaskModel(null, new Ticket(projectId));

	List<ProjectLanguage> projectLanguages = getProjectService().getProjectLanguagesByProjectId(projectId);

	ManualTaskHandlerUtils.sortProjectLanguages(projectLanguages);

	List<Map<String, Object>> projectLanguagesData = new ArrayList<>();

	List<String> removableLanguages;

	Map<Long, List<String>> userLanguagesMap = getProjectService().getUserLanguagesMap(projectId);

	boolean isLanguageDeleted = false;

	if (Objects.isNull(editCommand)) {
	    removableLanguages = removableLanguages(projectId, projectLanguages, userLanguagesMap);
	} else {
	    validateCommand(editCommand);
	    projectLanguages = editCommand.getProjectLanguages();
	    removableLanguages = filterRemovableLanguages(projectId, projectLanguages, userLanguagesMap);
	    isLanguageDeleted = true;
	}

	for (ProjectLanguage projectLanguage : projectLanguages) {
	    Map<String, Object> data = new LinkedHashMap<>();

	    String language = projectLanguage.getLanguage();
	    data.put(LANGUAGE_NAME_KEY, language);
	    data.put(LANGUAGE_DISPLAY_NAME_KEY, Language.valueOf(language).getDisplayName());
	    data.put(CAN_BE_REMOVED, removableLanguages.contains(language));

	    Map<String, Object> projectLanguageData = new HashMap<>();

	    projectLanguageData.put(LANGUAGE_KEY, data);

	    projectLanguagesData.add(projectLanguageData);
	}

	taskModel.addObject(PROJECT_LANGUAGES_KEY, projectLanguagesData);

	if (isLanguageDeleted) {
	    return new TaskModel[] { taskModel };
	}

	ProjectInfo projectInfo = project.getProjectInfo();

	taskModel.addObject(SHARE_PENDING_TERMS, project.getSharePendingTerms());

	taskModel.addObject(PROJECT_NAME_KEY, projectInfo.getName());

	taskModel.addObject(SHORT_CODE_KEY, projectInfo.getShortCode());
	taskModel.addObject(CLIENT_IDENTIFIER_KEY, projectInfo.getClientIdentifier());

	List<TmOrganization> organizations = getOrganizationService().findAllEnabledOrganizations();

	List<Map<String, Object>> organizationList = new ArrayList<>();

	for (TmOrganization organization : organizations) {

	    Map<String, Object> data = new HashMap<>();

	    data.put(ORGANIZATION_NAME, organization.getOrganizationInfo().getName());
	    data.put(ORGANIZATION_TICKET, new Ticket(organization.getOrganizationId()));
	    data.put(CHECKED,
		    (project.getOrganization() != null
			    && project.getOrganization().getOrganizationId().equals(organization.getOrganizationId())
				    ? CHECKED
				    : UNCHECKED));
	    organizationList.add(data);
	}

	taskModel.addObject(ORGANIZATIONS, organizationList);

	List<Map<String, String>> termStatuses = ManualTaskHandlerUtils.createTermStatusModel();

	taskModel.addObject(ManualTaskHandlerUtils.TERM_STATUSES, termStatuses);
	taskModel.addObject(ManualTaskHandlerUtils.DEFAULT_TERM_STATUS,
		ItemStatusTypeConverter.fromInternalToDto(project.getDefaultTermStatus()));

	return new TaskModel[] { taskModel };
    }

    public TermEntryService getTermEntryService() {
	return _termEntryService;
    }

    public UserProfileService getUserProfileService() {
	return _userProfileService;
    }

    @RefreshUserContext
    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> items) {

	ProjectInfo projectInfo = new ProjectInfo();

	ProjectCommand editCommand = (ProjectCommand) command;

	ProjectService projectService = getProjectService();

	RoleService roleService = getRoleService();

	Long projectId = taskIds[0];

	TmProject project = projectService.load(projectId);
	if (editCommand.getProjectInfo() != null) {

	    projectInfo.setName(editCommand.getProjectInfo().getName());
	    projectInfo.setShortCode(editCommand.getProjectInfo().getShortCode());
	    projectInfo.setClientIdentifier(editCommand.getProjectInfo().getClientIdentifier());
	    projectInfo.setEnabled(project.getProjectInfo().isEnabled());
	    projectId = projectService.updateProject(projectId, projectInfo);
	    project = projectService.load(projectId);
	}

	ItemStatusType defaultTermStatus = editCommand.getDefaultTermStatus();

	updateProjectIfDefaultTermStatusOrSharePendingTermsIsChanged(defaultTermStatus, project, projectService,
		editCommand);

	List<ProjectLanguage> projectLanguages = editCommand.getProjectLanguages();

	List<String> langs = new ArrayList<String>();
	if (CollectionUtils.isNotEmpty(projectLanguages)) {
	    for (ProjectLanguage proLang : projectLanguages) {
		langs.add(proLang.getLanguage());
	    }

	    projectId = projectService.addOrUpdateProjectLanguages(projectId, projectLanguages);
	    projectService.addOrUpdateProjectLanguageDetail(projectId, new HashSet<String>(langs));

	    updateGenericUserLanguages(projectId, langs);
	}

	// check and remove users from project if they do not have at least one
	// language of project
	Set<TmUserProfile> projectUsers = projectService.getProjectUsers(projectId);
	List<String> removedUsers = new ArrayList<String>();
	for (TmUserProfile projectUser : projectUsers) {
	    Long userId = projectUser.getUserProfileId();
	    if (CollectionUtils.isNotEmpty(projectService.getRolesByUserAndProject(projectId, userId))) {
		if (CollectionUtils.isEmpty(projectService.getProjectUserLanguageCodes(projectId, userId))) {
		    roleService.addOrUpdateProjectRoles(userId, projectId, null);
		    removedUsers.add(projectUser.getUserName());
		}
	    }
	}

	Long organizationId = editCommand.getOrganizationId();
	if (organizationId != null && !project.getOrganization().getOrganizationId().equals(organizationId)) {
	    getOrganizationService().addProjectToOrganization(organizationId, projectId);
	}

	TaskResponse taskResponse = new TaskResponse(new Ticket(projectId));

	taskResponse.addObject(AUTO_REMOVED_USERS, removedUsers.toArray());

	if (CollectionUtils.isNotEmpty(langs)) {
	    getCacheGatewaySessionUpdaterService().removeOnEditProjectLanguage(projectId, langs);
	}

	return taskResponse;

    }

    private List<String> filterRemovableLanguages(Long projectId, List<ProjectLanguage> incomingProjectLanguages,
	    Map<Long, List<String>> userLanguagesMap) {
	List<String> removableLanguages = removableLanguages(projectId, incomingProjectLanguages, userLanguagesMap);

	if (CollectionUtils.isEmpty(removableLanguages)) {
	    return removableLanguages;
	}

	List<String> deletedLangIds = getDeletedLangIds(incomingProjectLanguages, projectId);
	/*
	 * ****************************************************************************
	 * Note:If user is assigned on two languages, and one of them should be deleted
	 * then the other language should not be removable | TERII-5732.
	 * ****************************************************************************
	 */
	if (CollectionUtils.isNotEmpty(deletedLangIds)) {
	    String deletedLanguageId = deletedLangIds.get(0);
	    updateRemovableLanguagesList(removableLanguages, deletedLanguageId, userLanguagesMap);
	}

	return removableLanguages;
    }

    private CacheGatewaySessionUpdaterService getCacheGatewaySessionUpdaterService() {
	return _cacheGatewaySessionUpdaterService;
    }

    private List<String> getDeletedLangIds(List<ProjectLanguage> incomingProjectLanguages, Long projectId) {
	List<ProjectLanguage> currentProjectLanguages = getProjectService().getProjectLanguages(projectId);
	currentProjectLanguages.removeAll(incomingProjectLanguages);

	return currentProjectLanguages.stream().map(ProjectLanguage::getLanguage).collect(Collectors.toList());
    }

    private boolean isLanguageNonRemovable(String deletedLangId, String removableLang, List<String> userLanguages) {
	return userLanguages.size() == 2 && userLanguages.contains(deletedLangId)
		&& userLanguages.contains(removableLang);
    }

    private List<String> languagesWithNoTerms(Long projectId, List<ProjectLanguage> projectLanguages) {
	List<String> languagesWithNoTerms = new ArrayList<>();

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setProjectIds(Collections.singletonList(projectId));

	List<String> languages = projectLanguages.stream().map(ProjectLanguage::getLanguage)
		.collect(Collectors.toList());
	filter.setTargetLanguages(languages);

	FacetTermCounts facetTermCounts = getTermEntryService().searchFacetTermCounts(filter);

	Map<String, FacetTermCounts.LanguageTermCount> termCountByLanguage = facetTermCounts.getTermCountByLanguage();

	languages.forEach(language -> {
	    FacetTermCounts.LanguageTermCount languageTermCount = termCountByLanguage.get(language);
	    if (languageTermCount.getTermCount() == 0) {
		languagesWithNoTerms.add(language);
	    }
	});
	return languagesWithNoTerms;
    }

    private List<String> removableLanguages(Long projectId, List<ProjectLanguage> projectLanguages,
	    Map<Long, List<String>> userLanguagesMap) {
	if (projectLanguages.size() == 1) {
	    return Collections.emptyList();
	}

	List<String> removableLanguages = languagesWithNoTerms(projectId, projectLanguages);

	if (CollectionUtils.isEmpty(removableLanguages)) {
	    return Collections.emptyList();
	}
	/*
	 * If only one language is assigned to user then that language is not removable
	 * TERII-5732.
	 */
	removeOnlyOneAssignedLang(removableLanguages, userLanguagesMap);

	return removableLanguages;
    }

    private void removeOnlyOneAssignedLang(List<String> removableLanguages, Map<Long, List<String>> userLanguagesMap) {
	for (List<String> userLanguages : userLanguagesMap.values()) {
	    if (userLanguages.size() == 1) {
		removableLanguages.remove(userLanguages.get(0));
	    }
	}
    }

    private void updateGenericUserLanguages(Long projectId, List<String> langs) {
	List<Long> userIds = new ArrayList<Long>();

	Set<TmUserProfile> genericProjectUsers = getProjectService().getGenericProjectUsers(projectId);
	for (TmUserProfile tmUserProfile : genericProjectUsers) {
	    userIds.add(tmUserProfile.getUserProfileId());
	}

	for (TmUserProfile tmUserProfile : genericProjectUsers) {
	    Long userProfileId = tmUserProfile.getUserProfileId();
	    getProjectService().addOrUpdateProjectUserLanguages(userProfileId, projectId, langs, userIds, Boolean.TRUE);

	}
    }

    private void updateRemovableLanguagesList(List<String> removableLanguages, String deletedLangId,
	    Map<Long, List<String>> userLanguagesMap) {
	Iterator<String> iterator = removableLanguages.iterator();

	while (iterator.hasNext()) {
	    String removableLang = iterator.next();
	    for (List<String> userLanguages : userLanguagesMap.values()) {
		if (isLanguageNonRemovable(deletedLangId, removableLang, userLanguages)) {
		    iterator.remove();
		    break;
		}
	    }
	}
    }

    private void validateCommand(ProjectCommand command) {
	Validate.notEmpty(command.getProjectLanguages(), Messages.getString("ProjectCommandError.0"));
    }

    @Override
    protected Boolean getSharePendingTerms(ProjectCommand command) {
	return command.getSharePendingTerms();
    }
}
