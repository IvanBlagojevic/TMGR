package org.gs4tr.termmanager.webmvc.model.search;

import java.util.Set;

public class LanguageDirectionItem {

    private String _direction;

    private String _name;

    private Set<String> _projectTickets;

    private String _value;

    public LanguageDirectionItem() {
    }

    public String getDirection() {
	return _direction;
    }

    public String getName() {
	return _name;
    }

    public Set<String> getProjectTickets() {
	return _projectTickets;
    }

    public String getValue() {
	return _value;
    }

    public void setDirection(String direction) {
	_direction = direction;
    }

    public void setName(String name) {
	_name = name;
    }

    public void setProjectTickets(Set<String> projectTickets) {
	_projectTickets = projectTickets;
    }

    public void setValue(String value) {
	_value = value;
    }
}
