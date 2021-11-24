package org.gs4tr.termmanager.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;

public interface SubmissionTermService {

    void addNewDescription(String type, String tempText, String baseType, String submissionTermId, Long projectId);

    List<String> approveSubmissionTerms(List<String> submissionTermIds, Long submissionId);

    Set<String> cancelTermTranslation(Long submissionId, List<String> submissionTermIds);

    Term findById(String submissionTermId, Long projectId);

    List<Term> findByIds(List<String> submissionTermIds, Long projectId);

    List<Term> findSubmissionTermsBySubmissionId(Long submissionId);

    TermEntry findTermEntryByTermId(String termId, Long projectId);

    Map<String, String> undoTermTranslation(List<String> submissionTermIds, Long projectId);

    void updateSubmissionSourceTerms(String sourceLanguageId, Set<String> termEntryIds, Long projectId);

    void updateTempDescriptionText(String termId, String descriptionId, String tempText, Long projectId);

    void updateTempTermText(String submissionTermId, String tempTermText, Long projectId);
}
