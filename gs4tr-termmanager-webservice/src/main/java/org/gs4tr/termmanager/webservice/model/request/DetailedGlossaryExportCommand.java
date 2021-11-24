package org.gs4tr.termmanager.webservice.model.request;

import java.util.List;

import org.gs4tr.termmanager.service.export.ExportFormatEnum;

import io.swagger.annotations.ApiModelProperty;

public class DetailedGlossaryExportCommand extends BaseCommand {

    private ExportFormatEnum _fileType;
    private boolean _forbidden;
    private String _projectTicket;
    private String _sourceLocale;
    private List<String> _targetLocales;

    @ApiModelProperty(value = "Type of file to export. JSON or TBX.", allowableValues = "JSON,TBX")
    public ExportFormatEnum getFileType() {
	return _fileType;
    }

    @ApiModelProperty(value = "Project unique identifier.", required = true)
    public String getProjectTicket() {
	return _projectTicket;
    }

    @ApiModelProperty(value = "Source term language locale. Response must contains terms with this language.", required = true)
    public String getSourceLocale() {
	return _sourceLocale;
    }

    @ApiModelProperty(value = "Target term language locales. Response should contains terms with this languages.")
    public List<String> getTargetLocales() {
	return _targetLocales;
    }

    @ApiModelProperty(value = "Boolean value, if its true, it will return term entry with at least one blacklisted term in it.")
    public boolean isForbidden() {
	return _forbidden;
    }

    public void setFileType(ExportFormatEnum fileType) {
	_fileType = fileType;
    }

    public void setForbidden(boolean forbidden) {
	_forbidden = forbidden;
    }

    public void setProjectTicket(String projectTicket) {
	_projectTicket = projectTicket;
    }

    public void setSourceLocale(String sourceLocale) {
	_sourceLocale = sourceLocale;
    }

    public void setTargetLocales(List<String> targetLocales) {
	_targetLocales = targetLocales;
    }
}
