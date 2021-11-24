package org.gs4tr.termmanager.webservice.model.request;

import java.util.Collection;

import org.gs4tr.termmanager.model.dto.Description;
import org.gs4tr.termmanager.model.dto.TermV2ModelExtended;

import io.swagger.annotations.ApiModelProperty;

public class AddTermsCommand extends BaseCommand {

    private String _projectTicket;

    private Collection<Description> _termEntryDescriptions;

    private String _termEntryId;

    private Collection<TermV2ModelExtended> _terms;

    @ApiModelProperty(value = "Project unique identifier.", required = true)
    public String getProjectTicket() {
	return _projectTicket;
    }

    @ApiModelProperty(value = "Collection of term entry attributes.")
    public Collection<Description> getTermEntryDescriptions() {
	return _termEntryDescriptions;
    }

    @ApiModelProperty(value = "Term entry's unique identifier.")
    public String getTermEntryId() {
	return _termEntryId;
    }

    @ApiModelProperty(value = "Collection of term entry terms.", required = true)
    public Collection<TermV2ModelExtended> getTerms() {
	return _terms;
    }

    public void setProjectTicket(String projectTicket) {
	_projectTicket = projectTicket;
    }

    public void setTermEntryDescriptions(Collection<Description> termEntryDescriptions) {
	_termEntryDescriptions = termEntryDescriptions;
    }

    public void setTermEntryId(String termEntryId) {
	_termEntryId = termEntryId;
    }

    public void setTerms(Collection<TermV2ModelExtended> terms) {
	_terms = terms;
    }

    @Override
    public String toString() {
	return "AddTermsCommand [_projectTicket=" + _projectTicket + ", _termEntryDescriptions="
		+ _termEntryDescriptions + ", _terms=" + _terms + "]";
    }
}
