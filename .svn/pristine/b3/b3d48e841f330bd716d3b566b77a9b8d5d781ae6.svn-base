package org.gs4tr.termmanager.webmvc.controllers;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gs4tr.foundation.modules.entities.model.UserProfileContext;
import org.gs4tr.foundation3.core.utils.IdEncrypter;
import org.gs4tr.termmanager.model.NotificationProfile;
import org.gs4tr.termmanager.model.Preferences;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.webmvc.model.TestCase;
import org.gs4tr.termmanager.webmvc.model.TestSuite;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.JsonNode;

@TestSuite("controllers")
public class PreferencesUpdateControllerTest extends AbstractControllerTest {

    private static final String URL_POST = "preferencesUpdate.ter";
    private static final String URL_SHOW = "preferencesShow.ter";

    @Test
    @SuppressWarnings("unchecked")
    @TestCase("preferencesUpdateController")
    public void preferencesUpdateGETTest_1() throws Exception {
	mockObjects();
	MockHttpServletRequestBuilder get = MockMvcRequestBuilders.post("/" + URL_SHOW);

	ResultActions resultActions = _mockMvc.perform(get);

	verify(getUserProfileService(), times(1)).getUserPreferences(any(Long.class));
	verify(getProjectService(), times(1)).findProjectByIds(any(List.class));
	verify(getUserProfileService(), times(1)).getUserNotificationProfiles(any(Long.class));

	resultActions.andExpect(status().isOk());

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	String result = response.getContentAsString();

	Assert.assertNotNull(result);

	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);
	Assert.assertNotNull(resultNode);

	validateResultsWithClassifiers(resultNode);
	validateEmailScheduling(resultNode);
	validateDefaultSourceTargetProject(resultNode);
    }

    @Test
    @SuppressWarnings("unchecked")
    @TestCase("preferencesUpdateController")
    public void preferencesUpdateGETTest_2() throws Exception {
	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();
	List<NotificationProfile> notificationProfiles = getModelObject("notificationProfile", List.class);
	userProfile.setNotificationProfiles(notificationProfiles);
	UserProfileContext.setCurrentUserProfile(userProfile);

	Preferences preferences = getModelObject("preferences2", Preferences.class);
	userProfile.setPreferences(preferences);

	List<TmProject> tmProjects = getModelObject("tmProjects", List.class);

	when(getUserProfileService().getUserPreferences(any(Long.class))).thenReturn(preferences);
	when(getProjectService().findProjectByIds(any(List.class))).thenReturn(tmProjects);
	when(getUserProfileService().getUserNotificationProfiles(any(Long.class))).thenReturn(notificationProfiles);

	MockHttpServletRequestBuilder get = MockMvcRequestBuilders.post("/" + URL_SHOW);

	ResultActions resultActions = _mockMvc.perform(get);

	verify(getUserProfileService(), times(1)).getUserPreferences(any(Long.class));
	verify(getProjectService(), times(1)).findProjectByIds(any(List.class));
	verify(getUserProfileService(), times(1)).getUserNotificationProfiles(any(Long.class));

	resultActions.andExpect(status().isOk());

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	String result = response.getContentAsString();

	Assert.assertNotNull(result);

	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);
	Assert.assertNotNull(resultNode);

	validateResultsWithClassifiers(resultNode);
	validateEmailScheduling(resultNode);
    }

    @Test
    @TestCase("preferencesUpdateController")
    public void preferencesUpdatePOSTBasicSettingsTest() throws Exception {
	mockObjects();
	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/" + URL_POST);

	String notificationConfiguration = getJsonData("notificationConfiguration.json");

	post.param("notificationConfiguration", notificationConfiguration);

	ResultActions resultActions = _mockMvc.perform(post);

	resultActions.andExpect(status().isOk());

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	String result = response.getContentAsString();

	Assert.assertNotNull(result);

	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);
	Assert.assertNotNull(resultNode);

	JsonNode successNode = resultNode.get("success");
	Assert.assertNotNull(successNode);
	Assert.assertTrue(successNode.asBoolean());

	JsonNode searchBarNode = resultNode.get("searchBar");
	Assert.assertNotNull(searchBarNode);
    }

    @Test
    @TestCase("preferencesUpdateController")
    public void preferencesUpdatePOSTTest() throws Exception {
	mockObjects();
	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/" + URL_POST);

	String notificationConfiguration = getJsonData("notificationConfiguration.json");
	String emailScheduling = getJsonData("emailScheduling.json");

	String id1 = IdEncrypter.encryptGenericId(1);
	String id2 = IdEncrypter.encryptGenericId(2);

	post.param("notificationConfiguration", notificationConfiguration);
	post.param("emailScheduling", emailScheduling);
	post.param("language", "en-US");
	post.param("actionSize", "large");
	post.param("actionTextVisible", "true");
	post.param("itemsPerPage", "50");
	post.param("refreshPeriod", "30");
	post.param("timezone", "Europe/Vienna");
	post.param("defaultSourceLanguage", "en-US");
	post.param("defaultTargetLanguage", "de-DE");
	post.param("dashboardProjects", id1, id2);

	ResultActions resultActions = _mockMvc.perform(post);

	resultActions.andExpect(status().isOk());

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	String result = response.getContentAsString();

	Assert.assertNotNull(result);

	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);
	Assert.assertNotNull(resultNode);

	JsonNode successNode = resultNode.get("success");
	Assert.assertNotNull(successNode);
	Assert.assertTrue(successNode.asBoolean());

	JsonNode searchBarNode = resultNode.get("searchBar");
	Assert.assertNotNull(searchBarNode);
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("preferencesUpdateController")
    public void preferencesUpdateUserAssigneeTest() throws Exception {
	mockObjects();
	when(getUserProfileService().isAssigneeUser(any(Long.class))).thenReturn(true);

	MockHttpServletRequestBuilder get = MockMvcRequestBuilders.post("/" + URL_SHOW);

	ResultActions resultActions = _mockMvc.perform(get);

	verify(getUserProfileService(), times(1)).isAssigneeUser(any(Long.class));
	verify(getProjectService(), times(1)).findProjectByIds(any(List.class));
	verify(getUserProfileService(), times(1)).getUserNotificationProfiles(any(Long.class));

	resultActions.andExpect(status().isOk());

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	String result = response.getContentAsString();

	Assert.assertNotNull(result);

	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);
	Assert.assertNotNull(resultNode);

	validateResults(resultNode);
	validateDefaultSourceTargetProject(resultNode);
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("preferencesUpdateController")
    public void preferencesUpdateUserSubmitterTest() throws Exception {
	mockObjects();
	when(getUserProfileService().isSubmitterUser(any(Long.class))).thenReturn(true);

	MockHttpServletRequestBuilder get = MockMvcRequestBuilders.post("/" + URL_SHOW);

	ResultActions resultActions = _mockMvc.perform(get);

	verify(getUserProfileService(), times(1)).isSubmitterUser(any(Long.class));
	verify(getProjectService(), times(1)).findProjectByIds(any(List.class));
	verify(getUserProfileService(), times(1)).getUserNotificationProfiles(any(Long.class));

	resultActions.andExpect(status().isOk());

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	String result = response.getContentAsString();

	Assert.assertNotNull(result);

	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);
	Assert.assertNotNull(resultNode);

	validateResultsWithClassifiers(resultNode);
	validateEmailScheduling(resultNode);
	validateDefaultSourceTargetProject(resultNode);
    }

    @SuppressWarnings("unchecked")
    private void mockObjects() {
	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();
	List<NotificationProfile> notificationProfiles = getModelObject("notificationProfile", List.class);
	userProfile.setNotificationProfiles(notificationProfiles);
	UserProfileContext.setCurrentUserProfile(userProfile);

	Preferences preferences = getModelObject("preferences", Preferences.class);
	userProfile.setPreferences(preferences);

	List<TmProject> tmProjects = getModelObject("tmProjects", List.class);

	when(getUserProfileService().getUserPreferences(any(Long.class))).thenReturn(preferences);
	when(getProjectService().findProjectByIds(any(List.class))).thenReturn(tmProjects);
	when(getUserProfileService().getUserNotificationProfiles(any(Long.class))).thenReturn(notificationProfiles);
    }

    private void validateDefaultSourceTargetProject(JsonNode resultNode) {
	JsonNode defSourceNode = resultNode.get("defaultSourceLanguage");
	Assert.assertNotNull(defSourceNode);

	JsonNode defTargetNode = resultNode.get("defaultTargetLanguage");
	Assert.assertNotNull(defTargetNode);

	JsonNode defProjectNode = resultNode.get("defaultProject");
	Assert.assertNotNull(defProjectNode);
    }

    private void validateEmailScheduling(JsonNode resultNode) {
	JsonNode emailSchedulingNode = resultNode.get("emailScheduling");
	Assert.assertNotNull(emailSchedulingNode);
	Assert.assertNotNull(emailSchedulingNode.findValue("weeklyHour"));
	Assert.assertNotNull(emailSchedulingNode.findValue("dailyHour"));
	Assert.assertNotNull(emailSchedulingNode.findValue("dayOfWeek"));
    }

    private void validateResults(JsonNode resultNode) {

	JsonNode successNode = resultNode.get("success");
	Assert.assertNotNull(successNode);
	Assert.assertTrue(successNode.asBoolean());

	JsonNode notificationsNode = resultNode.get("notifications");
	Assert.assertNotNull(notificationsNode);

	Iterator<JsonNode> notifications = notificationsNode.elements();
	Assert.assertTrue(notifications.hasNext());

	while (notifications.hasNext()) {
	    JsonNode notification = notifications.next();
	    Assert.assertNotNull(notification.findValue("sendDailyMail"));
	    Assert.assertNotNull(notification.findValue("category"));
	    Assert.assertNotNull(notification.findValue("showOnDashboard"));
	    Assert.assertNotNull(notification.findValue("sendWeeklyMail"));
	    Assert.assertNotNull(notification.findValue("sendTaskMailSelected"));
	    Assert.assertNotNull(notification.findValue("showOnDashboardSelected"));
	    Assert.assertNotNull(notification.findValue("sendDailyMailSelected"));
	    Assert.assertNotNull(notification.findValue("classifier"));
	    Assert.assertNotNull(notification.findValue("sendWeeklyMailSelected"));
	    Assert.assertNotNull(notification.findValue("sendTaskMail"));
	    Assert.assertNotNull(notification.findValue("currentPriority"));
	    Assert.assertNotNull(notification.findValue("sendMail"));
	}

	JsonNode dataNode = resultNode.get("data");
	Assert.assertNotNull(dataNode);

	Iterator<JsonNode> datas = dataNode.elements();
	Assert.assertTrue(datas.hasNext());

	while (datas.hasNext()) {
	    JsonNode data = datas.next();
	    Assert.assertNotNull(data);
	}

	JsonNode availableLanguagesNode = resultNode.get("availableLanguages");
	Assert.assertNotNull(availableLanguagesNode);

	Iterator<JsonNode> availableLanguages = availableLanguagesNode.elements();
	Assert.assertTrue(availableLanguages.hasNext());

	while (availableLanguages.hasNext()) {
	    JsonNode availableLanguage = availableLanguages.next();
	    Assert.assertNotNull(availableLanguage);
	    Assert.assertNotNull(availableLanguage.findValue("value"));
	    Assert.assertNotNull(availableLanguage.findValue("locale"));
	}

	JsonNode allProjectsNode = resultNode.get("allProjects");
	Assert.assertNotNull(allProjectsNode);
	Assert.assertNotNull(allProjectsNode.findValue("projectName"));
	Assert.assertNotNull(allProjectsNode.findValue("projectTicket"));

    }

    private void validateResultsWithClassifiers(JsonNode resultNode) {

	List<String> notificationReportClassifiers = new ArrayList<>();
	notificationReportClassifiers.add("addedApprovedTerm");
	notificationReportClassifiers.add("addedPendingTerm");
	notificationReportClassifiers.add("approveTerm");
	notificationReportClassifiers.add("deletedTerm");
	notificationReportClassifiers.add("demoteTerm");
	notificationReportClassifiers.add("addedBlacklistedTerm");
	notificationReportClassifiers.add("blacklistTerm");
	notificationReportClassifiers.add("addedOnHoldTerm");
	notificationReportClassifiers.add("onHoldTerm");
	notificationReportClassifiers.add("editTerm");

	JsonNode successNode = resultNode.get("success");
	Assert.assertNotNull(successNode);
	Assert.assertTrue(successNode.asBoolean());

	JsonNode notificationsNode = resultNode.get("notifications");
	Assert.assertNotNull(notificationsNode);

	Iterator<JsonNode> notifications = notificationsNode.elements();
	Assert.assertTrue(notifications.hasNext());

	while (notifications.hasNext()) {
	    JsonNode notification = notifications.next();
	    Assert.assertNotNull(notification.findValue("sendDailyMail"));
	    Assert.assertNotNull(notification.findValue("category"));
	    Assert.assertNotNull(notification.findValue("showOnDashboard"));
	    Assert.assertNotNull(notification.findValue("sendWeeklyMail"));
	    Assert.assertNotNull(notification.findValue("sendTaskMailSelected"));
	    Assert.assertNotNull(notification.findValue("showOnDashboardSelected"));
	    Assert.assertNotNull(notification.findValue("sendDailyMailSelected"));
	    Assert.assertNotNull(notification.findValue("sendWeeklyMailSelected"));
	    Assert.assertNotNull(notification.findValue("sendTaskMail"));
	    Assert.assertNotNull(notification.findValue("currentPriority"));
	    Assert.assertNotNull(notification.findValue("sendMail"));

	    JsonNode classifier = notification.findValue("classifier");
	    Assert.assertNotNull(classifier);
	    notificationReportClassifiers.remove(classifier.toString().replaceAll("\"", ""));
	}

	Assert.assertEquals(0, notificationReportClassifiers.size());

	JsonNode dataNode = resultNode.get("data");
	Assert.assertNotNull(dataNode);

	Iterator<JsonNode> datas = dataNode.elements();
	Assert.assertTrue(datas.hasNext());

	while (datas.hasNext()) {
	    JsonNode data = datas.next();
	    Assert.assertNotNull(data);
	}

	JsonNode availableLanguagesNode = resultNode.get("availableLanguages");
	Assert.assertNotNull(availableLanguagesNode);

	Iterator<JsonNode> availableLanguages = availableLanguagesNode.elements();
	Assert.assertTrue(availableLanguages.hasNext());

	while (availableLanguages.hasNext()) {
	    JsonNode availableLanguage = availableLanguages.next();
	    Assert.assertNotNull(availableLanguage);
	    Assert.assertNotNull(availableLanguage.findValue("value"));
	    Assert.assertNotNull(availableLanguage.findValue("locale"));
	}

	JsonNode allProjectsNode = resultNode.get("allProjects");
	Assert.assertNotNull(allProjectsNode);
	Assert.assertNotNull(allProjectsNode.findValue("projectName"));
	Assert.assertNotNull(allProjectsNode.findValue("projectTicket"));

    }

}
