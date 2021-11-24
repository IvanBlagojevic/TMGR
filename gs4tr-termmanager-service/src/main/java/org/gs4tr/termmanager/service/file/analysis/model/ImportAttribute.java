package org.gs4tr.termmanager.service.file.analysis.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ImportAttribute implements Serializable {

    private static final long serialVersionUID = -6413943742227677647L;
    private final Level _level;
    private final String _name;
    /**
     * Lists of all attributes and notes defined in the project along with its
     * <code>Level</code>.
     */
    private final List<ProjectAttribute> _projectAttributes;
    private Status _status;

    public ImportAttribute(String name, Level level) {
	_name = Objects.requireNonNull(name);
	_level = Objects.requireNonNull(level);
	_projectAttributes = new ArrayList<>();
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	ImportAttribute other = (ImportAttribute) obj;
	if (_level != other._level)
	    return false;
	if (_name == null) {
	    if (other._name != null)
		return false;
	} else if (!_name.equals(other._name))
	    return false;
	if (_projectAttributes == null) {
	    if (other._projectAttributes != null)
		return false;
	} else if (!_projectAttributes.equals(other._projectAttributes))
	    return false;
	return _status == other._status;
    }

    public Level getLevel() {
	return _level;
    }

    public String getName() {
	return _name;
    }

    public List<ProjectAttribute> getProjectAttributes() {
	return _projectAttributes;
    }

    public Status getStatus() {
	return _status;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((_level == null) ? 0 : _level.hashCode());
	result = prime * result + ((_name == null) ? 0 : _name.hashCode());
	result = prime * result + ((_projectAttributes == null) ? 0 : _projectAttributes.hashCode());
	return prime * result + ((_status == null) ? 0 : _status.hashCode());
    }

    public void setStatus(Status status) {
	_status = status;
    }

    @Override
    public String toString() {
	return "ImportAttribute [_level=" + _level + ", _name=" + _name + ", _status=" + _status
		+ ", _projectAttributes=" + _projectAttributes + "]";
    }

    public enum Level
    {
	TERM_ATTRIBUTE, TERM_ENTRY, TERM_NOTE
    }
}
