package org.gs4tr.termmanager.model.glossary;

public class Comment {

    private String _text;

    private String _user;

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Comment other = (Comment) obj;
	if (_text == null) {
	    if (other._text != null)
		return false;
	} else if (!_text.equals(other._text))
	    return false;
	if (_user == null) {
	    return other._user == null;
	} else
	    return _user.equals(other._user);
    }

    public String getText() {
	return _text;
    }

    public String getUser() {
	return _user;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((_text == null) ? 0 : _text.hashCode());
	return prime * result + ((_user == null) ? 0 : _user.hashCode());
    }

    public void setText(String text) {
	_text = text;
    }

    public void setUser(String user) {
	_user = user;
    }
}
