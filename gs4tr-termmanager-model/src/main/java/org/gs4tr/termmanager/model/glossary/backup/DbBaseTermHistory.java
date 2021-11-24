package org.gs4tr.termmanager.model.glossary.backup;

import java.io.Serializable;
import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class DbBaseTermHistory implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -3636609858682570123L;

    private Long _id;

    private byte[] _revision;

    private int _revisionId;

    private String _termEntryUuid;

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	DbBaseTermHistory other = (DbBaseTermHistory) obj;
	if (!Arrays.equals(_revision, other._revision))
	    return false;
	if (_revisionId != other._revisionId)
	    return false;
	if (_termEntryUuid == null) {
	    if (other._termEntryUuid != null)
		return false;
	} else if (!_termEntryUuid.equals(other._termEntryUuid))
	    return false;
	return true;
    }

    @Id
    @Column(name = "TERM_HISTORY_ID", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
	return _id;
    }

    @Lob
    @Column(name = "REVISION")
    public byte[] getRevision() {
	return _revision;
    }

    @Column(name = "REVISION_ID")
    public int getRevisionId() {
	return _revisionId;
    }

    @Column(name = "TERMENTRY_UUID")
    public String getTermEntryUuid() {
	return _termEntryUuid;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + Arrays.hashCode(_revision);
	result = prime * result + _revisionId;
	result = prime * result + ((_termEntryUuid == null) ? 0 : _termEntryUuid.hashCode());
	return result;
    }

    public void setId(Long id) {
	_id = id;
    }

    public void setRevision(byte[] revision) {
	_revision = revision;
    }

    public void setRevisionId(int revisionId) {
	_revisionId = revisionId;
    }

    public void setTermEntryUuid(String termEntryUuid) {
	_termEntryUuid = termEntryUuid;
    }

    @Override
    public String toString() {
	return "DbBaseTermHistory [_revision=" + Arrays.toString(_revision) + ", _revisionId=" + _revisionId
		+ ", _termEntryUuid=" + _termEntryUuid + "]";
    }
}
