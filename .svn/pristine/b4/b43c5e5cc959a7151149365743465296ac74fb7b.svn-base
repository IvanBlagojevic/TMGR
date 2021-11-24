package org.gs4tr.termmanager.dao;

import java.util.Date;
import java.util.List;

import org.gs4tr.foundation.modules.dao.GenericDao;
import org.gs4tr.termmanager.model.ProjectUserDetail;
import org.gs4tr.termmanager.model.ProjectUserDetailIO;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;

public interface ProjectUserDetailDAO extends GenericDao<ProjectUserDetail, Long> {

    List<ProjectUserDetail> findByProjectId(Long projectId);

    ProjectUserDetail findByUserAndProject(Long userId, Long projectId);

    List<ProjectUserDetail> findByUsersAndProject(List<Long> userIds, Long projectId);

    boolean incrementalUpdateProjectDetail(Long userId, ProjectDetailInfo info, Date dateModified);

    boolean incrementalUpdateProjectDetail(ProjectUserDetailIO info);
}
