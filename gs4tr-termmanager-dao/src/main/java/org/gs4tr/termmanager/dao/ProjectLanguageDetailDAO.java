package org.gs4tr.termmanager.dao;

import java.util.Date;
import java.util.List;

import org.gs4tr.foundation.modules.dao.GenericDao;
import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.termmanager.model.ProjectLanguageDetail;
import org.gs4tr.termmanager.model.ProjectLanguageDetailInfoIO;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.gs4tr.termmanager.model.search.command.ProjectLanguageDetailRequest;
import org.gs4tr.termmanager.model.view.ProjectLanguageDetailView;

public interface ProjectLanguageDetailDAO extends GenericDao<ProjectLanguageDetail, Long> {

    ProjectLanguageDetail findProjectLangDetailByLangId(Long projectId, String languageId);

    PagedList<ProjectLanguageDetailView> getEntityPagedList(ProjectLanguageDetailRequest command,
	    PagedListInfo pagedListInfo);

    List<ProjectLanguageDetail> getProjectLanguageDetailsByProjectId(Long projectId);

    boolean incrementalUpdateProjectLanguageDetail(String languageId, ProjectDetailInfo info, Date dateModified);

    boolean incrementalUpdateProjectLanguageDetail(ProjectLanguageDetailInfoIO projectLanguageInfo);

    boolean recodeProjectLanguageDetail(Long projectId, String languageFrom, String languageTo);

    boolean updateProjectLanguageDetail(String languageId, ProjectDetailInfo info, Date dateModified);

    boolean updateProjectLanguageDetail(ProjectLanguageDetailInfoIO detailInfo);
}
