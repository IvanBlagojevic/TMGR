package org.gs4tr.termmanager.dao.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.entities.model.ProjectInfo;
import org.gs4tr.termmanager.dao.ProjectUserLanguageDAO;
import org.gs4tr.termmanager.model.ProjectUserLanguage;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ProjectUserLanguageDAOTest extends AbstractSpringDAOIntegrationTest {

    private static final Long PROJECT_ID_01 = 2L;

    private static final Long PROJECT_ID_02 = 1L;

    private static final String PROJECT_NAME_01 = "translationProject";

    private static final String PROJECT_NAME_02 = "testProject";

    private static final Long USER_PROFILE_ID_01 = 1L;

    private static final Long USER_PROFILE_ID_02 = 2L;

    @Autowired
    private ProjectUserLanguageDAO _projectUserLanguageDAO;

    @Test
    public void ProjectUserLanguagesByUser() {
	List<ProjectUserLanguage> projectUserLanguagesByUser = getProjectUserLanguageDAO()
		.getProjectUserLanguagesByUser(USER_PROFILE_ID_01);

	ProjectUserLanguage projectUserLanguage_01 = getProjectUserLanguageDAO().findById(1L);
	ProjectUserLanguage projectUserLanguage_02 = getProjectUserLanguageDAO().findById(2L);
	ProjectUserLanguage projectUserLanguage_03 = getProjectUserLanguageDAO().findById(4L);
	ProjectUserLanguage projectUserLanguage_04 = getProjectUserLanguageDAO().findById(6L);
	ProjectUserLanguage projectUserLanguage_05 = getProjectUserLanguageDAO().findById(7L);
	ProjectUserLanguage projectUserLanguage_06 = getProjectUserLanguageDAO().findById(8L);

	assertNotNull(projectUserLanguagesByUser);

	assertTrue(CollectionUtils.isNotEmpty(projectUserLanguagesByUser));
	assertTrue(projectUserLanguagesByUser.contains(projectUserLanguage_01));
	assertTrue(projectUserLanguagesByUser.contains(projectUserLanguage_02));
	assertTrue(projectUserLanguagesByUser.contains(projectUserLanguage_03));
	assertTrue(projectUserLanguagesByUser.contains(projectUserLanguage_04));
	assertTrue(projectUserLanguagesByUser.contains(projectUserLanguage_05));
	assertTrue(projectUserLanguagesByUser.contains(projectUserLanguage_06));
	assertTrue(projectUserLanguagesByUser.size() == 6);
    }

    @Test
    public void allProjectUsersTest() {
	List<TmUserProfile> tmUsers = getProjectUserLanguageDAO().getAllProjectUsers(PROJECT_ID_01);

	TmUserProfile tmUser1 = getUserProfileDAO().findById(USER_PROFILE_ID_01);
	TmUserProfile tmUser2 = getUserProfileDAO().findById(USER_PROFILE_ID_02);

	assertNotNull(tmUsers);

	assertTrue(CollectionUtils.isNotEmpty(tmUsers));
	assertTrue(tmUsers.size() == 6);

	assertTrue(tmUsers.contains(tmUser1));
	assertTrue(tmUsers.contains(tmUser2));
    }

    @Test
    public void allUserProjectScopedTest() {
	List<TmProject> tmProjects = getProjectUserLanguageDAO().getAllUserProjectsScoped(USER_PROFILE_ID_01);

	TmProject tmProject1 = getProjectDAO().findById(PROJECT_ID_01);
	TmProject tmProject2 = getProjectDAO().findById(PROJECT_ID_02);

	ProjectInfo projectInfo1 = tmProject1.getProjectInfo();
	ProjectInfo projectInfo2 = tmProject2.getProjectInfo();

	assertNotNull(tmProjects);

	assertTrue(CollectionUtils.isNotEmpty(tmProjects));
	assertTrue(tmProjects.contains(tmProject1));
	assertTrue(tmProjects.contains(tmProject2));
	assertTrue(projectInfo1.getName().equals(PROJECT_NAME_01));
	assertTrue(projectInfo2.getName().equals(PROJECT_NAME_02));
	assertTrue(tmProjects.size() == 3);
    }

    @Test
    public void allUserProjectsTest() {
	Class<?> classesToFetch = Long.class;
	List<TmProject> tmProjects = getProjectUserLanguageDAO().getAllUserProjects(USER_PROFILE_ID_01, classesToFetch);

	TmProject tmProject1 = getProjectDAO().findById(PROJECT_ID_01);
	TmProject tmProject2 = getProjectDAO().findById(PROJECT_ID_02);

	assertNotNull(tmProjects);

	assertTrue(CollectionUtils.isNotEmpty(tmProjects));
	assertTrue(tmProjects.contains(tmProject1));
	assertTrue(tmProjects.contains(tmProject2));
	assertTrue(tmProjects.size() == 3);
    }

    /* NOTE: projectUserLanguage.user.generic is true */
    @Test
    public void findByProjectIdsTest() {
	final List<Long> projectIds = new ArrayList<Long>();

	projectIds.add(PROJECT_ID_01);
	projectIds.add(PROJECT_ID_02);

	List<ProjectUserLanguage> userLanguagesByProjectIds = getProjectUserLanguageDAO().findByProjectIds(projectIds);

	ProjectUserLanguage projectUserLanguage = getProjectUserLanguageDAO().findById(new Long(5l));

	assertNotNull(userLanguagesByProjectIds);

	assertTrue(CollectionUtils.isNotEmpty(userLanguagesByProjectIds));
	assertTrue(userLanguagesByProjectIds.contains(projectUserLanguage));

	assertEquals(4, userLanguagesByProjectIds.size());
    }

    @Test
    public void getAllProjectUsersByProjectAndLanguageIdsTest_Case1() {

	/* Two users are assigned for ("de-DE") and ("en-UK") project language */

	List<String> languageIds = new ArrayList<>();
	languageIds.add("de-DE");
	languageIds.add("en-UK");

	List<TmUserProfile> usersByProjectAndLanguageIds = getProjectUserLanguageDAO()
		.getProjectUsersByLanguageIds(PROJECT_ID_02, languageIds);

	assertNotNull(usersByProjectAndLanguageIds);
	assertTrue(CollectionUtils.isNotEmpty(usersByProjectAndLanguageIds));
	assertEquals(usersByProjectAndLanguageIds.size(), 2);

    }

    @Test
    public void getAllProjectUsersByProjectAndLanguageIdsTest_Case2() {

	/* No users are assigned for ("fr-FR") project language */

	List<String> languageIds = new ArrayList<>();
	languageIds.add("fr-FR");

	List<TmUserProfile> usersByProjectAndLanguageIds = getProjectUserLanguageDAO()
		.getProjectUsersByLanguageIds(PROJECT_ID_02, languageIds);

	assertTrue(CollectionUtils.isEmpty(usersByProjectAndLanguageIds));

    }

    @Test
    public void getUserLanguagesMapTest() {
	List<TmUserProfile> tmUsers = getProjectUserLanguageDAO().getAllProjectUsers(PROJECT_ID_02);
	assertNotNull(tmUsers);

	Map<Long, List<String>> userLanguagesMap = getProjectUserLanguageDAO().getUserLanguagesMap(PROJECT_ID_02);
	assertNotNull(userLanguagesMap);

	/* same number of users */
	assertEquals(userLanguagesMap.size(), tmUsers.size());

	List<Long> userProfileIds = new ArrayList<>();
	tmUsers.forEach(tmUser -> userProfileIds.add(tmUser.getUserProfileId()));

	Set<Long> userIdsKeySet = userLanguagesMap.keySet();

	/* same users ids */
	assertTrue(userProfileIds.containsAll(userIdsKeySet));

	for (Map.Entry<Long, List<String>> entry : userLanguagesMap.entrySet()) {
	    Long userId = entry.getKey();
	    List<String> assignedLanguages = entry.getValue();

	    /* both users are assigned on one language */
	    assertTrue(assignedLanguages.size() == 1);

	    if (userId == 1) {
		assertTrue(assignedLanguages.contains("de-DE"));
	    } else {
		assertTrue(assignedLanguages.contains("en-UK"));
	    }
	}
    }

    @Test
    public void projectUserLanguagesTest() {
	List<ProjectUserLanguage> projectUserLanguages1 = getProjectUserLanguageDAO()
		.getProjectUserLanguages(PROJECT_ID_01, USER_PROFILE_ID_01);
	List<ProjectUserLanguage> projectUserLanguages2 = getProjectUserLanguageDAO()
		.getProjectUserLanguages(PROJECT_ID_02, USER_PROFILE_ID_01);

	assertNotNull(projectUserLanguages1);
	assertNotNull(projectUserLanguages2);

	assertTrue(CollectionUtils.isNotEmpty(projectUserLanguages1));
	assertTrue(CollectionUtils.isNotEmpty(projectUserLanguages2));
	assertEquals(projectUserLanguages1.size(), 2);
	assertEquals(projectUserLanguages2.size(), 1);
    }

    @Test
    public void recodeProjectUserLanguageTest() {
	ProjectUserLanguageDAO dao = getProjectUserLanguageDAO();
	String languageFrom = "en";
	String languageTo = "en-CA";

	List<ProjectUserLanguage> projectUserLanguages = dao.getProjectUserLanguagesByProject(PROJECT_ID_01);
	assertEquals(projectUserLanguages.size(), 6);

	List<ProjectUserLanguage> enProjectUserLang = projectUserLanguages.stream()
		.filter(pul -> pul.getLanguage().equals(languageFrom)).collect(Collectors.toList());
	// Two projectUserLanguages on languageFrom before recoding
	assertEquals(enProjectUserLang.size(), 2);

	// Perform Recode Operation
	dao.recodeProjectUserLanguage(PROJECT_ID_01, languageFrom, languageTo);
	dao.flush();
	dao.clear();

	List<ProjectUserLanguage> recodedProjectUserLanguages = dao.getProjectUserLanguagesByProject(PROJECT_ID_01);
	// Number of projectUserLanguages remains the same
	assertEquals(recodedProjectUserLanguages.size(), projectUserLanguages.size());

	// No projectUserLanguage on languageFrom after recoding
	List<ProjectUserLanguage> languageFromAfterRecoding = recodedProjectUserLanguages.stream()
		.filter(pul -> pul.getLanguage().equals(languageFrom)).collect(Collectors.toList());
	assertTrue(CollectionUtils.isEmpty(languageFromAfterRecoding));

	// All two projectUserLanguages on languageFrom are recoded to languageTo
	List<ProjectUserLanguage> languageToAfterRecoding = recodedProjectUserLanguages.stream()
		.filter(pul -> pul.getLanguage().equals(languageTo)).collect(Collectors.toList());
	assertEquals(languageToAfterRecoding.size(), enProjectUserLang.size());
    }

    private ProjectUserLanguageDAO getProjectUserLanguageDAO() {
	return _projectUserLanguageDAO;
    }
}
