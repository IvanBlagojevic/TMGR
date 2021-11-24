package org.gs4tr.termmanager.webservice.model.request;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;

public class TermSearchCommand extends BaseCommand {

    private boolean _fuzzy;
    private int _maxNumFound;
    private String _projectTicket;
    private boolean _searchForbidden;
    private String _sourceLanguage;
    private List<String> _targetLanguages;

    @ApiModelProperty(value = "Maximum number of hits.")
    public int getMaxNumFound() {
	return _maxNumFound;
    }

    @ApiModelProperty(value = "Project unique identifier.", required = true)
    public String getProjectTicket() {
	return _projectTicket;
    }

    @ApiModelProperty(value = "Source language code.")
    public String getSourceLanguage() {
	return _sourceLanguage;
    }

    @ApiModelProperty(value = "List of target language codes.")
    public List<String> getTargetLanguages() {
	return _targetLanguages;
    }

    @ApiModelProperty(value = "Enables approximate term matching.", example = "false")
    public boolean isFuzzy() {
	return _fuzzy;
    }

    @ApiModelProperty(value = "Includes/Excludes blacklisted terms in search result.")
    public boolean isSearchForbidden() {
	return _searchForbidden;
    }

    public void setFuzzy(boolean fuzzy) {
	_fuzzy = fuzzy;
    }

    public void setMaxNumFound(int maxNumFound) {
	_maxNumFound = maxNumFound;
    }

    public void setProjectTicket(String projectTicket) {
	_projectTicket = projectTicket;
    }

    public void setSearchForbidden(boolean searchForbidden) {
	_searchForbidden = searchForbidden;
    }

    public void setSourceLanguage(String sourceLanguage) {
	_sourceLanguage = sourceLanguage;
    }

    public void setTargetLanguages(List<String> targetLanguages) {
	_targetLanguages = targetLanguages;
    }
}
