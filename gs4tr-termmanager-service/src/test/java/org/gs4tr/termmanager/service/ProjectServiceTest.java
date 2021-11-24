package org.gs4tr.termmanager.service;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.entities.model.search.ProjectSearchRequest;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.termmanager.model.Attribute;
import org.gs4tr.termmanager.model.AttributeLevelEnum;
import org.gs4tr.termmanager.model.BaseTypeEnum;
import org.gs4tr.termmanager.model.InputFieldTypeEnum;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.ProjectLanguage;
import org.gs4tr.termmanager.model.ProjectMetadata;
import org.gs4tr.termmanager.model.ProjectMetadataRequest;
import org.gs4tr.termmanager.model.Statistics;
import org.gs4tr.termmanager.model.TermEntryAttributeTypeEnum;
import org.gs4tr.termmanager.model.TmOrganization;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.event.StatisticsInfo;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.service.impl.Messages;
import org.gs4tr.termmanager.service.utils.MailHelper.ReportType;
import org.gs4tr.termmanager.service.utils.StatisticsUtils;
import org.junit.Assert;
import org.junit.Test;

public class ProjectServiceTest extends AbstractSpringServiceTests {

    @Test
    public void checkIfProjectsAreDisabledTest() {

	Long disabledProjectId = 5L;
	boolean isExceptionThrown = false;

	try {
	    getProjectService().checkIfProjectsAreDisabled(singletonList(disabledProjectId));
	} catch (Exception e) {
	    isExceptionThrown = true;
	    assertTrue(e instanceof UserException);
	    assertEquals(e.getMessage(), Messages.getString("ProjectServiceImpl.12"));
	}

	assertTrue(isExceptionThrown);

    }

    @Test
    public void getProjectMetadata() {
	ProjectMetadataRequest request = new ProjectMetadataRequest();
	request.setProjectName("tRANs");
	request.setProjectName("metadata");

	List<ProjectMetadata> metadataList = getProjectService().getProjectMetadata(request);
	assertNotNull(metadataList);
	assertTrue(CollectionUtils.isNotEmpty(metadataList));
	assertEquals(1, metadataList.size());

	for (ProjectMetadata metadata : metadataList) {
	    assertNotNull(metadata.getOrganizationName());
	    assertNotNull(metadata.getProjectName());
	    assertNotNull(metadata.getProjectShortcode());
	    assertNotNull(metadata.getUsername());
	    assertNotNull(metadata.getPassword());
	    assertNotNull(metadata.getLanguages());
	    assertTrue(CollectionUtils.isNotEmpty(metadata.getLanguages()));
	}
    }

    @Test
    public void testAddOrUpdateProjectAttributes() {
	Long projectId = new Long(1);

	String attributeName = "attribute name";

	Attribute attribute = new Attribute();
	attribute.setAttributeLevel(AttributeLevelEnum.LANGUAGE);
	attribute.setComboValues("comboValue");
	attribute.setInputFieldTypeEnum(InputFieldTypeEnum.COMBO);
	attribute.setName(attributeName);
	attribute.setTermEntryAttributeType(TermEntryAttributeTypeEnum.TEXT);
	attribute.setBaseTypeEnum(BaseTypeEnum.DESCRIPTION);

	List<Attribute> projectAttributes = new ArrayList<Attribute>();
	projectAttributes.add(attribute);

	Long result = getProjectService().addOrUpdateProjectAttributes(projectId, projectAttributes);

	assertEquals(projectId, result);

	TmProject project = getProjectService().findProjectById(projectId, Attribute.class);
	assertNotNull(project);
	List<Attribute> attributes = project.getDescriptions();
	assertNotNull(attributes);
	assertTrue(CollectionUtils.isNotEmpty(attributes));

	for (Attribute projetcAttribute : attributes) {
	    String name = projetcAttribute.getName();
	    assertEquals(attributeName, name);
	}
    }

    @Test
    public void testAddOrUpdateProjectNotes() {
	Long projectId = new Long(1);

	Attribute projectNote = new Attribute();
	projectNote.setComboValues("comboValues");
	projectNote.setInputFieldTypeEnum(InputFieldTypeEnum.COMBO);
	String projectNoteName = "projectNote name";
	projectNote.setName(projectNoteName);
	projectNote.setBaseTypeEnum(BaseTypeEnum.NOTE);

	List<Attribute> projectNotes = new ArrayList<Attribute>();
	projectNotes.add(projectNote);

	Long result = getProjectService().addOrUpdateProjectAttributes(projectId, projectNotes);
	assertNotNull(result);

	TmProject project = getProjectService().findProjectById(projectId, Attribute.class);
	assertNotNull(project);
	List<Attribute> projectNotes2 = project.getNotes();
	assertNotNull(projectNotes2);
	assertTrue(CollectionUtils.isNotEmpty(projectNotes2));

	String changedNoteName = "changed note name";
	List<Attribute> newProjectNotes = new ArrayList<Attribute>();

	for (Attribute note : projectNotes2) {
	    assertNotNull(note);
	    assertEquals(projectNoteName, note.getName());

	    note.setName(changedNoteName);
	    newProjectNotes.add(note);
	}

	Long result2 = getProjectService().addOrUpdateProjectAttributes(projectId, newProjectNotes);
	assertNotNull(result2);

	TmProject project2 = getProjectService().findProjectById(projectId, Attribute.class);
	assertNotNull(project2);
	List<Attribute> notes = project2.getNotes();
	assertNotNull(notes);
	assertTrue(CollectionUtils.isNotEmpty(notes));
	assertEquals(1, notes.size());
	boolean isChanged = false;
	for (Attribute note : notes) {
	    if (changedNoteName.equals(note.getName())) {
		isChanged = true;
	    }
	}

	assertTrue(isChanged);
    }

    @Test
    public void testAddOrUpdateProjectUserLanguages() {
	Long userId = new Long(3);
	Long projectId = new Long(1);
	Long result = getProjectService().addOrUpdateProjectUserLanguages(userId, projectId, null, null, null);
	assertNotNull(result);
	assertEquals(userId, result);
    }

    @Test
    public void testClearStatistics() {
	List<Long> statisticsIds = new ArrayList<Long>();

	Long userId = 3L;

	List<Statistics> sts = getProjectService().getUserStatistics(userId, ReportType.DAILY.getName());
	Assert.assertTrue(CollectionUtils.isNotEmpty(sts));

	for (Statistics st : sts) {
	    Assert.assertEquals(1, st.getAddedApproved());
	    statisticsIds.add(st.getStatisticsId());
	}

	getProjectService().clearStatistics(statisticsIds);

	sts = getProjectService().getUserStatistics(userId, ReportType.DAILY.getName());
	Assert.assertTrue(CollectionUtils.isNotEmpty(sts));

	for (Statistics st : sts) {
	    Assert.assertEquals(0, st.getAddedApproved());
	}
    }

    @Test
    public void testFindProjectIdsByShortCodes() {
	List<Long> projectIds = Arrays.asList(1L, 2L);

	List<TmProject> projects = getProjectService().findProjectByIds(projectIds);

	Assert.assertTrue(CollectionUtils.isNotEmpty(projects));

	List<String> shortCodes = new ArrayList<>();

	projects.forEach(p -> shortCodes.add(p.getProjectInfo().getShortCode()));

	List<Long> ids = getProjectService().findProjectIdsByShortCodes(shortCodes);
	Assert.assertTrue(CollectionUtils.isNotEmpty(ids));
	Assert.assertEquals(2, ids.size());
	Assert.assertTrue(ids.containsAll(projectIds));
    }

    @Test
    public void testGetProjectAssignees() {
	Long projectId = 1L;
	Map<String, Set<TmUserProfile>> languageAssignees = getProjectService().getLanguageAssignees(projectId,
		"en-US");
	Assert.assertTrue(!languageAssignees.isEmpty());

	for (Entry<String, Set<TmUserProfile>> entry : languageAssignees.entrySet()) {
	    Set<TmUserProfile> assignees = entry.getValue();
	    Assert.assertTrue(CollectionUtils.isNotEmpty(assignees));
	    for (TmUserProfile tmUserProfile : assignees) {
		Assert.assertTrue(tmUserProfile.getUserInfo().isEnabled());
	    }
	}
    }

    /*
     * only one user have fr-FR language and only that user should be assigned to
     * submission
     */
    @Test
    public void testGetProjectAssigneesOnlyOneWithSourceLanguageTest() {
	Long projectId = 1L;
	Map<String, Set<TmUserProfile>> languageAssignees = getProjectService().getLanguageAssignees(projectId,
		"fr-FR");
	Assert.assertTrue(!languageAssignees.isEmpty());

	for (Entry<String, Set<TmUserProfile>> entry : languageAssignees.entrySet()) {
	    Set<TmUserProfile> assignees = entry.getValue();
	    Assert.assertTrue(CollectionUtils.isNotEmpty(assignees));
	    Assert.assertTrue(assignees.size() == 1);
	    for (TmUserProfile tmUserProfile : assignees) {
		Assert.assertTrue(tmUserProfile.getUserInfo().isEnabled());
	    }
	}

    }

    @Test
    public void testGetProjectAssigneesPowerUser() {
	Long projectId = 1L;
	Map<String, Set<TmUserProfile>> projectPowerAssignees = getProjectService().getLanguagePowerUsers(projectId);
	Assert.assertTrue(!projectPowerAssignees.isEmpty());

	for (Entry<String, Set<TmUserProfile>> entry : projectPowerAssignees.entrySet()) {
	    Set<TmUserProfile> assignees = entry.getValue();
	    Assert.assertTrue(CollectionUtils.isNotEmpty(assignees));

	    for (TmUserProfile tmUserProfile : assignees) {
		Assert.assertTrue(tmUserProfile.isPowerUser());
	    }
	}
    }

    @Test
    public void testGetProjectLanguages() {
	Long projectId = new Long(1);
	List<String> projectLanguages = getProjectService().getProjectLanguageCodes(projectId);

	assertNotNull(projectLanguages);
	assertTrue(CollectionUtils.isNotEmpty(projectLanguages));
    }

    @Test
    public void testGetProjectUsersByLanguageIds() {

	/*
	 * Four users are assigned for ("de-DE"),("en-US") and ("fr-FR") project
	 * language
	 */

	Long projectId = 1L;
	List<ProjectLanguage> projectLanguages = getProjectService().getProjectLanguages(projectId);

	List<TmUserProfile> usersByProjectAndLanguageIds = getProjectService().getProjectUsersByLanguageIds(projectId,
		projectLanguages);

	assertNotNull(usersByProjectAndLanguageIds);
	assertTrue(CollectionUtils.isNotEmpty(usersByProjectAndLanguageIds));
	assertEquals(usersByProjectAndLanguageIds.size(), 4);
    }

    @Test
    public void testGetProjectUsersByLanguageIdsNoUserFound() {

	/* No users are assigned for ("it-IT") project language */

	Long projectId = 1L;
	TmProject project = getProjectService().findProjectById(projectId, ProjectLanguage.class);

	ProjectLanguage projectLanguage = new ProjectLanguage();
	projectLanguage.setLanguage("it-IT");
	projectLanguage.setProject(project);

	List<TmUserProfile> usersByProjectAndLanguageIds = getProjectService().getProjectUsersByLanguageIds(projectId,
		Arrays.asList(projectLanguage));

	assertTrue(CollectionUtils.isEmpty(usersByProjectAndLanguageIds));

    }

    @Test
    public void testGetRolesByUserAndProject() {
	Long userId = new Long(3);
	Long projectId = new Long(1);
	List<Role> roles = getProjectService().getRolesByUserAndProject(projectId, userId);
	assertNotNull(roles);
	assertTrue(CollectionUtils.isNotEmpty(roles));
    }

    @Test
    public void testGetUserLanguagesMap() {
	Long projectId = new Long(1);
	Map<Long, List<String>> userLanguagesMap = getProjectService().getUserLanguagesMap(projectId);
	assertNotNull(userLanguagesMap);
	assertTrue(userLanguagesMap.size() == 4);

	Long user1 = 3L;
	Long user2 = 6L;
	Long user3 = 7L;
	Long user4 = 9L;

	List<Long> userIds = Arrays.asList(user1, user2, user3, user4);

	Set<Long> userIdsKeySet = userLanguagesMap.keySet();
	assertTrue(userIdsKeySet.containsAll(userIds));

	for (Long userId : userIds) {
	    List<String> assignedLanguages = userLanguagesMap.get(userId);
	    /* every user is assigned on en-US */
	    assertTrue(assignedLanguages.contains("en-US"));
	    if (userId.equals(user1)) {
		assertTrue(assignedLanguages.size() == 1);
	    } else if (userId.equals(user2)) {
		assertTrue(assignedLanguages.size() == 2);
		assertTrue(assignedLanguages.contains("de-DE"));
	    } else {
		/* user3 and user4 are assigned on same langugages */
		assertTrue(assignedLanguages.size() == 3);
		assertTrue(assignedLanguages.contains("de-DE"));
		assertTrue(assignedLanguages.contains("fr-FR"));
	    }
	}
    }

    @Test
    public void testGetUserProjectStatistics() {
	Long userId = 3L;

	Map<String, List<Statistics>> stsMap = getProjectService().getUserProjectStatistics(userId,
		ReportType.DAILY.getName());
	Assert.assertNotNull(stsMap);

	for (Entry<String, List<Statistics>> entry : stsMap.entrySet()) {
	    Assert.assertTrue(StringUtils.isNotEmpty(entry.getKey()));
	    Assert.assertTrue(CollectionUtils.isNotEmpty(entry.getValue()));
	}
    }

    @Test
    public void testGetUserProjects() {
	Long id = new Long(3);
	List<TmProject> projects = getProjectService().getUserProjects(id, TmOrganization.class);

	assertNotNull(projects);
	assertTrue(CollectionUtils.isNotEmpty(projects));
	assertNotNull(projects.get(0).getOrganization());
    }

    @Test
    public void testGetUserStatistics() {
	Long userId = 3L;

	List<Statistics> sts = getProjectService().getUserStatistics(userId, ReportType.DAILY.getName());
	Assert.assertTrue(CollectionUtils.isNotEmpty(sts));
    }

    @Test
    public void testRenameProjectAttributes() {
	Long projectId = 1L;

	String attributeName = "custom";

	TmProject project = getProjectService().findProjectById(projectId, Attribute.class);

	assertNotNull(project);

	List<Attribute> attributes = project.getAttributes();

	assertTrue(CollectionUtils.isNotEmpty(attributes));

	Attribute attributeForRenaming = attributes.get(0);
	attributeForRenaming.setRenameValue(attributeName);

	List<Attribute> projectAttributes = new ArrayList<Attribute>();
	projectAttributes.add(attributeForRenaming);

	getProjectService().addOrUpdateProjectAttributes(projectId, projectAttributes);

	project = getProjectService().findProjectById(projectId, Attribute.class);

	attributes = project.getAttributes();

	assertTrue(CollectionUtils.isNotEmpty(attributes));

	boolean flag = false;
	for (Attribute projetcAttribute : attributes) {
	    String name = projetcAttribute.getName();
	    flag = name.equals(attributeName);
	    if (flag) {
		break;
	    }
	}

	Assert.assertTrue(flag);
    }

    @Test
    public void testSearch() {
	String clientIdentifier = "testProject";
	String organizationName = "Translations";
	String name = "testProject";

	ProjectSearchRequest command = new ProjectSearchRequest();
	command.setClientIdentifier(clientIdentifier);
	command.setName(name);
	command.setOrganizationName(organizationName);

	PagedListInfo pagedListInfo = new PagedListInfo();

	PagedList<TmProject> result = getProjectService().search(command, pagedListInfo);

	assertNotNull(result);
	assertNotNull(result.getElements());
	assertTrue(result.getElements().length > 0);
    }

    @Test
    public void testSearchByOrganizationName() {

	String organizationName = "Translations";

	ProjectSearchRequest command = new ProjectSearchRequest();
	command.setOrganizationName(organizationName);

	PagedListInfo pagedListInfo = new PagedListInfo();

	PagedList<TmProject> result = getProjectService().search(command, pagedListInfo);
	assertNotNull(result);

	TmProject[] projects = result.getElements();
	assertNotNull(projects);

	for (TmProject project : projects) {
	    assertEquals(project.getOrganization().getOrganizationInfo().getName(), organizationName);
	}
    }

    @Test
    public void testUpdateProjectProperties() {
	TmProject project = getProjectService().load(1L);

	Boolean sharePendingTerms = project.getSharePendingTerms();

	Map<String, Object> command = new HashMap<>();

	command.put("sharePendingTerms", !sharePendingTerms);
	command.put("defaultTermStatus", ItemStatusTypeHolder.ON_HOLD.getName());

	getProjectService().updateProjectProperties(1L, command);

	project = getProjectService().load(1L);
	Assert.assertEquals(ItemStatusTypeHolder.ON_HOLD.getName(), project.getDefaultTermStatus().getName());
	Assert.assertEquals(!sharePendingTerms, project.getSharePendingTerms());

    }

    @Test
    public void testUpdateStatistics_case1() {
	Long userId = 3L;
	String languageId = "en-US";

	List<Statistics> sts = getProjectService().getUserStatistics(userId, "daily");
	Assert.assertTrue(CollectionUtils.isNotEmpty(sts));
	for (Statistics st : sts) {
	    Assert.assertEquals(1, st.getDemoted());
	    Assert.assertEquals(1, st.getApproved());
	}

	Long projectId = 1L;

	Term term = new Term();
	term.setProjectId(projectId);
	term.setLanguageId(languageId);
	term.setStatus(ItemStatusTypeHolder.PROCESSED.getName());

	Set<StatisticsInfo> statisticsInfos = new HashSet<>();
	StatisticsUtils.incrementStatistics(statisticsInfos, term.getProjectId(), term.getLanguageId(),
		term.getStatus());

	Term term1 = new Term();
	term1.setProjectId(projectId);
	term1.setLanguageId(languageId);
	term1.setStatus(ItemStatusTypeHolder.WAITING.getName());

	StatisticsUtils.incrementStatistics(statisticsInfos, term1.getProjectId(), term1.getLanguageId(),
		term1.getStatus());

	getStatisticsService().updateStatistics(statisticsInfos);

	sts = getProjectService().getUserStatistics(userId, "daily");
	Assert.assertTrue(CollectionUtils.isNotEmpty(sts));
	for (Statistics st : sts) {
	    Assert.assertEquals(2, st.getDemoted());
	    Assert.assertEquals(2, st.getApproved());
	}
    }
}
