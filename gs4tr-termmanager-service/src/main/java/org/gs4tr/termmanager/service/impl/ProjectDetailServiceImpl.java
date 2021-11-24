package org.gs4tr.termmanager.service.impl;

import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.gs4tr.foundation.modules.entities.model.PagedListHelper.getPage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.ProjectInfo;
import org.gs4tr.foundation.modules.entities.model.Task;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.foundation.modules.entities.model.types.EntityType;
import org.gs4tr.termmanager.dao.ProjectDetailDAO;
import org.gs4tr.termmanager.dao.ProjectLanguageDetailDAO;
import org.gs4tr.termmanager.dao.ProjectUserDetailDAO;
import org.gs4tr.termmanager.model.EntityTypeHolder;
import org.gs4tr.termmanager.model.ProjectDetail;
import org.gs4tr.termmanager.model.ProjectLanguageDetail;
import org.gs4tr.termmanager.model.ProjectUserDetail;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.gs4tr.termmanager.model.search.command.UserProjectSearchRequest;
import org.gs4tr.termmanager.model.view.ProjectDetailView;
import org.gs4tr.termmanager.model.view.ProjectReport;
import org.gs4tr.termmanager.persistence.solr.faceting.FacetTermCounts;
import org.gs4tr.termmanager.persistence.solr.query.AbstractPageRequest;
import org.gs4tr.termmanager.persistence.solr.query.TmgrPageRequest;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.service.ProjectDetailService;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.utils.AdminTasksHolderHelper;
import org.gs4tr.termmanager.service.utils.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

@Service("projectDetailService")
public class ProjectDetailServiceImpl implements ProjectDetailService {

    private static final String LANGUAGE = "language"; //$NON-NLS-1$

    private static final Log LOG = LogFactory.getLog(ProjectDetailServiceImpl.class);

    private static final String PROJECT_NAME = "project name"; //$NON-NLS-1$

    @Autowired
    private ProjectDetailDAO _projectDetailDAO;

    @Autowired
    private ProjectLanguageDetailDAO _projectLanguageDetailDAO;

    @Autowired
    private ProjectUserDetailDAO _projectUserDetailDAO;

    @Value("${synonym.number:5}")
    private int _synonymNumber;

    @Autowired
    private AdminTasksHolderHelper _tasksHolderHelper;

    @Autowired
    private TermEntryService _termEntryService;

    @Transactional(readOnly = true)
    @Override
    public ProjectDetail findByProjectId(Long projectId, final Class<?>... classesToFetch) {
	return getProjectDetailDAO().findByProjectId(projectId, classesToFetch);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ProjectReport> getAllProjectsReport(boolean groupByLanguages) {
	TmUserProfile user = TmUserProfile.getCurrentUserProfile();
	boolean powerUser = user.isPowerUser();
	Map<Long, Set<String>> projectUserLanguages = user.getProjectUserLanguages();
	Set<Long> projectIds = projectUserLanguages.keySet();

	Set<String> languageIds = new HashSet<>();
	for (Entry<Long, Set<String>> entry : projectUserLanguages.entrySet()) {
	    languageIds.addAll(entry.getValue());
	}

	if (LOG.isDebugEnabled()) {
	    String groupBy = groupByLanguages ? LANGUAGE : PROJECT_NAME;
	    LOG.debug(String.format(Messages.getString("ProjectDetailServiceImpl.2"), //$NON-NLS-1$
		    TmUserProfile.getCurrentUserName(), groupBy));
	}

	return getProjectDetailDAO().getAllProjectsReport(groupByLanguages, powerUser, projectIds, languageIds);
    }

    @Transactional
    @Override
    public void incrementUpdateProjectDetail(ProjectDetailInfo detailInfo) {
	if (isNull(detailInfo)) {
	    return;
	}

	Long projectId = detailInfo.getProjectId();
	Set<String> languageIds = detailInfo.getUpdatedLanguages();

	LogHelper.debug(LOG, String.format(Messages.getString("updating.project.details"), projectId, languageIds));

	getProjectDetailDAO().incrementalUpdateProjectDetail(detailInfo);

	Date dateModified = detailInfo.getDateModified();

	if (CollectionUtils.isNotEmpty(languageIds)) {
	    languageIds.forEach(l -> getProjectLanguageDetailDAO().incrementalUpdateProjectLanguageDetail(l, detailInfo,
		    dateModified));
	}

	Set<Long> userIds = detailInfo.getUpdatedUserIds();
	if (CollectionUtils.isNotEmpty(userIds)) {
	    userIds.forEach(u -> getProjectUserDetailDAO().incrementalUpdateProjectDetail(u, detailInfo, dateModified));
	}
    }

    @Transactional(readOnly = true)
    @Override
    public TaskPagedList<ProjectDetailView> search(UserProjectSearchRequest command, PagedListInfo pagedListInfo) {

	PagedList<ProjectDetailView> pagedList = createEntityPagedList(command, pagedListInfo);
	TaskPagedList<ProjectDetailView> taskPagedList = new TaskPagedList<>(pagedList);

	List<Task> tasks = ServiceUtils.postProcessEntityPagedList(taskPagedList, command.getFolder(), null,
		new EntityType[] { EntityTypeHolder.PROJECTDETAIL }, getTasksHolderHelper());

	taskPagedList.setTasks(tasks.toArray(new Task[tasks.size()]));

	return taskPagedList;
    }

    @Override
    @Transactional
    public void updateDateModifiedByProjectId(Long projectId, Date dateModified) {
	getProjectDetailDAO().updateDateModifiedByProjectId(projectId, dateModified);

	LogHelper.debug(LOG, String.format(Messages.getString("ProjectDetailServiceImpl.5"), //$NON-NLS-1$
		dateModified, projectId));
    }

    @Override
    @Transactional
    public void updateProjectAndLanguagesDateModified(Long projectId, Set<String> languages, Date newDateModified) {
	getProjectDetailDAO().updateProjectAndLanguagesDateModifiedByProjectId(projectId, languages, newDateModified);

	LogHelper.debug(LOG, String.format(Messages.getString("ProjectDetailServiceImpl.0"), //$NON-NLS-1$
		newDateModified, projectId, languages));
    }

    @Transactional
    @Override
    public void updateProjectDetailOnImport(Long projectId, Map<String, Long> languageDateModified, Long dateModified) {
	Set<String> languageIds = languageDateModified.keySet();
	if (CollectionUtils.isEmpty(languageIds)) {
	    LogHelper.info(LOG, Messages.getString("unable.to.update.project.details.on.import"));
	    return;
	}

	LogHelper.debug(LOG, String.format(Messages.getString("updating.project.details"), projectId, languageIds));

	TmgrSearchFilter filter = createSearchFilter(singletonList(projectId), languageIds, null);

	StopWatch watch = new StopWatch("Project detail update");
	watch.start("getNumberOfTermEntries");

	long numberOfTermEntries = getTermEntryService().getNumberOfTermEntries(filter).get(projectId);

	watch.stop();
	String prettyPrint = watch.prettyPrint();

	LogHelper.info(LOG, prettyPrint);

	watch.start("searchFacetTermCounts");

	FacetTermCounts facetTermCounts = getTermEntryService().searchFacetTermCounts(filter);

	watch.stop();
	LogHelper.info(LOG, prettyPrint);

	watch.start("incrementalUpdateProjectDetail");

	ProjectDetailInfo info = createProjectDetailInfo(projectId, numberOfTermEntries, facetTermCounts);
	updateProjectDetailOnImport(info, languageDateModified, dateModified);

	watch.stop();
	LogHelper.info(LOG, prettyPrint);
    }

    private PagedList<ProjectDetailView> createEntityPagedList(UserProjectSearchRequest command,
	    PagedListInfo pagedListInfo) {

	List<ProjectDetail> projectDetails = getProjectDetailDAO().searchProjectDetails(command, pagedListInfo);

	List<ProjectDetailView> projectDetailViews = createProjectDetailViews(projectDetails, command, pagedListInfo);

	PagedList<ProjectDetailView> pagedList = new PagedList<>();

	pagedList.setTotalCount((long) projectDetails.size());

	pagedList.setPagedListInfo(pagedListInfo);

	pagedList.setElements(projectDetailViews.toArray(new ProjectDetailView[projectDetailViews.size()]));

	return pagedList;
    }

    private ProjectDetailInfo createProjectDetailInfo(Long projectId, long numberOfTermEntries,
	    FacetTermCounts facetTermCounts) {
	final ProjectDetailInfo info = new ProjectDetailInfo(projectId);
	info.addTermEntryCount(numberOfTermEntries);
	if (Objects.isNull(facetTermCounts)) {
	    return info;
	}

	Map<String, FacetTermCounts.LanguageTermCount> termCountByLanguage = facetTermCounts.getTermCountByLanguage();
	for (Entry<String, FacetTermCounts.LanguageTermCount> entry : termCountByLanguage.entrySet()) {
	    String languageId = entry.getKey();
	    FacetTermCounts.LanguageTermCount languageTermCount = entry.getValue();

	    info.addTermCount(languageId, languageTermCount.getTermCount());
	    info.addApprovedTermCount(languageId, languageTermCount.getApproved());
	    info.addOnHoldTermCount(languageId, languageTermCount.getOnHold());
	    info.addPendingTermCount(languageId, languageTermCount.getPending());
	    info.addForbiddenTermCount(languageId, languageTermCount.getForbidden());
	    info.addTermInSubmissionCount(languageId,
		    languageTermCount.getInTranslationReview() + languageTermCount.getInFinalReview());
	}

	return info;
    }

    private ProjectDetailView createProjectDetailView(Map<Long, Long> termEntriesCount, TmUserProfile user,
	    Map<Long, Set<String>> userLanguages, ProjectDetail projectDetail) {

	ProjectDetailView projectView = new ProjectDetailView();

	TmProject project = projectDetail.getProject();

	Long projectId = project.getProjectId();
	projectView.setProjectId(projectId);
	Long projectDetailId = projectDetail.getProjectDetailId();
	projectView.setProjectDetailViewId(projectDetailId);
	projectView.setAvailableDescription(BooleanUtils.toBoolean(project.getAvailableDescription()));

	projectView.setTermEntryCount(termEntriesCount.get(projectId));

	Set<ProjectLanguageDetail> languageDetails = projectDetail.getLanguageDetails();
	Long userProfileId = user.getUserProfileId();
	ProjectUserDetail userDetail = projectDetail.getProjectUserDetail(userProfileId);
	Set<String> languages = userLanguages.get(projectId);

	populateTermCount(projectView, languageDetails, languages);

	projectView.setLanguageCount(languages.size());

	ProjectInfo projectInfo = project.getProjectInfo();

	projectView.setName(projectInfo.getName());
	projectView.setShortCode(projectInfo.getShortCode());

	if (user.isPowerUser()) {
	    projectView.setActiveSubmissionCount(projectDetail.getActiveSubmissionCount());
	    projectView.setCompletedSubmissionCount((projectDetail.getCompletedSubmissionCount()));
	} else if (nonNull(userDetail)) {
	    projectView.setActiveSubmissionCount(userDetail.getActiveSubmissionCount());
	    projectView.setCompletedSubmissionCount((userDetail.getCompletedSubmissionCount()));
	}

	projectView.setDateModified(projectDetail.getDateModified().getTime());

	return projectView;
    }

    private List<ProjectDetailView> createProjectDetailViews(List<ProjectDetail> projectDetails,
	    UserProjectSearchRequest command, PagedListInfo pagedListInfo) {
	if (CollectionUtils.isEmpty(projectDetails)) {
	    return new ArrayList<>();
	}

	List<ProjectDetail> page = getPage(projectDetails, pagedListInfo);

	List<Long> projectIds = page.stream().map(detail -> detail.getProject().getProjectId())
		.collect(Collectors.toList());

	TmgrSearchFilter searchFilter = createSearchFilter(projectIds, command.getLanguageIds(), pagedListInfo);

	TmUserProfile user = command.getUser();

	Map<Long, Set<String>> userLanguages = retainPageLanguages(user, projectIds);

	List<ProjectDetailView> result = new ArrayList<>(page.size());

	Map<Long, Long> termEntriesCount = getTermEntryService().getNumberOfTermEntries(searchFilter);

	for (ProjectDetail projectDetail : page) {
	    result.add(createProjectDetailView(termEntriesCount, user, userLanguages, projectDetail));
	}
	return result;
    }

    private TmgrSearchFilter createSearchFilter(List<Long> projectIds, Set<String> languageIds,
	    PagedListInfo pagedListInfo) {
	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setTargetLanguages(new ArrayList<>(languageIds));
	filter.setProjectIds(projectIds);
	filter.addLanguageResultField(true, getSynonymNumber(), languageIds.toArray(new String[languageIds.size()]));

	TmgrPageRequest pageRequest;
	if (Objects.nonNull(pagedListInfo)) {
	    pageRequest = new TmgrPageRequest(pagedListInfo.getIndex(), pagedListInfo.getSize(), null);
	} else {
	    pageRequest = new TmgrPageRequest(AbstractPageRequest.DEFAULT_PAGE, 1, null);
	}
	filter.setPageable(pageRequest);

	return filter;
    }

    private ProjectDetailDAO getProjectDetailDAO() {
	return _projectDetailDAO;
    }

    private ProjectLanguageDetailDAO getProjectLanguageDetailDAO() {
	return _projectLanguageDetailDAO;
    }

    private ProjectUserDetailDAO getProjectUserDetailDAO() {
	return _projectUserDetailDAO;
    }

    private int getSynonymNumber() {
	return _synonymNumber;
    }

    private AdminTasksHolderHelper getTasksHolderHelper() {
	return _tasksHolderHelper;
    }

    private TermEntryService getTermEntryService() {
	return _termEntryService;
    }

    private void populateTermCount(ProjectDetailView projectView, Set<ProjectLanguageDetail> languageDetails,
	    Set<String> userLangIds) {

	long termCount = 0, approved = 0, forbidden = 0, inSubmission = 0, onHold = 0, pending = 0;

	for (ProjectLanguageDetail languageDetail : languageDetails) {
	    if (languageDetail.isDisabled()) {
		continue;
	    }
	    String languageId = languageDetail.getLanguageId();
	    if (!userLangIds.contains(languageId)) {
		continue;
	    }
	    termCount += languageDetail.getTermCount();
	    approved += languageDetail.getApprovedTermCount();
	    forbidden += languageDetail.getForbiddenTermCount();
	    inSubmission += languageDetail.getTermInSubmissionCount();
	    pending += languageDetail.getPendingApprovalCount();
	    onHold += languageDetail.getOnHoldTermCount();
	}

	projectView.setTermCount(termCount);
	projectView.setApprovedTermCount(approved);
	projectView.setForbiddenTermCount(forbidden);
	projectView.setTermInSubmissionCount(inSubmission);
	projectView.setOnHoldTermCount(onHold);
	projectView.setPendingApprovalTermCount(pending);
    }

    private Map<Long, Set<String>> retainPageLanguages(TmUserProfile user, List<Long> projectIds) {

	Map<Long, Set<String>> userLanguages = new HashMap<>(user.getProjectUserLanguages());

	userLanguages.keySet().retainAll(projectIds);

	return userLanguages;
    }

    private void updateProjectDetailOnImport(ProjectDetailInfo detailInfo, Map<String, Long> languageDateModified,
	    Long dateModified) {
	if (isNull(detailInfo)) {
	    return;
	}

	detailInfo.setDateModified(new Date(dateModified));

	getProjectDetailDAO().updateProjectDetail(detailInfo);

	languageDateModified.forEach(
		(k, v) -> getProjectLanguageDetailDAO().updateProjectLanguageDetail(k, detailInfo, new Date(v)));
    }
}
