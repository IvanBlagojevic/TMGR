package org.gs4tr.termmanager.dao.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.gs4tr.termmanager.dao.ProjectLanguageDAO;
import org.gs4tr.termmanager.model.ProjectLanguage;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ProjectLanguageDAOTest extends AbstractSpringDAOIntegrationTest {

    private static final Long PROJECT_ID_01 = 1L;

    private static final Long PROJECT_ID_02 = 2L;

    @Autowired
    private ProjectLanguageDAO _projectLanguageDAO;

    /* Verify this method first because I use it in the next test */
    @Test
    public void findByIdTest() {
	ProjectLanguage projectLanguage = getProjectLanguageDAO().findById(1L);

	assertNotNull(projectLanguage);

	assertTrue(projectLanguage.getIdentifier().equals(1L));

	assertFalse(projectLanguage.isDefault());

	assertEquals("en-US", projectLanguage.getLanguage());
    }

    @Test
    public void findByProjectIdTest() {
	List<ProjectLanguage> projectLanguages = getProjectLanguageDAO().findByProjectId(PROJECT_ID_01);

	ProjectLanguage projectLanguage1 = getProjectLanguageDAO().findById(1L);
	ProjectLanguage projectLanguage2 = getProjectLanguageDAO().findById(2L);
	ProjectLanguage projectLanguage3 = getProjectLanguageDAO().findById(3L);
	ProjectLanguage projectLanguage4 = getProjectLanguageDAO().findById(4L);

	assertNotNull(projectLanguages);

	assertTrue(CollectionUtils.isNotEmpty(projectLanguages));
	assertTrue(projectLanguages.contains(projectLanguage1));
	assertTrue(projectLanguages.contains(projectLanguage2));
	assertTrue(projectLanguages.contains(projectLanguage3));
	assertTrue(projectLanguages.contains(projectLanguage4));

	assertEquals(4, projectLanguages.size());
    }

    @Test
    public void findByProjectIdsTest() {
	final List<Long> projectIds = new ArrayList<Long>();

	projectIds.add(PROJECT_ID_01);
	projectIds.add(PROJECT_ID_02);

	List<ProjectLanguage> projectLanguages = getProjectLanguageDAO().findByProjectIds(projectIds);

	assertNotNull(projectLanguages);

	assertTrue(CollectionUtils.isNotEmpty(projectLanguages));

	assertEquals(5, projectLanguages.size());
    }

    @Test
    public void getLanguageIdsByProjectId() {
	Set<String> languageIds = getProjectLanguageDAO().getLanguageIdsByProjectId(PROJECT_ID_01);
	assertNotNull(languageIds);
	assertTrue(CollectionUtils.isNotEmpty(languageIds));

    }

    @Test
    public void testGetProjectLanguagesMap() {
	Map<Long, Set<String>> map = getProjectLanguageDAO().getProjectLanguagesMap();
	assertNotNull(map);
	assertTrue(MapUtils.isNotEmpty(map));
    }

    private ProjectLanguageDAO getProjectLanguageDAO() {
	return _projectLanguageDAO;
    }
}
