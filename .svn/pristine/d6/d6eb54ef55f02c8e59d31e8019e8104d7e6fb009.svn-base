package org.gs4tr.termmanager.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.gs4tr.foundation.modules.entities.model.Identifiable;

@MappedSuperclass
public class Comment implements Identifiable<Long> {

    private static final long serialVersionUID = 4020131629312964340L;

    private Long _commentId;

    private String _markerId;

    private String _text;

    private String _user;

    public Comment() {
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Comment other = (Comment) obj;
	if (_markerId == null) {
	    if (other._markerId != null)
		return false;
	} else if (!_markerId.equals(other._markerId))
	    return false;
	if (_text == null) {
	    return other._text == null;
	} else
	    return _text.equals(other._text);
    }

    @Id
    @Column(name = "COMMENT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getCommentId() {
	return _commentId;
    }

    @Override
    @Transient
    public Long getIdentifier() {
	return getCommentId();
    }

    @Column(name = "MARKER_ID", length = 40, nullable = false, updatable = false, unique = true)
    public String getMarkerId() {
	return _markerId;
    }

    @Column(name = "TEXT")
    @Lob
    public String getText() {
	return _text;
    }

    @Column(name = "USER", length = 40, nullable = false)
    public String getUser() {
	return _user;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((_markerId == null) ? 0 : _markerId.hashCode());
	return prime * result + ((_text == null) ? 0 : _text.hashCode());
    }

    public void setCommentId(Long commentId) {
	_commentId = commentId;
    }

    @Override
    public void setIdentifier(Long id) {
	setCommentId(id);
    }

    public void setMarkerId(String markerId) {
	_markerId = markerId;
    }

    public void setText(String text) {
	_text = text;
    }

    public void setUser(String user) {
	_user = user;
    }
}
