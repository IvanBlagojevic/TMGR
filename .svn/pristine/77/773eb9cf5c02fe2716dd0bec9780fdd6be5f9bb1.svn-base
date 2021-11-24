package org.gs4tr.termmanager.webmvc.jetty.junit.runner;

import static java.util.Collections.singletonMap;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.createPostRequest;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.sendRecieve;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.jetty.http.HttpTester.Request;
import org.eclipse.jetty.http.HttpTester.Response;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestCase;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.foundation3.core.utils.IdEncrypter;
import org.gs4tr.termmanager.model.NotificationProfile;
import org.gs4tr.termmanager.model.Preferences;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.webmvc.json.validator.JSONValidator;
import org.gs4tr.termmanager.webmvc.model.commands.PreferencesUpdateCommand;
import org.gs4tr.termmanager.webmvc.test.annotations.TestUser;
import org.gs4tr.termmanager.webmvc.test.configuration.RoleNameEnum;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.databind.JsonNode;

@TestSuite("preferencesUpdateController")
public class PreferencesUpdateControllerTest extends AbstractMvcTest {

    private static final String ON_SUBMIT_URL = "preferencesUpdate.ter";
    private static final String SHOW_FORM_URL = "preferencesShow.ter";
    @Captor
    private ArgumentCaptor<Preferences> _captor;

    @Before
    public void initMocks() {
	MockitoAnnotations.initMocks(this);
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("onSubmit")
    @TestUser(roleName = RoleNameEnum.SYSTEM_POWER_USER)
    public void onSubmitPostTest() throws IOException {
	when(getUserProfileService().loadUserProfileByUsername(DEFAULT_USERNAME)).thenReturn(getCurrentUserProfile());

	PreferencesUpdateCommand command = getModelObject("preferencesUpdateCommand", PreferencesUpdateCommand.class);

	Preferences preferences = getModelObject("preferences", Preferences.class);
	when(getUserProfileService().getUserPreferences(anyLong())).thenReturn(preferences);

	List<TmProject> tmProjects = getModelObject("tmProjects", List.class);
	when(getProjectService().findProjectByIds(anyListOf(Long.class))).thenReturn(tmProjects);

	List<NotificationProfile> notificationProfiles = getModelObject("notificationProfiles", List.class);
	when(getUserProfileService().getUserNotificationProfiles(anyLong())).thenReturn(notificationProfiles);

	Map<String, String> params = singletonMap(JSON_DATA_KEY, OBJECT_MAPPER.writeValueAsString(command));

	Request request = createPostRequest(ON_SUBMIT_URL, params, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);
	assertEquals(200, response.getStatus());

	JSONValidator responseContent = new JSONValidator(response.getContent());
	responseContent.assertProperty(SUCCESS_KEY, String.valueOf(true));
	assertNotNull(responseContent.getObject("searchBar"));

	verify(getUserProfileService()).addOrUpdateNotificationProfiles(eq(getCurrentUserProfile()),
		anyListOf(NotificationProfile.class));
	verify(getUserProfileService()).updatePreferences(getCaptor().capture());

	Preferences updatedPreferences = getCaptor().getValue();

	assertEquals(command.getActionSize(), updatedPreferences.getActionSize());
	assertEquals(command.getActionTextVisible(), updatedPreferences.getActionTextVisible());
	assertEquals(command.getItemsPerPage(), updatedPreferences.getItemsPerPage());
	assertEquals(command.getRefreshPeriod(), updatedPreferences.getRefreshPeriod());
	assertEquals(command.getLanguage(), updatedPreferences.getLanguage());
	assertEquals(command.getDefaultSourceLanguage(), updatedPreferences.getDefaultSourceLanguage());
	assertEquals(command.getDefaultTargetLanguage(), updatedPreferences.getDefaultTargetLanguage());
	assertEquals(TicketConverter.fromDtoToInternal(command.getDefaultProject(), Long.class),
		updatedPreferences.getDefaultProjectId());
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("showForm")
    @TestUser(roleName = RoleNameEnum.SYSTEM_SUPER_USER)
    public void showFormPostTest() throws IOException {
	when(getUserProfileService().loadUserProfileByUsername(DEFAULT_USERNAME)).thenReturn(getCurrentUserProfile());

	List<TmProject> tmProjects = getModelObject("tmProjects", List.class);

	UserProfileService userProfileService = getUserProfileService();

	when(userProfileService.isAssigneeUser(anyLong())).thenReturn(Boolean.TRUE);
	when(userProfileService.isSubmitterUser(anyLong())).thenReturn(Boolean.TRUE);
	when(userProfileService.getUserPreferences(anyLong())).thenReturn(new Preferences());
	when(getProjectService().findProjectByIds(anyListOf(Long.class))).thenReturn(tmProjects);

	Request request = createPostRequest(SHOW_FORM_URL, null, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	JSONValidator responseContent = new JSONValidator(response.getContent());

	responseContent.assertProperty(SUCCESS_KEY, String.valueOf(true));

	Long userProfileId = getCurrentUserProfile().getUserProfileId();
	verify(userProfileService).isAssigneeUser(eq(userProfileId));
	verify(userProfileService).isSubmitterUser(eq(userProfileId));
	verify(getProjectService()).findProjectByIds(anyListOf(Long.class));

	JSONValidator skype = responseContent.getObject("allProjects").getObjectFromArray("projectName", "Skype");
	skype.getObject("projectTicket").assertProperty("ticketId", IdEncrypter.encryptGenericId(1));

	JSONValidator availableLanguages = responseContent.getObject("availableLanguages");
	assertNotNull(availableLanguages.getObjectFromArray("locale", Locale.US.getCode()));
	assertNotNull(availableLanguages.getObjectFromArray("locale", Locale.GERMANY.getCode()));
	assertNotNull(availableLanguages.getObjectFromArray("locale", Locale.FRANCE.getCode()));

	responseContent.assertProperty("translatorUser", String.valueOf(false));

	JsonNode mapResponse = OBJECT_MAPPER.readTree(response.getContent());
	JsonNode data = mapResponse.get("data");
	assertTrue(data.size() > 0);

	JSONValidator emailScheduling = responseContent.getObject("emailScheduling");
	emailScheduling.assertProperty("dayOfWeek", "6").assertProperty("weeklyHour", "17").assertProperty("dailyHour",
		"17");

	JSONValidator notifications = responseContent.getObject("notifications");
	assertNotNull(notifications.getObjectFromArray("classifier", "readyForTranslation"));
	assertNotNull(notifications.getObjectFromArray("classifier", "translationCanceled"));
	assertNotNull(notifications.getObjectFromArray("classifier", "translationCompleted"));
	assertDefaultTmNotificationReport(notifications, "classifier", "addedApprovedTerm");
	assertDefaultTmNotificationReport(notifications, "classifier", "addedPendingTerm");
	assertDefaultTmNotificationReport(notifications, "classifier", "approveTerm");
	assertDefaultTmNotificationReport(notifications, "classifier", "deletedTerm");
	assertDefaultTmNotificationReport(notifications, "classifier", "demoteTerm");
	assertDefaultTmNotificationReport(notifications, "classifier", "editTerm");
	assertDefaultTmNotificationReport(notifications, "classifier", "addedBlacklistedTerm");
	assertDefaultTmNotificationReport(notifications, "classifier", "blacklistTerm");
	assertDefaultTmNotificationReport(notifications, "classifier", "onHoldTerm");
	assertDefaultTmNotificationReport(notifications, "classifier", "addedOnHoldTerm");
    }

    private void assertDefaultTmNotificationReport(JSONValidator jsonObj, String keyPath, String value) {
	JSONValidator notification = jsonObj.getObjectFromArray(keyPath, value);
	assertNotNull(notification);
	assertEquals("false", notification.getObject("showOnDashboardSelected").toString());
	assertEquals("false", notification.getObject("sendTaskMail").toString());
	assertEquals("false", notification.getObject("showOnDashboard").toString());
	assertEquals("true", notification.getObject("sendDailyMail").toString());
	assertEquals("NORMAL", notification.getObject("currentPriority").toString().replaceAll("\"", ""));
	assertEquals("true", notification.getObject("sendMail").toString());
	assertEquals("false", notification.getObject("sendWeeklyMailSelected").toString());
	assertEquals("false", notification.getObject("sendDailyMailSelected").toString());
	assertEquals("statisticReport", notification.getObject("category").toString().replaceAll("\"", ""));
	assertEquals("true", notification.getObject("sendWeeklyMail").toString());
	assertEquals("false", notification.getObject("sendTaskMailSelected").toString());
    }

    private ArgumentCaptor<Preferences> getCaptor() {
	return _captor;
    }
}
