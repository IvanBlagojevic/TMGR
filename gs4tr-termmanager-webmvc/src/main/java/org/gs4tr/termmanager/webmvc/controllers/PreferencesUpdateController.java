package org.gs4tr.termmanager.webmvc.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.StringConstants;
import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.foundation.modules.webmvc.controllers.AbstractController;
import org.gs4tr.foundation.modules.webmvc.filters.model.RefreshUserContext;
import org.gs4tr.foundation.modules.webmvc.model.ModelMapResponse;
import org.gs4tr.termmanager.model.LanguageComparator;
import org.gs4tr.termmanager.model.NotificationPriority;
import org.gs4tr.termmanager.model.NotificationProfile;
import org.gs4tr.termmanager.model.Preferences;
import org.gs4tr.termmanager.model.TmNotificationReportType;
import org.gs4tr.termmanager.model.TmNotificationType;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.dto.Language;
import org.gs4tr.termmanager.model.dto.converter.LanguageConverter;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.SubmissionService;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.webmvc.model.commands.EmailSchedulingCommand;
import org.gs4tr.termmanager.webmvc.model.commands.NotificationConfigurationCommand;
import org.gs4tr.termmanager.webmvc.model.commands.PreferencesUpdateCommand;
import org.gs4tr.termmanager.webmvc.model.search.SearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PreferencesUpdateController extends AbstractController {

    private static final String ALL_PROJECTS = "allProjects"; //$NON-NLS-1$
    private static final String AVAILABLE_LANGUAGES = "availableLanguages"; //$NON-NLS-1$
    private static final String CATEGORY_PROPERTY = "category"; //$NON-NLS-1$
    private static final String CLASSIFIER_PROPERTY = "classifier"; //$NON-NLS-1$
    private static final String CURRENT_PRIORITY = "currentPriority"; //$NON-NLS-1$
    private static final String DAILY_HOUR = "dailyHour"; //$NON-NLS-1$
    private static final String DAILY_MAIL_SELECTED_PROPERTY = "sendDailyMailSelected"; //$NON-NLS-1$
    private static final String DASHBOARD_SELECTED_PROPERTY = "showOnDashboardSelected"; //$NON-NLS-1$
    private static final String DAY_OF_WEEK = "dayOfWeek"; //$NON-NLS-1$
    private static final int DEFAULT_DAY_OF_WEEK = 6;
    private static final int DEFAULT_HOUR = 17;
    private static final String DEFAULT_PROJECT = "defaultProject"; //$NON-NLS-1$
    private static final Boolean DEFAULT_SELECTION_VALUE = false;
    private static final String DEFAULT_SOURCE_LANGUAGE = "defaultSourceLanguage"; //$NON-NLS-1$
    private static final String DEFAULT_TARGET_LANGUAGE = "defaultTargetLanguage"; //$NON-NLS-1$
    private static final String EMAIL_SCHEDULING = "emailScheduling"; //$NON-NLS-1$
    private static final String NOTIFICATIONS_PROPERTY = "notifications"; //$NON-NLS-1$
    private static final String PROJECT_NAME_PROPERTY = "projectName"; //$NON-NLS-1$
    private static final String PROJECT_TICKET_PROPERTY = "projectTicket"; //$NON-NLS-1$
    private static final String SEARCH_BAR = "searchBar"; //$NON-NLS-1$
    private static final String SEND_DAILY_MAIL = "sendDailyMail";
    private static final String SEND_MAIL_PROPERTY = "sendMail"; //$NON-NLS-1$
    private static final String SEND_TASK_MAIL = "sendTaskMail";
    private static final String SEND_TASK_MAIL_SELECTED = "sendTaskMailSelected";
    private static final String SEND_WEEKLY_MAIL = "sendWeeklyMail";
    private static final String SHOW_ON_DASHBOARD_PROPERTY = "showOnDashboard"; //$NON-NLS-1$
    private static final String STATISTIC_REPORT = "statisticReport"; //$NON-NLS-1$
    private static final String TASK_PERFORMED = "Task Performed"; //$NON-NLS-1$
    private static final String TRANSLATOR_USER = "translatorUser";
    private static final String WEEKLY_HOUR = "weeklyHour"; //$NON-NLS-1$
    private static final String WEEKLY_MAIL_SELECTED_PROPERTY = "sendWeeklyMailSelected"; //$NON-NLS-1$
    private static final String ZONES_DATA_PROPERTY = "data"; //$NON-NLS-1$

    private static final Log _logger = LogFactory.getLog(PreferencesUpdateController.class);

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private SubmissionService _submissionService;

    @Autowired
    private UserProfileService _userProfileService;

    @RequestMapping(value = "preferencesUpdate.ter", method = RequestMethod.POST)
    @RefreshUserContext
    @ResponseBody
    public ModelMapResponse onSubmit(HttpServletRequest request, HttpServletResponse response,
	    @ModelAttribute PreferencesUpdateCommand preferencesCommand) throws Exception {
	TmUserProfile currentUserProfile = TmUserProfile.getCurrentUserProfile();

	updatePreferences(currentUserProfile, preferencesCommand);
	updateNotificationProfiles(currentUserProfile, preferencesCommand);
	ModelMapResponse mapResponse = new ModelMapResponse();
	Map<SearchCriteria, Object> searchBar = ControllerUtils.createSearchBarElements(getProjectService(),
		getSubmissionService(), getUserProfileService());
	mapResponse.put(SEARCH_BAR, searchBar);

	return mapResponse;
    }

    @RequestMapping(value = "preferencesShow.ter", method = RequestMethod.POST)
    @ResponseBody
    public ModelMapResponse showForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
	ModelMapResponse mapResponse = new ModelMapResponse();

	String[] timezones = TimeZone.getAvailableIDs();

	List<String> allZonesList = new ArrayList<String>();

	for (String timezone : timezones) {
	    allZonesList.add(timezone);
	}

	mapResponse.put(ZONES_DATA_PROPERTY, allZonesList);

	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();

	Long userId = userProfile.getUserProfileId();

	boolean assigneeUser = getUserProfileService().isAssigneeUser(userId);
	boolean submitterUser = getUserProfileService().isSubmitterUser(userId);

	mapResponse.put(TRANSLATOR_USER, (assigneeUser && !submitterUser));

	List<NotificationProfile> notificationProfiles = getUserProfileService().getUserNotificationProfiles(userId);

	List<Object> notificationInfosData = createNotificationData(userId, notificationProfiles, mapResponse,
		assigneeUser, submitterUser);

	mapResponse.put(NOTIFICATIONS_PROPERTY, notificationInfosData);

	Set<String> languages = ControllerUtils.getAllUserProjectLocales(userProfile);

	Language[] dtoLanguages = LanguageConverter.fromInternalToDto(languages);

	Arrays.sort(dtoLanguages, new LanguageComparator());

	mapResponse.put(AVAILABLE_LANGUAGES, dtoLanguages);
	Preferences preferences = userProfile.getPreferences();
	String sourceLocale = preferences.getDefaultSourceLanguage();
	String targetLocale = preferences.getDefaultTargetLanguage();

	Set<Long> projectIds = userProfile.getProjectUserLanguages().keySet();

	List<TmProject> projects = getProjectService().findProjectByIds(new ArrayList<Long>(projectIds));

	List<Object> dtoProjects = new ArrayList<Object>();

	for (TmProject project : projects) {
	    Map<String, Object> infoData = new HashMap<String, Object>();
	    infoData.put(PROJECT_NAME_PROPERTY, project.getProjectInfo().getName());
	    infoData.put(PROJECT_TICKET_PROPERTY, new Ticket(project.getProjectId()));
	    dtoProjects.add(infoData);
	}

	mapResponse.put(ALL_PROJECTS, dtoProjects);

	if (sourceLocale != null) {
	    if (languages.contains(sourceLocale)) {
		mapResponse.put(DEFAULT_SOURCE_LANGUAGE, sourceLocale);
	    }
	}

	// handling target locales
	if (targetLocale != null) {
	    boolean valid = false;
	    if (targetLocale.contains(StringConstants.COMMA)) {
		StringBuilder builder = new StringBuilder();
		String[] targetLocales = targetLocale.split(StringConstants.COMMA);
		int counter = 1;
		int lenght = targetLocales.length;
		for (String target : targetLocales) {
		    if (languages.contains(target)) {
			builder.append(target);
			if (counter < lenght) {
			    builder.append(StringConstants.COMMA);
			}
		    }
		    counter++;
		}
		targetLocale = builder.toString();
		if (StringUtils.isNotEmpty(targetLocale)) {
		    valid = true;
		}
	    } else {
		if (languages.contains(targetLocale)) {
		    valid = true;
		}
	    }

	    if (valid) {
		mapResponse.put(DEFAULT_TARGET_LANGUAGE, targetLocale);
	    }
	}

	Long defaultProjectId = preferences.getDefaultProjectId();
	if (defaultProjectId != null) {
	    if (ControllerUtils.isProjectInProjectList(defaultProjectId, projects)) {
		Ticket defaultProjectTicket = new Ticket(defaultProjectId);
		mapResponse.put(DEFAULT_PROJECT, defaultProjectTicket.getTicketId());
	    }
	}

	return mapResponse;
    }

    private List<Object> createNotificationData(Long userId, List<NotificationProfile> notificationProfiles,
	    ModelMapResponse mapResponse, boolean assigneeUser, boolean submitterUser) {
	List<Object> notificationInfosData = new ArrayList<Object>();

	boolean creatEemailScheduling = true;

	if (submitterUser) {
	    if (_logger.isDebugEnabled()) {
		_logger.debug(Messages.getString("PreferencesUpdateController.14")); //$NON-NLS-1$
	    }
	    notificationInfosData.addAll(getTaskNotificationData(notificationProfiles));
	    notificationInfosData.addAll(getReportNotificationData(notificationProfiles));
	} else if (assigneeUser) {
	    if (_logger.isDebugEnabled()) {
		_logger.debug(Messages.getString("PreferencesUpdateController.17")); //$NON-NLS-1$
	    }
	    notificationInfosData.addAll(getTaskNotificationData(notificationProfiles));
	    creatEemailScheduling = false;
	} else {
	    notificationInfosData.addAll(getReportNotificationData(notificationProfiles));
	}

	if (creatEemailScheduling) {
	    Map<String, Integer> emailSchedulingData = getEmailSchedulingData();
	    mapResponse.put(EMAIL_SCHEDULING, emailSchedulingData);
	}

	return notificationInfosData;
    }

    private List<String> filterNotificationsByClassifier(List<String> classifiers,
	    org.gs4tr.termmanager.model.dto.NotificationProfile notificationProfile) {
	List<String> result = new ArrayList<String>();

	for (String classifier : classifiers) {
	    if (classifier.equals(notificationProfile.getClassifier())) {
		result.add(classifier);
	    }
	}
	return result;
    }

    private Map<String, Integer> getEmailSchedulingData() {
	TmUserProfile currentUserProfile = TmUserProfile.getCurrentUserProfile();

	Long userProfileId = currentUserProfile.getUserProfileId();

	UserProfileService userProfileService = getUserProfileService();

	Preferences preferences = userProfileService.getUserPreferences(userProfileId);

	Integer dayOfWeek = (preferences.getDayOfWeek() != null) ? preferences.getDayOfWeek() : DEFAULT_DAY_OF_WEEK;
	Integer weeklyHour = (preferences.getWeeklyHour() != null) ? preferences.getWeeklyHour() : DEFAULT_HOUR;
	Integer dailyHour = (preferences.getDailyHour() != null) ? preferences.getDailyHour() : DEFAULT_HOUR;

	Map<String, Integer> infoDataTime = new HashMap<String, Integer>();
	infoDataTime.put(DAY_OF_WEEK, dayOfWeek);
	infoDataTime.put(WEEKLY_HOUR, weeklyHour);
	infoDataTime.put(DAILY_HOUR, dailyHour);

	return infoDataTime;
    }

    private List<String> getNotificationClassifiers() {
	List<String> classifiers = new ArrayList<String>();

	List<TmNotificationType> notificationTypes = TmNotificationType.getNotificationTypes();
	for (TmNotificationType notificationType : notificationTypes) {
	    classifiers.add(notificationType.getClassifier());
	}

	for (TmNotificationReportType notificationReportType : TmNotificationReportType.values()) {
	    classifiers.add(notificationReportType.getClassifier());
	}

	return classifiers;
    }

    private ProjectService getProjectService() {
	return _projectService;
    }

    private List<Object> getReportNotificationData(List<NotificationProfile> notificationProfiles) {

	List<Object> notificationInfosData = new ArrayList<Object>();

	for (TmNotificationReportType notificationType : TmNotificationReportType.values()) {
	    String classifier = notificationType.getClassifier();

	    Map<String, Object> infoData = new HashMap<String, Object>();

	    infoData.put(CLASSIFIER_PROPERTY, classifier);
	    infoData.put(SEND_MAIL_PROPERTY, notificationType.isMailNotification());
	    infoData.put(SHOW_ON_DASHBOARD_PROPERTY, notificationType.isPopupNotication());
	    infoData.put(CATEGORY_PROPERTY, STATISTIC_REPORT);

	    populateWithCurrentData(notificationType.getClassifier(), notificationProfiles, infoData, false, true);

	    notificationInfosData.add(infoData);
	}

	return notificationInfosData;
    }

    private SubmissionService getSubmissionService() {
	return _submissionService;
    }

    private List<Object> getTaskNotificationData(List<NotificationProfile> notificationProfiles) {

	List<Object> notificationInfosData = new ArrayList<Object>();

	for (TmNotificationType notificationType : TmNotificationType.getNotificationTypes()) {
	    String classifier = notificationType.getClassifier();

	    Map<String, Object> infoData = new HashMap<String, Object>();

	    infoData.put(CLASSIFIER_PROPERTY, classifier);
	    infoData.put(SEND_MAIL_PROPERTY, notificationType.isMailNotification());
	    infoData.put(SHOW_ON_DASHBOARD_PROPERTY, notificationType.isPopupNotication());
	    infoData.put(CATEGORY_PROPERTY, TASK_PERFORMED);

	    populateWithCurrentData(notificationType.getClassifier(), notificationProfiles, infoData, true, false);

	    notificationInfosData.add(infoData);
	}

	return notificationInfosData;
    }

    private UserProfileService getUserProfileService() {
	return _userProfileService;
    }

    private void populateWithCurrentData(String classifier, List<NotificationProfile> notificationProfiles,
	    Map<String, Object> infoData, boolean showTaskPerformed, boolean showStatisticReport) {
	NotificationProfile profile = null;
	if (CollectionUtils.isNotEmpty(notificationProfiles)) {
	    for (NotificationProfile notificationProfile : notificationProfiles) {
		if (notificationProfile.getNotificationClassifier().equals(classifier)) {
		    profile = notificationProfile;
		    break;
		}
	    }
	}

	if (profile == null) {
	    infoData.put(CURRENT_PRIORITY, NotificationPriority.DEFAULT.name());
	    infoData.put(DASHBOARD_SELECTED_PROPERTY, DEFAULT_SELECTION_VALUE);
	    infoData.put(DAILY_MAIL_SELECTED_PROPERTY, DEFAULT_SELECTION_VALUE);
	    infoData.put(WEEKLY_MAIL_SELECTED_PROPERTY, DEFAULT_SELECTION_VALUE);
	    infoData.put(SEND_TASK_MAIL_SELECTED, DEFAULT_SELECTION_VALUE);
	    infoData.put(SEND_TASK_MAIL, showTaskPerformed);
	    infoData.put(SEND_WEEKLY_MAIL, showStatisticReport);
	    infoData.put(SEND_DAILY_MAIL, showStatisticReport);
	} else {
	    infoData.put(CURRENT_PRIORITY, profile.getNotificationPriority());
	    infoData.put(DASHBOARD_SELECTED_PROPERTY, profile.isDisplayDashboardNotification());
	    infoData.put(DAILY_MAIL_SELECTED_PROPERTY, profile.isSendDailyMailNotification());
	    infoData.put(WEEKLY_MAIL_SELECTED_PROPERTY, profile.isSendWeeklyMailNotification());
	    infoData.put(SEND_TASK_MAIL_SELECTED, profile.isSendTaskMailNotification());
	    infoData.put(SEND_TASK_MAIL, showTaskPerformed);
	    infoData.put(SEND_WEEKLY_MAIL, showStatisticReport);
	    infoData.put(SEND_DAILY_MAIL, showStatisticReport);
	}
    }

    private void updateNotificationProfiles(TmUserProfile currentUserProfile,
	    PreferencesUpdateCommand preferencesCommand) {
	String notificationConfiguration = preferencesCommand.getNotificationConfiguration();

	NotificationConfigurationCommand notificationPriorityTypesCommand = JsonUtils
		.readValue(notificationConfiguration, NotificationConfigurationCommand.class);

	org.gs4tr.termmanager.model.dto.NotificationProfile[] configuration = notificationPriorityTypesCommand
		.getConfiguration();

	List<String> classifiers = getNotificationClassifiers();

	List<NotificationProfile> newNotificationProfiles = new ArrayList<NotificationProfile>();

	for (org.gs4tr.termmanager.model.dto.NotificationProfile notificationProfile : configuration) {
	    List<String> filteredClassifiers = filterNotificationsByClassifier(classifiers, notificationProfile);

	    for (String notificationClassifier : filteredClassifiers) {
		NotificationProfile profile = new NotificationProfile();

		profile.setNotificationClassifier(notificationClassifier);
		profile.setNotificationPriority(NotificationPriority
			.valueOf(notificationProfile.getNotificationPriority().getNotificationPriorityName()));
		profile.setSendWeeklyMailNotification(notificationProfile.isSendWeeklyMailNotification());
		profile.setSendDailyMailNotification(notificationProfile.isSendDailyMailNotification());
		profile.setDisplayDashboardNotification(notificationProfile.isDisplayDashboardNotification());
		profile.setNotificationClassifier(notificationProfile.getClassifier());
		profile.setSendTaskMailNotification(notificationProfile.isSendTaskMailNotification());

		newNotificationProfiles.add(profile);
	    }
	}

	getUserProfileService().addOrUpdateNotificationProfiles(currentUserProfile, newNotificationProfiles);
    }

    private void updatePreferences(TmUserProfile currentUserProfile, PreferencesUpdateCommand preferencesCommand) {
	Preferences preferences = currentUserProfile.getPreferences();

	preferences.setActionSize(preferencesCommand.getActionSize());
	preferences.setActionTextVisible(preferencesCommand.getActionTextVisible());

	preferences.setItemsPerPage(preferencesCommand.getItemsPerPage());
	preferences.setRefreshPeriod(preferencesCommand.getRefreshPeriod());
	preferences.setLanguage(preferencesCommand.getLanguage());
	preferences.setDefaultProjectId(
		TicketConverter.fromDtoToInternal(preferencesCommand.getDefaultProject(), Long.class));
	String defaultSourceLanugage = null;
	String defaultTargetLanguage = null;
	defaultSourceLanugage = preferencesCommand.getDefaultSourceLanguage();

	defaultTargetLanguage = preferencesCommand.getDefaultTargetLanguage();

	if (defaultSourceLanugage != null) {
	    preferences.setDefaultSourceLanguage(defaultSourceLanugage);
	} else {
	    preferences.setDefaultSourceLanguage(null);
	}

	if (defaultTargetLanguage != null) {
	    preferences.setDefaultTargetLanguage(defaultTargetLanguage);
	} else {
	    preferences.setDefaultTargetLanguage(null);
	}

	String emailScheduling = preferencesCommand.getEmailScheduling();
	if (StringUtils.isNotEmpty(emailScheduling)) {
	    EmailSchedulingCommand emailSchedulingCommand = JsonUtils.readValue(emailScheduling,
		    EmailSchedulingCommand.class);

	    int dailyHour = emailSchedulingCommand.getDailyHour() != 0 ? emailSchedulingCommand.getDailyHour()
		    : DEFAULT_HOUR;
	    int weeklyHour = emailSchedulingCommand.getWeeklyHour() != 0 ? emailSchedulingCommand.getWeeklyHour()
		    : DEFAULT_HOUR;
	    int dayOfWeek = emailSchedulingCommand.getDayOfWeek() != 0 ? emailSchedulingCommand.getDayOfWeek()
		    : DEFAULT_DAY_OF_WEEK;
	    preferences.setDailyHour(dailyHour);
	    preferences.setWeeklyHour(weeklyHour);
	    preferences.setDayOfWeek(dayOfWeek);
	} else {
	    preferences.setDailyHour(DEFAULT_HOUR);
	    preferences.setWeeklyHour(DEFAULT_HOUR);
	    preferences.setDayOfWeek(DEFAULT_DAY_OF_WEEK);
	}

	getUserProfileService().updatePreferences(preferences);
    }
}
