package org.gs4tr.termmanager.service.impl;

import static org.gs4tr.foundation.modules.entities.model.UserTypeEnum.POWER_USER;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.dao.GenericDao;
import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.ProjectInfo;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.entities.model.search.ProjectSearchRequest;
import org.gs4tr.foundation.modules.project.service.impl.BaseProjectServiceImpl;
import org.gs4tr.foundation.modules.security.model.Policy;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.termmanager.dao.OrganizationDAO;
import org.gs4tr.termmanager.dao.ProjectDAO;
import org.gs4tr.termmanager.dao.ProjectDetailDAO;
import org.gs4tr.termmanager.dao.ProjectLanguageDAO;
import org.gs4tr.termmanager.dao.ProjectLanguageDetailDAO;
import org.gs4tr.termmanager.dao.ProjectLanguageUserDetailDAO;
import org.gs4tr.termmanager.dao.ProjectSearchDAO;
import org.gs4tr.termmanager.dao.ProjectUserLanguageDAO;
import org.gs4tr.termmanager.dao.StatisticsDAO;
import org.gs4tr.termmanager.dao.UserProfileDAO;
import org.gs4tr.termmanager.dao.UserProjectRoleSearchDAO;
import org.gs4tr.termmanager.model.Attribute;
import org.gs4tr.termmanager.model.AttributeLevelEnum;
import org.gs4tr.termmanager.model.BaseTypeEnum;
import org.gs4tr.termmanager.model.EntityTypeHolder;
import org.gs4tr.termmanager.model.InputFieldTypeEnum;
import org.gs4tr.termmanager.model.ProjectDetail;
import org.gs4tr.termmanager.model.ProjectLanguage;
import org.gs4tr.termmanager.model.ProjectLanguageDetail;
import org.gs4tr.termmanager.model.ProjectLanguageUserDetail;
import org.gs4tr.termmanager.model.ProjectMetadata;
import org.gs4tr.termmanager.model.ProjectMetadataRequest;
import org.gs4tr.termmanager.model.ProjectPolicyEnum;
import org.gs4tr.termmanager.model.ProjectUserLanguage;
import org.gs4tr.termmanager.model.Statistics;
import org.gs4tr.termmanager.model.TermEntryAttributeTypeEnum;
import org.gs4tr.termmanager.model.TmOrganization;
import org.gs4tr.termmanager.model.TmPolicyEnum;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UserProjectRole;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.service.lock.manager.ExclusiveWriteLockManager;
import org.gs4tr.termmanager.service.utils.AdminTasksHolderHelper;
import org.gs4tr.termmanager.service.utils.MailHelper.ReportType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("projectService")
public class ProjectServiceImpl extends BaseProjectServiceImpl<TmProject> implements ProjectService {

    private static final Log LOGGER = LogFactory.getLog(ProjectServiceImpl.class);

    @Autowired
    private ExclusiveWriteLockManager _exclusiveWriteLockManager;

    @Autowired
    private OrganizationDAO _organizationDAO;

    @Autowired
    private ProjectDAO _projectDAO;

    @Autowired
    private ProjectDetailDAO _projectDetailDAO;

    @Autowired
    private ProjectLanguageDAO _projectLanguageDAO;

    @Autowired
    private ProjectLanguageDetailDAO _projectLanguageDetailDAO;

    @Autowired
    private ProjectLanguageUserDetailDAO _projectLanguageUserDetailDAO;

    @Autowired
    private ProjectSearchDAO _projectSearchDAO;

    @Autowired
    private ProjectUserLanguageDAO _projectUserLanguageDAO;

    @Autowired
    private StatisticsDAO _statisticsDAO;

    @Autowired
    private AdminTasksHolderHelper _tasksHolderHelper;

    @Autowired
    private UserProfileDAO _userProfileDAO;

    @Autowired
    private UserProfileService _userProfileService;

    @Autowired
    private UserProjectRoleSearchDAO _userProjectRoleSearchDAO;

    @Override
    @Transactional
    public Long addNewLanguagesOnImport(Long projectId, List<ProjectLanguage> newProjectLanguages) {

	TmUserProfile currentUserProfile = TmUserProfile.getCurrentUserProfile();
	Long userProfileId = currentUserProfile.getUserProfileId();
	TmUserProfile userProfile = getUserProfileDAO().load(currentUserProfile.getUserProfileId());

	Set<ProjectLanguage> projectLanguages = new HashSet<ProjectLanguage>(
		getProjectLanguageDAO().findByProjectId(projectId));

	for (ProjectLanguage proLang : newProjectLanguages) {
	    String languageId = proLang.getLanguage();

	    // adding languages to refresh user in spring context
	    Set<String> currentProjectUserLanguages = currentUserProfile.getProjectUserLanguages().get(projectId);
	    if (currentProjectUserLanguages == null) {
		currentProjectUserLanguages = new HashSet<>();
		currentUserProfile.getProjectUserLanguages().put(projectId, currentProjectUserLanguages);
	    }

	    currentProjectUserLanguages.add(languageId);

	    projectLanguages.add(proLang);
	}

	getUserProfileDAO().update(userProfile);

	Set<String> incomingLanguageLocales = new HashSet<String>();
	for (ProjectLanguage projectLanguage : projectLanguages) {
	    String languageId = projectLanguage.getLanguage();
	    incomingLanguageLocales.add(languageId);
	}
	addOrUpdateProjectLanguages(projectId, new ArrayList<>(projectLanguages));

	// details
	addOrUpdateProjectLanguageDetail(projectId, incomingLanguageLocales);

	List<TmUserProfile> users = getUserProjectRoleSearchDAO().getUsersByProject(projectId);
	List<Long> userIds = new ArrayList<Long>();

	for (TmUserProfile tmUserProfile : users) {
	    userIds.add(tmUserProfile.getUserProfileId());
	}

	List<String> projectUserLanguages = getProjectUserLanguageCodes(projectId, userProfileId);

	for (ProjectLanguage projectLanguage : newProjectLanguages) {
	    String projectLanguageCode = projectLanguage.getLanguage();
	    if (!projectUserLanguages.contains(projectLanguageCode)) {
		projectUserLanguages.add(projectLanguageCode);
	    }
	}

	addOrUpdateProjectUserLanguages(userProfileId, projectId, projectUserLanguages, userIds, null);

	List<TmUserProfile> genericUsers = getUserProjectRoleSearchDAO().getGenericUsersByProject(projectId);

	if (CollectionUtils.isNotEmpty(genericUsers)) {
	    List<Long> genericUserIds = new ArrayList<Long>();
	    for (TmUserProfile genericUser : genericUsers) {
		genericUserIds.add(genericUser.getUserProfileId());
	    }

	    List<String> genericProjectUserLanguages = new ArrayList<String>(incomingLanguageLocales);

	    for (Long genericUserId : genericUserIds) {
		addOrUpdateProjectUserLanguages(genericUserId, projectId, genericProjectUserLanguages, genericUserIds,
			Boolean.TRUE);
	    }
	}

	return userProfileId;
    }

    @Override
    @Transactional
    public Long addOrUpdateProjectAttributes(Long projectId, List<Attribute> incomingAttributes) {

	TmProject project = load(projectId);

	List<Attribute> existingAttributes = project.getAttributes();

	List<Attribute> attributeForRemoval = new ArrayList<Attribute>();

	// remove attributes
	for (Attribute existingAttribute : existingAttributes) {
	    if (!containsAttribute(incomingAttributes, existingAttribute)) {
		attributeForRemoval.add(existingAttribute);
	    }
	}

	existingAttributes.removeAll(attributeForRemoval);

	// add new attributes
	for (Attribute incomingAttribute : incomingAttributes) {
	    if (!containsAttribute(existingAttributes, incomingAttribute)) {
		existingAttributes.add(incomingAttribute);
	    }
	}

	// rename attributes
	for (Attribute incomingAttribute : incomingAttributes) {
	    String renameValue = incomingAttribute.getRenameValue();
	    if (StringUtils.isEmpty(renameValue)) {
		continue;
	    }

	    for (Attribute existingAttribute : existingAttributes) {
		if (existingAttribute.equalsCustom(incomingAttribute)) {
		    existingAttribute.setName(renameValue);
		}
	    }
	}

	update(project);

	return projectId;
    }

    @Override
    @Transactional
    public Long addOrUpdateProjectAttributesOnImport(Long projectId,
	    Map<AttributeLevelEnum, Set<String>> incomingProjectAttributes, boolean ignoreCase) {
	TmProject project = load(projectId);

	for (Map.Entry<AttributeLevelEnum, Set<String>> entry : incomingProjectAttributes.entrySet()) {
	    Set<String> newAttributes = entry.getValue();

	    if (CollectionUtils.isNotEmpty(newAttributes)) {
		addOrUpdateAttributesInternal(project, entry.getKey(), newAttributes, ignoreCase);
	    }
	}

	update(project);

	return projectId;
    }

    @Override
    @Transactional
    public void addOrUpdateProjectLanguageDetail(Long projectId, Set<String> incomingLanguages) {
	ProjectDetail projectDetail = getProjectDetailDAO().findByProjectId(projectId);

	Set<ProjectLanguageDetail> existingLanguageDetails = projectDetail.getLanguageDetails();
	List<ProjectLanguageDetail> languageDetailsForRemoval = new ArrayList<ProjectLanguageDetail>();
	if (CollectionUtils.isNotEmpty(existingLanguageDetails)) {
	    for (ProjectLanguageDetail languageDetail : existingLanguageDetails) {
		String languageId = languageDetail.getLanguageId();
		if (!incomingLanguages.contains(languageId)) {
		    languageDetailsForRemoval.add(languageDetail);
		}
	    }
	    existingLanguageDetails.removeAll(languageDetailsForRemoval);
	    for (ProjectLanguageDetail languageDetail : languageDetailsForRemoval) {
		if (LOGGER.isDebugEnabled()) {
		    LOGGER.debug(String.format(Messages.getString("ProjectServiceImpl.7"), //$NON-NLS-1$
			    projectId, languageDetail.getLanguageId()));
		}
		// getProjectLanguageDetailDAO().delete(languageDetail);
		getProjectLanguageDetailDAO().updatePropertyValue(languageDetail.getProjectLanguageDetailId(),
			"disabled", Boolean.TRUE); //$NON-NLS-1$
	    }
	}

	for (String languageId : incomingLanguages) {
	    ProjectLanguageDetail newLanguageDetail = new ProjectLanguageDetail(languageId, projectDetail);
	    if (CollectionUtils.isEmpty(existingLanguageDetails)
		    || !existingLanguageDetails.contains(newLanguageDetail)) {
		if (LOGGER.isDebugEnabled()) {
		    LOGGER.debug(String.format(Messages.getString("ProjectServiceImpl.8"), //$NON-NLS-1$
			    projectId, languageId));
		}

		newLanguageDetail = getProjectLanguageDetailDAO().save(newLanguageDetail);
	    } else if (existingLanguageDetails.contains(newLanguageDetail)) {
		for (ProjectLanguageDetail existingLanguageDetail : existingLanguageDetails) {
		    if (existingLanguageDetail.equals(newLanguageDetail) && existingLanguageDetail.isDisabled()) {
			if (LOGGER.isDebugEnabled()) {
			    LOGGER.debug(String.format(Messages.getString("ProjectServiceImpl.11"), //$NON-NLS-1$
				    projectId, existingLanguageDetail.getLanguageId()));
			}
			getProjectLanguageDetailDAO().updatePropertyValue(
				existingLanguageDetail.getProjectLanguageDetailId(), "disabled", Boolean.FALSE); //$NON-NLS-1$
		    }
		}
	    }
	}

	projectDetail = getProjectDetailDAO().load(projectDetail.getProjectDetailId());

	existingLanguageDetails = projectDetail.getLanguageDetails();
	long languageCount = (existingLanguageDetails != null) ? existingLanguageDetails.size() : 0;

	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug(String.format(Messages.getString("ProjectServiceImpl.10"), //$NON-NLS-1$
		    projectId, languageCount));
	}
	getProjectDetailDAO().updatePropertyValue(projectDetail.getProjectDetailId(), "languageCount", //$NON-NLS-1$
		languageCount);
    }

    @Override
    @Transactional
    public Long addOrUpdateProjectLanguages(Long projectId, List<ProjectLanguage> incomingProjectLanguages) {

	TmProject project = findById(projectId);

	Set<ProjectLanguage> existingProjectLanguages = project.getProjectLanguages();
	List<ProjectLanguage> languagesForRemoval = new ArrayList<ProjectLanguage>();

	Set<String> languageCodesForRemoval = new HashSet<String>();

	for (ProjectLanguage existingProjectLanguage : existingProjectLanguages) {
	    ProjectLanguage matchLanguage = findProjectLanguageByLanguage(incomingProjectLanguages,
		    existingProjectLanguage.getLanguage());

	    if (matchLanguage == null) {
		languagesForRemoval.add(existingProjectLanguage);
		languageCodesForRemoval.add(existingProjectLanguage.getLanguage());
	    } else if (!matchLanguage.equals(existingProjectLanguage)) {
		existingProjectLanguage.setDefault(matchLanguage.isDefault());
		getProjectLanguageDAO().update(existingProjectLanguage);
	    }
	}

	existingProjectLanguages.removeAll(languagesForRemoval);
	for (ProjectLanguage projectLanguage : languagesForRemoval) {
	    getProjectLanguageDAO().delete(projectLanguage);
	}
	unlinkProjectUserLanguagesByProject(projectId, languageCodesForRemoval);

	for (ProjectLanguage incomingProjectLanguage : incomingProjectLanguages) {
	    if (!existingProjectLanguages.contains(incomingProjectLanguage)) {
		if (findProjectLanguageByLanguage(existingProjectLanguages,
			incomingProjectLanguage.getLanguage()) == null) {
		    existingProjectLanguages.add(incomingProjectLanguage);
		    incomingProjectLanguage.setProject(project);
		    getProjectLanguageDAO().save(incomingProjectLanguage);
		}
	    }
	}

	update(project);

	return projectId;
    }

    @Override
    @Transactional
    public void addOrUpdateProjectLanguagesForRecodeOrClone(Long projectId,
	    List<ProjectLanguage> incomingProjectLanguages) {
	TmProject project = findById(projectId);

	Set<ProjectLanguage> existingProjectLanguages = project.getProjectLanguages();
	List<ProjectLanguage> languagesForRemoval = new ArrayList<ProjectLanguage>();

	Set<String> languageCodesForRemoval = new HashSet<String>();

	for (ProjectLanguage existingProjectLanguage : existingProjectLanguages) {
	    ProjectLanguage matchLanguage = findProjectLanguageByLanguage(incomingProjectLanguages,
		    existingProjectLanguage.getLanguage());

	    if (matchLanguage == null) {
		languagesForRemoval.add(existingProjectLanguage);
		languageCodesForRemoval.add(existingProjectLanguage.getLanguage());
	    } else if (!matchLanguage.equals(existingProjectLanguage)) {
		existingProjectLanguage.setDefault(matchLanguage.isDefault());
		getProjectLanguageDAO().update(existingProjectLanguage);
	    }
	}

	existingProjectLanguages.removeAll(languagesForRemoval);
	for (ProjectLanguage projectLanguage : languagesForRemoval) {
	    getProjectLanguageDAO().delete(projectLanguage);
	}
	unlinkProjectUserLanguagesByProject(projectId, languageCodesForRemoval);

	for (ProjectLanguage incomingProjectLanguage : incomingProjectLanguages) {
	    if (!existingProjectLanguages.contains(incomingProjectLanguage)) {
		if (findProjectLanguageByLanguage(existingProjectLanguages,
			incomingProjectLanguage.getLanguage()) == null) {
		    existingProjectLanguages.add(incomingProjectLanguage);
		    incomingProjectLanguage.setProject(project);
		    getProjectLanguageDAO().save(incomingProjectLanguage);
		}
	    }
	}

	update(project);
    }

    @Override
    @Transactional
    public Long addOrUpdateProjectNotesOnImport(Long projectId, Set<String> incomingProjectNotes, boolean ignoreCase) {
	TmProject project = load(projectId);

	List<Attribute> attributes = project.getAttributes();
	if (attributes == null) {
	    attributes = new ArrayList<Attribute>();
	    project.setAttributes(attributes);
	}

	List<Attribute> existingProjectNotes = project.getNotes();

	for (String incomingProjectNote : incomingProjectNotes) {
	    Attribute existingProjectNote = findNote(incomingProjectNote, existingProjectNotes, ignoreCase);
	    if (existingProjectNote == null) {
		attributes.add(getDefaultProjectNote(incomingProjectNote));
	    }
	}

	update(project);

	return projectId;
    }

    @Override
    @Transactional
    public Long addOrUpdateProjectUserLanguages(Long userId, Long projectId, List<String> languages, List<Long> userIds,
	    Boolean isGeneric) {

	if (languages == null) {
	    List<ProjectUserLanguage> projectUserLanguages = getProjectUserLanguageDAO()
		    .getProjectUserLanguages(projectId, userId);
	    for (ProjectUserLanguage puLanguage : projectUserLanguages) {
		getProjectUserLanguageDAO().delete(puLanguage);
	    }
	    return userId;
	}

	List<ProjectUserLanguage> projectUserLanguages = getProjectUserLanguageDAO()
		.getProjectUserLanguagesByProject(projectId);

	List<String> languagesList = new ArrayList<String>();
	languagesList.addAll(languages);

	List<String> languagesForSkipFromCreation = new ArrayList<String>();

	for (ProjectUserLanguage puLanguage : projectUserLanguages) {

	    String languageId = puLanguage.getLanguage();

	    TmUserProfile user = puLanguage.getUser();

	    if (isGeneric != null && !isGeneric.equals(user.getGeneric())) {
		continue;
	    }

	    Long puUserId = user.getUserProfileId();

	    if (!userIds.contains(puUserId)) {
		getProjectUserLanguageDAO().delete(puLanguage);
	    } else {
		if (userId.equals(puUserId)) {
		    if (!languagesList.contains(languageId)) {
			getProjectUserLanguageDAO().delete(puLanguage);
		    } else {
			languagesForSkipFromCreation.add(languageId);
		    }
		}
	    }
	}

	languagesList.removeAll(languagesForSkipFromCreation);
	TmUserProfile user = getUserProfileDAO().load(userId);

	TmProject project = getProjectDAO().load(projectId);

	List<ProjectLanguageDetail> languageDetails = getProjectLanguageDetailDAO()
		.getProjectLanguageDetailsByProjectId(projectId);

	for (String language : languagesList) {
	    // skip projectUserLanguage creation for power user
	    if (!user.isPowerUser()) {
		ProjectUserLanguage newProjectUserLanguage = new ProjectUserLanguage(project, user, language);
		getProjectUserLanguageDAO().save(newProjectUserLanguage);

		getStatisticsDAO().addOrUpdateProjectUserLangStatistics(newProjectUserLanguage,
			ReportType.WEEKLY.getName(), ReportType.DAILY.getName());

	    }

	    // create new project language user details
	    for (ProjectLanguageDetail languageDetail : languageDetails) {
		String languageId = languageDetail.getLanguageId();
		if (languageId.equals(language)) {
		    Set<ProjectLanguageUserDetail> userDetails = languageDetail.getUserDetails();
		    ProjectLanguageUserDetail projectLanguageUserDetail = new ProjectLanguageUserDetail(languageDetail,
			    user);
		    if (userDetails == null || !userDetails.contains(projectLanguageUserDetail)) {
			getProjectLanguageUserDetailDAO().save(projectLanguageUserDetail);
			if (userDetails == null) {
			    userDetails = new HashSet<ProjectLanguageUserDetail>();
			    languageDetail.setUserDetails(userDetails);
			}
			userDetails.add(projectLanguageUserDetail);
			break;
		    }
		}
	    }

	}

	return projectId;
    }

    @Override
    @Transactional(readOnly = true)
    public void checkIfProjectsAreDisabled(List<Long> projectIds) {
	if (getProjectDAO().checkIfProjectsAreDisabled(projectIds)) {
	    throw new UserException(Messages.getString("ProjectServiceImpl.12"));
	}
    }

    @Override
    @Transactional
    public void clearStatistics(Collection<Long> statisticsIds) {
	if (CollectionUtils.isEmpty(statisticsIds)) {
	    return;
	}
	getStatisticsDAO().clearUserStatistics(statisticsIds);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean containsGenericProjectUsers(List<Long> projectIds) {
	for (Long projectId : projectIds) {
	    List<TmUserProfile> genericUsers = getUserProjectRoleSearchDAO().getGenericUsersByProject(projectId);
	    if (CollectionUtils.isEmpty(genericUsers)) {
		return false;
	    }
	}

	return true;
    }

    @Transactional
    @Override
    public Long createProject(ProjectInfo projectInfo) {
	return super.createProject(projectInfo);
    }

    @Transactional
    @Override
    public Long enableProject(Long projectId) {
	TmProject project = load(projectId);
	ProjectInfo projectInfo = project.getProjectInfo();
	projectInfo.setEnabled(!projectInfo.isEnabled());

	return updateProject(projectId, projectInfo);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Long> findAllDisabledProjectIds() {
	return getProjectDAO().findAllDisabledProjectIds();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> findAllEnabledProjectIds() {
	return getProjectDAO().findAllEnabledProjectIds();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TmProject> findAllProjectsFetchLanguages() {
	List<TmProject> projects = getProjectDAO().findAll();
	for (TmProject project : projects) {
	    getProjectDAO().initializeProjectLanguages(project);
	}
	return projects;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, List<Attribute>> findAttributesByProjectId(List<Long> projectIds) {
	return getProjectDAO().findAttributesByProjectIds(projectIds);
    }

    @Override
    @Transactional(readOnly = true)
    public TmProject findProjectById(Long projectId, Class<?>... classes) {
	return getProjectDAO().findProjectById(projectId, classes);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TmProject> findProjectByIds(List<Long> projectIds) {
	return getProjectDAO().findProjectsByIds(projectIds);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Long> findProjectIdsByShortCodes(List<String> shortCodes) {
	return getProjectDAO().findProjectIdsByShortCodes(shortCodes);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectUserLanguage> findProjectUserLanguageByProjectId(Long projectId) {
	return getProjectUserLanguageDAO().getProjectUserLanguagesByProject(projectId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectUserLanguage> findProjectUserLanguageByProjectIds(List<Long> projectIds,
	    Class<?>... classesToFetch) {
	return getProjectUserLanguageDAO().findByProjectIds(projectIds, classesToFetch);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Long> findProjectsByNameLike(String nameLike) {
	return getProjectDAO().findProjectsByNameLike(nameLike);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Attribute> getAttributesByProjectId(Long projectId) {
	return getProjectDAO().getAttributesByProjectId(projectId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TmProject> getCurrentUserProjects() {
	List<TmProject> projects = new ArrayList<TmProject>();

	TmOrganization organization = TmUserProfile.getCurrentUserProfile().getOrganization();
	if (organization != null) {
	    Long organizationId = organization.getOrganizationId();
	    projects = getOrganizationDAO().findOrganizationProjects(organizationId);
	}

	CollectionUtils.filter(projects, new Predicate() {
	    @Override
	    public boolean evaluate(Object item) {
		TmProject project = (TmProject) item;
		if (project != null && project.getProjectInfo() != null
			&& project.getProjectInfo().isEnabled() != null) {
		    return project.getProjectInfo().isEnabled();
		}

		return false;
	    }
	});

	return projects;
    }

    @Override
    @Transactional(readOnly = true)
    public Set<TmUserProfile> getGenericProjectUsers(Long projectId) {

	HashSet<TmUserProfile> genericUser = new HashSet<TmUserProfile>();

	List<TmUserProfile> users = getUserProjectRoleSearchDAO().getUsersByProject(projectId);
	if (CollectionUtils.isNotEmpty(users)) {
	    for (TmUserProfile tmUserProfile : users) {
		if (tmUserProfile.getGeneric()) {
		    genericUser.add(tmUserProfile);
		}
	    }
	}

	return genericUser;

    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Set<TmUserProfile>> getLanguageAssignees(Long projectId, String sourceLanguage) {
	List<ProjectUserLanguage> puls = getProjectUserLanguageDAO().getProjectUserLanguagesByProject(projectId);

	List<UserProjectRole> userProjectRoles = getUserProjectRoleSearchDAO().getProjectUserRolesByProject(projectId);

	Policy addApprovedTermPolicy = new Policy();
	addApprovedTermPolicy.setPolicyId(ProjectPolicyEnum.POLICY_TM_TERM_ADD_APPROVED_TERM.name());
	Policy approveTermStatusPolicy = new Policy();
	approveTermStatusPolicy.setPolicyId(ProjectPolicyEnum.POLICY_TM_TERM_APPROVE_TERM_STATUS.name());

	Policy translatorViewInboxPolicy = new Policy();
	translatorViewInboxPolicy.setPolicyId(TmPolicyEnum.POLICY_TM_VIEW_TRANSLATOR_INBOX.name());
	Policy autoSavePolicy = new Policy();
	autoSavePolicy.setPolicyId(ProjectPolicyEnum.POLICY_TM_TERM_AUTO_SAVE_TRANSLATION.name());

	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();

	Set<String> submitterLanguages = new HashSet<String>();

	Map<TmUserProfile, List<String>> usersLanguagesMap = new HashMap<TmUserProfile, List<String>>();
	for (ProjectUserLanguage pul : puls) {
	    String language = pul.getLanguage();
	    TmUserProfile user = pul.getUser();

	    if (userProfile.isPowerUser() || user.getUserProfileId().equals(userProfile.getUserProfileId())) {
		submitterLanguages.add(language);
	    }

	    List<String> userLanguages = usersLanguagesMap.get(user);

	    if (userLanguages == null) {
		userLanguages = new ArrayList<String>();
		usersLanguagesMap.put(user, userLanguages);
	    }

	    userLanguages.add(language);
	}

	/* User is valid if it have source language and if it's not disabled */
	List<Long> valideUsersId = new ArrayList<Long>();
	for (Map.Entry<TmUserProfile, List<String>> entry : usersLanguagesMap.entrySet()) {
	    if (entry.getValue().contains(sourceLanguage) && entry.getKey().getUserInfo().isEnabled()) {
		valideUsersId.add(entry.getKey().getIdentifier());
	    }
	}

	Map<String, Set<TmUserProfile>> languageUserMap = new HashMap<String, Set<TmUserProfile>>();

	if (CollectionUtils.isNotEmpty(puls)) {
	    for (ProjectUserLanguage pul : puls) {
		String languageId = pul.getLanguage();
		TmUserProfile user = pul.getUser();
		if (!valideUsersId.contains(user.getIdentifier())) {
		    continue;
		}

		if (!submitterLanguages.contains(languageId)) {
		    continue;
		}

		Set<Role> systemRoles = user.getSystemRoles();

		for (Role role : systemRoles) {
		    Set<Policy> policies = role.getPolicies();
		    // check if user is translator
		    if (policies.contains(translatorViewInboxPolicy)) {
			Set<TmUserProfile> users = languageUserMap.get(languageId);
			if (users == null) {
			    users = new HashSet<TmUserProfile>();
			    languageUserMap.put(languageId, users);
			}

			for (UserProjectRole userProjectRole : userProjectRoles) {
			    if (user.equals(userProjectRole.getUserProfile())) {
				Set<Policy> projectPolicies = userProjectRole.getRole().getPolicies();
				if (projectPolicies.contains(autoSavePolicy)) {
				    users.add(user);
				    break;
				}
			    }
			}
		    }
		}
	    }
	}

	return languageUserMap;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Set<TmUserProfile>> getLanguagePowerUsers(Long projectId) {
	Map<String, Set<TmUserProfile>> languageUserMap = new HashMap<String, Set<TmUserProfile>>();
	List<ProjectLanguage> projectLanguages = getProjectLanguageDAO().findByProjectId(projectId);
	// this should not open new transaction (by default)
	List<TmUserProfile> powerUsers = getUserProfileService().findUsersByType(POWER_USER);
	Set<TmUserProfile> poweUsersSet = new HashSet<TmUserProfile>(powerUsers);

	// if power user is not enabled remove it
	Iterator<TmUserProfile> iterator = poweUsersSet.iterator();
	while (iterator.hasNext()) {
	    TmUserProfile userProfile = iterator.next();
	    if (!userProfile.getUserInfo().isEnabled()) {
		iterator.remove();
	    }
	}

	// Don't add languages not supported by submitter
	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();
	Map<Long, Set<String>> projectUserLanguages = userProfile.getProjectUserLanguages();
	Set<String> languages = projectUserLanguages.get(projectId);

	if (CollectionUtils.isNotEmpty(powerUsers)) {
	    for (ProjectLanguage projectLanguage : projectLanguages) {
		String languageId = projectLanguage.getLanguage();
		if (languages.contains(languageId)) {
		    languageUserMap.put(languageId, poweUsersSet);
		}
	    }
	}
	return languageUserMap;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getOrganizationIdByProject(Long projectId) {
	return getProjectDAO().getOrganizationIdByProject(projectId);
    }

    @Override
    public ProjectDAO getProjectDAO() {
	return _projectDAO;
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> getProjectLanguageCodes(List<Long> projectIds) {
	Set<String> langs = new HashSet<String>();

	List<ProjectLanguage> projectLanguages = getProjectLanguageDAO().findByProjectIds(projectIds);

	if (CollectionUtils.isNotEmpty(projectLanguages)) {
	    for (ProjectLanguage proLang : projectLanguages) {
		langs.add(proLang.getLanguage());
	    }
	}

	return langs;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getProjectLanguageCodes(Long projectId) {
	List<ProjectLanguage> projectLanguages = getProjectDAO().findProjectLanguagesByProjectId(projectId);

	List<String> langs = null;
	if (projectLanguages != null) {
	    langs = new ArrayList<String>();
	    for (ProjectLanguage proLang : projectLanguages) {
		if (proLang.getLanguage() != null) {
		    langs.add(proLang.getLanguage());
		}
	    }
	}

	return langs;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectLanguage> getProjectLanguages(Long projectId) {
	return getProjectDAO().findProjectLanguagesByProjectId(projectId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectLanguage> getProjectLanguagesByProjectId(Long projectId) {
	return getProjectLanguageDAO().findByProjectId(projectId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectLanguage> getProjectLanguagesForRecodeOrClone(Long projectId) {
	return getProjectDAO().findProjectLanguagesByProjectId(projectId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectMetadata> getProjectMetadata(ProjectMetadataRequest request) {
	return getProjectDAO().getProjectMetadata(request);
    }

    @Override
    public ProjectSearchDAO getProjectSearchDAO() {
	return _projectSearchDAO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getProjectUserLanguageCodes(Long projectId, Long userId) {
	List<String> projectLanguages = new ArrayList<String>();

	List<ProjectUserLanguage> projectUserLanguages = getProjectUserLanguageDAO().getProjectUserLanguages(projectId,
		userId);

	for (ProjectUserLanguage projectUserLanguage : projectUserLanguages) {
	    String language = projectUserLanguage.getLanguage();
	    projectLanguages.add(language);
	}

	return projectLanguages;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectUserLanguage> getProjectUserLanguages(Long userId) {
	return getProjectUserLanguageDAO().getProjectUserLanguagesByUser(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectUserLanguage> getProjectUserLanguages(Long projectId, Long userId) {
	return getProjectUserLanguageDAO().getProjectUserLanguages(projectId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<TmUserProfile> getProjectUsers(Long projectId) {

	List<TmUserProfile> users = getUserProjectRoleSearchDAO().getUsersByProject(projectId);

	return new HashSet<TmUserProfile>(users);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TmUserProfile> getProjectUsersByLanguageIds(Long projectId, List<ProjectLanguage> projectLanguages) {
	List<String> languageIds = projectLanguages.stream().map(ProjectLanguage::getLanguage)
		.collect(Collectors.toList());

	return getProjectUserLanguageDAO().getProjectUsersByLanguageIds(projectId, languageIds);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<TmUserProfile> getProjectUsersForRecodeOrClone(Long projectId) {
	List<TmUserProfile> users = getUserProjectRoleSearchDAO().getUsersByProject(projectId);

	return new HashSet<>(users);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getRoleIdsByUserAndProject(Long projectId, Long userId) {
	return getUserProjectRoleSearchDAO().getRoleIdsByUserAndProject(userId, projectId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getRolesByUserAndProject(Long projectId, Long userId) {
	return getUserProjectRoleSearchDAO().getRolesByUserAndProject(userId, projectId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Statistics> getStatisticsByProjectId(Long projectId) {
	return getStatisticsDAO().getStatisticsByProjectId(projectId);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, List<String>> getUserLanguagesMap(Long projectId) {
	return getProjectUserLanguageDAO().getUserLanguagesMap(projectId);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, List<Statistics>> getUserProjectStatistics(Long userId, String reportType) {
	Map<String, List<Statistics>> projectStatistics = new HashMap<String, List<Statistics>>();

	List<Statistics> sts = getUserStatistics(userId, reportType);
	if (CollectionUtils.isNotEmpty(sts)) {
	    for (Statistics st : sts) {
		ProjectUserLanguage projectUserLanguage = st.getProjectUserLanguage();
		String languageId = projectUserLanguage.getLanguage();
		String projectName = projectUserLanguage.getProject().getProjectInfo().getName();

		List<Statistics> list = projectStatistics.get(projectName);
		if (list == null) {
		    list = new ArrayList<Statistics>();
		    projectStatistics.put(projectName, list);
		}
		st.setLanguageId(languageId);
		list.add(st);
	    }
	}

	return projectStatistics;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TmProject> getUserProjects(Long userId, Class<?>... classesToFetch) {

	TmUserProfile user = TmUserProfile.getCurrentUserProfile();
	if (user.isPowerUser()) {
	    return getProjectDAO().findAllEnabledProjects(classesToFetch);
	} else {
	    return getProjectUserLanguageDAO().getAllUserProjects(userId, classesToFetch);
	}
    }

    @Override
    @Transactional(readOnly = true)
    public List<Statistics> getUserStatistics(Long userId, String reportType) {
	return getStatisticsDAO().getStatisticsByUserId(userId, reportType);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskPagedList<TmProject> search(ProjectSearchRequest command, PagedListInfo pagedListInfo) {
	PagedList<TmProject> entityPagedList = super.search(command, pagedListInfo);

	return getTasksHolderHelper().assignTasks(entityPagedList, null, EntityTypeHolder.PROJECT);
    }

    @Override
    @Value("#{projectDAO}")
    public void setEntityDao(GenericDao<TmProject, Long> entityDao) {
	super.setEntityDao(entityDao);
    }

    @Transactional
    @Override
    public boolean updateProjectProperties(Long projectId, Map<String, Object> updateCommand) {
	return getProjectDAO().updateProperties(projectId, updateCommand);
    }

    private void addOrUpdateAttributesInternal(TmProject project, AttributeLevelEnum termentryLeve,
	    Set<String> newAttributes, boolean ignoreCase) {
	List<Attribute> descriptions = project.getDescriptions();

	for (String incommingProjectNote : newAttributes) {

	    List<Attribute> attributes = project.getAttributes();
	    if (attributes == null) {
		project.setAttributes(new ArrayList<Attribute>());
	    }

	    Attribute existingAttribute = findAttribute(incommingProjectNote, descriptions, termentryLeve, ignoreCase);

	    if (existingAttribute == null) {
		attributes.add(getDefaultProjectAttribute(incommingProjectNote, termentryLeve));
	    }
	}
    }

    private void addOrUpdateProjectUserLanguagesStatistics(ProjectUserLanguage pul) {
	Statistics stWeekly = new Statistics();
	stWeekly.setReportType(ReportType.WEEKLY.getName());
	stWeekly.setProjectUserLanguage(pul);
	getStatisticsDAO().save(stWeekly);

	Statistics stDaily = new Statistics();
	stDaily.setReportType(ReportType.DAILY.getName());
	stDaily.setProjectUserLanguage(pul);
	getStatisticsDAO().save(stDaily);
    }

    private boolean containsAttribute(List<Attribute> existingProjectAttributes, Attribute incomingProjectAttribute) {
	if (CollectionUtils.isEmpty(existingProjectAttributes)) {
	    return false;
	}
	for (Attribute attribute : existingProjectAttributes) {
	    if (incomingProjectAttribute.equals(attribute)) {
		return true;
	    }
	}
	return false;
    }

    private Attribute findAttribute(String incommingProjectAttribute, List<Attribute> existingProjectAttributes,
	    AttributeLevelEnum termentryLeve, boolean ignoreCase) {
	if (CollectionUtils.isEmpty(existingProjectAttributes)) {
	    return null;
	}
	for (Attribute attribute : existingProjectAttributes) {
	    boolean equalsName = ignoreCase ? attribute.getName().equalsIgnoreCase(incommingProjectAttribute)
		    : attribute.getName().equals(incommingProjectAttribute);
	    if (equalsName && termentryLeve == attribute.getAttributeLevel()) {
		return attribute;
	    }
	}

	return null;
    }

    private Attribute findNote(String incommingProjectNote, List<Attribute> existingProjectNotes, boolean ignoreCase) {
	if (CollectionUtils.isEmpty(existingProjectNotes)) {
	    return null;
	}
	for (Attribute note : existingProjectNotes) {
	    boolean equalsName = ignoreCase ? note.getName().equalsIgnoreCase(incommingProjectNote)
		    : note.getName().equals(incommingProjectNote);
	    if (equalsName) {
		return note;
	    }
	}

	return null;
    }

    private ProjectLanguage findProjectLanguageByLanguage(Collection<ProjectLanguage> incomingProjectLanguages,
	    String language) {
	if (CollectionUtils.isEmpty(incomingProjectLanguages)) {
	    return null;
	}
	for (ProjectLanguage projectLanguage : incomingProjectLanguages) {
	    if (projectLanguage.getLanguage().equals(language)) {
		return projectLanguage;
	    }
	}
	return null;
    }

    private Attribute getDefaultProjectAttribute(String attributeType, AttributeLevelEnum termentryLevel) {
	Attribute projectAttribute = new Attribute();
	projectAttribute.setAttributeLevel(termentryLevel);
	projectAttribute.setInputFieldTypeEnum(InputFieldTypeEnum.TEXT);
	projectAttribute.setMultiplicity(Boolean.FALSE);
	projectAttribute.setName(attributeType);
	projectAttribute.setRequired(Boolean.FALSE);
	projectAttribute.setTermEntryAttributeType(TermEntryAttributeTypeEnum.TEXT);
	projectAttribute.setBaseTypeEnum(BaseTypeEnum.DESCRIPTION);

	return projectAttribute;
    }

    private Attribute getDefaultProjectNote(String noteType) {
	Attribute projectNote = new Attribute();
	projectNote.setInputFieldTypeEnum(InputFieldTypeEnum.TEXT);
	projectNote.setMultiplicity(Boolean.FALSE);
	projectNote.setName(noteType);
	projectNote.setRequired(Boolean.FALSE);
	projectNote.setBaseTypeEnum(BaseTypeEnum.NOTE);
	return projectNote;
    }

    private ExclusiveWriteLockManager getExclusiveWriteLockManager() {
	return _exclusiveWriteLockManager;
    }

    private OrganizationDAO getOrganizationDAO() {
	return _organizationDAO;
    }

    private ProjectDetailDAO getProjectDetailDAO() {
	return _projectDetailDAO;
    }

    private ProjectLanguageDAO getProjectLanguageDAO() {
	return _projectLanguageDAO;
    }

    private ProjectLanguageDetailDAO getProjectLanguageDetailDAO() {
	return _projectLanguageDetailDAO;
    }

    private ProjectLanguageUserDetailDAO getProjectLanguageUserDetailDAO() {
	return _projectLanguageUserDetailDAO;
    }

    private ProjectUserLanguageDAO getProjectUserLanguageDAO() {
	return _projectUserLanguageDAO;
    }

    private StatisticsDAO getStatisticsDAO() {
	return _statisticsDAO;
    }

    private AdminTasksHolderHelper getTasksHolderHelper() {
	return _tasksHolderHelper;
    }

    private UserProfileDAO getUserProfileDAO() {
	return _userProfileDAO;
    }

    private UserProfileService getUserProfileService() {
	return _userProfileService;
    }

    private UserProjectRoleSearchDAO getUserProjectRoleSearchDAO() {
	return _userProjectRoleSearchDAO;
    }

    private void unlinkProjectUserLanguagesByProject(Long projectId, Set<String> languagesForRemoval) {
	List<ProjectUserLanguage> projectUserLanguages = getProjectUserLanguageDAO()
		.getProjectUserLanguagesByProject(projectId);

	for (ProjectUserLanguage projectUserLanguage : projectUserLanguages) {
	    if (languagesForRemoval.contains(projectUserLanguage.getLanguage())) {
		getProjectUserLanguageDAO().delete(projectUserLanguage);
	    }
	}
    }

    @Override
    protected TmProject createBaseProjectInstance() {
	TmProject project = new TmProject();
	ProjectDetail projectDetail = new ProjectDetail(project);
	project.setProjectDetail(projectDetail);

	return project;
    }
}
