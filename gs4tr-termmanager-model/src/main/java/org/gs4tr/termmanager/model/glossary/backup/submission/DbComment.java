package org.gs4tr.termmanager.model.glossary.backup.submission;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "COMMENT")
public class DbComment implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -3792134538182880112L;

    private Long _id;

    private String _submissionTermUuid;

    private String _text;

    private String _user;

    private String _uuid;

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	DbComment other = (DbComment) obj;
	if (_text == null) {
	    if (other._text != null)
		return false;
	} else if (!_text.equals(other._text))
	    return false;
	if (_user == null) {
	    if (other._user != null)
		return false;
	} else if (!_user.equals(other._user))
	    return false;
	return true;
    }

    @Id
    @Column(name = "COMMENT_ID", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
	return _id;
    }

    @Column(name = "TERM_UUID", nullable = false, length = 128)
    public String getSubmissionTermUuid() {
	return _submissionTermUuid;
    }

    @Column(name = "TEXT", nullable = false)
    @Lob
    public String getText() {
	return _text;
    }

    @Column(name = "USER", nullable = false, length = 128)
    public String getUser() {
	return _user;
    }

    @Column(name = "UUID", nullable = false, length = 128)
    public String getUuid() {
	return _uuid;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((_text == null) ? 0 : _text.hashCode());
	result = prime * result + ((_user == null) ? 0 : _user.hashCode());
	return result;
    }

    public void setId(Long id) {
	_id = id;
    }

    public void setSubmissionTermUuid(String submissionTermUuid) {
	_submissionTermUuid = submissionTermUuid;
    }

    public void setText(String text) {
	_text = text;
    }

    public void setUser(String user) {
	_user = user;
    }

    public void setUuid(String uuid) {
	_uuid = uuid;
    }

    @Override
    public String toString() {
	return "DbComment [_submissionTermUuid=" + _submissionTermUuid + ", _text=" + _text + ", _user=" + _user
		+ ", _uuid=" + _uuid + "]";
    }
}
