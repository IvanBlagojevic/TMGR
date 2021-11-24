package org.gs4tr.termmanager.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.model.ProjectUserLanguage;
import org.gs4tr.termmanager.model.Statistics;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ProjectUserLanguageServiceTest extends AbstractSpringServiceTests {

    private static final Long PROJECT_ID_01 = 1L;

    @Autowired
    private ProjectUserLanguageService _projectUserLanguageService;

    @Test
    public void cloneProjectUserLanguageTest() {
	String localeTo = "en-GB";

	TmProject project = getProjectService().load(PROJECT_ID_01);

	// Prepare values before cloning for assert
	Set<TmUserProfile> projectUsers = getProjectService().getProjectUsersForRecodeOrClone(PROJECT_ID_01);
	Assert.assertEquals(4, projectUsers.size());

	List<ProjectUserLanguage> pulBefore = getProjectService().findProjectUserLanguageByProjectId(PROJECT_ID_01);
	Assert.assertEquals(pulBefore.size(), 9);

	List<Statistics> statisticsBefore = getStatisticsService().findStatisticsByProjectId(PROJECT_ID_01);
	Assert.assertEquals(6, statisticsBefore.size());

	// Note:One user is power user so we skipped projectUserLang creation for him

	// Perform Clone operation
	projectUsers
		.forEach(user -> getProjectUserLanguageService().cloneProjectUserLanguages(user, project, localeTo));

	List<ProjectUserLanguage> pulAfter = getProjectService().findProjectUserLanguageByProjectId(PROJECT_ID_01);
	// List of projectUserLangs has been increased for number of users
	Assert.assertEquals(pulAfter.size(), pulBefore.size() + (projectUsers.size() - 1));
	// ProjectUserLanguages that are not on languageTo are same as before cloning
	Assert.assertTrue(pulAfter.containsAll(pulBefore));

	List<ProjectUserLanguage> clonedPul = pulAfter.stream().filter(cPul -> cPul.getLanguage().equals(localeTo))
		.collect(Collectors.toList());
	// Number of projectUserLangs on localeTo is equals to number of users
	Assert.assertEquals((projectUsers.size() - 1), clonedPul.size());

	// Daily and weekly statistics are added to new projectUserLanguages
	List<Statistics> statisticsAfter = getStatisticsService().findStatisticsByProjectId(PROJECT_ID_01);
	Assert.assertEquals(statisticsAfter.size(), statisticsBefore.size() + 6);
    }

    @Test
    public void recodeProjectUserLanguageTest() {
	String localeFrom = "en-US";
	String localeTo = "en-GB";

	List<ProjectUserLanguage> pulListBefore = getProjectService().findProjectUserLanguageByProjectId(PROJECT_ID_01);

	List<ProjectUserLanguage> pulBefore = pulListBefore.stream().filter(pul -> pul.getLanguage().equals(localeFrom))
		.collect(Collectors.toList());
	// Four users are assigned on languageFrom before recode operation
	assertEquals(pulBefore.size(), 4);

	List<Statistics> statisticsBefore = getStatisticsService().findStatisticsByProjectId(PROJECT_ID_01);

	// Perform Recode Operation
	getProjectUserLanguageService().recodeProjectUserLanguage(PROJECT_ID_01, localeFrom, localeTo);

	List<ProjectUserLanguage> pulListAfter = getProjectService().findProjectUserLanguageByProjectId(PROJECT_ID_01);
	// Number of projectUserLanguages remains the same
	assertEquals(pulListAfter.size(), pulListBefore.size());

	// No projectUserLanguage on languageFrom after recoding
	List<ProjectUserLanguage> enPulAfter = pulListAfter.stream().filter(pul -> pul.getLanguage().equals(localeFrom))
		.collect(Collectors.toList());
	assertTrue(CollectionUtils.isEmpty(enPulAfter));

	// All projectUserLanguages on languageFrom are recoded to languageTo
	List<ProjectUserLanguage> enUsPulAfter = pulListAfter.stream().filter(pul -> pul.getLanguage().equals(localeTo))
		.collect(Collectors.toList());
	assertEquals(enUsPulAfter.size(), pulBefore.size());

	List<Statistics> statisticsAfter = getStatisticsService().findStatisticsByProjectId(PROJECT_ID_01);
	assertEquals(statisticsBefore.size(), statisticsAfter.size());
    }

    private ProjectUserLanguageService getProjectUserLanguageService() {
	return _projectUserLanguageService;
    }
}
