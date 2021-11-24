package org.gs4tr.termmanager.dao;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.gs4tr.foundation.modules.dao.GenericDao;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.TmProject;

public interface SubmissionDAO extends GenericDao<Submission, Long> {

    Set<Long> findAllSubmissionIds();

    List<Submission> findAllSubmissions();

    List<TmProject> findProjectsBySubmissionIds(List<Long> submissionIds);

    Submission findSubmissionByIdFetchChilds(Long submissionId);

    List<Submission> findSubmissionsByIdsFetchChilds(List<Long> submissionIds);

    List<Submission> findSubmissionsByProjectId(Long projectId);

    List<Submission> findSubmissionsByProjectIds(Collection<Long> projectIds);

    int updateLanguageByProjectId(String languageFrom, String languageTo, Long projectId);
}