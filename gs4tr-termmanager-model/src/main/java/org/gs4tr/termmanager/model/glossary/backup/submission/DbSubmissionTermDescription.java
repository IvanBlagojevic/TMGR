package org.gs4tr.termmanager.model.glossary.backup.submission;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.gs4tr.termmanager.model.glossary.backup.DbBaseDescription;

@Entity
@Table(name = "SUBMISSION_TERM_DESCRIPTION")
public class DbSubmissionTermDescription extends DbBaseDescription implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 6806765309071927385L;

    private String _submissionTermUuid;

    private String _tempValue;

    public DbSubmissionTermDescription() {
	super();
    }

    public DbSubmissionTermDescription(String type, byte[] value) {
	super(type, value);
    }

    public DbSubmissionTermDescription(String baseType, String type, byte[] value) {
	super(baseType, type, value);
    }

    @Column(name = "TERM_UUID", nullable = false, length = 128)
    public String getSubmissionTermUuid() {
	return _submissionTermUuid;
    }

    @Column(name = "TEMP_VALUE", nullable = true, updatable = true)
    @Lob
    public String getTempValue() {
	return _tempValue;
    }

    public void setSubmissionTermUuid(String submissionTermUuid) {
	_submissionTermUuid = submissionTermUuid;
    }

    public void setTempValue(String tempValue) {
	_tempValue = tempValue;
    }

    @Override
    public String toString() {
	return "DbSubmissionTermDescription [_submissionTermUuid=" + _submissionTermUuid + ", _tempValue=" + _tempValue
		+ ", toString()=" + super.toString() + "]";
    }
}
