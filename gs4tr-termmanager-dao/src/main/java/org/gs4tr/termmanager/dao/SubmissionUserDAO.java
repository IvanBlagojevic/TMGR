package org.gs4tr.termmanager.dao;

import java.util.List;

import org.gs4tr.foundation.modules.dao.GenericDao;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.SubmissionUser;

public interface SubmissionUserDAO extends GenericDao<SubmissionUser, Long> {

    Long findBySubmissionAndUserId(Long submissionId, Long userId);

    List<Submission> findSubmissionsByUserId(Long userId);
}
