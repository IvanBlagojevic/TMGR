package org.gs4tr.termmanager.dao;

import java.util.List;

import org.gs4tr.foundation.modules.dao.GenericDao;
import org.gs4tr.termmanager.model.SubmissionLanguage;

public interface SubmissionLanguageDAO extends GenericDao<SubmissionLanguage, Long> {

    List<SubmissionLanguage> findSubmissionLanguagesBySubmissionId(Long submissionId);

    int updateLanguageByProjectId(String languageFrom, String languageTo, Long projectId);

}
