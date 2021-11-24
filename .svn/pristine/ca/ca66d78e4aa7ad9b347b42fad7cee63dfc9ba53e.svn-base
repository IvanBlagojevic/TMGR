package org.gs4tr.termmanager.model.glossary.backup;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@MappedSuperclass
public class DbBaseTerm implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4000833867424923882L;

    private Date _dateCreated;

    private Date _dateModified;

    private Boolean _first = Boolean.FALSE;

    private Boolean _forbidden = Boolean.FALSE;

    private Long _id;

    private Boolean _inTranslationAsSource = Boolean.FALSE;

    private String _languageId;

    private byte[] _name;

    private Long _projectId;

    private String _status;

    private String _statusOld;

    private String _termEntryUuid;

    private String _userCreated;

    private String _userModified;

    private String _uuId;

    public DbBaseTerm() {

    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	DbBaseTerm other = (DbBaseTerm) obj;
	if (_forbidden == null) {
	    if (other._forbidden != null)
		return false;
	} else if (!_forbidden.equals(other._forbidden))
	    return false;
	if (_languageId == null) {
	    if (other._languageId != null)
		return false;
	} else if (!_languageId.equals(other._languageId))
	    return false;
	if (_name == null) {
	    if (other._name != null)
		return false;
	} else if (!Arrays.equals(_name, other._name))
	    return false;
	if (_projectId == null) {
	    return other._projectId == null;
	} else
	    return _projectId.equals(other._projectId);
    }

    @Column(name = "DATE_CREATED", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDateCreated() {
	return _dateCreated;
    }

    @Column(name = "DATE_MODIFIED", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDateModified() {
	return _dateModified;
    }

    @Column(name = "FIRST", nullable = false)
    public Boolean getFirst() {
	return _first;
    }

    @Column(name = "FORBIDDEN", nullable = false)
    public Boolean getForbidden() {
	return _forbidden;
    }

    @Id
    @Column(name = "TERM_ID", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
	return _id;
    }

    @Column(name = "IN_TRANSLATION_AS_SOURCE")
    public Boolean getInTranslationAsSource() {
	return _inTranslationAsSource;
    }

    @Column(name = "LANGUAGE_ID", length = 10, updatable = false, nullable = false)
    public String getLanguageId() {
	return _languageId;
    }

    @Column(name = "TERM_NAME")
    @Lob
    public byte[] getName() {
	return _name;
    }

    @Transient
    public String getNameAsString() {
	return _name != null ? new String(_name, StandardCharsets.UTF_8) : null;
    }

    @Column(name = "PROJECT_ID", length = 10, nullable = true, updatable = true)
    public Long getProjectId() {
	return _projectId;
    }

    @Column(name = "STATUS", nullable = false, length = 20)
    public String getStatus() {
	return _status;
    }

    @Column(name = "STATUS_OLD", nullable = true, length = 20)
    public String getStatusOld() {
	return _statusOld;
    }

    @Column(name = "TERMENTRY_UUID", nullable = false, length = 128)
    public String getTermEntryUuid() {
	return _termEntryUuid;
    }

    @Column(name = "USER_CREATED", nullable = true, length = 128)
    public String getUserCreated() {
	return _userCreated;
    }

    @Column(name = "USER_MODIFIED", nullable = true, length = 128)
    public String getUserModified() {
	return _userModified;
    }

    @Column(name = "UUID", nullable = false, length = 128)
    public String getUuId() {
	return _uuId;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((_forbidden == null) ? 0 : _forbidden.hashCode());
	result = prime * result + ((_languageId == null) ? 0 : _languageId.hashCode());
	result = prime * result + ((_name == null) ? 0 : Arrays.hashCode(_name));
	return prime * result + ((_projectId == null) ? 0 : _projectId.hashCode());
    }

    public void setDateCreated(Date dateCreated) {
	_dateCreated = dateCreated;
    }

    public void setDateModified(Date dateModified) {
	_dateModified = dateModified;
    }

    public void setFirst(Boolean first) {
	_first = first;
    }

    public void setForbidden(Boolean forbidden) {
	_forbidden = forbidden;
    }

    public void setId(Long id) {
	_id = id;
    }

    public void setInTranslationAsSource(Boolean inTranslationAsSource) {
	_inTranslationAsSource = inTranslationAsSource;
    }

    public void setLanguageId(String languageId) {
	_languageId = languageId;
    }

    public void setName(byte[] name) {
	_name = name;
    }

    @Transient
    public void setNameAsBytes(String name) {
	_name = name != null ? name.getBytes(StandardCharsets.UTF_8) : null;
    }

    public void setProjectId(Long projectId) {
	_projectId = projectId;
    }

    public void setStatus(String status) {
	_status = status;
    }

    public void setStatusOld(String statusOld) {
	_statusOld = statusOld;
    }

    public void setTermEntryUuid(String termEntryUuid) {
	_termEntryUuid = termEntryUuid;
    }

    public void setUserCreated(String userCreated) {
	_userCreated = userCreated;
    }

    public void setUserModified(String userModified) {
	_userModified = userModified;
    }

    public void setUuId(String uuId) {
	_uuId = uuId;
    }

    @Override
    public String toString() {
	return "DbBaseTerm [_dateCreated=" + _dateCreated + ", _dateModified=" + _dateModified + ", _first=" + _first
		+ ", _forbidden=" + _forbidden + ", _id=" + _id + ", _inTranslationAsSource=" + _inTranslationAsSource
		+ ", _languageId=" + _languageId + ", _name=" + getNameAsString() + ", _projectId=" + _projectId
		+ ", _status=" + _status + ", _statusOld=" + _statusOld + ", _termEntryUuid=" + _termEntryUuid
		+ ", _userCreated=" + _userCreated + ", _userModified=" + _userModified + ", _uuId=" + _uuId + "]";
    }
}
