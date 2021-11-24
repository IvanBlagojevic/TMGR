package org.gs4tr.termmanager.tests.controllers.mvc;

import java.util.UUID;

import org.gs4tr.foundation3.core.utils.IdEncrypter;
import org.gs4tr.termmanager.tests.AbstractSpringServiceTests;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@TestSuite("translation_round_trip")
public class TermTranslationTest extends AbstractSpringServiceTests {

    @Test
    @TestCase("test")
    public void testRoundTrip() throws Exception {
	String taskName = "send to translation";

	Long projectID = 1L;
	String projectTicket = IdEncrypter.encryptGenericId(projectID);
	String submissionName = "mySub";
	String submissionMarkerId = UUID.randomUUID().toString();
	String termEntryTicket = IdEncrypter.encryptGenericId(11L);

	String jsonData = getJsonData("submit.json", new String[] { "$submissionMarkerId", submissionMarkerId },
		new String[] { "$projectTicket", projectTicket }, new String[] { "$submissionName", submissionName },
		new String[] { "$termEntryTicket", termEntryTicket });

	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/task.ter");
	post.param("jsonTaskData", jsonData);
	post.param("taskName", taskName);

	ResultActions resultActions = _mockMvc.perform(post);

	String result = resultActions.andReturn().getResponse().getContentAsString();

	Assert.assertNotNull(result);
    }
}
