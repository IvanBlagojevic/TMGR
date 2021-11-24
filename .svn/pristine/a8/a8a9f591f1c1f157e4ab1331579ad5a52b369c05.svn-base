package org.gs4tr.termmanager.service.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.time.DateFormatUtils;
import org.gs4tr.foundation.modules.entities.model.LdapUserInfo;
import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.Task;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.entities.model.UserInfo;
import org.gs4tr.foundation.modules.entities.model.types.EntityType;
import org.gs4tr.foundation.modules.entities.model.types.ItemStatusType;
import org.gs4tr.foundation.modules.security.ldap.LdapUserHandlerInterface;
import org.gs4tr.foundation.modules.security.model.Policy;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.foundation.modules.spring.ApplicationContextLocator;
import org.gs4tr.foundation3.io.model.FileStatus;
import org.gs4tr.termmanager.model.AbstractItemHolder;
import org.gs4tr.termmanager.model.Attribute;
import org.gs4tr.termmanager.model.AttributeLevelEnum;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.ProjectPolicyEnum;
import org.gs4tr.termmanager.model.StringConstants;
import org.gs4tr.termmanager.model.TermSearchRequest;
import org.gs4tr.termmanager.model.TmOrganization;
import org.gs4tr.termmanager.model.TmPolicyEnum;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.UpdateCommand.CommandEnum;
import org.gs4tr.termmanager.model.UpdateCommand.TypeEnum;
import org.gs4tr.termmanager.model.dto.TmProjectDto;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.search.ItemFolderEnum;
import org.gs4tr.termmanager.service.manualtask.AvailableTaskValidator;
import org.gs4tr.termmanager.service.manualtask.AvailableTaskValidatorFolder;
import org.gs4tr.termmanager.service.manualtask.ManualTaskManager;
import org.gs4tr.termmanager.service.manualtask.SystemManualTaskHandler;

public class ServiceUtils {

    public static final String DATE_FORMAT = "dd-MM-yyyy-HH-mm-ss"; //$NON-NLS-1$

    public static final String POWER_USER_PROJECT_ROLE_NAME = "power_user"; //$NON-NLS-1$

    public static final String SYSTEM_GENERIC_USER_ROLE_NAME = "system_generic_user";

    public static final String TRANSLATOR_USER_PROJECT_ROLE_NAME = "translator_user"; //$NON-NLS-1$

    private static final String ADMIN_ROLE_NAME = "admin";//$NON-NLS-1$

    private static final List<String> DEFAULT_EXPORTABLE_STATUSES = Arrays
	    .asList(ItemStatusTypeHolder.PROCESSED.getName());

    private static final List<String> DEMOTE_T0_ON_HOLD_POLICIES = Arrays.asList(
	    ProjectPolicyEnum.POLICY_TM_TERM_DEMOTE_TO_ON_HOLD_TERM_STATUS.toString(),
	    ProjectPolicyEnum.POLICY_TM_TERM_ON_HOLD_TERM_STATUS.toString());

    private static final List<String> DEMOTE_TO_ON_PENDING_POLICIES = Arrays.asList(
	    ProjectPolicyEnum.POLICY_TM_TERM_DEMOTE_TO_PENDING_APPROVAL_TERM_STATUS.toString(),
	    ProjectPolicyEnum.POLICY_TM_TERM_DEMOTE_TERM_STATUS.toString());

    private static final String LDAP_AUTH_PROVIDER_BEAN_NAME = "ldapAuthProvider"; //$NON-NLS-1$

    private static final Pattern LDAP_ERROR_PATTERN = Pattern.compile("\\[LDAP:.+?-\\s?(.+?)\\s?\\]"); //$NON-NLS-1$

    private static final String LDAP_USER_HANDLER_BEAN_NAME = "ldapUserHandler"; //$NON-NLS-1$

    public static void addProjectsToSearchRequest(TermSearchRequest command) {

	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();

	List<Long> projectIds = command.getProjectIds();
	if (CollectionUtils.isEmpty(projectIds)) {
	    projectIds = new ArrayList<>();
	    Long defaultProjectId = userProfile.getPreferences().getDefaultProjectId();
	    if (Objects.nonNull(defaultProjectId)) {
		projectIds.add(defaultProjectId);
	    } else {
		Iterator<Long> allProjectIds = userProfile.getProjectUserLanguages().keySet().iterator();
		if (allProjectIds.hasNext()) {
		    projectIds.add(allProjectIds.next());
		}
	    }
	}

	command.setProjectIds(projectIds);
	command.setProjectId(projectIds.get(0));
    }

    public static void closeInputStream(InputStream inputStream) {
	try {
	    inputStream.close();
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    }

    public static List<TmProjectDto.TermStatusDto> collectUserPolicies(TmUserProfile currentUser, Long projectId) {

	List<TmProjectDto.TermStatusDto> userPolicies = new ArrayList<>();

	String[] approvePolicies = new String[] { ProjectPolicyEnum.POLICY_TM_TERM_ADD_APPROVED_TERM.toString() };
	if (currentUser.containsContextPolicies(projectId, approvePolicies)) {
	    userPolicies.add(new TmProjectDto.TermStatusDto(ItemStatusTypeHolder.PROCESSED));
	}

	String[] pendingPolicies = new String[] { ProjectPolicyEnum.POLICY_TM_TERM_ADD_PENDING_TERM.toString() };
	if (currentUser.containsContextPolicies(projectId, pendingPolicies)) {
	    userPolicies.add(new TmProjectDto.TermStatusDto(ItemStatusTypeHolder.WAITING));
	}

	String[] onHoldPolices = new String[] { ProjectPolicyEnum.POLICY_TM_TERM_ADD_ON_HOLD_TERM.toString() };
	if (currentUser.containsContextPolicies(projectId, onHoldPolices)) {
	    userPolicies.add(new TmProjectDto.TermStatusDto(ItemStatusTypeHolder.ON_HOLD));
	}

	return userPolicies;
    }

    public static boolean containsTranslationView(TmUserProfile user) {
	boolean containsTranslatorView = false;
	Policy translatorViewInboxPolicy = new Policy();
	translatorViewInboxPolicy.setPolicyId(TmPolicyEnum.POLICY_TM_VIEW_TRANSLATOR_INBOX.name());

	Set<Role> systemRoles = user.getSystemRoles();

	for (Role role : systemRoles) {
	    Set<Policy> policies = role.getPolicies();
	    if (policies.contains(translatorViewInboxPolicy)) {
		containsTranslatorView = true;
	    }
	}
	return containsTranslatorView;
    }

    public static UpdateCommand createCommentUpdateCommand(String parentMarkerId, String value) {
	UpdateCommand command = new UpdateCommand();
	command.setMarkerId(UUID.randomUUID().toString());
	command.setCommand(CommandEnum.TRANSLATE.getName());
	command.setItemType(TypeEnum.COMMENT.getName());
	command.setParentMarkerId(parentMarkerId);
	command.setValue(value);

	return command;
    }

    public static Long createGenericId() {
	return new Date().getTime();
    }

    public static boolean decideAvailablePolicy(Long projectId, String... policies) {
	TmUserProfile user = TmUserProfile.getCurrentUserProfile();
	return user.containsContextPolicies(projectId, policies);
    }

    public static ItemStatusType decideNewTermStatusOnRenameTerm(Long projectId) {

	List<ItemStatusType> statuses = new ArrayList<>(2);

	Set<String> userContextPolicies = TmUserProfile.getCurrentUserProfile().getContextPolicies(projectId);

	if (userContextPolicies.containsAll(DEMOTE_TO_ON_PENDING_POLICIES)) {
	    statuses.add(ItemStatusTypeHolder.WAITING);
	}

	if (userContextPolicies.containsAll(DEMOTE_T0_ON_HOLD_POLICIES)) {
	    statuses.add(ItemStatusTypeHolder.ON_HOLD);
	}

	if (statuses.size() == 0) {
	    return ItemStatusTypeHolder.PROCESSED;
	} else if (statuses.size() == 1)
	    return statuses.get(0);
	else {
	    return getItemStatusWithHigherTypeLevel(statuses.get(0), statuses.get(1));
	}

    }

    public static ItemStatusType decideTermStatus(TmProject project) {
	Long projectId = project.getProjectId();
	TmUserProfile currentUser = TmUserProfile.getCurrentUserProfile();

	ItemStatusType defaultTermStatus = project.getDefaultTermStatus();

	List<ItemStatusType> statuses = new ArrayList<>(3);

	String[] approvePolicies = new String[] { ProjectPolicyEnum.POLICY_TM_TERM_ADD_APPROVED_TERM.toString() };
	if (currentUser.containsContextPolicies(projectId, approvePolicies)) {
	    statuses.add(ItemStatusTypeHolder.PROCESSED);
	}

	String[] pendingPolicies = new String[] { ProjectPolicyEnum.POLICY_TM_TERM_ADD_PENDING_TERM.toString() };
	if (currentUser.containsContextPolicies(projectId, pendingPolicies)) {
	    statuses.add(ItemStatusTypeHolder.WAITING);
	}

	String[] onHoldPolicies = new String[] { ProjectPolicyEnum.POLICY_TM_TERM_ADD_ON_HOLD_TERM.toString() };
	if (currentUser.containsContextPolicies(projectId, onHoldPolicies)) {
	    statuses.add(ItemStatusTypeHolder.ON_HOLD);
	}

	if (statuses.size() == 0) {
	    return defaultTermStatus;
	} else if (statuses.size() == 1) {
	    return statuses.get(0);
	} else if (statuses.contains(defaultTermStatus)) {
	    return defaultTermStatus;
	} else if (statuses.size() == 2) {
	    return getItemStatusWithHigherTypeLevel(statuses.get(0), statuses.get(1));
	} else {
	    return statuses.get(statuses.size() - 1);
	}
    }

    public static boolean deleteDescriptions(Collection<Description> descriptions, List<String> types,
	    String baseType) {
	boolean modified = false;
	if (CollectionUtils.isEmpty(descriptions)) {
	    return modified;
	}
	Iterator<Description> iterator = descriptions.iterator();
	while (iterator.hasNext()) {
	    Description description = iterator.next();
	    if (descriptionTypeEqual(types, description)) {
		if (StringUtils.isNotBlank(baseType)) {
		    if (baseDescriptionTypeEqual(baseType, description)) {
			iterator.remove();
			modified = true;
		    }
		    continue;
		}
		iterator.remove();
		modified = true;
	    }
	}
	return modified;
    }

    public static Set<Long> findAllUserProjectIds() {
	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();
	return userProfile.getProjectUserLanguages().keySet();
    }

    public static TmOrganization findOrganizationById(final List<TmOrganization> allOrganizations, final Long id) {
	TmOrganization result = null;
	for (TmOrganization organization : allOrganizations) {
	    if (organization.getOrganizationId().equals(id)) {
		result = organization;
		break;
	    }
	}
	return result;
    }

    public static String generateExportDocumentName(String projectName, String projectShortCode, String format) {
	StringBuilder builder = new StringBuilder();

	if (StringUtils.isNotEmpty(projectName)) {
	    builder.append(projectName);
	    builder.append(StringConstants.UNDERSCORE);
	}

	if (StringUtils.isNotEmpty(projectShortCode)) {
	    builder.append(projectShortCode);
	    builder.append(StringConstants.UNDERSCORE);
	}

	String now = DateFormatUtils.format(new Date(), DATE_FORMAT);

	builder.append(now);
	builder.append(StringConstants.DOT);
	builder.append(format);

	return builder.toString();
    }

    public static Set<Description> getDescriptionsByType(Term term, String baseType) {
	Set<Description> descriptionsByType = new HashSet<>();

	Set<Description> descriptions = term.getDescriptions();
	if (CollectionUtils.isNotEmpty(descriptions)) {
	    for (Description description : descriptions) {
		if (description.getBaseType().equals(baseType)) {
		    descriptionsByType.add(description);
		}
	    }
	}
	return descriptionsByType;
    }

    public static List<String> getExportableStatusList(TmProject project) {
	List<String> exportableStatuses = new ArrayList<>(4);
	exportableStatuses.addAll(DEFAULT_EXPORTABLE_STATUSES);
	if (isSharePendingTermsOnProject(project.getSharePendingTerms())) {
	    exportableStatuses.add(ItemStatusTypeHolder.WAITING.getName());
	}
	return exportableStatuses;
    }

    public static String getFileEncoding(File tmpFile) {
	String encoding;
	try {
	    FileStatus status = org.gs4tr.foundation3.io.utils.FileUtils.getStatus(tmpFile);
	    encoding = status.getEncoding().getEncoding();
	} catch (Exception e) {
	    throw new RuntimeException(e.getMessage(), e);
	}

	return encoding;
    }

    public static List<Attribute> getTermEntryLevelAttributes(List<Attribute> projectAttributes) {

	List<Attribute> finalList = new ArrayList<>();
	if (CollectionUtils.isEmpty(projectAttributes)) {
	    return finalList;
	}

	for (Attribute attribute : projectAttributes) {
	    AttributeLevelEnum attributeLevel = attribute.getAttributeLevel();
	    if (AttributeLevelEnum.TERMENTRY == attributeLevel) {
		finalList.add(attribute);
	    }
	}
	return finalList;
    }

    public static String getTotalTimeFormatted(long endTime, long startTime) {
	long totalTime = endTime - startTime;
	return String.format("%d min, %d sec", //$NON-NLS-1$
		TimeUnit.MILLISECONDS.toMinutes(totalTime), TimeUnit.MILLISECONDS.toSeconds(totalTime)
			- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalTime)));
    }

    public static boolean isAdminUser(TmUserProfile userProfile) {
	if (userProfile == null) {
	    return false;
	}
	Set<Role> roles = userProfile.getSystemRoles();
	if (roles != null) {
	    for (Role role : roles) {
		if (role.getRoleId().equals(ADMIN_ROLE_NAME)) {
		    return true;
		}
	    }
	}

	return false;
    }

    public static boolean isAssigneeUser(TmUserProfile user, Long currentProjectId) {
	boolean containsTranslatorView = containsTranslationView(user);

	String[] policies = new String[] { ProjectPolicyEnum.POLICY_TM_TERM_AUTO_SAVE_TRANSLATION.toString() };

	boolean containsAutoSave = containsSpecificPolicy(user, currentProjectId, policies);

	return containsTranslatorView && containsAutoSave;
    }

    public static boolean isSharePendingTermsOnProject(Boolean sharePendingTerms) {
	return sharePendingTerms == null ? true : sharePendingTerms;
    }

    public static boolean isShowAutoSave(TmUserProfile user) {
	if (user.isPowerUser()) {
	    return true;
	}

	String[] policies = new String[] { ProjectPolicyEnum.POLICY_TM_TERM_AUTO_SAVE_TRANSLATION.toString() };
	Set<Long> projectIds = user.getProjectUserLanguages().keySet();
	for (Long projectId : projectIds) {
	    boolean contains = user.containsContextPolicies(projectId, policies);
	    if (contains) {
		return true;
	    }
	}

	return false;
    }

    public static boolean isSubmitterUser(TmUserProfile user, Long currentProjectId) {
	if (user.isPowerUser()) {
	    return true;
	}

	boolean containsTranslatorView = containsTranslationView(user);

	String[] policies = new String[] { ProjectPolicyEnum.POLICY_TM_SEND_TO_TRANSLATION_REVIEW.toString() };

	boolean containsPolicy = containsSpecificPolicy(user, currentProjectId, policies);

	return containsTranslatorView && containsPolicy;
    }

    public static boolean isTermExportable(Term term, String currentUser, List<String> exportableStatuses) {
	return (isTermExportableByUser(term, currentUser) || isTermExportableByStatus(term, exportableStatuses))
		&& !ItemStatusTypeHolder.isInWorkflow(term);
    }

    public static <T extends AbstractItemHolder, ID extends Serializable> List<Task> postProcessEntityPagedList(
	    PagedList<T> pagedList, ItemFolderEnum folder, List<Long> projectIds, EntityType[] entityTypes,
	    AdminTasksHolderHelper taskHolderHelper) {

	T[] elements = pagedList.getElements();
	Set<Task> tasks = new LinkedHashSet<>();

	Map<Task, Integer> availableTasks = new LinkedHashMap<>();
	Map<Task, List<String>> taskPolicyMap = new HashMap<>();

	if (projectIds == null) {
	    projectIds = new ArrayList<>();
	    TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();
	    projectIds.addAll(userProfile.getProjectUserLanguages().keySet());
	}

	for (Long pId : projectIds) {
	    if (ArrayUtils.isNotEmpty(elements)) {
		for (T element : elements) {
		    tasks.addAll(taskHolderHelper.getSystemEntityTasks(pId, element, folder, entityTypes));
		}
	    } else {
		tasks.addAll(taskHolderHelper.getSystemEntityTasks(pId, folder, entityTypes));
	    }
	}

	collectAvailableTasks(new ArrayList<>(tasks), availableTasks);

	List<Task> allTasks = new ArrayList<>(tasks);

	for (T element : elements) {
	    computeAvailableTasks(element, allTasks, availableTasks, taskHolderHelper, taskPolicyMap, folder);
	}

	return allTasks;
    }

    public static String resolveStatusName(String status) throws Exception {
	ItemStatusType itemStatusType = ItemStatusTypeHolder.valueOf(status);
	if (Objects.nonNull(itemStatusType)) {
	    return itemStatusType.getName();
	}

	String statusByDisplayName = ItemStatusTypeHolder.getStatusByDisplayName(status);
	if (Objects.nonNull(statusByDisplayName)) {
	    return statusByDisplayName;
	}

	throw new UserException(String.format(Messages.getString("invalid.term.status.name"), status));
    }

    public static void sortUpdateCommands(List<UpdateCommand> updateCommands) {
	if (CollectionUtils.isNotEmpty(updateCommands)) {
	    // Make sure that "update" commands are first in the list
	    updateCommands.sort((c1, c2) -> c1.getCommandEnum().compare(c2.getCommandEnum()));
	}
    }

    public static void sortUpdateCommandsByType(List<UpdateCommand> updateCommands) {
	if (CollectionUtils.isNotEmpty(updateCommands)) {
	    updateCommands.sort((o1, o2) -> o1.getTypeEnum().compare(o2.getTypeEnum()));
	}
    }

    public static boolean supportsLdap() {
	return ApplicationContextLocator.getContext(null).containsBean(LDAP_AUTH_PROVIDER_BEAN_NAME);
    }

    public static boolean updateLdapPassword(String userName, String oldPassword, String newPassword,
	    UserInfo userInfo) {
	boolean ldapChanged = false;

	if (supportsLdap() && StringUtils.isNotBlank(newPassword)) {
	    LdapUserHandlerInterface ldapUserHandler = getLdapUserHandler();

	    LdapUserInfo ldapUserInfo = ldapUserHandler.findUser(userName);
	    if (ldapUserInfo != null) {
		Validate.isTrue(ldapUserHandler.allowPasswordChange(), Messages.getString("ServiceUtils.24")); //$NON-NLS-1$
		try {
		    if (oldPassword != null) {
			ldapUserHandler.changeCurrentUserPassword(ldapUserInfo, oldPassword, newPassword);
		    } else {
			ldapUserHandler.changePassword(ldapUserInfo, newPassword);
		    }
		    // to disable changing password of tmgr user setting
		    // userInfos
		    // password to null
		    if (userInfo != null) {
			userInfo.setPassword(StringUtils.EMPTY);
		    }
		    ldapChanged = true;
		} catch (Exception ex) {
		    String extractedLdapMessage = extractLdapMessage(ex.getMessage());
		    String errorMessage = extractedLdapMessage == null ? ex.getMessage() : extractedLdapMessage;
		    throw new RuntimeException(errorMessage, ex);
		}
	    }
	}
	return ldapChanged;
    }

    private static boolean baseDescriptionTypeEqual(String baseType, Description description) {
	return description != null && !StringUtils.isBlank(baseType) && baseType.equals(description.getBaseType());
    }

    private static boolean checkTaskPolicyForItem(Map<Task, List<String>> taskPolicyMap, Long projectId, Task task) {

	List<String> taskPolicies = taskPolicyMap.get(task);
	if (CollectionUtils.isNotEmpty(taskPolicies)) {

	    if (projectId != null) {
		TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();
		Set<String> contextPolicies = userProfile.getContextPolicies(projectId);

		return CollectionUtils.containsAny(contextPolicies, taskPolicies);
	    }
	} else {
	    return true;
	}

	return false;
    }

    private static void collectAvailableTasks(List<Task> tasks, Map<Task, Integer> availableTasks) {
	Set<Task> availableTasksKeys = availableTasks.keySet();

	Integer taskId = 2;

	if (!availableTasks.isEmpty()) {
	    for (Map.Entry<Task, Integer> entry : availableTasks.entrySet()) {
		Integer availableTaskId = entry.getValue();
		if (availableTaskId.compareTo(taskId) > 0) {
		    taskId = availableTaskId;
		}
	    }
	    taskId *= 2;
	}

	for (Task task : tasks) {
	    if (!availableTasksKeys.contains(task)) {
		availableTasks.put(task, taskId);

		taskId *= 2;
	    }
	}
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static <T extends AbstractItemHolder> void computeAvailableTasks(T element, List<Task> tasks,
	    Map<Task, Integer> availableTasks, AdminTasksHolderHelper tasksHolderHelper,
	    Map<Task, List<String>> taskPolicyMap, ItemFolderEnum folder) {

	ManualTaskManager<SystemManualTaskHandler> manualTaskManager = tasksHolderHelper.getManualTaskManager(folder);

	Long elementAvailableTasks = element.getAvailableTasks();
	if (elementAvailableTasks == null) {
	    elementAvailableTasks = 0L;
	}

	for (Task task : tasks) {
	    Integer value = availableTasks.get(task);
	    if (value != null && checkTaskPolicyForItem(taskPolicyMap, element.getProjectId(), task)) {
		SystemManualTaskHandler taskHandler = manualTaskManager.getUserTaskHandler(task.getName());
		if (taskHandler instanceof AvailableTaskValidator) {
		    if (((AvailableTaskValidator) taskHandler).isTaskAvailable(element)) {
			elementAvailableTasks += value;
		    }
		} else if (taskHandler instanceof AvailableTaskValidatorFolder) {
		    if (((AvailableTaskValidatorFolder) taskHandler).isTaskAvailableForFolder(element, folder)) {
			elementAvailableTasks += value;
		    }
		} else {
		    elementAvailableTasks += value;
		}
	    }
	}
	element.setAvailableTasks(elementAvailableTasks);
    }

    private static boolean containsSpecificPolicy(TmUserProfile user, Long currentProjectId, String[] policies) {
	boolean containsPolicy = false;

	if (currentProjectId == null || currentProjectId == 0) {
	    Set<Long> projectIds = user.getProjectUserLanguages().keySet();
	    for (Long projectId : projectIds) {
		boolean contains = user.containsContextPolicies(projectId, policies);
		if (contains) {
		    containsPolicy = true;
		    break;
		}
	    }
	} else {
	    containsPolicy = user.containsContextPolicies(currentProjectId, policies);
	}
	return containsPolicy;
    }

    private static boolean descriptionTypeEqual(List<String> types, Description description) {
	return description != null && !CollectionUtils.isEmpty(types) && types.contains(description.getType());
    }

    private static String extractLdapMessage(String message) {
	Matcher matcher = LDAP_ERROR_PATTERN.matcher(message);
	if (matcher.find()) {
	    return matcher.group(1);
	}
	return null;
    }

    private static ItemStatusType getItemStatusWithHigherTypeLevel(ItemStatusType itemStatus1,
	    ItemStatusType itemStatus2) {

	String status1 = itemStatus1.toString();
	String status2 = itemStatus2.toString();

	return ItemStatusTypeHolder.getItemStatusTypeLevel(status1) < ItemStatusTypeHolder
		.getItemStatusTypeLevel(status2) ? itemStatus1 : itemStatus2;

    }

    private static LdapUserHandlerInterface getLdapUserHandler() {
	return (LdapUserHandlerInterface) ApplicationContextLocator.getContext(null)
		.getBean(LDAP_USER_HANDLER_BEAN_NAME);
    }

    private static boolean isTermExportableByStatus(Term term, List<String> exportableStatuses) {
	return exportableStatuses.contains(term.getStatus());
    }

    private static boolean isTermExportableByUser(Term term, String username) {
	return username.equals(term.getUserCreated());
    }
}
