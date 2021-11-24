package org.gs4tr.termmanager.tests.controllers.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.io.IOException;
import java.util.Set;

import org.apache.xerces.parsers.DOMParser;
import org.gs4tr.foundation.modules.security.model.Policy;
import org.gs4tr.termmanager.model.ProjectPolicyEnum;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class FindProjectByShortcodeControllerTest extends BaseRestControllerTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String URL = "/rest/projectByShortcode";

    @Test
    public void findUserProjectByShortCodeGetTest() throws Exception {
	String userId = loginGet("pm", "test");
	String projectShortcode = "TEST001";

	MockHttpServletRequestBuilder get = get(URL);
	get.param("projectShortcode", projectShortcode);
	get.param("userId", userId);

	ResultActions resultActions = _mockMvc.perform(get);

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	assertEquals("application/xml;charset=UTF-8", response.getContentType());

	String result = response.getContentAsString();
	Assert.assertNotNull(result);
    }

    @Test
    public void testReadOnlyProjectWithOnHoldPolicy() throws Exception {

	TmUserProfile userProfile = getUserProfileService().findById(9L);
	assertTrue(userProfile.getGeneric());

	Set<Policy> policies = userProfile.getSystemRoles().iterator().next().getPolicies();
	assertEquals(1, policies.size());

	Policy policy = policies.iterator().next();
	assertEquals(ProjectPolicyEnum.POLICY_TM_TERM_ADD_ON_HOLD_TERM.name(), policy.getPolicyId());

	String userId = loginGet(userProfile.getUserName(), userProfile.getGenericPassword());

	String projectShortcode = "TEST001";

	MockHttpServletRequestBuilder get = get(URL);
	get.param("projectShortcode", projectShortcode);
	get.param("userId", userId);

	ResultActions resultActions = _mockMvc.perform(get);

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	assertEquals("application/xml;charset=UTF-8", response.getContentType());

	String result = response.getContentAsString();
	Assert.assertNotNull(result);

	Boolean readOnly = parseReadOnlyField(result);
	assertFalse(readOnly);
    }

    private Boolean parseReadOnlyField(String xmlString) throws IOException, SAXException {
	DOMParser parser = new DOMParser();

	parser.parse(new InputSource(new java.io.StringReader(xmlString)));
	Document doc = parser.getDocument();
	return Boolean.valueOf(doc.getElementsByTagName("readOnly").item(0).getTextContent());
    }
}