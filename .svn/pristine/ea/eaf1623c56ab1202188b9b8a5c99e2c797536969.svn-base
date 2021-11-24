package org.gs4tr.termmanager.model.glossary.backup;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class DbBaseTermEntryHistory implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 8876776101309281659L;

    private Date _dateModified;

    private byte[] _descriptions;

    private String _historyAction;

    private Long _id;

    private Integer _revisionId;

    private String _termEntryUUid;

    private byte[] _termUUIDs;

    private String _userModified;

    @Column(name = "DATE_MODIFIED", nullable = false, length = 128)
    public Date getDateModified() {
	return _dateModified;
    }

    @Lob
    @Column(name = "TERMENTRY_DESCRIPTIONS")
    public byte[] getDescriptions() {
	return _descriptions;
    }

    @Column(name = "HISTORY_ACTION", nullable = false, length = 128)
    public String getHistoryAction() {
	return _historyAction;
    }

    @Id
    @Column(name = "TERMENTRY_HISTORY_ID", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
	return _id;
    }

    @Column(name = "REVISION_ID", length = 10, nullable = false, updatable = false)
    public int getRevisionId() {
	return _revisionId;
    }

    @Column(name = "TERMENTRY_UUID", nullable = false, length = 128)
    public String getTermEntryUUid() {
	return _termEntryUUid;
    }

    @Lob
    @Column(name = "TERM_UIDS")
    public byte[] getTermUUIDs() {
	return _termUUIDs;
    }

    @Column(name = "USER_MODIFIED", nullable = false, length = 128)
    public String getUserModified() {
	return _userModified;
    }

    public void setDateModified(Date dateModified) {
	_dateModified = dateModified;
    }

    public void setDescriptions(byte[] descriptions) {
	_descriptions = descriptions;
    }

    public void setHistoryAction(String historyAction) {
	_historyAction = historyAction;
    }

    public void setId(Long id) {
	_id = id;
    }

    public void setRevisionId(int revisionId) {
	_revisionId = revisionId;
    }

    public void setTermEntryUUid(String termEntryUUid) {
	_termEntryUUid = termEntryUUid;
    }

    public void setTermUUIDs(byte[] termUUIDs) {
	_termUUIDs = termUUIDs;
    }

    public void setUserModified(String userModified) {
	_userModified = userModified;
    }

    @Override
    public String toString() {
	return "DbBaseTermEntryHistory [_dateModified=" + _dateModified + ", _historyAction=" + _historyAction
		+ ", _id=" + _id + ", _termEntryUUid=" + _termEntryUUid + ", _userModified=" + _userModified + "]";
    }

}
