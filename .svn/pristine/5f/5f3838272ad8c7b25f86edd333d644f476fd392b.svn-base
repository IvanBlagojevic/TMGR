package org.gs4tr.termmanager.model;

public class TermTooltip {

    private String _submissionName;

    private String _submitterName;

    public TermTooltip(String submissionName, String submitterName) {
	_submissionName = submissionName;
	_submitterName = submitterName;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	TermTooltip other = (TermTooltip) obj;
	if (_submissionName == null) {
	    if (other._submissionName != null)
		return false;
	} else if (!_submissionName.equals(other._submissionName))
	    return false;
	if (_submitterName == null) {
	    return other._submitterName == null;
	} else
	    return _submitterName.equals(other._submitterName);
    }

    public String getSubmissionName() {
	return _submissionName;
    }

    public String getSubmitterName() {
	return _submitterName;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((_submissionName == null) ? 0 : _submissionName.hashCode());
	return prime * result + ((_submitterName == null) ? 0 : _submitterName.hashCode());
    }

    public void setSubmissionName(String submissionName) {
	_submissionName = submissionName;
    }

    public void setSubmitterName(String submitterName) {
	_submitterName = submitterName;
    }
}
