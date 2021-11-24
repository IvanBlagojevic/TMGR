package org.gs4tr.termmanager.webmvc.jetty.junit.runner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.createGetRequest;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.sendRecieve;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jetty.http.HttpTester.Request;
import org.eclipse.jetty.http.HttpTester.Response;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestCase;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.LanguageAlignmentEnum;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.webmvc.json.validator.JSONValidator;
import org.gs4tr.termmanager.webmvc.model.search.ControlType;
import org.gs4tr.termmanager.webmvc.model.search.SearchCriteria;
import org.gs4tr.termmanager.webmvc.test.annotations.TestUser;
import org.gs4tr.termmanager.webmvc.test.configuration.RoleNameEnum;
import org.junit.Test;

@TestSuite("initializeSearchBarController")
public class InitializeSearchBarControllerTest extends AbstractMvcTest {

    private static final String URL = "initializeSearchBar.ter";

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("initializeSearchBar")
    @TestUser(roleName = RoleNameEnum.SYSTEM_SUPER_USER)
    public void initializeSearchBarTest() {
	when(getUserProfileService().loadUserProfileByUsername(DEFAULT_USERNAME)).thenReturn(getCurrentUserProfile());

	List<TmProject> tmProjects = getModelObject("tmProjects", List.class);

	List<String> usernames = getModelObject("usernames", List.class);

	when(getProjectService().findProjectByIds(anyListOf(Long.class))).thenReturn(tmProjects);
	when(getUserProfileService().findAllNonGenerciUsernames()).thenReturn(usernames);

	Request request = createGetRequest(URL, null, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	verify(getUserProfileService()).findAllNonGenerciUsernames();
	verify(getProjectService()).findProjectByIds(eq(Arrays.asList(1L)));

	JSONValidator responseContent = new JSONValidator(response.getContent());

	responseContent.assertProperty(SUCCESS_KEY, String.valueOf(true));

	JSONValidator searchBar = responseContent.getObject("searchBar");

	JSONValidator lastname = searchBar.getObject(SearchCriteria.USER_LAST_NAME.name());
	lastname.assertProperty("type", ControlType.INPUT.name());
	lastname.assertProperty("name", SearchCriteria.USER_LAST_NAME.getControlName());
	lastname.assertObjectFinish();

	JSONValidator organizationName = searchBar.getObject(SearchCriteria.ORGANIZATION_NAME.name());
	organizationName.assertProperty("type", ControlType.INPUT.name());
	organizationName.assertProperty("name", SearchCriteria.ORGANIZATION_NAME.getControlName());
	organizationName.assertObjectFinish();

	JSONValidator languageSetName = searchBar.getObject(SearchCriteria.LANGUAGE_SET_NAME.name());
	languageSetName.assertProperty("type", ControlType.INPUT.name());
	languageSetName.assertProperty("name", SearchCriteria.LANGUAGE_SET_NAME.getControlName());
	languageSetName.assertObjectFinish();

	JSONValidator dateCompletedRange = searchBar.getObject(SearchCriteria.DATE_COMPLETED_RANGE.name());
	dateCompletedRange.assertProperty("type", ControlType.DATE_RANGE.name());
	dateCompletedRange.assertProperty("name", SearchCriteria.DATE_COMPLETED_RANGE.getControlName());
	dateCompletedRange.assertObjectFinish();

	JSONValidator dateCreatedRange = searchBar.getObject(SearchCriteria.DATE_CREATED_RANGE.name());
	dateCreatedRange.assertProperty("type", ControlType.DATE_RANGE.name());
	dateCreatedRange.assertProperty("name", SearchCriteria.DATE_CREATED_RANGE.getControlName());
	dateCreatedRange.assertObjectFinish();

	JSONValidator projectName = searchBar.getObject(SearchCriteria.PROJECT_NAME.name());
	projectName.assertProperty("type", ControlType.INPUT.name());
	projectName.assertProperty("name", SearchCriteria.PROJECT_NAME.getControlName());
	projectName.assertObjectFinish();

	JSONValidator creationUsers = searchBar.getObject(SearchCriteria.CREATION_USERS.name());
	creationUsers.assertProperty("required", String.valueOf(false));
	creationUsers.assertProperty("type", ControlType.COMBO_MULTI.name());
	creationUsers.assertProperty("name", SearchCriteria.CREATION_USERS.getControlName());

	JSONValidator creationUserValues = creationUsers.getObject("values");
	assertNotNull(creationUserValues.getObjectFromArray("name", "admin"));
	assertNotNull(creationUserValues.getObjectFromArray("name", "system"));
	assertNotNull(creationUserValues.getObjectFromArray("name", "super"));
	assertNotNull(creationUserValues.getObjectFromArray("name", "translator"));

	JSONValidator modificationUsers = searchBar.getObject(SearchCriteria.MODIFICATION_USERS.name());
	modificationUsers.assertProperty("required", String.valueOf(false));
	modificationUsers.assertProperty("type", ControlType.COMBO_MULTI.name());
	modificationUsers.assertProperty("name", SearchCriteria.MODIFICATION_USERS.getControlName());

	JSONValidator modificationUserValues = modificationUsers.getObject("values");
	assertNotNull(modificationUserValues.getObjectFromArray("name", "admin"));
	assertNotNull(modificationUserValues.getObjectFromArray("name", "system"));
	assertNotNull(modificationUserValues.getObjectFromArray("name", "super"));
	assertNotNull(modificationUserValues.getObjectFromArray("name", "translator"));

	JSONValidator languageDirection = searchBar.getObject(SearchCriteria.LANGUAGE_DIRECTION.name())
		.getObject("values");
	JSONValidator germany = languageDirection.getObjectFromArray("value", Locale.GERMANY.getCode());
	JSONValidator french = languageDirection.getObjectFromArray("value", Locale.FRANCE.getCode());
	JSONValidator english = languageDirection.getObjectFromArray("value", Locale.US.getCode());

	assertNotNull(germany);
	assertNotNull(french);
	assertNotNull(english);

	english.assertProperty("direction", LanguageAlignmentEnum.LTR.getValue());
	germany.assertProperty("direction", LanguageAlignmentEnum.LTR.getValue());
	french.assertProperty("direction", LanguageAlignmentEnum.LTR.getValue());

	JSONValidator termStatuses = searchBar.getObject(SearchCriteria.TERM_STATUSES.name());
	JSONValidator termStatusValues = termStatuses.getObject("values");
	assertNotNull(termStatusValues.getObjectFromArray("name", ItemStatusTypeHolder.PROCESSED.getName()));
	assertNotNull(termStatusValues.getObjectFromArray("name", ItemStatusTypeHolder.WAITING.getName()));
	assertNotNull(termStatusValues.getObjectFromArray("name", ItemStatusTypeHolder.MISSING_TRANSLATION.getName()));
	assertNotNull(termStatusValues.getObjectFromArray("name", ItemStatusTypeHolder.BLACKLISTED.getName()));
	assertNotNull(termStatusValues.getObjectFromArray("name", ItemStatusTypeHolder.IN_FINAL_REVIEW.getName()));
	assertNotNull(
		termStatusValues.getObjectFromArray("name", ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName()));

	JSONValidator submissionStatuses = searchBar.getObject(SearchCriteria.SUBMISSION_STATUSES.name());
	JSONValidator submissionStatusesValues = submissionStatuses.getObject("values");
	assertNotNull(submissionStatusesValues.getObjectFromArray("name", ItemStatusTypeHolder.COMPLETED.getName()));
	assertNotNull(submissionStatusesValues.getObjectFromArray("name", ItemStatusTypeHolder.CANCELLED.getName()));
	assertNotNull(
		submissionStatusesValues.getObjectFromArray("name", ItemStatusTypeHolder.IN_FINAL_REVIEW.getName()));
	assertNotNull(submissionStatusesValues.getObjectFromArray("name",
		ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName()));

	assertNotNull(searchBar.getObject(SearchCriteria.ENTITY_TYPE.name()));
	assertNotNull(searchBar.getObject(SearchCriteria.HIDE_BLANKS.name()));
	assertNotNull(searchBar.getObject(SearchCriteria.HIDE_BLANKS.name()));
	assertNotNull(searchBar.getObject(SearchCriteria.USER_NAME.name()));
	assertNotNull(searchBar.getObject(SearchCriteria.ROLE_NAME.name()));
	assertNotNull(searchBar.getObject(SearchCriteria.TERM_NAME.name()));
	assertNotNull(searchBar.getObject(SearchCriteria.PROJECT_LIST.name()));

	assertNotNull(searchBar.getObject(SearchCriteria.SUBMISSION_USERS.name()));
	assertNotNull(searchBar.getObject(SearchCriteria.DATE_MODIFIED_RANGE.name()));
	assertNotNull(searchBar.getObject(SearchCriteria.SUBMISSION_NAME.name()));
	assertNotNull(searchBar.getObject(SearchCriteria.USER_FIRST_NAME.name()));

	assertNotNull(searchBar.getObject(SearchCriteria.SUBMISSION_TERM_LANGUAGE.name()));
	assertNotNull(searchBar.getObject(SearchCriteria.TERM_ENTRY_DATE_CREATED_RANGE.name()));
	assertNotNull(searchBar.getObject(SearchCriteria.LANGUAGE_DIRECTION_SUBMISSION.name()));
	assertNotNull(searchBar.getObject(SearchCriteria.SUBMISSION_PROJECT_LIST.name()));
    }
}
