package org.gs4tr.termmanager.dao.hibernate;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.entities.model.ProjectInfo;
import org.gs4tr.termmanager.model.Attribute;
import org.gs4tr.termmanager.model.ProjectDetail;
import org.gs4tr.termmanager.model.ProjectLanguage;
import org.gs4tr.termmanager.model.ProjectMetadata;
import org.gs4tr.termmanager.model.ProjectMetadataRequest;
import org.gs4tr.termmanager.model.TmProject;
import org.junit.Assert;
import org.junit.Test;

public class ProjectDaoTest extends AbstractSpringDAOIntegrationTest {

    private static final Long DISABLED_PROJECT_ID_1 = 5L;

    private static final Long DISABLED_PROJECT_ID_2 = 6L;

    private static final String EXPECTED_DATA_SET_NAME = "expectedSaveProject";

    private static final Long ORGANIZATION_ID_01 = 1L;

    private static final Long PROJECT_ID_01 = 2L;

    private static final Long PROJECT_ID_02 = 1L;

    private static final Long PROJECT_ID_03 = 4L;

    private static final String PROJECT_TABLE = "TM_PROJECT";

    @Test
    public void findAllDisabledProjectsTest() {
	List<Long> disabledProjectIds = getProjectDAO().findAllDisabledProjectIds();

	assertNotNull(disabledProjectIds);

	assertTrue(isNotEmpty(disabledProjectIds));
	assertEquals(2, disabledProjectIds.size());
    }

    @Test
    public void findAllEnabledProjectIdsTest() {
	List<Long> enabledProjectIds = getProjectDAO().findAllEnabledProjectIds();

	assertNotNull(enabledProjectIds);

	assertTrue(isNotEmpty(enabledProjectIds));
	assertTrue(enabledProjectIds.contains(PROJECT_ID_01));
	assertTrue(enabledProjectIds.contains(PROJECT_ID_02));
	assertTrue(enabledProjectIds.contains(PROJECT_ID_03));
	assertEquals(5, enabledProjectIds.size());
    }

    @Test
    public void findAllEnabledProjectsTest() {
	List<TmProject> enabledProjects = getProjectDAO().findAllEnabledProjects();

	assertNotNull(enabledProjects);

	assertTrue(isNotEmpty(enabledProjects));
	assertEquals(5, enabledProjects.size());
    }

    @Test
    public void findAttributesByProjectIdsTest() {
	TmProject tmProject = getProjectDAO().findById(PROJECT_ID_02);

	List<Attribute> projectAttributes = tmProject.getAttributes();

	assertNotNull(tmProject);
	assertNotNull(projectAttributes);

	assertEquals(2, projectAttributes.size());
	assertEquals("customAttribute1", projectAttributes.get(0).getName());
	assertEquals("customAttribute2", projectAttributes.get(1).getName());

    }

    @Test
    public void findDisabledProjectIdsTest() {
	List<Long> projectIds = new ArrayList<>(Arrays.asList(PROJECT_ID_01, PROJECT_ID_02, PROJECT_ID_03,
		DISABLED_PROJECT_ID_1, DISABLED_PROJECT_ID_2));

	assertTrue(getProjectDAO().checkIfProjectsAreDisabled(projectIds));
    }

    @Test
    public void findProjectByIdTest() {
	TmProject tmProject = getProjectDAO().findById(PROJECT_ID_02);

	ProjectInfo projectInfo = tmProject.getProjectInfo();

	ProjectDetail projectDetail = tmProject.getProjectDetail();

	assertNotNull(tmProject);
	assertNotNull(tmProject.getOrganization());
	assertNotNull(projectDetail);
	assertNotNull(tmProject.getAttributes());

	assertEquals("testProject", projectInfo.getName());
	assertTrue(projectInfo.isEnabled());

	assertFalse(tmProject.getAddApprovedTerms());

	assertEquals(4, projectDetail.getLanguageCount());
	assertEquals(6, projectDetail.getApprovedTermCount());
	assertEquals(0, projectDetail.getForbiddenTermCount());
	assertEquals(2, projectDetail.getTermInSubmissionCount());
	assertEquals(8, projectDetail.getTermCount());

	assertEquals(0, projectDetail.getCompletedSubmissionCount());

	assertEquals("testProject", projectInfo.getClientIdentifier());
	assertEquals("TEST001", projectInfo.getShortCode());
    }

    @Test
    public void findProjectIdsByShortCodesTest() {
	List<TmProject> projects = getProjectDAO().findByIds(Arrays.asList(PROJECT_ID_01, PROJECT_ID_02));
	Assert.assertTrue(isNotEmpty(projects));

	List<String> shortCodes = new ArrayList<>();

	projects.forEach(p -> shortCodes.add(p.getProjectInfo().getShortCode()));

	List<Long> ids = getProjectDAO().findProjectIdsByShortCodes(shortCodes);
	Assert.assertTrue(isNotEmpty(ids));
	Assert.assertEquals(2, ids.size());
	Assert.assertTrue(ids.contains(PROJECT_ID_01));
	Assert.assertTrue(ids.contains(PROJECT_ID_02));
    }

    @Test
    public void findProjectLanguagesByProjectIdTest() {
	List<ProjectLanguage> projectLanguages1 = getProjectDAO().findProjectLanguagesByProjectId(PROJECT_ID_01);
	List<ProjectLanguage> projectLanguages2 = getProjectDAO().findProjectLanguagesByProjectId(PROJECT_ID_02);

	assertNotNull(projectLanguages1);
	assertNotNull(projectLanguages2);

	assertTrue(isNotEmpty(projectLanguages1));
	assertTrue(isNotEmpty(projectLanguages2));

	assertEquals(1, projectLanguages1.size());
	assertEquals(4, projectLanguages2.size());
    }

    @Test
    public void findProjectsByIdsTest() {
	final List<Long> projectIds = new ArrayList<>();

	projectIds.add(PROJECT_ID_01);
	projectIds.add(PROJECT_ID_02);
	projectIds.add(PROJECT_ID_03);

	List<TmProject> tmProjectList = getProjectDAO().findProjectsByIds(projectIds);

	TmProject tmProject1 = getProjectDAO().findById(PROJECT_ID_01);
	TmProject tmProject2 = getProjectDAO().findById(PROJECT_ID_02);
	TmProject tmProject3 = getProjectDAO().findById(PROJECT_ID_03);

	assertNotNull(tmProjectList);

	assertTrue(isNotEmpty(tmProjectList));
	assertTrue(tmProjectList.contains(tmProject1));
	assertTrue(tmProjectList.contains(tmProject2));
	assertTrue(tmProjectList.contains(tmProject3));
	assertEquals(3, tmProjectList.size());

    }

    @Test
    public void findProjectsByNameLikeTest() {
	List<Long> xxx = getProjectDAO().findProjectsByNameLike("xxx");
	assertEquals(2, xxx.size());

	List<Long> XXX = getProjectDAO().findProjectsByNameLike("XXX");
	assertEquals(2, XXX.size());

	List<Long> xXx = getProjectDAO().findProjectsByNameLike("xXx");
	assertEquals(2, xXx.size());

	List<Long> XxX = getProjectDAO().findProjectsByNameLike("XxX");
	assertEquals(2, XxX.size());
    }

    @Test
    public void getOrganizationIdByProjectTest() {
	Long organizationId = getProjectDAO().getOrganizationIdByProject(PROJECT_ID_01);

	assertNotNull(organizationId);

	assertEquals(ORGANIZATION_ID_01, organizationId);
    }

    @Test
    public void getProjectMetadataTestWithOrganizationNameAndProjectNameTest() {

	ProjectMetadataRequest request = new ProjectMetadataRequest();
	request.setOrganizationName("Translations");
	request.setProjectName("trans");

	List<ProjectMetadata> metadataList = getProjectDAO().getProjectMetadata(request);

	assertNotNull(metadataList);

	assertTrue(isNotEmpty(metadataList));

	validateProjectMetadata(metadataList, request);

    }

    @Test
    public void getProjectMetadataTestWithOrganizationNameProjectNameAndLanguagesAndUserNameTest() {

	List<String> languages = new ArrayList<>();
	languages.add("en-US");
	languages.add("fr-FR");

	ProjectMetadataRequest request = new ProjectMetadataRequest();
	request.setOrganizationName("TRAns");
	request.setProjectName("trans");
	request.setLanguages(languages);
	request.setUsername("nnie_B");

	List<ProjectMetadata> metadataList = getProjectDAO().getProjectMetadata(request);

	assertNotNull(metadataList);

	assertTrue(isNotEmpty(metadataList));
	assertEquals(1, metadataList.size());

	validateProjectMetadata(metadataList, request);

    }

    @Test
    public void getProjectMetadataTestWithOrganizationNameTest() {

	ProjectMetadataRequest request = new ProjectMetadataRequest();
	request.setOrganizationName("trANs");

	List<ProjectMetadata> metadataList = getProjectDAO().getProjectMetadata(request);

	assertNotNull(metadataList);

	assertTrue(isNotEmpty(metadataList));

	validateProjectMetadata(metadataList, request);

    }

    @Test
    public void getProjectMetadataTestWithProjectNameTest() {

	ProjectMetadataRequest request = new ProjectMetadataRequest();
	request.setProjectName("tes");

	List<ProjectMetadata> metadataList = getProjectDAO().getProjectMetadata(request);

	assertNotNull(metadataList);

	assertTrue(isNotEmpty(metadataList));

	validateProjectMetadata(metadataList, request);

    }

    @Test
    public void getProjectMetadataTestWithoutAnyParameterTest() {

	ProjectMetadataRequest request = new ProjectMetadataRequest();

	List<ProjectMetadata> metadataList = getProjectDAO().getProjectMetadata(request);

	assertNotNull(metadataList);

	assertTrue(isNotEmpty(metadataList));
    }

    @Test
    public void getProjectMetadataWithLanguagesTest() {

	List<String> languages = new ArrayList<>();
	languages.add("en-US");

	ProjectMetadataRequest request = new ProjectMetadataRequest();
	request.setLanguages(languages);

	List<ProjectMetadata> metadataList = getProjectDAO().getProjectMetadata(request);

	assertNotNull(metadataList);

	assertTrue(isNotEmpty(metadataList));

	validateProjectMetadata(metadataList, request);

    }

    @Test
    public void getProjectMetadataWithPInvalidProjectShortCodeTest() {

	ProjectMetadataRequest request = new ProjectMetadataRequest();
	request.setProjectShortcode("TESS001");

	List<ProjectMetadata> metadataList = getProjectDAO().getProjectMetadata(request);

	assertNotNull(metadataList);

	assertTrue(CollectionUtils.isEmpty(metadataList));

    }

    @Test
    public void getProjectMetadataWithProjectShortCodeTest() {

	ProjectMetadataRequest request = new ProjectMetadataRequest();
	request.setProjectShortcode("TEST001");

	List<ProjectMetadata> metadataList = getProjectDAO().getProjectMetadata(request);

	assertNotNull(metadataList);

	assertTrue(isNotEmpty(metadataList));

	validateProjectMetadata(metadataList, request);

    }

    @Test
    public void getProjectMetadataWontBeReturnedForOpeUserTest1() {

	ProjectMetadataRequest request = new ProjectMetadataRequest();

	// OPE user's project shortcode
	request.setProjectShortcode("TEST004");

	List<ProjectMetadata> metadataList = getProjectDAO().getProjectMetadata(request);

	assertNotNull(metadataList);

	assertTrue(CollectionUtils.isEmpty(metadataList));

    }

    @Test
    public void getProjectMetadataWontBeReturnedForOpeUserTest2() {

	ProjectMetadataRequest request = new ProjectMetadataRequest();

	// OPE user's username
	request.setUsername("giga");

	List<ProjectMetadata> metadataList = getProjectDAO().getProjectMetadata(request);

	assertNotNull(metadataList);

	assertTrue(CollectionUtils.isEmpty(metadataList));

    }

    @Test
    public void testFindById() {
	Long projectId = 1L;

	TmProject project = getProjectDAO().findById(projectId);

	assertNotNull(project);

	Set<ProjectLanguage> projectLanguages = project.getProjectLanguages();
	assertNotNull(projectLanguages);
	assertEquals(projectLanguages.size(), 4);

	String language = Locale.CANADA.getCode();
	ProjectLanguage language2 = new ProjectLanguage(project, language);
	projectLanguages.add(language2);

	getProjectDAO().update(project);

	project = getProjectDAO().findById(projectId);

	projectLanguages = project.getProjectLanguages();
	assertNotNull(projectLanguages);
	assertEquals(5, projectLanguages.size());
    }

    @Test
    public void testGetAttributesByProjectId() {
	List<Attribute> attributes = getProjectDAO().getAttributesByProjectId(1L);
	assertNotNull(attributes);
	assertTrue(isNotEmpty(attributes));

	for (Attribute att : attributes) {
	    assertTrue(StringUtils.isNotEmpty(att.getName()));
	    assertNotNull(att.getBaseTypeEnum());
	    assertEquals("val1|val2", att.getComboValues());
	    assertTrue(StringUtils.isEmpty(att.getValue()));
	}
    }

    @Test
    public void testSave() throws Exception {

	TmProject project = createFreshProject();

	TmProject projectNew = getProjectDAO().save(project);

	assertNotNull(projectNew.getProjectId());

	assertDatabaseTables(PROJECT_TABLE, EXPECTED_DATA_SET_NAME);
    }

    private TmProject createFreshProject() {
	TmProject project = new TmProject();

	ProjectInfo projectInfo = new ProjectInfo();
	projectInfo.setName("testProjectCreation");
	projectInfo.setShortCode("TEST005");

	project.setProjectInfo(projectInfo);

	return project;
    }

    private void validateProjectMetadata(List<ProjectMetadata> metadataList, ProjectMetadataRequest request) {

	for (ProjectMetadata projectMetadata : metadataList) {

	    if (request.getUsername() != null) {
		assertTrue(projectMetadata.getUsername().toLowerCase().contains(request.getUsername().toLowerCase()));
	    }

	    if (request.getProjectShortcode() != null) {
		assertEquals(projectMetadata.getProjectShortcode(), request.getProjectShortcode());
	    }

	    if (request.getProjectName() != null) {
		assertTrue(projectMetadata.getProjectName().toLowerCase()
			.contains(request.getProjectName().toLowerCase()));
	    }

	    if (request.getOrganizationName() != null) {
		assertTrue(projectMetadata.getOrganizationName().toLowerCase()
			.contains(request.getOrganizationName().toLowerCase()));
	    }

	    if (isNotEmpty(request.getLanguages())) {
		assertTrue(projectMetadata.getLanguages().containsAll(request.getLanguages()));
	    }
	}

    }

}
