package org.gs4tr.termmanager.service.file.analysis.model;

import java.io.Serializable;

import org.gs4tr.termmanager.service.file.analysis.model.ImportAttribute.Level;

public class ProjectAttribute implements Serializable {

    private static final long serialVersionUID = 6320972053936435210L;
    private final Level _level;
    private final String _name;

    public ProjectAttribute(String name, Level level) {
	_name = name;
	_level = level;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	ProjectAttribute other = (ProjectAttribute) obj;
	if (_level != other._level)
	    return false;
	if (_name == null) {
	    if (other._name != null)
		return false;
	} else if (!_name.equals(other._name))
	    return false;
	return true;
    }

    public Level getLevel() {
	return _level;
    }

    public String getName() {
	return _name;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((_level == null) ? 0 : _level.hashCode());
	result = prime * result + ((_name == null) ? 0 : _name.hashCode());
	return result;
    }

    @Override
    public String toString() {
	return "ProjectAttribute [_level=" + _level + ", _name=" + _name + "]";
    }
}
