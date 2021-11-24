package org.gs4tr.termmanager.dao.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.gs4tr.termmanager.dao.ProjectDescriptionDAO;
import org.gs4tr.termmanager.model.ProjectDescription;
import org.gs4tr.termmanager.model.TmProject;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ProjectDescriptionDAOTest extends AbstractSpringDAOIntegrationTest {

    public static final long PROJECT_ID = 1L;

    @Autowired
    private ProjectDescriptionDAO _projectDescriptionDAO;

    @Test
    public void deleteProjectNote() {
	ProjectDescription projectDescription = getProjectDescriptionDAO().findByProjectId(PROJECT_ID);
	assertNotNull(projectDescription);

	getProjectDescriptionDAO().delete(projectDescription);

	projectDescription = getProjectDescriptionDAO().findByProjectId(PROJECT_ID);
	Assert.assertNull(projectDescription);
    }

    @Test
    public void findAllProjectNotes() {
	List<ProjectDescription> notes = getProjectDescriptionDAO().findAll();
	assertTrue(!notes.isEmpty());
	assertEquals(1, notes.size());
    }

    @Test
    public void saveProjectNote() {
	TmProject project = getProjectDAO().findById(2L);

	ProjectDescription projectDescription = new ProjectDescription("Some project note", project);

	ProjectDescription savedProjectDescription = getProjectDescriptionDAO().save(projectDescription);
	Assert.assertNotNull(savedProjectDescription);
	Assert.assertNotNull(savedProjectDescription.getProjectDescriptionId());
    }

    @Test
    public void updateProjectNote() {

	ProjectDescription projectDescription = getProjectDescriptionDAO().findByProjectId(PROJECT_ID);

	Assert.assertNotNull(projectDescription.getText());

	String updatedText = "Updated note value.";

	projectDescription.setText(updatedText);

	getProjectDescriptionDAO().update(projectDescription);

	ProjectDescription loadProjectDescription = getProjectDescriptionDAO()
		.load(projectDescription.getProjectDescriptionId());

	assertEquals(loadProjectDescription.getText(), updatedText);
    }

    private ProjectDescriptionDAO getProjectDescriptionDAO() {
	return _projectDescriptionDAO;
    }
}
