package org.gs4tr.termmanager.model.dto;

import java.util.Set;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("termEntry")
public class TermEntry {

    @XStreamAlias("attributes")
    private Attribute[] _attributes;

    @XStreamAlias("termEntryDescriptions")
    private Description[] _termEntryDescriptions;

    @XStreamAlias("ticket")
    private String _ticket;

    @XStreamAlias("forbidden")
    private Boolean _forbidden;

    private Set<Term> _terms;

    public Attribute[] getAttributes() {
	return _attributes;
    }

    public Description[] getDescriptions() {
	return _termEntryDescriptions;
    }

    public Set<Term> getTerms() {
	return _terms;
    }

    public String getTicket() {
	return _ticket;
    }

    public Boolean isForbidden() {
	return _forbidden;
    }

    public void setAttributes(Attribute[] attributes) {
	_attributes = attributes;
    }

    public void setForbidden(Boolean forbidden) {
	_forbidden = forbidden;
    }

    public void setDescriptions(Description[] descriptions) {
	_termEntryDescriptions = descriptions;
    }

    public void setTerms(Set<Term> terms) {
	_terms = terms;
    }

    public void setTicket(String ticket) {
	_ticket = ticket;
    }

}