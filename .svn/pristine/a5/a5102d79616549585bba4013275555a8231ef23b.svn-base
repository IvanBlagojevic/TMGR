package org.gs4tr.termmanager.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "SUBMISSION_LANGUAGE_COMMENT")
public class SubmissionLanguageComment extends Comment {

    private static final long serialVersionUID = 894084226146702532L;

    private SubmissionLanguage _submissionLanguage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUBMISSION_LANGUAGE_ID", nullable = false, updatable = false)
    public SubmissionLanguage getSubmissionLanguage() {
	return _submissionLanguage;
    }

    public void setSubmissionLanguage(SubmissionLanguage submissionLanguage) {
	_submissionLanguage = submissionLanguage;
    }
}
