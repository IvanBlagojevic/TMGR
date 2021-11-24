package org.gs4tr.termmanager.model;

import java.util.Set;

import org.gs4tr.termmanager.model.glossary.Term;

public class TermHolder {
    private LanguageAlignmentEnum _alignment;

    private boolean _isSource;

    private String _languageId;

    private Long _submissionId;

    private String _submissionName;

    private String _submitter;

    private Set<Term> _terms;

    private Set<TermTooltip> _termTooltip;

    public LanguageAlignmentEnum getAlignment() {
	return _alignment;
    }

    public String getLanguageId() {
	return _languageId;
    }

    public Long getSubmissionId() {
	return _submissionId;
    }

    public String getSubmissionName() {
	return _submissionName;
    }

    public String getSubmitter() {
	return _submitter;
    }

    public Set<Term> getTerms() {
	return _terms;
    }

    public Set<TermTooltip> getTermTooltip() {
	return _termTooltip;
    }

    public boolean isSource() {
	return _isSource;
    }

    public void setAlignment(LanguageAlignmentEnum alignment) {
	_alignment = alignment;
    }

    public void setLanguageId(String languageId) {
	_languageId = languageId;
    }

    public void setSource(boolean isSource) {
	_isSource = isSource;
    }

    public void setSubmissionId(Long submissionId) {
	_submissionId = submissionId;
    }

    public void setSubmissionName(String submissionName) {
	_submissionName = submissionName;
    }

    public void setSubmitter(String submitter) {
	_submitter = submitter;
    }

    public void setTerms(Set<Term> terms) {
	_terms = terms;
    }

    public void setTermTooltip(Set<TermTooltip> termTooltip) {
	_termTooltip = termTooltip;
    }

}
