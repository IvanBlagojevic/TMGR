package org.gs4tr.termmanager.webmvc.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.mutable.MutableInt;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.entities.model.StringConstants;
import org.gs4tr.foundation.modules.entities.model.Task;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.foundation.modules.entities.model.types.ItemStatusType;
import org.gs4tr.foundation.modules.security.model.Policy;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.foundation3.solr.model.concordance.ConcordanceType;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.Language;
import org.gs4tr.termmanager.model.Preferences;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.dto.converter.SelectStyleConverter;
import org.gs4tr.termmanager.model.dto.converter.TaskConverter;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.model.feature.TmgrFeatureConstants;
import org.gs4tr.termmanager.model.search.LanguageSearchEnum;
import org.gs4tr.termmanager.model.search.TypeSearchEnum;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.SubmissionService;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.webmvc.model.search.ControlType;
import org.gs4tr.termmanager.webmvc.model.search.DateRange;
import org.gs4tr.termmanager.webmvc.model.search.DateSearchEnum;
import org.gs4tr.termmanager.webmvc.model.search.DoubleMultiComboBox;
import org.gs4tr.termmanager.webmvc.model.search.DoubleMultiComboBoxDefaultValue;
import org.gs4tr.termmanager.webmvc.model.search.EntitySearch;
import org.gs4tr.termmanager.webmvc.model.search.GridSearchBox;
import org.gs4tr.termmanager.webmvc.model.search.GridSearchItem;
import org.gs4tr.termmanager.webmvc.model.search.InputText;
import org.gs4tr.termmanager.webmvc.model.search.InputTextAndComboBox;
import org.gs4tr.termmanager.webmvc.model.search.InputTextAndComboItem;
import org.gs4tr.termmanager.webmvc.model.search.LanguageDirectionItem;
import org.gs4tr.termmanager.webmvc.model.search.LanguageDirectionSearchEnum;
import org.gs4tr.termmanager.webmvc.model.search.LanguageSearch;
import org.gs4tr.termmanager.webmvc.model.search.LanguageSearchBox;
import org.gs4tr.termmanager.webmvc.model.search.LinkedComboBox;
import org.gs4tr.termmanager.webmvc.model.search.LinkedComboBoxDefaultValue;
import org.gs4tr.termmanager.webmvc.model.search.LinkedComboItem;
import org.gs4tr.termmanager.webmvc.model.search.LinkedMultiComboBox;
import org.gs4tr.termmanager.webmvc.model.search.LinkedSingleComboBox;
import org.gs4tr.termmanager.webmvc.model.search.MultiComboBox;
import org.gs4tr.termmanager.webmvc.model.search.SearchCriteria;
import org.gs4tr.termmanager.webmvc.model.search.SimpleComboItem;
import org.gs4tr.termmanager.webmvc.model.search.TermNameSearchEnum;

public class ControllerUtils {

    private static final boolean GRID_FEATURE = Arrays.asList(TmgrFeatureConstants.getEnabledFeatures())
	    .contains(TmgrFeatureConstants.GRID_FEATURE);
    private static final int START_ID_FOR_DEFAULT_TASKS = 1;

    public static Map<String, Set<Policy>> createCategoryPolicyMap(Set<Policy> policies) {

	Map<String, Set<Policy>> categoryPolicies = new HashMap<>();

	for (Policy policy : policies) {
	    String category = policy.getCategory();

	    Set<Policy> policiesSet = categoryPolicies.computeIfAbsent(category, k -> new HashSet<>());

	    policiesSet.add(policy);
	}

	return categoryPolicies;
    }

    public static Map<SearchCriteria, Object> createSearchBarElements(ProjectService projectService,
	    SubmissionService submissionService, UserProfileService userProfileService) {

	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();
	Map<Long, Set<String>> projectUserLanguages = userProfile.getProjectUserLanguages();

	LinkedSingleComboBox submissionTermLanguageCombo = new LinkedSingleComboBox(
		SearchCriteria.SUBMISSION_TERM_LANGUAGE);
	configureSubmissionTermLanguageCombo(submissionTermLanguageCombo, userProfile);

	LinkedMultiComboBox submissionLanguageCombo = new LinkedMultiComboBox(
		SearchCriteria.LANGUAGE_DIRECTION_SUBMISSION);
	configureSubmissionLanguageDirectionCombo(submissionLanguageCombo, userProfile);

	MultiComboBox submissionProjectComboBox = new MultiComboBox(SearchCriteria.SUBMISSION_PROJECT_LIST);
	configureSubmissionProjectComboBox(submissionService, submissionProjectComboBox, userProfile);

	MultiComboBox submissionStatusComboBox = new MultiComboBox(SearchCriteria.SUBMISSION_STATUSES);
	configureSubmissionStatusComboBox(submissionStatusComboBox);

	DoubleMultiComboBox submissionUsersComboBox = new DoubleMultiComboBox(SearchCriteria.SUBMISSION_USERS);
	configureSubmissionUsersComboBox(userProfile, submissionUsersComboBox, submissionService);

	EnumMap<SearchCriteria, Object> bar = new EnumMap<>(SearchCriteria.class);
	List<String> allUsers = userProfileService.findAllNonGenerciUsernames();

	if (!GRID_FEATURE) {
	    configureSearchBar(bar, projectService, projectUserLanguages, allUsers);

	} else {
	    configureGridFeatureSearchBar(bar, projectService, projectUserLanguages, allUsers);

	}

	bar.put(SearchCriteria.USER_NAME, new InputText(SearchCriteria.USER_NAME));
	bar.put(SearchCriteria.EMAIL_ADDRESS, new InputText(SearchCriteria.EMAIL_ADDRESS));
	bar.put(SearchCriteria.LANGUAGE_SET_NAME, new InputText(SearchCriteria.LANGUAGE_SET_NAME));
	bar.put(SearchCriteria.USER_FIRST_NAME, new InputText(SearchCriteria.USER_FIRST_NAME));
	bar.put(SearchCriteria.USER_LAST_NAME, new InputText(SearchCriteria.USER_LAST_NAME));
	bar.put(SearchCriteria.ORGANIZATION_NAME, new InputText(SearchCriteria.ORGANIZATION_NAME));
	bar.put(SearchCriteria.ROLE_NAME, new InputText(SearchCriteria.ROLE_NAME));
	bar.put(SearchCriteria.PROJECT_NAME, new InputText(SearchCriteria.PROJECT_NAME));
	bar.put(SearchCriteria.PROJECT_SHORT_CODE, new InputText(SearchCriteria.PROJECT_SHORT_CODE));
	bar.put(SearchCriteria.TERM_ENTRY_DATE_CREATED_RANGE,
		new DateRange(SearchCriteria.TERM_ENTRY_DATE_CREATED_RANGE));
	bar.put(SearchCriteria.DATE_COMPLETED_RANGE, new DateRange(SearchCriteria.DATE_COMPLETED_RANGE));
	bar.put(SearchCriteria.LANGUAGE_DIRECTION_SUBMISSION, submissionLanguageCombo);
	bar.put(SearchCriteria.SUBMISSION_PROJECT_LIST, submissionProjectComboBox);
	bar.put(SearchCriteria.SUBMISSION_TERM_LANGUAGE, submissionTermLanguageCombo);
	bar.put(SearchCriteria.SUBMISSION_STATUSES, submissionStatusComboBox);
	bar.put(SearchCriteria.SUBMISSION_NAME, new InputText(SearchCriteria.SUBMISSION_NAME));
	bar.put(SearchCriteria.SUBMISSION_USERS, submissionUsersComboBox);

	return bar;
    }

    public static Set<String> getAllUserProjectLocales(TmUserProfile userProfile) {
	Set<String> languages = new HashSet<>();
	for (Map.Entry<Long, Set<String>> entry : userProfile.getProjectUserLanguages().entrySet()) {
	    languages.addAll(entry.getValue());
	}
	return languages;
    }

    public static String[] getDefaultSourceAndTargetLanguage() {
	String[] sourceAndTarget = new String[2];

	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();

	Set<String> languages = getAllUserProjectLocales(userProfile);
	Preferences preferences = userProfile.getPreferences();
	String sourceLanguage = preferences.getDefaultSourceLanguage();
	String targetLanguage = preferences.getDefaultTargetLanguage();
	int projectUserLanguagesNumber = languages.size();
	Iterator<String> iterator = languages.iterator();
	if (sourceLanguage != null && languages.contains(sourceLanguage)) {
	    sourceAndTarget[0] = sourceLanguage;
	} else {
	    if (projectUserLanguagesNumber > 0) {
		sourceAndTarget[0] = iterator.next();
	    }
	}
	if (targetLanguage != null && languages.contains(targetLanguage)) {
	    sourceAndTarget[1] = targetLanguage;
	} else {
	    if (projectUserLanguagesNumber > 0) {
		sourceAndTarget[1] = projectUserLanguagesNumber >= 2 ? iterator.next() : iterator.next();
	    }
	}
	return sourceAndTarget;
    }

    public static String getDefaultSourceLanguage() {

	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();

	Set<String> languages = getAllUserProjectLocales(userProfile);
	Preferences preferences = userProfile.getPreferences();
	String sourceLanguage = preferences.getDefaultSourceLanguage();

	int projectUserLanguagesNumber = languages.size();

	if (sourceLanguage != null && languages.contains(sourceLanguage)) {
	    return sourceLanguage;
	} else if (projectUserLanguagesNumber > 0) {
	    Iterator<String> iterator = languages.iterator();
	    sourceLanguage = iterator.next();
	}

	return sourceLanguage;

    }

    public static <N> List<org.gs4tr.termmanager.model.dto.Task> getDtoTaskUnion(TaskPagedList<N> entityPagedList) {

	List<org.gs4tr.termmanager.model.dto.Task> dtoTasks = new ArrayList<>();
	Task[] defaultTasks = entityPagedList.getTasks();

	if (defaultTasks != null) {
	    List<org.gs4tr.termmanager.model.dto.Task> defaultDtoTasks = collectDtoTasks(Arrays.asList(defaultTasks));

	    dtoTasks.addAll(defaultDtoTasks);
	}

	sortTasks(dtoTasks);

	return dtoTasks;
    }

    public static boolean isProjectInProjectList(Long defaultProjectId, List<TmProject> projects) {
	for (TmProject tmProject : projects) {
	    if (tmProject.getProjectId().equals(defaultProjectId)) {
		return true;
	    }
	}
	return false;
    }

    public static Map<String, Boolean> isProjectPolicyEnabled(TmUserProfile userProfile, String[] policies) {
	Map<String, Boolean> projectPolicyEnabled = new HashMap<>();
	boolean powerUser = userProfile.isPowerUser();
	Set<Long> projectIds = userProfile.getProjectUserLanguages().keySet();
	for (Long projectId : projectIds) {
	    String projectTicket = TicketConverter.fromInternalToDto(projectId);
	    Boolean policyEnabled = powerUser || userProfile.containsContextPolicies(projectId, policies);
	    projectPolicyEnabled.put(projectTicket, policyEnabled);
	}

	return projectPolicyEnabled;
    }

    private static List<org.gs4tr.termmanager.model.dto.Task> collectDtoTasks(Collection<Task> tasks) {
	List<org.gs4tr.termmanager.model.dto.Task> dtoTasks = new ArrayList<>();

	final MutableInt index = new MutableInt(START_ID_FOR_DEFAULT_TASKS);

	CollectionUtils.collect(tasks, input -> {

	    org.gs4tr.termmanager.model.dto.Task dtoTask = TaskConverter.fromInternalToDto((Task) input);

	    final Task taskInternal = ((Task) input);

	    int taskId = index.intValue() * 2;
	    dtoTask.setTaskId(taskId);

	    index.setValue(taskId);

	    dtoTask.setSelectStyle(SelectStyleConverter.fromInternalToDto(taskInternal.getSelectStyle()));

	    return dtoTask;
	}, dtoTasks);

	sortTasks(dtoTasks);

	return dtoTasks;
    }

    private static void configureCreationAndModificationUsersCombo(MultiComboBox creationUsersComboBox,
	    MultiComboBox modificationUsersComboBox, List<String> usernames) {

	List<SimpleComboItem> comboItems = new ArrayList<>();
	for (String username : usernames) {
	    SimpleComboItem comboItem = new SimpleComboItem();
	    comboItem.setName(username);
	    comboItem.setValue(username);
	    comboItems.add(comboItem);
	}

	creationUsersComboBox.setValue(new ArrayList<>());
	creationUsersComboBox.setValues(comboItems);

	modificationUsersComboBox.setValue(new ArrayList<>());
	modificationUsersComboBox.setValues(comboItems);
    }

    private static void configureDefaultLanguageValues(LanguageSearch sourceSearch, LanguageSearch targetSearch) {

	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();

	List<String> languages = new ArrayList<>(getAllUserProjectLocales(userProfile));

	Preferences preferences = userProfile.getPreferences();
	String sourceLanguage = preferences.getDefaultSourceLanguage();
	String targetLanguage = preferences.getDefaultTargetLanguage();

	int size = languages.size();

	List<String> defaultSourceLanguages = new ArrayList<>();

	String defaultSourceLanguage = resolveDefaultSourceLanguage(sourceLanguage, languages);
	if (Objects.nonNull(defaultSourceLanguage)) {
	    defaultSourceLanguages.add(defaultSourceLanguage);
	}
	sourceSearch.setDefaultValues(defaultSourceLanguages);

	String[] targetLanguages = null;
	if (targetLanguage != null) {
	    targetLanguages = getDefaultTargetLanguageFromPreferences(targetLanguage);
	}

	List<String> defaultTargetLanguages = new ArrayList<>();

	if (targetLanguages != null) {
	    defaultTargetLanguages = Arrays.stream(targetLanguages).filter(languages::contains)
		    .collect(Collectors.toList());

	} else if (size > 0) {
	    targetLanguage = getTargetLanguageFromUserLanguages(languages, defaultSourceLanguage);
	    defaultTargetLanguages.add(targetLanguage);
	}

	targetSearch.setDefaultValues(defaultTargetLanguages);

    }

    private static void configureEntityTypeComboBox(DoubleMultiComboBox entityTypeMultiComboBox) {
	String attributesName = TypeSearchEnum.ATTRIBUTES.name();
	String termName = TypeSearchEnum.TERM.name();

	List<SimpleComboItem> values1 = new ArrayList<>();
	values1.add(new SimpleComboItem(attributesName, attributesName));
	values1.add(new SimpleComboItem(termName, termName));

	String sourceName = LanguageSearchEnum.SOURCE.name();
	String targetName = LanguageSearchEnum.TARGET.name();

	List<SimpleComboItem> values2 = new ArrayList<>();
	values2.add(new SimpleComboItem(sourceName, sourceName));
	values2.add(new SimpleComboItem(targetName, targetName));

	List<String> value1 = new ArrayList<>();
	value1.add(termName);

	List<String> value2 = new ArrayList<>();
	value2.add(sourceName);
	value2.add(targetName);

	DoubleMultiComboBoxDefaultValue defaultValue = new DoubleMultiComboBoxDefaultValue();
	defaultValue.setValue1(value1);
	defaultValue.setValue2(value2);

	entityTypeMultiComboBox.setValue(defaultValue);
	entityTypeMultiComboBox.setValues1(values1);
	entityTypeMultiComboBox.setValues2(values2);
	entityTypeMultiComboBox.setRequired(true);
    }

    private static void configureGridFeatureSearchBar(Map<SearchCriteria, Object> bar, ProjectService projectService,
	    Map<Long, Set<String>> projectUserLanguages, List<String> users) {

	createUserSearchBox(bar, SearchCriteria.TL_CREATION_USERS, SearchCriteria.TL_MODIFICATION_USERS, users);
	createDateSearchBox(bar, SearchCriteria.TL_DATE_CREATED_RANGE, SearchCriteria.TL_DATE_MODIFIED_RANGE);
	createEntityTypeSearchBox(bar, SearchCriteria.TL_ENTITY_TYPE);
	createHideBlanksSearchBox(bar, SearchCriteria.TL_HIDE_BLANKS);
	createLanguageDirectionBox(bar, SearchCriteria.TL_LANGUAGE_DIRECTION, projectUserLanguages);
	createProjectListSearchBox(bar, SearchCriteria.TL_PROJECT_LIST, projectService, projectUserLanguages.keySet());
	createTermNameSearchBox(bar, SearchCriteria.TL_TERM_NAME);
	createTermStatusesSearchBox(bar, SearchCriteria.TL_TERM_STATUSES);

	MultiComboBox termStatusComboBox = new MultiComboBox(SearchCriteria.TERM_STATUSES);
	configureTermStatusComboBox(termStatusComboBox);

	DoubleMultiComboBox entityTypeMultiComboBox = new DoubleMultiComboBox(SearchCriteria.ENTITY_TYPE);
	configureEntityTypeComboBox(entityTypeMultiComboBox);

	InputTextAndComboBox termNameInputTextAndCombo = new InputTextAndComboBox(SearchCriteria.TERM_NAME);
	configureTermName(termNameInputTextAndCombo);

	bar.put(SearchCriteria.DATE_CREATED_RANGE, new DateRange(SearchCriteria.DATE_CREATED_RANGE));
	bar.put(SearchCriteria.DATE_MODIFIED_RANGE, new DateRange(SearchCriteria.DATE_MODIFIED_RANGE));
	bar.put(SearchCriteria.ENTITY_TYPE, entityTypeMultiComboBox);
	bar.put(SearchCriteria.TERM_NAME, termNameInputTextAndCombo);
	bar.put(SearchCriteria.TERM_STATUSES, termStatusComboBox);
    }

    private static void configureLanguageDirectionCombo(LinkedComboBox languageDirectionCombo,
	    Map<Long, Set<String>> projectUserLanguages) {

	Map<String, Set<String>> languageProjectsMap = new HashMap<>();

	for (Entry<Long, Set<String>> entry : projectUserLanguages.entrySet()) {
	    Long projectId = entry.getKey();
	    String projectTicket = TicketConverter.fromInternalToDto(projectId);
	    Set<String> languageIds = entry.getValue();
	    for (String languageId : languageIds) {
		Set<String> projectTickets = languageProjectsMap.computeIfAbsent(languageId, k -> new HashSet<>());
		projectTickets.add(projectTicket);
	    }
	}

	List<LinkedComboItem> values = new ArrayList<>();
	for (Entry<String, Set<String>> entry : languageProjectsMap.entrySet()) {
	    String languageId = entry.getKey();
	    Set<String> projectTickets = entry.getValue();

	    LinkedComboItem comboItem = new LinkedComboItem();
	    comboItem.setValue(languageId);
	    Language language = Language.valueOf(languageId);
	    comboItem.setName(language.getDisplayName());
	    comboItem.setDirection(language.getLanguageAlignment().getValue());
	    comboItem.setTickets(projectTickets);

	    values.add(comboItem);
	}

	sortLanguagesWithEngOnFirstPlace(values);

	LinkedComboBoxDefaultValue value = createDefaultLanguageDirection();

	languageDirectionCombo.setValues(values);
	languageDirectionCombo.setValue(value);
    }

    private static void configureProjectComboBox(ProjectService projectService, MultiComboBox projectComboBox,
	    Set<Long> projectIds) {
	List<TmProject> projects = projectService.findProjectByIds(new ArrayList<>(projectIds));
	List<SimpleComboItem> comboItems = new ArrayList<>();
	for (TmProject project : projects) {
	    SimpleComboItem comboItem = new SimpleComboItem();
	    comboItem.setName(project.getProjectInfo().getName());
	    comboItem.setValue(TicketConverter.fromInternalToDto(project.getProjectId()));
	    comboItems.add(comboItem);
	}

	String value = createDefaultProjectOption(projectIds);
	List<String> valueList = new ArrayList<>();
	valueList.add(value);

	projectComboBox.setValue(valueList);
	projectComboBox.setValues(comboItems);
	projectComboBox.setRequired(true);
    }

    private static void configureProjectItem(GridSearchItem projectItem, ProjectService projectService,
	    Set<Long> projectIds) {

	List<TmProject> projects = projectService.findProjectByIds(new ArrayList<>(projectIds));
	List<SimpleComboItem> values = new ArrayList<>();

	for (TmProject project : projects) {
	    SimpleComboItem value = new SimpleComboItem();
	    value.setName(project.getProjectInfo().getName());
	    value.setValue(TicketConverter.fromInternalToDto(project.getProjectId()));
	    values.add(value);
	}

	String defaultValue = createDefaultProjectOption(projectIds);
	List<String> defaultValues = new ArrayList<>();
	defaultValues.add(defaultValue);

	projectItem.setValues(values);
	projectItem.setDefaultValues(defaultValues);

    }

    private static void configureSearchBar(Map<SearchCriteria, Object> bar, ProjectService projectService,
	    Map<Long, Set<String>> projectUserLanguages, List<String> users) {

	MultiComboBox creationUsersComboBox = new MultiComboBox(SearchCriteria.CREATION_USERS);
	MultiComboBox modificationUsersComboBox = new MultiComboBox(SearchCriteria.MODIFICATION_USERS);
	configureCreationAndModificationUsersCombo(creationUsersComboBox, modificationUsersComboBox, users);

	DoubleMultiComboBox entityTypeMultiComboBox = new DoubleMultiComboBox(SearchCriteria.ENTITY_TYPE);
	configureEntityTypeComboBox(entityTypeMultiComboBox);

	MultiComboBox hideBlanksComboBox = new MultiComboBox(SearchCriteria.HIDE_BLANKS);
	hideBlanksComboBox.setValues(new ArrayList<>());
	hideBlanksComboBox.setValue(new ArrayList<>());

	LinkedComboBox termLanguageCombo = new LinkedComboBox(SearchCriteria.LANGUAGE_DIRECTION);
	configureLanguageDirectionCombo(termLanguageCombo, projectUserLanguages);

	MultiComboBox projectComboBox = new MultiComboBox(SearchCriteria.PROJECT_LIST);
	configureProjectComboBox(projectService, projectComboBox, projectUserLanguages.keySet());

	InputTextAndComboBox termNameInputTextAndCombo = new InputTextAndComboBox(SearchCriteria.TERM_NAME);
	configureTermName(termNameInputTextAndCombo);

	MultiComboBox termStatusComboBox = new MultiComboBox(SearchCriteria.TERM_STATUSES);
	configureTermStatusComboBox(termStatusComboBox);

	bar.put(SearchCriteria.CREATION_USERS, creationUsersComboBox);
	bar.put(SearchCriteria.MODIFICATION_USERS, modificationUsersComboBox);
	bar.put(SearchCriteria.DATE_CREATED_RANGE, new DateRange(SearchCriteria.DATE_CREATED_RANGE));
	bar.put(SearchCriteria.DATE_MODIFIED_RANGE, new DateRange(SearchCriteria.DATE_MODIFIED_RANGE));
	bar.put(SearchCriteria.ENTITY_TYPE, entityTypeMultiComboBox);
	bar.put(SearchCriteria.HIDE_BLANKS, hideBlanksComboBox);
	bar.put(SearchCriteria.LANGUAGE_DIRECTION, termLanguageCombo);
	bar.put(SearchCriteria.PROJECT_LIST, projectComboBox);
	bar.put(SearchCriteria.TERM_NAME, termNameInputTextAndCombo);
	bar.put(SearchCriteria.TERM_STATUSES, termStatusComboBox);

    }

    private static void configureSubmissionLanguageDirectionCombo(LinkedMultiComboBox submissionLanguageCombo,
	    TmUserProfile userProfile) {
	Set<String> userLanguageIds = new HashSet<>();

	Map<Long, Set<String>> projectUserLanguages = userProfile.getProjectUserLanguages();
	for (Entry<Long, Set<String>> entry : projectUserLanguages.entrySet()) {
	    userLanguageIds.addAll(entry.getValue());
	}

	Map<Long, Map<String, Set<String>>> submissionUserLanguages = userProfile.getSubmissionUserLanguages();

	Map<String, Set<String>> sourceLanguageSubs = new HashMap<>();
	Map<String, Set<String>> targetLanguageSubs = new HashMap<>();

	for (Entry<Long, Map<String, Set<String>>> entry : submissionUserLanguages.entrySet()) {
	    Long submissionId = entry.getKey();
	    Map<String, Set<String>> subLanguages = entry.getValue();

	    String subTicket = TicketConverter.fromInternalToDto(submissionId);

	    for (Entry<String, Set<String>> subEntry : subLanguages.entrySet()) {
		String sourceLanguageId = subEntry.getKey();

		Set<String> sSubTickets = sourceLanguageSubs.computeIfAbsent(sourceLanguageId, k -> new HashSet<>());
		sSubTickets.add(subTicket);

		Set<String> targetLanguageIds = subEntry.getValue();
		for (String targetLanguageId : targetLanguageIds) {
		    Set<String> tSubTickets = targetLanguageSubs.computeIfAbsent(targetLanguageId,
			    k -> new HashSet<>());
		    tSubTickets.add(subTicket);
		}
	    }
	}

	List<LinkedComboItem> values1 = new ArrayList<>();

	for (Entry<String, Set<String>> entry : sourceLanguageSubs.entrySet()) {
	    String sourceLangueId = entry.getKey();
	    Set<String> subTickets = entry.getValue();

	    if (!userLanguageIds.contains(sourceLangueId)) {
		continue;
	    }

	    LinkedComboItem value1 = new LinkedComboItem();
	    value1.setTickets(subTickets);
	    value1.setValue(sourceLangueId);
	    value1.setName(Locale.get(sourceLangueId).getDisplayName());

	    values1.add(value1);
	}

	List<LinkedComboItem> values2 = new ArrayList<>();

	for (Entry<String, Set<String>> entry : targetLanguageSubs.entrySet()) {
	    String targetLangueId = entry.getKey();
	    Set<String> subTickets = entry.getValue();

	    if (!userLanguageIds.contains(targetLangueId)) {
		continue;
	    }

	    LinkedComboItem value2 = new LinkedComboItem();
	    value2.setTickets(subTickets);
	    value2.setValue(targetLangueId);
	    value2.setName(Locale.get(targetLangueId).getDisplayName());

	    values2.add(value2);
	}

	// LinkedMultiComboBoxDefaultValue value =
	// createSubmissionDefaultLanguageDirection(userLanguageIds);

	submissionLanguageCombo.setValues1(values1);
	submissionLanguageCombo.setValues2(values2);
	submissionLanguageCombo.setValue(new DoubleMultiComboBoxDefaultValue());
	submissionLanguageCombo.setRequired(false);
    }

    private static void configureSubmissionProjectComboBox(SubmissionService submissionService,
	    MultiComboBox submissionProjectComboBox, TmUserProfile userProfile) {
	Map<Long, Map<String, Set<String>>> submissionUserLanguages = userProfile.getSubmissionUserLanguages();
	if (!submissionUserLanguages.isEmpty()) {
	    Set<Long> submissionIds = submissionUserLanguages.keySet();
	    List<TmProject> projects = submissionService.findProjectsBySubmissionIds(new ArrayList<>(submissionIds));
	    if (CollectionUtils.isNotEmpty(projects)) {
		List<SimpleComboItem> comboItems = new ArrayList<>();
		for (TmProject project : projects) {
		    SimpleComboItem comboItem = new SimpleComboItem();
		    comboItem.setName(project.getProjectInfo().getName());
		    comboItem.setValue(TicketConverter.fromInternalToDto(project.getProjectId()));
		    comboItems.add(comboItem);
		}
		submissionProjectComboBox.setValues(comboItems);
		submissionProjectComboBox.setRequired(false);
	    }
	}
    }

    private static void configureSubmissionStatusComboBox(MultiComboBox statusComboBox) {
	List<SimpleComboItem> values = new ArrayList<>();

	String inFinalReviewName = ItemStatusTypeHolder.IN_FINAL_REVIEW.getName();
	String inTranslationReviewName = ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName();
	String canceledName = ItemStatusTypeHolder.CANCELLED.getName();
	String completedName = ItemStatusTypeHolder.COMPLETED.getName();

	values.add(new SimpleComboItem(completedName, completedName));
	values.add(new SimpleComboItem(canceledName, canceledName));
	values.add(new SimpleComboItem(inFinalReviewName, inFinalReviewName));
	values.add(new SimpleComboItem(inTranslationReviewName, inTranslationReviewName));

	statusComboBox.setValues(values);
    }

    private static void configureSubmissionTermLanguageCombo(LinkedSingleComboBox submissionTermLanguageCombo,
	    TmUserProfile userProfile) {
	Set<String> userLanguageIds = new HashSet<>();

	Map<Long, Set<String>> projectUserLanguages = userProfile.getProjectUserLanguages();
	for (Entry<Long, Set<String>> entry : projectUserLanguages.entrySet()) {
	    userLanguageIds.addAll(entry.getValue());
	}

	Map<Long, Map<String, Set<String>>> submissionUserLanguages = userProfile.getSubmissionUserLanguages();

	Map<String, Set<String>> targetLanguageSubs = new HashMap<>();

	for (Entry<Long, Map<String, Set<String>>> entry : submissionUserLanguages.entrySet()) {
	    Long submissionId = entry.getKey();
	    Map<String, Set<String>> subLanguages = entry.getValue();

	    String subTicket = TicketConverter.fromInternalToDto(submissionId);

	    for (Entry<String, Set<String>> subEntry : subLanguages.entrySet()) {
		Set<String> targetLanguageIds = subEntry.getValue();
		for (String targetLanguageId : targetLanguageIds) {
		    Set<String> tSubTickets = targetLanguageSubs.computeIfAbsent(targetLanguageId,
			    k -> new HashSet<>());
		    tSubTickets.add(subTicket);
		}
	    }
	}

	List<LinkedComboItem> items = new ArrayList<>();

	for (Entry<String, Set<String>> entry : targetLanguageSubs.entrySet()) {
	    String targetLangueId = entry.getKey();
	    Set<String> subTickets = entry.getValue();

	    if (!userLanguageIds.contains(targetLangueId)) {
		continue;
	    }

	    LinkedComboItem item = new LinkedComboItem();
	    item.setTickets(subTickets);
	    item.setValue(targetLangueId);
	    Language language = Language.valueOf(targetLangueId);
	    item.setName(language.getDisplayName());
	    item.setDirection(language.getLanguageAlignment().getValue());

	    items.add(item);
	}

	String defaultLanguage = createSubmissionTermDefaultLanguage(userLanguageIds);

	submissionTermLanguageCombo.setValues(items);
	submissionTermLanguageCombo.setValue(defaultLanguage);
    }

    private static void configureSubmissionUsersComboBox(TmUserProfile user,
	    DoubleMultiComboBox submissionUsersComboBox, SubmissionService submissionService) {
	Set<Long> submissionIds = user.getSubmissionUserLanguages().keySet();

	Map<String, Set<String>> submissionUsers = submissionService
		.findSubmissionUsers(new ArrayList<>(submissionIds));

	String defaultSubmitter = null;
	String defaultAssignee = null;

	List<SimpleComboItem> submitters = new ArrayList<>();
	List<SimpleComboItem> assignees = new ArrayList<>();

	for (Entry<String, Set<String>> entry : submissionUsers.entrySet()) {
	    String submitter = entry.getKey();

	    if (defaultSubmitter == null) {
		defaultSubmitter = submitter;
	    }

	    SimpleComboItem submitterItem = new SimpleComboItem();
	    submitterItem.setName(submitter);
	    submitterItem.setValue(submitter);
	    submitters.add(submitterItem);

	    for (String assingee : entry.getValue()) {
		if (defaultAssignee == null) {
		    defaultAssignee = assingee;
		}

		SimpleComboItem assigneeItem = new SimpleComboItem();
		assigneeItem.setName(assingee);
		assigneeItem.setValue(assingee);
		assignees.add(assigneeItem);
	    }
	}

	List<String> defaultSubmitterValue = new ArrayList<>();
	defaultSubmitterValue.add(defaultSubmitter);

	List<String> defaultAssigneeValue = new ArrayList<>();
	defaultAssigneeValue.add(defaultAssignee);

	DoubleMultiComboBoxDefaultValue value = new DoubleMultiComboBoxDefaultValue();
	value.setValue1(defaultSubmitterValue);
	value.setValue2(defaultAssigneeValue);

	submissionUsersComboBox.setValues1(submitters);
	submissionUsersComboBox.setValues2(assignees);
	submissionUsersComboBox.setValue(value);
    }

    private static void configureTermName(InputTextAndComboBox termNameInputTextAndCombo) {
	List<SimpleComboItem> values = new ArrayList<>();

	String defaultSearch = ConcordanceType.DEFAULT.name();
	values.add(new SimpleComboItem(defaultSearch, defaultSearch));

	String exactSearch = ConcordanceType.EXACT.name();
	values.add(new SimpleComboItem(exactSearch, exactSearch));

	InputTextAndComboItem value = new InputTextAndComboItem(null, defaultSearch);
	termNameInputTextAndCombo.setValue(value);
	termNameInputTextAndCombo.setValues(values.toArray(new SimpleComboItem[values.size()]));
    }

    private static void configureTermStatusComboBox(MultiComboBox statusComboBox) {
	List<SimpleComboItem> values = new ArrayList<>();
	for (ItemStatusType status : ItemStatusTypeHolder.getTermStatusValues()) {
	    String name = status.getName();
	    SimpleComboItem comboItem = new SimpleComboItem(name, name);
	    values.add(comboItem);
	}

	statusComboBox.setValues(values);
    }

    private static void createDateSearchBox(Map<SearchCriteria, Object> bar, SearchCriteria dateCreated,
	    SearchCriteria dateModified) {

	GridSearchBox dateCreatedBox = new GridSearchBox();
	dateCreatedBox.setName(dateCreated.getControlName());

	GridSearchBox dateModifiedBox = new GridSearchBox();
	dateModifiedBox.setName(dateModified.getControlName());

	GridSearchItem fromDateItem = new GridSearchItem();
	fromDateItem.setType(DateSearchEnum.FROM_DATE.getControlType().getCommand());
	fromDateItem.setName(DateSearchEnum.FROM_DATE.getCommand());

	GridSearchItem toDateItem = new GridSearchItem();
	toDateItem.setType(DateSearchEnum.TO_DATE.getControlType().getCommand());
	toDateItem.setName(DateSearchEnum.TO_DATE.getCommand());

	List<GridSearchItem> items = new ArrayList<>();
	items.add(fromDateItem);
	items.add(toDateItem);

	dateCreatedBox.setItems(items);
	dateModifiedBox.setItems(items);

	bar.put(dateCreated, dateCreatedBox);
	bar.put(dateModified, dateModifiedBox);
    }

    private static LinkedComboBoxDefaultValue createDefaultLanguageDirection() {

	LinkedComboBoxDefaultValue defaultLanguageDirection = new LinkedComboBoxDefaultValue();

	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();

	List<String> languages = new ArrayList<>(getAllUserProjectLocales(userProfile));
	sortLanguageCodes(languages);

	Preferences preferences = userProfile.getPreferences();
	String sourceLanguage = preferences.getDefaultSourceLanguage();
	String targetLanguage = preferences.getDefaultTargetLanguage();

	String[] targetLanguages = null;
	if (targetLanguage != null) {
	    if (targetLanguage.contains(StringConstants.COMMA)) {
		targetLanguages = targetLanguage.split(StringConstants.COMMA);
	    } else {
		targetLanguages = new String[1];
		targetLanguages[0] = targetLanguage;
	    }
	}

	int size = languages.size();
	if (sourceLanguage != null && languages.contains(sourceLanguage)) {
	    defaultLanguageDirection.setValue1(sourceLanguage);
	} else {
	    if (size > 0) {
		sourceLanguage = languages.stream().filter(l -> l.contains(Locale.ENGLISH.getCode())).findFirst()
			.orElse(languages.get(0));
		defaultLanguageDirection.setValue1(sourceLanguage);
	    }
	}

	if (targetLanguages != null) {
	    for (String targetLang : targetLanguages) {
		if (languages.contains(targetLang)) {
		    defaultLanguageDirection.addValue2(targetLang);
		}
	    }
	} else {
	    if (size > 0) {
		Iterator<String> iterator = languages.iterator();
		targetLanguage = iterator.next();
		if (sourceLanguage.equals(targetLanguage) && iterator.hasNext()) {
		    targetLanguage = iterator.next();
		}
		defaultLanguageDirection.addValue2(targetLanguage);
	    }
	}

	return defaultLanguageDirection;
    }

    private static String createDefaultProjectOption(Set<Long> projectIds) {
	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();
	Preferences preferences = userProfile.getPreferences();
	Long defaultProjectId = preferences.getDefaultProjectId();

	String defaultProjectTicket = null;
	if (defaultProjectId != null && projectIds.contains(defaultProjectId)) {
	    defaultProjectTicket = TicketConverter.fromInternalToDto(defaultProjectId);
	} else {
	    if (!projectIds.isEmpty()) {
		defaultProjectId = projectIds.iterator().next();
		defaultProjectTicket = TicketConverter.fromInternalToDto(defaultProjectId);
	    }
	}

	return defaultProjectTicket;
    }

    private static void createEntityTypeSearchBox(Map<SearchCriteria, Object> bar, SearchCriteria searchCriteria) {

	GridSearchBox entityTypeSearchBox = new GridSearchBox();
	entityTypeSearchBox.setName(searchCriteria.getControlName());
	entityTypeSearchBox.setRequired(true);

	GridSearchItem attributeTermItem = new GridSearchItem();
	attributeTermItem.setName(EntitySearch.SEARCH_IN.getCommand());
	attributeTermItem.setType(EntitySearch.SEARCH_IN.getControlType().getCommand());
	attributeTermItem.setDefaultValues(Collections.singletonList(TypeSearchEnum.TERM.name()));

	String attributeName = MessageResolver.getMessage("TlEntityTypeAttribute");
	String termName = MessageResolver.getMessage("TlEntityTypeTerm");

	List<SimpleComboItem> attributeTermValues = new ArrayList<>();
	attributeTermValues.add(new SimpleComboItem(attributeName, TypeSearchEnum.ATTRIBUTES.name()));
	attributeTermValues.add(new SimpleComboItem(termName, TypeSearchEnum.TERM.name()));

	String sourceName = MessageResolver.getMessage("TlLanguageSearchSource");
	String targetName = MessageResolver.getMessage("TlLanguageSearchTarget");

	String sourceValue = LanguageSearchEnum.SOURCE.name();
	String targetValue = LanguageSearchEnum.TARGET.name();

	GridSearchItem sourceTargetItem = new GridSearchItem();
	sourceTargetItem.setName(EntitySearch.INCLUDE.getCommand());
	sourceTargetItem.setType(EntitySearch.INCLUDE.getControlType().getCommand());
	sourceTargetItem.setDefaultValues(new ArrayList<>(Arrays.asList(sourceName, targetValue)));

	List<SimpleComboItem> sourceTargetValues = new ArrayList<>();
	sourceTargetValues.add(new SimpleComboItem(sourceName, sourceValue));
	sourceTargetValues.add(new SimpleComboItem(targetName, targetValue));

	attributeTermItem.setValues(attributeTermValues);
	sourceTargetItem.setValues(sourceTargetValues);

	List<GridSearchItem> items = new ArrayList<>();
	items.add(attributeTermItem);
	items.add(sourceTargetItem);

	entityTypeSearchBox.setItems(items);

	bar.put(searchCriteria, entityTypeSearchBox);
    }

    private static void createHideBlanksSearchBox(Map<SearchCriteria, Object> bar, SearchCriteria searchCriteria) {

	GridSearchBox searchBox = new GridSearchBox();
	searchBox.setName(searchCriteria.getControlName());

	GridSearchItem item = new GridSearchItem();
	item.setName(searchCriteria.getControlName());
	item.setType(searchCriteria.getControlType().getCommand());

	GridSearchItem placeholder = createPlaceholderItem();

	List<GridSearchItem> items = new ArrayList<>();
	items.add(item);
	items.add(placeholder);

	searchBox.setItems(items);

	bar.put(searchCriteria, searchBox);
    }

    private static void createLanguageDirectionBox(Map<SearchCriteria, Object> bar, SearchCriteria searchCriteria,
	    Map<Long, Set<String>> projectUserLanguages) {

	LanguageSearchBox searchBox = new LanguageSearchBox();
	searchBox.setName(searchCriteria.getControlName());

	Map<String, Set<String>> languageProjectsMap = new HashMap<>();

	for (Entry<Long, Set<String>> entry : projectUserLanguages.entrySet()) {
	    Long projectId = entry.getKey();
	    String projectTicket = TicketConverter.fromInternalToDto(projectId);
	    Set<String> languageIds = entry.getValue();
	    for (String languageId : languageIds) {
		Set<String> projectTickets = languageProjectsMap.computeIfAbsent(languageId, k -> new HashSet<>());
		projectTickets.add(projectTicket);
	    }
	}

	List<LanguageDirectionItem> values = new ArrayList<>();

	for (Entry<String, Set<String>> entry : languageProjectsMap.entrySet()) {

	    String languageId = entry.getKey();
	    Set<String> projectTickets = entry.getValue();

	    LanguageDirectionItem item = new LanguageDirectionItem();
	    Language language = Language.valueOf(languageId);

	    item.setProjectTickets(projectTickets);
	    item.setName(language.getDisplayName());
	    item.setValue(languageId);
	    item.setDirection(language.getLanguageAlignment().getValue());

	    values.add(item);
	}

	sortLanguageList(values);

	LanguageSearch sourceSearch = new LanguageSearch();
	sourceSearch.setName(LanguageDirectionSearchEnum.SOURCE.getCommand());
	sourceSearch.setType(LanguageDirectionSearchEnum.SOURCE.getControlType().getCommand());
	sourceSearch.setValues(values);

	LanguageSearch targetSearch = new LanguageSearch();
	targetSearch.setName(LanguageDirectionSearchEnum.TARGET.getCommand());
	targetSearch.setType(LanguageDirectionSearchEnum.TARGET.getControlType().getCommand());
	targetSearch.setValues(values);

	configureDefaultLanguageValues(sourceSearch, targetSearch);

	List<LanguageSearch> items = new ArrayList<>();
	items.add(sourceSearch);
	items.add(targetSearch);

	searchBox.setItems(items);

	bar.put(searchCriteria, searchBox);
    }

    private static GridSearchItem createPlaceholderItem() {

	GridSearchItem placeHolderItem = new GridSearchItem();
	placeHolderItem.setType(ControlType.PLACEHOLDER.getCommand());
	return placeHolderItem;

    }

    private static void createProjectListSearchBox(Map<SearchCriteria, Object> bar, SearchCriteria searchCriteria,
	    ProjectService projectService, Set<Long> projectIds) {

	GridSearchBox projectBox = new GridSearchBox();
	projectBox.setRequired(true);
	projectBox.setName(searchCriteria.getControlName());

	List<GridSearchItem> items = new ArrayList<>();

	GridSearchItem placeholder = createPlaceholderItem();

	GridSearchItem projectItem = new GridSearchItem();
	projectItem.setName(searchCriteria.getControlName());
	projectItem.setType(searchCriteria.getControlType().getCommand());

	configureProjectItem(projectItem, projectService, projectIds);

	items.add(projectItem);
	items.add(placeholder);

	projectBox.setItems(items);

	bar.put(searchCriteria, projectBox);

    }

    private static String createSubmissionTermDefaultLanguage(Set<String> userLanguageIds) {
	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();

	Set<String> targetLanguages = getUserSubmissionTargetLocales(userProfile);

	Preferences preferences = userProfile.getPreferences();
	String targetLanguage = preferences.getDefaultTargetLanguage();

	if (targetLanguage == null || !targetLanguages.contains(targetLanguage)) {
	    if (!targetLanguages.isEmpty()) {
		for (String languageId : targetLanguages) {
		    if (!userLanguageIds.contains(languageId)) {
			continue;
		    }
		    targetLanguage = languageId;
		    break;
		}
	    }
	}

	return targetLanguage;
    }

    private static void createTermNameSearchBox(Map<SearchCriteria, Object> bar, SearchCriteria searchCriteria) {

	GridSearchBox termNameSearchBox = new GridSearchBox();
	termNameSearchBox.setName(searchCriteria.getControlName());

	String defaultValue = ConcordanceType.DEFAULT.name();
	String defaultName = MessageResolver.getMessage("ConcordanceTypeDefault");
	String exactValue = ConcordanceType.EXACT.name();
	String exactName = MessageResolver.getMessage("ConcordanceTypeExact");

	List<SimpleComboItem> values = new ArrayList<>();
	values.add(new SimpleComboItem(defaultName, defaultValue));
	values.add(new SimpleComboItem(exactName, exactValue));

	GridSearchItem optionsItem = new GridSearchItem();
	optionsItem.setValues(values);
	optionsItem.setName(TermNameSearchEnum.OPTIONS.getCommand());
	optionsItem.setType(TermNameSearchEnum.OPTIONS.getControlType().getCommand());

	GridSearchItem termItem = new GridSearchItem();
	termItem.setName(TermNameSearchEnum.TERM.getCommand());
	termItem.setType(TermNameSearchEnum.TERM.getControlType().getCommand());

	List<GridSearchItem> items = new ArrayList<>();
	items.add(termItem);
	items.add(optionsItem);

	termNameSearchBox.setItems(items);

	bar.put(searchCriteria, termNameSearchBox);
    }

    private static void createTermStatusesSearchBox(Map<SearchCriteria, Object> bar, SearchCriteria searchCriteria) {

	GridSearchBox termStatusesSearchBox = new GridSearchBox();
	termStatusesSearchBox.setName(searchCriteria.getControlName());

	GridSearchItem placeholderItem = createPlaceholderItem();

	GridSearchItem statusesItem = new GridSearchItem();
	statusesItem.setName(searchCriteria.getControlName());
	statusesItem.setType(searchCriteria.getControlType().getCommand());

	List<SimpleComboItem> values = new ArrayList<>();

	for (ItemStatusType status : ItemStatusTypeHolder.getTermStatusValues()) {
	    String value = status.getName();
	    String name = ItemStatusTypeHolder.getStatusDisplayName(value);
	    SimpleComboItem comboItem = new SimpleComboItem(name, value);
	    values.add(comboItem);
	}

	statusesItem.setValues(values);

	List<GridSearchItem> items = new ArrayList<>();
	items.add(statusesItem);
	items.add(placeholderItem);

	termStatusesSearchBox.setItems(items);

	bar.put(searchCriteria, termStatusesSearchBox);
    }

    private static void createUserSearchBox(Map<SearchCriteria, Object> bar, SearchCriteria createdUser,
	    SearchCriteria modifiedUser, List<String> allUsers) {

	GridSearchBox createdUserBox = new GridSearchBox();
	createdUserBox.setName(createdUser.getControlName());

	GridSearchItem createdUserItem = new GridSearchItem();
	createdUserItem.setName(createdUser.getControlName());
	createdUserItem.setType(createdUser.getControlType().getCommand());

	GridSearchBox modifiedUserBox = new GridSearchBox();
	modifiedUserBox.setName(modifiedUser.getControlName());

	GridSearchItem modifiedUserItem = new GridSearchItem();
	modifiedUserItem.setName(modifiedUser.getControlName());
	modifiedUserItem.setType(modifiedUser.getControlType().getCommand());

	SimpleComboItem item = new SimpleComboItem();
	List<SimpleComboItem> values = new ArrayList<>();

	for (String username : allUsers) {
	    item.setName(username);
	    item.setValue(username);
	}

	values.add(item);
	createdUserItem.setValues(values);
	modifiedUserItem.setValues(values);

	GridSearchItem placeHolderItem = createPlaceholderItem();

	List<GridSearchItem> createdItems = new ArrayList<>();
	createdItems.add(createdUserItem);
	createdItems.add(placeHolderItem);

	List<GridSearchItem> modifiedItems = new ArrayList<>();
	modifiedItems.add(modifiedUserItem);
	modifiedItems.add(placeHolderItem);

	createdUserBox.setItems(createdItems);
	modifiedUserBox.setItems(modifiedItems);

	bar.put(createdUser, createdUserBox);
	bar.put(modifiedUser, modifiedUserBox);

    }

    private static int getComparedValueForString(String val1, String val2) {
	boolean isFirstEnglish = isEnglish(val1);
	boolean isSecondEnglish = isEnglish(val2);

	boolean isBothStartWithEng = isFirstEnglish && isSecondEnglish;
	boolean isBothDontStartWithEng = (!isFirstEnglish && !isSecondEnglish);

	if (isBothStartWithEng || isBothDontStartWithEng) {
	    return val1.compareTo(val2);
	}

	if (isFirstEnglish) {
	    return -1;
	}

	return 1;
    }

    private static String[] getDefaultTargetLanguageFromPreferences(String targetLanguage) {

	if (targetLanguage.contains(StringConstants.COMMA)) {
	    return targetLanguage.split(StringConstants.COMMA);

	} else {
	    String[] targetLanguages = new String[1];
	    targetLanguages[0] = targetLanguage;
	    return targetLanguages;

	}
    }

    private static String getTargetLanguageFromUserLanguages(List<String> languages, String sourceLanguage) {

	Iterator<String> iterator = languages.iterator();
	String targetLanguage = iterator.next();
	return sourceLanguage.equals(targetLanguage) && iterator.hasNext() ? iterator.next() : targetLanguage;
    }

    private static Set<String> getUserSubmissionTargetLocales(TmUserProfile userProfile) {
	Set<String> languages = new HashSet<>();

	Map<Long, Map<String, Set<String>>> submissionUserLanguages = userProfile.getSubmissionUserLanguages();

	for (Entry<Long, Map<String, Set<String>>> entry : submissionUserLanguages.entrySet()) {
	    Map<String, Set<String>> value = entry.getValue();
	    for (Entry<String, Set<String>> subEntry : value.entrySet()) {
		languages.addAll(subEntry.getValue());
	    }
	}

	return languages;
    }

    private static boolean isEnglish(String locale) {
	String enLang = "English";
	return locale.startsWith(enLang);
    }

    private static String resolveDefaultSourceLanguage(String sourceLanguage, List<String> languages) {
	if (validateSourceLanguage(sourceLanguage, languages)) {
	    return sourceLanguage;
	}

	if (CollectionUtils.isNotEmpty(languages)) {
	    return languages.get(0);
	}

	return null;
    }

    private static void sortLanguageCodes(List<String> languageIds) {
	languageIds.sort(Comparator.comparing(o -> Locale.get(o).getDisplayName()));
    }

    private static void sortLanguageList(List<LanguageDirectionItem> items) {
	items.sort((obj1, obj2) -> {

	    String val1 = obj1.getName();
	    String val2 = obj2.getName();

	    return getComparedValueForString(val1, val2);
	});
    }

    private static void sortLanguagesWithEngOnFirstPlace(List<LinkedComboItem> items) {
	items.sort((obj1, obj2) -> {

	    String val1 = obj1.getName();
	    String val2 = obj2.getName();

	    return getComparedValueForString(val1, val2);

	});
    }

    private static void sortTasks(List<org.gs4tr.termmanager.model.dto.Task> tasks) {
	tasks.sort((task1, task2) -> {
	    if (task1 != null && task2 != null) {
		return task1.getWeight() < task2.getWeight() ? -1 : 1;
	    }
	    return 0;
	});
    }

    private static boolean validateSourceLanguage(String sourceLanguage, List<String> languages) {
	return sourceLanguage != null && languages.contains(sourceLanguage);

    }
}
