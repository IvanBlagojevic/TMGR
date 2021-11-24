package org.gs4tr.termmanager.model.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("term")
public class Term {

    @XStreamAlias("blacklisted")
    private Boolean _blacklisted;

    @XStreamOmitField
    private String _markerId;

    @XStreamAlias("name")
    private String _name;

    @XStreamAlias("termDescriptions")
    private Description[] _termDescriptions;

    @XStreamAlias("termNotes")
    private TermNote[] _termNotes;

    @XStreamAlias("ticket")
    private String _ticket;

    public Boolean getBlacklisted() {
	return _blacklisted;
    }

    public String getMarkerId() {
	return _markerId;
    }

    public String getName() {
	return _name;
    }

    public Description[] getTermDescriptions() {
	return _termDescriptions;
    }

    public TermNote[] getTermNotes() {
	return _termNotes;
    }

    public String getTicket() {
	return _ticket;
    }

    public void setBlacklisted(Boolean blacklisted) {
	_blacklisted = blacklisted;
    }

    public void setMarkerId(String markerId) {
	_markerId = markerId;
    }

    public void setName(String name) {
	_name = name;
    }

    public void setTermDescriptions(Description[] descriptions) {
	_termDescriptions = descriptions;
    }

    public void setTermNotes(TermNote[] termNotes) {
	_termNotes = termNotes;
    }

    public void setTicket(String ticket) {
	_ticket = ticket;
    }
}