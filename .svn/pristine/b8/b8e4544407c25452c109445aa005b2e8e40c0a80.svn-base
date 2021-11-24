package org.gs4tr.termmanager.webmvc.jetty.junit.runner;

import static junit.framework.Assert.assertEquals;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.createGetRequest;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.sendRecieve;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jetty.http.HttpTester.Request;
import org.eclipse.jetty.http.HttpTester.Response;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestCase;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.foundation.modules.webmvc.test.annotations.ClientBean;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.SubmissionLanguage;
import org.gs4tr.termmanager.service.SubmissionService;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.webmvc.model.commands.SearchCommand;
import org.gs4tr.termmanager.webmvc.test.annotations.TestUser;
import org.gs4tr.termmanager.webmvc.test.configuration.RoleNameEnum;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

@TestSuite("browseSubmissionLanguages")
public class BrowseSubmissionlanguagesControllerTest extends AbstractMvcTest {

    private static final String URL = "browseSubmissionLanguages.ter";

    @ClientBean
    private SubmissionService _submissionService;

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("submissionWithTwoLanguages")
    @TestUser(roleName = RoleNameEnum.SYSTEM_SUPER_USER)
    public void browseSubmissionLanguages_1() throws IOException {
	when(getUserProfileService().loadUserProfileByUsername(DEFAULT_USERNAME)).thenReturn(getCurrentUserProfile());

	Submission submission = getModelObject("submission", Submission.class);

	List<SubmissionLanguage> languages = getModelObject("submissionLanguages", List.class);

	when(getSubmissionService().findSubmissionByIdFetchChilds(anyLong())).thenReturn(submission);
	when(getSubmissionService().findSubmissionLanguages(anyLong())).thenReturn(languages);

	SearchCommand searchCommand = getModelObject("searchCommand", SearchCommand.class);

	Map<String, String> parameters = new HashMap<String, String>();

	parameters.put(JSON_DATA_KEY, OBJECT_MAPPER.writeValueAsString(searchCommand));

	Request request = createGetRequest(URL, parameters, getSessionParameters());

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	// times(1) is the default and can be omitted
	verify(getSubmissionService()).findSubmissionByIdFetchChilds(eq(1L));
	verify(getSubmissionService()).findSubmissionLanguages(eq(1L));

	String content = response.getContent();

	JsonNode resultNode = JsonUtils.readValue(content, JsonNode.class);

	JsonNode successNode = resultNode.get("success");
	Assert.assertNotNull(successNode);
	Assert.assertTrue(successNode.asBoolean());

	/*
	 * TERII-5955 Workflow Submissions | "Version change detected" info message
	 * appears when we open submission
	 */
	JsonNode versionNode = resultNode.get("version");
	Assert.assertNotNull(versionNode);

	JsonNode languagesStatus = resultNode.get("languagesStatus");
	Assert.assertNotNull(languagesStatus);

	JsonNode statusJsonNodeDe = languagesStatus.get(Locale.GERMANY.getCode());
	Assert.assertNotNull(statusJsonNodeDe);
	Assert.assertEquals(ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName(), statusJsonNodeDe.asText());

	JsonNode statusJsonNodeFr = languagesStatus.get(Locale.FRANCE.getCode());
	Assert.assertNotNull(statusJsonNodeFr);
	Assert.assertEquals(ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName(), statusJsonNodeFr.asText());
    }

    @Before
    public void setUp() {
	reset(getSubmissionService());
    }

    private SubmissionService getSubmissionService() {
	return _submissionService;
    }
}
