package org.gs4tr.termmanager.persistence.update;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gs4tr.termmanager.model.ImportErrorAction;
import org.gs4tr.termmanager.model.glossary.Description;

public class ImportOptionsModel {

    private Map<String, Set<String>> _allowedTermDescriptions = new HashMap<>(2);

    private Set<String> _allowedTermEntryAttributes = new HashSet<>();

    private Map<String, Map<String, String>> _attributeNoteReplacements = new HashMap<>(2);

    private DescriptionImportOption _descriptionImportOption;

    private boolean _ignoreCase = true;

    private ImportErrorAction _importErrorAction;

    private List<String> _importLocales;

    private Map<String, String> _languageReplacementByCode;

    private boolean _notEmptyGlossary = true;

    private boolean _overwriteByTermEntryId = true;

    private Long _projectId;

    private String _projectName;

    private String _projectShortCode;

    private String _status;

    private Map<String, String> _statusReplacement;

    private String _syncLanguageId;

    private Map<String, String> _termEntryAttributeReplacements = new HashMap<>();

    // Limit number of synonyms
    private int synonymNumber;

    public Map<String, Set<String>> getAllowedTermDescriptions() {
	return _allowedTermDescriptions;
    }

    public Set<String> getAllowedTermEntryAttributes() {
	return _allowedTermEntryAttributes;
    }

    public Map<String, Set<String>> getAllowedTermEntryAttributesMap() {
	Map<String, Set<String>> map = new HashMap<>();
	map.put(Description.ATTRIBUTE, getAllowedTermEntryAttributes());
	return map;
    }

    public Map<String, Map<String, String>> getAttributeNoteReplacements() {
	return _attributeNoteReplacements;
    }

    public DescriptionImportOption getDescriptionImportOption() {
	return _descriptionImportOption;
    }

    public ImportErrorAction getImportErrorAction() {
	return _importErrorAction;
    }

    public List<String> getImportLocales() {
	return _importLocales;
    }

    public Map<String, String> getLanguageReplacementByCode() {
	return _languageReplacementByCode;
    }

    public Long getProjectId() {
	return _projectId;
    }

    public String getProjectName() {
	return _projectName;
    }

    public String getProjectShortCode() {
	return _projectShortCode;
    }

    public String getStatus() {
	return _status;
    }

    public Map<String, String> getStatusReplacement() {
	return _statusReplacement;
    }

    public String getSyncLanguageId() {
	return _syncLanguageId;
    }

    public int getSynonymNumber() {
	return synonymNumber;
    }

    public Map<String, String> getTermEntryAttributeReplacements() {
	return _termEntryAttributeReplacements;
    }

    public boolean isIgnoreCase() {
	return _ignoreCase;
    }

    public boolean isNotEmptyGlossary() {
	return _notEmptyGlossary;
    }

    public boolean isOverwriteByTermEntryId() {
	return _overwriteByTermEntryId;
    }

    public void setAllowedTermDescriptions(Map<String, Set<String>> allowedTermDescriptions) {
	_allowedTermDescriptions = allowedTermDescriptions;
    }

    public void setAllowedTermEntryAttributes(Set<String> allowedTermEntryAttributes) {
	_allowedTermEntryAttributes = allowedTermEntryAttributes;
    }

    public void setAttributeNoteReplacements(Map<String, Map<String, String>> attributeNoteReplacements) {
	_attributeNoteReplacements = attributeNoteReplacements;
    }

    public void setDescriptionImportOption(DescriptionImportOption descriptionImportOption) {
	_descriptionImportOption = descriptionImportOption;
    }

    public void setIgnoreCase(boolean ignoreCase) {
	_ignoreCase = ignoreCase;
    }

    public void setImportErrorAction(ImportErrorAction importErrorAction) {
	_importErrorAction = importErrorAction;
    }

    public void setImportLocales(List<String> importLocales) {
	_importLocales = importLocales;
    }

    public void setLanguageReplacementByCode(Map<String, String> languageReplacementByCode) {
	_languageReplacementByCode = languageReplacementByCode;
    }

    public void setNotEmptyGlossary(boolean notEmptyGlossary) {
	_notEmptyGlossary = notEmptyGlossary;
    }

    public void setOverwriteByTermEntryId(boolean overwriteByTermEntryId) {
	_overwriteByTermEntryId = overwriteByTermEntryId;
    }

    public void setProjectId(Long projectId) {
	_projectId = projectId;
    }

    public void setProjectName(String projectName) {
	_projectName = projectName;
    }

    public void setProjectShortCode(String projectShortCode) {
	_projectShortCode = projectShortCode;
    }

    public void setStatus(String status) {
	_status = status;
    }

    public void setStatusReplacement(Map<String, String> statusReplacement) {
	_statusReplacement = statusReplacement;
    }

    public void setSyncLanguageId(String syncLanguageId) {
	_syncLanguageId = syncLanguageId;
    }

    public void setSynonymNumber(int synonymNumber) {
	this.synonymNumber = synonymNumber;
    }

    public void setTermEntryAttributeReplacements(Map<String, String> termEntryAttributeReplacements) {
	_termEntryAttributeReplacements = termEntryAttributeReplacements;
    }
}
