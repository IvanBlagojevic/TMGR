package org.gs4tr.termmanager.webmvc.controllers;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.gs4tr.foundation.modules.entities.model.UserProfile;
import org.gs4tr.foundation.modules.entities.model.UserProfileContext;
import org.gs4tr.foundation.modules.entities.model.UserTypeEnum;
import org.gs4tr.foundation3.core.utils.IdEncrypter;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.SubmissionLanguage;
import org.gs4tr.termmanager.service.SubmissionService;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.webmvc.model.TestCase;
import org.gs4tr.termmanager.webmvc.model.TestSuite;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.JsonNode;

@TestSuite("controllers")
public class BrowseSubmissionlanguagesControllerTest extends AbstractControllerTest {

    private static final String URL = "browseSubmissionLanguages.ter";

    @Autowired
    private SubmissionService _submissionService;

    @Test
    @TestCase("browseSubmission")
    public void browseSubmissionWithPowerUserTest() throws Exception {

	UserProfile userProfile = UserProfileContext.getCurrentUserProfile();
	UserProfileContext.clearContext();
	userProfile.getUserInfo().setUserType(UserTypeEnum.POWER_USER);
	UserProfileContext.setCurrentUserProfile(userProfile);

	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.get("/" + URL);
	post.param("submissionTicket", IdEncrypter.encryptGenericId(1));

	ResultActions resultActions = _mockMvc.perform(post);

	verify(getSubmissionService(), times(1)).findSubmissionLanguages(any(Long.class));
	verify(getSubmissionService(), times(1)).findSubmissionByIdFetchChilds(any(Long.class));

	resultActions.andExpect(status().isOk());

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	String result = response.getContentAsString();
	Assert.assertNotNull(result);

	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);
	Assert.assertNotNull(resultNode);

	JsonNode successNode = resultNode.get("success");
	Assert.assertNotNull(successNode);
	Assert.assertTrue(successNode.asBoolean());
    }

    public SubmissionService getSubmissionService() {
	return _submissionService;
    }

    @Before
    public void setUp() throws Exception {
	reset(getSubmissionService());
	mockObjects();
    }

    private void mockObjects() {
	@SuppressWarnings("unchecked")
	List<SubmissionLanguage> submissionLanguages = (List<SubmissionLanguage>) getModelObject("submissionLanguages",
		SubmissionLanguage.class);
	Submission submission = getModelObject("submission", Submission.class);

	when(getSubmissionService().findSubmissionByIdFetchChilds(any(Long.class))).thenReturn(submission);
	when(getSubmissionService().findSubmissionLanguages(any(Long.class))).thenReturn(submissionLanguages);
    }
}
