package org.gs4tr.termmanager.tests;

import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmOrganization;
import org.gs4tr.termmanager.service.OrganizationService;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class EnableOrganizationTaskHandlerTest extends AbstractSpringServiceTests {

    @Autowired
    private OrganizationService _organizationService;

    public OrganizationService getOrganizationService() {
	return _organizationService;
    }

    @Test
    public void testEnableOrganizationPost() throws Exception {

	TmOrganization organization = getOrganizationService().findById(1L);
	Boolean enabled = organization.getOrganizationInfo().isEnabled();
	Assert.assertTrue(enabled);

	ManualTaskHandler taskHandler = getHandler("enable organization");

	TaskResponse response = taskHandler.processTasks(new Long[] { 3L }, null, null, null);
	Assert.assertNotNull(response);
	Assert.assertEquals(Boolean.TRUE, getOrganizationService().findById(3L).getOrganizationInfo().isEnabled());
    }
}
