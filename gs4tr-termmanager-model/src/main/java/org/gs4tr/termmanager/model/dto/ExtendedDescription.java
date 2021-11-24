package org.gs4tr.termmanager.model.dto;

public class ExtendedDescription extends Description {

    private boolean _commited = true;

    private String _markerId;

    public String getMarkerId() {
	return _markerId;
    }

    public boolean isCommited() {
	return _commited;
    }

    public void setCommited(boolean commited) {
	_commited = commited;
    }

    public void setMarkerId(String markerId) {
	_markerId = markerId;
    }
}
