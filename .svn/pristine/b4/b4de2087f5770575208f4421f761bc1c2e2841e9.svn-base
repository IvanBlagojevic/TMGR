package org.gs4tr.termmanager.model.glossary.backup.submission;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.gs4tr.termmanager.model.glossary.backup.DbBaseDescription;

@Entity
@Table(name = "SUBMISSION_TERMENTRY_DESCRIPTION")
public class DbSubmissionTermEntryDescription extends DbBaseDescription implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 6806765309071927385L;

    private String _submissionTermEntryUuid;

    private String _tempValue;

    public DbSubmissionTermEntryDescription() {
	super();
    }

    public DbSubmissionTermEntryDescription(String type, byte[] value) {
	super(type, value);
    }

    public DbSubmissionTermEntryDescription(String baseType, String type, byte[] value) {
	super(baseType, type, value);
    }

    @Column(name = "TERMENTRY_UUID", nullable = false, length = 128)
    public String getSubmissionTermEntryUuid() {
	return _submissionTermEntryUuid;
    }

    @Column(name = "TEMP_VALUE", nullable = true, updatable = true)
    @Lob
    public String getTempValue() {
	return _tempValue;
    }

    public void setSubmissionTermEntryUuid(String submissionTermEntryUuid) {
	_submissionTermEntryUuid = submissionTermEntryUuid;
    }

    public void setTempValue(String tempValue) {
	_tempValue = tempValue;
    }

    @Override
    public String toString() {
	return "DbSubmissionTermEntryDescription [_submissionTermEntryUuid=" + _submissionTermEntryUuid
		+ ", _tempValue=" + _tempValue + ", toString()=" + super.toString() + "]";
    }
}
