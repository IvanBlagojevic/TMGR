package org.gs4tr.termmanager.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "TermLocale")
public class TermV2ModelExtended extends TermV2Model {

    private String _locale;

    @ApiModelProperty(value = "Term language locale.", required = true)
    public String getLocale() {
	return _locale;
    }

    public void setLocale(String locale) {
	_locale = locale;
    }

    @Override
    public String toString() {
	return "TermV2ModelExtended [_locale=" + _locale + ", descriptions=" + getDescriptions() + ", termText="
		+ getTermText() + ", forbidden=" + isForbidden() + "]";
    }
}
