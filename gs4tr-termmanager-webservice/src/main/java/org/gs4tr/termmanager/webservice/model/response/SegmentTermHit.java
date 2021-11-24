package org.gs4tr.termmanager.webservice.model.response;

import java.util.Collection;
import java.util.List;

import org.gs4tr.foundation3.xliff.differences.Range;
import org.gs4tr.termmanager.model.dto.Description;
import org.gs4tr.termmanager.model.dto.LanguageModel;

import io.swagger.annotations.ApiModelProperty;

public class SegmentTermHit {

    private Collection<Description> _descriptions;
    private String _locale;
    private List<SegmentHitRange> _ranges;
    private List<LanguageModel> _targets;
    private String _termHit;

    @ApiModelProperty(value = "Collection of term level description, it could be \"ATTRIBUTE\" or \"NOTE\".")
    public Collection<Description> getDescriptions() {
	return _descriptions;
    }

    @ApiModelProperty(value = "Language locale code.")
    public String getLocale() {
	return _locale;
    }

    @ApiModelProperty(value = "List of ranges for segment highlighting.")
    public List<SegmentHitRange> getRanges() {
	return _ranges;
    }

    @ApiModelProperty(value = "Collection of translations grouped by language.")
    public List<LanguageModel> getTargets() {
	return _targets;
    }

    @ApiModelProperty(value = "Matched term text. Match can be approximate or precise.")
    public String getTermHit() {
	return _termHit;
    }

    public void setDescriptions(Collection<Description> descriptions) {
	_descriptions = descriptions;
    }

    public void setLocale(String locale) {
	_locale = locale;
    }

    public void setRanges(List<SegmentHitRange> ranges) {
	_ranges = ranges;
    }

    public void setTargets(List<LanguageModel> targets) {
	_targets = targets;
    }

    public void setTermHit(String termHit) {
	_termHit = termHit;
    }

}
