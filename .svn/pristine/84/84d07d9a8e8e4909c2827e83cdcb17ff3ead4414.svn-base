package org.gs4tr.termmanager.io.tests;

import static org.gs4tr.termmanager.io.tests.AbstractIOTest.LANGUAGE_ID;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;

import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.ProjectLanguageDetailInfoIO;
import org.gs4tr.termmanager.model.ProjectUserDetailIO;
import org.gs4tr.termmanager.model.TransactionalUnit;
import org.gs4tr.termmanager.model.dto.ProjectDetailCountsIO;
import org.gs4tr.termmanager.model.dto.ProjectDetailsIO;
import org.gs4tr.termmanager.model.dto.StatisticInfoIO;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.gs4tr.termmanager.model.event.StatisticsInfo;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;

public class TestHelper {

    public static final Long PROJECT_ID_1 = 1L;

    public static final Long PROJECT_ID_2 = 2L;

    public static final String PROJECT_NAME = "PROJECT";

    public static final String TERM_ID = "term-00001";

    public static final String USER = "USER";

    public static Description createDescription(String baseType, String type, String value) {
	Description description = new Description(baseType, type, value);
	description.setUuid(UUID.randomUUID().toString());
	return description;
    }

    public static TermEntry createEmptyTermEntry() {
	TermEntry entry = new TermEntry();
	entry.setUuId(UUID.randomUUID().toString());
	entry.setProjectId(PROJECT_ID_1);
	entry.setProjectName(PROJECT_NAME);
	entry.setRevisionId(1L);
	entry.setUserCreated(USER);
	entry.setUserModified(USER);

	entry.addDescription(createDescription(Description.ATTRIBUTE, "definition", "definition value"));

	return entry;
    }

    public static ProjectDetailsIO createInfoDetails() {

	ProjectDetailsIO projectDetails = new ProjectDetailsIO();
	projectDetails.setProjectId(PROJECT_ID_1);

	ProjectDetailCountsIO infoDetails = createProjectInfoDetails();
	List<ProjectLanguageDetailInfoIO> languageInfos = createProjectLanguageInfo();
	List<ProjectUserDetailIO> userInfos = createProjectUserInfo();

	projectDetails.setProjectInfoDetails(infoDetails);
	projectDetails.setProjectLanguageDetails(languageInfos);
	projectDetails.setProjectUserDetails(userInfos);

	return projectDetails;

    }

    public static ProjectDetailInfo createProjectDetailInfo() {
	ProjectDetailInfo info = new ProjectDetailInfo(PROJECT_ID_1);
	info.addTermCount(LANGUAGE_ID, 1L);
	info.addTermEntryCount(1L);
	info.incrementActiveSubmissionCount();
	info.incrementApprovedTermCount(LANGUAGE_ID);
	info.incrementOnHoldTermCount(LANGUAGE_ID);
	info.incrementPendingTermCount(LANGUAGE_ID);
	info.incrementForbiddenTermCount(LANGUAGE_ID);
	info.incrementCompletedSubmissionCount();
	info.setDateModified(new Date());

	return info;
    }

    public static Set<StatisticsInfo> createStatisticInfo() {
	Set<StatisticsInfo> statisticsInfos = new HashSet<>();
	StatisticsInfo info = new StatisticsInfo(PROJECT_ID_1, LANGUAGE_ID);
	info.incrementAddedApprovedCount();
	statisticsInfos.add(info);
	return statisticsInfos;
    }

    public static Set<StatisticInfoIO> createStatisticsInfoDetails() {
	Set<StatisticInfoIO> details = new HashSet<>();
	StatisticInfoIO infoDetails = new StatisticInfoIO();
	infoDetails.setLanguageId(LANGUAGE_ID);
	infoDetails.setBlackListedCount(1L);
	infoDetails.setApprovedCount(1L);
	infoDetails.setAddedPendingApprovalCount(1L);
	infoDetails.setAddedOnHoldCount(1L);
	infoDetails.setAddedApprovedCount(1L);
	infoDetails.setAddedBlacklistedCount(1L);
	infoDetails.setDeletedCount(1L);
	infoDetails.setOnHoldCount(1L);
	infoDetails.setPendingApprovalCount(1L);
	infoDetails.setUpdatedCount(1L);
	infoDetails.setProjectId(PROJECT_ID_1);
	details.add(infoDetails);
	return details;
    }

    public static Term createSubmissionTerm(String languageId, String name, String status) {
	Term term = createTerm(languageId, name, status);
	term.setParentUuId(UUID.randomUUID().toString());
	term.setCanceled(Boolean.FALSE);
	term.setDateSubmitted(new Date().getTime());
	return term;
    }

    public static TermEntry createSubmissionTermEntry() {
	TermEntry entry = new TermEntry();
	entry.setUuId(UUID.randomUUID().toString());
	entry.setParentUuId(UUID.randomUUID().toString());
	entry.setProjectId(PROJECT_ID_1);
	entry.setProjectName(PROJECT_NAME);
	entry.setRevisionId(1L);
	entry.setUserCreated(USER);
	entry.setUserModified(USER);

	IntStream.range(1, 2).forEach(i -> entry.addTerm(
		createSubmissionTerm("en", UUID.randomUUID().toString(), ItemStatusTypeHolder.PROCESSED.getName())));
	entry.addDescription(createDescription(Description.ATTRIBUTE, "definition", "definition value"));

	return entry;
    }

    public static Term createTerm(String languageId, String name, String status) {
	Term term = new Term();
	term.setUuId(TERM_ID);
	term.setLanguageId(languageId);
	term.setName(name);
	term.setForbidden(false);
	term.setStatus(status);
	term.setUserCreated(USER);
	term.setUserModified(USER);
	term.setDateModified(System.currentTimeMillis());
	term.setDateCreated(System.currentTimeMillis());
	term.setProjectId(PROJECT_ID_1);

	term.addDescription(createDescription(Description.ATTRIBUTE, "context", "context value"));

	return term;
    }

    public static TermEntry createTermEntry() {
	TermEntry entry = new TermEntry();
	entry.setUuId(UUID.randomUUID().toString());
	entry.setProjectId(PROJECT_ID_1);
	entry.setProjectName(PROJECT_NAME);
	entry.setRevisionId(1L);
	entry.setUserCreated(USER);
	entry.setUserModified(USER);

	IntStream.range(1, 10).forEach(i -> entry
		.addTerm(createTerm("en", UUID.randomUUID().toString(), ItemStatusTypeHolder.PROCESSED.getName())));
	entry.addDescription(createDescription(Description.ATTRIBUTE, "definition", "definition value"));

	return entry;
    }

    public static TransactionalUnit createTransactionalUnit() {

	TransactionalUnit unit = new TransactionalUnit();

	TermEntry entry1 = createTermEntry();
	TermEntry entry2 = createTermEntry();
	List<TermEntry> entries = new ArrayList<>();
	entries.add(entry1);
	entries.add(entry2);

	ProjectDetailInfo projectDetailInfo = createProjectDetailInfo();
	Set<StatisticsInfo> statisticsInfo = createStatisticInfo();

	unit.setTermEntries(entries);
	unit.setProjectDetailInfo(projectDetailInfo);
	unit.setStatisticsInfo(statisticsInfo);

	return unit;
    }

    private static ProjectDetailCountsIO createProjectInfoDetails() {
	ProjectDetailCountsIO infoDetails = new ProjectDetailCountsIO();
	infoDetails.setProjectId(PROJECT_ID_1);
	infoDetails.setTermEntryCount(5L);
	infoDetails.setOnHoldTermCount(5L);
	infoDetails.setPendingTermCount(5L);
	infoDetails.setForbiddenTermCount(5L);
	infoDetails.setApprovedTermCount(5L);
	infoDetails.setDateModified(new Date(System.currentTimeMillis()));
	infoDetails.setActiveSubmissionCount(5L);
	infoDetails.setCompletedSubmissionCount(5L);
	infoDetails.setTermInSubmissionCount(5L);
	infoDetails.setTotalCount();
	return infoDetails;
    }

    private static List<ProjectLanguageDetailInfoIO> createProjectLanguageInfo() {

	List<ProjectLanguageDetailInfoIO> infos = new ArrayList<>();
	ProjectLanguageDetailInfoIO languageInfo = new ProjectLanguageDetailInfoIO(LANGUAGE_ID);
	languageInfo.setActiveSubmissionCount(5L);
	languageInfo.setTermInSubmissionCount(5L);
	languageInfo.setTermEntryCount(5L);
	languageInfo.setPendingTermCount(5L);
	languageInfo.setOnHoldTermCount(5L);
	languageInfo.setForbiddenTermCount(5L);
	languageInfo.setDateModified(new Date(System.currentTimeMillis()));
	languageInfo.setApprovedTermCount(5L);
	languageInfo.setProjectId(PROJECT_ID_1);
	languageInfo.setTermInSubmissionCount(5L);
	languageInfo.setCompletedSubmissionCount(5L);
	infos.add(languageInfo);
	return infos;

    }

    private static List<ProjectUserDetailIO> createProjectUserInfo() {

	List<ProjectUserDetailIO> userInfos = new ArrayList<>();

	ProjectUserDetailIO info = new ProjectUserDetailIO(3L);
	info.setCompletedSubmissionCount(5L);
	info.setActiveSubmissionCount(5L);
	info.setDateModified(new Date(System.currentTimeMillis()));
	info.setProjectId(PROJECT_ID_1);
	userInfos.add(info);
	return userInfos;

    }
}
