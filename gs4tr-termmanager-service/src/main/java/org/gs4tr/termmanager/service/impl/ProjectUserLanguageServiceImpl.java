package org.gs4tr.termmanager.service.impl;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.gs4tr.termmanager.dao.ProjectLanguageDetailDAO;
import org.gs4tr.termmanager.dao.ProjectLanguageUserDetailDAO;
import org.gs4tr.termmanager.dao.ProjectUserLanguageDAO;
import org.gs4tr.termmanager.dao.StatisticsDAO;
import org.gs4tr.termmanager.model.ProjectLanguageDetail;
import org.gs4tr.termmanager.model.ProjectLanguageUserDetail;
import org.gs4tr.termmanager.model.ProjectUserLanguage;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.service.ProjectUserLanguageService;
import org.gs4tr.termmanager.service.utils.MailHelper.ReportType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("projectUserLanguageService")
public class ProjectUserLanguageServiceImpl implements ProjectUserLanguageService {

    @Autowired
    private ProjectLanguageDetailDAO _projectLanguageDetailDAO;

    @Autowired
    private ProjectLanguageUserDetailDAO _projectLanguageUserDetailDAO;

    @Autowired
    private ProjectUserLanguageDAO _projectUserLanguageDAO;

    @Autowired
    private StatisticsDAO _statisticsDAO;

    @Override
    @Transactional
    public void cloneProjectUserLanguages(TmUserProfile user, TmProject project, String languageTo) {
	Long projectId = project.getProjectId();

	if (!user.isPowerUser()) {
	    ProjectUserLanguage clonedProjectUserLanguage = new ProjectUserLanguage(project, user, languageTo);
	    getProjectUserLanguageDAO().save(clonedProjectUserLanguage);

	    getStatisticsDAO().addOrUpdateProjectUserLangStatistics(clonedProjectUserLanguage,
		    ReportType.WEEKLY.getName(), ReportType.DAILY.getName());

	}

	ProjectLanguageDetail clonedPld = getProjectLanguageDetailDAO().findProjectLangDetailByLangId(projectId,
		languageTo);

	// Create new ProjectLanguageUserDetails
	if (Objects.nonNull(clonedPld)) {
	    Set<ProjectLanguageUserDetail> clonedUserDetails = new HashSet<>();

	    ProjectLanguageUserDetail clonedUserDetail = new ProjectLanguageUserDetail(clonedPld, user);

	    getProjectLanguageUserDetailDAO().save(clonedUserDetail);

	    clonedUserDetails.add(clonedUserDetail);

	    clonedPld.setUserDetails(clonedUserDetails);
	}
    }

    @Override
    @Transactional
    public void recodeProjectUserLanguage(Long projectId, String languageFrom, String languageTo) {
	getProjectUserLanguageDAO().recodeProjectUserLanguage(projectId, languageFrom, languageTo);
    }

    private ProjectLanguageDetailDAO getProjectLanguageDetailDAO() {
	return _projectLanguageDetailDAO;
    }

    private ProjectLanguageUserDetailDAO getProjectLanguageUserDetailDAO() {
	return _projectLanguageUserDetailDAO;
    }

    private ProjectUserLanguageDAO getProjectUserLanguageDAO() {
	return _projectUserLanguageDAO;
    }

    private StatisticsDAO getStatisticsDAO() {
	return _statisticsDAO;
    }
}
