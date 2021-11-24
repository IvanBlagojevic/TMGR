package org.gs4tr.termmanager.model.dto;

public class DtoComment {

    private Long _commentId;

    private String _commentTicket;

    private String _markerId;

    private String _text;

    private String _user;

    public Long getCommentId() {
	return _commentId;
    }

    public String getCommentTicket() {
	return _commentTicket;
    }

    public String getMarkerId() {
	return _markerId;
    }

    public String getText() {
	return _text;
    }

    public String getUser() {
	return _user;
    }

    public void setCommentId(Long commentId) {
	_commentId = commentId;
    }

    public void setCommentTicket(String commentTicket) {
	_commentTicket = commentTicket;
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
