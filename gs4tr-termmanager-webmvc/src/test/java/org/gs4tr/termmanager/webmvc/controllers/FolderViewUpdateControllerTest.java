package org.gs4tr.termmanager.webmvc.controllers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.gs4tr.foundation.modules.entities.model.Metadata;
import org.gs4tr.foundation.modules.entities.model.UserProfile;
import org.gs4tr.foundation.modules.entities.model.UserProfileContext;
import org.gs4tr.foundation.modules.usermanager.model.AbstractUserProfile;
import org.gs4tr.foundation.modules.usermanager.service.security.DefaultUserDetails;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.service.SubmissionService;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.webmvc.model.TestCase;
import org.gs4tr.termmanager.webmvc.model.TestSuite;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.JsonNode;

@TestSuite("controllers")
public class FolderViewUpdateControllerTest extends AbstractControllerTest {

    private static final String URL = "folderViewUpdate.ter";

    @Autowired
    private SubmissionService _submissionService;

    private MockHttpSession mockSession;

    @Test
    @TestCase("folderView")
    public void folderViewSessionTest() throws Exception {
	UserProfile userProfile = UserProfileContext.getCurrentUserProfile();

	SecurityContext securityContext = mock(SecurityContext.class);
	Authentication authentication = mock(Authentication.class);

	getMockSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
		securityContext);

	when(securityContext.getAuthentication()).thenReturn(authentication);
	when(authentication.isAuthenticated()).thenReturn(true);

	when(authentication.getPrincipal())
		.thenReturn(new DefaultUserDetails<AbstractUserProfile>((AbstractUserProfile) userProfile));

	String userMetadata = getJsonData("userProfileMetadata.json");

	Metadata metadata2 = new Metadata();
	metadata2.setKey("submissiontermlist");
	metadata2.setValue(userMetadata);

	List<Metadata> metadataList = new ArrayList<Metadata>();
	metadataList.add(metadata2);

	userProfile.setMetadata(metadataList);
	UserProfileContext.setCurrentUserProfile(userProfile);

	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/" + URL);
	post.session(getMockSession());
	post.param("folder", "SUBMISSIONTERMLIST");
	post.param("contextUser", "John");

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
    }

    @Test
    @TestCase("folderView")
    public void folderViewUpdateWithMetadataTest() throws Exception {
	String userMetadata = getJsonData("userProfileMetadata.json");

	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();

	Metadata metadata2 = new Metadata();
	metadata2.setKey("submissiontermlist");
	metadata2.setValue(userMetadata);

	List<Metadata> metadataList = new ArrayList<Metadata>();
	metadataList.add(metadata2);

	userProfile.setMetadata(metadataList);
	UserProfileContext.setCurrentUserProfile(userProfile);

	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/" + URL);
	post.param("folder", "SUBMISSIONTERMLIST");
	post.param("contextUser", "John");

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
    }

    @Test
    public void folderViewUpdateWithoutMetadataTest() throws Exception {

	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/" + URL);
	post.param("folder", "SUBMISSIONTERMLIST");

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
    }

    public SubmissionService getSubmissionService() {
	return _submissionService;
    }

    @Before
    public void setUp() throws Exception {
	reset(getSubmissionService());
	setMockSession(
		new MockHttpSession(getWebApplicationContext().getServletContext(), UUID.randomUUID().toString()));

    }

    private MockHttpSession getMockSession() {
	return mockSession;
    }

    private void setMockSession(MockHttpSession mockSession) {
	this.mockSession = mockSession;
    }
}
