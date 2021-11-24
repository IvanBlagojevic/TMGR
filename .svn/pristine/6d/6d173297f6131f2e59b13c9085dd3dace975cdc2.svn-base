package org.gs4tr.termmanager.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang.StringUtils;
import org.gs4tr.termmanager.model.ProjectDescription;
import org.gs4tr.termmanager.model.TmProject;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ProjectDescriptionServiceTest extends AbstractSpringServiceTests {

    @Autowired
    private ProjectDescriptionService _projectDescriptionService;

    @Test
    public void addNewProjectDescription() {
	Long tmProjectId = 2L;
	TmProject project = getProjectService().load(tmProjectId);
	Assert.assertTrue(!project.getAvailableDescription());

	ProjectDescription addedDescription = new ProjectDescription("Some other project description", project);

	getProjectDescriptionService().saveOrUpdateProjectDescription(addedDescription);

	project = getProjectService().load(tmProjectId);
	assertTrue(project.getAvailableDescription());

	ProjectDescription retrievedDescription = getProjectDescriptionService().findByProjectId(tmProjectId);
	assertNotNull(retrievedDescription);
	assertNotNull(retrievedDescription.getProjectDescriptionId());
	assertEquals("Some other project description", retrievedDescription.getText());
    }

    @Test
    public void deleteProjectDescription() {
	Long tmProjectId = 1L;
	ProjectDescription description = getProjectDescriptionService().findByProjectId(tmProjectId);
	assertEquals("Some project description", description.getText());

	TmProject project = getProjectService().load(tmProjectId);

	assertTrue(project.getAvailableDescription());

	getProjectDescriptionService().deleteByProjectId(tmProjectId);
	project = getProjectService().load(tmProjectId);

	assertNull(getProjectDescriptionService().findByProjectId(tmProjectId));
	assertFalse(project.getAvailableDescription());
    }

    @Test
    public void getProjectDescription() {
	Long projectId = 1L;
	ProjectDescription description = getProjectDescriptionService().findByProjectId(projectId);
	assertNotNull(description);
	assertEquals("Some project description", description.getText());
    }

    @Test
    public void updateProjectDescription() {
	Long tmProjectId = 1L;

	ProjectDescriptionService service = getProjectDescriptionService();

	ProjectDescription description = service.findByProjectId(tmProjectId);

	String text = description.getText();
	assertTrue(StringUtils.isNotEmpty(text));

	String newText = "Some other description";
	description.setText(newText);
	service.saveOrUpdateProjectDescription(description);

	ProjectDescription updatedDescription = service.findByProjectId(tmProjectId);
	assertEquals(newText, updatedDescription.getText());
    }

    private ProjectDescriptionService getProjectDescriptionService() {
	return _projectDescriptionService;
    }
}
