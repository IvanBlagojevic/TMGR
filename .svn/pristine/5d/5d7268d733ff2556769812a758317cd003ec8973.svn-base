package org.gs4tr.termmanager.webservice.model.response;

import java.util.Collection;
import java.util.List;

import org.gs4tr.termmanager.model.dto.Description;
import org.gs4tr.termmanager.model.dto.LanguageModel;

import io.swagger.annotations.ApiModelProperty;

public class TermEntryHit {

    private Collection<Description> _entryDescriptions;

    private LanguageModel _sources;

    private List<LanguageModel> _targets;

    private String _termEntryId;

    @ApiModelProperty(value = "Collection of term entry level descriptions and it could be only \"ATTRIBUTE\" but not a \"NOTE\".")
    public Collection<Description> getEntryDescriptions() {
	return _entryDescriptions;
    }

    @ApiModelProperty(value = "Matched source terms in term entry.")
    public LanguageModel getSources() {
	return _sources;
    }

    @ApiModelProperty(value = "Collection of target translations grouped by language in a term entry.")
    public List<LanguageModel> getTargets() {
	return _targets;
    }

    @ApiModelProperty(value = "Term entry id.")
    public String getTermEntryId() {
	return _termEntryId;
    }

    public void setEntryDescriptions(Collection<Description> entryDescriptions) {
	_entryDescriptions = entryDescriptions;
    }

    public void setSources(LanguageModel sources) {
	_sources = sources;
    }

    public void setTargets(List<LanguageModel> targets) {
	_targets = targets;
    }

    public void setTermEntryId(String termEntryId) {
	_termEntryId = termEntryId;
    }
}
