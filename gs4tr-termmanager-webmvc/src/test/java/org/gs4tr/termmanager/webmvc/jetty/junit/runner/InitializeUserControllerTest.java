package org.gs4tr.termmanager.webmvc.jetty.junit.runner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.createPostRequest;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.sendRecieve;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jetty.http.HttpTester.Request;
import org.eclipse.jetty.http.HttpTester.Response;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestCase;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.foundation3.core.utils.IdEncrypter;
import org.gs4tr.foundation3.solr.model.concordance.ConcordanceType;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.search.ItemFolderEnum;
import org.gs4tr.termmanager.model.search.LanguageSearchEnum;
import org.gs4tr.termmanager.model.search.TypeSearchEnum;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.webmvc.controllers.InitializeUserController;
import org.gs4tr.termmanager.webmvc.json.validator.JSONValidator;
import org.gs4tr.termmanager.webmvc.model.search.ControlType;
import org.gs4tr.termmanager.webmvc.model.search.DateSearchEnum;
import org.gs4tr.termmanager.webmvc.model.search.EntitySearch;
import org.gs4tr.termmanager.webmvc.model.search.LanguageDirectionSearchEnum;
import org.gs4tr.termmanager.webmvc.model.search.SearchCriteria;
import org.gs4tr.termmanager.webmvc.model.search.TermNameSearchEnum;
import org.gs4tr.termmanager.webmvc.test.annotations.TestUser;
import org.gs4tr.termmanager.webmvc.test.configuration.RoleNameEnum;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

@TestSuite("initializeUserController")
public class InitializeUserControllerTest extends AbstractMvcTest {

    private static final boolean GRID_FEATURE = false;

    private static final String URL = "initializeUser.ter";

    @Test
    @TestCase("initializeSystemUser")
    @TestUser(roleName = RoleNameEnum.SYSTEM_SUPER_USER)
    @SuppressWarnings("unchecked")
    public void defaultSourceLanguageIsEnUSAndProjectUserLanguagesContainsAnotherEnLanguageTest() {
	when(getUserProfileService().loadUserProfileByUsername(DEFAULT_USERNAME)).thenReturn(getCurrentUserProfile());

	Map<Long, Set<String>> projectUserLanguages = getModelObject("projectUserLanguages2", Map.class);

	TmUserProfile profile = getCurrentUserProfile();
	profile.setProjectUserLanguages(projectUserLanguages);
	profile.getPreferences().setDefaultSourceLanguage("en-US");

	List<String> usernames = getModelObject("usernames", List.class);
	when(getUserProfileService().findAllNonGenerciUsernames()).thenReturn(usernames);

	Request request = createPostRequest(URL, null, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	String result = response.getContent();
	Assert.assertNotNull(result);

	JsonNode searchBar = JsonUtils.readValue(result, JsonNode.class).get("searchBar");
	Assert.assertNotNull(searchBar);

	validateDefaultSourceLang(searchBar, "en-GB");

    }

    @Test
    @TestCase("initializeSystemUser")
    @TestUser(roleName = RoleNameEnum.SYSTEM_SUPER_USER)
    @SuppressWarnings("unchecked")
    public void defaultSourceLanguageIsNullAndProjectUserLanguagesContainsEnLanguageTest() {
	when(getUserProfileService().loadUserProfileByUsername(DEFAULT_USERNAME)).thenReturn(getCurrentUserProfile());

	Map<Long, Set<String>> projectUserLanguages = getModelObject("projectUserLanguages", Map.class);

	TmUserProfile profile = getCurrentUserProfile();
	profile.setProjectUserLanguages(projectUserLanguages);

	List<String> usernames = getModelObject("usernames", List.class);
	when(getUserProfileService().findAllNonGenerciUsernames()).thenReturn(usernames);

	Request request = createPostRequest(URL, null, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	String result = response.getContent();
	Assert.assertNotNull(result);

	JsonNode searchBar = JsonUtils.readValue(result, JsonNode.class).get("searchBar");
	Assert.assertNotNull(searchBar);

	validateDefaultSourceLang(searchBar, "en-GB");

    }

    @Test
    @TestCase("initializeSystemUser")
    @TestUser(roleName = RoleNameEnum.SYSTEM_SUPER_USER)
    @SuppressWarnings("unchecked")
    public void defaultSourceLanguageIsNullAndProjectUserLanguagesDoesNotContainsEnLanguageTest() {
	when(getUserProfileService().loadUserProfileByUsername(DEFAULT_USERNAME)).thenReturn(getCurrentUserProfile());

	Map<Long, Set<String>> projectUserLanguages = getModelObject("projectUserLanguages1", Map.class);

	TmUserProfile profile = getCurrentUserProfile();
	profile.setProjectUserLanguages(projectUserLanguages);

	List<String> usernames = getModelObject("usernames", List.class);
	when(getUserProfileService().findAllNonGenerciUsernames()).thenReturn(usernames);

	Request request = createPostRequest(URL, null, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	String result = response.getContent();
	Assert.assertNotNull(result);

	JsonNode searchBar = JsonUtils.readValue(result, JsonNode.class).get("searchBar");
	Assert.assertNotNull(searchBar);

	validateDefaultSourceLang(searchBar, "bg-BG");

    }

    /*
     * TERII-5898 Server Side - Login Application Error
     */
    @SuppressWarnings("unchecked")
    @Test
    @TestCase("initializeAdminUser")
    @TestUser(roleName = RoleNameEnum.ADMIN)
    public void initializeAdminTest() {
	TmUserProfile profile = getCurrentUserProfile();
	profile.setProjectUserLanguages(new HashMap<>());

	when(getUserProfileService().loadUserProfileByUsername(DEFAULT_USERNAME)).thenReturn(getCurrentUserProfile());

	Request request = createPostRequest(URL, null, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);
	assertEquals(200, response.getStatus());

	String result = response.getContent();
	Assert.assertNotNull(result);
	Assert.assertTrue(!result.contains("Application error"));

	JsonNode searchBar = JsonUtils.readValue(result, JsonNode.class).get("searchBar");
	Assert.assertNotNull(searchBar);

	if (GRID_FEATURE) {
	    validateCreationUserSearchBox(searchBar);
	    validateModificationUserSearchBox(searchBar);
	    validateCreationDateSearchBox(searchBar);
	    validateTlCreationDateSearchBox(searchBar);
	    validateModificationDateSearchBox(searchBar);
	    validateTlModificationDateSearchBox(searchBar);
	    validateEntityTypeSearchBox(searchBar);
	    validateTlEntityTypeSearchBox(searchBar);
	    validateHideBlanksSearchBox(searchBar);
	    validateAdminLanguageDirectionSearchBox(searchBar);
	    validateTermNameSearchBox(searchBar);
	    validateTlTermNameSearchBox(searchBar);
	    validateTermStatusesSearchBox(searchBar);
	    validateTlTermStatusesSearchBox(searchBar);
	}
    }

    @Test
    @TestCase("initializeSystemUser")
    @TestUser(roleName = RoleNameEnum.SYSTEM_SUPER_USER)
    @SuppressWarnings("unchecked")
    public void initializeNewSearchBar() {
	when(getUserProfileService().loadUserProfileByUsername(DEFAULT_USERNAME)).thenReturn(getCurrentUserProfile());

	List<TmProject> tmProjects = getModelObject("tmProjects", List.class);

	List<String> usernames = getModelObject("usernames", List.class);

	when(getProjectService().findProjectByIds(anyListOf(Long.class))).thenReturn(tmProjects);
	when(getUserProfileService().findAllNonGenerciUsernames()).thenReturn(usernames);

	Request request = createPostRequest(URL, null, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	String result = response.getContent();
	Assert.assertNotNull(result);

	JsonNode searchBar = JsonUtils.readValue(result, JsonNode.class).get("searchBar");
	Assert.assertNotNull(searchBar);

	if (GRID_FEATURE) {
	    validateCreationUserSearchBox(searchBar);
	    validateModificationUserSearchBox(searchBar);
	    validateCreationDateSearchBox(searchBar);
	    validateTlCreationDateSearchBox(searchBar);
	    validateModificationDateSearchBox(searchBar);
	    validateTlModificationDateSearchBox(searchBar);
	    validateEntityTypeSearchBox(searchBar);
	    validateTlEntityTypeSearchBox(searchBar);
	    validateHideBlanksSearchBox(searchBar);
	    validateLanguageDirectionSearchBox(searchBar);
	    validateProjectListSearchBox(searchBar);
	    validateTermNameSearchBox(searchBar);
	    validateTlTermNameSearchBox(searchBar);
	    validateTermStatusesSearchBox(searchBar);
	    validateTlTermStatusesSearchBox(searchBar);
	}

    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("initializeSystemUser")
    @TestUser(roleName = RoleNameEnum.SYSTEM_SUPER_USER)
    public void initializeSyperUserTest() throws Exception {
	when(getUserProfileService().loadUserProfileByUsername(DEFAULT_USERNAME)).thenReturn(getCurrentUserProfile());

	List<TmProject> tmProjects = getModelObject("tmProjects", List.class);

	List<String> usernames = getModelObject("usernames", List.class);

	when(getProjectService().findProjectByIds(anyListOf(Long.class))).thenReturn(tmProjects);
	when(getUserProfileService().findAllNonGenerciUsernames()).thenReturn(usernames);

	Request request = createPostRequest(URL, null, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	verify(getProjectService()).findProjectByIds(eq(Arrays.asList(1L)));
	verify(getUserProfileService()).canSwitchUser(eq(getCurrentUserProfile()));
	verify(getUserProfileService()).findAllNonGenerciUsernames();
	verify(getRoleService()).findAllPolicies();

	JSONValidator responseContent = new JSONValidator(response.getContent());

	responseContent.assertProperty(InitializeUserController.THEME_COOKIE_NAME, "theme");

	JSONValidator avaliableThemes = responseContent.getObject(InitializeUserController.AVALIABLE_THEMES);
	avaliableThemes.assertSimpleArrayElement("classicAqua");
	avaliableThemes.assertSimpleArrayElement("classicDefault");

	JSONValidator menuConfig = responseContent.getObject("menuConfig");
	JSONValidator adminMenu = menuConfig.getObject("adminMenu");
	adminMenu.assertArrayFinish();

	JSONValidator userMenu = menuConfig.getObject("userMenu");

	JSONValidator projectDetails = userMenu.getObjectFromArray("id", ItemFolderEnum.PROJECTDETAILS.name());
	projectDetails.assertProperty("parent", ItemFolderEnum.PROJECTDETAILS.name())
		.assertProperty("url", "projectDetailView.ter").assertProperty("systemHidden", "false");

	JSONValidator termList = userMenu.getObjectFromArray("id", ItemFolderEnum.TERM_LIST.name());
	termList.assertProperty("parent", ItemFolderEnum.TERM_LIST.name()).assertProperty("url", "multilingualView.ter")
		.assertProperty("systemHidden", "false");

	JSONValidator submissionDetails = userMenu.getObjectFromArray("id", ItemFolderEnum.SUBMISSIONDETAILS.name());
	submissionDetails.assertProperty("url", "submissionDetailView.ter").assertProperty("systemHidden", "false")
		.assertProperty("parent", ItemFolderEnum.SUBMISSIONDETAILS.name());

	JSONValidator submissionTermList = userMenu.getObjectFromArray("id", ItemFolderEnum.SUBMISSIONTERMLIST.name());
	submissionTermList.assertProperty("url", "multilingualView.ter").assertProperty("systemHidden", "true")
		.assertProperty("parent", ItemFolderEnum.SUBMISSIONTERMLIST.name());

	JSONValidator preferences = responseContent.getObject("preferences");
	preferences.assertProperty("itemsPerPage", String.valueOf(200)).assertProperty("timezone", "EST");

	responseContent.assertProperty("canEditTerm", String.valueOf(true)).assertProperty("canAutoSave",
		String.valueOf(true));

	JSONValidator projectAddEditConfiguration = responseContent.getObject("projectAddEditConfiguration");

	final Long projectId = tmProjects.get(0).getProjectId();

	projectAddEditConfiguration.assertProperty(IdEncrypter.encryptGenericId(projectId), String.valueOf(true));

	JSONValidator project = responseContent.getObject("project");
	project.assertArrayFinish();

	JSONValidator userProfile = responseContent.getObject("userProfile");
	userProfile.assertProperty("generic", String.valueOf(false)).assertPropertyNull("tasks");

	JSONValidator systemRoles = userProfile.getObject("systemRoles");

	JSONValidator systemRole = systemRoles.getObjectFromArray("roleId", "system_super_user");
	assertNotNull(systemRole.getObject("roleType"));

	JSONValidator policies = systemRole.getObject("policies");
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_TM_VIEW_TRANSLATOR_INBOX"));
	assertNotNull(policies.getObjectFromArray("policyId", "POLICY_TM_MENU_TERMS"));

	JSONValidator system = responseContent.getObject("system");
	system.assertObjectFinish();

	JSONValidator maxInactiveInterval = responseContent.getObject("maxInactiveInterval");
	assertNotNull(maxInactiveInterval);

	assertNotNull(responseContent.getObject("languages"));

	assertNotNull(responseContent.getObject("searchBar"));
    }

    private void validateAdminLanguageDirectionSearchBox(JsonNode searchBar) {
	JsonNode languageDirection = searchBar.get(SearchCriteria.TL_LANGUAGE_DIRECTION.name());
	assertNotNull(languageDirection);
	assertEquals(languageDirection.get("name").asText(), SearchCriteria.TL_LANGUAGE_DIRECTION.getControlName());
	assertEquals(languageDirection.get("required").asText(), "true");
	assertEquals(languageDirection.get("items").size(), 2);

	JsonNode node1 = languageDirection.get("items").get(0);

	assertEquals(node1.get("type").asText(), LanguageDirectionSearchEnum.SOURCE.getControlType().getCommand());
	assertEquals(node1.get("name").asText(), LanguageDirectionSearchEnum.SOURCE.getCommand());

	assertTrue(node1.get("values").size() == 0);
	assertTrue(node1.get("defaultValues").size() == 0);

	JsonNode node2 = languageDirection.get("items").get(1);

	assertEquals(node2.get("type").asText(), LanguageDirectionSearchEnum.TARGET.getControlType().getCommand());
	assertEquals(node2.get("name").asText(), LanguageDirectionSearchEnum.TARGET.getCommand());
	assertTrue(node2.get("values").size() == 0);
	assertTrue(node2.get("defaultValues").size() == 0);

    }

    private void validateCreationDateSearchBox(JsonNode searchBar) {

	JsonNode dateCreated = searchBar.get(SearchCriteria.DATE_CREATED_RANGE.name());
	assertNotNull(dateCreated);
	assertEquals(dateCreated.get("name").asText(), SearchCriteria.DATE_CREATED_RANGE.getControlName());
	assertEquals(dateCreated.get("type").asText(), SearchCriteria.DATE_CREATED_RANGE.getControlType().toString());

    }

    private void validateCreationUserSearchBox(JsonNode searchBar) {
	JsonNode creationUser = searchBar.get(SearchCriteria.TL_CREATION_USERS.name());
	assertNotNull(creationUser);
	assertEquals(creationUser.get("name").asText(), SearchCriteria.TL_CREATION_USERS.getControlName());
	assertEquals(creationUser.get("required").asText(), "false");
	assertEquals(creationUser.get("items").size(), 2);

	JsonNode node1 = creationUser.get("items").get(0);
	JsonNode node2 = creationUser.get("items").get(1);

	assertEquals(node1.get("name").asText(), SearchCriteria.TL_CREATION_USERS.getControlName());
	assertEquals(node1.get("type").asText(), SearchCriteria.TL_CREATION_USERS.getControlType().getCommand());
	assertEquals(node2.get("type").asText(), ControlType.PLACEHOLDER.getCommand());
    }

    private void validateDefaultSourceLang(JsonNode searchBar, String languageCode) {

	JsonNode languageDirection = searchBar.get(SearchCriteria.LANGUAGE_DIRECTION.name());
	assertNotNull(languageDirection);

	String defaultSourceValues = languageDirection.get("value").get("value1").asText();
	assertNotNull(defaultSourceValues);

	assertTrue(languageCode.equals(defaultSourceValues));

    }

    private void validateEntityTypeSearchBox(JsonNode searchBar) {
	JsonNode entityType = searchBar.get(SearchCriteria.ENTITY_TYPE.name());
	assertNotNull(entityType);
	assertEquals(entityType.get("name").asText(), SearchCriteria.ENTITY_TYPE.getControlName());
	assertEquals(entityType.get("type").asText(), SearchCriteria.ENTITY_TYPE.getControlType().toString());
	assertEquals(entityType.get("required").asText(), "true");

	assertEquals(entityType.get("values1").size(), 2);

	JsonNode valuesNode1 = entityType.get("values1").get(0);
	JsonNode valuesNode2 = entityType.get("values1").get(1);

	assertEquals(valuesNode1.get("name").asText(), TypeSearchEnum.ATTRIBUTES.name());
	assertEquals(valuesNode1.get("value").asText(), TypeSearchEnum.ATTRIBUTES.name());
	assertEquals(valuesNode2.get("name").asText(), TypeSearchEnum.TERM.name());
	assertEquals(valuesNode2.get("value").asText(), TypeSearchEnum.TERM.name());

	assertEquals(entityType.get("values2").size(), 2);

	JsonNode valuesNode3 = entityType.get("values2").get(0);
	JsonNode valuesNode4 = entityType.get("values2").get(1);

	assertEquals(valuesNode3.get("name").asText(), LanguageSearchEnum.SOURCE.name());
	assertEquals(valuesNode3.get("value").asText(), LanguageSearchEnum.SOURCE.name());
	assertEquals(valuesNode4.get("name").asText(), LanguageSearchEnum.TARGET.name());
	assertEquals(valuesNode4.get("value").asText(), LanguageSearchEnum.TARGET.name());

	JsonNode value = entityType.get("value");

	assertEquals(value.size(), 2);

	JsonNode valuesNode5 = value.get("value1");
	JsonNode valuesNode6 = value.get("value2");

	assertEquals(valuesNode5.size(), 1);
	assertEquals(valuesNode5.get(0).asText(), TypeSearchEnum.TERM.name());

	assertEquals(valuesNode6.size(), 2);
	assertEquals(valuesNode6.get(0).asText(), LanguageSearchEnum.SOURCE.name());
	assertEquals(valuesNode6.get(1).asText(), LanguageSearchEnum.TARGET.name());
    }

    private void validateHideBlanksSearchBox(JsonNode searchBar) {
	JsonNode hideBlanks = searchBar.get(SearchCriteria.TL_HIDE_BLANKS.name());
	assertNotNull(hideBlanks);
	assertEquals(hideBlanks.get("name").asText(), SearchCriteria.TL_HIDE_BLANKS.getControlName());
	assertEquals(hideBlanks.get("items").size(), 2);

	JsonNode node = hideBlanks.get("items").get(0);

	assertEquals(node.get("name").asText(), SearchCriteria.TL_HIDE_BLANKS.getControlName());
	assertEquals(node.get("type").asText(), SearchCriteria.TL_HIDE_BLANKS.getControlType().getCommand());

    }

    private void validateLanguageDirectionSearchBox(JsonNode searchBar) {

	JsonNode languageDirection = searchBar.get(SearchCriteria.TL_LANGUAGE_DIRECTION.name());
	assertNotNull(languageDirection);
	assertEquals(languageDirection.get("name").asText(), SearchCriteria.TL_LANGUAGE_DIRECTION.getControlName());
	assertEquals(languageDirection.get("required").asText(), "true");
	assertEquals(languageDirection.get("items").size(), 2);

	JsonNode node1 = languageDirection.get("items").get(0);

	assertEquals(node1.get("type").asText(), LanguageDirectionSearchEnum.SOURCE.getControlType().getCommand());
	assertEquals(node1.get("name").asText(), LanguageDirectionSearchEnum.SOURCE.getCommand());

	assertTrue(node1.get("values").size() > 0);
	assertTrue(node1.get("defaultValues").size() > 0);

	JsonNode node2 = languageDirection.get("items").get(1);

	assertEquals(node2.get("type").asText(), LanguageDirectionSearchEnum.TARGET.getControlType().getCommand());
	assertEquals(node2.get("name").asText(), LanguageDirectionSearchEnum.TARGET.getCommand());
	assertTrue(node2.get("values").size() > 0);
	assertTrue(node2.get("defaultValues").size() > 0);

    }

    private void validateModificationDateSearchBox(JsonNode searchBar) {

	JsonNode dateModified = searchBar.get(SearchCriteria.DATE_MODIFIED_RANGE.name());
	assertNotNull(dateModified);

	assertEquals(dateModified.get("name").asText(), SearchCriteria.DATE_MODIFIED_RANGE.getControlName());
	assertEquals(dateModified.get("type").asText(), SearchCriteria.DATE_MODIFIED_RANGE.getControlType().toString());
    }

    private void validateModificationUserSearchBox(JsonNode searchBar) {
	JsonNode modificationUser = searchBar.get(SearchCriteria.TL_MODIFICATION_USERS.name());
	assertNotNull(modificationUser);
	assertEquals(modificationUser.get("name").asText(), SearchCriteria.TL_MODIFICATION_USERS.getControlName());
	assertEquals(modificationUser.get("required").asText(), "false");
	assertEquals(modificationUser.get("items").size(), 2);

	JsonNode node1 = modificationUser.get("items").get(0);
	JsonNode node2 = modificationUser.get("items").get(1);

	assertEquals(node1.get("name").asText(), SearchCriteria.TL_MODIFICATION_USERS.getControlName());
	assertEquals(node1.get("type").asText(), SearchCriteria.TL_MODIFICATION_USERS.getControlType().getCommand());
	assertEquals(node2.get("type").asText(), ControlType.PLACEHOLDER.getCommand());
    }

    private void validateProjectListSearchBox(JsonNode searchBar) {

	JsonNode projectList = searchBar.get(SearchCriteria.TL_PROJECT_LIST.name());
	assertNotNull(projectList);
	assertEquals(projectList.get("name").asText(), SearchCriteria.TL_PROJECT_LIST.getControlName());
	assertEquals(projectList.get("required").asText(), "true");
	assertEquals(projectList.get("items").size(), 2);

	JsonNode node1 = projectList.get("items").get(0);
	JsonNode node2 = projectList.get("items").get(1);

	assertEquals(node1.get("name").asText(), SearchCriteria.TL_PROJECT_LIST.getControlName());
	assertEquals(node1.get("type").asText(), SearchCriteria.TL_PROJECT_LIST.getControlType().getCommand());
	assertEquals(node1.get("values").size(), 1);
	assertEquals(node1.get("defaultValues").size(), 1);

	assertEquals(node2.get("type").asText(), ControlType.PLACEHOLDER.getCommand());
    }

    private void validateTermNameSearchBox(JsonNode searchBar) {

	JsonNode termName = searchBar.get(SearchCriteria.TERM_NAME.name());
	assertNotNull(termName);
	assertEquals(termName.get("name").asText(), SearchCriteria.TERM_NAME.getControlName());
	assertEquals(termName.get("type").asText(), SearchCriteria.TERM_NAME.getControlType().toString());

	JsonNode values = termName.get("values");
	assertEquals(values.size(), 2);

	assertEquals(values.get(0).get("name").asText(), ConcordanceType.DEFAULT.name());
	assertEquals(values.get(0).get("value").asText(), ConcordanceType.DEFAULT.name());

	assertEquals(values.get(1).get("name").asText(), ConcordanceType.EXACT.name());
	assertEquals(values.get(1).get("value").asText(), ConcordanceType.EXACT.name());

	JsonNode value = termName.get("value");
	assertEquals(value.size(), 2);

	assertEquals(value.get("value1").asText(), "null");
	assertEquals(value.get("value2").asText(), ConcordanceType.DEFAULT.name());

    }

    private void validateTermStatusesSearchBox(JsonNode searchBar) {

	JsonNode termStatuses = searchBar.get(SearchCriteria.TERM_STATUSES.name());
	assertNotNull(termStatuses);

	assertEquals(termStatuses.get("name").asText(), SearchCriteria.TERM_STATUSES.getControlName());
	assertEquals(termStatuses.get("type").asText(), SearchCriteria.TERM_STATUSES.getControlType().toString());
	assertEquals(termStatuses.get("required").asText(), "false");
	assertEquals(termStatuses.get("value").asText(), "null");

	JsonNode statuses = termStatuses.get("values");
	assertNotNull(statuses);
	assertEquals(statuses.size(), 7);

    }

    private void validateTlCreationDateSearchBox(JsonNode searchBar) {

	JsonNode dateCreated = searchBar.get(SearchCriteria.TL_DATE_CREATED_RANGE.name());
	assertNotNull(dateCreated);
	assertEquals(dateCreated.get("name").asText(), SearchCriteria.TL_DATE_CREATED_RANGE.getControlName());
	assertEquals(dateCreated.get("required").asText(), "false");
	assertEquals(dateCreated.get("items").size(), 2);

	JsonNode node1 = dateCreated.get("items").get(0);
	JsonNode node2 = dateCreated.get("items").get(1);

	assertEquals(node1.get("name").asText(), DateSearchEnum.FROM_DATE.getCommand());
	assertEquals(node1.get("type").asText(), DateSearchEnum.FROM_DATE.getControlType().getCommand());
	assertEquals(node2.get("name").asText(), DateSearchEnum.TO_DATE.getCommand());
	assertEquals(node2.get("type").asText(), DateSearchEnum.TO_DATE.getControlType().getCommand());

    }

    private void validateTlEntityTypeSearchBox(JsonNode searchBar) {
	JsonNode entityType = searchBar.get(SearchCriteria.TL_ENTITY_TYPE.name());
	assertNotNull(entityType);
	assertEquals(entityType.get("name").asText(), SearchCriteria.TL_ENTITY_TYPE.getControlName());
	assertEquals(entityType.get("required").asText(), "true");
	assertEquals(entityType.get("items").size(), 2);

	JsonNode node1 = entityType.get("items").get(0);
	JsonNode node2 = entityType.get("items").get(1);

	assertEquals(node1.get("name").asText(), EntitySearch.SEARCH_IN.getCommand());
	assertEquals(node1.get("type").asText(), EntitySearch.SEARCH_IN.getControlType().getCommand());
	assertEquals(node1.get("values").size(), 2);

	JsonNode value1 = node1.get("values").get(0);
	JsonNode value2 = node1.get("values").get(1);

	assertEquals(value1.get("name").asText(), MessageResolver.getMessage("TlEntityTypeAttribute"));
	assertEquals(value1.get("value").asText(), TypeSearchEnum.ATTRIBUTES.name());
	assertEquals(value2.get("name").asText(), MessageResolver.getMessage("TlEntityTypeTerm"));
	assertEquals(value2.get("value").asText(), TypeSearchEnum.TERM.name());

	assertEquals(node2.get("name").asText(), EntitySearch.INCLUDE.getCommand());
	assertEquals(node2.get("type").asText(), EntitySearch.INCLUDE.getControlType().getCommand());
	assertEquals(node2.get("values").size(), 2);

	JsonNode value3 = node2.get("values").get(0);
	JsonNode value4 = node2.get("values").get(1);

	assertEquals(value3.get("name").asText(), MessageResolver.getMessage("TlLanguageSearchSource"));
	assertEquals(value3.get("value").asText(), LanguageSearchEnum.SOURCE.name());
	assertEquals(value4.get("name").asText(), MessageResolver.getMessage("TlLanguageSearchTarget"));
	assertEquals(value4.get("value").asText(), LanguageSearchEnum.TARGET.name());
    }

    private void validateTlModificationDateSearchBox(JsonNode searchBar) {

	JsonNode dateModified = searchBar.get(SearchCriteria.TL_DATE_MODIFIED_RANGE.name());
	assertNotNull(dateModified);
	assertEquals(dateModified.get("name").asText(), "dateModifiedRange");
	assertEquals(dateModified.get("required").asText(), "false");
	assertEquals(dateModified.get("items").size(), 2);

	JsonNode node1 = dateModified.get("items").get(0);
	JsonNode node2 = dateModified.get("items").get(1);

	assertEquals(node1.get("name").asText(), DateSearchEnum.FROM_DATE.getCommand());
	assertEquals(node1.get("type").asText(), DateSearchEnum.FROM_DATE.getControlType().getCommand());
	assertEquals(node2.get("name").asText(), DateSearchEnum.TO_DATE.getCommand());
	assertEquals(node2.get("type").asText(), DateSearchEnum.TO_DATE.getControlType().getCommand());

    }

    private void validateTlTermNameSearchBox(JsonNode searchBar) {

	JsonNode termName = searchBar.get(SearchCriteria.TL_TERM_NAME.name());
	assertNotNull(termName);
	assertEquals(termName.get("name").asText(), SearchCriteria.TL_TERM_NAME.getControlName());
	assertEquals(termName.get("items").size(), 2);

	JsonNode node1 = termName.get("items").get(0);
	JsonNode node2 = termName.get("items").get(1);

	assertEquals(node1.get("name").asText(), TermNameSearchEnum.TERM.getCommand());
	assertEquals(node1.get("type").asText(), TermNameSearchEnum.TERM.getControlType().getCommand());
	assertEquals(node2.get("name").asText(), TermNameSearchEnum.OPTIONS.getCommand());
	assertEquals(node2.get("type").asText(), TermNameSearchEnum.OPTIONS.getControlType().getCommand());
	assertEquals(node2.get("values").get(0).get("name").asText(),
		MessageResolver.getMessage("ConcordanceTypeDefault"));
	assertEquals(node2.get("values").get(0).get("value").asText(), ConcordanceType.DEFAULT.name());
	assertEquals(node2.get("values").get(1).get("name").asText(),
		MessageResolver.getMessage("ConcordanceTypeExact"));
	assertEquals(node2.get("values").get(1).get("value").asText(), ConcordanceType.EXACT.name());

    }

    private void validateTlTermStatusesSearchBox(JsonNode searchBar) {

	JsonNode termStatuses = searchBar.get(SearchCriteria.TL_TERM_STATUSES.name());
	assertNotNull(termStatuses);
	assertEquals(termStatuses.get("name").asText(), SearchCriteria.TL_TERM_STATUSES.getControlName());
	assertEquals(termStatuses.get("items").size(), 2);

	JsonNode node1 = termStatuses.get("items").get(0);
	JsonNode node2 = termStatuses.get("items").get(1);

	assertEquals(node1.get("name").asText(), SearchCriteria.TL_TERM_STATUSES.getControlName());
	assertEquals(node1.get("type").asText(), SearchCriteria.TL_TERM_STATUSES.getControlType().getCommand());
	assertEquals(node1.get("values").size(), 7);

	assertEquals(node2.get("type").asText(), ControlType.PLACEHOLDER.getCommand());

    }

}
