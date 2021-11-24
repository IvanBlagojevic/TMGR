package org.gs4tr.termmanager.webservice.model.request;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gs4tr.termmanager.model.ImportTypeEnum;
import org.gs4tr.termmanager.model.dto.ImportAttributeReplacement;
import org.gs4tr.termmanager.persistence.update.DescriptionImportOption;
import org.gs4tr.termmanager.service.termentry.synchronization.SyncOption;

import io.swagger.annotations.ApiModelProperty;

public class ImportCommand extends BaseCommand {

    private Set<String> _allowedTermAttributes;

    private Set<String> _allowedTermEntryAttributes;

    private Set<String> _allowedTermNotes;

    private List<ImportAttributeReplacement> _attributeReplacements;

    private DescriptionImportOption _descriptionImportOption = DescriptionImportOption.ADD_ALL;

    private List<String> _importLocales;

    private String _importType = ImportTypeEnum.TBX.name();

    private String _projectTicket;

    private Map<String, String> _statusReplacement;

    private String _syncLang;

    private SyncOption _syncOption = SyncOption.MERGE;

    @ApiModelProperty(value = "Allowed term attribute types.")
    public Set<String> getAllowedTermAttributes() {
	return _allowedTermAttributes;
    }

    @ApiModelProperty(value = "Allowed term entry attribute types.")
    public Set<String> getAllowedTermEntryAttributes() {
	return _allowedTermEntryAttributes;
    }

    @ApiModelProperty(value = "Allowed term entry note types.")
    public Set<String> getAllowedTermNotes() {
	return _allowedTermNotes;
    }

    @ApiModelProperty(value = "Incoming description types and their replacements.")
    public List<ImportAttributeReplacement> getAttributeReplacements() {
	return _attributeReplacements;
    }

    @ApiModelProperty(value = "Description import option.", allowableValues = "ADD ALL, IMPORT ONLY SELECTED, IGNORE ALL")
    public DescriptionImportOption getDescriptionImportOption() {
	return _descriptionImportOption;
    }

    @ApiModelProperty(value = "Import language codes.", required = true)
    public List<String> getImportLocales() {
	return _importLocales;
    }

    @ApiModelProperty(value = "Type of file for import.", allowableValues = "TBX")
    public String getImportType() {
	return _importType;
    }

    @ApiModelProperty(value = "Project unique identifier.", required = true)
    public String getProjectTicket() {
	return _projectTicket;
    }

    @ApiModelProperty(value = "External term statuses and their matches in TMGR. It is used for integration with other systems.")
    public Map<String, String> getStatusReplacement() {
	return _statusReplacement;
    }

    @ApiModelProperty(value = "Synchronization language code.", required = true)
    public String getSyncLang() {
	return _syncLang;
    }

    @ApiModelProperty(value = "Term entry synchronization option.", allowableValues = "MERGE, OVERWRITE, APPEND")
    public SyncOption getSyncOption() {
	return _syncOption;
    }

    public void setAllowedTermAttributes(Set<String> allowedTermAttributes) {
	_allowedTermAttributes = allowedTermAttributes;
    }

    public void setAllowedTermEntryAttributes(Set<String> allowedTermEntryAttributes) {
	_allowedTermEntryAttributes = allowedTermEntryAttributes;
    }

    public void setAllowedTermNotes(Set<String> allowedTermNotes) {
	_allowedTermNotes = allowedTermNotes;
    }

    public void setAttributeReplacements(List<ImportAttributeReplacement> attributeReplacements) {
	_attributeReplacements = attributeReplacements;
    }

    public void setDescriptionImportOption(DescriptionImportOption descriptionImportOption) {
	_descriptionImportOption = descriptionImportOption;
    }

    public void setImportLocales(List<String> importLocales) {
	_importLocales = importLocales;
    }

    public void setImportType(String importType) {
	_importType = importType;
    }

    public void setProjectTicket(String projectTicket) {
	_projectTicket = projectTicket;
    }

    public void setStatusReplacement(Map<String, String> statusReplacement) {
	_statusReplacement = statusReplacement;
    }

    public void setSyncLang(String syncLang) {
	_syncLang = syncLang;
    }

    public void setSyncOption(SyncOption syncOption) {
	_syncOption = syncOption;
    }

    @Override
    public String toString() {
	return "ImportCommand [_importType=" + _importType + ", _projectTicket=" + _projectTicket + ", _syncLang="
		+ _syncLang + "]";
    }
}
